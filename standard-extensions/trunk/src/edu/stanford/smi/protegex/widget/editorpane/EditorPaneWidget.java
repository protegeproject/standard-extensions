package edu.stanford.smi.protegex.widget.editorpane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.BrowserLauncher;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.widget.TextComponentWidget;

/**
 * Slot widget for detecting hyperlinks in entered text and opening
 * the link in a new browser when it is clicked.
 * It also supports simple HTML tags, such as bold, italics, underline,
 * strike-through and inserting images.
 * It also detects links to internal ontologies entities and opens
 * them in a separate window when clicked.
 *
 *  @author Vivek Tripathi (vivekyt@stanford.edu)
 */
public class EditorPaneWidget extends TextComponentWidget {

	private static final long serialVersionUID = -1198463792463053162L;
	private EditorPaneComponent epc;
	private LabeledComponent labeledComponent;
	JComboBox internalLinkTo;
	private final String EditorPaneHelpURL = "http://protegewiki.stanford.edu/index.php/EditorPane";

	@Override
	protected JComponent createCenterComponent(JTextComponent textComponent) {
		return ComponentFactory.createScrollPane(textComponent);
	}


	@Override
	protected JTextComponent createTextComponent() {
		return createEditorPane();
	}

	public JEditorPane getEditorPane() {
		return (JEditorPane) getTextComponent();
	}

	public JEditorPane createEditorPane() {
		epc = new EditorPaneComponent();

		KnowledgeBase kb = ProjectManager.getProjectManager().getCurrentProject().getKnowledgeBase();
		String[] options1 = { "Add Internal Link To" , "Class", "Property" , "Individual"};
		String[] options2 = { "Add Internal Link To" , "Class", "Slot", "Instance"  };


		if(PluginUtilities.isOWL(kb))
		{
			epc.setOWLMode(true);
			internalLinkTo = new JComboBox(options1);

		}
		else
		{
			epc.setOWLMode(false);
			internalLinkTo = new JComboBox(options2);

		}

		internalLinkTo.setRenderer(new FrameRenderer() {
			private static final long serialVersionUID = -6665828965048673792L;

            @Override
			public void load(Object value) {
				if (value.equals("Class")) {
					setMainIcon(Icons.getClsIcon());
					setMainText("Class");
				} else if (value.equals("Slot")) {
					setMainIcon(Icons.getSlotIcon());
					setMainText("Slot");
				} else if (value.equals("Instance")) {
					setMainIcon(Icons.getInstanceIcon());
					setMainText("Instance");
				}
				else if (value.equals("Property")) {
					setMainIcon(Icons.getSlotIcon());
					setMainText("Property");
				} else if (value.equals("Individual")) {
					setMainIcon(Icons.getInstanceIcon());
					setMainText("Individual");
				} else {
					super.load(value);
				}
			}
		}
		);

		internalLinkTo.setSelectedIndex(0);
		internalLinkTo.addActionListener(epc.getAddInternalLinkActionListener());

	//	add(internalLinkTo, BorderLayout.BEFORE_FIRST_LINE);
		EditorPaneLinkDetector epl = epc.createEditorPaneLinkDetector();
	//	lc = new LabeledComponent("", epl, false, true);
//		lc.setHeaderComponent(internalLinkTo, BorderLayout.WEST);
		return epl;
	}


	public void initialize() {
		super.initialize(true, true, 2, 2);
		labeledComponent = (LabeledComponent) getComponent(0);
		JButton helpButton = new JButton("?");
		addButtons(labeledComponent);

		helpButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
			    	try{
			    		BrowserLauncher.openURL(EditorPaneHelpURL);
			    	}
			    	catch (IOException e1){
			    		;
			    	}
		    	}

		});

		JComponent lc1 = new JPanel();
		lc1.add(internalLinkTo, BorderLayout.WEST);
		lc1.add(helpButton, BorderLayout.EAST);
		labeledComponent.setHeaderComponent(lc1,BorderLayout.WEST);

	}


	@Override
	protected Collection<AllowableAction> createActions() {
		ArrayList<AllowableAction> actions = new ArrayList<AllowableAction>();
/*
		AllowableAction b = epc.getBoldAction();
		actions.add(b);


		AllowableAction i = epc.getItalicsAction();
		actions.add(i);


		AllowableAction u = epc.getUnderlineAction();
		actions.add(u);


		AllowableAction s = epc.getStrikeThroughAction();
		actions.add(s);

		AllowableAction h = epc.getHighligherAction();
		actions.add(h);

		AllowableAction insert_image = epc.getInsertImageAction();
		actions.add(insert_image);
		*/
		return actions;
	}


	@Override
	public Collection getValues() {
		String s = getText();
		String modifiedS;
		// this functions strips off the </p> from text. Also it inserts <br> for all newlines
		// which user had entered
		if(s != null) {
			modifiedS = EditorPaneLinkDetectorUtil.insertBRForNewline(s);
		} else {
			modifiedS = s;
		}

		return CollectionUtilities.createList(modifiedS);
	}

	@Override
	public void setValues(Collection values) {
		Object o = CollectionUtilities.getFirstItem(values);
		String text = o == null ? (String) null : o.toString();

		// the text that we are reading from the knowledge base can be html enabled with
		// html tags or it can be plain text in which case we need to interpret all
		// the hyperlinks on the fly!
		if(text != null && text.indexOf("<html>") == -1 && text.indexOf("<body>") == -1 && text.indexOf("<a href") == -1)
		{
			// so the text in knowledge base doesn't have html tags and we need
			// to detect all the hyperlinks now in order to display them
			String htmltext = EditorPaneLinkDetectorUtil.enableLinkInPlainText(text);
			setText(htmltext);
		}
		else
		{
			// text in knowledge base is html enabled. so just display it.
			setText(text);
		}

	}

	@Override
	public void dispose() {
		EditorPaneLinkDetector epane = (EditorPaneLinkDetector) getEditorPane();
		// remove mouse listeners for cleanup
		epane.dispose();
		super.dispose();
	}

	protected void addButtons(LabeledComponent c) {
        addButton(epc.getBoldAction());
        addButton(epc.getItalicsAction());
        addButton(epc.getUnderlineAction());
        addButton(epc.getStrikeThroughAction());
        addButton(epc.getHighligherAction());
        addButton(epc.getInsertImageAction());
    }

	public void addButton(Action action) {
        addButton(action, true);
    }

    public void addButton(Action action, boolean defaultState) {
        if (action != null) {
            addButtonConfiguration(action, defaultState);
            if (displayButton(action)) {
            	labeledComponent.addHeaderButton(action);
            }
        }
    }

}




