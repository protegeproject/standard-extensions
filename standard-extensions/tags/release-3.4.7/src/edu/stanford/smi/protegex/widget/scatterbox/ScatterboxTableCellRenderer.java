package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxTableCellRenderer extends GenericTableCellRenderer implements Constants {
    private static final long serialVersionUID = 4411051146754958894L;
    private AbstractTableWidgetValue _abstractTableWidgetValue;
    private ScatterboxWidget _widget;
    private ScatterboxWidgetState _widgetState;
    private JPanel _nullPanel;
    private JPanel _selectedNullPanel;
    private Border _draggingBorder;
    private KBQueryUtils _queryUtilsObject;

    public ScatterboxTableCellRenderer(ScatterboxWidget widget, Color draggingBoxColor, KBQueryUtils queryUtilsObject) {
        super(false);
        _queryUtilsObject = queryUtilsObject;
        _widget = widget;
        _widgetState = _widget.getState();
        _abstractTableWidgetValue = new AbstractTableWidgetValue();
        _abstractTableWidgetValue.color = Color.black;
        _nullPanel = new JPanel();
        _nullPanel.setOpaque(true);
        _selectedNullPanel = new JPanel();
        _selectedNullPanel.setOpaque(true);
        _selectedNullPanel.setBackground(NULL_SELECTED_COLOR);
        _draggingBorder = BorderFactory.createLineBorder(draggingBoxColor);
    }

    private void customizeComponentForDagging(JComponent component, int row, int column) {
        if ((_widget.isCurrentlyDragging())
            && (row == _widget.getCurrentlyDraggingOverRow())
            && (column == _widget.getCurrentlyDraggingOverColumn())) {
            component.setBorder(_draggingBorder);
        } else {
            component.setBorder(null);
        }
        return;
    }

    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
        JComponent returnValue;
        if (null == value) {
            if ((hasFocus) && (table.isEditing())) {
                table.editingCanceled(new ChangeEvent(this));
            }
            if (isSelected) {
                returnValue = _selectedNullPanel;
            } else {
                returnValue = _nullPanel;
            }
            customizeComponentForDagging(returnValue, row, column);
        } else {
            Instance entryInstance = (Instance) value;
            Instance rangeInstance = _queryUtilsObject.getRangeObject(entryInstance);
            _abstractTableWidgetValue.instance = rangeInstance;
            _abstractTableWidgetValue.slot = _queryUtilsObject.getRangeValueSlot(entryInstance);
            _abstractTableWidgetValue.values = rangeInstance.getOwnSlotValues(_abstractTableWidgetValue.slot);
            returnValue =
                (JComponent) super.getTableCellRendererComponent(table, _abstractTableWidgetValue, isSelected, hasFocus, row, column);
        }
        return returnValue;
    }
}
