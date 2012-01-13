package edu.stanford.smi.protegex.widget.instancetable;

import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InstanceTable extends AbstractTable {
    private static final long serialVersionUID = -3747746131691986890L;
    private InstanceTableModel _instanceTableModel;

    public InstanceTable(InstanceTableModel model, AbstractTableWidgetCellEditor editor, boolean highlightSelectedRow) {
        super(model, editor);
        _instanceTableModel = model;
        setDefaultRenderer(Object.class, new GenericTableCellRenderer(highlightSelectedRow));
    }

    public InstanceTable(InstanceTableModel model, boolean highlightSelectedRow) {
        this(model, null, highlightSelectedRow);
    }

    public int getRowForInstance(Instance instance) {
        return _instanceTableModel.getRowForInstance(instance);
    }

    public Collection getSelectedInstances() {
        ArrayList returnValue = new ArrayList();
        int[] selectedRows = getSelectedRows();
        int loop;
        for (loop = 0; loop < selectedRows.length; loop++) {
            returnValue.add(_instanceTableModel.getInstanceAt(selectedRows[loop]));
        }
        return returnValue;
    }

    public boolean isCellEditable(int row, int column) {
        if (null == _editor) {
            return false;
        }
        AbstractTableWidgetValue value = (AbstractTableWidgetValue) _model.getValueAt(row, column);
        Cls cls = (value.instance).getDirectType();
        if (cls.getTemplateSlotAllowsMultipleValues(value.slot)) {
            return false;
        }
        return true;
    }

    public void setEditor(AbstractTableWidgetCellEditor editor) {
        _editor = editor;
        if (_editor != null) {
            setRowSelectionAllowed(false);
            setColumnSelectionAllowed(false);
            setCellSelectionEnabled(true);
            setDefaultEditor(AbstractTableWidgetValue.class, editor);
        } else {
            setRowSelectionAllowed(true);
            setColumnSelectionAllowed(false);
        }
    }
}
