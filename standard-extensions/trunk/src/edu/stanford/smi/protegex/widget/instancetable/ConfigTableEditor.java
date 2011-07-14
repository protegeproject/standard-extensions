package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.util.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  This is not a subtle object. Basically, it's an experiment, to allow in
 *  place editing in our configuration panel.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ConfigTableEditor extends JTextField implements TableCellEditor {
    private static final long serialVersionUID = 5818774260021722810L;
    private JTable _underlyingTable;
    private ColorWell _colorWell;
    private boolean _editingUsingColorWell;
    private TableEditorListenerManager _editorListenerManager;
    private InstanceTableWidgetState _state;
    private int _row;
    private int _column;

    private class ColorChangeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateWidgetState();
        }
    }

    public ConfigTableEditor(JTable underlyingTable, InstanceTableWidgetState state) {
        _underlyingTable = underlyingTable;
        _colorWell = new ColorWell(Color.white, new ColorChangeListener(), false);
        _state = state;
        _editorListenerManager = new TableEditorListenerManager(underlyingTable);
    }

    public void addCellEditorListener(CellEditorListener l) {
        _editorListenerManager.addCellEditorListener(l);
    }

    public void cancelCellEditing() {
        _editorListenerManager.fireEditingCanceled();
    }

    private boolean editableColumn(MouseEvent mouseEvent) {
        int column = _underlyingTable.columnAtPoint(mouseEvent.getPoint());
        if (0 == column) {
            return false;
        }
        return true;
    }

    private boolean editableRow(MouseEvent mouseEvent) {
        int row = _underlyingTable.rowAtPoint(mouseEvent.getPoint());
        if (0 == row) {
            return false;
        }
        return true;
    }

    public Object getCellEditorValue() {
        if (_editingUsingColorWell) {
            return _colorWell.getColor();
        }
        return getText();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        _row = row;
        _column = column;
        if (value instanceof Color) {
            _editingUsingColorWell = true;
            _colorWell.setColor((Color) value);
            return _colorWell;
        }
        if (value instanceof String) {
            setText((String) value);
            _editingUsingColorWell = false;
            setEditable(true);
            setBackground(Color.white);
        } else {
            setEditable(false);
        }
        return this;
    }

    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) anEvent;
            return ((editableRow(mouseEvent)) && (editableColumn(mouseEvent)));
        }
        return false;
    }

    protected void processFocusEvent(FocusEvent e) {
        if (FocusEvent.FOCUS_LOST == e.getID()) {
            updateWidgetState();
        }
        super.processFocusEvent(e);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        _editorListenerManager.removeCellEditorListener(l);
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) anEvent;
            int row = _underlyingTable.rowAtPoint(mouseEvent.getPoint());
            if (row > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean stopCellEditing() {
        _editorListenerManager.fireEditingStopped();
        return true;
    }

    private void updateWidgetState() {
        VisibleSlotDescription vsd = _state.getDescriptionForIndex(_column - 1);
        switch (_row) {
            case 0 :
                vsd.slot = (Slot) getCellEditorValue();
                break;
            case 1 :
                vsd.columnName = (String) getCellEditorValue();
                break;
            case 2 :
                vsd.color = (Color) getCellEditorValue();
                break;
        }
        return;
    }
}
