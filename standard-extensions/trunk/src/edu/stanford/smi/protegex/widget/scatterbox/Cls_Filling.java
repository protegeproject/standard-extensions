package edu.stanford.smi.protegex.widget.scatterbox;

import java.util.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Cls_Filling extends BasicOrder implements Constants {

    public Cls_Filling(ScatterboxWidget widget, Slot slot, KBQueryUtils queryUtilsObject) {
        super(widget, slot, queryUtilsObject);
    }

    private Collection getAllSubclasses(Collection hierarchicalRoots, boolean includeRoots) {
        ArrayList returnValue = new ArrayList();
        Iterator i = hierarchicalRoots.iterator();
        while (i.hasNext()) {
            Cls nextCls = (Cls) i.next();
            ArrayList nextList = new ArrayList(nextCls.getSubclasses());
            if (includeRoots) {
                nextList.add(nextCls);
            }
            returnValue.add(nextList);
        }
        return returnValue;
    }

    private ArrayList getClasses(String policy, Collection hierarchicalRoots) {
        if (null == hierarchicalRoots) {
            return null;
        }
        if (policy.equals(HIERARCHICAL_INCLUDE_EVERYTHING)) {
            return returnEntireHierarchy(hierarchicalRoots);
        }
        if (policy.equals(HIERARCHICAL_OMIT_ROOTS)) {
            return returnHierarchyWithoutRoots(hierarchicalRoots);
        }
        if (policy.equals(HIERARCHICAL_ONLY_INCLUDE_LEVEL_1_CHILDREN)) {
            return returnDirectChildren(hierarchicalRoots);
        }
        return returnLeaves(hierarchicalRoots);
    }

    private Collection getDirectSubclasses(Collection hierarchicalRoots) {
        ArrayList returnValue = new ArrayList();
        Iterator i = hierarchicalRoots.iterator();
        while (i.hasNext()) {
            Cls nextCls = (Cls) i.next();
            ArrayList nextList = new ArrayList(nextCls.getDirectSubclasses());
            returnValue.add(nextList);
        }
        return returnValue;
    }

    protected Collection getValues() {
        Cls domainCls = _queryUtilsObject.getDomainType(_widget.getCls());
        ScatterboxWidgetState state = _widget.getState();
        Collection hierarchicalRoots = domainCls.getTemplateSlotAllowedParents(_slot);
        String policy = state.getHierarchicalPolicyWhenClassesAreDomainIndices();
        ArrayList classes = getClasses(policy, hierarchicalRoots);
        if (!state.isIncludeAbstractClassesInTermList()) {
            return removeAbstractClasses(classes);
        }
        return classes;
    }

    private ArrayList removeAbstractClasses(ArrayList listOfClasses) {
        ArrayList returnValue = new ArrayList();
        Iterator i = listOfClasses.iterator();
        while (i.hasNext()) {
            Cls nextCls = (Cls) i.next();
            if (!nextCls.isAbstract()) {
                returnValue.add(nextCls);
            }
        }
        return returnValue;
    }

    private ArrayList removeInteriorNodes(Collection candidates) {
        ArrayList returnValue = new ArrayList();
        Iterator i = candidates.iterator();
        while (i.hasNext()) {
            Cls nextCls = (Cls) i.next();
            if (0 == nextCls.getDirectSubclassCount()) {
                returnValue.add(nextCls);
            }
        }
        return returnValue;
    }

    private ArrayList returnDirectChildren(Collection hierarchicalRoots) {
        Collection collectionOfListsOfClasses = getDirectSubclasses(hierarchicalRoots);
        return concatenateWithoutDuplicates(collectionOfListsOfClasses);
    }

    private ArrayList returnEntireHierarchy(Collection hierarchicalRoots) {
        Collection collectionOfListsOfClasses = getAllSubclasses(hierarchicalRoots, true);
        return concatenateWithoutDuplicates(collectionOfListsOfClasses);
    }

    private ArrayList returnHierarchyWithoutRoots(Collection hierarchicalRoots) {
        Collection collectionOfListsOfClasses = getAllSubclasses(hierarchicalRoots, false);
        return concatenateWithoutDuplicates(collectionOfListsOfClasses);
    }

    private ArrayList returnLeaves(Collection hierarchicalRoots) {
        Collection collectionOfListsOfClasses = getAllSubclasses(hierarchicalRoots, false);
        Collection candidates = concatenateWithoutDuplicates(collectionOfListsOfClasses);
        return removeInteriorNodes(candidates);
    }
}
