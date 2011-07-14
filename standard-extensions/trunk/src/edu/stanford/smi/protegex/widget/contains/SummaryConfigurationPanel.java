package edu.stanford.smi.protegex.widget.contains;

import java.awt.event.*;

import javax.swing.*;

import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SummaryConfigurationPanel extends AbstractWidgetConfigurationPanel {
    private static final long serialVersionUID = 8964526206725058142L;
    private ContainsWidgetState _containsWidgetState;
    private JCheckBox _displaySummaryPanel;
    private JCheckBox _displayTotalNumberOfSubordinateInstances;
    private JCheckBox _displayBrowserKeysOfSubordinateInstances;

    private class DisplaySummaryInformationCheckboxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _containsWidgetState.setDisplaySummaryPanel(_displaySummaryPanel.isSelected());
        }
    }

    private class DisplayTotalNumberOfSubordinateInstancesCheckboxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _containsWidgetState.setDisplayTotalNumberOfSubordinateInstances(_displayTotalNumberOfSubordinateInstances.isSelected());
        }
    }

    private class DisplayBrowserKeysOfSubordinateInstancesCheckboxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _containsWidgetState.setDisplayBrowserKeysOfSubordinateInstances(_displayBrowserKeysOfSubordinateInstances.isSelected());
        }
    }

    public SummaryConfigurationPanel(ContainsWidgetState state) {
        super(state);
    }

    private void buildDisplayBrowserKeysOfSubordinateInstances(int yPosition) {
        _displayBrowserKeysOfSubordinateInstances = createCheckBox("Display Browser Keys of Subordinate Instances", yPosition);
        _displayBrowserKeysOfSubordinateInstances.setSelected(
            _containsWidgetState.isDisplayBrowserKeysOfSubordinateInstances());
        _displayBrowserKeysOfSubordinateInstances.addActionListener(
            new DisplayBrowserKeysOfSubordinateInstancesCheckboxListener());
    }

    private void buildDisplaySummaryPanel(int yPosition) {
        _displaySummaryPanel = createCheckBox("Display Summary Panel", yPosition);
        _displaySummaryPanel.setSelected(_containsWidgetState.isDisplaySummaryPanel());
        _displaySummaryPanel.addActionListener(new DisplaySummaryInformationCheckboxListener());
    }

    private void buildDisplayTotalNumberOfSubordinateInstances(int yPosition) {
        _displayTotalNumberOfSubordinateInstances = createCheckBox("Display Total Number of Subordinate Instances", yPosition);
        _displayTotalNumberOfSubordinateInstances.setSelected(
            _containsWidgetState.isDisplayTotalNumberOfSubordinateInstances());
        _displayTotalNumberOfSubordinateInstances.addActionListener(
            new DisplayTotalNumberOfSubordinateInstancesCheckboxListener());
    }

    protected void buildGUI() {
        _containsWidgetState = (ContainsWidgetState) _state;
        buildDisplaySummaryPanel(1);
        buildDisplayTotalNumberOfSubordinateInstances(2);
        buildDisplayBrowserKeysOfSubordinateInstances(3);
        addVerticalSpace(4);
    }
}
