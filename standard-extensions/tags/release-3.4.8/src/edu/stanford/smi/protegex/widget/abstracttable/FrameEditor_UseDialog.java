package edu.stanford.smi.protegex.widget.abstracttable;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class FrameEditor_UseDialog extends FrameEditor implements TableEditorInterface {

    private static final long serialVersionUID = 3748743384297653356L;

    public FrameEditor_UseDialog(JComponent onScreenComponent, Project project) {
        this(onScreenComponent, project, null, null);
    }

    public FrameEditor_UseDialog(JComponent onScreenComponent, Project project, Instance instance, Slot slot) {
        super(onScreenComponent, project, instance, slot);
    }

    protected void configureComboBoxModel() {
        ArrayList modelValues = new ArrayList();
        if (null == _value) {
            modelValues.add(NULL_STRING);
        } else {
            modelValues.add(_value);
        }
        if (_value != null) {
            modelValues.add(EDIT);
        }
        modelValues.add(CHOOSE);
        if (_value != null) {
            modelValues.add(REMOVE);
        }
        _model.setContents(modelValues);
    }

    protected List getPossibleChoices() {
        throw new Error("FrameEditor_UseDialog's getPossibleChoices method called");
    }

    protected abstract void getValueFromDialog();
}
