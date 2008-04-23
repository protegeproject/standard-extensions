package edu.stanford.smi.protegex.widget.scatterbox;

import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class DomainRangeObjectPairEntry extends BasicQueryObject implements DomainRangeObjectPairConstants {

    public Instance getDomainObject(Instance entry) {
        KnowledgeBase kb = entry.getKnowledgeBase();
        Slot domainValueSlot = kb.getSlot(DOMAIN_VALUE_SLOT);
        return (Instance) entry.getOwnSlotValue(domainValueSlot);
    }

    public Cls getDomainTypeFromEntryCls(Cls entryCls) {
        KnowledgeBase kb = entryCls.getKnowledgeBase();
        Slot domainValueSlot = kb.getSlot(DOMAIN_VALUE_SLOT);
        return getType(entryCls, domainValueSlot);
    }

    public Instance getOrCreateDomainObject(Instance entry) {
        Instance domainInstance = getDomainObject(entry);
        if (null != domainInstance) {
            return domainInstance;
        }
        KnowledgeBase kb = entry.getKnowledgeBase();
        Cls entryCls = entry.getDirectType();
        Cls domainCls = getDomainTypeFromEntryCls(entryCls);

        domainInstance = kb.createInstance(null, domainCls);
        setDomainObject(entry, domainInstance);
        return domainInstance;
    }

    public Instance getOrCreateRangeObject(Instance entry) {
        Instance rangeInstance = getRangeObject(entry);
        if (null != rangeInstance) {
            return rangeInstance;
        }
        KnowledgeBase kb = entry.getKnowledgeBase();
        Cls entryCls = entry.getDirectType();
        Cls rangeCls = getRangeTypeFromEntryCls(entryCls);

        rangeInstance = kb.createInstance(null, rangeCls);
        setRangeObject(entry, rangeInstance);
        return rangeInstance;
    }

    public Instance getRangeObject(Instance entry) {
        KnowledgeBase kb = entry.getKnowledgeBase();
        Slot rangeValueSlot = kb.getSlot(RANGE_VALUE_SLOT);
        return (Instance) entry.getOwnSlotValue(rangeValueSlot);
    }

    // range
    public Cls getRangeTypeFromEntryCls(Cls entryCls) {
        KnowledgeBase kb = entryCls.getKnowledgeBase();
        Slot rangeValueSlot = kb.getSlot(RANGE_VALUE_SLOT);
        return getType(entryCls, rangeValueSlot);
    }

    private Cls getType(Cls cls, Slot slot) {
        Collection allowedClasses = cls.getTemplateSlotAllowedClses(slot);
        if ((null == allowedClasses) || (1 != allowedClasses.size())) {
            return null;
        }
        Cls type = (Cls) CollectionUtilities.getSoleItem(allowedClasses);
        return type;
    }

    public void setDomainObject(Instance entry, Instance domainObject) {
        KnowledgeBase kb = entry.getKnowledgeBase();
        Slot domainValueSlot = kb.getSlot(DOMAIN_VALUE_SLOT);
        entry.setOwnSlotValue(domainValueSlot, domainObject);
        return;
    }

    public void setRangeObject(Instance entry, Instance rangeObject) {
        KnowledgeBase kb = entry.getKnowledgeBase();
        Slot rangeValueSlot = kb.getSlot(RANGE_VALUE_SLOT);
        entry.setOwnSlotValue(rangeValueSlot, rangeObject);
        return;
    }
}
