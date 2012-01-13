package edu.stanford.smi.protegex.widget.scatterbox;

import javax.swing.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class RowHeader extends JTable {

    private static final long serialVersionUID = 7681694776521430532L;

    public RowHeader(Order order) {
        super(new RowHeaderTableModel(order));
        setShowGrid(false);
    }
}
