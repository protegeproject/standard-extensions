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

public class ClassSelectWidget extends AbstractListValueWidget {

    /** Constructor. */
    public ClassSelectWidget(SearchWidget widget) {
        super(widget);
        constraints = ConstraintsModel.getClsConstraints();
        this.label = "cls";
        createComponents(null);
        setActionsEnabled(false);
    }

    /** Constructor with label, slot, and actions. */
    public ClassSelectWidget(SearchWidget widget, String label, String slot) {
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

    // This is used to clear the display only.
    public void clearList() {
        ArrayList displayInstances = new ArrayList(CollectionUtilities.createCollection(null));
        Collections.sort(displayInstances, theComparator);
        ComponentUtilities.setListValues(itsList, displayInstances);
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

    public Cls getCls() {
        return (Cls) itsInstance;
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
            private static final long serialVersionUID = 2927328134386784008L;

            public void actionPerformed(ActionEvent event) {
                removeInstance();
            }
        };
    }

    /** Get Select(+) Instance Action. */
    private Action getSelectClsAction() {
        return new AbstractAction("Select Cls", Icons.getAddIcon()) {
            private static final long serialVersionUID = -4144795581426016980L;

            public void actionPerformed(ActionEvent event) {
                Instance instance;
                instanceSlot = getSlot();
                if (instanceSlot != null) {
                    Collection clses;
                    clses = instanceSlot.getDirectDomain();
                    itsWidget.setSelectSlotName();

                    if (clses.size() > 0) {
                        instance = DisplayUtilities.pickCls(itsComp, getKB(), clses);
                    } else
                        instance = null;
                } else {
                    Collection clses = DisplayUtilities.pickClses(itsComp, getKB());
                    itsWidget.setSelectSlotName();
                    if (clses == null) {
                        instance = null;
                    } else {
                        instance = null;
                        Iterator j = clses.iterator();
                        while (j.hasNext()) {
                            instance = (Instance) j.next();
                            break;
                        }
                    }
                }

                if (instance != null) {
                    setDisplayedInstance(instance);
                    setActionsEnabled(true);
                }
                itsTab.enableSearch();
            }
        };
    }

    /** Get the selected string in text area and return them as a string array. */
    public String[] getSelectedItems() {
        String[] text = new String[1];
        text[0] = itsInstance.getBrowserText();
        return text;
    }

    /** Get the selected object which is a class for class slot. */
    public Object getSelectedObject() {
        return getCls();
    }

    /** Get slot return the current slot. */
    private Slot getSlot() {
        slotName = itsWidget.getSearchSubject();
        if (slotName == null)
            return null;
        Slot slot = itsWidget.getKB().getSlot(slotName);
        return slot;
    }

    /** View the instance. */
    private Action getViewClsAction() {
        return new AbstractAction("View Cls", Icons.getViewClsIcon()) {
            private static final long serialVersionUID = 3929681094374282192L;

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
        valueChanged();
    }

    private void removeInstance() {
        itsWidget.setSelectSlotName();
        removeDisplayedInstance();
        setActionsEnabled(false);
    }

    private void replaceInstance(Instance instance) {
        itsInstance = instance;
    }

    public Collection search(Collection instances, Slot slot, String operation, Object obj) {
        return null;
    }

    public void setActionsEnabled(boolean b) {
        if (isViewEnabled)
            itsViewAction.setEnabled(b);
        else
            itsViewAction.setEnabled(false);
        itsRemoveAction.setEnabled(b);

    }

    /** Overwrite the setCls to setup the selected cls */
    public void setCls(Cls cls) {
        selection = cls;
        itsInstance = (Instance) selection;
        // Note: to remove this line might be wrong
        setDisplayedInstance(itsInstance);

    }

    /** Set the specified string to the text area. */
    public void setData(Object[] data) {
        itsList.clearSelection();
        itsWidget.setReady(true);

        setActionsEnabled(false);

        itsInstance = null;
        if (data == null || (String) data[0] == null) {
            return;
        }

        slotName = (String) data[0];
        instanceSlot = getSlot();
        if (instanceSlot == null)
            return;

    }

    /** Set the input instance as the selected classs */
    private void setDisplayedInstance(Instance instance) {
        replaceInstance(instance);
        updateList();
        valueChanged();
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

    private void valueChanged() {
        String className;
        if (itsInstance == null) {
            className = null;
        } else
            className = itsInstance.getBrowserText();

        if (className == null || className.length() < 1) {
            itsInstance = null;
            setActionsEnabled(false);
            itsWidget.setClass2(itsInstance);
            return;
        }

        if (className != null && itsInstance != null) {
            setActionsEnabled(true);
            itsWidget.setClass2(itsInstance);
        }

    }

    public void viewObject() {
        if (itsInstance == null)
            return;
        showInstance(itsInstance);
    }
}
