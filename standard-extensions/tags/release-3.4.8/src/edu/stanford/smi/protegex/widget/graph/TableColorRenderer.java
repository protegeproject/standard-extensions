package edu.stanford.smi.protegex.widget.graph;

import java.awt.Component;
import java.awt.Color;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class TableColorRenderer extends JLabel implements TableCellRenderer {
    private static final long serialVersionUID = -8385412886537488621L;
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public TableColorRenderer(boolean isBordered) {
        super();
        this.isBordered = isBordered;
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {

    	setBackground((Color)color);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2, 10, 2,
							10, table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2, 10,
							2, 10, table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
        return this;
        
    }

    /*public String getToolTipText() {
        String toolTipText = "Click for color chooser";
        return toolTipText;
    }*/
}