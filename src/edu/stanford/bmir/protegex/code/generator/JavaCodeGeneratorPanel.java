package edu.stanford.bmir.protegex.code.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.DisplayUtilities;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableList;

public class JavaCodeGeneratorPanel extends JPanel {

	private KnowledgeBase kb;
	private Collection<Cls> clses = new HashSet<Cls>();

    private JTextField factoryClassNameTextField;
    private JFileChooser fileChooser = new JFileChooser(".");
    private EditableJavaCodeGeneratorOptions options;
    private JTextField packageTextField;
    private JTextField rootFolderTextField;
    private JCheckBox setCheckBox;
	private LabeledComponent lcClasses;
	private SelectableList clsesList;
	private JRadioButton generateAllClses;
	private JRadioButton generateSelectedClses;


    public JavaCodeGeneratorPanel(KnowledgeBase kb, EditableJavaCodeGeneratorOptions options) {
    	this.kb = kb;
        this.options = options;

        packageTextField = new JTextField();
        if (options.getPackage() != null) {
            packageTextField.setText(options.getPackage());
        }
        rootFolderTextField = new JTextField();
        if (options.getOutputFolder() != null) {
            rootFolderTextField.setText(options.getOutputFolder().getAbsolutePath());
        }

        fileChooser.setDialogTitle("Select output folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        factoryClassNameTextField = new JTextField();
        if (options.getFactoryClassName() != null) {
            factoryClassNameTextField.setText(options.getFactoryClassName());
        }

        setCheckBox = new JCheckBox("Return Set instead of Collection");
        setCheckBox.setSelected(options.getSetMode());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        LabeledComponent lc = new LabeledComponent("Root output folder", rootFolderTextField);
        lc.addHeaderButton(new AbstractAction("Select folder...", Icons.getAddIcon()) {
            public void actionPerformed(ActionEvent e) {
                selectFolder();
            }
        });
        add(lc);
        add(Box.createVerticalStrut(8));
        add(new LabeledComponent("Java package", packageTextField));
        add(Box.createVerticalStrut(8));
        add(new LabeledComponent("Factory class name", factoryClassNameTextField));
        add(Box.createVerticalStrut(8));
        //add(createCheckBoxPanel(abstractCheckBox));
        add(Box.createVerticalStrut(8));
        add(createCheckBoxPanel(setCheckBox));
        add(Box.createVerticalStrut(8));
        /*
         * This could work, but it is hard to compute exactly what classes should be exported
         * and which one shouldn't..
         */

/*
        JPanel p = new JPanel(new GridLayout(1,2));
        generateAllClses = new JRadioButton("all classes");
        generateSelectedClses = new JRadioButton("selected classes");
        ButtonGroup group = new ButtonGroup();
        group.add(generateAllClses);
        group.add(generateSelectedClses);
        p.add(generateAllClses);
        p.add(generateSelectedClses);

        lcClasses = createClsesPanel();
        lcClasses.setFooterComponent(p);
        add(lcClasses);

        generateAllClses.setSelected(true);
        clsesList.setBackground(Color.LIGHT_GRAY);

        generateAllClses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clsesList.setBackground(generateAllClses.isSelected() ? Color.LIGHT_GRAY : Color.WHITE);
			}
        });

        generateSelectedClses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clsesList.setBackground(generateAllClses.isSelected() ? Color.LIGHT_GRAY : Color.WHITE);
			}
        });
*/
        add(Box.createVerticalStrut(8));
    }

    private LabeledComponent createClsesPanel() {
		clsesList = ComponentFactory.createSelectableList(null);
		clsesList.setCellRenderer(FrameRenderer.createInstance());

		ComponentUtilities.addListValues(clsesList, clses);

		LabeledComponent labeledComp = new LabeledComponent("Generate code for:", new JScrollPane(clsesList), true );

		labeledComp.addHeaderButton(new AllowableAction("Add classes", Icons.getAddClsIcon(), null) {

			public void actionPerformed(ActionEvent e) {
				HashSet<Cls> allClses = new HashSet<Cls>();
				Iterator j = kb.getClses().iterator();
				while (j.hasNext()) {
					Cls s = (Cls) j.next();
					if (!s.isSystem()) {
						allClses.add(s);
					}
				}

				allClses.add(kb.getRootCls());

				Collection<Cls> newClses = DisplayUtilities.pickClses(JavaCodeGeneratorPanel.this, kb, "Select classes (multiple selection)");
				addClsesIfNotExists(newClses);

				ComponentUtilities.clearListValues(clsesList);
				ComponentUtilities.addListValues(clsesList, clses);

				if (newClses.size() > 0) {
					generateSelectedClses.setSelected(true);
					clsesList.setBackground(Color.WHITE);
				}
			}

			private void addClsesIfNotExists(Collection<Cls> newClses) {
				for (Cls c : newClses) {
					if (!clses.contains(c)) {
						clses.add(c);
					}
				}
			}
		});

		labeledComp.addHeaderButton(new AllowableAction("Remove class", Icons.getRemoveClsIcon(), clsesList) {

			public void actionPerformed(ActionEvent arg0) {
				Collection selection = getSelection();

				if (selection != null) {
					clses.removeAll(selection);
				}

				ComponentUtilities.clearListValues(clsesList);
				ComponentUtilities.addListValues(clsesList, clses);

				if (clses.size() == 0) {
					generateAllClses.setSelected(true);
					clsesList.setBackground(Color.LIGHT_GRAY);
				}
			}

		});

		return labeledComp;
    }


    private JPanel createCheckBoxPanel(Component comp) {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(BorderLayout.WEST, comp);
        southPanel.add(BorderLayout.CENTER, new JPanel());
        southPanel.setPreferredSize(new Dimension(300, 24));
        return southPanel;
    }


    public void ok() {
        File newFile = null;
        String rootFolder = rootFolderTextField.getText().trim();
        if (rootFolder.length() > 0) {
            newFile = new File(rootFolder);
        }
        options.setOutputFolder(newFile);
        //options.setAbstractMode(abstractCheckBox.isSelected());
        options.setSetMode(setCheckBox.isSelected());
        options.setFactoryClassName(factoryClassNameTextField.getText());
        String pack = packageTextField.getText().trim();
        options.setPackage(pack.length() > 0 ? pack : null);
        options.setClses(getClses());
    }


    private void selectFolder() {
        if (fileChooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            rootFolderTextField.setText(file.toString());
        }
    }

    public Collection<Cls> getClses() {
    	List<Cls> c = new ArrayList<Cls>();
    	c.addAll(clses);

    	if (clses.size() == 0) {
    		for (Iterator iterator = kb.getClses().iterator(); iterator.hasNext();) {
				Cls cls = (Cls) iterator.next();
				if (!cls.isSystem()) {
					c.add(cls);
				}
			}
    	}

    	Collections.sort(c, new FrameComparator());
    	return c;
    }
}
