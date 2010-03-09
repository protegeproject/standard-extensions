package edu.stanford.smi.protegex.widget.contains;

import javax.swing.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class WidgetSeparator extends JPanel {
    protected int _divisor;

    public WidgetSeparator(int divisor) {
        _divisor = divisor;
    }
}
