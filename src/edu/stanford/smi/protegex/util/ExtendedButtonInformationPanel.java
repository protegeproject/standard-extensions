package edu.stanford.smi.protegex.util;

import edu.stanford.smi.protege.util.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

/**
 *  Panel for buttons which can pop up a dialog
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ExtendedButtonInformationPanel extends LabeledComponent {
    private static final long serialVersionUID = -647877972193739246L;
    private JTextField _tooltipTextField;
    private JTextField _dialogTitleTextField;
    private JCheckBox _inUseCheckBox;
    private ArrayList _actionListeners;
    private boolean _notInSetMethod;

    private class CheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            announceChanges();
        }
    }

    private class TextFieldChanged implements DocumentListener {
        public void changedUpdate(DocumentEvent event) {
            announceChanges();
        }

        public void insertUpdate(DocumentEvent event) {
            announceChanges();
        }

        public void removeUpdate(DocumentEvent event) {
            announceChanges();
        }
    }

    public ExtendedButtonInformationPanel(String label, boolean checkBoxEnabled, String tooltip, String dialogTitle) {
        super(label, (JComponent) null);
        setCenterComponent(createCenterPanel(label, checkBoxEnabled, tooltip, dialogTitle));
        setBorder(BorderFactory.createEtchedBorder());
        _notInSetMethod = true;
    }

    public void addActionListener(ActionListener listener) {
        if (!_actionListeners.contains(listener)) {
            ArrayList newActionListeners = new ArrayList(_actionListeners);
            newActionListeners.add(listener);
            _actionListeners = newActionListeners;
        }
        return;
    }

    private void announceChanges() {
        if (_notInSetMethod) {
            Iterator i = _actionListeners.iterator();
            while (i.hasNext()) {
                ((ActionListener) i.next()).actionPerformed(null);
            }
        }
    }

    private JPanel createCenterPanel(String label, boolean buttonDisplayed, String tooltip, String dialogTitle) {
        JPanel returnValue = new JPanel(new GridBagLayout());
        _inUseCheckBox = new JCheckBox("Display " + label);
        _tooltipTextField = new JTextField(30);
        _dialogTitleTextField = new JTextField(30);
        _actionListeners = new ArrayList();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        returnValue.add(_inUseCheckBox, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        returnValue.add(new JLabel("Tooltip  "), gbc);

        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        returnValue.add(_tooltipTextField, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        returnValue.add(new JLabel("Dialog Title  "), gbc);

        gbc.weightx = 1.0;
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        returnValue.add(_dialogTitleTextField, gbc);

        setButtonDisplayed(buttonDisplayed);
        setTooltip(tooltip);
        setDialogTitle(dialogTitle);
        TextFieldChanged docListener = new TextFieldChanged();
        (_tooltipTextField.getDocument()).addDocumentListener(docListener);
        (_dialogTitleTextField.getDocument()).addDocumentListener(docListener);
        _inUseCheckBox.addActionListener(new CheckBoxListener());
        return returnValue;
    }

    public String getDialogTitle() {
        return _dialogTitleTextField.getText();
    }

    public boolean getEnabled() {
        return _inUseCheckBox.isSelected();
    }

    public String getTooltip() {
        return _tooltipTextField.getText();
    }

    public boolean isEnabled() {
        return _inUseCheckBox.isSelected();
    }

    public void removeActionListener(ActionListener listener) {
        if (_actionListeners.contains(listener)) {
            ArrayList newActionListeners = new ArrayList(_actionListeners);
            newActionListeners.remove(listener);
            _actionListeners = newActionListeners;
        }
        return;
    }

    public void setButtonDisplayed(boolean buttonDisplayed) {
        _notInSetMethod = false;
        _inUseCheckBox.setSelected(buttonDisplayed);
        _notInSetMethod = true;
    }

    public void setDialogTitle(String dialogTitle) {
        _notInSetMethod = false;
        _dialogTitleTextField.setText(dialogTitle);
        _notInSetMethod = true;
    }

    public void setTooltip(String tooltip) {
        _notInSetMethod = false;
        _tooltipTextField.setText(tooltip);
        _notInSetMethod = true;
    }
}
