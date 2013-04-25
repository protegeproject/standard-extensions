package edu.stanford.smi.protegex.widget.slider;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Responsible for (1) Setting value class type (2) Adding and removing index
 *  definitions (3) setting the x index (4) setting the y index aggregates the
 *  objects in configuration.mainconfigurationpanel
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class MainConfigurationPanel extends JPanel implements Validatable {
    private static final long serialVersionUID = 1337777428551820435L;
    private SliderProperties _properties;
    private JTextField _tickIncrement;
    private JCheckBox _displayTicks;
    private Box _internalBox;

    private class TickIncrementListener extends DocumentChangedListener {
        public void stateChanged(ChangeEvent e) {
            Integer newValue = getTextFieldAsInteger();
            if (null != newValue) {
                _properties.setTickIncrement(newValue);
            }
        }
    }

    private class DisplayTicksListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _properties.setDisplayTicks(_displayTicks.isSelected());
        }
    }

    public MainConfigurationPanel(SliderWidget widget) {
        super(new BorderLayout());
        _properties = widget.getProperties();
        _internalBox = new Box(BoxLayout.Y_AXIS);
        createTickIncrementTextField();
        createDisplayTicksCheckBox();
        add(_internalBox, BorderLayout.CENTER);
    }

    private JTextField constructLabeledTextField(String label) {
        JTextField returnValue = new JTextField();
        returnValue.setColumns(10);
        LabeledComponent surroundingBox = new LabeledComponent(label, returnValue);
        _internalBox.add(surroundingBox);
        Box.createVerticalStrut(4);
        return returnValue;
    }

    private void createDisplayTicksCheckBox() {
        _displayTicks = new JCheckBox("Display Ticks");
        _displayTicks.addActionListener(new DisplayTicksListener());
        LabeledComponent surroundingBox = new LabeledComponent("", _displayTicks);
        _internalBox.add(surroundingBox);
        Box.createVerticalStrut(4);
        setDisplayChecksCheckBox();
    }

    private void createTickIncrementTextField() {
        _tickIncrement = constructLabeledTextField("Tick Increment");
        (_tickIncrement.getDocument()).addDocumentListener(new TickIncrementListener());
        setTickIncrementField();
    }

    private Integer getTextFieldAsInteger() {
        String text = _tickIncrement.getText();
        if ((null != text) && !text.equals("")) {
            try {
                Integer intValue = new Integer(text);
                return intValue;
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void saveContents() {
        _properties.save();
    }

    private void setDisplayChecksCheckBox() {
        _displayTicks.setSelected(_properties.isDisplayTicks());
    }

    private void setTickIncrementField() {
        _tickIncrement.setText(String.valueOf(_properties.getTickIncrement()));
    }

    public boolean validateContents() {
        if (_properties.isDisplayTicks()) {
            return (null != getTextFieldAsInteger());
        }
        return true;
    }
}
