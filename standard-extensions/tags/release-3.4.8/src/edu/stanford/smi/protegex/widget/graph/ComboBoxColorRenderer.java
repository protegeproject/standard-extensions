package edu.stanford.smi.protegex.widget.graph;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

public class ComboBoxColorRenderer extends JLabel implements ListCellRenderer {
    private static final long serialVersionUID = 885811465600454837L;
    private ColorIcon icon = new ColorIcon();
    private Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);

    public ComboBoxColorRenderer() {
        setOpaque(true);
        setIcon(icon);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        icon.setColor((Color) value);
        setIcon(icon);
        if(isSelected) {
            setForeground(list.getSelectionForeground());
            setBackground(list.getSelectionBackground());
        } else {
            setForeground(list.getForeground());
            setBackground(list.getBackground());
            setBorder(emptyBorder);
        }
        return this;
    }
}