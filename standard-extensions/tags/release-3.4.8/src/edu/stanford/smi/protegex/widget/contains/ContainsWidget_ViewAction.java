package edu.stanford.smi.protegex.widget.contains;

import java.awt.event.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidget_ViewAction extends ContainsWidget_AbstractAction {

    private static final long serialVersionUID = -6653118272086189140L;

    public ContainsWidget_ViewAction(ContainsWidget widget) {
        super(widget, (widget.getState()).getViewInstanceButtonTooltip(), Icons.getViewIcon());
        _project = widget.getProject();
    }

    public void actionPerformed(ActionEvent e) {
        Instance selection = _widget.getSelectedInstance();
        if (null != selection) {
            _project.show(selection);
        }
    }
}
