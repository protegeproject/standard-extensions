package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class DomainRangeObjectPairEntryScatterboxWidget extends ScatterboxWidget {

    private static final long serialVersionUID = -2938578903728571674L;

    protected KBQueryUtils getQueryUtilsObject() {
        return new DomainRangeObjectPairEntry();
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return DomainRangeObjectPairEntryScatterboxConfigurationChecks.isSuitable(
            cls,
            slot,
            facet,
            new DomainRangeObjectPairEntry());
    }
}
