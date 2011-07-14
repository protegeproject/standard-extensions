package edu.stanford.smi.protegex.util;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 *  Actually has the actual ColorPanel and a JButton. Basically renders a color
 *  and allows it to be changed Alternate version of ColorChooser used in
 *  diagrams
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ColorWell extends JPanel {
    private static final long serialVersionUID = -1055373818922007854L;

    private final static String DEFAULT_DIALOG_LABEL = "Choose a Color";

    private ActionListener _okListener;
    private Color _color;
    private String _dialogLabel;
    private ColorPanel _colorPanel;
    private JButton _button;

    private class ColorListener extends MouseAdapter implements ActionListener {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                getNewColorFromColorPanel();
            }
        }

        public void actionPerformed(ActionEvent e) {
            getNewColorFromColorPanel();
        }

        private void getNewColorFromColorPanel() {
            setColor(JColorChooser.showDialog(ColorWell.this, _dialogLabel, _color));
        }
    }

    private class PropagateFocusEvents implements FocusListener {
        public void focusGained(FocusEvent e) {
            processFocusEvent(e);
        }

        public void focusLost(FocusEvent e) {
            processFocusEvent(e);
        }
    }

    public ColorWell(Color color, ActionListener colorChangeListener) {
        this(color, colorChangeListener, DEFAULT_DIALOG_LABEL, true);
    }

    public ColorWell(Color color, ActionListener colorChangeListener, String dialogLabel) {
        this(color, colorChangeListener, dialogLabel, true);
    }

    public ColorWell(Color color, ActionListener colorChangeListener, String dialogLabel, boolean useButton) {
        super(new GridBagLayout());
        _dialogLabel = dialogLabel;
        buildGUI(useButton);
        addListeners();
        setOpaque(true);
        setColor(color);
        _okListener = colorChangeListener;
    }

    public ColorWell(Color color, ActionListener colorChangeListener, boolean useButton) {
        this(color, colorChangeListener, DEFAULT_DIALOG_LABEL, useButton);
    }

    private void addButton() {
        _button = new JButton("Change color");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridx = 6;
        gbc.gridy = 0;
        add(_button, gbc);
    }

    private void addColorPanel() {
        _colorPanel = new ColorPanel(_color);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(_colorPanel, gbc);
    }

    private void addListeners() {
        ColorListener colorListener = new ColorListener();
        PropagateFocusEvents focusListener = new PropagateFocusEvents();
        _colorPanel.addMouseListener(colorListener);
        _colorPanel.addFocusListener(focusListener);
        if (null != _button) {
            _button.addActionListener(colorListener);
            _button.addFocusListener(focusListener);
        }
    }

    private void addSpacer() {
        JPanel spacer = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridx = 5;
        gbc.gridy = 0;
        add(spacer, gbc);
    }

    private void buildGUI(boolean useButton) {
        addColorPanel();
        if (useButton) {
            addSpacer();
            addButton();
        }
    }

    public Color getColor() {
        return _color;
    }

    protected void processFocusEvent(FocusEvent e) {
        super.processFocusEvent(e);
    }

    public void setColor(Color color) {
        if (null != color) {
            _color = color;
            if (null != _okListener) {
                _okListener.actionPerformed(new ActionEvent(this, 0, "Color changed"));
            }
            _colorPanel.setColor(_color);
            repaint();
        }
    }
}
