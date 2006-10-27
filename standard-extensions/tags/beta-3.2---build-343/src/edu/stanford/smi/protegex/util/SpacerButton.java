package edu.stanford.smi.protegex.util;

import java.awt.event.*;

import javax.swing.*;

/**
 *  Disabled button used to space out other buttons (Unused?)
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SpacerButton extends AbstractAction {

    public SpacerButton() {
        super("");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {

    }
}
