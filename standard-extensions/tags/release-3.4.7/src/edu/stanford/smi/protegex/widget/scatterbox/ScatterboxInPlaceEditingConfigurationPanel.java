package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protegex.widget.abstracttable.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxInPlaceEditingConfigurationPanel extends InPlaceEditingConfigurationPanel {
    private static final long serialVersionUID = 7559504023488627682L;
    private ScatterboxWidgetState _scatterboxWidgetState;
    private JCheckBox _autocreateEntriesCheckbox;
    private JCheckBox _autoDisplayInstancesCheckbox;

    private class AutocreateCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _scatterboxWidgetState.setAutocreateWhenEditing(_autocreateEntriesCheckbox.isSelected());
        }
    }

    private class AutoDisplayCheckBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _scatterboxWidgetState.setAutomaticallyDisplayFormsForCreatedInstances(_autoDisplayInstancesCheckbox.isSelected());
        }
    }

    public ScatterboxInPlaceEditingConfigurationPanel(ScatterboxWidgetState scatterboxWidgetState) {
        super(scatterboxWidgetState);
    }

    protected void buildAutocreateBeforeEditingCheckBox(int yPosition) {
        _autocreateEntriesCheckbox = createCheckBox("Automatically create entries when editing", yPosition);
        _autocreateEntriesCheckbox.setSelected(_scatterboxWidgetState.isAutocreateWhenEditing());
        _autocreateEntriesCheckbox.addActionListener(new AutocreateCheckBoxListener());
    }

    protected void buildAutomaticallyDisplayFormsForCreatedInstances(int yPosition) {
        _autoDisplayInstancesCheckbox = createCheckBox("Automatically display forms for created instances", yPosition);
        _autoDisplayInstancesCheckbox.setSelected(_scatterboxWidgetState.isAutomaticallyDisplayFormsForCreatedInstances());
        _autoDisplayInstancesCheckbox.addActionListener(new AutoDisplayCheckBoxListener());
    }

    protected void buildGUI() {
        _scatterboxWidgetState = (ScatterboxWidgetState) _state;
        buildInPlaceEditingCheckBox(0);
        buildUseDialogForClasses(1);
        buildUseDialogForInstances(2);
        buildAutocreateBeforeEditingCheckBox(3);
        buildAutomaticallyDisplayFormsForCreatedInstances(4);
        addVerticalSpace(5);
    }
}
