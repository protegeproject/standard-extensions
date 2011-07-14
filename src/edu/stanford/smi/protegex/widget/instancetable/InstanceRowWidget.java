package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InstanceRowWidget extends InstanceTableWidget {

    private static final long serialVersionUID = 3954972619518577290L;

    public InstanceRowWidget() {
        setPreferredRows(1);
        setPreferredColumns(2);
    }

    protected void addActionButtonsToComponent(LabeledComponent centerPiece) {
        if (_state.isDisplayViewInstanceButton()) {
            centerPiece.addHeaderButton(new Action_ViewInstance(this, _displayTable));
        }
        if (_state.isDisplayCreateInstanceButton()) {
            centerPiece.addHeaderButton(new Action_CreateInstance(this));
        }
        if (_state.isDisplayAddInstanceButton()) {
            centerPiece.addHeaderButton(new Action_AddInstance(this));
        }
        if (_state.isDisplayRemoveInstanceButton()) {
            centerPiece.addHeaderButton(new Action_RemoveInstance(this, _displayTable));
        }
        if (_state.isDisplayDeleteInstanceButton()) {
            centerPiece.addHeaderButton(new Action_DeleteInstance(this, _displayTable));
        }
    }

    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        // hack to make sure our state is consistent with the plist.
        _state = new InstanceTableWidgetState(getPropertyList(), getAllowedClses(), getKnowledgeBase());
        return new InstanceRowConfigurationPanel(this);
    }

    public void initialize() {
        buildTableComponents();
        setTableColumnWidths();
        LabeledComponent centerPiece = new LabeledComponent(getLabel(), ComponentFactory.createScrollPane(_displayTable));
        addActionButtonsToComponent(centerPiece);
        JComponent warnings = InstanceRowConfigurationChecks.getShortWarning(getCls(), getSlot());
        if (null != warnings) {
            JPanel panelWithWarning = new JPanel(new BorderLayout());
            panelWithWarning.add(centerPiece, BorderLayout.CENTER);
            panelWithWarning.add(warnings, BorderLayout.SOUTH);
            add(panelWithWarning);
        } else {
            add(centerPiece);
        }
        if (!isRuntime()) {
            (_displayTable.getTableHeader()).setReorderingAllowed(false);
        }
        return;
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return InstanceRowConfigurationChecks.checkValidity(cls, slot);
    }

    public static void main(String[] args) {
        edu.stanford.smi.protege.Application.main(args);
    }
}
