package edu.stanford.smi.protegex.widget.scatterbox;

import javax.swing.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class RowHeader extends JTable {

    public RowHeader(Order order) {
        super(new RowHeaderTableModel(order));
        setShowGrid(false);
    }
}
