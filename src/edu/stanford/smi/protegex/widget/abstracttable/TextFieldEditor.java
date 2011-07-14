package edu.stanford.smi.protegex.widget.abstracttable;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class TextFieldEditor extends JTextField implements TableEditorInterface {
    private static final long serialVersionUID = -7688416828964424490L;
    protected Instance _instance;
    protected Slot _slot;
    private boolean _hasBeenEdited;

    private class TextChangedListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            _hasBeenEdited = true;
        }

        public void insertUpdate(DocumentEvent e) {
            _hasBeenEdited = true;
        }

        public void removeUpdate(DocumentEvent e) {
            _hasBeenEdited = true;
        }
    }

    public TextFieldEditor(Instance instance, Slot slot) {
        _instance = instance;
        _slot = slot;
        setValueFromKB();
        (getDocument()).addDocumentListener(new TextChangedListener());
    }

    protected abstract Object convertString(String text);

    public Object getValue() {
        return convertString(getText());
    }

    /**
     * @deprecated
     */
    public boolean isManagingFocus() {
        return true;
    }

    public boolean isValueAcceptable() {
        return (null != getValue());
    }

    public boolean needsToStoreChanges() {
        return _hasBeenEdited;
    }

    // Functionality we need (AbstractEditor and subclasses)
    public void setInstance(Instance instance) {
        _instance = instance;
        setValueFromKB();
    }

    public void setSlot(Slot slot) {
        _slot = slot;
        setValueFromKB();
    }

    private void setValueFromKB() {
        if ((null != _instance) && (null != _slot)) {
            Object actualValue = _instance.getOwnSlotValue(_slot);
            if (null != actualValue) {
                setText(actualValue.toString());
            } else {
                setText("");
            }
            _hasBeenEdited = false;
        }
        return;
    }

    public void storeValueInKB() {
        Object value = getValue();
        if (null != value) {
            _instance.setOwnSlotValue(_slot, value);
        } else {
            if ("".equals(getText())) {
                _instance.setOwnSlotValue(_slot, null);
            }
        }
        _hasBeenEdited = false;
    }
}
