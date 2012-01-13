package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class NewInstanceConfigurationPanel extends AbstractTableWidgetConfigurationSubPanel {

    private static final long serialVersionUID = 4833874581581708715L;

    private class SelectNewlyCreatedOrAddedInstanceCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ((InstanceTableWidgetState) _state).setAutoSelectInsertions(((JCheckBox) e.getSource()).isSelected());
        }
    }

    private class HighlightSelectedRowCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ((InstanceTableWidgetState) _state).setHighlightSelectedRow(((JCheckBox) e.getSource()).isSelected());
        }
    }

    private class DisplayNewInstanceFormsCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ((InstanceTableWidgetState) _state).setCreateFormForNewInstances(((JCheckBox) e.getSource()).isSelected());
        }
    }

    private class PrototypeButton implements ActionListener {
        private int _depth;

        private PrototypeButton(int depth) {
            _depth = depth;
        }

        public void actionPerformed(ActionEvent e) {
            JRadioButton caller = (JRadioButton) e.getSource();
            if (caller.isSelected()) {
                ((InstanceTableWidgetState) _state).setPrototypeDepth(_depth);
            }
        }
    }

    public NewInstanceConfigurationPanel(InstanceTableWidgetState state) {
        super(state);
        buildGUI();
    }

    private void addRadioButton(ButtonGroup buttonGroup, JPanel panel, String description, int depth) {
        int prototypeDepth = ((InstanceTableWidgetState) _state).getPrototypeDepth();
        JRadioButton newButton = new JRadioButton(description);
        newButton.addActionListener(new PrototypeButton(depth));
        panel.add(newButton);
        buttonGroup.add(newButton);
        if (depth == prototypeDepth) {
            newButton.setSelected(true);
        }
    }

    private void buildDisplayNewInstanceFormsCheckbox(int yPosition) {
        JCheckBox displayNewInstanceForms;
        displayNewInstanceForms = createCheckBox("Display forms for newly created instances", yPosition);
        displayNewInstanceForms.setSelected(((InstanceTableWidgetState) _state).isCreateFormForNewInstances());
        displayNewInstanceForms.addActionListener(new DisplayNewInstanceFormsCheckBoxListener());
    }

    protected void buildGUI() {
        buildDisplayNewInstanceFormsCheckbox(0);
        buildSelectNewlyCreatedOrAddedInstanceCheckBox(1);
        buildHighlightSelectedRowCheckbox(2);
        buildPrototypeDepthRadioButton(3);
        addVerticalSpace(4);
    }

    private void buildHighlightSelectedRowCheckbox(int yPosition) {
        JCheckBox highlightSelectedRows;
        highlightSelectedRows = createCheckBox("Always highlight selected row", yPosition);
        highlightSelectedRows.setSelected(((InstanceTableWidgetState) _state).isHighlightSelectedRow());
        highlightSelectedRows.addActionListener(new HighlightSelectedRowCheckBoxListener());
    }

    private void buildPrototypeDepthRadioButton(int yPosition) {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        ButtonGroup buttonGroup = new ButtonGroup();
        addRadioButton(buttonGroup, buttonPanel, "Shallow Copy", 0);
        addRadioButton(buttonGroup, buttonPanel, "Depth 1 Copy", 1);
        addRadioButton(buttonGroup, buttonPanel, "Deep Copy", -1);
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        LabeledComponent componentToAdd = new LabeledComponent("Prototype Depth", buttonPanel);
        add(componentToAdd, buildComponentGridBagConstraints(yPosition));
    }

    private void buildSelectNewlyCreatedOrAddedInstanceCheckBox(int yPosition) {
        JCheckBox selectNewlyCreatedOrAddedInstanceCheckBox;
        selectNewlyCreatedOrAddedInstanceCheckBox =
            createCheckBox("Automatically select newly created or added rows", yPosition);
        selectNewlyCreatedOrAddedInstanceCheckBox.setSelected(((InstanceTableWidgetState) _state).isAutoSelectInsertions());
        selectNewlyCreatedOrAddedInstanceCheckBox.addActionListener(new SelectNewlyCreatedOrAddedInstanceCheckBoxListener());
    }

    public void saveContents() {
        ((InstanceTableWidgetState) _state).save();
    }

    public boolean validateContents() {
        return true;
    }
}
