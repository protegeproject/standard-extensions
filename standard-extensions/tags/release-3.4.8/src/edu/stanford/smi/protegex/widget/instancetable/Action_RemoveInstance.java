package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.event.*;

import edu.stanford.smi.protege.resource.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Action_RemoveInstance extends Action_AbstractTableListener {

    private static final long serialVersionUID = 9178660748954395973L;

    public Action_RemoveInstance(InstanceTableWidget widget, InstanceTable table) {
        super((widget.getState()).getRemoveInstanceButtonTooltip(), Icons.getRemoveIcon(), widget, table);
    }

    public void actionPerformed(ActionEvent e) {
        if (isSlotEditable()) {
            _widget.removeValues(_table.getSelectedInstances());
        }
    }
}
