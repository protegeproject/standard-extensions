package edu.stanford.smi.protegex.widget.graph;

import java.util.Comparator;

public class GraphObjectComparator implements Comparator {

    public GraphObjectComparator() {
    }

    public int compare(Object o1, Object o2) {
        GraphObjectProperties prop1 = (GraphObjectProperties) o1;
        GraphObjectProperties prop2 = (GraphObjectProperties) o2;

        String s1 = prop1.getClsName();
        String s2 = prop2.getClsName();

        return s1.compareTo(s2);
    }

    public boolean equals(Object obj) {
        /**@todo Implement this java.util.Comparator method*/
        throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
    }
}