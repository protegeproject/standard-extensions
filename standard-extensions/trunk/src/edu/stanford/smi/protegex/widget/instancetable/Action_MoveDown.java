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
public class Action_MoveDown extends Action_AbstractTableListener {

    private static final long serialVersionUID = -7451398402386747683L;

    public Action_MoveDown(InstanceTableWidget widget, InstanceTable table) {
        super((widget.getState()).getMoveInstanceDownTooltip(), Icons.getDownIcon(), widget, table);
    }

    public void actionPerformed(ActionEvent e) {
        Collection instances = _table.getSelectedInstances();
        if (instances.size() == 1 && isSlotEditable()) {
            Instance instance = (Instance) CollectionUtilities.getFirstItem(instances);
            _widget.moveValue(instance, +1);
        }
    }
}
