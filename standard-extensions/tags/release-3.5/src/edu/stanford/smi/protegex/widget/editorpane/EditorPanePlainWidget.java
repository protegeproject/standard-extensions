package edu.stanford.smi.protegex.widget.editorpane;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;

import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.widget.TextComponentWidget;

/**
 * Slot widget for detecting hyperlinks in entered text and opening 
 * the link in a new browser when it is clicked. 
 * 
 *  @author Vivek Tripathi (vivekyt@stanford.edu) 
 */

public class EditorPanePlainWidget extends TextComponentWidget {

	private static final long serialVersionUID = -179982525529844650L;
	
	private boolean htmlFoundInKnowledgebase;

	protected JComponent createCenterComponent(JTextComponent textComponent) {
		return ComponentFactory.createScrollPane(textComponent);
	}


	protected JTextComponent createTextComponent() {
		return createEditorPane();
	}

	public JEditorPane getEditorPane() {
        return (JEditorPane) getTextComponent();
    }
	
	public JEditorPane createEditorPane() {
        return new EditorPaneLinkDetector();
    }
	
	public void initialize() {
		 super.initialize(true, 2, 2);
	}
	
	
	/* override TextComponentWidget.getText() which would remove trailing spaces*/
	public String getText() {
		return getTextComponent().getText();
	}
	
	public Collection getValues() {
        String s = getText();
        String modifiedText;
        if(s != null)
        {	
	        if(htmlFoundInKnowledgebase == false)
	        {// knowledgebase had plain text when we read from it. so now we need
	         // to write back plain text after stripping off the html tags from it.
	        	modifiedText = EditorPaneLinkDetectorUtil.htmlToPlainText(s);
	        }
	        else
	        {// knowledgebase already contained html and hence expects us to put
	         // html back in the knowledgebase... however user might have entered
	         // some new lines during editing which need to be replaced by <br>
	        	modifiedText = EditorPaneLinkDetectorUtil.insertBRForNewline(s);
	        }
        }
        else
        	modifiedText = s;
        return CollectionUtilities.createList(modifiedText);
    }

	
	public void setValues(Collection values) {
        Object o = CollectionUtilities.getFirstItem(values);
        String text = o == null ? (String) null : o.toString();
        if(text != null && text.indexOf("<html>") == -1 && text.indexOf("<body>") == -1)
        {// knowledgebase had plain text when we read from it.
        	htmlFoundInKnowledgebase = false;
        	String htmltext = EditorPaneLinkDetectorUtil.enableLinkInPlainText(text);
        	setText(htmltext);
	    }
        else
        {// knowledgebase had html text when we read from it.
        	htmlFoundInKnowledgebase = (text == null ? false : true);
        	setText(text);
        }
    }
	
	public void dispose() {
		EditorPaneLinkDetector epane = (EditorPaneLinkDetector) getEditorPane();
		epane.dispose();		
        super.dispose();       
    }

}
