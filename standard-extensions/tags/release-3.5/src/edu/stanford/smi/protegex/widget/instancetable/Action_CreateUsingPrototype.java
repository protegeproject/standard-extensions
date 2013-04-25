package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.event.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Action_CreateUsingPrototype extends Action_AbstractTableListener {

    private static final long serialVersionUID = -4297575243479509311L;

    public Action_CreateUsingPrototype(InstanceTableWidget widget, InstanceTable table) {
        super((widget.getState()).getPrototypeButtonTooltip(), Icons.getCopyIcon(), widget, table);
    }

    public void actionPerformed(ActionEvent e) {
        if (isSlotEditable()) {
            Collection instances = _table.getSelectedInstances();
            Iterator i = instances.iterator();
            int depth = (_widget.getState()).getPrototypeDepth();
            ArrayList newInstances = new ArrayList();
            Instance lastInstance = null;
            while (i.hasNext()) {
                lastInstance = createUsingPrototype((Instance) i.next(), depth);
                newInstances.add(lastInstance);
            }
            _widget.addValues(newInstances);
            if (null != lastInstance) {
                _widget.selectInstanceIfAppropriate(lastInstance);
            }
            if ((_widget.getState()).isCreateFormForNewInstances()) {
                showInstances(newInstances);
            }
        }
    }

    private Instance createUsingPrototype(Instance prototypeInstance, int depth) {
        return RecursiveCopy.recursivelyCopyInstance(prototypeInstance, depth);
    }

    private void showInstances(Collection instances) {
        Project project = _widget.getProject();
        Iterator i = instances.iterator();
        while (i.hasNext()) {
            project.show((Instance) i.next());
        }
        return;
    }
}
