package edu.stanford.smi.protegex.widget.contains;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class OverallAppearanceConfigurationPanel extends AbstractWidgetConfigurationPanel {
    private static final long serialVersionUID = 8583384001885181114L;
    private ContainsWidgetState _containsWidgetState;
    private JCheckBox _subwidgetSelectionAllowed;
    private JCheckBox _putSeparatorBetweenSubwidgets;
    private JTextField _spaceBetweenWidgets;
    private JCheckBox _containInVerticalDirection;

    private class RowSelectionCheckboxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ((ContainsWidgetState) _state).setSubwidgetSelectionAllowed(_subwidgetSelectionAllowed.isSelected());
        }
    }

    private class ContainInVerticalDirection implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ((ContainsWidgetState) _state).setContainInVerticalDirection(_containInVerticalDirection.isSelected());
        }
    }

    private class SpacerSizeChangeListener extends DocumentChangedListener {
        public void stateChanged(ChangeEvent e) {
            Integer newValue = getTextFieldAsInteger();
            if (null != newValue) {
                _containsWidgetState.setSpaceBetweenSubwidgets(newValue.intValue());
            }
        }
    }

    public OverallAppearanceConfigurationPanel(ContainsWidgetState state) {
        super(state);
    }

    private void buildContainInVerticalDirection(int yPosition) {
        _containInVerticalDirection = createCheckBox("Stack subwidgets vertically", yPosition);
        _containInVerticalDirection.setSelected(_containsWidgetState.isContainInVerticalDirection());
        _containInVerticalDirection.addActionListener(new ContainInVerticalDirection());
    }

    protected void buildGUI() {
        _containsWidgetState = (ContainsWidgetState) _state;
        buildObjectsCanBeSelected(1);
        buildPutSeparatorBetweenSubwidgets(2);
        buildSpaceBetweenWidgets(3);
        buildContainInVerticalDirection(4);
        addVerticalSpace(5);
    }

    private void buildObjectsCanBeSelected(int yPosition) {
        _subwidgetSelectionAllowed = createCheckBox("Allow subordinate widgets to be selected", yPosition);
        _subwidgetSelectionAllowed.setSelected(_containsWidgetState.isSubwidgetSelectionAllowed());
        _subwidgetSelectionAllowed.addActionListener(new RowSelectionCheckboxListener());
    }

    private void buildPutSeparatorBetweenSubwidgets(int yPosition) {
        _putSeparatorBetweenSubwidgets = createCheckBox("Use separator between subwidgets", yPosition);
        _putSeparatorBetweenSubwidgets.setSelected(_containsWidgetState.isSeparatorUsedBetweenSubwidgets());
        _putSeparatorBetweenSubwidgets.addActionListener(new RowSelectionCheckboxListener());
    }

    private void buildSpaceBetweenWidgets(int yPosition) {
        _spaceBetweenWidgets = createTextfield("Space to put between subwidgets", yPosition);
        String integerValue = String.valueOf(_containsWidgetState.getSpaceBetweenSubwidgets());
        _spaceBetweenWidgets.setText(integerValue);
        (_spaceBetweenWidgets.getDocument()).addDocumentListener(new SpacerSizeChangeListener());
    }

    private Integer getTextFieldAsInteger() {
        String text = _spaceBetweenWidgets.getText();
        if ((null != text) && !text.equals("")) {
            try {
                Integer intValue = new Integer(text);
                return intValue;
            } catch (Exception e) {
            }
        }
        return null;
    }
}
