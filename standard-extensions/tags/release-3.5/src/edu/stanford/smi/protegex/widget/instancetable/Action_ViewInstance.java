package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.event.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Action_ViewInstance extends Action_AbstractTableListener {

    private static final long serialVersionUID = 7413170893236775224L;

    public Action_ViewInstance(InstanceTableWidget widget, InstanceTable table) {
        super((widget.getState()).getViewInstanceButtonTooltip(), Icons.getViewIcon(), widget, table);
    }

    public void actionPerformed(ActionEvent e) {
        Project project = _widget.getProject();
        Collection instances = _table.getSelectedInstances();
        Iterator i = instances.iterator();
        while (i.hasNext()) {
            project.show((Instance) i.next());
        }
        return;
    }
}
