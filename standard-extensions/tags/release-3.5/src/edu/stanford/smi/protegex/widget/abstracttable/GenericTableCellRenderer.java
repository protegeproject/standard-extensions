package edu.stanford.smi.protegex.widget.abstracttable;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import edu.stanford.smi.protege.model.Frame;

/**
 *  Renderer for everything Note: it uses AbstractTableWidgetValue, but only
 *  really uses the color and value slots.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class GenericTableCellRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 4062004666892839353L;

    public GenericTableCellRenderer(boolean highlightSelectedRow) {
        super();
        setBorder(null);
        setOpaque(true);
    }

    private static Color getHighlightBackgroundColor() {
        return UIManager.getColor("Tree.selectionBackground");
    }

    private static Color getHighlightForegroundColor() {
        return UIManager.getColor("Tree.selectionForeground");
    }
    private static Color getNormalBackgroundColor() {
        return UIManager.getColor("Tree.textBackground");
    }

    private String buildString(AbstractTableWidgetValue value) {
        String returnValue = null;
        if (value.values == null) {
            return null;
        }
        Iterator i = (value.values).iterator();
        while (i.hasNext()) {
            Object nextValue = i.next();
            if (null == returnValue) {
                returnValue = getStringForObject(nextValue);
            } else {
                returnValue += ", " + getStringForObject(nextValue);
            }
        }
        return returnValue;
    }

    private String getStringForObject(Object value) {
        String firstPass;
        if (value instanceof Frame) {
            firstPass = ((Frame) value).getBrowserText();
        } else {
            firstPass = value.toString();
        }
        return modifyToHandleCarriageReturns(firstPass);
    }

    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
        if ((hasFocus) && (table.isEditing())) {
            table.editingCanceled(new ChangeEvent(this));
        }
        AbstractTableWidgetValue itv = (AbstractTableWidgetValue) (value);
        isSelected = isSelected || row == table.getSelectedRow();
        if (isSelected) {
            setForeground(getHighlightForegroundColor());
            setBackground(getHighlightBackgroundColor());
        } else {
            setForeground(itv.color);
            setBackground(getNormalBackgroundColor());
        }
        setText(buildString(itv));
        return this;
    }

    private String modifyToHandleCarriageReturns(String string) {
        int index = string.indexOf('\n');
        if (index < 0) {
            return string;
        }
        return string.substring(0, index) + "...";
    }
}
