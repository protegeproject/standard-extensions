package edu.stanford.smi.protegex.util;

import java.util.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Abstract superclass for lots of state objects Wrapper around PropertyList
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class AbstractWidgetState extends Observable {
    // button-related state
    protected PropertyList _properties;
    // protected Cls _cls;
    // protected KnowledgeBase _kb;

    public AbstractWidgetState(PropertyList properties) {
        _properties = properties;
        restore();
    }

    protected void broadcast() {
        setChanged();
        notifyObservers();
    }

    public abstract void dispose();

    protected boolean isCustomized() {
        // pretty dumb right now-- empty implies never been customized.
        Collection names = _properties.getNames();
        if ((null == names) || (0 == names.size())) {
            return false;
        }
        return true;
    }

    protected boolean readBoolean(String name, boolean defaultValue) {
        Boolean objectValue = _properties.getBoolean(name);
        if (null != objectValue) {
            return objectValue.booleanValue();
        }
        return defaultValue;
    }

    protected int readInt(String name, int defaultValue) {
        Integer objectValue = _properties.getInteger(name);
        if (null != objectValue) {
            return objectValue.intValue();
        }
        return defaultValue;
    }

    protected String readString(String name, String defaultValue) {
        String objectValue = _properties.getString(name);
        if (null != objectValue) {
            return objectValue;
        }
        return defaultValue;
    }

    public abstract void restore();

    public abstract void save();
}
