package edu.stanford.smi.protegex.widget.abstracttable;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class UseDialogPanel extends LabeledComponent {
    private static final long serialVersionUID = -3878123383704391330L;
    private JTextField _dialogTitleTextField;
    private JCheckBox _useDialogCheckBox;
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

    public UseDialogPanel(String selectionType, String label, boolean useDialog, String dialogTitle) {
        super(label, (JComponent) null);
        setCenterComponent(createCenterPanel(selectionType, useDialog, dialogTitle));
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

    private JPanel createCenterPanel(String selectionType, boolean useDialog, String dialogTitle) {
        JPanel returnValue = new JPanel(new GridBagLayout());
        _useDialogCheckBox = new JCheckBox("Use a dialog panel to select " + selectionType);
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
        returnValue.add(_useDialogCheckBox, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        returnValue.add(new JLabel("Dialog Title  "), gbc);

        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        returnValue.add(_dialogTitleTextField, gbc);

        setUseDialog(useDialog);
        setDialogTitle(dialogTitle);
        (_dialogTitleTextField.getDocument()).addDocumentListener(new TextFieldChanged());
        _useDialogCheckBox.addActionListener(new CheckBoxListener());
        return returnValue;
    }

    public String getDialogTitle() {
        return _dialogTitleTextField.getText();
    }

    public boolean getUseDialog() {
        return _useDialogCheckBox.isSelected();
    }

    public boolean isUseDialog() {
        return _useDialogCheckBox.isSelected();
    }

    public void removeActionListener(ActionListener listener) {
        if (_actionListeners.contains(listener)) {
            ArrayList newActionListeners = new ArrayList(_actionListeners);
            newActionListeners.remove(listener);
            _actionListeners = newActionListeners;
        }
        return;
    }

    public void setDialogTitle(String dialogTitle) {
        _notInSetMethod = false;
        _dialogTitleTextField.setText(dialogTitle);
        _notInSetMethod = true;
    }

    public void setUseDialog(boolean useDialog) {
        _notInSetMethod = false;
        _useDialogCheckBox.setSelected(useDialog);
        _notInSetMethod = true;
    }
}
