package edu.stanford.smi.protegex.widget.instancetable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import edu.stanford.smi.protege.model.Slot;

/**
 *  Table used in configuration panel to get column width, color, name
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ConfigTable extends JTable {
    private static final long serialVersionUID = -4473085117179805038L;
    private InstanceTableWidgetState _state;

    private class CaptureSlides implements TableColumnModelListener {
        public void columnMarginChanged(ChangeEvent e) {
        }

        public void columnSelectionChanged(ListSelectionEvent e) {
        }

        public void columnRemoved(TableColumnModelEvent e) {
        }

        public void columnAdded(TableColumnModelEvent e) {
            // we need to do this in invoke later because
            // at the time this is called, the index of the tableColumn
            // may not be correct [especially if someone fires
            // a TabsleStructureChange event)
            
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        addWidthChangeListeners();
                    }
                }
            );
        }

        public void columnMoved(TableColumnModelEvent e) {
            final int source = e.getFromIndex();
            final int target = e.getToIndex();
            if (source != target && target > 0) {
                final Object modelValue = getModel().getValueAt(0, source);
                if (modelValue instanceof Slot) {
                    _state.moveSlotToIndex(source -1 , target - 1);
                }
            }
        }
    }

    private class CaptureWidthChanges implements PropertyChangeListener {
        private VisibleSlotDescription _visibleSlotDescription;

        public CaptureWidthChanges(VisibleSlotDescription visibleSlotDescription) {
            _visibleSlotDescription = visibleSlotDescription;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            if ("width".equals(property)) {
                Integer newWidth = (Integer) evt.getNewValue();
                _visibleSlotDescription.preferredSize = newWidth.intValue();
            }
        }
    }

    public ConfigTable(InstanceTableWidgetState state) {
        super(new ConfigTableModel(state));
        _state = state;
        setTableColumnWidths();
        addWidthChangeListeners();
        setDefaultEditor(Object.class, new ConfigTableEditor(this, state));
        setDefaultRenderer(Object.class, new ConfigTableRenderer());
        (getColumnModel()).addColumnModelListener(new CaptureSlides());
    }

    private void addWidthChangeListeners() {
        int counter = 1;
        TableColumnModel tableColumnModel = getColumnModel();
        Iterator i = (_state.getSlotVisibilityDescriptions()).iterator();
        while (i.hasNext() && counter < tableColumnModel.getColumnCount()) {
            VisibleSlotDescription vsd = (VisibleSlotDescription) i.next();
            TableColumn tableColumn = tableColumnModel.getColumn(counter);
            bind(vsd, tableColumn);
            counter++;
        }
    }

    private void bind(VisibleSlotDescription vsd, TableColumn column) {
        column.addPropertyChangeListener(new CaptureWidthChanges(vsd));
    }

    @Override
	public boolean isCellEditable(int row, int column) {
        return true;
    }

    public void setPreferredWidthForColumn(int column, int preferredWidth) {
        TableColumnModel columnModel = getColumnModel();
        if (column < columnModel.getColumnCount()) {
            TableColumn columnObject = columnModel.getColumn(column);
            columnObject.setPreferredWidth(preferredWidth);
        }
    }

    private void setTableColumnWidths() {
        int counter = 1;
        Iterator i = (_state.getSlotVisibilityDescriptions()).iterator();
        while (i.hasNext()) {
            VisibleSlotDescription vsd = (VisibleSlotDescription) i.next();
            setPreferredWidthForColumn(counter, vsd.preferredSize);
            counter++;
        }
    }
}
