package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Visible slot descriptions
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ColumnConfigurationPanel extends JPanel implements Validatable {
    private static final long serialVersionUID = 4398950154292954702L;
    private ConfigTable _underlyingTable;
    private InstanceTableWidgetState _state;
    private AddSlot _slotAdder;
    private RemoveSlot _slotRemover;

    private class AddSlot extends AbstractAction {
        private static final long serialVersionUID = 4388173307102002986L;

        public AddSlot() {
            super("Add one or more slots to the list of columns", Icons.getAddIcon());
        }

        public void actionPerformed(ActionEvent e) {
            Collection possibleSlots = _state.getRemainingSlots();
            if (!possibleSlots.isEmpty()) {
                Collection selection = DisplayUtilities.pickSlots(ColumnConfigurationPanel.this, possibleSlots);
                if (null != selection) {
                    Iterator i = selection.iterator();
                    while (i.hasNext()) {
                        _state.makeSlotVisible((Slot) i.next());
                    }
                }
            }
            setButtonState();
        }
    }

    private class RemoveSlot extends AbstractAction {
        private static final long serialVersionUID = 6596049371835880091L;

        public RemoveSlot() {
            super("Remove the currently selected column", Icons.getRemoveIcon());
        }

        public void actionPerformed(ActionEvent e) {
            int[] columnsToRemove = _underlyingTable.getSelectedColumns();
            if (_underlyingTable.isEditing()) {
                (_underlyingTable.getDefaultEditor(Object.class)).stopCellEditing();
            }
            _underlyingTable.clearSelection();
            int loopCounter;
            for (loopCounter = columnsToRemove.length - 1; loopCounter > -1; loopCounter--) {
                _state.removeVisibleSlotDescriptionAtIndex(columnsToRemove[loopCounter] - 1);
            }
            setButtonState();
            return;
        }
    }

    public ColumnConfigurationPanel(InstanceTableWidgetState state) {
        super(new BorderLayout());
        _state = state;
        _underlyingTable = new ConfigTable(_state);
        LabeledComponent center = new LabeledComponent("Selected Slots", new JScrollPane(_underlyingTable));
        _slotAdder = new AddSlot();
        _slotRemover = new RemoveSlot();
        setButtonState();
        center.addHeaderButton(_slotAdder);
        center.addHeaderButton(_slotRemover);
        add(center, BorderLayout.CENTER);
    }

    public void saveContents() {
        _state.save();
    }

    private void setButtonState() {
        Collection possibleSlots = _state.getRemainingSlots();
        int numberOfColumns = _state.getTotalNumberOfVisibleSlots();
        if (possibleSlots.isEmpty()) {
            _slotAdder.setEnabled(false);
        } else {
            _slotAdder.setEnabled(true);
        }
        if (0 == numberOfColumns) {
            _slotRemover.setEnabled(false);
        } else {
            _slotRemover.setEnabled(true);
        }
    }

    public boolean validateContents() {
        return true;
    }
}
