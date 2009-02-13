package edu.stanford.smi.protegex.widget.abstracttable;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class StringEditor extends TextFieldEditor {

    public StringEditor() {
        super(null, null);
    }

    public StringEditor(Instance instance, Slot slot) {
        super(instance, slot);
    }

    protected Object convertString(String text) {
        return text;
    }
}
