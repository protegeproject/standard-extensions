package edu.stanford.smi.protegex.util;

import edu.stanford.smi.protege.model.*;
import java.awt.*;
import javax.swing.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class FrameRenderer_ListCell extends JLabel implements ListCellRenderer {

    private static final long serialVersionUID = 7396337691367728978L;

    public FrameRenderer_ListCell() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int row, boolean selected, boolean hasFocus) {
        if (selected) {
            setForeground(list.getSelectionForeground());
            setBackground(list.getSelectionBackground());
        } else {
            setForeground(list.getForeground());
            setBackground(list.getBackground());
        }
        if (value == null) {
            setText("");
        } else {
            if (value instanceof Instance) {
                setText(((Instance) value).getBrowserText());
            } else {
                setText(value.toString());
            }
        }
        setEnabled(list.isEnabled());
        return this;
    }
}
