package edu.stanford.smi.protegex.widget.graph;

// java
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// nwoods
import com.nwoods.jgo.JGoArea;
import com.nwoods.jgo.JGoText;
import com.nwoods.jgo.JGoView;

// stanford
import edu.stanford.smi.protege.model.BrowserSlotPattern;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.resource.Text;

public class NodeLabel extends JGoText {
    private static final long serialVersionUID = -6286813020026246705L;
    private String myOldText;
    private GraphDocument myDoc;
    final static BufferedImage gBuffer = new BufferedImage(1, 1,
            BufferedImage.TYPE_INT_ARGB);

    public NodeLabel() {
        super();
    }

    public NodeLabel(String text)
    {
        super(text);
        setDraggable(false);
        setSelectable(false);
        setEditable(true);
        setEditOnSingleClick(false);
        setAlignment(JGoText.ALIGN_CENTER);
        setTransparent(true);
        setMultiline(true);
        setWrapping(true);
        setClipping(false);
    }

    public void doStartEdit(JGoView view, Point vc) {
        myOldText = getText();
        myDoc = (GraphDocument) view.getDocument();

        JGoArea parent = getParent();
        if (parent instanceof Node) {
            Node node = (Node) parent;
            Instance instance = node.getInstance();
            Cls cls = instance.getDirectType();
            BrowserSlotPattern pattern = cls.getBrowserSlotPattern();

           /**
            * Don't allow in-place text editing if:
            *
            * a). there is no display slot set
            * b). multiple display slots are configured
            * c). display slot is not of type string
            * d). :NAME slot is not a template slot on the Cls object
            *     (this only matters if :NAME is set to display slot)
            */

            if (pattern == null) {
                setEditable(false);
                return;
            }

            ArrayList browserSlots = new ArrayList(pattern.getSlots());
            if (browserSlots.size() != 1) {
                setEditable(false);
                return;
            }

            Slot browserSlot = pattern.getFirstSlot();
            if (browserSlot.getValueType() != ValueType.STRING) {
                setEditable(false);
                return;
            }

            if ((browserSlot.getName().equals(Model.Slot.NAME)) &&
                (!cls.hasTemplateSlot(browserSlot))) {
                setEditable(false);
                return;
            }

            super.doStartEdit(view, vc);
        }
    }

    public void doEndEdit() {
        // Call super.doEndEdit so that the JTextComponent will go away.
        super.doEndEdit();

        JGoArea parent = getParent();
        if (parent instanceof Node) {
            Node node = (Node) parent;
            Instance instance = node.getInstance();
            Cls cls = instance.getDirectType();
            Slot slot = cls.getBrowserSlotPattern().getFirstSlot();

            if (slot.getName() == Model.Slot.NAME) {
                // If the form browser key is the :NAME slot, make sure
                // the new text is not the same as any other frame in the
                // system.
                KnowledgeBase kb = instance.getKnowledgeBase();
                if (!kb.containsFrame(getText())) {
                    myDoc.setExternalUpdate(true);
                    try {
                        instance = (Instance) instance.rename(getText());
                    } catch (java.lang.IllegalArgumentException e) {
                        setText(myOldText);
                        final String errorMessage = e.getMessage();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                            	JOptionPane.showMessageDialog(getView(),
                                        errorMessage,
                                        Text.getProgramNameAndVersion(),
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                    myDoc.setExternalUpdate(false);
                } else {
                    setText(myOldText);
                }
            } else {
                instance.setOwnSlotValue(slot, getText());
            }
        }
    }

    public void setCustomWrapWidth() {
        // Shortcut.
        if (!isWrapping() || !isMultiline()) return;

        Node parent = (Node) getParent();
        int drawableWidth = parent.getDrawable().getBoundingRect().width - 2;
        int customWidth = getCustomWrapWidth();
        int maxWidth = Math.max(customWidth, drawableWidth);

        setWidth(maxWidth);
        setWrappingWidth(maxWidth);
        setSpotLocation(Center, parent.getDrawable(), Center);
    }

    // JGo's default functionality is to wrap on words and then wrap on letters.
    // We don't want this - we want to wrap on words and if the bounding
    // rectangle of the node becomes too small, we set the wrapping width
    // to the width of the widest word and the label draws outside the bounds
    // of the node's bounding rect.
    public int getCustomWrapWidth() {
        int width = -1;
        String myText = getText();
        Graphics2D g = (Graphics2D) gBuffer.getGraphics();
        g.setFont(getFont());

        if (myText.trim().indexOf(" ") < 0) {
            return g.getFontMetrics().stringWidth(myText);
        }

        StringTokenizer st = new StringTokenizer(myText);
        while (st.hasMoreTokens()) {
            int i = g.getFontMetrics().stringWidth(st.nextToken());
            width = Math.max(width, i);
        }

        return width;
    }

    public void paint(Graphics2D g, JGoView view) {
        setCustomWrapWidth();
        super.paint(g, view);
    }
}
