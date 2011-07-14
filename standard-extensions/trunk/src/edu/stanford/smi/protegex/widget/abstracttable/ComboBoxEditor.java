package edu.stanford.smi.protegex.widget.abstracttable;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class ComboBoxEditor extends JComboBox implements TableEditorInterface {
    private static final long serialVersionUID = 5512576126000550787L;
    protected Instance _instance;
    protected Slot _slot;
    protected boolean _readingFromModel;
    protected boolean _processingSelection;

    public ComboBoxEditor() {
        this(null, null);
    }

    public ComboBoxEditor(Instance instance, Slot slot) {
        super();
        _processingSelection = false;
        _readingFromModel = false;
        _instance = instance;
        _slot = slot;
        setModel(getComboBoxModel());
        configureUIFromKB();
    }

    public abstract void configureUIFromKB();

    protected abstract ComboBoxModel getComboBoxModel();

    public Object getValue() {
        if ((null == _instance) || (null == _slot)) {
            return null;
        }
        return _instance.getOwnSlotValue(_slot);
    }

    protected void handleSelectionChange() {
        if (_processingSelection) {
            return;
        }
        _processingSelection = true;
        if (!_readingFromModel) {
            storeValueInKB();
        }
        _processingSelection = false;
    }

    /**
     * @deprecated
     */
    public boolean isManagingFocus() {
        return false;
    }

    public boolean isValueAcceptable() {
        return true;
    }

    public boolean needsToStoreChanges() {
        return false;
    }

    protected void selectedItemChanged() {
        handleSelectionChange();
    }

    public void setInstance(Instance instance) {
        _instance = instance;
        configureUIFromKB();
    }

    public void setSlot(Slot slot) {
        _slot = slot;
        configureUIFromKB();
    }

    public abstract void storeValueInKB();
}
