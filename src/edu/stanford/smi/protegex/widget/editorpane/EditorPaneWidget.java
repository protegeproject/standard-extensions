package edu.stanford.smi.protegex.widget.editorpane;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.widget.TextComponentWidget;

/**
 * Slot widget for detecting hyperlinks in entered text and opening 
 * the link in a new browser when it is clicked. 
 * It also supports simple HTML tags, such as bold, italics, underline,
 * strikethrough and inserting images.
 * It also detects links to internal ontologies entities and opens
 * them in a separate window when clicked.
 * 
 *  @author Vivek Tripathi (vivekyt@stanford.edu) 
 */
public class EditorPaneWidget extends TextComponentWidget {

	private static final long serialVersionUID = -1198463792463053162L;
	

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
		EditorPaneLinkDetector e = new EditorPaneLinkDetector();
		return e;
	}


	public void initialize() {		
		super.initialize(true, true, 2, 2);
	}
	
	
	private void insertImage()
	{
		JEditorPane e = (JEditorPane) getEditorPane();
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

		s = "<img src=\""+s+"\"";
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

	protected Collection<AllowableAction> createActions() {
		ArrayList<AllowableAction> actions = new ArrayList<AllowableAction>();

					
		AllowableAction b = new AllowableAction("Bold", 
				ComponentUtilities.loadImageIcon(EditorPaneWidget.class, "images/text_bold.gif") , null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				(new StyledEditorKit.BoldAction()).actionPerformed(arg0);
			}
		};		
		actions.add(b);


		AllowableAction i = new AllowableAction("Italics", 
				ComponentUtilities.loadImageIcon(EditorPaneWidget.class, "images/text_italic.gif"), null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				(new StyledEditorKit.ItalicAction()).actionPerformed(arg0);
			}
		};
		actions.add(i);

		
		AllowableAction u = new AllowableAction("Underline", 
				ComponentUtilities.loadImageIcon(EditorPaneWidget.class, "images/text_underline.gif"), null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				(new StyledEditorKit.UnderlineAction()).actionPerformed(arg0);
			}
		};
		actions.add(u);

		
		AllowableAction s = new AllowableAction("StrikeThrough", 
				ComponentUtilities.loadImageIcon(EditorPaneWidget.class, "images/text_strike.gif"), null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				(new StrikeThroughAction()).actionPerformed(arg0);
			}
		};
		actions.add(s);

		
		AllowableAction insert_image = new AllowableAction("Insert Image", 
				ComponentUtilities.loadImageIcon(EditorPaneWidget.class, "images/imagegallery.gif"), null)
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				insertImage();
			}
		};
		actions.add(insert_image);

		return actions;           
	}
	

	private void HTMLFormat(int action){
		String fulltext = getText();
		String selection = this.getEditorPane().getSelectedText();
		String modifiedS, finaltext, fullplaintext, copyfullplaintext;
		int beginIndex, endIndex, startSearch, selectionstart, count, temp;

		fullplaintext = htmlToPlainText(fulltext);	

		startSearch = this.getEditorPane().getSelectionStart();

		copyfullplaintext = fullplaintext;

		count = 0;
		temp = 0;
		while(copyfullplaintext.indexOf("<b>",temp) != -1)
		{
			count = count + 3;
			temp = copyfullplaintext.indexOf("<b>",temp) + 3;

		}
		startSearch = startSearch + count;
		//	System.out.println("first");

		count = 0;
		temp = 0;
		while(copyfullplaintext.indexOf("</b>",temp) != -1)
		{
			count = count + 4;
			temp = copyfullplaintext.indexOf("</b>",temp)  + 4;

		}
		startSearch = startSearch + count;
		//	System.out.println("2");

		count = 0;
		temp = 0;
		while(copyfullplaintext.indexOf("<i>",temp) != -1)
		{
			count = count + 3;
			temp = copyfullplaintext.indexOf("<i>",temp)  + 3;

		}
		startSearch = startSearch + count;
//		System.out.println("3");

		count = 0;
		temp = 0;
		while(copyfullplaintext.indexOf("</i>",temp) != -1)
		{
			count = count + 4;
			temp = copyfullplaintext.indexOf("</i>",temp)  + 4;

		}
		startSearch = startSearch + count;
		//	System.out.println("4");

		count = 0;
		temp = 0;
		while(copyfullplaintext.indexOf("<u>",temp) != -1)
		{
			count = count + 3;
			temp = copyfullplaintext.indexOf("<u>",temp)  + 3;

		}
		startSearch = startSearch + count;
//		System.out.println("5");

		count = 0;
		temp = 0;
		while(copyfullplaintext.indexOf("</u>",temp) != -1)
		{
			count = count + 4;
			temp = copyfullplaintext.indexOf("</u>",temp)  + 4;

		}
		startSearch = startSearch + count;
		//	System.out.println("6");


		switch(action)
		{
		case 1:
		{
			modifiedS = "<b>"+selection+"</b>";
			break;
		}
		case 2:
		{
			modifiedS = "<i>"+selection+"</i>";
			break;
		}
		case 3:
		{
			modifiedS = "<u>"+selection+"</u>";
			break;
		}
		case 4:
		{
			modifiedS = "<del>"+selection+"</del>";
			break;
		}
		default:
			modifiedS = selection;


		}


		copyfullplaintext = fullplaintext.substring(0, startSearch-1) + "$" + fullplaintext.substring(startSearch-1); 
		//	System.out.println("selection: "+selection+" modified: "+modifiedS+" fullplaintext: "+copyfullplaintext);
		beginIndex = fullplaintext.indexOf(selection, startSearch-1);
		endIndex = beginIndex + selection.length();
		//	System.out.println("startsearch: "+(startSearch-1)+" begin: "+beginIndex+" end: "+endIndex+" fullplaintext: "+copyfullplaintext);
		finaltext = fullplaintext.substring(0, beginIndex) + modifiedS + fullplaintext.substring(endIndex);
		finaltext = enableLinkInPlainText(finaltext);

		//	System.out.println("++++++++++++++ finaltext: "+finaltext);
		markDirty(true);
		setText(finaltext);


	}

	private String htmlToPlainText(String s)
	{
		boolean linkPresent = false;
		int startSearch = 0;
		int linkIndex, hrefIndex;
		String modifiedText, text;

		// here we find the start of text (excluding the initial html tags)
		int beginIndex = s.indexOf("<body>") + "<body>".length();

		// if html tag <body> had extra \n and spaces introduced in our text, we remove it
		if(s.substring(beginIndex, beginIndex + "\n    ".length()).equalsIgnoreCase("\n    "))
			beginIndex = beginIndex + "\n    ".length();
		else // this means that there were no extra spaces after the <body> tag
			beginIndex = beginIndex + 1;  // we add this 1 to move past the \n

		// here we find index of end of text (closing html tags start here)
		int endIndex = s.indexOf("</body>", beginIndex) - 1;

		// we get the text to be operated on in a seperate string htmltext
		String htmltext = s.substring(beginIndex, endIndex);


		// we remove the extra \n which might be added by html formatting 
		// (these dont represent the new lines entered by the user
		htmltext = htmltext.replaceAll("\n    ","");

		// This substring can contain </p> tags which need to be replaced with 
		// with <br>
		if(htmltext.indexOf("</p>") != -1) 
		{
			text = replacePTagsWithBR(htmltext);

		}
		else
			text = htmltext;

		// now we have <br> representing new lines entered by the user we convert
		// it into plain text format by replacing by \n
		text = text.replaceAll("<br>", "\n");

		// now we have to remove all the html tags associated with each hyperlink
		// we find the index of first hyperlink
		if(text.indexOf("<a href=") != -1)
		{
			linkPresent = true;
			// modifiedText now contains pure text till the first hyperlink tag starts
			modifiedText = text.substring(0, text.indexOf("<a href="));
		}
		else // no hyperlink present in code. so entire thing is now plain text === assumption ===
			modifiedText = text;

		// if hyperlink was present, then we have set this flag true
		while(linkPresent)
		{
			// hrefIndex is the index where hyperlink tag starts
			hrefIndex = text.indexOf("<a href=", startSearch);

			// linkIndex is the index where actual text of link starts Ex: www.google.com
			linkIndex = text.indexOf(">", hrefIndex) + 1;

			// we have already detected the first hyperlink. So next time we will start
			// our search for next hyperlink from this index
			startSearch = linkIndex;

			// now we include the text of the link "www.google.com" to the already 
			// extracted pure plain text (which was stored in modifiedText)
			modifiedText = modifiedText + text.substring(linkIndex, text.indexOf("</a>", startSearch));

			// now if there are more links, we should continue in the loop. else we should add the
			// remaining plain text after this current link to our modifiedText string.
			if(text.indexOf("<a href=", startSearch) != -1)
			{
				// loop needs to run more since more links are present
				// we add the pure plain text to modifiedText string till the start of next hyperlink tag
				modifiedText = modifiedText + text.substring(text.indexOf("</a>", startSearch)+"</a>".length(), text.indexOf("<a href=", startSearch));
			}
			else
			{
				// we have found all links. whatever remains is just plain text. So add
				// it to our string modifiedText and return
				modifiedText = modifiedText + text.substring(text.indexOf("</a>", startSearch)+"</a>".length());
				linkPresent = false;
			}
		}
		// to remove the last \n from this string. otherwise extra \n shows up in the end
		int length = modifiedText.length();

		while(length >= 0)
		{
			if(modifiedText.substring(length-1).indexOf("\n") != -1)
				break;
			else
				length = length-1;

		}

		// here we obtain the part which we will be removing in "remainder" string
		// and check that it doesn't contain any actual text apart from \n and spaces
		String remainder = modifiedText.substring(length-1);
		remainder = remainder.replaceAll("\n","");
		remainder = remainder.replaceAll(" ","");
		if(remainder.length() == 0) // this means that the part we are removing from
		{  							// modifiedText just contains one \n and some spaces
			modifiedText = modifiedText.substring(0, length-1);
		}
		//else the part we want to remove also contains some other characters
		// and we don't change modifiedText since we don't want to get into trouble
		// by removing anything other than spaces and \n.

		return modifiedText;
	}

	private String replacePTagsWithBR(String s2)
	{
		boolean pPresent = false;
		int pAtIndex = -1;
		int pStyleAtIndex = -1;
		String modifiedS2;

		pPresent = true;
		modifiedS2 = s2;
		String part1, part2, part3;
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

			// here we check if more replacement needs to be done
			pStyleAtIndex = modifiedS2.indexOf("<p style");
			if(pStyleAtIndex < 0)
			{
				pPresent =  false;
				break;
			}

		}
		modifiedS2 = modifiedS2.replaceAll("\n    ","");
		modifiedS2 = modifiedS2.replaceAll("\n","");

		return modifiedS2;

	}

	public Collection getValues() {
		String s = getText();
		String modifiedS;
		// this functions strips off the </p> from text. Also it inserts <br> for all newlines
		// which user had entered
		if(s != null)
		{	
			modifiedS = insertBRForNewline(s);

		}
		else
		{
			modifiedS = s;

		}

		return CollectionUtilities.createList(modifiedS);
	}

	public void setValues(Collection values) {
		Object o = CollectionUtilities.getFirstItem(values);
		String text = o == null ? (String) null : o.toString();


		//System.out.println("setValues for html " + getInstance().getBrowserText() + ": " + text);

		// the text that we are reading from the knowledge base can be html enabled with
		// html tags or it can be plain text in which case we need to interpret all 
		// the hyperlinks on the fly!
		if(text != null && text.indexOf("<html>") == -1 && text.indexOf("<body>") == -1)
		{
			// so the text in knowledge base doesn't have html tags and we need
			// to detect all the hyperlinks now in order to display them
			String htmltext = enableLinkInPlainText(text);
			setText(htmltext);
		}
		else
		{
			// text in knowledge base is html enabled. so just display it.
			setText(text);
		}

	}

	// this functions strips off the </p> from text. Also it inserts <br> for all newlines
	// which user had entered
	private String insertBRForNewline(String s)
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

	private int findMin(int a, int b, int c)
	{
		if(a < b && a < c && a != -1)
			return a;
		if(b < a && b < c && b != -1)
			return b;
		if(c < a && c < b && c != -1)
			return c;
		if(a == -1)
			return findMin(b, c);
		if(b == -1)
			return findMin(a, c);
		if(c == -1)
			return findMin(a, b);
		return -1;
	}

	private int findMin(int a, int b)
	{
		if(a < b && a != -1)
			return a;
		if(b < a && b != -1)
			return b;
		if(a == -1 && b != -1)
			return b;
		if(b == -1 && a != -1)
			return a;
		return -1;
	}

	private String enableLinkInPlainText(String text)
	{
		// text is plain text. so it has \n to represent new lines. but 
		// html doesnt understand \n. so we replace all \n with <br>
		String htmltext = text.replaceAll("\n", "<br>");

		int startSearch = 0;
		int linkIndex;
		int linkIndex1, linkIndex2, linkIndex3, linkIndex4, linkIndex5, linkEnds, linkEnds1, linkEnds2;
		boolean linkPresent = false;

		// if there is no link in this plain text, we dont have to do anything
		// else we need to parse links and enable them
		if(htmltext.indexOf("http:") != -1 || htmltext.indexOf("www.") != -1 || htmltext.indexOf("mailto:") != -1 || htmltext.indexOf("ftp://") != -1 || htmltext.indexOf("file:/") != -1 )
			linkPresent = true;

		while(linkPresent)
		{
			linkIndex = -1;
			// if http: is present then find its index
			linkIndex1 = htmltext.indexOf("http:", startSearch);

			// if www. is present then find its index
			linkIndex2 = htmltext.indexOf("www.", startSearch);

			// if mailto: is present then find its index
			linkIndex3 = htmltext.indexOf("mailto:", startSearch);

			// if file:/ is present then find its index
			linkIndex4 = htmltext.indexOf("file:/", startSearch);

			// if ftp:// is present then find its index
			linkIndex5 = htmltext.indexOf("ftp:/", startSearch);

			if(linkIndex1 == -1 && linkIndex2 == -1 && linkIndex3 == -1 && linkIndex4 == -1 && linkIndex5 == -1)
			{
				// how can we land here! we shouldnt be in this while loop
				linkIndex = 0;
				linkPresent = false;
				break;
			}

			if(linkIndex1 != -1 || linkIndex2 != -1 || linkIndex3 != -1 )
				linkIndex = findMin(linkIndex1, linkIndex2, linkIndex3);
			if(linkIndex != -1 || linkIndex4 != -1 || linkIndex5 != -1 )
				linkIndex = findMin(linkIndex, linkIndex4, linkIndex5);

			// now we have in the variable linkIndex the place where first link
			// starts so we now find the place where link ends (either link 
			// ends with a space or newline
			linkEnds1 = htmltext.indexOf(" ", linkIndex);
			linkEnds2 = htmltext.indexOf("<br>", linkIndex);

			if(linkEnds2 == -1 && linkEnds1 == -1)
			{
				// this means that the link doesnt end! 
				// we avoid enabling such links
				linkEnds = 0;
				linkPresent = false;
				break;
			}

			linkEnds = findMin(linkEnds1, linkEnds2);

			//      System.out.println("linkstarts: "+linkIndex+" linkends: "+linkEnds);

			// now we have start and end indexes of the first link.
			// we add tags around this link and get new htmltext with first link tagged
			htmltext = htmltext.substring(0, linkIndex) +
			"<a href='" + htmltext.substring(linkIndex, linkEnds) + 
			"'>" + htmltext.substring(linkIndex, linkEnds)+ "</a>" +
			htmltext.substring(linkEnds);

			// now next link should be searched only after the place where first link ends
			startSearch = htmltext.indexOf("</a>", linkEnds) + "</a>".length();

			// here we check whether there is next link in the remaining text or not.
			if(htmltext.indexOf("http:",startSearch) == -1 && htmltext.indexOf("www.",startSearch) == -1 && htmltext.indexOf("mailto:",startSearch) == -1 && htmltext.indexOf("ftp://",startSearch) == -1 && htmltext.indexOf("file:/",startSearch) == -1)
			{	
				linkPresent = false;
				break;
			}

		} 
		// add higher level html tags and body
		htmltext = "<html> \n <head> \n <style type=\"text/css\"> \n <!-- \n body { font-family: arial; font-size: 12pt } \n  p { margin-top: 2; margin-bottom: 2; margin-left: 2; margin-right: 2; font-family: arial } \n  --> \n  </style> \n </head> \n  <body> \n" + htmltext + "\n </body> \n  </html>";
		return htmltext;
	}


	public void dispose() {
		EditorPaneLinkDetector epane = (EditorPaneLinkDetector) getEditorPane();
		// remove mouse listeners for cleanup
		epane.dispose();		
		super.dispose();       
	}
}

	class StrikeThroughAction extends StyledEditorKit.StyledTextAction{
	
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



