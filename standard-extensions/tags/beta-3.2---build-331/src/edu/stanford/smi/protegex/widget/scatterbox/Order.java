package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;
/**
 *  Description of the Interface
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public interface Order {

    public void fillObjectWithIndexedValue(Instance instance, int index);

    public int getIndexForEntry(Instance entry);

    public int getIndexForValue(Object value);

    public String getName();

    public int getSize();

    public Slot getSlot();

    public Object getValueForEntry(Instance entry);

    public Object getValueForIndex(int index);

    public void setName(String name);

    public void setSlot(Slot slot);
}
