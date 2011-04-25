package edu.stanford.smi.protegex.widget.scatterbox;

import java.util.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class StandardScatterboxConfigurationChecks implements Constants {

    private static boolean boundedClassSlot(Slot slot, Cls cls) {
        if (ValueType.CLS != cls.getTemplateSlotValueType(slot)) {
            return false;
        }
        return true;
    }

    private static boolean boundedInstanceSlot(Slot slot, Cls cls) {
        if (ValueType.INSTANCE != cls.getTemplateSlotValueType(slot)) {
            return false;
        }
        return true;
    }

    private static boolean boundedIntegerSlot(Slot slot, Cls cls) {
        if (ValueType.INTEGER != cls.getTemplateSlotValueType(slot)) {
            return false;
        }
        if (null == cls.getTemplateSlotMaximumValue(slot)) {
            return false;
        }
        if (null == cls.getTemplateSlotMinimumValue(slot)) {
            return false;
        }
        return true;
    }

    private static boolean boundedSymbolSlot(Slot slot, Cls cls) {
        if (ValueType.SYMBOL != cls.getTemplateSlotValueType(slot)) {
            return false;
        }
        return true;
    }

    public static boolean checkClsTaxonomicStructure(Cls cls) {
        KnowledgeBase kb = cls.getKnowledgeBase();
        Cls necessarySuperClass = kb.getCls(FUNCTION_NAME);
        if (necessarySuperClass == null || !cls.hasSuperclass(necessarySuperClass)) {
            return false;
        }
        return true;
    }

    public static boolean goodDomainSpecification(KBQueryUtils queryObject, Cls entryCls) {
        Cls domainType = queryObject.getDomainTypeFromEntryCls(entryCls);
        Collection slots = queryObject.getDomainValueSlots(entryCls);
        if (null == domainType) {
            return false;
        }
        if ((null == slots) || (2 != slots.size())) {
            return false;
        }
        if (!slotsAreBound(slots, domainType)) {
            return false;
        }
        if (!slotsAreBoundedRange(slots, domainType)) {
            return false;
        }
        return true;
    }

    public static boolean goodRangeSpecification(KBQueryUtils queryObject, Cls entryCls) {
        Cls rangeType = queryObject.getRangeTypeFromEntryCls(entryCls);
        Collection slots = queryObject.getRangeValueSlots(entryCls);
        if (null == rangeType) {
            return false;
        }
        if ((null == slots) || (1 != slots.size())) {
            return false;
        }
        if (!slotsAreBound(slots, rangeType)) {
            return false;
        }
        return true;
    }

    public static boolean slotsAreBound(Collection slots, Cls cls) {
        Iterator i = slots.iterator();
        while (i.hasNext()) {
            Slot nextSlot = (Slot) i.next();
            if (!cls.hasTemplateSlot(nextSlot)) {
                return false;
            }
        }
        return true;
    }

    public static boolean slotsAreBoundedRange(Collection slots, Cls cls) {
        Iterator i = slots.iterator();
        while (i.hasNext()) {
            Slot nextSlot = (Slot) i.next();
            if (!boundedIntegerSlot(nextSlot, cls)) {
                if (!boundedSymbolSlot(nextSlot, cls)) {
                    if (!boundedClassSlot(nextSlot, cls)) {
                        if (!boundedInstanceSlot(nextSlot, cls)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
