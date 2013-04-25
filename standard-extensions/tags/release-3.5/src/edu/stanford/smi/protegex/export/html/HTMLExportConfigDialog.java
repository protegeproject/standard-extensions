package edu.stanford.smi.protegex.export.html;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.DisplayUtilities;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.AddAction;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.FileField;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.util.RemoveAction;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protege.util.SystemUtilities;

/**
 *
 * @author Jennifer Vendetti
 */
public class HTMLExportConfigDialog extends JDialog {
    private static final long serialVersionUID = 6135579028602268526L;
    JButton cancelButton = new JButton("Cancel", Icons.getCancelIcon());
    JButton checkAllFacetsButton = new JButton("Check All");
    JButton checkAllSlotsButton = new JButton("Check All");
    JButton clearAllFacetsButton = new JButton("Clear All");
    JButton clearAllSlotsButton = new JButton("Clear All");
    JButton deleteButton = new JButton("Delete");
    JButton okButton = new JButton("OK", Icons.getOkIcon());
    JButton saveButton = new JButton("Save");
    JCheckBox numberInstancesCheckbox = new JCheckBox("Use numbering for instance lists");
    JCheckBox showInstancesCheckbox = new JCheckBox("Show Instances");
    JCheckBox sortSubclassesCheckbox = new JCheckBox("Sort Subclasses");
    JCheckBox useHierarchicalFoldersCheckbox = new JCheckBox("Use hierarchical folder structure"); // (MikeHewett) 12 Feb 2007
    JComboBox configNamesComboBox;
    JPanel configButtonPanel = new JPanel(new FlowLayout());
    JPanel customCodePanel = new JPanel();
    JPanel facetButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel facetsPanel = new JPanel(new BorderLayout(5, 5));
    JPanel generalPanel = new JPanel(new GridBagLayout());
    JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
    JPanel okCancelButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel slotButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel slotsPanel = new JPanel(new BorderLayout(5, 5));
    JLabel configNameLabel = new JLabel("Configuration Name:");
    JLabel facetsLabel = new JLabel("Select facets to display");
    JLabel headerLabel = new JLabel("Header:");
    JLabel slotsLabel = new JLabel("Select slots to display");
    JList facetList;
    JList slotList;
    JScrollPane facetsScrollPane = new JScrollPane();
    JScrollPane slotsScrollPane = new JScrollPane();
    JTabbedPane tabbedPane = new JTabbedPane();
    JTextField configNamesEditor;

    boolean okPressed;
    Document document;
    FileField cssComponent;
    FileField footerComponent;
    FileField headerComponent;
    FileField outputDirComponent;
    LabeledComponent classesComponent;
    Project project;
    SelectableList classesList;
    String prefix;

    public HTMLExportConfigDialog(java.awt.Frame owner, String title,
                                  boolean modal, Project project, File configFile)
        throws HeadlessException {
        super(owner, title, modal);
        this.project = project;
        this.prefix = configFile.getParent() + File.separator;

        try {
            // Read in configuration file.
            javax.xml.parsers.DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(configFile);

            initializeUI();
        }
        catch(Exception e) {
        	ModalDialog.showMessageDialog(this, "There were errors at export.\nSee console for more deatils.");
            Log.getLogger().log(Level.WARNING, "There were error at export", e);
       }
    }

    private void initializeUI() throws Exception {
        /**
         * Build OK & Cancel button panel
         */
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                okButton_actionPerformed(ae);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cancelButton_actionPerformed(ae);
            }
        });

        okCancelButtonPanel.add(okButton);
        okCancelButtonPanel.add(cancelButton);

        /**
         * Build slots panel
         */
        ComponentUtilities.setTitleLabelFont(slotsLabel);

        slotButtonPanel.add(checkAllSlotsButton);
        checkAllSlotsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                checkAllSlotsButton_actionPerformed(ae);
            }
        });

        slotButtonPanel.add(clearAllSlotsButton);
        clearAllSlotsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                clearAllSlotsButton_actionPerformed(ae);
            }
        });

        initSlotList();
        slotsScrollPane.getViewport().add(slotList);

        slotsPanel.add(slotsLabel, BorderLayout.NORTH);
        slotsPanel.add(slotsScrollPane, BorderLayout.CENTER);
        slotsPanel.add(slotButtonPanel, BorderLayout.SOUTH);
        slotsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        /**
         * Build facets panel
         */
        ComponentUtilities.setTitleLabelFont(facetsLabel);

        facetButtonPanel.add(checkAllFacetsButton);
        checkAllFacetsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                checkAllFacetsButton_actionPerformed(ae);
            }
        });

        facetButtonPanel.add(clearAllFacetsButton);
        clearAllFacetsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                clearAllFacetsButton_actionPerformed(ae);
            }
        });

        initFacetList();
        facetsScrollPane.getViewport().add(facetList);

        facetsPanel.add(facetsLabel, BorderLayout.NORTH);
        facetsPanel.add(facetsScrollPane, BorderLayout.CENTER);
        facetsPanel.add(facetButtonPanel, BorderLayout.SOUTH);
        facetsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        /**
         * Build general & customization panels
         */
        initConfigNamesComboBox();
    	configNamesComboBox.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent ie) {
            	configNamesComboBox_itemStateChanged(ie);
            }
    	});

        configNamesEditor = (JTextField) configNamesComboBox.getEditor().getEditorComponent();
        configNamesEditor.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent e) {
                configNamesEditor_keyReleased(e);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveButton_actionPerformed(e);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteButton_actionPerformed(e);
            }
        });

        initGeneralPanel();
        initCustomCodePanel();

        /**
         * Build tabbed pane
         */
        tabbedPane.add("General", generalPanel);
        tabbedPane.add("Slots", slotsPanel);
        tabbedPane.add("Facets", facetsPanel);
        tabbedPane.add("Customization", customCodePanel);

        /**
         * Build main panel
         */
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(okCancelButtonPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
    }

    private void initSlotList() {
        DefaultListModel model = new DefaultListModel();

        java.util.List slots = new ArrayList(project.getKnowledgeBase().getSlots());
        Collections.sort(slots, new FrameComparator());
        Iterator i = slots.iterator();
        while (i.hasNext()) {
            FrameData fdata;
            Slot slot = (Slot) i.next();
            String slotName = slot.getName();
            if (slotName.equals(Model.Slot.DIRECT_DOMAIN) ||
                slotName.equals(Model.Slot.DIRECT_INSTANCES) ||
                slotName.equals(Model.Slot.DIRECT_SUBCLASSES) ||
                slotName.equals(Model.Slot.DIRECT_SUBSLOTS) ||
                slotName.equals(Model.Slot.DIRECT_SUPERCLASSES) ||
                slotName.equals(Model.Slot.DIRECT_SUPERSLOTS) ||
                slotName.equals(Model.Slot.DIRECT_TEMPLATE_SLOTS) ||
                slotName.equals(Model.Slot.DIRECT_TYPES) ||
                slotName.equals(Model.Slot.DOCUMENTATION) ||
                slotName.equals(Model.Slot.NAME)) {
                fdata = new FrameData(slot.getBrowserText(), false);
            } else {
                fdata = new FrameData(slot.getBrowserText(), true);
            }
            model.addElement(fdata);
        }

        slotList = new JList(model);
        CheckListCellRenderer renderer = new CheckListCellRenderer();
        slotList.setCellRenderer(renderer);
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        CheckListener listener = new CheckListener(slotList);
        slotList.addMouseListener(listener);
        slotList.addKeyListener(listener);
    }

    private void initFacetList() {
        FrameData[] facets = {
            new FrameData("Cardinality", true),
            new FrameData("Default Values", false),
            new FrameData("Numeric Minimum & Maximum", false),
            new FrameData("Template Value", false),
            new FrameData("Value Type", true)
        };

        facetList = new JList(facets);
        CheckListCellRenderer renderer = new CheckListCellRenderer();
        facetList.setCellRenderer(renderer);
        facetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        CheckListener listener = new CheckListener(facetList);
        facetList.addMouseListener(listener);
        facetList.addKeyListener(listener);
    }

    private void initClassesList() {
        classesList = ComponentFactory.createSelectableList(null);
        classesList.setCellRenderer(FrameRenderer.createInstance());

        Collection values = new ArrayList();
        Cls cls = project.getKnowledgeBase().getRootCls();
        values.add(cls);
        ComponentUtilities.setListValues(classesList, values);

        // Bug - if you set the selected values before adding this list
        // to a labeled component, the header buttons on the labeled
        // components end up not working properly.
        //ComponentUtilities.setSelectedValue(classesList, cls);
    }

    private void initCustomCodePanel() {
        headerComponent = new FileField("Header:", prefix + "header.html",
                                        ".html", "HTML File");

        footerComponent = new FileField("Footer:", prefix + "footer.html",
                                        ".html", "HTML File");

        cssComponent = new FileField("Stylesheet:", prefix + "htmlexport.css",
                                     ".css", "Cascading Style Sheet File");

        JPanel innerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0; // fill up horizontal space
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        innerPanel.add(headerComponent, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0; // fill up horizontal space
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0); // top, left, bottom, right
        innerPanel.add(footerComponent, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0; // fill up horizontal space
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0); // top, left, bottom, right
        innerPanel.add(cssComponent, c);

        customCodePanel.setLayout(new BorderLayout());
        customCodePanel.add(innerPanel, BorderLayout.NORTH);
        customCodePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private void initGeneralPanel() {
        ComponentUtilities.setTitleLabelFont(configNameLabel);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 5, 0, 0); // top, left, bottom, right
        generalPanel.add(configNameLabel, c);

        // ComboBox with configuration names
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0; // fill horizontal space
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 0, 0); // top, left, bottom, right
        generalPanel.add(configNamesComboBox, c);

        // Button panel with Save and Delete buttons
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
        configButtonPanel.add(saveButton);
        configButtonPanel.add(deleteButton);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0; // fill horizontal space
        c.weighty = 0.0; // fill vertical space
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        generalPanel.add(configButtonPanel, c);

        // Output Directory component
        File dir = ApplicationProperties.getLastFileDirectory();        
        outputDirComponent = new FileField("Output Directory:", dir == null ? 
        		null : dir.getAbsolutePath(), "Output Directory");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1.0; // fill horizontal space
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        generalPanel.add(outputDirComponent, c);

        // Classes component
        initClassesList();
        classesComponent = new LabeledComponent("Root Classes", new JScrollPane(classesList));
        classesComponent.addHeaderButton(createAddClsAction());
        classesComponent.addHeaderButton(createRemoveClsAction());
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1.0; // fill horizontal space
        c.weighty = 1.0; // fill vertical space
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        generalPanel.add(classesComponent, c);

        // showInstances check box
        showInstancesCheckbox.setSelected(true);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0; // fill horizontal space
        c.weighty = 0.0; // fill vertical space
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 5, 5, 0);
        generalPanel.add(showInstancesCheckbox, c);

        // sortSubclasses check box
        sortSubclassesCheckbox.setSelected(true);
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0; // fill horizontal space
        c.weighty = 0.0; // fill vertical space
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 5, 5, 0);
        generalPanel.add(sortSubclassesCheckbox, c);

        // useHierarchicalFolders check box         (MikeHewett) 12 Feb 2007
        useHierarchicalFoldersCheckbox.setSelected(false);
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0; // fill horizontal space
        c.weighty = 0.0; // fill vertical space
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 5, 5, 0);
        generalPanel.add(useHierarchicalFoldersCheckbox, c);

		// numberInstances check box
        c.gridx = 0;
        c.gridy = 7;   // (MikeHewett) 12 Feb 2007  modified from 6 to 7 due to inserting a new element above.
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0; // fill horizontal space
        c.weighty = 0.0; // fill vertical space
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 5, 5, 0);
        generalPanel.add(numberInstancesCheckbox, c);

        generalPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private void initConfigNamesComboBox() {
        // Read configuration names from configuration file.  Add each
        // name to the combo box.
        Vector configNames = new Vector();
        try {
            NodeList results = XPathAPI.selectNodeList(document,
                "/html.export.configurations/configuration/name");
            for (int i = 0; i < results.getLength(); i++) {
                Node result = results.item(i);
                XObject value = XPathAPI.eval(result, "string()");
                configNames.add(value.str());
            }
        } catch (javax.xml.transform.TransformerException e) {
            System.out.println(e.getMessage());
        }

        Collections.sort(configNames, new AlphabeticComparator());
        configNames.add(0, "Protege Default");
        configNamesComboBox = new JComboBox(configNames);
        configNamesComboBox.setSelectedIndex(0);
        configNamesComboBox.setEditable(true);
    }

    public void clearAllFacetsButton_actionPerformed(ActionEvent ae) {
        int numItems = facetList.getModel().getSize();
        for (int i = 0; i < numItems; i++) {
            FrameData fdata = (FrameData) facetList.getModel().getElementAt(i);
            fdata.setSelected(false);
        }
        facetList.repaint();
    }

    public void checkAllFacetsButton_actionPerformed(ActionEvent ae) {
        int numItems = facetList.getModel().getSize();
        for (int i = 0; i < numItems; i++) {
            FrameData fdata = (FrameData) facetList.getModel().getElementAt(i);
            fdata.setSelected(true);
        }
        facetList.repaint();
    }

    public void clearAllSlotsButton_actionPerformed(ActionEvent ae) {
        int numItems = slotList.getModel().getSize();
        for (int i = 0; i < numItems; i++) {
            FrameData fdata = (FrameData) slotList.getModel().getElementAt(i);
            fdata.setSelected(false);
        }
        slotList.repaint();
    }

    public void checkAllSlotsButton_actionPerformed(ActionEvent ae) {
        int numItems = slotList.getModel().getSize();
        for (int i = 0; i < numItems; i++) {
            FrameData fdata = (FrameData) slotList.getModel().getElementAt(i);
            fdata.setSelected(true);
        }
        slotList.repaint();
    }

    public void okButton_actionPerformed(ActionEvent ae) {
        this.okPressed = true;
        setVisible(false);
    }

    public void cancelButton_actionPerformed(ActionEvent ae) {
        this.okPressed = false;
        setVisible(false);
    }

    public void configNamesComboBox_itemStateChanged(ItemEvent ie) {
        // Shortcut - don't care about deselection.
        if (ie.getStateChange() == ItemEvent.DESELECTED) {
			return;
		}

        String configName = (String) configNamesComboBox.getSelectedItem();
        if (configName == "Protege Default") {
            restoreDefaultSettings();
        } else {
            String xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]";
			try {
        		Node result = XPathAPI.selectSingleNode(document, xpath);
				// Workaround - since we are using an editable combo box, it's
            	// possible to get an item selected event for items that have
                // not yet been saved the to XML config file.  In this case,
                // we don't want to read settings out of the config file - just
                // allow user to continue working.
                if (result != null) {
                	populateFromConfigFile(configName);
                }
            } catch(javax.xml.transform.TransformerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void configNamesEditor_keyReleased(KeyEvent e) {
        String s = configNamesEditor.getText();
        if (s.equals("Protege Default") || s.length() == 0) {
            saveButton.setEnabled(false);
            deleteButton.setEnabled(false);
        } else {
            deleteButton.setEnabled(true);
            saveButton.setEnabled(true);
        }
    }

    public void saveButton_actionPerformed(ActionEvent ae) {
		// Overwrite existing configurations with the same name.
		String strConfigName = (String) configNamesComboBox.getSelectedItem();
        String xpath = "/html.export.configurations/configuration[name=\"" + strConfigName + "\"]";
        try {
            NodeList results = XPathAPI.selectNodeList(document, xpath);
            for (int i = 0; i < results.getLength(); i++) {
                Node result = results.item(i);
                Node parent = result.getParentNode();
                parent.removeChild(result);
            }
        } catch(javax.xml.transform.TransformerException e) {
        	System.out.println(e.getMessage());
        }

        org.w3c.dom.Text text;
        Node root = document.getDocumentElement();

		Node config = document.createElement("configuration");
        root.appendChild(config);

        Node configName = document.createElement("name");
        text = document.createTextNode(strConfigName);
        configName.appendChild(text);
        config.appendChild(configName);

        Node outputDir = document.createElement("output.dir");
        text = document.createTextNode(outputDirComponent.getFilePath().getPath());
        outputDir.appendChild(text);
        config.appendChild(outputDir);

        Node header = document.createElement("header");
        text = document.createTextNode(headerComponent.getFilePath().getPath());
        header.appendChild(text);
        config.appendChild(header);

        Node footer = document.createElement("footer");
        text = document.createTextNode(footerComponent.getFilePath().getPath());
        footer.appendChild(text);
        config.appendChild(footer);

        Node stylesheet = document.createElement("stylesheet");
        text = document.createTextNode(cssComponent.getFilePath().getPath());
        stylesheet.appendChild(text);
        config.appendChild(stylesheet);

        Node showInstances = document.createElement("show.instances");
        Boolean b = new Boolean(showInstancesCheckbox.isSelected());
        text = document.createTextNode(b.toString());
        showInstances.appendChild(text);
        config.appendChild(showInstances);

        Node sortSubclasses = document.createElement("sort.subclasses");
        b = new Boolean(sortSubclassesCheckbox.isSelected());
        text = document.createTextNode(b.toString());
        sortSubclasses.appendChild(text);
        config.appendChild(sortSubclasses);

        // (MikeHewett) 12 Feb 2007
        Node useHierarchicalFolders = document.createElement("hierarchical.folders");
        b = new Boolean(useHierarchicalFoldersCheckbox.isSelected());
        text = document.createTextNode(b.toString());
        useHierarchicalFolders.appendChild(text);
        config.appendChild(useHierarchicalFolders);

        Node useNumbering = document.createElement("use.numbering");
        b = new Boolean(numberInstancesCheckbox.isSelected());
        text = document.createTextNode(b.toString());
        useNumbering.appendChild(text);
        config.appendChild(useNumbering);

        Node classes = document.createElement("classes");
        config.appendChild(classes);
        ArrayList rootClasses = new ArrayList(ComponentUtilities.getListValues(classesList));
        Iterator iterator = rootClasses.iterator();
        while (iterator.hasNext()) {
            Cls cls = (Cls) iterator.next();
            Node classNode = document.createElement("class");
            text = document.createTextNode(cls.getName());
            classNode.appendChild(text);
            classes.appendChild(classNode);
        }

        Node slots = document.createElement("slots");
        config.appendChild(slots);
        int size = slotList.getModel().getSize();
        for (int i = 0; i < size; i++) {
            FrameData fdata = (FrameData) slotList.getModel().getElementAt(i);
            if (fdata.isSelected()) {
                Node slot = document.createElement("slot");
                text = document.createTextNode(fdata.getName());
                slot.appendChild(text);
                slots.appendChild(slot);
            }
        }

        Node facets = document.createElement("facets");
        config.appendChild(facets);
        size = facetList.getModel().getSize();
        for (int i = 0; i < size; i++) {
            FrameData fdata = (FrameData) facetList.getModel().getElementAt(i);
            if (fdata.isSelected()) {
                Node facet = document.createElement("facet");
                text = document.createTextNode(fdata.getName());
                facet.appendChild(text);
                facets.appendChild(facet);
            }
        }

        saveConfigurationFile();
        configNamesComboBox.addItem(configNamesEditor.getText());
    }

    public void deleteButton_actionPerformed(ActionEvent ae) {
		String configName = (String) configNamesComboBox.getSelectedItem();

        // Remove configuration from XML config file.
        try {
            Node root = document.getDocumentElement();
            String xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]";
            Node node = XPathAPI.selectSingleNode(root, xpath);
    		if (node != null) {
				Node parent = node.getParentNode();
                parent.removeChild(node);
                saveConfigurationFile();
            }
        } catch (javax.xml.transform.TransformerException e) {
            System.out.println(e.getMessage());
        }

        int index = configNamesComboBox.getSelectedIndex();
        if (index > -1) {
            configNamesComboBox.setSelectedIndex(index - 1);
            configNamesComboBox.removeItemAt(index);
        }
    }

    private Action createAddClsAction() {
        return new AddAction(ResourceKey.CLASS_ADD) {
            private static final long serialVersionUID = -3744951205356444658L;

            @Override
			public void onAdd() {
                Collection c = DisplayUtilities.pickClses(generalPanel,
                    project.getKnowledgeBase());
                if (!c.isEmpty()) {
                    ComponentUtilities.addListValues(classesList, c);
                }
            }
        };
    }

    private Action createRemoveClsAction() {
        return new RemoveAction(ResourceKey.CLASS_REMOVE, classesList) {
            private static final long serialVersionUID = 4188446620166264499L;

            @Override
			public void onRemove(Collection values) {
                ComponentUtilities.removeListValues(classesList, values);
            }
        };
    }

    public ExportConfiguration getExportConfiguration() {
        ExportConfiguration config = new ExportConfiguration();

	    config.setShowInstances(showInstancesCheckbox.isSelected());
        config.setSortSubclasses(sortSubclassesCheckbox.isSelected());
        config.setUseHierarchicalFolders(useHierarchicalFoldersCheckbox.isSelected());  // (MikeHewett) 12 Feb 2007
        config.setUseNumbering(numberInstancesCheckbox.isSelected());
        config.setOutputDir(outputDirComponent.getPath());
        config.setHeaderPath(headerComponent.getPath());
        config.setFooterPath(footerComponent.getPath());
        config.setCSSPath(cssComponent.getPath());
        config.setProject(project);

        ArrayList rootClasses = new ArrayList(ComponentUtilities.getListValues(classesList));
        config.setRootClasses(rootClasses);

        ArrayList slots = new ArrayList();
        int size = slotList.getModel().getSize();
        for (int i = 0; i < size; i++) {
            FrameData fdata = (FrameData) slotList.getModel().getElementAt(i);
            if (fdata.isSelected()) {
                slots.add(fdata.getName());
            }
        }
        config.setSlotsToDisplay(slots);

        ArrayList facets = new ArrayList();
        size = facetList.getModel().getSize();
        for (int i = 0; i < size; i++) {
            FrameData fdata = (FrameData) facetList.getModel().getElementAt(i);
            if (fdata.isSelected()) {
                facets.add(fdata.getName());
            }
        }
        config.setFacetsToDisplay(facets);

        return config;
    }

	private void saveConfigurationFile() {
    	try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            File file = new File(prefix + "HTMLExportConfigurations.xml");
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (javax.xml.transform.TransformerException e) {
            System.out.println(e.getMessage());
        }
    }

    private void restoreDefaultSettings() {
        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);
        showInstancesCheckbox.setSelected(true);
        sortSubclassesCheckbox.setSelected(true);
        useHierarchicalFoldersCheckbox.setSelected(false);   // (MikeHewett) 12 Feb 2007
        numberInstancesCheckbox.setSelected(false);
        outputDirComponent.setPath(SystemUtilities.getUserDirectory());
        headerComponent.setPath(prefix + "header.html");
        footerComponent.setPath(prefix + "footer.html");
        cssComponent.setPath(prefix + "htmlexport.css");

        // Default classes.
        Collection values = new ArrayList();
        Cls cls = project.getKnowledgeBase().getCls(":THING");
        values.add(cls);
        ComponentUtilities.setListValues(classesList, values);

        // Default facets.
        int size = facetList.getModel().getSize();
        for (int i = 0; i < size; i++) {
            FrameData fdata = (FrameData) facetList.getModel().getElementAt(i);
            String name = fdata.getName();
            if (name.equals("Cardinality") || name.equals("Value Type")) {
                fdata.setSelected(true);
            } else {
                fdata.setSelected(false);
            }
        }

        // Default slots.
        size = slotList.getModel().getSize();
        for (int i = 0; i < size; i++) {
            FrameData fdata = (FrameData) slotList.getModel().getElementAt(i);
            String name = fdata.getName();
            if (name.equals(Model.Slot.DIRECT_DOMAIN) ||
                name.equals(Model.Slot.DIRECT_INSTANCES) ||
                name.equals(Model.Slot.DIRECT_SUBCLASSES) ||
                name.equals(Model.Slot.DIRECT_SUBSLOTS) ||
                name.equals(Model.Slot.DIRECT_SUPERCLASSES) ||
                name.equals(Model.Slot.DIRECT_SUPERSLOTS) ||
                name.equals(Model.Slot.DIRECT_TEMPLATE_SLOTS) ||
                name.equals(Model.Slot.DIRECT_TYPES) ||
                name.equals(Model.Slot.DOCUMENTATION) ||
                name.equals(Model.Slot.NAME)) {
                fdata.setSelected(false);
            } else {
                fdata.setSelected(true);
            }
        }
    }

    private void populateFromConfigFile(String configName) {
        String xpath;
        Node result;
        XObject value;

        try {
            deleteButton.setEnabled(true);
            saveButton.setEnabled(true);

            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/show.instances";
            result = XPathAPI.selectSingleNode(document, xpath);
            if (result != null) {
                value = XPathAPI.eval(result, "string()");
                if (value.str().equals("true")) {
                    showInstancesCheckbox.setSelected(true);
                }
                else {
                    showInstancesCheckbox.setSelected(false);
                }
            }

            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/sort.subclasses";
            result = XPathAPI.selectSingleNode(document, xpath);
            if (result != null) {
                value = XPathAPI.eval(result, "string()");
                if (value.str().equals("true")) {
                    sortSubclassesCheckbox.setSelected(true);
                }
                else {
                    sortSubclassesCheckbox.setSelected(false);
                }
            }

            // (MikeHewett) 12 Feb 2007
            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/hierarchical.folders";
            result = XPathAPI.selectSingleNode(document, xpath);
            if (result != null) {
                value = XPathAPI.eval(result, "string()");
                if (value.str().equals("true")) {
                    useHierarchicalFoldersCheckbox.setSelected(true);
                }
                else {
                    useHierarchicalFoldersCheckbox.setSelected(false);
                }
            }

            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/use.numbering";
            result = XPathAPI.selectSingleNode(document, xpath);
            if (result != null) {
                value = XPathAPI.eval(result, "string()");
                if (value.str().equals("true")) {
                    numberInstancesCheckbox.setSelected(true);
                }
                else {
                    numberInstancesCheckbox.setSelected(false);
                }
            }

            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/output.dir";
            result = XPathAPI.selectSingleNode(document, xpath);
            if (result != null) {
                value = XPathAPI.eval(result, "string()");
                outputDirComponent.setPath(value.str());
            }

            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/header";
            result = XPathAPI.selectSingleNode(document, xpath);
            if (result != null) {
                value = XPathAPI.eval(result, "string()");
                headerComponent.setPath(value.str());
            }

            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/footer";
            result = XPathAPI.selectSingleNode(document, xpath);
            if (result != null) {
                value = XPathAPI.eval(result, "string()");
                footerComponent.setPath(value.str());
            }

            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/stylesheet";
            result = XPathAPI.selectSingleNode(document, xpath);
            if (result != null) {
                value = XPathAPI.eval(result, "string()");
                cssComponent.setPath(value.str());
            }

            // Set root classes.
            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/classes/class";
            NodeList results = XPathAPI.selectNodeList(document, xpath);
            if (results != null && results.getLength() > 0) {
                ArrayList rootClasses = new ArrayList();
                for (int i = 0; i < results.getLength(); i++) {
                    result = results.item(i);
                    value = XPathAPI.eval(result, "string()");
                    Cls cls = project.getKnowledgeBase().getCls(value.str());
                    if (cls != null) {
                        rootClasses.add(cls);
                    }
                }
                ComponentUtilities.setListValues(classesList, rootClasses);
            }

            // Set slots to export.
            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/slots/slot";
            results = XPathAPI.selectNodeList(document, xpath);
            if (results != null && results.getLength() > 0) {
                ArrayList slotsToExport = new ArrayList();
                for (int i = 0; i < results.getLength(); i++) {
                    result = results.item(i);
                    value = XPathAPI.eval(result, "string()");
                    slotsToExport.add(value.str());
                }

                int numItems = slotList.getModel().getSize();
                for (int i = 0; i < numItems; i++) {
                    FrameData fdata = (FrameData) slotList.getModel().getElementAt(i);
                    String slotName = fdata.getName();
                    if (slotsToExport.contains(slotName)) {
                        fdata.setSelected(true);
                    } else {
                        fdata.setSelected(false);
                    }
                }
            }

            // Set facets to export.
            xpath = "/html.export.configurations/configuration[name=\"" + configName + "\"]/facets/facet";
            results = XPathAPI.selectNodeList(document, xpath);
            if (results != null && results.getLength() > 0) {
                ArrayList facetsToExport = new ArrayList();
                for (int i = 0; i < results.getLength(); i++) {
                    result = results.item(i);
                    value = XPathAPI.eval(result, "string()");
                    facetsToExport.add(value.str());
                }

                int numItems = facetList.getModel().getSize();
                for (int i = 0; i < numItems; i++) {
                    FrameData fdata = (FrameData) facetList.getModel().getElementAt(i);
                    String facetName = fdata.getName();
                    if (facetsToExport.contains(facetName)) {
                        fdata.setSelected(true);
                    } else {
                        fdata.setSelected(false);
                    }
                }
            }
        } catch (javax.xml.transform.TransformerException e) {
            System.out.println(e.getMessage());
        }
    }
}
