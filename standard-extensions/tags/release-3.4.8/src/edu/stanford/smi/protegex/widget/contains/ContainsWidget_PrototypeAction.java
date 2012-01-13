package edu.stanford.smi.protegex.widget.contains;

import java.awt.event.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidget_PrototypeAction extends ContainsWidget_AbstractAction {
    private static final long serialVersionUID = 7517380533925014719L;
    private int _depth = 1;

    public ContainsWidget_PrototypeAction(ContainsWidget widget, int depth) {
        super(widget, (widget.getState()).getPrototypeButtonTooltip(), Icons.getCopyIcon());
        _depth = depth;
    }

    public void actionPerformed(ActionEvent e) {
        Instance selectedInstance = _widget.getSelectedInstance();
        if (null == selectedInstance) {
            return;
        }
        Instance newInstance = createUsingPrototype(selectedInstance, _depth);
        if (_state.isCreateFormForNewInstances()) {
            _project.show(newInstance);
        }
        return;
    }

    private Instance createUsingPrototype(Instance prototypeInstance, int depth) {
        return RecursiveCopy.recursivelyCopyInstance(prototypeInstance, depth);
    }

    protected void updateActivation() {
        super.updateActivation();
        if (!_widget.allowsMultipleValues()) {
            setEnabled(false);
        }
    }
}
