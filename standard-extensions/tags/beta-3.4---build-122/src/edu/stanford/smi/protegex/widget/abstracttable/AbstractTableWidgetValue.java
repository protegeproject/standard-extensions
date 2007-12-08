package edu.stanford.smi.protegex.widget.abstracttable;

import java.awt.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Value of TableModels used with AbstractTable
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class AbstractTableWidgetValue {
    public Color color;
    // Actual value(s) being displayed and edited.
    // This is initialized with the slot value at the instance
    public Collection values;
    public Instance instance;
    public Slot slot;
}
