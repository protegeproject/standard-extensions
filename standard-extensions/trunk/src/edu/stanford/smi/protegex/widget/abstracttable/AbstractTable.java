package edu.stanford.smi.protegex.widget.abstracttable;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class AbstractTable extends JTable {
    private static final long serialVersionUID = -8120613409704165834L;
    protected TableModel _model;
    protected AbstractTableWidgetCellEditor _editor;

    public AbstractTable(TableModel model) {
        this(model, null);
    }

    public AbstractTable(TableModel model, AbstractTableWidgetCellEditor editor) {
        super(model);
        _model = model;
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setOpaque(true);
        setEditor(editor);
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
        super.columnSelectionChanged(e);
        tellEditorAboutChange();
    }

    public boolean editCellAt(int row, int column, EventObject e) {
        if (e instanceof MouseEvent) {
            if (((MouseEvent) e).getClickCount() == 1) {
                return false;
            }
        }
        return super.editCellAt(row, column, e);
    }

    public abstract boolean isCellEditable(int row, int column);

    public abstract void setEditor(AbstractTableWidgetCellEditor editor);

    public void setPreferredWidthForColumn(int column, int preferredWidth) {
        TableColumnModel columnModel = getColumnModel();
        if (column < columnModel.getColumnCount()) {
            TableColumn columnObject = columnModel.getColumn(column);
            columnObject.setPreferredWidth(preferredWidth);
        }
    }

    public void stopEditing() {
        if (isEditing()) {
            _editor.cancelCellEditing();
        }
    }

    private void tellEditorAboutChange() {
        if (null != _editor) {
            _editor.updateKBIfNecessary();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        tellEditorAboutChange();
    }
}
