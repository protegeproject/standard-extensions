package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ViewButton extends ScatterboxAction {

    private static final long serialVersionUID = -2702244290272808217L;

    public ViewButton(
        ScatterboxWidget widget,
        ScatterboxTable table,
        ScatterboxTableModel model,
        KBQueryUtils queryUtilsObject) {
        super(widget, (widget.getState()).getViewInstanceButtonTooltip(), Icons.getViewIcon(), table, model, queryUtilsObject);
    }

    protected void performTask(int row, int column) {
        Instance entry = (Instance) _model.getValueAt(row, column);
        (_widget.getProject()).show(entry);
    }

    protected void setActivation() {
        if (_widget.isThereASelection()) {
            if (null != _widget.getSelectedEntry()) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
            return;
        }
        setEnabled(false);
        return;
    }
}
