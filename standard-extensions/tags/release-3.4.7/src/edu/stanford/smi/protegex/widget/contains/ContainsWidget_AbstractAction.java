package edu.stanford.smi.protegex.widget.contains;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class ContainsWidget_AbstractAction extends AbstractAction {
    private static final long serialVersionUID = -718905515021290019L;
    protected ContainsWidget _widget;
    protected Project _project;
    protected KnowledgeBase _kb;
    protected ContainsWidgetState _state;

    public ContainsWidget_AbstractAction(ContainsWidget widget, String tooltipString, Icon icon) {
        super(tooltipString, icon);
        _project = widget.getProject();
        _state = widget.getState();
        _kb = widget.getKnowledgeBase();
        _widget = widget;
        updateActivation();
    }

    public abstract void actionPerformed(ActionEvent e);

    protected Collection getAllowedClasses() {
        Instance instance = _widget.getInstance();
        Slot slot = _widget.getSlot();
        Cls cls = instance.getDirectType();
        return new ArrayList(cls.getTemplateSlotAllowedClses(slot));
    }

    protected void updateActivation() {
        if (_widget.isSelectionEmpty()) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }
}
