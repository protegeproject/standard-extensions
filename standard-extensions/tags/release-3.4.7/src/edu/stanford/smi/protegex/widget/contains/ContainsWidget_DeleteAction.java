package edu.stanford.smi.protegex.widget.contains;

import java.awt.event.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidget_DeleteAction extends ContainsWidget_AbstractAction {
    private static final long serialVersionUID = 2067682992509572087L;
    private KnowledgeBase _kb;

    public ContainsWidget_DeleteAction(ContainsWidget widget) {
        super(widget, (widget.getState()).getDeleteInstanceButtonTooltip(), Icons.getDeleteIcon());
        _kb = widget.getKnowledgeBase();
    }

    public void actionPerformed(ActionEvent e) {
        Instance selection = _widget.getSelectedInstance();
        if (null != selection) {
            _widget.removeInstance(selection);
            _kb.deleteFrame(selection);
        }
    }
}
