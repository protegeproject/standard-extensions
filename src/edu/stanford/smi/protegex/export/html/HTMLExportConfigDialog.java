package edu.stanford.smi.protegex.export.html;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import org.apache.xpath.*;
import org.apache.xpath.objects.XObject;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.plugin.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;

/**
 *
 * @author Jennifer Vendetti
 */
public class HTMLExportConfigDialog extends JDialog {
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
                                  boolean modal, Project project)
        throws HeadlessException {
        super(owner, title, modal);
        this.project = project;

        prefix = PluginUtilities.getPluginsDirectory().getPath() +
                                                  File.separator +
                 "edu.stanford.smi.protegex.standard_extensions" +
                                                  File.separator +
                                                   "html_export" +
                                                   File.separator;

        try {
            // Read in configuration file.
            javax.xml.parsers.DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(prefix + "HTMLExportConfigurations.xml");

            initializeUI();
        }
        catch(Exception e) {
            e.printStackTrace();
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
        configNamesComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configNamesComboBox_actionPerformed(e);
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
        Cls cls = project.getKnowledgeBase().getCls(":THING");
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
        /** @todo change middle parameter back to null - or something... */
        outputDirComponent = new FileField("Output Directory:", "c:\\temp\\htmlexport", "Output Directory");
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

        c.gridx = 0;
        c.gridy = 5;
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

    public void configNamesComboBox_actionPerformed(ActionEvent ae) {
        /** @todo implement this method */
    }

    private Action createAddClsAction() {
        return new AddAction(ResourceKey.CLASS_ADD) {
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
            public void onRemove(Collection values) {
                ComponentUtilities.removeListValues(classesList, values);
            }
        };
    }

    public ExportConfiguration getExportConfiguration() {
        ExportConfiguration config = new ExportConfiguration();

		config.setShowInstances(showInstancesCheckbox.isSelected());
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
}
