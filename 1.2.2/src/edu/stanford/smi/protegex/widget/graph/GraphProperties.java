package edu.stanford.smi.protegex.widget.graph;

import edu.stanford.smi.protege.util.PropertyList;

public abstract class GraphProperties {
    private String clsName;
    private String prefix;
    private PropertyList propertyList;

    public GraphProperties(String clsName, PropertyList propertyList) {
        this.clsName = clsName;
        this.prefix = clsName + "_";
        this.propertyList = propertyList;
    }

    public abstract void save();

    public String getStringProperty(String property, String defaultValue) {
        String s = propertyList.getString(property);
        if (s == null) {
            s = defaultValue;
        }
        return s;
    }

    public boolean getBooleanProperty(String property, Boolean defaultValue) {
        Boolean b = propertyList.getBoolean(property);
        if (b == null) {
            b = defaultValue;
        }
        return b.booleanValue();
    }

    public int getIntProperty(String property, Integer defaultValue) {
        Integer i = propertyList.getInteger(property);
        if (i == null) {
            i = defaultValue;
        }
        return i.intValue();
    }

    public String getClsName() {
        return clsName;
    }

    public String getPrefix() {
        return prefix;
    }

    public PropertyList getPropertyList() {
        return propertyList;
    }
}