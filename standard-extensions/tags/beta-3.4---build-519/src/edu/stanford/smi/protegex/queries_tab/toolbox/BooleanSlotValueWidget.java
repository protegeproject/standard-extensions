package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.queries_tab.*;

public class BooleanSlotValueWidget extends AbstractSlotValueWidget{
  JComboBox itsCombo;
  JComponent itsComp;
  String[] values = {"true", "false"};

    /** Constructor. */
    public BooleanSlotValueWidget(SearchWidget widget) {
        super(widget);
        constraints = ConstraintsModel.getBooleanConstraints();
        this.label = "boolean";
        createComponents(null);
    }

    /** Constructor with label, slot, and actions. */
    public BooleanSlotValueWidget(SearchWidget widget, String label, String slot) {
        super(widget);
        setSlotName(slot);
        this.label = label;
    }

    /** Create a Combobox. */
    private JComboBox createComboBox() {

        JComboBox combo = new JComboBox(values);
        combo.setPreferredSize(new Dimension(120, 25));
        return combo;
    }

    /** Create components in this relation display. */
    private void createComponents(String label) {
        itsCombo = createComboBox();
        JScrollPane scroll = new JScrollPane(itsCombo);
        scroll.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        LabeledComponent c = new LabeledComponent("Boolean", itsCombo);
        itsComp = c;

    }

    /** Return the relation display component. */
    public JComponent getComponent() {
        return itsComp;
    }

    /** Get the data from the text area and return it as a string array. */
    public Object[] getData() {
        String[] itsData = new String[1];
        itsData[0] = getSelectedItem();
        return itsData;
    }

    /** Get the selected string in text area and return them as a string array. */
    public String getSelectedItem() {
        return (String) itsCombo.getSelectedItem();
    }

    /** Get the selected object which is a string for symbol slot. */
    public Object getSelectedObject() {
        return getSelectedItem();
    }

    private int getTestIndex(String constraint) {
        return 0;
    }

    /** Return the embedded widget in the relation display, the JTextArea. */
    public JComponent getWidget() {
        return itsComp;
    }

    /** Set the slot not a single value slot. */
    public boolean isSlotSingleValued() {
        return true;
    }

    public Collection search() {
        if (specification.getType() == null)
            return null;
        String slotType = specification.getType();
        if (!slotType.toLowerCase().equals("boolean"))
            return null;

        // Here we first test the function based on the simplest Boolean cases
        ArrayList resultInstances = new ArrayList();
        Collection instances = itsInstances;
        Iterator i = instances.iterator();

        Slot slot = itsWidget.getKB().getSlot(specification.getName());

        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (testBoolean(getTestIndex(specification.getConstraint()), instance, slot))
                resultInstances.add(instance);
        }
        return resultInstances;
    }

    // This is used for Query test
    public Collection search(Collection instances, Slot slot, String operation, Object obj) {
        ArrayList resultInstances = new ArrayList();
        Iterator i = instances.iterator();
        String value = (String) obj;
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (testBoolean(getTestIndex(operation), instance, slot, value))
                resultInstances.add(instance);
        }
        return resultInstances;
    }

    /** Set the specified string to the text area. */
    public void setData(Object[] data) {

        itsWidget.setReady(true);
        itsCombo.setSelectedItem(data[0]);
        return;

    }

    public void setSelectedObject(Object obj) {
        itsCombo.setSelectedItem(obj);
    }

    private boolean testBoolean(int testIndex, Instance instance, Slot slot) {
        String value;
        if (specification.getValue() == null)
            return false;
        if (((String) specification.getValue()).length() < 1)
            return false;
        value = (String) specification.getValue();
        return testBoolean(testIndex, instance, slot, value);
    }

    /** Main test subroutine for Integer slot. */
    //private boolean testInteger(int testIndex, Instance instance, Slot slot, int value) {

    /** Main test subroutine for Boolean slot. */
    private boolean testBoolean(int testIndex, Instance instance, Slot slot, String value) {
        boolean testResult = false;
        if (instance == null)
            return testResult;
        String instanceVal;

        switch (testIndex) {
            case 0 : // is equal to
                if (instance.getOwnSlotValue(slot) == null)
                    return false;
                instanceVal = instance.getOwnSlotValue(slot).toString();
                if (instanceVal.equalsIgnoreCase(value))
                    testResult = true;
                break;

            default :
                break;
        }
        return testResult;
    }
}
