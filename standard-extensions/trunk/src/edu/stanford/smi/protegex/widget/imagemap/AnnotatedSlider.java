package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *  Basically, a slider with a secondary view. Usually the view is either a
 *  textfield or a line (indicating thickness).
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
class AnnotatedSlider extends JPanel {
    private static final long serialVersionUID = -5194436711487711835L;
    public final static int LINE_VIEW = 0;
    public final static int TEXT_VIEW = 1;

    private JSlider _slider;
    private SecondaryView _secondaryView;
    private int _minimum;
    private int _maximum;
    private ArrayList _listeners;

    private class SliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            _secondaryView.setValue(_slider.getValue());
            broadcast();
        }
    }

    public AnnotatedSlider(int minimum, int maximum, int value, SecondaryView secondaryView) {
        super(new GridBagLayout());
        _secondaryView = secondaryView;
        _minimum = minimum;
        _maximum = maximum;
        buildGUI();
        _slider.setValue(value);
        _slider.addChangeListener(new SliderListener());
        _listeners = new ArrayList();
    }

    public void addChangeListener(ChangeListener changeListener) {
        if (!_listeners.contains(changeListener)) {
            _listeners.add(changeListener);
        }
    }

    private void addSlider() {
        _slider = new JSlider(_minimum, _maximum, _minimum);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(_slider, gbc);
    }

    private void addSpacer() {
        JPanel spacer = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridx = 5;
        gbc.gridy = 0;
        add(spacer, gbc);
    }

    private void addView() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridx = 6;
        gbc.gridy = 0;
        add((JComponent) _secondaryView, gbc);
    }

    private void broadcast() {
        Iterator i = _listeners.iterator();
        ChangeEvent event = new ChangeEvent(this);
        while (i.hasNext()) {
            ((ChangeListener) (i.next())).stateChanged(event);
        }
    }

    private void buildGUI() {
        addSlider();
        addSpacer();
        addView();
    }

    public static AnnotatedSlider getAnnotatedSlider(int minimum, int maximum, int value, int secondaryView) {
        switch (secondaryView) {
            case LINE_VIEW :
                return new AnnotatedSlider(minimum, maximum, value, new LineView());
            case TEXT_VIEW :
                return new AnnotatedSlider(minimum, maximum, value, new TextView());
        }
        return null;
    }

    public int getValue() {
        return _slider.getValue();
    }

    public void removeChangeListener(ChangeListener changeListener) {
        _listeners.remove(changeListener);
    }

    public void setValue(int value) {
        _slider.setValue(value);
        _secondaryView.setValue(value);
    }
}
