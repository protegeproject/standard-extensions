package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;
import java.awt.event.*;

import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the Class
 *
 * @author    Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class InstanceTableButtonsConfigurationPanel extends SixButtonsConfigurationPanel {
    private static final long serialVersionUID = -1185312545818121677L;
    private ButtonInformationPanel _moveInstancePanel;

    private class DisplayMoveButtonsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _buttonRelatedWidgetState.setDisplayMoveInstanceButtons(_moveInstancePanel.isEnabled());
            _buttonRelatedWidgetState.setMoveInstanceButtonsTooltip(_moveInstancePanel.getTooltip());
        }
    }

    public InstanceTableButtonsConfigurationPanel(ButtonRelatedWidgetState state) {
        super(state);
        _mainPanel.setLayout(new GridLayout(7, 1, 0, 10));
    }

    protected void buildButtonSelectionPanel(int yPosition) {
        super.buildButtonSelectionPanel(yPosition);

        _moveInstancePanel =
            new ButtonInformationPanel(
                "Move Buttons",
                _buttonRelatedWidgetState.isDisplayMoveInstanceButtons(),
                _buttonRelatedWidgetState.getMoveInstanceTooltip());
        _moveInstancePanel.addActionListener(new DisplayMoveButtonsListener());

        _mainPanel.add(_moveInstancePanel);
    }
}
