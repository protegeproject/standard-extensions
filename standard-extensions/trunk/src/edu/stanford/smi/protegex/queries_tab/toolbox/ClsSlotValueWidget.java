package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.queries_tab.*;

public class ClsSlotValueWidget extends AbstractListValueWidget {

    /** Constructor. */
    public ClsSlotValueWidget(SearchWidget widget) {
        super(widget);
        constraints = ConstraintsModel.getClsConstraints();
        this.label = "cls";
        createComponents(null);
    }

    /** Constructor with label, slot, and actions. */
    public ClsSlotValueWidget(SearchWidget widget, String label, String slot) {
        super(widget);
        setSlotName(slot);
        this.label = label;
    }

    protected void addActions() {

        LabeledComponent c = new LabeledComponent("Class", itsList);

        itsSelectAction = getSelectClsAction();
        itsViewAction = getViewClsAction();
        itsRemoveAction = getRemoveClsAction();

        c.addHeaderButton(itsViewAction);
        c.addHeaderButton(itsSelectAction);
        c.addHeaderButton(itsRemoveAction);

        itsComp = c;
    }

    private boolean containInstance(Instance sel, Slot slot, Instance instance) {
        if (itsInstance == null)
            return false;
        Collection instances = sel.getOwnSlotValues(slot);
        Iterator i = instances.iterator();
        while (i.hasNext()) {

            Instance tmpInstance = (Instance) i.next();
            if (tmpInstance.equals(instance))
                return true;
        }
        return false;

    }

    /** Create components in this relation display. */
    protected void createListener() {
        itsKBListener = new KnowledgeBaseAdapter() {
            public void clsDeleted(KnowledgeBaseEvent event) {
                if (itsInstance == null)
                    return;
                edu.stanford.smi.protege.model.Frame frame = event.getFrame();

                if (frame instanceof Instance) {
                    String name = ((Instance) frame).getBrowserText();
                    if (frame.equals(itsInstance)) {
                        removeInstance();
                        setDisplayName(name);
                    }
                }
            }
        };
        itsWidget.getKB().addKnowledgeBaseListener(itsKBListener);
    }

    /** Return the relation display component. */
    public JComponent getComponent() {
        return itsComp;
    }

    /** Get the data from the text area and return it as a string array. */
    public Object[] getData() {
        String[] itsData = new String[1];
        if (itsInstance == null)
            return null;
        itsData[0] = itsInstance.getBrowserText();

        return itsData;
    }

    private Action getRemoveClsAction() {
        return new AbstractAction("Remove Cls", Icons.getRemoveIcon()) {
            private static final long serialVersionUID = 1952003663211228765L;

            public void actionPerformed(ActionEvent event) {
                removeInstance();
            }
        };
    }

    /** Get Select(+) Instance Action. */
    private Action getSelectClsAction() {
        return new AbstractAction("Select Cls", Icons.getAddIcon()) {
            private static final long serialVersionUID = -2278120649245681367L;

            public void actionPerformed(ActionEvent event) {

                if (instanceSlot == null)
                    instanceSlot = getSlot();
                if (instanceSlot == null)
                    return;
                Collection clses = instanceSlot.getAllowedParents();
                Instance instance = DisplayUtilities.pickCls(itsComp, getKB(), clses);

                if (instance != null) {
                    setDisplayedInstance(instance);
                    itsInstance = instance;
                }
                setActionsEnabled(true);
            }
        };
    }

    /** Get the selected string in text area and return them as a string array. */
    public String[] getSelectedItems() {
        String[] text = new String[1];
        text[0] = itsInstance.getBrowserText();
        return text;
    }

    /** Get the selected object which is a cls for class slot. */
    public Object getSelectedObject() {
        return itsInstance;
    }

    /** Get slot return the current slot. */
    public Slot getSlot() {

        if (slotName == null)
            return null;
        Slot slot = itsWidget.getKB().getSlot(slotName);
        return slot;
    }

    private int getTestIndex(String constraint) {
        for (int i = 0; i < constraints.length; i++)
            if (constraint.toLowerCase().equals(constraints[i]))
                return i;
        return -1;
    }

    /** View the instance. */
    private Action getViewClsAction() {
        return new AbstractAction("View Cls", Icons.getViewIcon()) {
            private static final long serialVersionUID = 4726833494816199402L;

            public void actionPerformed(ActionEvent event) {
                viewObject();
            }
        };
    }

    /** Return the embedded widget in the relation display, the JTextArea. */
    public JComponent getWidget() {
        return itsComp;
    }

    /** Set the slot not a single value slot. */
    public boolean isSlotSingleValued() {
        return true;
    }

    private void removeDisplayedInstance() {
        replaceInstance(null);
        updateList();
    }

    private void removeInstance() {
        removeDisplayedInstance();
        setActionsEnabled(false);
    }

    private void replaceInstance(Instance instance) {
        itsInstance = instance;
    }

    public Collection search() {
        if (specification.getType() == null)
            return null;
        String slotType = specification.getType();
        if (!slotType.toLowerCase().equals("cls"))
            return null;

        // Here we first test the function based on the simplest Instance cases
        ArrayList resultInstances = new ArrayList();
        Collection instances = itsInstances;
        Iterator i = instances.iterator();
        Slot slot = itsWidget.getKB().getSlot(specification.getName());
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (testInstance(getTestIndex(specification.getConstraint()), instance, slot))
                resultInstances.add(instance);
        }
        return resultInstances;
    }

    // This is used for Query test
    public Collection search(Collection instances, Slot slot, String operation, Object obj) {
        ArrayList resultInstances = new ArrayList();
        Iterator i = instances.iterator();
        String value = (String) (((Instance) obj).getBrowserText());
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (testInstance(getTestIndex(operation), instance, slot, value))
                resultInstances.add(instance);
        }
        return resultInstances;
    }

    public void setActionsEnabled(boolean b) {
        if (isViewEnabled)
            itsViewAction.setEnabled(b);
        else
            itsViewAction.setEnabled(false);
        itsRemoveAction.setEnabled(b);

    }

    /** Set the specified string to the text area. */
    public void setData(Object[] data) {
        removeDisplayedInstance();
        setActionsEnabled(false);
        itsWidget.setReady(true);

        setActionsEnabled(false);

        itsInstance = null;
        if (data == null || (String) data[0] == null) {
            return;
        }
        if (selection == null)
            return;
        slotName = (String) data[0];
        instanceSlot = getSlot();
        if (instanceSlot == null)
            return;
    }

    private void setDisplayedInstance(Instance instance) {
        replaceInstance(instance);
        updateList();
    }

    public void setSelectedObject(Object obj) {
        setDisplayedInstance((Instance) obj);
    }

    public void setViewEnabled(boolean b) {
        isViewEnabled = b;
        itsViewAction.setEnabled(isViewEnabled);
    }

    public void showInstance(Instance instance) {
        if (instance != null)
            itsWidget.getKB().getProject().show(instance);
    }

    private boolean testInstance(int testIndex, Instance instance, Slot slot) {
        String value;
        if (specification.getValue() == null)
            return false;
        if (((String) specification.getValue()).length() < 1)
            return false;
        value = instance.getBrowserText();
        return testInstance(testIndex, instance, slot, value);
    }

    /** Main test subroutine for Integer slot. */
    //private boolean testInteger(int testIndex, Instance instance, Slot slot,
    // int value) {
    /** Main test subroutine for Instance slot. */
    private boolean testInstance(int testIndex, Instance instance, Slot slot, String value) {

        boolean testResult = false;
        if (instance == null)
            return testResult;
        if (slot == null) {
            return false;
        }

        Instance tmpInstance = (Instance) instance.getOwnSlotValue(slot);
        if (tmpInstance == null && testIndex == 0)
            return false;
        if (tmpInstance == null && testIndex == 1)
            return true;

        switch (testIndex) {
        case 0: // is equal to
            return containInstance(instance, slot, itsInstance);
        case 1: // is not equal to
            return (!containInstance(instance, slot, itsInstance));
        default:
            break;
        }
        return testResult;
    }

    public void viewObject() {
        if (instanceSlot == null || itsInstance == null)
            return;
        showInstance(itsInstance);
    }
}