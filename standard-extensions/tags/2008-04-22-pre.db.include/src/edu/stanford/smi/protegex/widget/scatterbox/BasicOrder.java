package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;
import java.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class BasicOrder implements Order, Constants {
    protected ArrayList _elements;
    protected int _numberOfElements;
    protected Slot _slot;
    protected ScatterboxWidget _widget;
    protected String _name;
    protected KBQueryUtils _queryUtilsObject;

    public BasicOrder(ScatterboxWidget widget, Slot slot, KBQueryUtils queryUtilsObject) {
        _queryUtilsObject = queryUtilsObject;
        _slot = slot;
        _widget = widget;
        _elements = new ArrayList(getValues());
        _numberOfElements = _elements.size();
        _name = _slot.getName();
    }

    protected void appendList(List mainList, List newValues) {
        Iterator i = newValues.iterator();
        while (i.hasNext()) {
            Object nextObject = i.next();
            if (!mainList.contains(nextObject)) {
                mainList.add(nextObject);
            }
        }
        return;
    }

    protected ArrayList concatenateWithoutDuplicates(Collection collectionOfListsOfClasses) {
        ArrayList returnValue = new ArrayList();
        Iterator i = collectionOfListsOfClasses.iterator();
        while (i.hasNext()) {
            appendList(returnValue, (List) i.next());
        }
        return returnValue;
    }

    public void fillObjectWithIndexedValue(Instance instance, int index) {
        Object value = getValueForIndex(index);
        instance.setOwnSlotValue(_slot, value);
        return;
    }

    public int getIndexForEntry(Instance entry) {
        return getIndexForValue(getValueForEntry(entry));
    }

    public int getIndexForValue(Object value) {
        int counter;
        for (counter = 0; counter < _numberOfElements; counter++) {
            Object nextObject = _elements.get(counter);
            if (value.equals(nextObject)) {
                return counter;
            }
        }
        return -1;
    }

    public String getName() {
        return _name;
    }

    public int getSize() {
        return _numberOfElements;
    }

    public Slot getSlot() {
        return _slot;
    }

    public Object getValueForEntry(Instance entry) {
        Instance domainObject = _queryUtilsObject.getDomainObject(entry);
        return domainObject.getOwnSlotValue(_slot);
    }

    public Object getValueForIndex(int index) {
        return _elements.get(index);
    }

    protected abstract Collection getValues();

    public void setName(String name) {
        _name = name;
    }

    public void setSlot(Slot slot) {
        _slot = slot;
        return;
    }
}
