package edu.stanford.smi.protegex.queries_tab;

import java.util.*;

import javax.swing.*;

public class SlotsModel extends DefaultComboBoxModel{
   private static final long serialVersionUID = 2585523361788138059L;
private Collection slots;
   private String[] slotNames;
   private String[] slotTypes;
   private String type;

    public SlotsModel(String[] names, String[] types, Collection slotCol) {
        super();
        initialize(names, types, slotCol);
    }

    private void addAny() {
        if (slotNames == null)
            return;
        for (int i = 0; i < slotNames.length; i++) {
            addElement(slotNames[i]);
        }
    }

    private void addType(String type) {
        if (type == null || slotNames == null)
            addAny();
        for (int i = 0; i < slotNames.length; i++) {
            if (slotTypes[i].equals(type))
                addElement(slotNames[i]);
        }
    }

    public String[] getSlotNames() {
        return slotNames;
    }

    public Collection getSlots() {
        return slots;
    }

    public String[] getSlotTypes() {
        return slotTypes;
    }

    public String getType() {
        return type;
    }

    public void initialize(String[] names, String[] types, Collection slotCol) {
        slotNames = names;
        slotTypes = types;
        this.slots = slotCol;
    }

    public void removeName(String name) {

    }

    public void setUpComboBox(String type) {
        if (getSize() > 0)
            removeAllElements();
        if (type == null || type.equals("ANY"))
            addAny();
        else
            addType(type);
    }
}
