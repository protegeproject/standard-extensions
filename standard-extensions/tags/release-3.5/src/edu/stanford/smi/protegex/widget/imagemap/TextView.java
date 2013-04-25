package edu.stanford.smi.protegex.widget.imagemap;

import javax.swing.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class TextView extends JTextField implements SecondaryView {
    private static final long serialVersionUID = 8561936628672501463L;
    private final static int DEFAULT_WIDTH = 2;

    public TextView() {
        this(DEFAULT_WIDTH);
    }

    public TextView(int width) {
        super(width);
        setEditable(false);
    }

    public void setValue(int value) {
        setText(String.valueOf(value));
    }
}
