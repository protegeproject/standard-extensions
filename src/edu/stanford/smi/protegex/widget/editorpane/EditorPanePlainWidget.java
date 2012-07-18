package edu.stanford.smi.protegex.widget.editorpane;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringEscapeUtils;

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

	private static final String HTML_HEADER = "<html> \n <head> \n <style type=\"text/css\"> \n <!-- \n body { font-family: arial; font-size: 12pt } \n  p { margin-top: 2; margin-bottom: 2; margin-left: 2; margin-right: 2; font-family: arial } \n  --> \n  </style> \n </head> \n  <body> \n";
	private static final String HTML_FOOTER = "\n </body> \n  </html>";
	private static final String INTERNAL_LINK_PATTERN = EditorPaneLinkDetector.ONTOLOGY_COMPONENT_LINK_PREFIX + "'.+?'" + "(" + EditorPaneLinkDetector.PATTERN_ONTOLOGY_COMPONENT_LINK_SEP + ")";

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
	        	modifiedText = htmlToPlainText(s);
	        }
	        else
	        {// knowledgebase already contained html and hence expects us to put
	         // html back in the knowledgebase... however user might have entered
	         // some new lines during editing which need to be replaced by <br>
	        	modifiedText = insertBRForNewline(s);
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
        	String htmltext = enableLinkInPlainText(text);
        	setText(htmltext);
	    }
        else
        {// knowledgebase had html text when we read from it.
        	htmlFoundInKnowledgebase = (text == null ? false : true);
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
	    String s2 = s.substring(beginIndex, s.lastIndexOf("</body>") - 1);
	    
	    // s3 contains finishing html tags after the user entered text
	    String s3 = s.substring(s.lastIndexOf("</body>"));
	    
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
	    	modifiedS2 = modifiedS2.replaceAll("[\r]?\n[\r]?    ","");
	    	modifiedS2 = modifiedS2.replaceAll("[\r]?\n[\r]?","");
	    	    	        	
	    }
	    else
	    { 
	    	// this means that editor pane has not accounted for the new lines
	    	// which the user might have entered and hence we need to replace
	    	// all \n with <br>
	    	s2 = s2.replaceAll("[\r]?\n[\r]?    ", "");   
	    	modifiedS2 = s2.replaceAll("[\r]?\n[\r]?", "<br>");
	    	
	    }
	    
	    // to remove the last <br> from this string. otherwise extra <br> shows up 
	    // in the end
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
    	modifiedS2 = modifiedS2.replaceAll("[\r]?\n[\r]?    ","");
    	modifiedS2 = modifiedS2.replaceAll("[\r]?\n[\r]?","");
    	   	        	
        return modifiedS2;
		
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
        int endIndex = s.lastIndexOf("</body>");
        
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
        int length = findMin(modifiedText.length(), modifiedText.lastIndexOf("\n"));
       
        // here we obtain the part which we will be removing in "remainder" string
        // and check that it doesn't contain any actual text apart from \n and spaces
        String remainder = modifiedText.substring(length);
        remainder = remainder.replaceAll("[\r]?\n[\r]?","");
        remainder = remainder.replaceAll(" ","");
        if(remainder.length() == 0) // this means that the part we are removing from
        {  							// modifiedText just contains one \n and some spaces
        	modifiedText = modifiedText.substring(0, length);
        }
        //else the part we want to remove also contains some other characters
        // and we don't change modifiedText since we don't want to get into trouble
        // by removing anything other than spaces and \n.
               
        modifiedText = StringEscapeUtils.unescapeHtml(modifiedText);
        
        return modifiedText;
	}
	
	private int findMin(int a, int b, int c)
	{
		return findMin(a, findMin(b,c));
	}
	
	private int findMin(int a, int b)
	{
		if (a == b)
			return a;
		if (a == -1)
			return b;
		if (b == -1)
			return a;
		return a < b ? a : b;
	}
	
	private String enableLinkInPlainText(String text)
	{
		// text is plain text. so it has \n to represent new lines. but 
		// html doesnt understand \n. so we replace all \n with <br>
	    String htmltext = text;
	    htmltext = StringEscapeUtils.escapeHtml(htmltext);
	    htmltext = htmltext.replaceAll("(\\b) (\\b)", "$1&SingleSp;$2");
	    htmltext = htmltext.replaceAll(" ", "&nbsp;");
	    htmltext = htmltext.replaceAll("&SingleSp;", " ");
	    htmltext = htmltext.replaceAll("(\\b)\t(\\b)", "$1&SingleTab;$2");
	    htmltext = htmltext.replaceAll("\t", "&#09;");
	    htmltext = htmltext.replaceAll("&SingleTab;", "\t");
	    htmltext = htmltext.replaceAll("[\r]?\n[\r]?", "<br>");
	    
	    int startSearch = 0;
	    int linkIndex;
        int linkIndex1, linkIndex2, linkIndex3, linkIndex4, linkIndex5, linkIndex6, linkEnds, linkEnds1, linkEnds2, linkEnds3;
        boolean linkPresent = false;
        
        Matcher internalLinkMatcher = Pattern.compile(INTERNAL_LINK_PATTERN).matcher(htmltext);
        // if there is no link in this plain text, we dont have to do anything
        // else we need to parse links and enable them
        if(htmltext.indexOf("http:") != -1 || htmltext.indexOf("www.") != -1 || 
        		htmltext.indexOf("mailto:") != -1 || htmltext.indexOf("ftp://") != -1 || 
        		htmltext.indexOf("file:/") != -1 || internalLinkMatcher.find()) {
        	linkPresent = true;
        }
        
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
	        
	        // if internal link (@'xyz') is present then find its index
	        internalLinkMatcher.reset();
	        linkIndex6 = internalLinkMatcher.find(startSearch) ? internalLinkMatcher.start() : -1 ;
	        
	        if(linkIndex1 == -1 && linkIndex2 == -1 && linkIndex3 == -1 && linkIndex4 == -1 && linkIndex5 == -1 && linkIndex6 == -1)
	        {
	        	// how can we land here! we shouldnt be in this while loop
	        	linkIndex = 0;
	        	linkPresent = false;
	        	break;
	        }
	        // whichever is first, we start with that
	        /*
	        else if(linkIndex2 == -1 && linkIndex1 != -1)
	        	linkIndex = linkIndex1;
	        else if(linkIndex1 == -1 && linkIndex2 != -1)
	        	linkIndex = linkIndex2;
	        else if (linkIndex1 < linkIndex2)
	        	linkIndex = linkIndex1;
	        else
	        	linkIndex = linkIndex2;
	        */
	        linkIndex = findMin( 
	        		findMin(linkIndex1, linkIndex2, linkIndex3), 
	        		findMin(linkIndex4, linkIndex5, linkIndex6));
	        
	        // now we have in the variable linkIndex the place where first link
	        // starts so we now find the place where link ends (either link 
	        // ends with a space or newline
	        linkEnds1 = htmltext.indexOf(" ", linkIndex);
	        linkEnds2 = htmltext.indexOf("<br>", linkIndex);
	        linkEnds3 = htmltext.indexOf("&nbsp;", linkIndex);
	        
	        if(linkEnds1 == -1 && linkEnds2 == -1 && linkEnds3 == -1)
	        {
	        	linkEnds = htmltext.length();
	        }
	        else {
	        	linkEnds = findMin(linkEnds1, linkEnds2, linkEnds3);
	        }
	        
        	//if we found an internal reference use the pattern matching to locate the end of the reference 
        	if (linkIndex == linkIndex6) {
        		linkEnds = findMin(linkEnds, internalLinkMatcher.start(1));
        	}
        	
        	String tentativeUrl = htmltext.substring(linkIndex, linkEnds);
        	String dotEndPattern = (linkIndex == linkIndex6 ? 
        			EditorPaneLinkDetector.PATTERN_DOT_END_ONTOLOGY_COMPONENT_LINK : EditorPaneLinkDetector.PATTERN_DOT_END_EXTERNAL_LINK);
        	Matcher dotEndMatcher = Pattern.compile(dotEndPattern).matcher(tentativeUrl);
        	if (dotEndMatcher.find()) {
        		linkEnds = linkIndex + dotEndMatcher.start(1);
        	}
        	
	        // now we have start and end indexes of the first link.
	        // we add tags around this link and get new htmltext with first link tagged
	        htmltext = htmltext.substring(0, linkIndex) +
	        "<a href='" + (linkIndex == linkIndex6 ? "internalLink" : htmltext.substring(linkIndex, linkEnds)) + 
	        "'>" + htmltext.substring(linkIndex, linkEnds)+ "</a>" +
	        htmltext.substring(linkEnds);
	    
	        // now next link should be searched only after the place where first link ends
	        startSearch = htmltext.indexOf("</a>", linkEnds) + "</a>".length();
	        
	        internalLinkMatcher = Pattern.compile(INTERNAL_LINK_PATTERN).matcher(htmltext);
	        // here we check whether there is next link in the remaining text or not.
	        if(htmltext.indexOf("http:",startSearch) == -1 && htmltext.indexOf("www.",startSearch) == -1 && 
	        		htmltext.indexOf("mailto:",startSearch) == -1 && htmltext.indexOf("ftp://",startSearch) == -1 && 
	        		htmltext.indexOf("file:/",startSearch) == -1 && (!internalLinkMatcher.find(startSearch)) )
	        {	
	           	linkPresent = false;
	           	break;
	        }
	                
        } 
        // add higher level html tags and body
        htmltext = HTML_HEADER + htmltext + HTML_FOOTER;
        return htmltext;
	}
	
	public void dispose() {
		EditorPaneLinkDetector epane = (EditorPaneLinkDetector) getEditorPane();
		epane.dispose();		
        super.dispose();       
    }

}
