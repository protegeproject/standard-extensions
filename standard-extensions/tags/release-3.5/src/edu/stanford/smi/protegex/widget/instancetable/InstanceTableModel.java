package edu.stanford.smi.protegex.widget.instancetable;

import java.util.*;

import javax.swing.table.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InstanceTableModel extends AbstractTableModel implements Observer {
    private static final long serialVersionUID = -52922615179005755L;
    private ArrayList _values;
    // <Instance>
    private InstanceTableWidgetState _state;
    private int rowCount;
    private int columnCount;
    private AbstractTableWidgetValue _displayValueObject;
    private OurFrameListener _ourFrameListener;
    private InstanceTable _underlyingTable;

    private class OurFrameListener implements FrameListener {
        public void ownSlotValueChanged(FrameEvent event) {
            broadcastChangeAnnouncement();
        }

        public void browserTextChanged(FrameEvent event) {
        }

        public void deleted(FrameEvent event) {
        }

        public void editabilityChanged(FrameEvent event) {
        }

        public void nameChanged(FrameEvent event) {
        }

        public void ownFacetAdded(FrameEvent event) {
        }

        public void ownFacetRemoved(FrameEvent event) {
        }

        public void ownFacetValueChanged(FrameEvent event) {
        }

        public void ownSlotAdded(FrameEvent event) {
        }

        public void ownSlotRemoved(FrameEvent event) {
        }

        public void visibilityChanged(FrameEvent event) {
        }
    }

    public InstanceTableModel(InstanceTable underlyingTable, Collection values, InstanceTableWidgetState state) {
        _values = new ArrayList(values);
        _state = state;
        _displayValueObject = new AbstractTableWidgetValue();
        _ourFrameListener = new OurFrameListener();
        _underlyingTable = underlyingTable;
        attachListener();
        refreshCachedValues();
    }

    private void attachListener() {
        Iterator i = _values.iterator();
        while (i.hasNext()) {
            ((Frame) i.next()).addFrameListener(_ourFrameListener);
        }
    }

    protected void broadcastChangeAnnouncement() {
        _underlyingTable.stopEditing();
        fireTableDataChanged();
    }

    private void detachListener() {
        Iterator i = _values.iterator();
        while (i.hasNext()) {
            ((Frame) i.next()).removeFrameListener(_ourFrameListener);
        }
    }

    public Class getColumnClass(int column) {
        return AbstractTableWidgetValue.class;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public String getColumnName(int index) {
        VisibleSlotDescription vsd = _state.getDescriptionForIndex(index);
        return vsd.columnName;
    }

    public Instance getInstanceAt(int row) {
        return (Instance) _values.get(row);
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getRowForInstance(Instance instance) {
        return _values.indexOf(instance);
    }

    public InstanceTable getUnderlyingTable() {
        return _underlyingTable;
    }

    public Object getValueAt(int row, int column) {
        Instance instance = (Instance) _values.get(row);
        VisibleSlotDescription vsd = _state.getDescriptionForIndex(column);
        _displayValueObject.color = vsd.color;
        _displayValueObject.values = instance.getOwnSlotValues(vsd.slot);
        _displayValueObject.instance = instance;
        _displayValueObject.slot = vsd.slot;
        return _displayValueObject;
    }

    private void refreshCachedValues() {
        columnCount = _state.getTotalNumberOfVisibleSlots();
        rowCount = _values.size();
    }

    public void setUnderlyingTable(InstanceTable underlyingTable) {
        _underlyingTable = underlyingTable;
    }

    public void setValues(Collection values) {
        detachListener();
        _values = new ArrayList(values);
        attachListener();
        rowCount = _values.size();
        broadcastChangeAnnouncement();
    }

    public void update(Observable o, Object arg) {
        refreshCachedValues();
    }
}
