package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;
import java.awt.dnd.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxTable extends AbstractTable {
    private static final long serialVersionUID = -4998649260240980508L;
    private ScatterboxWidget _widget;
    private ScatterboxWidgetState _state;
    private RowHeader _rowHeader;
    protected ScatterboxTableModel _scatterboxTableModel;
    private KBQueryUtils _queryUtilsObject;

    /*
     * private class PrivateTableHeader extends JTableHeader
     * {
     * protected TableCellRenderer createDefaultRenderer()
     * {
     * return super.createDefaultRenderer();
     * }
     * }
     */

    public ScatterboxTable(
        ScatterboxTableModel model,
        ScatterboxWidget widget,
        ScatterboxTableCellEditor editor,
        boolean enableDragging,
        KBQueryUtils queryUtilsObject) {
        super(model);
        _queryUtilsObject = queryUtilsObject;
        _widget = widget;
        _state = _widget.getState();
        setDefaultRenderer(Object.class, new ScatterboxTableCellRenderer(_widget, Color.blue, _queryUtilsObject));
        setModel(model);
        getTableHeader().setReorderingAllowed(false);
        setCellSelectionEnabled(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (enableDragging) {
            setupDragDropSystem();
        }
    }

    public ScatterboxTable(
        ScatterboxTableModel model,
        ScatterboxWidget widget,
        boolean enableDragging,
        KBQueryUtils queryUtilsObject) {
        this(model, widget, null, enableDragging, queryUtilsObject);
    }

    protected void configureEnclosingScrollPane() {
        Container p = getParent();
        if (!(p instanceof JViewport)) {
            return;
        }
        Container gp = p.getParent();
        if (!(gp instanceof JScrollPane)) {
            return;
        }
        JScrollPane scrollPane = (JScrollPane) gp;
        JViewport viewport = scrollPane.getViewport();
        if (viewport == null || viewport.getView() != this) {
            return;
        }
        scrollPane.setColumnHeaderView(getTableHeader());
        // scrollPane.getViewport().setBackingStoreEnabled(true);
        configureRowHeader();
        scrollPane.setRowHeaderView(_rowHeader);
        scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
        super.configureEnclosingScrollPane();
    }

    private void configureRowHeader() {
        _rowHeader = new RowHeader(_widget.getVerticalOrder());
        _rowHeader.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        /*
         * Once we're using 1.3, we should reinsert the next two lines
         * (and the associated private class definition). Basically, they
         * make the row headers, look the same as the column headers.
         * This is a good thing, but the way we did it in 1.2 broke in 1.3
         * (and the way we do it in 1.3 relies on a method that doesn't
         * exist in 1.2-- hence the commenting out
         * PrivateTableHeader hack = new PrivateTableHeader();
         * _rowHeader.setDefaultRenderer(Object.class, hack.createDefaultRenderer());
         */
        TableColumnModel tcM = getColumnModel();
        if (0 == tcM.getColumnCount()) {
            return;
        }
        TableColumn column = tcM.getColumn(0);
        if (null != column) {
            _rowHeader.setPreferredScrollableViewportSize(
                new Dimension(column.getPreferredWidth() + tcM.getColumnMargin(), 0));
        }
    }

    public boolean isCellEditable(int row, int column) {
        if (null == _editor) {
            return false;
        }
        if ((null == _scatterboxTableModel.getValueAt(row, column)) && (!_state.isAutocreateWhenEditing())) {
            return false;
        }
        return true;
    }

    private void resizeComponent(LabeledComponent component, int adjustment) {
        LabeledComponent mainComponent = _widget.getMainComponent();
        Component interiorComponent = mainComponent.getCenterComponent();
        Dimension dprime = new Dimension(interiorComponent.getSize());
        interiorComponent.setSize(new Dimension(dprime.width + adjustment, dprime.height + adjustment));
        interiorComponent.doLayout();
        repaint();
    }

    public void setEditor(AbstractTableWidgetCellEditor editor) {
        _editor = editor;
        if (_editor != null) {
            setRowSelectionAllowed(false);
            setColumnSelectionAllowed(false);
            setCellSelectionEnabled(true);
            setDefaultEditor(Object.class, editor);
        } else {
            setRowSelectionAllowed(true);
            setColumnSelectionAllowed(false);
        }
    }

    public void setModel(TableModel model) {
        if (!(model instanceof ScatterboxTableModel)) {
            throw new Error("Attempt to give ScatterboxTable a model of type " + model.getClass());
        }
        if (null != _rowHeader) {
            _rowHeader.setModel(new RowHeaderTableModel(_widget.getVerticalOrder()));
        }
        _scatterboxTableModel = (ScatterboxTableModel) model;
        super.setModel(_scatterboxTableModel);
        if (null != _widget) {
            final LabeledComponent mainComponent = _widget.getMainComponent();
            if (null != mainComponent) {
                resizeComponent(mainComponent, -1);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        resizeComponent(mainComponent, 1);
                    }
                });
            }
        }
    }

    private void setupDragDropSystem() {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
            this,
            DnDConstants.ACTION_COPY_OR_MOVE,
            new ScatterboxTableDragGestureListener(_widget, this, _queryUtilsObject));
        new DropTarget(
            this,
            DnDConstants.ACTION_COPY_OR_MOVE,
            new ScatterboxTableDropTargetListener(_widget, this, _queryUtilsObject));
    }

    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        if (null != _widget) {
            _widget.selectionChanged();
        }
    }
}
