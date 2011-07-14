package edu.stanford.smi.protegex.queries_tab.toolbox;

import javax.swing.*;

public class SymbolSlotModel extends DefaultComboBoxModel{

   private static final long serialVersionUID = 8952292919566362546L;
private String[] slotValues;

    public SymbolSlotModel() {
        super();
    }

    public SymbolSlotModel(String[] names) {
        super();
        initialize(names);
    }

    private void addValues() {
        if (slotValues == null)
            return;
        for (int i = 0; i < slotValues.length; i++) {
            addElement(slotValues[i]);
        }
    }

    public String[] getValues() {
        return slotValues;
    }

    public void initialize(String[] names) {
        slotValues = names;
        setUpComboBox();
    }

    public void setUpComboBox() {
        if (getSize() > 0)
            removeAllElements();
        addValues();
    }
}
