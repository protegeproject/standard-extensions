package edu.stanford.smi.protegex.widget.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.event.EventListenerList;

public class ComboBoxColorEditor implements ComboBoxEditor {
    ColorIcon editorIcon = new ColorIcon();
    JLabel editorLabel = new JLabel(editorIcon);

    EventListenerList listenerList = new EventListenerList();

    JColorChooser colorChooser = new JColorChooser();
    ActionListener okListener = new OKListener();
    Dialog dialog = JColorChooser.createDialog(null, "Choose A Color", true,
                                               colorChooser, okListener, null);

    class OKListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Color color = colorChooser.getColor();
            editorIcon.setColor(color);
            fireActionPerformed(e);
        }
    }

    public ComboBoxColorEditor() {
        editorLabel.setBorder(BorderFactory.createEtchedBorder());
        editorLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dialog.setVisible(true);
            }
        });
    }

    public Component getEditorComponent() {
        return editorLabel;
    }

    public void setItem(Object newValue) {
        Color color = (Color) newValue;
        editorIcon.setColor(color);
    }

    public Object getItem() {
        return editorIcon.getColor();
    }

    public void selectAll() {
        // Ignore.
    }

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed(ActionEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }
}