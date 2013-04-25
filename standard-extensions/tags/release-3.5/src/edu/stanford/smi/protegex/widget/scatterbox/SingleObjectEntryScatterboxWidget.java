package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SingleObjectEntryScatterboxWidget extends ScatterboxWidget {

    private static final long serialVersionUID = 5461223076708179834L;

    protected KBQueryUtils getQueryUtilsObject() {
        return new SingleObjectEntry();
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return SingleObjectEntryScatterboxConfigurationChecks.isSuitable(cls, slot, facet, new SingleObjectEntry());
    }
}
