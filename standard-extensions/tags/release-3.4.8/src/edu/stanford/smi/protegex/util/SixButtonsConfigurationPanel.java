package edu.stanford.smi.protegex.util;

import java.awt.event.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SixButtonsConfigurationPanel extends FourButtonsConfigurationPanel {
    private static final long serialVersionUID = -2663184231079075632L;
    protected ButtonInformationPanel _prototypeInstancePanel;

    private class DisplayPrototypeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _buttonRelatedWidgetState.setDisplayPrototypeButton(_prototypeInstancePanel.isEnabled());
            _buttonRelatedWidgetState.setPrototypeButtonTooltip(_prototypeInstancePanel.getTooltip());
        }
    }

    public SixButtonsConfigurationPanel(ButtonRelatedWidgetState state) {
        super(state);
    }

    protected void buildButtonSelectionPanel(int yPosition) {
        super.buildButtonSelectionPanel(yPosition);

        _prototypeInstancePanel =
            new ButtonInformationPanel(
                "Prototype Button",
                _buttonRelatedWidgetState.isDisplayPrototypeButton(),
                _buttonRelatedWidgetState.getPrototypeButtonTooltip());
        _prototypeInstancePanel.addActionListener(new DisplayPrototypeButtonListener());

        _mainPanel.add(_deleteInstancePanel);
        _mainPanel.add(_prototypeInstancePanel);
    }
}
