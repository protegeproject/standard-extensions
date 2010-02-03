package edu.stanford.smi.protegex.queries_tab.toolbox;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    Bill Grosso <grosso@smi.stanford.edu>
 */
public abstract class AbstractTemplateSlotNumberValidator implements NumberValidator {
    public final static int VALID_NUMBER = 0;
    public final static int NUMBER_OUT_OF_BOUNDS = 1;
    public final static int FORMAT_ERROR = 2;

    private Cls _cls;
    private Slot _slot;

    public AbstractTemplateSlotNumberValidator(Cls cls, Slot slot) {
        _cls = cls;
        _slot = slot;
    }

    private String getErrorRange(Number min, Number max) {
        String lowerBound = (null == min) ? "-infinity" : min.toString();
        String upperBound = (null == max) ? "+infinity" : max.toString();
        return "[" + lowerBound + ", " + upperBound + "]";
    }

    public boolean isValid(Object o) {
        return validateString(o.toString()) == null;
    }

    public String validateString(String stringToCheck) {
        Number number;
        try {
            number = convertToNumber(stringToCheck);
        } catch (NumberFormatException e) {
            return getErrorMessage();
        }
        Number max = _cls.getTemplateSlotMaximumValue(_slot);
        Number min = _cls.getTemplateSlotMinimumValue(_slot);
        if (null != max) {
            double maxAsDouble = max.doubleValue();
            if (maxAsDouble < number.doubleValue()) {
                return "Number too large. It must be in the range " + getErrorRange(min, max);
            }
        }
        if (null != min) {
            double minAsDouble = min.doubleValue();
            if (minAsDouble > number.doubleValue()) {
                return "Number too small. It must be in the range " + getErrorRange(min, max);
            }
        }
        return null;
    }
    
    public String getErrorMessage(Object o) {
        return getErrorMessage();
    }
    
    public abstract String getErrorMessage();
}
