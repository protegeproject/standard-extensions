package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxTableCellEditor extends AbstractTableWidgetCellEditor {
    private AbstractTableWidgetValue _abstractTableWidgetValue;
    private ScatterboxWidget _widget;
    private ScatterboxWidgetState _widgetState;
    private ScatterboxTableModel _model;
    private KBQueryUtils _queryUtilsObject;

    public ScatterboxTableCellEditor(
        ScatterboxWidget widget,
        ScatterboxWidgetState widgetState,
        JTable underlyingTable,
        Project project,
        KBQueryUtils queryUtilsObject) {
        super(underlyingTable, widgetState, project);
        _queryUtilsObject = queryUtilsObject;
        _model = (ScatterboxTableModel) underlyingTable.getModel();
        _widget = widget;
        _widgetState = widgetState;
        _abstractTableWidgetValue = new AbstractTableWidgetValue();
        _abstractTableWidgetValue.color = Color.black;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (null == value) {
            value = _model.createEntry(row, column);
            _widget.addEntry((Instance) value);
        }
        Instance entryInstance = (Instance) value;
        Instance rangeInstance = _queryUtilsObject.getRangeObject(entryInstance);
        _abstractTableWidgetValue.instance = rangeInstance;
        _abstractTableWidgetValue.slot = _queryUtilsObject.getRangeValueSlot(entryInstance);
        _abstractTableWidgetValue.values = rangeInstance.getOwnSlotValues(_abstractTableWidgetValue.slot);
        return super.getTableCellEditorComponent(table, _abstractTableWidgetValue, isSelected, row, column);
    }
}
