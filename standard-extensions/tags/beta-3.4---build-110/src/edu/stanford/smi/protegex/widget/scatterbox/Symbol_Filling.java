package edu.stanford.smi.protegex.widget.scatterbox;

import java.util.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Symbol_Filling extends BasicOrder {

    public Symbol_Filling(ScatterboxWidget widget, Slot slot, KBQueryUtils queryUtilsObject) {
        super(widget, slot, queryUtilsObject);
    }

    protected Collection getValues() {
        Cls domainCls = _queryUtilsObject.getDomainType(_widget.getCls());
        return domainCls.getTemplateSlotAllowedValues(_slot);
    }
}
