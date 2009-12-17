package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;
import java.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Instance_Filling extends BasicOrder implements Constants {

    public Instance_Filling(ScatterboxWidget widget, Slot slot, KBQueryUtils queryUtilsObject) {
        super(widget, slot, queryUtilsObject);
    }

    private ArrayList getInstances(String policy, Collection hierarchicalRoots) {
        if (null == hierarchicalRoots) {
            return null;
        }
        if (policy.equals(DIRECT_INSTANCES_ONLY)) {
            return returnDirectInstances(hierarchicalRoots);
        }
        return returnAllInstances(hierarchicalRoots);
    }

    protected Collection getValues() {
        Cls domainCls = _queryUtilsObject.getDomainType(_widget.getCls());
        ScatterboxWidgetState state = _widget.getState();
        Collection hierarchicalRoots = domainCls.getTemplateSlotAllowedClses(_slot);
        String policy = state.getPolicyWhenInstancesAreDomainIndices();
        ArrayList instances = getInstances(policy, hierarchicalRoots);
        return instances;
    }

    private ArrayList returnAllInstances(Collection hierarchicalRoots) {
        ArrayList instanceList = new ArrayList();
        Iterator i = hierarchicalRoots.iterator();
        while (i.hasNext()) {
            Cls nextCls = (Cls) i.next();
            ArrayList nextList = new ArrayList(nextCls.getInstances());
            instanceList.add(nextList);
        }
        return concatenateWithoutDuplicates(instanceList);
    }

    private ArrayList returnDirectInstances(Collection hierarchicalRoots) {
        ArrayList instanceList = new ArrayList();
        Iterator i = hierarchicalRoots.iterator();
        while (i.hasNext()) {
            Cls nextCls = (Cls) i.next();
            ArrayList nextList = new ArrayList(nextCls.getDirectInstances());
            instanceList.add(nextList);
        }
        return concatenateWithoutDuplicates(instanceList);
    }
}
