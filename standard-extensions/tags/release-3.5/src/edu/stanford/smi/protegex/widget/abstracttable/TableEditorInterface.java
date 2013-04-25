package edu.stanford.smi.protegex.widget.abstracttable;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Interface
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public interface TableEditorInterface {
    public final static String NULL_STRING = "<no value>";

    public Object getValue();

    public boolean isValueAcceptable();

    public boolean needsToStoreChanges();

    public void setInstance(Instance instance);

    public void setSlot(Slot slot);

    public void storeValueInKB();
}
