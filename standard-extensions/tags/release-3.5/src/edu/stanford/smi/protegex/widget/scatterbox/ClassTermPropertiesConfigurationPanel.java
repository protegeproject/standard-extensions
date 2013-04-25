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
public class ClassTermPropertiesConfigurationPanel extends AbstractWidgetConfigurationPanel implements Constants {
    private static final long serialVersionUID = -5521386916964263303L;
    private ScatterboxWidgetState _scatterboxWidgetState;
    private ScatterboxWidget _widget;
    private JCheckBox _includeAbstractClassesInTermListCheckBox;
    private ButtonGroup _hierarchicalInclusionButtonGroup;
    private HashMap _radioButtonToStringChoice;
    private JPanel _radioButtonPanel;
    private RadioButtonListener _radioButtonListener;

    private class IncludeAbstractClassesInTermListCheckboxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _scatterboxWidgetState.setIncludeAbstractClassesInTermList(_includeAbstractClassesInTermListCheckBox.isSelected());
        }
    }

    private class RadioButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String policy = (String) _radioButtonToStringChoice.get(e.getSource());
            _scatterboxWidgetState.setHierarchicalPolicyWhenClassesAreDomainIndices(policy);
        }
    }

    public ClassTermPropertiesConfigurationPanel(ScatterboxWidgetState widgetState) {
        super(widgetState);
    }

    protected void buildGUI() {

        _scatterboxWidgetState = (ScatterboxWidgetState) _state;

        _widget = _scatterboxWidgetState.getWidget();

        buildIncludeAbstractClassesInTermListCheckBox(1);

        buildRadioButtonForHierarchicalPolicy(2);

        addVerticalSpace(5);

    }

    protected void buildIncludeAbstractClassesInTermListCheckBox(int yPosition) {
        _includeAbstractClassesInTermListCheckBox = createCheckBox("Include abstract classes in term list ", yPosition);
        _includeAbstractClassesInTermListCheckBox.setSelected(_scatterboxWidgetState.isIncludeAbstractClassesInTermList());
        _includeAbstractClassesInTermListCheckBox.addActionListener(new IncludeAbstractClassesInTermListCheckboxListener());
    }

    private void buildRadioButton(String label, boolean isSelected, String consequence) {
        JRadioButton newButton = new JRadioButton(label);
        _hierarchicalInclusionButtonGroup.add(newButton);
        newButton.setSelected(isSelected);
        _radioButtonPanel.add(newButton);
        newButton.addActionListener(_radioButtonListener);
        _radioButtonToStringChoice.put(newButton, consequence);
        return;
    }

    protected void buildRadioButtonForHierarchicalPolicy(int yPosition) {
        _hierarchicalInclusionButtonGroup = new ButtonGroup();
        _radioButtonPanel = new JPanel(new GridLayout(4, 1));
        _radioButtonPanel.setBorder(BorderFactory.createEtchedBorder());
        _radioButtonToStringChoice = new HashMap();
        _radioButtonListener = new RadioButtonListener();
        String currentHierarchicalChoice = _scatterboxWidgetState.getHierarchicalPolicyWhenClassesAreDomainIndices();
        buildRadioButton(
            "Include everything",
            HIERARCHICAL_INCLUDE_EVERYTHING.equals(currentHierarchicalChoice),
            HIERARCHICAL_INCLUDE_EVERYTHING);
        buildRadioButton(
            "Include all the classes except the roots",
            HIERARCHICAL_OMIT_ROOTS.equals(currentHierarchicalChoice),
            HIERARCHICAL_OMIT_ROOTS);
        buildRadioButton(
            "Only include the primary children",
            HIERARCHICAL_ONLY_INCLUDE_LEVEL_1_CHILDREN.equals(currentHierarchicalChoice),
            HIERARCHICAL_ONLY_INCLUDE_LEVEL_1_CHILDREN);
        buildRadioButton(
            "Only include the leaf classes",
            HIERARCHICAL_ONLY_INCLUDE_LEAVES.equals(currentHierarchicalChoice),
            HIERARCHICAL_ONLY_INCLUDE_LEAVES);
        LabeledComponent wrapper = new LabeledComponent("Choose terms by hierarchical properties", _radioButtonPanel, true);
        GridBagConstraints constraints = buildComponentGridBagConstraints(yPosition);
        constraints.gridheight = 2;
        add(wrapper, constraints);
    }
}
