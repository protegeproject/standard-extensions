package edu.stanford.smi.protegex.widget.abstracttable;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class FrameEditor_PullDownMenu extends FrameEditor implements TableEditorInterface {

    private static final long serialVersionUID = 1708695684789857341L;

    public FrameEditor_PullDownMenu(JComponent onScreenComponent, Project project) {
        this(onScreenComponent, project, null, null);
    }

    public FrameEditor_PullDownMenu(JComponent onScreenComponent, Project project, Instance instance, Slot slot) {
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
            modelValues.add(REMOVE);
        }
        modelValues.addAll(getPossibleChoices());
        _model.setContents(modelValues);
    }

    protected abstract List getPossibleChoices();

    protected void getValueFromDialog() {
        throw new Error("FrameEditor_PullDownMenu's getValuerFromDialog method called");
    }
}
