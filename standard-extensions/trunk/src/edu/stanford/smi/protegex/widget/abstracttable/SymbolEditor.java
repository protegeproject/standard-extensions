package edu.stanford.smi.protegex.widget.abstracttable;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SymbolEditor extends ComboBoxEditor {
    private static final long serialVersionUID = 4447324250250517518L;
    protected AlphabeticalComboBoxModel _model;

    public SymbolEditor() {
        this(null, null);
    }

    public SymbolEditor(Instance instance, Slot slot) {
        super(instance, slot);
    }

    public void configureUIFromKB() {
        if (_readingFromModel) {
            return;
        }
        _readingFromModel = true;
        if ((null != _instance) && (null != _slot)) {
            Cls instanceCls = _instance.getDirectType();
            Collection possibleValues = instanceCls.getTemplateSlotAllowedValues(_slot);
            ArrayList modifiedValues = new ArrayList(possibleValues);
            modifiedValues.add(NULL_STRING);
            ((AlphabeticalComboBoxModel) _model).setContents(modifiedValues);
            setValue();
        }
        _readingFromModel = false;
    }

    protected ComboBoxModel getComboBoxModel() {
        _model = new AlphabeticalComboBoxModel();
        return _model;
    }

    public void setValue() {
        Object value = getValue();
        if (null == value) {
            _model.setSelectedItem(NULL_STRING);
        } else {
            _model.setSelectedItem(value);
        }
    }

    public void storeValueInKB() {
        Object value = _model.getSelectedItem();
        if (value == NULL_STRING) {
            _instance.setOwnSlotValue(_slot, null);
        } else {
            _instance.setOwnSlotValue(_slot, value);
        }
    }
}
