package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class AddButton extends ScatterboxAction {
    private static final long serialVersionUID = 4063798177558405497L;
    private String _dialogTitle;

    public AddButton(
        ScatterboxWidget widget,
        ScatterboxTable table,
        ScatterboxTableModel model,
        KBQueryUtils queryUtilsObject) {
        super(widget, (widget.getState()).getAddInstanceButtonTooltip(), Icons.getAddIcon(), table, model, queryUtilsObject);
        _dialogTitle = (widget.getState()).getAddInstanceDialogTitle();
    }

    protected void performTask(int row, int column) {
        Cls rangeType = _queryUtilsObject.getRangeType(_widget.getCls());
        Instance value =
            DisplayUtilities.pickInstance(
                (Component) _widget,
                CollectionUtilities.createList(rangeType),
                "Choose a value to enter in the table");
        if (null != value) {
            Instance newEntry = _model.createEntry(row, column, value);
            _widget.addEntry(newEntry);
        }
        return;
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
