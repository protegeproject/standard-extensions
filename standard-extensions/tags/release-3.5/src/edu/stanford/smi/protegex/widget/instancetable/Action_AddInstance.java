package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Action_AddInstance extends AbstractAction {
    private static final long serialVersionUID = -1976697794441539166L;
    private String _dialogTitle;
    private InstanceTableWidget _widget;

    public Action_AddInstance(InstanceTableWidget widget) {
        super((widget.getState()).getAddInstanceButtonTooltip(), Icons.getAddIcon());
        _widget = widget;
        _dialogTitle = (widget.getState()).getAddInstanceDialogTitle();
    }

    public void actionPerformed(ActionEvent e) {
        if (_widget.isEditable()) {
            Collection selections =
                DisplayUtilities.pickInstances((Component) _widget, _widget.getAllowedClses(), _dialogTitle);
            if ((null != selections) && (selections.size() != 0)) {
                _widget.addValues(selections);
            }
        }
    }
}
