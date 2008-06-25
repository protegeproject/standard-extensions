package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class DomainRangeObjectPairEntryScatterboxConfigurationChecks implements DomainRangeObjectPairConstants {

    private static boolean checkEntryClsTaxonomicStructure(KBQueryUtils queryObject, Cls entryCls) {
        KnowledgeBase kb = entryCls.getKnowledgeBase();
        Cls entryMetaClass = kb.getCls(ENTRY_METACLASS_NAME);
        if (!entryCls.hasType(entryMetaClass)) {
            return false;
        }
        if (!StandardScatterboxConfigurationChecks.goodDomainSpecification(queryObject, entryCls)) {
            return false;
        }
        if (!StandardScatterboxConfigurationChecks.goodRangeSpecification(queryObject, entryCls)) {
            return false;
        }
        return true;
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet, KBQueryUtils queryObject) {
        if ((null == cls) || (null == slot)) {
            return false;
        }
        if (!StandardScatterboxConfigurationChecks.checkClsTaxonomicStructure(cls)) {
            return false;
        }
        Cls entryCls = queryObject.getEntryCls(cls);
        if ((null == entryCls) || !checkEntryClsTaxonomicStructure(queryObject, entryCls)) {
            return false;
        }
        return true;
    }
}
