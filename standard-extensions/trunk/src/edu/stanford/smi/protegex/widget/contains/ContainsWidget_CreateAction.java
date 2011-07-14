package edu.stanford.smi.protegex.widget.contains;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidget_CreateAction extends ContainsWidget_AbstractAction {
    private static final long serialVersionUID = 5274321661679914430L;
    private String _dialogTitle;

    public ContainsWidget_CreateAction(ContainsWidget widget) {
        this(widget, (widget.getState()).getCreateInstanceButtonTooltip(), Icons.getCreateIcon());
        _dialogTitle = (widget.getState()).getCreateInstanceDialogTitle();
    }

    public ContainsWidget_CreateAction(ContainsWidget widget, String tooltipString, Icon icon) {
        super(widget, tooltipString, icon);
    }

    public void actionPerformed(ActionEvent e) {
        Instance newInstance = _kb.createInstance(null, getClsForInstance());
        _widget.addInstance(newInstance);
        if (_state.isCreateFormForNewInstances()) {
            _project.show(newInstance);
        }
    }

    private Cls getClsForInstance() {
        Collection allowedClasses = getAllowedClasses();
        if (allowedClasses.size() == 0) {
            allowedClasses.add(_kb.getRootCls());
        }
        return DisplayUtilities.pickConcreteCls((JComponent) _widget, _kb, allowedClasses, _dialogTitle);
    }

    protected void updateActivation() {
        boolean enabled = _widget.getValues().size() == 0 || _widget.allowsMultipleValues();
        setEnabled(enabled);
    }
}
