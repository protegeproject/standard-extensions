package edu.stanford.smi.protegex.widget.slider;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.*;

/**
 *  This object is the facade for the SliderWidget subsystem. It constitutes the
 *  interface between Protege and the actual functionality of SliderWidget.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SliderWidget extends AbstractSlotWidget implements Observer {

    private static final long serialVersionUID = 1996046640830744112L;
    // actual instance code begins here
    private SliderProperties _properties;
    private JSlider _slider;
    private JTextField _textField;
    private ListensToSlider _sliderChangeListener;

    private class ListensToSlider implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            _textField.setText(String.valueOf(_slider.getValue()));
            if (!_slider.getValueIsAdjusting()) {
                valueChanged();
            }
        }
    }

    public SliderWidget() {
    }

    private void addListeners() {
        _sliderChangeListener = new ListensToSlider();
        _slider.addChangeListener(_sliderChangeListener);
    }

    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        return new SliderWidgetConfigurationPanel(this);
    }

    public void dispose() {
        _properties.dispose();
        _sliderChangeListener = null;
        super.dispose();
    }

    public SliderProperties getProperties() {
        return _properties;
    }

    public Collection getValues() {
        ArrayList returnValue = new ArrayList();
        returnValue.add(new Integer(_slider.getValue()));
        return returnValue;
    }

    public void initialize() {
        // initialize is called after the constructor has been called and AbstractWidget
        // is fully configured. This means that the methods defined in AbstractWidget will
        // all return the appropriate objects.
        Slot slot = getSlot();
        setPreferredRows(1);
        setPreferredColumns(1);
        _properties = new SliderProperties(getPropertyList(), getCls(), slot);
        _slider = new JSlider();
        _textField = new JTextField();
        _textField.setEditable(false);
        _textField.setColumns(3);

        GridBagConstraints sliderConstraints = new GridBagConstraints();
        sliderConstraints.fill = GridBagConstraints.BOTH;

        GridBagConstraints textfieldConstraints = new GridBagConstraints();
        textfieldConstraints.fill = GridBagConstraints.BOTH;
        textfieldConstraints.gridwidth = GridBagConstraints.REMAINDER;

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.add(_slider, sliderConstraints);
        contentPanel.add(_textField, textfieldConstraints);

        LabeledComponent actualUI = new LabeledComponent(getLabel(), contentPanel, true);
        add(actualUI);
        setUIFromProperties();
        if (isRuntime()) {
            addListeners();
        }
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        if ((null == cls) || (null == slot)) {
            return false;
        }
        // does the slot point to numbers
        if ((cls.getTemplateSlotValueType(slot) != ValueType.INTEGER)
            && (cls.getTemplateSlotValueType(slot) != ValueType.FLOAT)) {
            return false;
        }
        // is the slot single valued
        if (cls.getTemplateSlotAllowsMultipleValues(slot)) {
            return false;
        }

        if (null == cls.getTemplateSlotMaximumValue(slot)) {
            return false;
        }
        if (null == cls.getTemplateSlotMinimumValue(slot)) {
            return false;
        }

        //looks like we're suitable
        return true;
    }

    public void setEditable(boolean b) {
        // do nothing in here
    }

    private void setUIFromProperties() {
        Number min = getCls().getTemplateSlotMinimumValue(getSlot());
        if (min != null) {
            _slider.setMinimum(min.intValue());
        }
        Number max = getCls().getTemplateSlotMaximumValue(getSlot());
        if (max != null) {
            _slider.setMaximum(max.intValue());
        }
        if (_properties.isDisplayTicks()) {
            _slider.setPaintTicks(true);
            _slider.setMinorTickSpacing(_properties.getTickIncrement());
        } else {
            _slider.setPaintTicks(false);
        }
    }

    public void setValues(java.util.Collection values) {
        if ((null == values) || (0 == values.size())) {
            return;
        }
        Iterator i = values.iterator();
        Object value = i.next();
        _slider.setValue(((Integer) value).intValue());
        return;
    }

    // and methods that are here for the aggregated objects
    public void update(Observable observable, Object argument) {
        setUIFromProperties();
    }
}
