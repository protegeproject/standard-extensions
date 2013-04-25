package edu.stanford.smi.protegex.widget.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.PropertyList;

public class ViewConfigurationPanel extends JPanel {
    private static final long serialVersionUID = -977147442622417584L;

    private JPanel gridPropsPnl = new JPanel();

    // UI for grid style.  Right now this is just for setting the style
    // of the grid (i.e. tick marks, no tick marks, etc.).
    private JLabel lblGridStyle = new JLabel("Grid Style");
    private ButtonGroup group = new ButtonGroup();
    private JRadioButton invisible = new JRadioButton(GraphTypes.GRID_INVISIBLE);
    private JRadioButton dots = new JRadioButton(GraphTypes.GRID_DOTS);
    private JRadioButton crosses = new JRadioButton(GraphTypes.GRID_CROSSES);
    private JRadioButton lines = new JRadioButton(GraphTypes.GRID_LINES);

    private JLabel lblSnapOnMove = new JLabel("Snap on Move");
    private ButtonGroup group2 = new ButtonGroup();
    private JRadioButton noSnap = new JRadioButton(GraphTypes.GRID_NO_SNAP);
    private JRadioButton jump = new JRadioButton(GraphTypes.GRID_SNAP_DURING);
    private JRadioButton afterwards = new JRadioButton(GraphTypes.GRID_SNAP_AFTER);

    private ViewProperties vProps;

    public ViewConfigurationPanel(Cls cls, PropertyList props) {
        try {
            vProps = new ViewProperties(cls.getName(), props);
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws Exception {
        /* Build Grid Properties panel ****************************************/
        TitledBorder tb0 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(148, 145, 140)), "Default Grid Properties");
        gridPropsPnl.setBorder(tb0);

        initRadioButtons();

        gridPropsPnl.setLayout(new BoxLayout(gridPropsPnl, BoxLayout.Y_AXIS));
        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 10)));
        gridPropsPnl.add(lblGridStyle);
        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 5)));
        gridPropsPnl.add(invisible);
        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 5)));
        gridPropsPnl.add(crosses);
        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 5)));
        gridPropsPnl.add(dots);
        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 5)));
        gridPropsPnl.add(lines);

        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 10)));
        gridPropsPnl.add(lblSnapOnMove);
        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 5)));
        gridPropsPnl.add(noSnap);
        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 5)));
        gridPropsPnl.add(jump);
        gridPropsPnl.add(Box.createRigidArea(new Dimension(0, 5)));
        gridPropsPnl.add(afterwards);

        this.setLayout(new BorderLayout(5, 5));
        this.add(gridPropsPnl, BorderLayout.NORTH);
    }

    public void saveContents() {
        vProps.setGridStyle(group.getSelection().getActionCommand());
        vProps.setSnapOnMove(group2.getSelection().getActionCommand());
        vProps.save();
    }

    private void initRadioButtons() {
        crosses.setActionCommand(GraphTypes.GRID_CROSSES);
        dots.setActionCommand(GraphTypes.GRID_DOTS);
        invisible.setActionCommand(GraphTypes.GRID_INVISIBLE);
        lines.setActionCommand(GraphTypes.GRID_LINES);

        noSnap.setActionCommand(GraphTypes.GRID_NO_SNAP);
        jump.setActionCommand(GraphTypes.GRID_SNAP_DURING);
        afterwards.setActionCommand(GraphTypes.GRID_SNAP_AFTER);

        group.add(crosses);
        group.add(dots);
        group.add(invisible);
        group.add(lines);

        group2.add(noSnap);
        group2.add(jump);
        group2.add(afterwards);

        String s = vProps.getGridStyle();
        if (s.equals(GraphTypes.GRID_CROSSES)) {
            crosses.setSelected(true);
        } else if (s.equals(GraphTypes.GRID_DOTS)) {
            dots.setSelected(true);
        } else if (s.equals(GraphTypes.GRID_INVISIBLE)) {
            invisible.setSelected(true);
        } else if (s.equals(GraphTypes.GRID_LINES)) {
            lines.setSelected(true);
        }

        s = vProps.getSnapOnMove();
        if (s.equals(GraphTypes.GRID_NO_SNAP)) {
            noSnap.setSelected(true);
        } else if (s.equals(GraphTypes.GRID_SNAP_DURING)) {
            jump.setSelected(true);
        } else if (s.equals(GraphTypes.GRID_SNAP_AFTER)) {
            afterwards.setSelected(true);
        }
    }
}