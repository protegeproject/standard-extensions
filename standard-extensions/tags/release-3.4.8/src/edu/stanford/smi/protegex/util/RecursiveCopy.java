package edu.stanford.smi.protegex.util;

import java.util.*;

import edu.stanford.smi.protege.model.*;
/*
 * Recursive copy does not create new slots or facets qua slots or facets.
 * It does, however, create new slots or facets if they are *targets*.
 */

/**
 *  Recursive copy of simple instances
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class RecursiveCopy {

    private static void copyOwnSlot(
        Slot slot,
        Instance source,
        Instance destination,
        int depth,
        HashMap alreadyDuplicatedInstances,
        List newInstancesCreated) {
        Iterator values = (source.getOwnSlotValues(slot)).iterator();
        while (values.hasNext()) {
            Object value = values.next();
            if (value instanceof Instance) {
                if ((value instanceof Cls) || (value instanceof Facet) || (value instanceof Slot) || (depth == 0)) {
                    destination.addOwnSlotValue(slot, value);
                } else {
                    destination.addOwnSlotValue(
                        slot,
                        recursivelyCopyInstance(
                            (Instance) value,
                            depth - 1,
                            alreadyDuplicatedInstances,
                            newInstancesCreated));
                }
            } else {
                destination.addOwnSlotValue(slot, value);
            }
        }
    }

    private static void copyOwnSlots(
        Instance source,
        Instance destination,
        int depth,
        HashMap alreadyDuplicatedInstances,
        List newInstancesCreated) {
        Iterator slots = (source.getOwnSlots()).iterator();
        while (slots.hasNext()) {
            Slot nextSlot = (Slot) slots.next();
            if (isCopyableSlot(nextSlot)) {
                copyOwnSlot(nextSlot, source, destination, depth, alreadyDuplicatedInstances, newInstancesCreated);
            }
        }
        return;
    }

    private static boolean isCopyableSlot(Slot slot) {
        String name = slot.getName();
        return !(name.equals(Model.Slot.NAME) || (name.equals(Model.Slot.DIRECT_TYPES)));
    }

    public static Instance recursivelyCopyInstance(Instance instance, int depth) {
        return recursivelyCopyInstance(instance, depth, new HashMap(), null);
    }

    private static Instance recursivelyCopyInstance(
        Instance instance,
        int depth,
        HashMap alreadyDuplicatedInstances,
        List newInstancesCreated) {
        Instance returnValue = (Instance) alreadyDuplicatedInstances.get(instance);
        if (null != returnValue) {
            return returnValue;
        }
        Cls cls = instance.getDirectType();
        KnowledgeBase kb = instance.getKnowledgeBase();
        // create an instance but don't fill in the default values
        returnValue = kb.createInstance((String) null, cls, false);
        alreadyDuplicatedInstances.put(instance, returnValue);
        if (null != newInstancesCreated) {
            newInstancesCreated.add(returnValue);
        }
        copyOwnSlots(instance, returnValue, depth, alreadyDuplicatedInstances, newInstancesCreated);
        return returnValue;
    }

    public static Instance recursivelyCopyInstance(Instance instance, int depth, List newInstancesCreated) {
        return recursivelyCopyInstance(instance, depth, new HashMap(), newInstancesCreated);
    }
}
