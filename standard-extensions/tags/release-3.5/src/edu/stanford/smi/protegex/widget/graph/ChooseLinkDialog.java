package edu.stanford.smi.protegex.widget.graph;

// java
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

// stanford
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.resource.Icons;

/**
 * @author Jennifer Vendetti
 */
public class ChooseLinkDialog extends JDialog {
    private static final long serialVersionUID = -3146895752548347363L;
    private JPanel mainPanel = new JPanel();
    private JPanel linkTypePanel = new JPanel(new GridBagLayout());
    private JPanel buttonPanel = new JPanel();

    private JButton okButton = new JButton("OK", Icons.getOkIcon());
    private JButton cancelButton = new JButton("Cancel", Icons.getCancelIcon());

    private ButtonGroup group = new ButtonGroup();
    private JRadioButton connectorButton = new JRadioButton("Connector Slot:");
    private JRadioButton relationButton = new JRadioButton("Reified Relation");

    private JLabel slotNameLabel = new JLabel();
    private JList relationsList = new JList();
    private ArrayList relations = new ArrayList();
    private HashMap relationsMap = new HashMap();
    private Slot connectorSlot = null;
    private Object linkType = null;

    public ChooseLinkDialog() {
        this(null, "", true, null, new ArrayList());
    }

    public ChooseLinkDialog(Frame owner, String title, boolean modal,
                            Slot connectorSlot, ArrayList relations) {
        super(owner, title, modal);
        try {
            this.connectorSlot = connectorSlot;
            this.relations = relations;
            jbInit();
            pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {

        /* build button panel *************************************************/
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButton_actionPerformed(e);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                linkType = null;
                setVisible(false);
            }
        });

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);

        /* build link type panel **********************************************/
        group.add(connectorButton);
        group.add(relationButton);

        // Default to having connector slot radio button selected.
        connectorButton.setSelected(true);

        slotNameLabel.setText(connectorSlot.getBrowserText());

        initList(relations);
        JScrollPane scrollPane = new JScrollPane(relationsList);
        scrollPane.setPreferredSize(new Dimension(350, 100));
        scrollPane.setMinimumSize(new Dimension(350, 100));
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);

        linkTypePanel.add(relationButton,
                          new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                          GridBagConstraints.CENTER, GridBagConstraints.NONE,
                          new Insets(2, 5, 2, 0), 0, 0));
        linkTypePanel.add(scrollPane,
                          new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0,
                          GridBagConstraints.WEST, GridBagConstraints.BOTH,
                          new Insets(0, 10, 5, 10), 0, 0));
        linkTypePanel.add(connectorButton,
                          new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                          GridBagConstraints.CENTER, GridBagConstraints.NONE,
                          new Insets(5, 5, 0, 0), 0, 0));
        linkTypePanel.add(slotNameLabel,
                          new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                          GridBagConstraints.WEST, GridBagConstraints.NONE,
                          new Insets(5, 0, 0, 5), 0, 0));

        /* build main panel ***************************************************/
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(linkTypePanel);
        mainPanel.add(buttonPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private void initList(ArrayList listData) {
        DefaultListModel model = new DefaultListModel();
        for (int i=0; i<listData.size(); i++) {
            Cls cls = (Cls) listData.get(i);
            String clsName = cls.getName();
            model.addElement(clsName);

            // Keep a map of cls names and the actual Cls objects.
            relationsMap.put(clsName, cls);
        }

        relationsList.setModel(model);
        relationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        relationsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = relationsList.locationToIndex(e.getPoint());
                    setLinkTypeAndClose(index);
                }
            }
        });

        if (!model.isEmpty()) {
            relationsList.setSelectedIndex(0);
        }
    }

    private void setLinkTypeAndClose(int index) {
        DefaultListModel model = (DefaultListModel) relationsList.getModel();
        String clsName = (String) model.getElementAt(index);
        linkType = (Cls) relationsMap.get(clsName);
        setVisible(false);
    }

    void okButton_actionPerformed(ActionEvent e) {
        if (connectorButton.isSelected()) {
            linkType = connectorSlot;
        } else if (relationButton.isSelected()) {
            DefaultListModel model = (DefaultListModel) relationsList.getModel();
            int index = relationsList.getSelectedIndex();
            String clsName = (String) model.getElementAt(index);
            linkType = (Cls) relationsMap.get(clsName);
        }

        setVisible(false);
    }

    public Object getLinkType() {
        return linkType;
    }
}