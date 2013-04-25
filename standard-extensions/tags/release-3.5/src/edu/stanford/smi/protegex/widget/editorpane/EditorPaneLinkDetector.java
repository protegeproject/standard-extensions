package edu.stanford.smi.protegex.widget.editorpane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.accessibility.AccessibleHypertext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.parser.ParserDelegator;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.BrowserLauncher;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.util.SystemUtilities;

/**
 * Utility class for detecting hyperlinks and internal ontology links in a text.
 *
 * @author Vivek Tripathi (vivekyt@stanford.edu)
 *
 */

public class EditorPaneLinkDetector extends JEditorPane implements Disposable {

	private static final long serialVersionUID = 6879261696169758008L;

	public final static String ONTOLOGY_COMPONENT_LINK_PREFIX = "@";
	public final static String PATTERN_ONTOLOGY_COMPONENT_LINK_SEP = "\\s+|&#?\\w{2,8};|\\p{Punct}|$";

	public static final String PATTERN_DOT_END_EXTERNAL_LINK = "([\\W&&[^/]]+)$";
	public static final String PATTERN_DOT_END_ONTOLOGY_COMPONENT_LINK = "([\\W&&[^']]+)$";

//	private final static int EDITOR_PANE_BROWSER_TEXT_DEFAULT_FRAME_LIMIT = 10000;
//	private final static String EDITOR_PANE_BROWSER_TEXT_FRAME_LIMIT_PROPERTY = "editor.pane.browsertext.frame.limit";

	private String linkActive;
	private boolean editable;
	private boolean detectEnter;

	public String getLinkActive() {
		return linkActive;
	}

	public void addText(String s) {
		String old = getText();
		int addIndex = old.indexOf("</body>") - 1;
		String toadd1 = s.substring(0, s.indexOf("<html>") - 1);
		String toadd2 = s.substring(s.indexOf("<body>") + "<body>".length() + 1, s.indexOf("</body>") - 1);
		toadd2 = toadd2.replaceFirst("<p style=\"margin-top: 0\">", "");
		String text = old.substring(0, addIndex) + "<p style=\"margin-top: 0\">" + toadd1 + toadd2 + old.substring(addIndex);
		setText(text);
	}

	private void internalLinkClicked(String name) {
		try {
			KnowledgeBase kb = ProjectManager.getProjectManager().getCurrentProject().getKnowledgeBase();
			Instance inst = kb.getInstance(name);
			if (inst == null) {
				ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getMainPanel(), "Could not find entity with name: "	+ name);
			} else {
				kb.getProject().show(inst);
			}
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at opening the display for the internal link: " + name, e);
		}
	}

	public void dispose() {
		removeMouseListener(getEditorPaneMouseListener());
		removeMouseMotionListener(getEditorPaneMouseMotionListener());
	}

	private MouseListener ml = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (linkActive != null) {
				// this means that the editor pane
				// has a hyperlink at the point
				// where mouse pointer is clicked currently.
				String str = null;

				// the user might or might not have entered
				// http:// in the hyperlink text. We add "http://"
				// in the hyperlink if needed.
				// http://|https://|www.|ftp://|file:/|mailto:)\\S+)(\\s+)")

				String linkString = ONTOLOGY_COMPONENT_LINK_PREFIX + "'";

				if (linkActive.contains(linkString)) {
					str = linkActive.substring(linkActive.indexOf(linkString)
							+ linkString.length(), linkActive.length() - 1);
					internalLinkClicked(str);

				} else if (linkActive.contains("ftp://")) {
					str = linkActive;
					try {
						BrowserLauncher.openURL(str);
					} catch (IOException e1) {
						// do nothing
					}
				} else {
					if (linkActive.contains("mailto:")
							|| linkActive.contains("http://")
							|| linkActive.contains("file:/")) {
						str = linkActive;
					} else {
						str = "http://" + linkActive;
					}

					// here we call the explorer with the clicked
					// link as an argument. These are present in str
					// Runtime.getRuntime().exec(str);
					SystemUtilities.showHTML(str);
				}
				if (editable) {
					setEditable(true);
				}
			}
		}
	};

	private MouseMotionListener mml = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {

			AccessibleJTextComponent context = (AccessibleJTextComponent) getAccessibleContext()
					.getAccessibleEditableText();

			AccessibleHypertext accText = (AccessibleHypertext) context
					.getAccessibleText();

			// accText.getIndexAtPoint() returns 0 based index of character
			// at the mouse tip
			int index = accText.getIndexAtPoint(e.getPoint());

			// Returns the index into an array of hyperlinks that is
			// associated with this character index, or -1 if there
			// is no hyperlink associated with this index.
			int linkIndex = accText.getLinkIndex(index);

			if (linkIndex == -1) { // this means that the editor pane
				// does not have a hyperlink at the point
				// where mouse pointer is currently
				setToolTipText(null);
				linkActive = null;
				if (!isEditable() && editable) {
					setEditable(true);
				}
				return;
			}

			// getAccessibleActionDescription() Returns a String
			// description of this particular link action.
			// We save it in linkDesc.
			String linkDesc = accText.getLink(linkIndex).getAccessibleActionDescription(0);


			// here we make linkActive as the link on which mouse pointer
			// is moved. This linkActive is then used in mouseclick listener

			linkActive = linkDesc;

			// We display the link in the ToolTipText of the pointer
			// and also Change mouse pointer to 'hand'
			// when it is moved over a hyperlink

			if (isEditable()) {
				setEditable(false);
			}
		}
	};

	public MouseListener getEditorPaneMouseListener() {
		return ml;
	}

	public MouseMotionListener getEditorPaneMouseMotionListener() {
		return mml;
	}

	public EditorPaneLinkDetector() {
		this(true, true);
	}

	public EditorPaneLinkDetector(boolean edit, boolean detectent) {
		this.detectEnter = detectent;
		this.editable = edit;
		this.setFont(new Font("Arial", Font.PLAIN, 12));
		linkActive = null;
		HTMLEditorKit htmlkit = new HTMLEditorKit();

		StyleSheet styles = htmlkit.getStyleSheet();
		StyleSheet ss = new StyleSheet();

		ss.addStyleSheet(styles);

		ss.addRule("body {font-family:arial;font-size:12pt}");
		ss.addRule("p {font-family:arial;margin:2}");

		// styles.addRule("body {font-size : 21pt; }");
		// styles.addRule("body {font-family : Sans Serif; }");

		// HTMLDocLinkDetector is class made which extends HTMLDocument
		HTMLDocument doc = new HTMLDocLinkDetector(ss);
		setEditorKit(htmlkit);

		setDocument(doc);
		addMouseListener(ml);
		addMouseMotionListener(mml);

		setEditable(editable);

		if (!detectEnter) {
			Action doNothing = new AbstractAction() {
				private static final long serialVersionUID = 3324100681335212165L;

                public void actionPerformed(ActionEvent e) {
					;// do nothing
				}
			};
			getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "doNothing");
			getActionMap().put("doNothing", doNothing);
		}
	}

	protected class HTMLDocLinkDetector extends HTMLDocument {

		private static final long serialVersionUID = 5354086875595780200L;

        public HTMLDocLinkDetector(StyleSheet ss) {
			super(ss);
			// p - the new asynchronous loading priority;
			// a value less than zero indicates that the document
			// should not be loaded asynchronously
			setAsynchronousLoadPriority(4);
			// Sets the number of tokens to buffer before trying to
			// update the documents element structure.
			setTokenThreshold(100);
			// Sets the parser that is used by the methods that insert
			// html into the existing document, such as setInnerHTML,
			// and setOuterHTML.
			// HTMLEditorKit.createDefaultDocument can also set the default
			// parser.
			setParser(new ParserDelegator());
		}

		/**
		 * Returns true if the Element contains a HTML.Tag.A attribute, false
		 * otherwise.
		 *
		 * @param e
		 *            the Element to be checked
		 * @return
		 */
		protected boolean isLink(Element e) {
			return e.getAttributes().getAttribute(HTML.Tag.A) != null;
		}

		/**
		 * This method corrects or creates a url contained in an Element as an
		 * hyperlink.
		 *
		 * @param e
		 *            the Element to be computed
		 * @throws BadLocationException
		 */
		protected void computeLinks(Element e) throws BadLocationException {
			int caretPos = getCaretPosition(); // gets the position of
			// insert text caret
			try {
				if (isLink(e)) {
					// has been edited, correct the link
					correctLink(e);
				} else {
					// call createLink(e)
					createLink(e);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			setCaretPosition(Math.min(caretPos, getLength()));
		}

		/**
		 * The method corrects the url inside an Element, that is supposed to be
		 * an element containing a link only. This function is typically called
		 * when the url is beeing edited. What the function does is to remove
		 * the html tags, so the url is actually edited in plain text and not as
		 * an hyperlink.
		 *
		 * @param e
		 *            the Element that contains the url
		 * @throws BadLocationException
		 * @throws IOException
		 */
		protected void correctLink(Element e) throws BadLocationException,
				IOException {

			int length = e.getEndOffset() - e.getStartOffset();

			boolean endOfDoc = e.getEndOffset() == getLength() + 1;

			// to avoid catching the final '\n' of the document.
			if (endOfDoc) {
				length--;
			}

			String text = getText(e.getStartOffset(), length);

			setOuterHTML(e, text);

			// insert final spaces ignored by the html
			Matcher spaceMatcher = Pattern.compile("(\\s+)$").matcher(text);

			if (spaceMatcher.find()) {// Returns true if, and only if, a
										// subsequence of the input
				// sequence matches this matcher's pattern
				String endingSpaces = spaceMatcher.group(1);
				insertString(Math.min(getLength(), e.getEndOffset()),
						endingSpaces.replaceAll(" ", "&nbsp;").replaceAll("\t", "&#09;"), null);
			}
		}

		/**
		 * The method check if the element contains a url in plain text, and if
		 * so, it creates the html tag HTML.Tag.A to have the url displayed as
		 * an hyperlink.
		 *
		 * @param e
		 *            element that contains the url
		 * @throws BadLocationException
		 * @throws IOException
		 */
		protected void createLink(Element e) throws BadLocationException,
				IOException {

			// This function gets called as we type each character. It
			// continuously parses the text starting from last known hyperlink
			// position till the current caret position and tries to find out
			// whether a link has been entered. If it finds a link, it enables
			// the link and then changes the startOffset to the position where
			// this newly found link has ended.

			int caretPos = getCaretPosition();
			int startOffset = e.getStartOffset();
			int length = e.getEndOffset() - e.getStartOffset();

			boolean endOfDoc = e.getEndOffset() == getLength() + 1;
			// to avoid catching the final '\n' of the document.
			if (endOfDoc) {
				length--;
			}

			// get the entire string starting from previous hyperlink or newline
			// to caret position
			String text = getText(startOffset, length);

			// here we specify to the parser for the stings to parse in given
			// text
			// Case-insensitive matching can also be done by (?i).
			// "\\b" matches a word boundary
			// matcher detects external links
			Matcher matcher = Pattern
					.compile(
							"(?i)(\\b(http://|https://|www.|ftp://|file:/|mailto:)\\S+?)(\\s+|&#?\\w{2,8};|$)")
					.matcher(text);

			String str = "((" + ONTOLOGY_COMPONENT_LINK_PREFIX + "').+?')(" + PATTERN_ONTOLOGY_COMPONENT_LINK_SEP + ")";
			// matcherInternal detects internal links
			Matcher matcherInternal = Pattern.compile(str).matcher(text);

			// Matcher matcherInternal = Pattern.compile(
			// "(?i)(\\b(" + ONTOLOGY_COMPONENT_LINK_PREFIX +
			// "').+)(')").matcher(text);

			int linkfound = -1;
			String url = null;
			String endingSpaces = null;

			if (matcherInternal.find()) {
				url = matcherInternal.group(1);
				endingSpaces = matcherInternal.group(3);
				linkfound = 0;
			} else if (matcher.find()) {
				// if we find a hyperlink in given text
				url = matcher.group(1);
				//endingSpaces = " ";
				endingSpaces = matcher.group(3);
				// Example: if user types "this is a hyperlink www.google.com"
				// then following would be the conents of above String variables
				// url: www.google.com prefix: www. endingSpaces:
				linkfound = 1;
			}

			if (linkfound == 1 || linkfound == 0) {
				// to ignore characters after the caret
				int validPos = 0;
				Matcher dotEndMatcher;
				if (linkfound == 1) {
					validPos = startOffset + matcher.start(3) + 1;
					if (validPos > caretPos) {
						return;
					}
					dotEndMatcher = Pattern.compile(PATTERN_DOT_END_EXTERNAL_LINK).matcher(url);
				} else {
					validPos = startOffset + matcherInternal.start(3);
					if (validPos > caretPos) {
						return;
					}
					dotEndMatcher = Pattern.compile(PATTERN_DOT_END_ONTOLOGY_COMPONENT_LINK).matcher(url);
				}

				// Ending non alpha characters like [.,?%] shouldn't be included
				// in the url.
				String endingDots = "";
				if (linkfound == 1) {
					if (dotEndMatcher.find()) {
						endingDots = dotEndMatcher.group(1);

						url = dotEndMatcher.replaceFirst("");
					}
				}

				// Example: if user types "this is a hyperlink www.google.com"
				// then matcher.replaceFirst would the text as
				// text: this is a hyperlink <a
				// href='www.google.com'>www.google.com</a>
				if (linkfound == 1) {
					text = matcher.replaceFirst("<a href='" + url + "'>" + url
							+ "</a>" + endingDots + endingSpaces.replaceAll(" ", "&nbsp;").replaceAll("\t", "&#09;"));
				} else {
					String showString = url;
					//TODO: Vivek, please take a look at this code
					// This code needs to be revived. Internal links could show the
					// browser text but have in the link, the full name
/*
					try {
						String frameName = url.substring(2, url.length() - 1);
						KnowledgeBase kb = ProjectManager.getProjectManager().getCurrentProject().getKnowledgeBase();
						Frame frame = kb.getFrame(frameName);
						if (frame != null) {
							showString = ONTOLOGY_COMPONENT_LINK_PREFIX + "'" + frame.getBrowserText() + "'";
						}
					} catch (Exception e2) {
						Log.emptyCatchBlock(e2);
					}
*/
					text = matcherInternal.replaceFirst("<a href='internalLink'>" + showString
							+ "</a>" + endingDots + endingSpaces.replaceAll(" ", "&nbsp;").replaceAll("\t", "&#09;"));
				}


				// e - the branch element whose children will be replaced
				// text - the string to be parsed and assigned to e
				setOuterHTML(e, text);

				// insert initial spaces in normal text which were ignored by
				// the html

				//TODO: Vivek, please look at this code.
				//It goes in an infinite loop, when in OWL and insert internal link

				/*
				Matcher spaceMatcher = Pattern.compile("^(\\s+)").matcher(text);

				if (spaceMatcher.find()) {
					String initialSpaces = spaceMatcher.group(1);
					insertString(startOffset, initialSpaces, null);
				}

				// insert final spaces in normal text ignored by the html
				spaceMatcher = Pattern.compile("(\\s+)$").matcher(text);

				if (spaceMatcher.find()) {
					String extraSpaces = spaceMatcher.group(1);
					int endoffset = e.getEndOffset();
					if (extraSpaces.charAt(extraSpaces.length() - 1) == '\n') {
						extraSpaces = extraSpaces.substring(0, extraSpaces
								.length() - 1);
						endoffset--;
					}
					insertString(Math.min(getLength(), endoffset), extraSpaces,
							null);
				}
				*/

			}
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			// offs - the starting offset >= 0
			// len - the number of characters to remove >= 0
			super.remove(offs, len);
			Element e = getCharacterElement(offs - len);
			computeLinks(e);
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {

			super.insertString(offs, str, a);
			Element e = getCharacterElement(offs);
			computeLinks(e);
		}
	}

}
