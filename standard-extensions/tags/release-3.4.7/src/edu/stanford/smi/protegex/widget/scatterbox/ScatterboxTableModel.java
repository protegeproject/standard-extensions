package edu.stanford.smi.protegex.widget.scatterbox;

import java.util.*;

import javax.swing.table.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;

/**
 *  Stores, and returns, entries.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 2959699668282175639L;
    private Order _rowOrder;
    private Order _columnOrder;
    private Order _orderOne;
    private Order _orderTwo;
    private HashMap _orderOneToOrderTwoHashMap;
    private ScatterboxWidget _widget;
    private ScatterboxWidgetState _widgetState;
    private Project _project;
    private ArrayList _entriesWeListenTo;
    private HashMap _entriesToDomainObjects;
    private HashMap _entriesToRangeObjects;
    private HashMap _domainObjectsToEntries;
    private HashMap _rangeObjectsToEntries;
    private HashMap _entryToOrderOneValue;
    private HashMap _entryToOrderTwoValue;
    private EntryChangeListener _entryChangeListener;
    private DomainValueObjectChangeListener _domainValueObjectChangeListener;
    private RangeValueObjectChangeListener _rangeValueObjectChangeListener;
    private ScatterboxTable _underlyingTable;
    private KBQueryUtils _queryUtilsObject;

    private class EntryChangeListener extends FrameAdapter {
        public void ownSlotValueChanged(FrameEvent event) {
            Instance source = (Instance) event.getFrame();
            unregisterListeners(source);
            registerListeners(source);
            broadcastChangeAnnouncement();
        }
    }

    private class DomainValueObjectChangeListener extends FrameAdapter {
        public void ownSlotValueChanged(FrameEvent event) {
            Instance entry = (Instance) _domainObjectsToEntries.get(event.getFrame());
            Instance currentWidgetSelection = _widget.getSelectedEntry();
            removeEntry(entry);
            addEntry(entry);
            if (entry == currentWidgetSelection) {
                _widget.selectEntry(entry);
            }
            broadcastChangeAnnouncement();
        }
    }

    private class RangeValueObjectChangeListener extends FrameAdapter {
        public void ownSlotValueChanged(FrameEvent event) {
            Instance entry = (Instance) _rangeObjectsToEntries.get(event.getFrame());
            removeEntry(entry);
            addEntry(entry);
            broadcastChangeAnnouncement();
        }
    }

    public ScatterboxTableModel(ScatterboxWidget widget, KBQueryUtils queryUtilsObject) {
        _queryUtilsObject = queryUtilsObject;
        _entryToOrderOneValue = new HashMap();
        _entryToOrderTwoValue = new HashMap();
        _widget = widget;
        _widgetState = _widget.getState();
        _project = _widget.getProject();
        _orderOne = _rowOrder = _widget.getVerticalOrder();
        _orderTwo = _columnOrder = _widget.getHorizontalOrder();
        _orderOneToOrderTwoHashMap = new HashMap();
        _entryChangeListener = new EntryChangeListener();
        _domainValueObjectChangeListener = new DomainValueObjectChangeListener();
        _rangeValueObjectChangeListener = new RangeValueObjectChangeListener();
        setEntries(_widget.getEntries());
    }

    private void addEntry(Instance entry) {
        Object orderOneValue = _orderOne.getValueForEntry(entry);
        Object orderTwoValue = _orderTwo.getValueForEntry(entry);
        _entryToOrderOneValue.put(entry, orderOneValue);
        _entryToOrderTwoValue.put(entry, orderTwoValue);
        HashMap internalHash = (HashMap) _orderOneToOrderTwoHashMap.get(orderOneValue);
        if (null == internalHash) {
            internalHash = new HashMap();
            _orderOneToOrderTwoHashMap.put(orderOneValue, internalHash);
        }
        internalHash.put(orderTwoValue, entry);
        registerListeners(entry);
    }

    protected void broadcastChangeAnnouncement() {
        if (null != _underlyingTable) {
            _underlyingTable.stopEditing();
        }
        fireTableDataChanged();
    }

    protected void broadcastStructureChangeAnnouncement() {
        if (null != _underlyingTable) {
            _underlyingTable.stopEditing();
        }
        fireTableStructureChanged();
    }

    private void clearListeningState() {
        if (null != _entriesWeListenTo) {
            Iterator i = _entriesWeListenTo.iterator();
            while (i.hasNext()) {
                Instance nextEntry = (Instance) i.next();
                unregisterListeners(nextEntry);
            }
        }
        createListeningHashes();
        return;
    }

    public Instance createEntry(int row, int column) {
        return createEntry(row, column, null);
    }

    public Instance createEntry(int row, int column, Instance rangeValue) {
        Cls functionCls = _widget.getCls();
        KnowledgeBase kb = functionCls.getKnowledgeBase();
        Cls entryCls = _queryUtilsObject.getEntryCls(functionCls);

        Instance newEntry = kb.createInstance(null, entryCls);
        Instance newDomainInstance = _queryUtilsObject.getOrCreateDomainObject(newEntry);
        if (null == rangeValue) {
            rangeValue = _queryUtilsObject.getOrCreateRangeObject(newEntry);
        }
        _queryUtilsObject.setRangeObject(newEntry, rangeValue);

        Order horizontalOrder = _widget.getHorizontalOrder();
        Order verticalOrder = _widget.getVerticalOrder();

        horizontalOrder.fillObjectWithIndexedValue(newDomainInstance, column);
        verticalOrder.fillObjectWithIndexedValue(newDomainInstance, row);
        if (_widgetState.isAutomaticallyDisplayFormsForCreatedInstances()) {
            _project.show(newEntry);
        }
        return newEntry;
    }

    private void createListeningHashes() {
        _entriesWeListenTo = new ArrayList();
        _entriesToDomainObjects = new HashMap();
        _entriesToRangeObjects = new HashMap();
        _domainObjectsToEntries = new HashMap();
        _rangeObjectsToEntries = new HashMap();
    }

    public void dispose() {
        clearListeningState();
    }

    public int getColumnCount() {
        return _columnOrder.getSize();
    }

    public String getColumnName(int columnIndex) {
        Object columnValue = _columnOrder.getValueForIndex(columnIndex);
        if (columnValue instanceof Instance) {
            return ((Instance) columnValue).getBrowserText();
        }
        return columnValue.toString();
    }

    public int getRowCount() {
        return _rowOrder.getSize();
    }

    // actual structure methods
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object rowValue = _rowOrder.getValueForIndex(rowIndex);
        Object columnValue = _columnOrder.getValueForIndex(columnIndex);
        Object returnValue = null;
        if (_orderOne == _rowOrder) {
            HashMap internalHash = (HashMap) _orderOneToOrderTwoHashMap.get(rowValue);
            if (null != internalHash) {
                returnValue = internalHash.get(columnValue);
            }
        } else {
            HashMap internalHash = (HashMap) _orderOneToOrderTwoHashMap.get(columnValue);
            if (null != internalHash) {
                returnValue = internalHash.get(columnValue);
            }
        }
        return returnValue;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private void registerListeners(Instance entry) {
        if (null == _entriesWeListenTo) {
            createListeningHashes();
        }
        _entriesWeListenTo.add(entry);
        Instance domainObject = _queryUtilsObject.getDomainObject(entry);
        Instance rangeObject = _queryUtilsObject.getRangeObject(entry);
        _entriesToDomainObjects.put(entry, domainObject);
        _entriesToRangeObjects.put(entry, rangeObject);
        _domainObjectsToEntries.put(domainObject, entry);
        _rangeObjectsToEntries.put(rangeObject, entry);
        domainObject.addFrameListener(_domainValueObjectChangeListener);
        rangeObject.addFrameListener(_rangeValueObjectChangeListener);
        entry.addFrameListener(_entryChangeListener);
        return;
    }

    private void removeEntry(Instance entry) {
        Object orderOneValue = _entryToOrderOneValue.get(entry);
        Object orderTwoValue = _entryToOrderTwoValue.get(entry);
        HashMap internalHash = (HashMap) _orderOneToOrderTwoHashMap.get(orderOneValue);
        if (null == internalHash) {
            return;
        }
        internalHash.remove(orderTwoValue);
        _entryToOrderOneValue.remove(entry);
        _entryToOrderTwoValue.remove(entry);
        unregisterListeners(entry);
    }

    public void setColumnToOrder(Order order) {
        if (_columnOrder != order) {
            _rowOrder = _columnOrder;
            _columnOrder = order;
            broadcastStructureChangeAnnouncement();
        }
    }

    public void setColumnToOrder(String orderName) {
        if (!(_columnOrder.getName()).equals(orderName)) {
            Order rowOrder = _rowOrder;
            _rowOrder = _columnOrder;
            _columnOrder = rowOrder;
            broadcastStructureChangeAnnouncement();
        }
    }

    public void setEntries(Collection entries) {
        clearListeningState();
        _orderOneToOrderTwoHashMap = new HashMap();
        if (null == entries) {
            return;
        }
        Iterator i = entries.iterator();
        while (i.hasNext()) {
            addEntry((Instance) i.next());
        }
        broadcastChangeAnnouncement();
    }

    public void setRowToOrder(Order order) {
        if (_rowOrder != order) {
            _columnOrder = _rowOrder;
            _rowOrder = order;
            broadcastStructureChangeAnnouncement();
        }
    }

    public void setRowToOrder(String orderName) {
        if (!(_rowOrder.getName()).equals(orderName)) {
            Order columnOrder = _columnOrder;
            _columnOrder = _rowOrder;
            _rowOrder = columnOrder;
            broadcastStructureChangeAnnouncement();
        }
    }

    public void setUnderlyingTable(ScatterboxTable underlyingTable) {
        _underlyingTable = underlyingTable;
    }

    private void unregisterListeners(Instance entry) {
        if ((null == entry) || (null == _entriesWeListenTo)) {
            return;
        }
        Instance domainObject = (Instance) _entriesToDomainObjects.get(entry);
        Instance rangeObject = (Instance) _entriesToRangeObjects.get(entry);
        if (null != domainObject) {
            domainObject.removeFrameListener(_domainValueObjectChangeListener);
        }
        if (null != rangeObject) {
            rangeObject.removeFrameListener(_rangeValueObjectChangeListener);
        }
        entry.removeFrameListener(_entryChangeListener);
        return;
    }
}
