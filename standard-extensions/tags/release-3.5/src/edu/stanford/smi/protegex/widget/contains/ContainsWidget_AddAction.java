package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidget_AddAction extends ContainsWidget_AbstractAction {
    private static final long serialVersionUID = 4571238097082821522L;
    private String _dialogTitle;

    public ContainsWidget_AddAction(ContainsWidget widget) {
        super(widget, (widget.getState()).getAddInstanceButtonTooltip(), Icons.getAddIcon());
        _dialogTitle = (widget.getState()).getAddInstanceDialogTitle();
    }

    public void actionPerformed(ActionEvent e) {
        Collection choices = DisplayUtilities.pickInstances((Component) _widget, getAllowedClasses(), _dialogTitle);
        _widget.addInstances(choices);
    }

    protected void updateActivation() {
        boolean enabled = _widget.getValues().size() == 0 || _widget.allowsMultipleValues();
        setEnabled(enabled);
    }
}
