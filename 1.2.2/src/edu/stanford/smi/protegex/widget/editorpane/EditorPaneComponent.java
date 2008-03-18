package edu.stanford.smi.protegex.widget.editorpane;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;

public class EditorPaneComponent {

	private EditorPaneLinkDetector e;
	
	public EditorPaneLinkDetector createEditorPaneLinkDetector(){
		return createEditorPaneLinkDetector(true, true);
	}
	
	public EditorPaneLinkDetector createEditorPaneLinkDetector(boolean editable, boolean detectEnter){
		e = new EditorPaneLinkDetector(editable, detectEnter);
		e.setAutoscrolls(true);
		return e;
	}
	
	public LabeledComponent createUI(EditorPaneLinkDetector e) {
		LabeledComponent lc = new LabeledComponent("", e, false, true);
		
		lc.addHeaderButton(getBoldAction()); 
		lc.addHeaderButton(getItalicsAction()); 
		lc.addHeaderButton(getUnderlineAction()); 
		lc.addHeaderButton(getStrikeThroughAction()); 
		return lc;

	}

	public AllowableAction getBoldAction()
	{
		return new AllowableAction("Bold", 
				ComponentUtilities.loadImageIcon(EditorPaneComponent.class, "images/text_bold.gif") , null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				(new StyledEditorKit.BoldAction()).actionPerformed(arg0);
			}
		};
		
	}
	
	public AllowableAction getItalicsAction()
	{
	
		return new AllowableAction("Italics", 
				ComponentUtilities.loadImageIcon(EditorPaneComponent.class, "images/text_italic.gif"), null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				(new StyledEditorKit.ItalicAction()).actionPerformed(arg0);
			}
		};
	}
	
	public AllowableAction getUnderlineAction()
	{
		return new AllowableAction("Underline", 
				ComponentUtilities.loadImageIcon(EditorPaneComponent.class, "images/text_underline.gif"), null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				(new StyledEditorKit.UnderlineAction()).actionPerformed(arg0);
			}
		};
	}

	public AllowableAction getStrikeThroughAction()
	{
		return new AllowableAction("StrikeThrough", 
				ComponentUtilities.loadImageIcon(EditorPaneComponent.class, "images/text_strike.gif"), null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				(new StrikeThroughAction()).actionPerformed(arg0);
			}
		};

	}
	
	public AllowableAction getInsertImageAction()
	{
		return new AllowableAction("Insert Image", 
				ComponentUtilities.loadImageIcon(EditorPaneComponent.class, "images/imagegallery.gif"), null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				insertImage();
			}
		};
	}
	
	private void insertImage()
	{
		Document doc = e.getDocument();

		String s = (String)JOptionPane.showInputDialog(
				e,
				"Please enter the URL Location of Image:",
				"Insert Image",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"http://protege.stanford.edu/images/ProtegeLogo.gif");

		//If a string was returned, say so.
		if ((s == null) || (s.length() == 0)) {
			return;
		}

		if(s.indexOf(".gif") == -1 && s.indexOf(".jpg") == -1 && s.indexOf(".bmp") == -1 && s.indexOf(".png") == -1 && s.indexOf(".ico") == -1)
		{
			System.out.println("Invalid image");
			return;
		}

		s = "<img src=\""+ s +"\">";
		try{
			doc.insertString(e.getCaretPosition(), "---image---", null);
		} catch (BadLocationException ble) {

			return;
		}

		String text = e.getText();
		String modifiedtext;
		if(text != null)
		{	
			modifiedtext = insertBRForNewline(text);
			modifiedtext = modifiedtext.replaceAll("---image---", s);
		}
		else
		{
			modifiedtext = text;

		}

		e.setText(modifiedtext);
		

	}

	// this functions strips off the </p> from text. Also it inserts <br> for all newlines
	// which user had entered
	public String insertBRForNewline(String s)
	{
		boolean pPresent = false;
		int pAtIndex = -1;
		int pStyleAtIndex = -1;

		// find index from where user entered text starts
		int beginIndex = s.indexOf("<body>") + "<body>".length()+ 1;

		// s1 contains initial html tags before the user entered text
		String s1 = s.substring(0, beginIndex);

		// s2 contains the user entered text along with intermediate html tags
		String s2 = s.substring(beginIndex, s.indexOf("</body>") - 1);

		// s3 contains finishing html tags after the user entered text
		String s3 = s.substring(s.indexOf("</body>"));

		String modifiedS2;

		if(s2.indexOf("</p>") != -1) // this means html body recognizes
		{							 // new lines in the text and puts 
			// </p> for each new line
			pPresent = true;
			modifiedS2 = s2;
			String part1, part2, part3;

			// find where first </p> and <p style is present in the text
			pAtIndex = s2.indexOf("</p>");
			pStyleAtIndex = s2.indexOf("<p style");

			while(pPresent)
			{
				// next 3 lines replace <p style="margin-top: 0"> with null
				part1 = modifiedS2.substring(0, pStyleAtIndex);
				part2 = modifiedS2.substring(pStyleAtIndex + "<p style=\"margin-top: 0\">".length()+1);
				modifiedS2 = part1 + part2;

				// next 5 lines replace </p> with <br>
				pAtIndex = modifiedS2.indexOf("</p>");
				part1 = modifiedS2.substring(0, pAtIndex);
				part2 = "<br>";
				part3 = modifiedS2.substring(pAtIndex + "</p".length()+1);
				modifiedS2 = part1 + part2 + part3;

				// here we check if more replacement needs to be done else we exit the loop
				pStyleAtIndex = modifiedS2.indexOf("<p style");
				if(pStyleAtIndex < 0)
				{
					pPresent =  false;
					break;
				}

			}

			// since we are writing html to the pins file, we don't need any \n. We
			// already have put <br> wherever newlines were starting
			modifiedS2 = modifiedS2.replaceAll("\n    ","");
			modifiedS2 = modifiedS2.replaceAll("\n","");

		}
		else
		{ 
			// this means that editor pane has not accounted for the new lines
			// which the user might have entered and hence we need to replace
			// all \n with <br>
			s2 = s2.replaceAll("\n    ", "");   
			modifiedS2 = s2.replaceAll("\n", "<br>");

		}

		// to remove the last <br> from this string. otherwise extra <br> shows up in the end
		int length = modifiedS2.length();

		while(length >= 0)
		{
			if(modifiedS2.substring(length-1).indexOf("<br>") != -1)
				break;
			else
				length = length-1;

		}

		// here we obtain the part which we will be removing in "remainder" string
		// and check that it doesn't contain any actual text apart from <br> and spaces
		String remainder = modifiedS2.substring(length-1);
		remainder = remainder.replaceAll("<br>","");
		remainder = remainder.replaceAll(" ","");
		if(remainder.length() == 0) // this means that the part we are removing from
		{  							// modifiedText just contains one <br> and some spaces
			modifiedS2 = modifiedS2.substring(0, length-1);
		}
		//else the part we want to remove also contains some other characters
		// and we don't change modifiedText since we don't want to get into trouble
		// by removing anything other than spaces and <br>.

		// we were operating on the middle part of entire string. now we 
		// complete the original string and return it.
		String modifiedS = s1 + modifiedS2 + s3;
		return modifiedS;
	}

}
