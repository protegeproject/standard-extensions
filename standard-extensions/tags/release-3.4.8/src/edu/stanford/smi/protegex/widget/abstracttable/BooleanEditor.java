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
public class BooleanEditor extends ComboBoxEditor {
    private static final long serialVersionUID = -1174955571929718873L;
    private ArrayList _choices;
    protected EditableComboBoxModel _model;
    protected Boolean _value;
    private static String TRUE = "true";
    private static String FALSE = "false";

    public BooleanEditor() {
        this(null, null);
    }

    public BooleanEditor(Instance instance, Slot slot) {
        super(instance, slot);
    }

    private void configureComboBoxModel() {
        if (null == _choices) {
            _choices = new ArrayList();
            _choices.add(NULL_STRING);
            _choices.add(TRUE);
            _choices.add(FALSE);
        }
        _model.setContents(_choices);
    }

    public void configureUIFromKB() {
        if (_readingFromModel) {
            return;
        }
        _readingFromModel = true;
        _value = (Boolean) getValue();
        configureComboBoxModel();
        selectValueInComboBox();
        _readingFromModel = false;
    }

    protected ComboBoxModel getComboBoxModel() {
        _model = new EditableComboBoxModel();
        return _model;
    }

    public void selectValueInComboBox() {
        Object value = getValue();
        if (null == value) {
            _model.setSelectedItem(NULL_STRING);
        } else {
            if ((Boolean.TRUE).equals(value)) {
                _model.setSelectedItem(TRUE);
            } else {
                _model.setSelectedItem(FALSE);
            }
        }
    }

    public void storeValueInKB() {
        if ((_instance == null) || (_slot == null)) {
            return;
        }
        Object value = _model.getSelectedItem();
        if (NULL_STRING == value) {
            _instance.setOwnSlotValue(_slot, null);
        } else {
            if (TRUE == value) {
                _instance.setOwnSlotValue(_slot, Boolean.TRUE);
            } else {
                _instance.setOwnSlotValue(_slot, Boolean.FALSE);
            }
        }
    }
}
