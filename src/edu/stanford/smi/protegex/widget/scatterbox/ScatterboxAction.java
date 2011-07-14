package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class ScatterboxAction extends AbstractAction implements ScatterboxWidgetListener {
    private static final long serialVersionUID = 2122902219757205850L;
    protected ScatterboxWidget _widget;
    protected Project _project;
    protected KnowledgeBase _kb;
    protected ScatterboxWidgetState _state;
    protected ScatterboxTable _table;
    protected ScatterboxTableModel _model;
    protected KBQueryUtils _queryUtilsObject;

    private class OurTableModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            setActivation();
        }
    }

    public ScatterboxAction(
        ScatterboxWidget widget,
        String tooltipString,
        Icon icon,
        ScatterboxTable table,
        ScatterboxTableModel model,
        KBQueryUtils queryUtilsObject) {
        super(tooltipString, icon);
        _queryUtilsObject = queryUtilsObject;
        _table = table;
        _model = model;
        _project = widget.getProject();
        _state = widget.getState();
        _kb = widget.getKnowledgeBase();
        _widget = widget;
        _widget.addSelectionObserver(this);
        _model.addTableModelListener(new OurTableModelListener());
        setActivation();
    }

    public void actionPerformed(ActionEvent e) {
        int selectedRow = _table.getSelectedRow();
        int selectedColumn = _table.getSelectedColumn();
        if ((selectedRow < 0) || (selectedColumn < 0)) {
            return;
        }
        performTask(selectedRow, selectedColumn);
        _table.repaint();
    }

    protected abstract void performTask(int row, int column);

    public void scatterboxWidgetSelectionChanged() {
        setActivation();
    }

    protected abstract void setActivation();
}
