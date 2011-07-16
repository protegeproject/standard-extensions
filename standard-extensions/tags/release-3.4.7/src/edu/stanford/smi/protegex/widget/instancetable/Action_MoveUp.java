package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.event.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Description of the Class
 *
 * @author    Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class Action_MoveUp extends Action_AbstractTableListener {

    private static final long serialVersionUID = 3299485715199010663L;

    public Action_MoveUp(InstanceTableWidget widget, InstanceTable table) {
        super((widget.getState()).getMoveInstanceUpTooltip(), Icons.getUpIcon(), widget, table);
    }

    public void actionPerformed(ActionEvent e) {
        Collection instances = _table.getSelectedInstances();
        if (instances.size() == 1 && isSlotEditable()) {
            Instance instance = (Instance) CollectionUtilities.getFirstItem(instances);
            _widget.moveValue(instance, -1);
        }
    }
}
