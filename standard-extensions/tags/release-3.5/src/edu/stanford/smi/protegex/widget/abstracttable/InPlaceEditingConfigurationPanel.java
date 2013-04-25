package edu.stanford.smi.protegex.widget.abstracttable;

import java.awt.event.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InPlaceEditingConfigurationPanel extends AbstractTableWidgetConfigurationSubPanel implements Validatable {
    private static final long serialVersionUID = 2131089154655761041L;
    private UseDialogPanel _useDialogForClasses;
    private UseDialogPanel _useDialogForInstances;
    private JCheckBox _inPlaceEditingCheckBox;

    private class InPlaceEditCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _state.setEditInPlace(_inPlaceEditingCheckBox.isSelected());
        }
    }

    private class UseDialogForClassesListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _state.setUseDialogToSelectClasses(_useDialogForClasses.isUseDialog());
            _state.setDialogTitleForSelectingClasses(_useDialogForClasses.getDialogTitle());
        }
    }

    private class UseDialogForInstancesListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _state.setUseDialogToSelectInstances(_useDialogForInstances.isUseDialog());
            _state.setDialogTitleForSelectingInstances(_useDialogForInstances.getDialogTitle());
        }
    }

    public InPlaceEditingConfigurationPanel(AbstractTableWidgetState state) {
        super(state);
    }

    protected void buildGUI() {
        buildInPlaceEditingCheckBox(0);
        buildUseDialogForClasses(1);
        buildUseDialogForInstances(2);
        addVerticalSpace(3);
    }

    protected void buildInPlaceEditingCheckBox(int yPosition) {
        _inPlaceEditingCheckBox = createCheckBox("Use in place editing", yPosition);
        _inPlaceEditingCheckBox.setSelected(_state.isEditInPlace());
        _inPlaceEditingCheckBox.addActionListener(new InPlaceEditCheckBoxListener());
    }

    protected void buildUseDialogForClasses(int yPosition) {
        _useDialogForClasses =
            new UseDialogPanel(
                "classes",
                "Selecting Classes",
                _state.isUseDialogToSelectClasses(),
                _state.getDialogTitleForSelectingClasses());
        _useDialogForClasses.addActionListener(new UseDialogForClassesListener());
        add(_useDialogForClasses, buildComponentGridBagConstraints(yPosition));
    }

    protected void buildUseDialogForInstances(int yPosition) {
        _useDialogForInstances =
            new UseDialogPanel(
                "instances",
                "Selecting Instances",
                _state.isUseDialogToSelectInstances(),
                _state.getDialogTitleForSelectingInstances());
        _useDialogForInstances.addActionListener(new UseDialogForInstancesListener());
        add(_useDialogForInstances, buildComponentGridBagConstraints(yPosition));
    }

    public void saveContents() {
        _state.save();
    }

    public boolean validateContents() {
        return true;
    }
}
