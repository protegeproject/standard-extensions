package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InstanceTermPropertiesConfigurationPanel extends AbstractWidgetConfigurationPanel implements Constants {
    private static final long serialVersionUID = 5244270119897357679L;
    private ScatterboxWidgetState _scatterboxWidgetState;
    private ScatterboxWidget _widget;
    private ButtonGroup _policyButtonGroup;
    private HashMap _radioButtonToStringChoice;
    private JPanel _radioButtonPanel;
    private RadioButtonListener _radioButtonListener;

    private class RadioButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String policy = (String) _radioButtonToStringChoice.get(e.getSource());
            _scatterboxWidgetState.setHierarchicalPolicyWhenClassesAreDomainIndices(policy);
        }
    }

    public InstanceTermPropertiesConfigurationPanel(ScatterboxWidgetState widgetState) {
        super(widgetState);
    }

    protected void buildGUI() {

        _scatterboxWidgetState = (ScatterboxWidgetState) _state;

        _widget = _scatterboxWidgetState.getWidget();

        buildRadioButtonForInstancePolicy(1);

        addVerticalSpace(3);

    }

    private void buildRadioButton(String label, boolean isSelected, String consequence) {
        JRadioButton newButton = new JRadioButton(label);
        _policyButtonGroup.add(newButton);
        newButton.setSelected(isSelected);
        _radioButtonPanel.add(newButton);
        newButton.addActionListener(_radioButtonListener);
        _radioButtonToStringChoice.put(newButton, consequence);
        return;
    }

    protected void buildRadioButtonForInstancePolicy(int yPosition) {
        _policyButtonGroup = new ButtonGroup();
        _radioButtonPanel = new JPanel(new GridLayout(2, 1));
        _radioButtonPanel.setBorder(BorderFactory.createEtchedBorder());
        _radioButtonToStringChoice = new HashMap();
        _radioButtonListener = new RadioButtonListener();
        String currentPolicy = _scatterboxWidgetState.getPolicyWhenInstancesAreDomainIndices();
        buildRadioButton("Include all instances", USE_ALL_INSTANCES.equals(currentPolicy), USE_ALL_INSTANCES);
        buildRadioButton("Only include direct instances", DIRECT_INSTANCES_ONLY.equals(currentPolicy), DIRECT_INSTANCES_ONLY);
        LabeledComponent wrapper = new LabeledComponent("Select instance policy", _radioButtonPanel, true);
        GridBagConstraints constraints = buildComponentGridBagConstraints(yPosition);
        constraints.gridheight = 2;
        add(wrapper, constraints);
    }
}
