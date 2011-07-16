package edu.stanford.smi.protegex.util;

import java.awt.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Base class for configuration panels
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class AbstractWidgetConfigurationPanel extends JPanel implements Validatable {
    private static final long serialVersionUID = -8593772017355647904L;
    public AbstractWidgetState _state;

    // "public" is a necessary hack because the language spec is messed up
    public AbstractWidgetConfigurationPanel(AbstractWidgetState state) {
        super(new GridBagLayout());
        _state = state;
        buildGUI();
    }

    protected void addVerticalSpace(int yPosition) {
        JPanel spacer = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = yPosition;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(spacer, gbc);
    }

    protected GridBagConstraints buildComponentGridBagConstraints(int yPosition) {
        GridBagConstraints returnValue = new GridBagConstraints();
        returnValue.gridwidth = 4;
        returnValue.gridheight = 1;
        returnValue.gridx = 0;
        returnValue.gridy = yPosition;
        returnValue.weightx = 1.0;
        returnValue.weighty = 0.0;
        returnValue.fill = GridBagConstraints.HORIZONTAL;
        return returnValue;
    }

    protected abstract void buildGUI();

    protected JCheckBox createCheckBox(String explanation, int yPosition) {
        JCheckBox returnValue = new JCheckBox(explanation);
        add(returnValue, buildComponentGridBagConstraints(yPosition));
        return returnValue;
    }

    protected JTextField createTextfield(String explanation, int yPosition) {
        JTextField returnValue = new JTextField();
        LabeledComponent wrapper = new LabeledComponent(explanation, returnValue, true);
        add(wrapper, buildComponentGridBagConstraints(yPosition));
        return returnValue;
    }

    public void saveContents() {
        _state.save();
    }

    public boolean validateContents() {
        return true;
    }
}
