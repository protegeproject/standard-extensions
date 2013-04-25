package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.awt.*;

import javax.swing.*;

public class QueryNamePanel extends JComponent {
    private static final long serialVersionUID = 2436706248586452049L;
    private JLabel label;
    private JTextField nameField;


    public QueryNamePanel() {
        setLayout(new GridLayout(2, 1));
        label = new JLabel("Query Name:");
        nameField = new JTextField("");
        add(label);
        add(nameField);
    }

    public String getText() {
        return nameField.getText().trim();
    }
}
