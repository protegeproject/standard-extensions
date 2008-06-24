package edu.stanford.smi.protegex.widget.graph;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.PropertyList;

/**
 * Allows a user to choose which slot for a particular class will hold
 * reified relations and configure the appearance of the relations.
 *
 * @author Jennifer Vendetti
 */
public class RelationConfigurationPanel extends JPanel {
    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel centerPanel = new JPanel(new BorderLayout());
    JPanel southPanel = new JPanel(new BorderLayout());
    JPanel southTopPanel = new JPanel(new GridBagLayout());
    JPanel southBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JLabel label1 = new JLabel("Relation Slots:  ");
    JLabel label2 = new JLabel("Line Color:");
    JLabel label3 = new JLabel("Line Type:");
    JLabel label4 = new JLabel("Arrowhead Type:");

    JComboBox rrSlotList;
    JComboBox lineColorList = new JComboBox();
    JComboBox lineCombo = new JComboBox();
    JComboBox arrowheadCombo = new JComboBox();

    JCheckBox showBrowserText = new JCheckBox("Show Browser Text");

    DefaultTableModel model;
    JTable allowedClsesTable;
    JScrollPane scrollPane;

    PropertyList propertyList;

    // Cls whose slot we are diagramming.
    private Cls cls;

    // Key: Slot name from "Relation Slots:" combo box.
    // Value: Slot object.
    HashMap relationSlots = new HashMap();

    // Key: Class name for a reified relation.
    // Value: RelationProperties object.
    HashMap relations = new HashMap();

    // Keep track of whether or not the class has any slots that hold
    // reified relations.
    private boolean relationsExist = true;

    public RelationConfigurationPanel(Cls cls, PropertyList propertyList) {
        this.cls = cls;
        this.propertyList = propertyList;

        // See if there are any reified relations in this project.  If there
        // aren't, just show a message to that effect.
        KnowledgeBase kb = cls.getKnowledgeBase();
        Cls metacls = kb.getCls(Model.Cls.DIRECTED_BINARY_RELATION);
        Collection subClses = metacls.getSubclasses();

        try {
            if (subClses.size() == 0) {
                jbInit_1();
            } else {
                jbInit_2();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit_1() throws Exception {
        Font font = new Font("Dialog", Font.PLAIN, 16);
        JTextArea text = new JTextArea("The " + cls.getName() + " class has no slots that contain reified relations (subclasses of :DIRECTED-BINARY-RELATION)");
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setBackground(getBackground());
        text.setFont(font);

        mainPanel.add(text);
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        relationsExist = false;
    }

    private void jbInit_2() throws Exception {
        /* Build south top panel **********************************************/
        lineCombo.addItem("Solid");
        lineCombo.addItem("Dashed");
        lineCombo.addItem("Dotted");
        lineCombo.addItem("Dash Dot");
        lineCombo.addItem("Dash Dot Dot");
        lineCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lineCombo_actionPerformed(e);
            }
        });

        lineColorList.addItem(Color.blue);
        lineColorList.addItem(Color.cyan);
        lineColorList.addItem(Color.darkGray);
        lineColorList.addItem(Color.gray);
        lineColorList.addItem(Color.lightGray);
        lineColorList.addItem(Color.green);
        lineColorList.addItem(Color.magenta);
        lineColorList.addItem(Color.orange);
        lineColorList.addItem(Color.pink);
        lineColorList.addItem(Color.red);
        lineColorList.addItem(Color.yellow);
        lineColorList.addItem(Color.white);
        lineColorList.addItem(Color.black);
        lineColorList.setRenderer(new ComboBoxColorRenderer());
        lineColorList.setEditor(new ComboBoxColorEditor());
        lineColorList.setEditable(true);
        lineColorList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lineColorList_actionPerformed(e);
            }
        });

        arrowheadCombo.addItem("Arrowhead");
        arrowheadCombo.addItem("Diamond");
        arrowheadCombo.addItem("Simple Line Drawn");
        arrowheadCombo.addItem("Triangle");
        arrowheadCombo.addItem(GraphTypes.NONE);
        arrowheadCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                arrowheadCombo_actionPerformed(e);
            }
        });

        southTopPanel.add(label2, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
        southTopPanel.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
        southTopPanel.add(label4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        southTopPanel.add(lineColorList, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        southTopPanel.add(lineCombo, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        southTopPanel.add(arrowheadCombo, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        /* Build south bottom panel *******************************************/
        showBrowserText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showBrowserText_actionPerformed(e);
            }
        });
        southBottomPanel.add(showBrowserText);

        /* Build south panel **************************************************/
        southPanel.add(southTopPanel, BorderLayout.WEST);
        southPanel.add(southBottomPanel, BorderLayout.SOUTH);

        /* Build center panel *************************************************/
        initializeTable();
        scrollPane = new JScrollPane(allowedClsesTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        /* Build north panel **************************************************/

        // Populate "Relation Slots:" combo box.
        initSlotListCombo();

        rrSlotList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rrSlotList_actionPerformed(e);
            }
        });

        String slotForRelations = propertyList.getString("slotForRelations");
        if (slotForRelations == null) {
            rrSlotList.setSelectedIndex(0);
        } else {
            rrSlotList.setSelectedItem(slotForRelations);
        }

        northPanel.add(label1);
        northPanel.add(rrSlotList);

        /* Build main panel ***************************************************/
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
    }

    private void initSlotListCombo() {
        TreeSet slotList = new TreeSet();
        Collection templateSlots = cls.getTemplateSlots();
        Iterator i = templateSlots.iterator();
        while (i.hasNext()) {
            Slot templateSlot = (Slot) i.next();
            if (cls.getTemplateSlotValueType(templateSlot) == ValueType.INSTANCE) {

                Collection allowedClses = cls.getTemplateSlotAllowedClses(templateSlot);
                Iterator j = allowedClses.iterator();
                while (j.hasNext()) {
                    Cls allowedCls = (Cls) j.next();
                    if ((allowedCls.isConcrete() || hasConcreteChildren(allowedCls)) && hasRelationSuperclass(allowedCls)) {
                        slotList.add(templateSlot.getName());

                        // Map slot name in combo box to actual Slot object.
                        relationSlots.put(templateSlot.getName(), templateSlot);
                    }
                }
            }
        }

        Vector v = new Vector(slotList);
        Collections.sort(v);
        v.add(0, GraphTypes.NONE);
        rrSlotList = new JComboBox(v);
    }

    private void initializeTable() {
        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        model.addColumn("Class");
        model.addColumn("Line Type");
        model.addColumn("Line Color");
        model.addColumn("Arrowhead Type");
        model.addColumn("Show Browser Text");

        allowedClsesTable = new JTable(model);

        ListSelectionModel rowSM = allowedClsesTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged(e);
            }
        });

        allowedClsesTable.setPreferredScrollableViewportSize(new
                Dimension(500, 200));

        TableCellRenderer cellRenderer = new TableColorRenderer(true);
        TableColumn colorColumn = allowedClsesTable.getColumn("Line Color");
        colorColumn.setCellRenderer(cellRenderer);

        // Don't allow drag and drop of columns.
        allowedClsesTable.getTableHeader().setReorderingAllowed(false);
    }

    void tableSelectionChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        saveCurrentSettings();

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        int selectedRow = lsm.getMinSelectionIndex();
        if (selectedRow > -1) {
            String clsName = (String) allowedClsesTable.getValueAt(selectedRow, 0);
            RelationProperties rProps = (RelationProperties) relations.get(clsName);
            lineColorList.setSelectedItem(rProps.getLineColor());
            lineCombo.setSelectedItem(rProps.getLineType());
            arrowheadCombo.setSelectedItem(rProps.getArrowheadType());
            showBrowserText.setSelected(rProps.isTextDisplayed());
        }
    }

    private void saveCurrentSettings() {
        int rowCount = allowedClsesTable.getRowCount();
        for (int i=0; i<rowCount; i++) {
            String clsName = (String) allowedClsesTable.getValueAt(i, 0);
            String lineType = (String) allowedClsesTable.getValueAt(i, 1);
            Color lineColor = (Color) allowedClsesTable.getValueAt(i, 2);
            String arrowheadType = (String) allowedClsesTable.getValueAt(i, 3);
            String showText = (String) allowedClsesTable.getValueAt(i, 4);
            boolean b = (showText.equals("True")) ? true : false;

            RelationProperties rProps = (RelationProperties) relations.get(clsName);
            rProps.setLineType(lineType);
            rProps.setLineColor(lineColor);
            rProps.setArrowheadType(arrowheadType);
            rProps.setDisplayText(b);
        }
    }

    public void saveContents() {
        saveCurrentSettings();

        Set keySet = relations.keySet();
        Iterator i = keySet.iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            RelationProperties rProps = (RelationProperties) relations.get(key);
            rProps.save();
        }

        propertyList.setString("slotForRelations",
                               (String) rrSlotList.getSelectedItem());
    }

    public boolean hasReifiedRelations() {
        return relationsExist;
    }

    void disableBottomPanel() {
        label2.setEnabled(false);
        lineColorList.setEnabled(false);
        lineColorList.setSelectedItem(Color.black);
        label3.setEnabled(false);
        lineCombo.setEnabled(false);
        label4.setEnabled(false);
        arrowheadCombo.setEnabled(false);
        showBrowserText.setEnabled(false);
    }

    void enableBottomPanel() {
        label2.setEnabled(true);
        lineColorList.setEnabled(true);
        label3.setEnabled(true);
        lineCombo.setEnabled(true);
        label4.setEnabled(true);
        arrowheadCombo.setEnabled(true);
        showBrowserText.setEnabled(true);
    }

    void rrSlotList_actionPerformed(ActionEvent e) {
        // The user selected a new slot to hold reified relations from the
        // "Relation Slots:" combo box.

        saveCurrentSettings();

        // Clear the contents of the table.
        model.setRowCount(0);

        String selection = (String) rrSlotList.getSelectedItem();
        if (selection.equals(GraphTypes.NONE)) {
            disableBottomPanel();
        } else {
            enableBottomPanel();

            // Re-populate the table with the allowed classes for the slot that was
            // selected in the combo box.
            Slot slot = (Slot) relationSlots.get(selection);
            if (slot != null) {
                ArrayList allowedClses = new ArrayList(getConcreteAllowedClses(slot));
                Collections.sort(allowedClses, new ClsComparator());
                for (int i = 0; i < allowedClses.size(); i++) {
                    Cls allowedCls = (Cls) allowedClses.get(i);

                    // See if we've fetched the properties for this
                    // relation before.
                    String clsName = allowedCls.getName();
                    RelationProperties props = (RelationProperties) relations.get(clsName);
                    if (props == null) {
                        props = new RelationProperties(clsName, propertyList);
                        relations.put(clsName, props);
                    }

                    Color lineColor = props.getLineColor();
                    String lineType = props.getLineType();
                    String arrowheadType = props.getArrowheadType();
                    String displayText = props.isTextDisplayed() ? "True" : "False";
                    model.addRow(new Object[]{clsName, lineType, lineColor, arrowheadType, displayText});
                }
                allowedClsesTable.setRowSelectionInterval(0, 0);
            }
        }
    }

    void lineColorList_actionPerformed(ActionEvent e) {
        Color lineColor = (Color) lineColorList.getSelectedItem();
        int row = allowedClsesTable.getSelectedRow();
        if (row > -1) {
            allowedClsesTable.setValueAt(lineColor, row, 2);
        }
    }

    void lineCombo_actionPerformed(ActionEvent e) {
        String lineType = (String) lineCombo.getSelectedItem();
        int row = allowedClsesTable.getSelectedRow();
        if (row > -1) {
            allowedClsesTable.setValueAt(lineType, row, 1);
        }
    }

    void arrowheadCombo_actionPerformed(ActionEvent e) {
        String arrowheadType = (String) arrowheadCombo.getSelectedItem();
        int row = allowedClsesTable.getSelectedRow();
        if (row > -1) {
            allowedClsesTable.setValueAt(arrowheadType, row, 3);
        }
    }

    void showBrowserText_actionPerformed(ActionEvent e) {
        String showText = showBrowserText.isSelected() ? "True" : "False";
        int row = allowedClsesTable.getSelectedRow();
        if (row > -1) {
            allowedClsesTable.setValueAt(showText, row, 4);
        }
    }

    private TreeSet getConcreteAllowedClses(Slot slot) {
        TreeSet allowedClses = new TreeSet();
        Collection c = cls.getTemplateSlotAllowedClses(slot);
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            if (cls.isConcrete()) {
                allowedClses.add(cls);
            }

            Collection sc = cls.getSubclasses();
            Iterator j = sc.iterator();
            while (j.hasNext()) {
                Cls subCls = (Cls) j.next();
                if (subCls.isConcrete()) {
                    allowedClses.add(subCls);
                }
            }
        }
        return allowedClses;
    }

    private boolean hasRelationSuperclass(Cls cls) {
        KnowledgeBase kb = this.cls.getKnowledgeBase();
        Cls dbrCls = kb.getCls(Model.Cls.DIRECTED_BINARY_RELATION);
        return (cls.hasSuperclass(dbrCls)) ? true : false;
    }

    private boolean hasConcreteChildren(Cls cls) {
        Collection concreteSubClses = cls.getConcreteSubclasses();
        return (concreteSubClses.size() > 0) ? true : false;
    }
}
