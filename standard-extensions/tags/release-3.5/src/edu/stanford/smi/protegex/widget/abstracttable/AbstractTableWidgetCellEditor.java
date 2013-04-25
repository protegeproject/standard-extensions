package edu.stanford.smi.protegex.widget.abstracttable;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Delegates UI to a specific TableEditorInterface Aggregates a whole bunch of
 *  simple editors Chooses the right one based on value type.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class AbstractTableWidgetCellEditor implements TableCellEditor {
    private TableEditorListenerManager _editorListenerManager;
    // currently active editor
    private TableEditorInterface _editor;
    private AbstractTableWidgetState _state;
    private JTable _underlyingTable;
    private Project _project;
    private int _row;
    private int _column;
    private HashMap _valueTypesToEditors;

    public AbstractTableWidgetCellEditor(JTable underlyingTable, AbstractTableWidgetState state, Project project) {
        _state = state;
        _project = project;
        _underlyingTable = underlyingTable;
        _editorListenerManager = new TableEditorListenerManager(_underlyingTable);
        createEditors();
    }

    public void addCellEditorListener(CellEditorListener listener) {
        _editorListenerManager.addCellEditorListener(listener);
    }

    public void cancelCellEditing() {
        _editorListenerManager.fireEditingCanceled();
    }

    private void createClassEditor() {
        if (_state.isUseDialogToSelectInstances()) {
            _valueTypesToEditors.put(
                ValueType.INSTANCE,
                new InstanceEditor_UseDialog(_state.getDialogTitleForSelectingInstances(), _underlyingTable, _project));
        } else {
            _valueTypesToEditors.put(ValueType.INSTANCE, new InstanceEditor_PullDownMenu(_underlyingTable, _project));
        }
    }

    private void createEditors() {
        _valueTypesToEditors = new HashMap();
        createSimpleEditors();
        createClassEditor();
        createInstanceEditor();
    }

    private void createInstanceEditor() {
        if (_state.isUseDialogToSelectClasses()) {
            _valueTypesToEditors.put(
                ValueType.CLS,
                new ClassEditor_UseDialog(_state.getDialogTitleForSelectingClasses(), _underlyingTable, _project));
        } else {
            _valueTypesToEditors.put(ValueType.CLS, new ClassEditor_PullDownMenu(_underlyingTable, _project));
        }
    }

    private void createSimpleEditors() {
        _valueTypesToEditors.put(ValueType.FLOAT, new FloatEditor());
        _valueTypesToEditors.put(ValueType.INTEGER, new IntegerEditor());
        _valueTypesToEditors.put(ValueType.STRING, new StringEditor());
        _valueTypesToEditors.put(ValueType.SYMBOL, new SymbolEditor());
        _valueTypesToEditors.put(ValueType.BOOLEAN, new BooleanEditor());
    }

    public Object getCellEditorValue() {
        return (_editor == null) ? null : _editor.getValue();
    }

    private TableEditorInterface getEditor(Instance instance, Slot slot, Collection values) {
        ValueType valueType = instance.getOwnSlotValueType(slot);
        TableEditorInterface returnValue = (TableEditorInterface) _valueTypesToEditors.get(valueType);
        returnValue.setInstance(instance);
        returnValue.setSlot(slot);
        return returnValue;
    }

    /**
         *  value must be an AbstractTableWidgetValue
         *
         * @return             The TableCellEditorComponent value
         * @param  table       Description of Parameter
         * @param  value       Description of Parameter
         * @param  isSelected  Description of Parameter
         * @param  row         Description of Parameter
         * @param  column      Description of Parameter
         */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        AbstractTableWidgetValue typedValue = (AbstractTableWidgetValue) value;
        _editor = getEditor(typedValue.instance, typedValue.slot, typedValue.values);
        JComponent returnValue = (JComponent) _editor;
        _row = row;
        _column = column;
        returnValue.setForeground(typedValue.color);
        return returnValue;
    }

    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public void removeCellEditorListener(CellEditorListener listener) {
        _editorListenerManager.removeCellEditorListener(listener);
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
        _editorListenerManager.fireEditingStopped();
        _editor.storeValueInKB();
        return true;
    }

    public void updateKBIfNecessary() {
        if ((null != _editor) && (_editor.needsToStoreChanges())) {
            _editor.storeValueInKB();
        }
    }
}
