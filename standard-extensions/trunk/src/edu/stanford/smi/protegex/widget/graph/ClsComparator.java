package edu.stanford.smi.protegex.widget.graph;

import java.util.Comparator;
import edu.stanford.smi.protege.model.Cls;

public class ClsComparator implements Comparator {

    public ClsComparator() {
    }

    public int compare(Object o1, Object o2) {
        Cls cls1 = (Cls) o1;
        Cls cls2 = (Cls) o2;

        String name1 = cls1.getName();
        String name2 = cls2.getName();

        return name1.compareTo(name2);
    }

    public boolean equals(Object obj) {
        /**@todo Implement this java.util.Comparator method*/
        throw new UnsupportedOperationException("Method equals() not yet implemented.");
    }
}