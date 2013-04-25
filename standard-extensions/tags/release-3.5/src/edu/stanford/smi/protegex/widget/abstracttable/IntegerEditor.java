package edu.stanford.smi.protegex.widget.abstracttable;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class IntegerEditor extends TextFieldEditor {

    private static final long serialVersionUID = 2585803814489149210L;

    public IntegerEditor() {
        super(null, null);
    }

    public IntegerEditor(Instance instance, Slot slot) {
        super(instance, slot);
    }

    protected Object convertString(String text) {
        try {
            return new Integer(text);
        } catch (Exception e) {
        }
        return null;
    }
}
