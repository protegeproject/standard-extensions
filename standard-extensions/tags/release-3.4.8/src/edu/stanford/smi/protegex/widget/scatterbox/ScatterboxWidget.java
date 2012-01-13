package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class ScatterboxWidget extends AbstractSlotWidget {
    private static final long serialVersionUID = 3432901280334140433L;
    protected ScatterboxWidgetState _state;
    protected ScatterboxTable _table;
    protected ScatterboxTableModel _model;
    protected Order _horizontalOrder;
    protected Order _verticalOrder;
    protected LabeledComponent _mainComponent;
    protected ArrayList _selectionObservers;
    protected ArrayList _entries;
    protected boolean _isCurrentlyDragging;
    protected int _currentlyDraggingOverRow;
    protected int _currentlyDraggingOverColumn;
    protected KBQueryUtils _queryUtilsObject;
    protected ClsListener _domainClsListener;

    private class DoubleClickPullsUpInstanceFrame extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                Instance entry = getSelectedEntry();
                if ((null == entry) && (_state.isAutocreateWhenEditing())) {
                    Point point = e.getPoint();

                    entry = (Instance) _model.createEntry(_table.rowAtPoint(point), _table.columnAtPoint(point));
                    addEntry(entry);
                }
                getProject().show(entry);
            }
        }
    }

    private class DomainClsListener extends ClsAdapter {
        public void templateFacetValueChanged(ClsEvent p0) {
            buildTableModel();
        }
    }

    public ScatterboxWidget() {
        setPreferredRows(4);
        setPreferredColumns(2);
        _entries = new ArrayList();
        _domainClsListener = new DomainClsListener();
    }

    private void addButtonsToMainComponent() {
        if (_state.isDisplayViewInstanceButton()) {
            _mainComponent.addHeaderButton(new ViewButton(this, _table, _model, _queryUtilsObject));
        }
        if (_state.isDisplayCreateInstanceButton()) {
            _mainComponent.addHeaderButton(new CreateButton(this, _table, _model, _queryUtilsObject));
        }
        if (_state.isDisplayAddInstanceButton()) {
            _mainComponent.addHeaderButton(new AddButton(this, _table, _model, _queryUtilsObject));
        }
        if (_state.isDisplayRemoveInstanceButton()) {
            _mainComponent.addHeaderButton(new RemoveButton(this, _table, _model, _queryUtilsObject));
        }
    }

    public void addEntry(Instance newEntry) {
        replaceEntry(null, newEntry);
    }

    public void addSelectionObserver(ScatterboxWidgetListener observer) {
        _selectionObservers.add(observer);
    }

    private void announceSelection() {
        Iterator i = _selectionObservers.iterator();
        while (i.hasNext()) {
            ((ScatterboxWidgetListener) (i.next())).scatterboxWidgetSelectionChanged();
        }
    }

    private void buildGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        buildUnderlyingTable();
        JScrollPane scrollPane = new JScrollPane(_table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        HorizontalTextPanel topPanel = new HorizontalTextPanel(_horizontalOrder.getName());
        VerticalTextPanel sidePanel = new VerticalTextPanel(_verticalOrder.getName());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanel, BorderLayout.WEST);
        _mainComponent = new LabeledComponent(getLabel(), mainPanel, true);
        addButtonsToMainComponent();
        add(_mainComponent);
        if (isRuntime()) {
            performRunTimeCustomizations();
        } else {
            performDesignTimeCustomizations();
        }
    }

    private Order buildOrderForSlot(Slot slot) {
        Cls domainCls = _queryUtilsObject.getDomainType(getCls());
        if (ValueType.INTEGER == domainCls.getTemplateSlotValueType(slot)) {
            return new Integer_Filling(this, slot, _queryUtilsObject);
        }
        if (ValueType.SYMBOL == domainCls.getTemplateSlotValueType(slot)) {
            return new Symbol_Filling(this, slot, _queryUtilsObject);
        }
        if (ValueType.INSTANCE == domainCls.getTemplateSlotValueType(slot)) {
            return new Instance_Filling(this, slot, _queryUtilsObject);
        }
        if (ValueType.CLS == domainCls.getTemplateSlotValueType(slot)) {
            return new Cls_Filling(this, slot, _queryUtilsObject);
        }
        return null;
    }

    private void buildTableModel() {
        Slot horizontalSlot = _state.getHorizontalSlot();
        Slot verticalSlot = _state.getVerticalSlot();
        _horizontalOrder = buildOrderForSlot(horizontalSlot);
        _horizontalOrder.setName(_state.getHorizontalAxisLabel());
        _verticalOrder = buildOrderForSlot(verticalSlot);
        _verticalOrder.setName(_state.getVerticalAxisLabel());
        _model = new ScatterboxTableModel(this, _queryUtilsObject);
        if (null != _table) {
            _table.setModel(_model);
        }
    }

    private void buildUnderlyingTable() {
        buildTableModel();
        _table = new ScatterboxTable(_model, this, null, true, _queryUtilsObject);
        _model.setUnderlyingTable(_table);
    }

    private void cleanupListeners() {
        (_queryUtilsObject.getDomainType(getCls())).removeClsListener(_domainClsListener);
    }

    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        _state = new ScatterboxWidgetState(this, getPropertyList());
        return new ScatterboxWidgetConfigurationPanel(this);
    }

    public void dispose() {
        super.dispose();
        cleanupListeners();
    }

    public int getCurrentlyDraggingOverColumn() {
        return _currentlyDraggingOverColumn;
    }

    public int getCurrentlyDraggingOverRow() {
        return _currentlyDraggingOverRow;
    }

    public Collection getDomainSlots() {
        Cls entryCls = _queryUtilsObject.getEntryCls(getCls());
        return _queryUtilsObject.getDomainValueSlots(entryCls);
    }

    public Collection getEntries() {
        return new ArrayList(_entries);
    }

    public Order getHorizontalOrder() {
        return _horizontalOrder;
    }

    public KBQueryUtils getKBQueryUtils() {
        return _queryUtilsObject;
    }

    public LabeledComponent getMainComponent() {
        return _mainComponent;
    }

    protected abstract KBQueryUtils getQueryUtilsObject();

    public Slot getRangeSlot() {
        Cls entryCls = _queryUtilsObject.getEntryCls(getCls());
        return (Slot) CollectionUtilities.getFirstItem(_queryUtilsObject.getRangeValueSlots(entryCls));
    }

    public Instance getSelectedEntry() {
        int currentRow = _table.getSelectedRow();
        if (currentRow < 0) {
            return null;
        }
        int currentColumn = _table.getSelectedColumn();
        if (currentColumn < 0) {
            return null;
        }
        return (Instance) _model.getValueAt(currentRow, currentColumn);
    }

    public ScatterboxWidgetState getState() {
        return _state;
    }

    public Collection getValues() {
        return getEntries();
    }

    public Order getVerticalOrder() {
        return _verticalOrder;
    }

    public void initialize() {
        _queryUtilsObject = getQueryUtilsObject();
        (_queryUtilsObject.getDomainType(getCls())).addClsListener(_domainClsListener);
        _state = new ScatterboxWidgetState(this, getPropertyList());
        _selectionObservers = new ArrayList();
        buildGUI();
    }

    public boolean isCurrentlyDragging() {
        return _isCurrentlyDragging;
    }

    public boolean isThereASelection() {
        int currentRow = _table.getSelectedRow();
        if (currentRow < 0) {
            return false;
        }
        int currentColumn = _table.getSelectedColumn();
        if (currentColumn < 0) {
            return false;
        }
        return true;
    }

    private void performDesignTimeCustomizations() {
        _table.setEnabled(false);
        _table.setCellSelectionEnabled(false);
        _table.setColumnSelectionAllowed(false);
        _table.setRowSelectionAllowed(false);
    }

    private void performRunTimeCustomizations() {
        if (!_state.isEditInPlace()) {
            _table.addMouseListener(new DoubleClickPullsUpInstanceFrame());
        } else {
            _table.setEditor(new ScatterboxTableCellEditor(this, _state, _table, getProject(), _queryUtilsObject));
        }
    }

    public void removeEntry(Instance entryToRemove) {
        replaceEntry(entryToRemove, null);
    }

    public void removeSelectionObserver(ScatterboxWidgetListener observer) {
        _selectionObservers.remove(observer);
    }

    public void replaceEntry(Instance entryToRemove, Instance newEntry) {
        if (null != entryToRemove) {
            _entries.remove(entryToRemove);
        }
        if (null != newEntry) {
            if (!_entries.contains(newEntry)) {
                _entries.add(newEntry);
            }
        }
        valueChanged();
        _model.setEntries(new ArrayList(_entries));
    }

    public void selectEntry(Instance instance) {
        int row = _verticalOrder.getIndexForEntry(instance);
        int column = _horizontalOrder.getIndexForEntry(instance);
        _table.setRowSelectionInterval(row, row);
        _table.setColumnSelectionInterval(column, column);
        return;
    }

    public void selectionChanged() {
        announceSelection();
    }

    public void setCurrentlyDraggingOverColumn(int currentlyDraggingOverColumn) {
        _currentlyDraggingOverColumn = currentlyDraggingOverColumn;
    }

    public void setCurrentlyDraggingOverRow(int currentlyDraggingOverRow) {
        _currentlyDraggingOverRow = currentlyDraggingOverRow;
    }

    public void setIsCurrentlyDragging(boolean isCurrentlyDragging) {
        _isCurrentlyDragging = isCurrentlyDragging;
    }

    public void setValues(java.util.Collection values) {
        _table.stopEditing();
        super.setValues(values);
        Instance instance = getInstance();
        Slot entrySlot = getSlot();
        _entries = new ArrayList(instance.getOwnSlotValues(entrySlot));
        _model.setEntries(_entries);
    }
}
