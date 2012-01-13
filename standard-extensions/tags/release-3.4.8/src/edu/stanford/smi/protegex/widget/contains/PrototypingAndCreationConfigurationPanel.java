package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class PrototypingAndCreationConfigurationPanel extends AbstractWidgetConfigurationPanel {
    private static final long serialVersionUID = -776962789147038109L;
    private ContainsWidgetState _containsWidgetState;
    private JCheckBox _displayNewInstanceForms;
    private JCheckBox _selectNewInsertions;
    private JCheckBox _insertAtCurrentSelection;

    private class DisplayNewInstanceFormsCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _containsWidgetState.setCreateFormForNewInstances(_displayNewInstanceForms.isSelected());
        }
    }

    private class NewlyInsertedObjectsAreSelectedCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _containsWidgetState.setSelectNewInsertions(_selectNewInsertions.isSelected());
        }
    }

    private class InsertAtCurrentSelectionPointCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _containsWidgetState.setInsertAtCurrentSelection(_insertAtCurrentSelection.isSelected());
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
                _containsWidgetState.setPrototypeDepth(_depth);
            }
        }
    }

    public PrototypingAndCreationConfigurationPanel(ContainsWidgetState state) {
        super(state);
    }

    private void addRadioButton(ButtonGroup buttonGroup, JPanel panel, String description, int depth) {
        int prototypeDepth = _containsWidgetState.getPrototypeDepth();
        JRadioButton newButton = new JRadioButton(description);
        newButton.addActionListener(new PrototypeButton(depth));
        panel.add(newButton);
        buttonGroup.add(newButton);
        if (depth == prototypeDepth) {
            newButton.setSelected(true);
        }
    }

    private void buildDisplayNewInstanceFormsCheckbox(int yPosition) {
        _displayNewInstanceForms = createCheckBox("Display forms for newly created instances", yPosition);
        _displayNewInstanceForms.setSelected(_containsWidgetState.isCreateFormForNewInstances());
        _displayNewInstanceForms.addActionListener(new DisplayNewInstanceFormsCheckBoxListener());
    }

    protected void buildGUI() {
        _containsWidgetState = (ContainsWidgetState) _state;
        buildDisplayNewInstanceFormsCheckbox(1);
        buildNewlyInsertedObjectsAreSelected(2);
        buildInsertAtCurrentSelectionPoint(3);
        buildPrototypeDepthRadioButton(4);
        addVerticalSpace(5);
    }

    private void buildInsertAtCurrentSelectionPoint(int yPosition) {
        _insertAtCurrentSelection = createCheckBox("Insert at point of current selection", yPosition);
        _insertAtCurrentSelection.setSelected(_containsWidgetState.isInsertAtCurrentSelection());
        _insertAtCurrentSelection.addActionListener(new InsertAtCurrentSelectionPointCheckBoxListener());
    }

    private void buildNewlyInsertedObjectsAreSelected(int yPosition) {
        _selectNewInsertions = createCheckBox("Newly inserted instances are selected", yPosition);
        _selectNewInsertions.setSelected(_containsWidgetState.isSelectNewInsertions());
        _selectNewInsertions.addActionListener(new NewlyInsertedObjectsAreSelectedCheckBoxListener());
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
}
