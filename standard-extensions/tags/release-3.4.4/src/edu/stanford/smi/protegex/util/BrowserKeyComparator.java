package edu.stanford.smi.protegex.util;

import edu.stanford.smi.protege.model.*;
import java.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class BrowserKeyComparator implements Comparator {

    public BrowserKeyComparator() {
    }

    public int compare(Object o1, Object o2) {
        Frame f1 = (Frame) o1;
        Frame f2 = (Frame) o2;
        return (f1.getBrowserText()).compareTo(f2.getBrowserText());
    }
}
