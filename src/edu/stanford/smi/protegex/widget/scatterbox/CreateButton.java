package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class CreateButton extends ScatterboxAction {
    private static final long serialVersionUID = -6759963854059203158L;
    private String _dialogTitle;
    private ScatterboxTableModel _model;

    public CreateButton(
        ScatterboxWidget widget,
        ScatterboxTable table,
        ScatterboxTableModel model,
        KBQueryUtils queryUtilsObject) {
        super(
            widget,
            (widget.getState()).getCreateInstanceButtonTooltip(),
            Icons.getCreateIcon(),
            table,
            model,
            queryUtilsObject);
        _model = model;
        _dialogTitle = (widget.getState()).getAddInstanceDialogTitle();
    }

    protected void performTask(int row, int column) {
        Instance newEntry = _model.createEntry(row, column);
        Instance entryToRemove = (Instance) _model.getValueAt(row, column);
        _widget.replaceEntry(entryToRemove, newEntry);
    }

    protected void setActivation() {
        if (_widget.isThereASelection()) {
            if (null != _widget.getSelectedEntry()) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
            return;
        }
        setEnabled(false);
        return;
    }
}
