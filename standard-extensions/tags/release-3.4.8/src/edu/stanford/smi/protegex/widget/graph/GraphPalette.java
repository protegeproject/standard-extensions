package edu.stanford.smi.protegex.widget.graph;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.nwoods.jgo.JGoGridView;
import com.nwoods.jgo.JGoListPosition;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoPalette;

public class GraphPalette extends JGoPalette {
    private static final long serialVersionUID = -5555348486316683836L;
    final static BufferedImage gBuffer = new BufferedImage(1, 1,
        BufferedImage.TYPE_INT_ARGB);

    public GraphPalette() {
        super();
        setGridWidth(20);
        setGridHeight(20);
        setGridSpot(JGoGridView.GridInvisible);
        setGridOrigin(new Point(0, 5));
        setHidingDisabledScrollbars(true);
    }

    public void layoutItems() {
        // This will force the objects to calculate their sizes when
        // displayed.  If this isn't done, text boundaries may not be
        // accurate.  This causes trouble when positioning objects vertically
        // and possibly horizontally.
        paint(gBuffer.getGraphics());

        int xPos = getGridOrigin().x + getPaddingX();
        int yPos = getGridOrigin().y;

        JGoListPosition pos = getDocument().getFirstObjectPos();
        while (pos != null) {
            JGoObject obj = getDocument().getObjectAtPos(pos);
            if (obj instanceof Node) {
                Node node = (Node) obj;
                node.setSize(40, 40);
                yPos += getPaddingY(node);
                node.setTopLeft(xPos, yPos);
                yPos += node.getHeight() + 5 + getPaddingY(node);
            }
            pos = getDocument().getNextObjectPosAtTop(pos);
        }
    }

    private int getPaddingY(Node node) {
        int yPad = 0;

        int dHeight = node.getDrawable().getHeight();
        int lHeight = node.getLabel().getHeight();
        if (lHeight > dHeight) {
            yPad = (lHeight - dHeight) / 2;
        }

        return yPad;
    }

    private int getPaddingX() {
        int xPad = 0;
        JGoListPosition pos = getDocument().getFirstObjectPos();
        while (pos != null) {
            JGoObject obj = getDocument().getObjectAtPos(pos);
            if (obj instanceof Node) {
                Node node = (Node) obj;
                NodeLabel label = (NodeLabel) node.getLabel();
                int labelWidth = label.getCustomWrapWidth() / 2;
                int availableWidth = node.getWidth() / 2;
                if (labelWidth > availableWidth) {
                    int difference = labelWidth - availableWidth;
                    if (difference > xPad) {
                        xPad = difference;
                    }
                }
            }
            pos = getDocument().getNextObjectPosAtTop(pos);
        }
        return xPad + 3;
    }
}