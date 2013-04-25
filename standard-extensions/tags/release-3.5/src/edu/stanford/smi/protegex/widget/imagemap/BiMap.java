package edu.stanford.smi.protegex.widget.imagemap;

import java.util.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Two directional map. Two parts to this: An over-ride of several HashMap
 *  methods (to implement the BiMap indexing) and a new method, getKeysForValue,
 *  to allow access to the BiMap functionality.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class BiMap extends HashMap {
    private static final long serialVersionUID = -6723081838808663581L;
    private HashMap _backwardsMap;

    public BiMap() {
        _backwardsMap = new HashMap();
    }

    public BiMap(BiMap biMap) {
        _backwardsMap = new HashMap();
        putAll(biMap);
    }

    public void clear() {
        super.clear();
        _backwardsMap.clear();
    }

    public Object clone() {
        BiMap returnValue = new BiMap();
        returnValue.putAll(this);
        return returnValue;
    }

    private boolean compareContents(BiMap biMap) {
        Iterator keys = keySet().iterator();
        while (keys.hasNext()) {
            Object nextKey = keys.next();
            if (biMap.containsKey(nextKey)) {
                Collection ourValues = (Collection) get(nextKey);
                Collection foreignValues = (Collection) biMap.get(nextKey);
                if (!CollectionUtilities.containSameItems(ourValues, foreignValues)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        if (!(object instanceof BiMap)) {
            return false;
        }
        BiMap biMap = (BiMap) object;
        if (size() != biMap.size()) {
            return false;
        }
        return compareContents(biMap);
    }

    public Collection getKeysForValue(Object value) {
        return (ArrayList) _backwardsMap.get(value);
    }

    public Object put(Object key, Object value) {
        ArrayList backPointers = (ArrayList) _backwardsMap.get(value);
        if (null == backPointers) {
            backPointers = new ArrayList();
            _backwardsMap.put(value, backPointers);
        }
        backPointers.add(key);
        return super.put(key, value);
    }

    public void putAll(Map t) {
        Iterator tEntries = t.entrySet().iterator();
        while (tEntries.hasNext()) {
            Map.Entry next = (Map.Entry) tEntries.next();
            put(next.getKey(), next.getValue());
        }
        return;
    }

    public Object remove(Object key) {
        Object value = get(key);
        ArrayList backPointers = (ArrayList) _backwardsMap.get(value);
        if (null != backPointers) {
            backPointers.remove(key);
        }
        return super.remove(key);
    }
}
