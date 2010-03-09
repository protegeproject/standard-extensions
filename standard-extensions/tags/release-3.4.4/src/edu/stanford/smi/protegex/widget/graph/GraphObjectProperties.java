package edu.stanford.smi.protegex.widget.graph;

import edu.stanford.smi.protege.util.PropertyList;

public abstract class GraphObjectProperties extends GraphProperties {

    public GraphObjectProperties(String clsName, PropertyList propertyList) {
        super(clsName, propertyList);
    }

    public abstract String getLineType();
    public abstract String getArrowheadType();
}