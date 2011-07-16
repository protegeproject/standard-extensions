package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *  An action which is enabled if there is a selection and is not enabled
 *  otherwise
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class Action_AbstractTableListener extends AbstractAction {
    private static final long serialVersionUID = -1796739862338195073L;
    protected InstanceTableWidget _widget;
    protected InstanceTable _table;

    private class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            setEnabledBySelection();
        }
    }

    public Action_AbstractTableListener(String tooltipString, Icon icon, InstanceTableWidget widget, InstanceTable table) {
        super(tooltipString, icon);
        _widget = widget;
        _table = table;
        (_table.getSelectionModel()).addListSelectionListener(new TableSelectionListener());
        setEnabledBySelection();
    }

    public abstract void actionPerformed(ActionEvent e);

    protected void setEnabledBySelection() {
        if (_table.getSelectedRow() > -1) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    protected boolean isSlotEditable() {
        return _widget.isEditable();
    }
}
