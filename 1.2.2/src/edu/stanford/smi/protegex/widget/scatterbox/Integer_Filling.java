package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;
import java.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Integer_Filling extends BasicOrder {

    public Integer_Filling(ScatterboxWidget widget, Slot slot, KBQueryUtils queryUtilsObject) {
        super(widget, slot, queryUtilsObject);
    }

    protected Collection getValues() {
        Cls domainCls = _queryUtilsObject.getDomainType(_widget.getCls());
        int minValue = (domainCls.getTemplateSlotMinimumValue(_slot)).intValue();
        int maxValue = (domainCls.getTemplateSlotMaximumValue(_slot)).intValue();
        ArrayList returnValue = new ArrayList();
        for (int loop = minValue; loop <= maxValue; loop++) {
            returnValue.add(new Integer(loop));
        }
        return returnValue;
    }
}
