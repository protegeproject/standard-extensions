package edu.stanford.smi.protegex.widget.editorpane;

import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

class StrikeThroughAction extends StyledEditorKit.StyledTextAction{
	
	private static final long serialVersionUID = -2891339664649725938L;

    public StrikeThroughAction(){
		super(StyleConstants.StrikeThrough.toString());
	}

	public void actionPerformed(ActionEvent ae){
		JEditorPane editor = getEditor(ae);
		if (editor != null) {
			StyledEditorKit kit = getStyledEditorKit(editor);
			MutableAttributeSet attr = kit.getInputAttributes();
			boolean strikeThrough = (StyleConstants.isStrikeThrough(attr)) ? false : true;
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setStrikeThrough(sas, strikeThrough);
			setCharacterAttributes(editor, sas, false);
		} 
	}
}
