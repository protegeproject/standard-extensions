package edu.stanford.smi.protegex.queries_tab.toolbox;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    Bill Grosso <grosso@smi.stanford.edu>
 */
public class IntegerTemplateSlotValidator extends AbstractTemplateSlotNumberValidator {

    public IntegerTemplateSlotValidator(Cls cls, Slot slot) {
        super(cls, slot);
    }

    public Number convertToNumber(String stringToCheck) throws NumberFormatException {
        return new Integer(stringToCheck);
    }

    public String getErrorMessage() {
        return "Text does not define a integral number";
    }
}
