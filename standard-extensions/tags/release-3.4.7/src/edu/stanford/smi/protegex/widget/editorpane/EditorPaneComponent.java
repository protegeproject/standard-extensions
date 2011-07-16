package edu.stanford.smi.protegex.widget.editorpane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Highlighter.Highlight;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.ConfigureAction;
import edu.stanford.smi.protege.ui.DisplayUtilities;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.BrowserLauncher;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;

/*
 * @author Vivek Tripathi (vivekyt@stanford.edu)
 *
 */

public class EditorPaneComponent {

	private EditorPaneLinkDetector e;
	private boolean OWL = false;
	private final String ChatHelpURL = "http://protegewiki.stanford.edu/index.php/HTML_Chat";
	private final String EditorPaneHelpURL = "http://protegewiki.stanford.edu/index.php/EditorPane";
	private final String highlightstarttag1 = "<font color=\"blue\" style=\"background-color: yellow\">";
	private final String highlightstarttag2 = "<font style=\"background-color: yellow\" color=\"blue\">";
	private final String highlightendtag = "</font>";

	public EditorPaneLinkDetector createEditorPaneLinkDetector() {
		return createEditorPaneLinkDetector(true, true);
	}

	public EditorPaneLinkDetector createEditorPaneLinkDetector(
			boolean editable, boolean detectEnter) {
		e = new EditorPaneLinkDetector(editable, detectEnter);
		e.setAutoscrolls(true);
		// e.addMouseListener(ml);
		return e;
	}

	public LabeledComponent createUI(EditorPaneLinkDetector e) {
		LabeledComponent lc = new LabeledComponent("", e, false, true);

		lc.addHeaderButton(getBoldAction());
		lc.addHeaderButton(getItalicsAction());
		lc.addHeaderButton(getUnderlineAction());
		lc.addHeaderButton(getStrikeThroughAction());
		lc.addHeaderButton(getHighligherAction());

		JButton helpButton = new JButton("?");

		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					BrowserLauncher.openURL(ChatHelpURL);
				} catch (IOException e1) {
					Log.emptyCatchBlock(e1);
				}
			}
		});

		KnowledgeBase kb = ProjectManager.getProjectManager()
		.getCurrentProject().getKnowledgeBase();
		String[] options1 = { "Add Internal Link To", "Class", "Property",
		"Individual" };
		String[] options2 = { "Add Internal Link To", "Class", "Slot",
		"Instance" };
		JComboBox internalLinkTo;
		if (PluginUtilities.isOWL(kb)) {
			OWL = true;
			internalLinkTo = new JComboBox(options1);
		} else {
			OWL = false;
			internalLinkTo = new JComboBox(options2);
		}

		internalLinkTo.setRenderer(new FrameRenderer() {
			private static final long serialVersionUID = 2908749286411682298L;

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
				} else if (value.equals("Property")) {
					setMainIcon(Icons.getSlotIcon());
					setMainText("Property");
				} else if (value.equals("Individual")) {
					setMainIcon(Icons.getInstanceIcon());
					setMainText("Individual");
				} else {
					super.load(value);
				}
			}
		});

		internalLinkTo.setSelectedIndex(0);
		internalLinkTo.addActionListener(getAddInternalLinkActionListener());

		JComponent lc1 = new JPanel();

		lc1.add(internalLinkTo, BorderLayout.WEST);
		lc1.add(helpButton, BorderLayout.EAST);
		lc.setHeaderComponent(lc1, BorderLayout.WEST);

		return lc;
	}

	protected Action createILAction() {
		return new ConfigureAction() {
			private static final long serialVersionUID = -2073953813575426441L;

            @Override
			public void loadPopupMenu(JPopupMenu menu) {
				// menu.add(createSetDisplaySlotAction());
				menu.add(createShowAllInstancesAction());
			}
		};
	}

	protected JMenuItem createShowAllInstancesAction() {
		Action action = new AbstractAction("Class") {
			private static final long serialVersionUID = -1581681076251717145L;

            public void actionPerformed(ActionEvent event) {
				KnowledgeBase kb = ProjectManager.getProjectManager()
				.getCurrentProject().getKnowledgeBase();
				Instance cls = DisplayUtilities.pickCls(e, kb, kb.getClses());
				String linkname = cls.getName();

			}
		};
		JMenuItem item = new JCheckBoxMenuItem(action);

		return item;
	}

	public void setOWLMode(boolean o) {
		OWL = o;
	}

	public ActionListener getAddInternalLinkActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e1) {
				JComboBox cb = (JComboBox) e1.getSource();
				int selection = cb.getSelectedIndex();
				cb.setSelectedIndex(0);

				insertInternalLink(selection, OWL);
				cb.setSelectedIndex(0);
				e.grabFocus();

			}
		};
	}

	private void insertInternalLink(int selection, boolean isOWL) {
		String linkname = null;
		KnowledgeBase kb = ProjectManager.getProjectManager()
		.getCurrentProject().getKnowledgeBase();

		switch (selection) {
		case 0:
			// do nothing
			break;
		case 3:
			Instance instance = DisplayUtilities.pickInstance(e, kb);
			if (instance == null) {	return;	}
			linkname = instance.getName();
			break;
		case 1:
			Instance cls = DisplayUtilities.pickCls(e, kb, kb.getRootClses());
			if (cls == null) { return; }
			linkname = cls.getName();
			break;
		case 2:
			Slot slot = DisplayUtilities.pickSlot(e, kb.getSlots());
			if (slot == null) {	return;	}
			linkname = slot.getName();
			break;
		}

		linkname = "@'" + linkname + "' ";
		Document doc = e.getDocument();
		try {
			doc.insertString(e.getCaretPosition(), linkname, null);
		} catch (BadLocationException ble) {
			Log.emptyCatchBlock(ble);
		}
	}

	public AllowableAction getHTMLHighlightAction() {
		return new AllowableAction("Highlight", ComponentUtilities.loadImageIcon(EditorPaneComponent.class,
		"images/text_htmlhighlight.gif"), null) {
			private static final long serialVersionUID = 6800135092366317185L;

            public void actionPerformed(ActionEvent arg0) {

				try {
					e.getDocument().insertString(e.getSelectionStart(),
							"--highlightstart--", null);
					e.getDocument().insertString(e.getSelectionEnd(),
							"--highlightend--", null);
				} catch (BadLocationException ble) {
					Log.emptyCatchBlock(ble);
					return;
				}

				String text = e.getText();

				String modifiedtext;
				if (text != null) {
					modifiedtext = insertBRForNewline(text);
					modifiedtext = modifiedtext
					.replaceAll("--highlightstart--",
					"<font color=\"blue\" style=\"background-color: yellow\">");
					modifiedtext = modifiedtext.replaceAll("--highlightend--",
					"</font>");
				} else {
					modifiedtext = text;
				}
				e.setText(modifiedtext);
			}
		};
	}

	public AllowableAction getHighligherAction() {
		return new AllowableAction("Highlight", ComponentUtilities
				.loadImageIcon(EditorPaneComponent.class,
				"images/text_highlight.gif"), null) {
			private static final long serialVersionUID = -1717578477901220417L;

            public void actionPerformed(ActionEvent arg0) {

				try {
					e.getDocument().insertString(e.getSelectionStart(),
							"--UNstart--", null);
					e.getDocument().insertString(e.getSelectionEnd(),
							"--UNend--", null);
				} catch (BadLocationException ble) {
					Log.emptyCatchBlock(ble);
					return;
				}

				String text = e.getText();
				String selection;
				String modifiedtext;

				if (text != null) {
					boolean unhighlight = false;
					text = insertBRForNewline(text);
					int START = text.indexOf("--UNstart--");
					int END = text.indexOf("--UNend--");

					if (START == -1 || END == -1) {
						return;
					}

					selection = text.substring(START, END);

					if (selection.indexOf(highlightstarttag1) == "--UNstart--"
						.length()
						|| selection.indexOf(highlightstarttag2) == "--UNstart--"
							.length()) {
						unhighlight = true;
					}

					selection = selection.replaceAll(highlightstarttag1, "");
					selection = selection.replaceAll(highlightstarttag2, "");
					selection = selection.replaceAll(highlightendtag, "");

					if (unhighlight == false) {
						selection = highlightstarttag1 + selection
						+ highlightendtag;
					}
					modifiedtext = text.substring(0, START) + selection
					+ text.substring(END);
					modifiedtext = modifiedtext.replaceAll("--UNend--", "");
					modifiedtext = modifiedtext.replaceAll("--UNstart--", "");

				} else {
					modifiedtext = text;
				}
				e.setText(modifiedtext);
			}
		};
	}

	public AllowableAction getUnhighlightAction() {
		return new AllowableAction("Unhighlight", ComponentUtilities
				.loadImageIcon(EditorPaneComponent.class,
				"images/text_unhighlight.gif"), null) {
			private static final long serialVersionUID = 5978503892815299632L;

            public void actionPerformed(ActionEvent arg0) {

				try {
					e.getDocument().insertString(e.getSelectionStart(),
							"--UNstart--", null);
					e.getDocument().insertString(e.getSelectionEnd(),
							"--UNend--", null);
				} catch (BadLocationException ble) {
					Log.emptyCatchBlock(ble);
					return;
				}

				String text = e.getText();
				String selection;
				String beforeStart;
				String modifiedtext;
				if (text != null) {
					text = insertBRForNewline(text);
					int START = text.indexOf("--UNstart--");
					int END = text.indexOf("--UNend--");
					if (START == -1 || END == -1) {
						return;
					}

					selection = text.substring(START, END);
					selection = selection.replaceAll(highlightstarttag1, "");
					selection = selection.replaceAll(highlightstarttag2, "");
					selection = selection.replaceAll(highlightendtag, "");

					modifiedtext = text.substring(0, START) + selection
					+ text.substring(END);
					modifiedtext = modifiedtext.replaceAll("--UNend--", "");
					modifiedtext = modifiedtext.replaceAll("--UNstart--", "");

				} else {
					modifiedtext = text;
				}
				e.setText(modifiedtext);
			}
		};
	}

	public AllowableAction getHighlightAction() {
		return new AllowableAction("Highlight", ComponentUtilities
				.loadImageIcon(EditorPaneComponent.class,
				"images/text_highlight.gif"), null) {
			private static final long serialVersionUID = 3409061654966089792L;

            public void actionPerformed(ActionEvent arg0) {
				/*
				 * if(bold.isSelected()) bold.setSelected(false); else
				 * bold.setSelected(true);
				 */
				int flag = 0;
				Highlighter hilite = e.getHighlighter();

				Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(
						Color.yellow);
				try {
					Highlighter.Highlight[] hilites = hilite.getHighlights();
					for (Highlight hilite2 : hilites) {
						if (hilite2.getStartOffset() <= e.getSelectionStart()
								&& hilite2.getEndOffset() >= e
								.getSelectionEnd()) {
							hilite.removeHighlight(hilite2);
							hilite.addHighlight(hilite2.getStartOffset(), e
									.getSelectionStart(), myHighlightPainter);
							hilite.addHighlight(e.getSelectionEnd(), hilite2
									.getEndOffset(), myHighlightPainter);
							flag = 1;
							break;
						}
					}
					if (flag == 1) {
						for (Highlight hilite2 : hilites) {
							if (hilite2.getStartOffset() >= e
									.getSelectionStart()
									&& hilite2.getEndOffset() <= e
									.getSelectionEnd()) {
								hilite.removeHighlight(hilite2);
							}
						}
					}

					if (flag == 0) {
						hilite.addHighlight(e.getSelectionStart(), e
								.getSelectionEnd(), myHighlightPainter);
					}
				} catch (BadLocationException e) {
					Log.emptyCatchBlock(e);
				}
			}
		};

	}

	public AllowableAction getBoldAction() {
		return new AllowableAction("Bold", ComponentUtilities.loadImageIcon(
				EditorPaneComponent.class, "images/text_bold.gif"), null) {
			private static final long serialVersionUID = 1294177624137798115L;

            public void actionPerformed(ActionEvent arg0) {
				new StyledEditorKit.BoldAction().actionPerformed(arg0);
			}
		};
	}


	public AllowableAction getItalicsAction() {

		return new AllowableAction("Italics", ComponentUtilities.loadImageIcon(
				EditorPaneComponent.class, "images/text_italic.gif"), null) {
			private static final long serialVersionUID = 6782336002666970878L;

            public void actionPerformed(ActionEvent arg0) {
				new StyledEditorKit.ItalicAction().actionPerformed(arg0);
			}
		};
	}

	public AllowableAction getUnderlineAction() {
		return new AllowableAction("Underline", ComponentUtilities
				.loadImageIcon(EditorPaneComponent.class,
				"images/text_underline.gif"), null) {
			private static final long serialVersionUID = 492531846281451286L;

            public void actionPerformed(ActionEvent arg0) {
				new StyledEditorKit.UnderlineAction().actionPerformed(arg0);
			}
		};
	}

	public AllowableAction getEditorPaneHelpAction() {
		return new AllowableAction("Help", ComponentUtilities.loadImageIcon(
				EditorPaneComponent.class, "images/Help.gif"), null) {
			private static final long serialVersionUID = 6461423862725443416L;

            public void actionPerformed(ActionEvent arg0) {
				try {
					BrowserLauncher.openURL(EditorPaneHelpURL);
				} catch (IOException e1) {
					Log.emptyCatchBlock(e1);
				}
			}
		};
	}

	public AllowableAction getStrikeThroughAction() {
		return new AllowableAction("StrikeThrough", ComponentUtilities
				.loadImageIcon(EditorPaneComponent.class,
				"images/text_strike.gif"), null) {
			private static final long serialVersionUID = -23800044774747999L;

            public void actionPerformed(ActionEvent arg0) {
				new StrikeThroughAction().actionPerformed(arg0);
			}
		};
	}

	public AllowableAction getInsertImageAction() {
		return new AllowableAction("Insert Image", ComponentUtilities
				.loadImageIcon(EditorPaneComponent.class,
				"images/imagegallery.gif"), null) {
			private static final long serialVersionUID = -6648915966951648767L;

            public void actionPerformed(ActionEvent arg0) {
				insertImage();
			}
		};
	}

	private void insertImage() {
		Document doc = e.getDocument();

		String s = (String) JOptionPane.showInputDialog(e,
				"Please enter the URL Location of Image:", "Insert Image",
				JOptionPane.PLAIN_MESSAGE, null, null,
		"http://protege.stanford.edu/images/ProtegeLogo.gif");

		// If a string was returned, say so.
		if (s == null || s.length() == 0) {
			return;
		}

		if (s.indexOf(".gif") == -1 && s.indexOf(".jpg") == -1
				&& s.indexOf(".bmp") == -1 && s.indexOf(".png") == -1
				&& s.indexOf(".ico") == -1) {
			return;
		}

		s = "<img src=\"" + s + "\">";
		try {
			doc.insertString(e.getCaretPosition(), "---image---", null);
		} catch (BadLocationException ble) {
			Log.emptyCatchBlock(ble);
			return;
		}

		String text = e.getText();
		String modifiedtext;
		if (text != null) {
			modifiedtext = insertBRForNewline(text);
			modifiedtext = modifiedtext.replaceAll("---image---", s);
		} else {
			modifiedtext = text;

		}
		e.setText(modifiedtext);
	}

	// this functions strips off the </p> from text. Also it inserts <br> for
	// all newlines
	// which user had entered
	public String insertBRForNewline(String s) {
		boolean pPresent = false;
		int pAtIndex = -1;
		int pStyleAtIndex = -1;

		// find index from where user entered text starts
		int beginIndex = s.indexOf("<body>") + "<body>".length() + 1;

		// s1 contains initial html tags before the user entered text
		String s1 = s.substring(0, beginIndex);

		// s2 contains the user entered text along with intermediate html tags
		String s2 = s.substring(beginIndex, s.indexOf("</body>") - 1);

		// s3 contains finishing html tags after the user entered text
		String s3 = s.substring(s.indexOf("</body>"));

		String modifiedS2;

		if (s2.indexOf("</p>") != -1) // this means html body recognizes
		{ // new lines in the text and puts
			// </p> for each new line
			pPresent = true;
			modifiedS2 = s2;
			String part1, part2, part3;

			// find where first </p> and <p style is present in the text
			pAtIndex = s2.indexOf("</p>");
			pStyleAtIndex = s2.indexOf("<p style");

			while (pPresent) {
				// next 3 lines replace <p style="margin-top: 0"> with null
				part1 = modifiedS2.substring(0, pStyleAtIndex);
				part2 = modifiedS2.substring(pStyleAtIndex
						+ "<p style=\"margin-top: 0\">".length() + 1);
				modifiedS2 = part1 + part2;

				// next 5 lines replace </p> with <br>
				pAtIndex = modifiedS2.indexOf("</p>");
				part1 = modifiedS2.substring(0, pAtIndex);
				part2 = "<br>";
				part3 = modifiedS2.substring(pAtIndex + "</p".length() + 1);
				modifiedS2 = part1 + part2 + part3;

				// here we check if more replacement needs to be done else we
				// exit the loop
				pStyleAtIndex = modifiedS2.indexOf("<p style");
				if (pStyleAtIndex < 0) {
					pPresent = false;
					break;
				}
			}

			// since we are writing html to the pins file, we don't need any \n.
			// We
			// already have put <br> wherever newlines were starting
			modifiedS2 = modifiedS2.replaceAll("\n    ", "");
			modifiedS2 = modifiedS2.replaceAll("\n", "");

		} else {
			// this means that editor pane has not accounted for the new lines
			// which the user might have entered and hence we need to replace
			// all \n with <br>
			s2 = s2.replaceAll("\n    ", "");
			modifiedS2 = s2.replaceAll("\n", "<br>");

		}

		// to remove the last <br> from this string. otherwise extra <br> shows
		// up in the end
		int length = modifiedS2.length();

		while (length >= 0) {
			if (modifiedS2.substring(length - 1).indexOf("<br>") != -1) {
				break;
			} else {
				length = length - 1;
			}
		}

		// here we obtain the part which we will be removing in "remainder"
		// string
		// and check that it doesn't contain any actual text apart from <br> and
		// spaces
		String remainder = modifiedS2.substring(length - 1);
		remainder = remainder.replaceAll("<br>", "");
		remainder = remainder.replaceAll(" ", "");
		if (remainder.length() == 0) // this means that the part we are removing
			// from
		{ // modifiedText just contains one <br> and some spaces
			modifiedS2 = modifiedS2.substring(0, length - 1);
		}
		// else the part we want to remove also contains some other characters
		// and we don't change modifiedText since we don't want to get into
		// trouble
		// by removing anything other than spaces and <br>.

		// we were operating on the middle part of entire string. now we
		// complete the original string and return it.
		String modifiedS = s1 + modifiedS2 + s3;
		return modifiedS;
	}

}

// A private subclass of the default highlight painter
class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
	public MyHighlightPainter(Color color) {
		super(color);
	}
}
