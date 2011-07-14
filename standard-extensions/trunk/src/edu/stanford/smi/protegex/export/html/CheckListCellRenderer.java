package edu.stanford.smi.protegex.export.html;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class CheckListCellRenderer extends JCheckBox
    implements ListCellRenderer {

    private static final long serialVersionUID = -6763584042778171645L;
    private Border border = new EmptyBorder(1, 1, 1, 1);

    public CheckListCellRenderer() {
        super();
        setOpaque(true);
        setBorder(border);
    }

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {

        setText(value.toString());
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());

        FrameData fdata = (FrameData) value;
        setSelected(fdata.isSelected());

        return this;
    }
}