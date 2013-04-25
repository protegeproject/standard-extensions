package edu.stanford.smi.protegex.widget.scatterbox;

import javax.swing.table.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class RowHeaderTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -8825063708542403637L;
    private Order _underlyingOrder;

    public RowHeaderTableModel(Order order) {
        _underlyingOrder = order;
    }

    public int getColumnCount() {
        return 1;
    }

    public String getColumnName(int columnIndex) {
        return "";
    }

    public int getRowCount() {
        return _underlyingOrder.getSize();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = _underlyingOrder.getValueForIndex(rowIndex);
        if (value instanceof Instance) {
            return ((Instance) value).getBrowserText();
        }
        return value;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
