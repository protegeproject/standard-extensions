package edu.stanford.smi.protegex.widget.graph;

// java
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

// stanford
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.resource.Icons;

/**
 * @author Jennifer Vendetti
 */
public class ChooseRelationDialog extends JDialog {
    private static final long serialVersionUID = -6318247802884438267L;
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private JPanel relationsPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();

    private JButton okButton = new JButton("OK", Icons.getOkIcon());
    private JButton cancelButton = new JButton("Cancel", Icons.getCancelIcon());

    private JLabel relationsListLabel = new JLabel("Choose Reified Relation:");
    private JList relationsList = new JList();
    private ArrayList relations = new ArrayList();
    private HashMap relationsMap = new HashMap();
    private Cls chosenRelation = null;

    public ChooseRelationDialog() {
        this(null, "", false, new ArrayList());
    }

    public ChooseRelationDialog(Frame frame, String title, boolean modal,
                                ArrayList relations) {
        super(frame, title, modal);
        try {
            this.relations = relations;

            // Keep a map of cls names and the actual Cls objects.
            for (int i=0; i<relations.size(); i++) {
                Cls cls = (Cls) relations.get(i);
                String clsName = cls.getName();
                relationsMap.put(clsName, cls);
            }

            jbInit();
            pack();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        // Build relations panel.
        initList();
        JScrollPane scrollPane = new JScrollPane(relationsList);
        scrollPane.setPreferredSize(new Dimension(350, 100));
        scrollPane.setMinimumSize(new Dimension(350, 100));
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);

        relationsPanel.setLayout(new BoxLayout(relationsPanel,
                                               BoxLayout.Y_AXIS));
        relationsListLabel.setLabelFor(relationsList);
        relationsPanel.add(relationsListLabel);
        relationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        relationsPanel.add(scrollPane);
        relationsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10,
                                                                 10, 10));

        // Build button panel.
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButton_actionPerformed(e);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButton_actionPerformed(e);
            }
        });

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);

        // Build main panel.
        mainPanel.add(relationsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
    }

    private void initList() {
        DefaultListModel model = new DefaultListModel();
        for (int i=0; i<relations.size(); i++) {
            Cls cls = (Cls) relations.get(i);
            model.addElement(cls.getName());
        }
        relationsList.setModel(model);
        relationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        relationsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = relationsList.locationToIndex(e.getPoint());
                    setRelationAndClose(index);
                }
            }
        });

        if (!model.isEmpty()) {
            relationsList.setSelectedIndex(0);
        }
    }

    void okButton_actionPerformed(ActionEvent e) {
        int index = relationsList.getSelectedIndex();
        setRelationAndClose(index);
    }

    void cancelButton_actionPerformed(ActionEvent e) {
        chosenRelation = null;
        setVisible(false);
    }

    private void setRelationAndClose(int index) {
        DefaultListModel model = (DefaultListModel) relationsList.getModel();
        String clsName = (String) model.getElementAt(index);
        chosenRelation = (Cls) relationsMap.get(clsName);
        setVisible(false);
    }

    public Cls getRelation() {
        return chosenRelation;
    }
}