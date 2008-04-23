package edu.stanford.smi.protegex.widget.abstracttable;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class FloatEditor extends TextFieldEditor {

    public FloatEditor() {
        super(null, null);
    }

    public FloatEditor(Instance instance, Slot slot) {
        super(instance, slot);
    }

    protected Object convertString(String text) {
        try {
            return new Float(text);
        } catch (Exception e) {
        }
        return null;
    }
}
