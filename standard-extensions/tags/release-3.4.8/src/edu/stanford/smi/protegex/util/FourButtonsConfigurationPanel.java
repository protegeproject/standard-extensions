package edu.stanford.smi.protegex.util;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;

/**
 * Panel for five buttons.
 *
 * @author William Grosso <grosso@smi.stanford.edu>
 */
public class FourButtonsConfigurationPanel extends AbstractWidgetConfigurationPanel {
    private static final long serialVersionUID = -5087960442487257766L;
    protected ButtonInformationPanel _addInstancePanel;
    protected ButtonInformationPanel _createInstancePanel;
    protected ButtonInformationPanel _removeInstancePanel;
    protected ButtonInformationPanel _viewInstancePanel;
    protected ButtonInformationPanel _deleteInstancePanel;
    protected JPanel _mainPanel;
    protected ButtonRelatedWidgetState _buttonRelatedWidgetState;

    private class DisplayCreateInstanceButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _buttonRelatedWidgetState.setDisplayCreateInstanceButton(_createInstancePanel.isEnabled());
            _buttonRelatedWidgetState.setCreateInstanceButtonTooltip(_createInstancePanel.getTooltip());
            // _buttonRelatedWidgetState.setCreateInstanceDialogTitle(_createInstancePanel.getDialogTitle());
        }
    }

    private class DisplayAddInstanceButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _buttonRelatedWidgetState.setDisplayAddInstanceButton(_addInstancePanel.isEnabled());
            _buttonRelatedWidgetState.setAddInstanceButtonTooltip(_addInstancePanel.getTooltip());
            // _buttonRelatedWidgetState.setAddInstanceDialogTitle(_addInstancePanel.getDialogTitle());
        }
    }

    private class DisplayRemoveInstanceButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _buttonRelatedWidgetState.setDisplayRemoveInstanceButton(_removeInstancePanel.isEnabled());
            _buttonRelatedWidgetState.setRemoveInstanceButtonTooltip(_removeInstancePanel.getTooltip());
        }
    }

    private class DisplayViewInstanceButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _buttonRelatedWidgetState.setDisplayViewInstanceButton(_viewInstancePanel.isEnabled());
            _buttonRelatedWidgetState.setViewInstanceButtonTooltip(_viewInstancePanel.getTooltip());
        }
    }

    private class DisplayDeleteInstanceButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _buttonRelatedWidgetState.setDisplayDeleteInstanceButton(_deleteInstancePanel.isEnabled());
            _buttonRelatedWidgetState.setDeleteInstanceButtonTooltip(_deleteInstancePanel.getTooltip());
        }
    }

    public FourButtonsConfigurationPanel(ButtonRelatedWidgetState state) {
        super(state);
    }

    //  public ButtonInformationPanel(String checkBoxLabel, boolean checkBoxEnabled, String tooltip)
    protected void buildButtonSelectionPanel(int yPosition) {
        _mainPanel = new JPanel(new GridLayout(6, 1, 0, 10));

        _addInstancePanel =
            new ButtonInformationPanel(
                "Add Instance Button",
                _buttonRelatedWidgetState.isDisplayAddInstanceButton(),
                _buttonRelatedWidgetState.getAddInstanceButtonTooltip());
        // _buttonRelatedWidgetState.getAddInstanceDialogTitle());
        _addInstancePanel.addActionListener(new DisplayAddInstanceButtonListener());

        _createInstancePanel =
            new ButtonInformationPanel(
                "Create Instance Button",
                _buttonRelatedWidgetState.isDisplayCreateInstanceButton(),
                _buttonRelatedWidgetState.getCreateInstanceButtonTooltip());
        // _buttonRelatedWidgetState.getCreateInstanceDialogTitle());
        _createInstancePanel.addActionListener(new DisplayCreateInstanceButtonListener());

        _removeInstancePanel =
            new ButtonInformationPanel(
                "Remove Instance Button",
                _buttonRelatedWidgetState.isDisplayRemoveInstanceButton(),
                _buttonRelatedWidgetState.getRemoveInstanceButtonTooltip());
        _removeInstancePanel.addActionListener(new DisplayRemoveInstanceButtonListener());

        _viewInstancePanel =
            new ButtonInformationPanel(
                "View Instance Button",
                _buttonRelatedWidgetState.isDisplayViewInstanceButton(),
                _buttonRelatedWidgetState.getViewInstanceButtonTooltip());
        _viewInstancePanel.addActionListener(new DisplayViewInstanceButtonListener());

        _deleteInstancePanel =
            new ButtonInformationPanel(
                "Delete Instance Button",
                _buttonRelatedWidgetState.isDisplayDeleteInstanceButton(),
                _buttonRelatedWidgetState.getDeleteInstanceButtonTooltip());
        _deleteInstancePanel.addActionListener(new DisplayDeleteInstanceButtonListener());

        _mainPanel.add(_addInstancePanel);
        _mainPanel.add(_createInstancePanel);
        _mainPanel.add(_removeInstancePanel);
        _mainPanel.add(_viewInstancePanel);
        _mainPanel.add(_deleteInstancePanel);
        _mainPanel.setBorder(BorderFactory.createEtchedBorder());
        LabeledComponent componentToAdd = new LabeledComponent("Allowed Buttons", _mainPanel);
        add(componentToAdd, buildComponentGridBagConstraints(yPosition));
    }

    protected void buildGUI() {
        _buttonRelatedWidgetState = (ButtonRelatedWidgetState) _state;
        buildButtonSelectionPanel(1);
        addVerticalSpace(2);
    }
}
