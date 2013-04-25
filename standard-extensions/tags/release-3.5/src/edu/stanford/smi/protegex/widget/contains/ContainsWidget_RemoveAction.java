package edu.stanford.smi.protegex.widget.contains;

import java.awt.event.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidget_RemoveAction extends ContainsWidget_AbstractAction {

    private static final long serialVersionUID = 8737706740969152578L;

    public ContainsWidget_RemoveAction(ContainsWidget widget) {
        super(widget, (widget.getState()).getRemoveInstanceButtonTooltip(), Icons.getRemoveIcon());
    }

    public void actionPerformed(ActionEvent e) {
        Instance selection = _widget.getSelectedInstance();
        if (null != selection) {
            _widget.removeInstance(selection);
        }
    }
}
