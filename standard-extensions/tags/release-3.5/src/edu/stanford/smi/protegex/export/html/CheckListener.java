package edu.stanford.smi.protegex.export.html;

import java.awt.event.*;
import javax.swing.JList;

public class CheckListener implements MouseListener, KeyListener {

    private JList list;

    public CheckListener(JList list) {
        this.list = list;
    }

    public void mouseClicked(MouseEvent e) {
        // If mouse click is less than 20 pixels from left edge,
        // consider it a click on check box.
        if (e.getX() < 20) {
            doCheck();
        }
    }

    public void mouseEntered(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            doCheck();
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    private void doCheck() {
        int index = list.getSelectedIndex();

        // Shortcut.
        if (index < 0 ) return;

        FrameData fdata = (FrameData) list.getModel().getElementAt(index);
        fdata.invertSelected();
        list.repaint();
    }
}