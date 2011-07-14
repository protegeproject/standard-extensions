package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ConfigTableRenderer extends JTextField implements TableCellRenderer {
    private static final long serialVersionUID = -4212694010918756148L;
    private ColorPanel _colorPanel;

    public ConfigTableRenderer() {
        setOpaque(true);
        _colorPanel = new ColorPanel(Color.white);
    }

    private Component configureForSlot(Slot slot) {
        setBackground(Color.white);
        setText(slot.getBrowserText());
        return this;
    }

    private Component configureForString(String text, int column) {
        if (0 == column) {
            setBackground(Color.lightGray);
        } else {
            setBackground(Color.white);
        }
        setText(text);
        return this;
    }

    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
        if (value instanceof Color) {
            _colorPanel.setColor((Color) value);
            return _colorPanel;
        }
        if (value instanceof Slot) {
            return configureForSlot((Slot) value);
        }
        if (value instanceof String) {
            return configureForString((String) value, column);
        }
        // Should never get here. But, if we do, it will be obvious
        setBackground(Color.red);
        setText("Daughter of Ugly Widget");
        return this;
    }
}
