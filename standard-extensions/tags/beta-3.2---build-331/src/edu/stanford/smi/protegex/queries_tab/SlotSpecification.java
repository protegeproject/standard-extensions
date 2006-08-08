package edu.stanford.smi.protegex.queries_tab;

public class SlotSpecification {
   private String slotType;
   private String slotName;
   private String constraint;
   private Object value;

    public SlotSpecification() {
        slotType = null;
        slotName = null;
        constraint = null;
        value = null;
    }

    public SlotSpecification(String name, String type, String cons, String v) {

        slotType = type;
        slotName = name;
        constraint = cons;
        value = v;
    }

    public String getConstraint() {
        return constraint;
    }

    public String getName() {
        return slotName;
    }

    public String getType() {
        return slotType;
    }

    public Object getValue() {
        return value;
    }

    public void setConstraint(String cons) {
        constraint = cons;
    }

    public void setName(String name) {
        slotName = name;
    }

    public void setType(String type) {
        slotType = type;
    }

    public void setValue(Object v) {
        value = v;
    }
}
