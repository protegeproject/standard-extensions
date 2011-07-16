package edu.stanford.smi.protegex.widget.instancetable;

import java.util.*;

import javax.swing.table.*;

/**
 *  Basically an adapter-- takes the state and makes it palatable to a Table.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ConfigTableModel extends AbstractTableModel implements Observer {
    private static final long serialVersionUID = 8392619663766943396L;
    private InstanceTableWidgetState _state;

    public ConfigTableModel(InstanceTableWidgetState state) {
        _state = state;
        _state.addObserver(this);
    }

    public int getColumnCount() {
        return (_state.getVisibleSlots()).size() + 1;
    }

    public String getColumnName(int column) {
        if (column != 0) {
            VisibleSlotDescription vsd = _state.getDescriptionForIndex(column - 1);
            return vsd.columnName;
        }
        return " ";
    }

    private String getFirstColumnValue(int row) {
        switch (row) {
            case 0:
                return "Slot Name";
            case 1:
                return "Column Name";
            case 2:
                return "Text Color";
        }
        return null;
    }

    private Object getRealValues(int row, int column) {
        VisibleSlotDescription vsd = _state.getDescriptionForIndex(column);
        switch (row) {
            case 0:
                return vsd.slot;
            case 1:
                return vsd.columnName;
            case 2:
                return vsd.color;
        }
        return null;
    }

    public int getRowCount() {
        return 3;
    }

    public Object getValueAt(int row, int column) {
        if (0 == column) {
            return getFirstColumnValue(row);
        }
        return getRealValues(row, column - 1);
    }

    public void update(Observable o, Object arg) {
        fireTableStructureChanged();
    }
}
