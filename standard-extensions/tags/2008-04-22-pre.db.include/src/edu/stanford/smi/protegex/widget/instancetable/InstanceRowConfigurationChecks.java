package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Static methods to make sure that InstanceTable is valid. Returns a string. A
 *  null return value means "okay"
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InstanceRowConfigurationChecks {

    public static boolean checkValidity(Cls cls, Slot slot) {
        if ((null == cls) || (null == slot)) {
            return false;
        }
        boolean returnValue = true;
        returnValue = returnValue && slotTargetOfTypeInstance(cls, slot);
        returnValue = returnValue && slotHasRightCardinality(cls, slot);
        // returnValue = returnValue && singleAllowedClass(cls, slot);
        return returnValue;
    }

    public static JComponent getDetailedWarnings(Cls cls, Slot slot) {
        JTextArea returnValue = new JTextArea();
        boolean noErrorsFound = singleAllowedClass(cls, slot, returnValue);
        if (noErrorsFound) {
            returnValue.setForeground(Color.black);
            returnValue.setText("The widget seems to be correctly configured.");
        }
        return returnValue;
    }

    public static JComponent getShortWarning(Cls cls, Slot slot) {
        if (checkValidity(cls, slot)) {
            return null;
        }
        JTextField returnValue = new JTextField();
        returnValue.setForeground(Color.red);
        returnValue.setText("Widget incorrectly configured. See Forms Layout Panel for details.");
        return returnValue;
    }

    protected static boolean singleAllowedClass(Cls cls, Slot slot) {
        return cls.getTemplateSlotAllowedClses(slot).size() == 1;
    }

    protected static boolean singleAllowedClass(Cls cls, Slot slot, JTextArea warningDisplayArea) {
        if (!singleAllowedClass(cls, slot)) {
            warningDisplayArea.setForeground(Color.red);
            warningDisplayArea.append("The slot must have a single allowed class. \n");
            return false;
        }
        return true;
    }

    protected static boolean slotHasRightCardinality(Cls cls, Slot slot) {
        return !cls.getTemplateSlotAllowsMultipleValues(slot);
    }

    protected static boolean slotHasRightCardinality(Cls cls, Slot slot, JTextArea warningDisplayArea) {
        if (!slotHasRightCardinality(cls, slot)) {
            warningDisplayArea.setForeground(Color.red);
            warningDisplayArea.append("The slot must be a single-valued slot. \n");
            return false;
        }
        return true;
    }

    // validity checks
    protected static boolean slotTargetOfTypeInstance(Cls cls, Slot slot) {
        return cls.getTemplateSlotValueType(slot) == ValueType.INSTANCE;
    }

    // configuration checks
    protected static boolean slotTargetOfTypeInstance(Cls cls, Slot slot, JTextArea warningDisplayArea) {
        if (!slotTargetOfTypeInstance(cls, slot)) {
            warningDisplayArea.setForeground(Color.red);
            warningDisplayArea.append("The Target of the slot must be of type instance. \n");
            return false;
        }
        return true;
    }
}
