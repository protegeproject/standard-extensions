package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SingleObjectEntry extends BasicQueryObject {

    public Instance getDomainObject(Instance entry) {
        return entry;
    }

    public Cls getDomainTypeFromEntryCls(Cls entryCls) {
        return entryCls;
    }

    public Instance getOrCreateDomainObject(Instance entry) {
        return entry;
    }

    public Instance getOrCreateRangeObject(Instance entry) {
        return entry;
    }

    public Instance getRangeObject(Instance entry) {
        return entry;
    }

    public Cls getRangeTypeFromEntryCls(Cls entryCls) {
        return entryCls;
    }

    public void setDomainObject(Instance entry, Instance domainObject) {
        return;
    }

    public void setRangeObject(Instance entry, Instance rangeObject) {
        return;
    }
}
