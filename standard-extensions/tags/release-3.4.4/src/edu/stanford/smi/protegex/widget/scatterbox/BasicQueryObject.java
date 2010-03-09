package edu.stanford.smi.protegex.widget.scatterbox;

import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class BasicQueryObject implements KBQueryUtils {

    public abstract Instance getDomainObject(Instance entry);

    // domain
    public Cls getDomainType(Cls functionCls) {
        Cls entryCls = getEntryCls(functionCls);
        return getDomainTypeFromEntryCls(entryCls);
    }

    public abstract Cls getDomainTypeFromEntryCls(Cls entryCls);

    public Collection getDomainValueSlots(Cls entryCls) {
        KnowledgeBase kb = entryCls.getKnowledgeBase();
        Slot domainValueSlots = kb.getSlot(DOMAIN_VALUE_SLOTS);
        return entryCls.getOwnSlotValues(domainValueSlots);
    }

    public Slot getEntriesSlot(Cls functionCls) {
        KnowledgeBase kb = functionCls.getKnowledgeBase();
        return kb.getSlot(ENTRY_SLOT_NAME);
    }

    public Cls getEntryCls(Cls functionCls) {
        Slot entriesSlot = getEntriesSlot(functionCls);
        Slot entrySlot = (Slot) functionCls.getOwnSlotValue(entriesSlot);
        if (null == entrySlot) {
            return null;
        }
        Collection allowedClasses = functionCls.getTemplateSlotAllowedClses(entrySlot);
        if ((null == allowedClasses) || (1 != allowedClasses.size())) {
            return null;
        }
        Cls entryCls = (Cls) CollectionUtilities.getSoleItem(allowedClasses);
        return entryCls;
    }

    public abstract Instance getOrCreateDomainObject(Instance entry);

    public abstract Instance getOrCreateRangeObject(Instance entry);

    public abstract Instance getRangeObject(Instance entry);

    // range
    public Cls getRangeType(Cls functionCls) {
        Cls entryCls = getEntryCls(functionCls);
        return getRangeTypeFromEntryCls(entryCls);
    }

    public abstract Cls getRangeTypeFromEntryCls(Cls entryCls);

    public Slot getRangeValueSlot(Cls entryCls) {
        Collection rangeValueSlots = getRangeValueSlots(entryCls);
        if (null == rangeValueSlots) {
            return null;
        }
        return (Slot) CollectionUtilities.getFirstItem(rangeValueSlots);
    }

    public Slot getRangeValueSlot(Instance entry) {
        Cls entryCls = entry.getDirectType();
        return getRangeValueSlot(entryCls);
    }

    public Collection getRangeValueSlots(Cls entryCls) {
        KnowledgeBase kb = entryCls.getKnowledgeBase();
        Slot rangeValueSlots = kb.getSlot(RANGE_VALUE_SLOTS);
        return entryCls.getOwnSlotValues(rangeValueSlots);
    }

    public Collection getRangeValueSlots(Instance entry) {
        Cls entryCls = entry.getDirectType();
        return getRangeValueSlots(entryCls);
    }

    public abstract void setDomainObject(Instance entry, Instance domainObject);

    public abstract void setRangeObject(Instance entry, Instance rangeObject);
}
