package edu.stanford.smi.protegex.widget.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.PropertyList;

public class NodeConfigurationPanel extends JPanel {
    private static final long serialVersionUID = 498531733601866872L;
    JCheckBox italic = new JCheckBox("Italic");
    JCheckBox bold = new JCheckBox("Bold");
    JCheckBox displayText = new JCheckBox("Display text for connectors");

    JComboBox shapeList = new JComboBox();
    JComboBox shapeColorList = new JComboBox();
    JComboBox textColorList = new JComboBox();
    JComboBox connectorSlots = new JComboBox();
    JComboBox lineList = new JComboBox();
    JComboBox arrowheadList = new JComboBox();

    JLabel displayNameLabel = new JLabel("Display Name:");
    JLabel jLabel1 = new JLabel("Shape:");
    JLabel jLabel2 = new JLabel("Shape Color:");
    JLabel jLabel3 = new JLabel("Text Color");
    JLabel jLabel4 = new JLabel("Line Type:");
    JLabel jLabel5 = new JLabel("Arrowhead Type:");
    JLabel jLabel6 = new JLabel("Connector Slot:");
    JLabel lblStrut = new JLabel();

    JPanel topPnl = new JPanel(new BorderLayout());
    JPanel bottomPnl = new JPanel(new BorderLayout());
    JPanel nodePnl = new JPanel(new GridBagLayout());
    JPanel connectorPnl = new JPanel(new GridBagLayout());

    JTable clsesTable = new JTable();
	JTextField displayNameTextField = new JTextField();

    private int lastSelectedIndex = -1;
    PropertyList propertyList;

    // Keep a mapping of the allowed class names and their corresponding
    // Cls objects for convenience.
    HashMap allowedClsMap = new HashMap();

    // Keep an array of NodeProperties objects to store configuration info
    // for each allowed class.  This info gets saved to the diagram widget's
    // PropertyList.
    ArrayList allowedClsProperties = new ArrayList();
    
    class ClsTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 8445278922388372474L;
        final String[] columnNames = { "Class", "Shape", "Shape Color" };
        Object[][] data = new Object[allowedClsMap.size()][3];

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        @SuppressWarnings("unchecked")
    	public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 2) {
                return true;
            } else {
                return false;
            }
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }
    
    @SuppressWarnings("unchecked")
	public NodeConfigurationPanel(ArrayList allowedClses, PropertyList propertyList) {
        try {
            this.propertyList = propertyList;

            if (allowedClses != null) {
                for (int i = 0; i < allowedClses.size(); i++) {
                    Cls cls = (Cls) allowedClses.get(i);
                    if (cls.isConcrete()) {
                        allowedClsMap.put(cls.getName(), cls);
                        
                        String clsName = cls.getName();
                        String browserText = cls.getBrowserText();
                        NodeProperties props = new NodeProperties(clsName, browserText, propertyList);
                        allowedClsProperties.add(props);
                    }

                    ArrayList subClses = new ArrayList(cls.getSubclasses());
                    for (int j = 0; j < subClses.size(); j++) {
                        Cls subCls = (Cls) subClses.get(j);
                        if (subCls.isConcrete()) {
                            allowedClsMap.put(subCls.getName(), subCls);
                            allowedClsProperties.add(new NodeProperties(subCls.getName(), 
                            	subCls.getBrowserText(), propertyList));
                        }
                    }
                }
            }

            Collections.sort(allowedClsProperties, new GraphObjectComparator());

            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        /** Build node panel */
        buildNodePanel();

        /** Build connector panel */
        buildConnectorPanel();

        /** Build top panel */
        initializeTable();
        if (clsesTable.getRowCount() > 0) {
            clsesTable.setRowSelectionInterval(0, 0);
        }
        JScrollPane scrollPane = new JScrollPane(clsesTable);
        topPnl.add(scrollPane, BorderLayout.CENTER);

        /** Build bottom panel */
        bottomPnl.add(nodePnl, BorderLayout.NORTH);
        bottomPnl.add(connectorPnl, BorderLayout.SOUTH);

        /** Build main panel */
        this.setLayout(new BorderLayout());
        this.add(topPnl, BorderLayout.CENTER);
        this.add(bottomPnl, BorderLayout.SOUTH);
    }

    private void buildNodePanel() {
		shapeList.addItem(GraphTypes.DIAMOND);
		shapeList.addItem(GraphTypes.ELLIPSE);
		shapeList.addItem(GraphTypes.HEXAGON);
		shapeList.addItem(GraphTypes.INVERTED_TRIANGLE);
		shapeList.addItem(GraphTypes.OCTAGON);
		shapeList.addItem(GraphTypes.PENTAGON);
		shapeList.addItem(GraphTypes.RECTANGLE);
		shapeList.addItem(GraphTypes.ROUNDED_RECTANGLE);
		shapeList.addItem(GraphTypes.TRIANGLE);
		shapeList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	shapeList_actionPerformed(e);
		  	}
		});

		shapeColorList = new JComboBox();
		// This is the default color for nodes ("SteelBlue").
		shapeColorList.addItem(new Color(70, 130, 180));
		shapeColorList.addItem(Color.blue);
		shapeColorList.addItem(Color.cyan);
		shapeColorList.addItem(Color.darkGray);
		shapeColorList.addItem(Color.gray);
		shapeColorList.addItem(Color.lightGray);
		shapeColorList.addItem(Color.green);
		shapeColorList.addItem(Color.magenta);
		shapeColorList.addItem(Color.orange);
		shapeColorList.addItem(Color.pink);
		shapeColorList.addItem(Color.red);
		shapeColorList.addItem(Color.yellow);
		shapeColorList.addItem(Color.white);
		shapeColorList.addItem(Color.black);
		shapeColorList.setRenderer(new ComboBoxColorRenderer());
		shapeColorList.setEditor(new ComboBoxColorEditor());
		shapeColorList.setEditable(true);
		shapeColorList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	shapeColorList_actionPerformed(e);
		  	}
		});

		textColorList = new JComboBox();
		textColorList.addItem(Color.black);
		textColorList.addItem(Color.blue);
		textColorList.addItem(Color.cyan);
		textColorList.addItem(Color.darkGray);
		textColorList.addItem(Color.gray);
		textColorList.addItem(Color.lightGray);
		textColorList.addItem(Color.green);
		textColorList.addItem(Color.magenta);
		textColorList.addItem(Color.orange);
		textColorList.addItem(Color.pink);
		textColorList.addItem(Color.red);
		textColorList.addItem(Color.yellow);
		textColorList.addItem(Color.white);
		textColorList.setRenderer(new ComboBoxColorRenderer());
		textColorList.setEditor(new ComboBoxColorEditor());
		textColorList.setEditable(true);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(10,5,5,5);
        c.anchor = GridBagConstraints.WEST;
        nodePnl.add(displayNameLabel, c);

		nodePnl.add(jLabel1, new GridBagConstraints(0,1,1,1,0.0,0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));
		nodePnl.add(jLabel2, new GridBagConstraints(0,2,1,1,0.0,0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));
		nodePnl.add(jLabel3, new GridBagConstraints(0,3,1,1,0.0,0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));

		c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        nodePnl.add(displayNameTextField, c);

		nodePnl.add(shapeList, new GridBagConstraints(1,1,1,1,0.0,0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0));
		nodePnl.add(shapeColorList, new GridBagConstraints(1,2,1,1,0.0,0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0));
		nodePnl.add(textColorList, new GridBagConstraints(1,3,1,1,0.0,0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0));
		nodePnl.add(italic, new GridBagConstraints(3,3,1,1,1.0,0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0));
		nodePnl.add(bold, new GridBagConstraints(2,3,1,1,0.0,0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));
	}

	private void buildConnectorPanel() {
		lineList.addItem(GraphTypes.SOLID);
		lineList.addItem(GraphTypes.DASHED);
		lineList.addItem(GraphTypes.DOTTED);
		lineList.addItem(GraphTypes.DASH_DOT);
		lineList.addItem(GraphTypes.DASH_DOT_DOT);

		arrowheadList.addItem(GraphTypes.ARROW_ARROWHEAD);
		arrowheadList.addItem(GraphTypes.ARROW_DIAMOND);
		arrowheadList.addItem(GraphTypes.ARROW_SIMPLE_LINE_DRAWN);
		arrowheadList.addItem(GraphTypes.ARROW_TRIANGLE);
		arrowheadList.addItem(GraphTypes.NONE);

		lblStrut.setText("");

		connectorPnl.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white, 
				new Color(148, 145, 140)), "Optional Connector Slot"));
		
		connectorPnl.add(jLabel4, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		connectorPnl.add(jLabel5, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		connectorPnl.add(jLabel6, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		connectorPnl.add(connectorSlots, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		
		connectorPnl.add(arrowheadList, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		
		connectorPnl.add(lineList, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		
		connectorPnl.add(lblStrut, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		connectorPnl.add(displayText, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	}

    private void setUpTableColorRenderer(JTable table) {
        TableCellRenderer cellRenderer = new TableColorRenderer(true);
        TableColumn colorColumn = table.getColumn("Shape Color");
        colorColumn.setCellRenderer(cellRenderer);
    }

    private void initializeTable() {
        ClsTableModel model = new ClsTableModel();
        clsesTable = new JTable(model);
        clsesTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
        clsesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setUpTableColorRenderer(clsesTable);
        clsesTable.getTableHeader().setReorderingAllowed(false);

        ListSelectionModel rowSM = clsesTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged(e);
            }
        });

        // Fill table with the list of allowed classes and their properties.
        for (int i = 0; i < allowedClsProperties.size(); i++) {
        	NodeProperties properties = (NodeProperties) allowedClsProperties.get(i);
            Cls allowedCls = (Cls) allowedClsMap.get(properties.getClsName());
            clsesTable.setValueAt(new FrameData(allowedCls), i, 0);
            clsesTable.setValueAt(properties.getShape(), i, 1);
            clsesTable.setValueAt(properties.getShapeColor(), i, 2);
        }
    }

    public void saveContents() {
        saveCurrentSettings(lastSelectedIndex);
        for (int i = 0; i < allowedClsProperties.size(); i++) {
            NodeProperties properties = (NodeProperties) allowedClsProperties.get(i);
            properties.save();
        }
    }

    private void saveCurrentSettings(int index) {
        NodeProperties props = (NodeProperties) allowedClsProperties.get(index);
        
        props.setCustomDisplayName((displayNameTextField.getText()).trim());
        props.setShape((String) shapeList.getSelectedItem());
        props.setShapeColor((Color) shapeColorList.getSelectedItem());
        props.setTextColor((Color) textColorList.getSelectedItem());
        props.setBold(bold.isSelected());
        props.setItalic(italic.isSelected());
        
        Object obj = connectorSlots.getSelectedItem();
        if (obj.equals(GraphTypes.NONE)) {
        	props.setConnectorSlot(GraphTypes.NONE);
        } else if (obj instanceof FrameData) {
        	FrameData slotData = (FrameData) obj;
			props.setConnectorSlot(slotData.getFullName());
        }
        
        props.setLineType((String) lineList.getSelectedItem());
        props.setArrowheadType((String) arrowheadList.getSelectedItem());
        props.setDisplayText(displayText.isSelected());
    }

    @SuppressWarnings("unchecked")
	void tableSelectionChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;

        if (lastSelectedIndex != -1) {
            saveCurrentSettings(lastSelectedIndex);
        }

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        int selectedIndex = lsm.getMinSelectionIndex();
        NodeProperties props = (NodeProperties) allowedClsProperties.get(selectedIndex);

        // Repopulate the "Connector Slots:" combo box.
        connectorSlots.removeAllItems();
        connectorSlots.addItem(GraphTypes.NONE);
        
        FrameData data = (FrameData) clsesTable.getValueAt(selectedIndex, 0);
        Cls cls = (Cls) allowedClsMap.get(data.getFullName());
        ArrayList templateSlots = new ArrayList(cls.getTemplateSlots());
        for (int i = 0; i < templateSlots.size(); i++) {
            Slot templateSlot = (Slot) templateSlots.get(i);
            if ((templateSlot.getValueType() == ValueType.INSTANCE)
            		&& (!templateSlot.isSystem())) {
            	FrameData slotData = new FrameData(templateSlot);
                connectorSlots.addItem(slotData);
            }
        }

        // Update the UI based on the saved properties for the
        // selected class.
        String connectorSlot = props.getConnectorSlot();
        if (connectorSlot != null) {
        	Slot slot = (Slot) cls.getKnowledgeBase().getSlot(connectorSlot);
        	if (slot != null) {
				FrameData slotData = new FrameData(slot);
				connectorSlots.setSelectedItem(slotData);
			}
        	else {
                connectorSlots.setSelectedItem(GraphTypes.NONE);
        	}
        } else {
            connectorSlots.setSelectedItem(GraphTypes.NONE);
        }

		displayNameTextField.setText(props.getCustomDisplayName());
        shapeList.setSelectedItem(props.getShape());
        shapeColorList.setSelectedItem(props.getShapeColor());
        textColorList.setSelectedItem(props.getTextColor());
        bold.setSelected(props.isBold());
        italic.setSelected(props.isItalic());
        lineList.setSelectedItem(props.getLineType());
        arrowheadList.setSelectedItem(props.getArrowheadType());
        displayText.setSelected(props.isTextDisplayed());

        lastSelectedIndex = selectedIndex;
    }

    void shapeList_actionPerformed(ActionEvent e) {
        String shape = (String) shapeList.getSelectedItem();
        int row = clsesTable.getSelectedRow();
        clsesTable.setValueAt(shape, row, 1);
    }

    void shapeColorList_actionPerformed(ActionEvent e) {
        Color shapeColor = (Color) shapeColorList.getSelectedItem();
        int row = clsesTable.getSelectedRow();
        clsesTable.setValueAt(shapeColor, row, 2);
    }
}
