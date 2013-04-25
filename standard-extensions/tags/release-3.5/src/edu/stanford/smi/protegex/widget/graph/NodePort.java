package edu.stanford.smi.protegex.widget.graph;

// java
import java.awt.*;
import java.awt.image.*;

import com.nwoods.jgo.*;

public class NodePort extends JGoPort {

    private static final long serialVersionUID = -4067234535541350894L;

    public NodePort() {
        super();
        setSelectable(false);
        setDraggable(false);
        setStyle(JGoPort.StyleHidden);
        setToSpot(JGoPort.NoSpot);
        setFromSpot(JGoPort.NoSpot);
        setValidDuplicateLinks(true);
        setSize(5, 5);
    }

    public boolean validLink(JGoPort to) {
        boolean validLink = false;

        if ((super.validLink(to)) && (getParent() != to.getParent()) &&
            (to instanceof NodePort)) {

            GraphDocument doc = (GraphDocument) getDocument();
            LinkUtilities lu = new LinkUtilities(doc.getKB(),
                    doc.getPropertyList(), this, to);
            if ((lu.hasValidConnectorSlot()) || (lu.hasValidRelations())) {
                validLink = true;
            }
        }

        return validLink;
    }

    /**
     * Return a point on the edge of the drawable rather than a point on the
     * port itself.
     */
    public Point getLinkPointFromPoint(int x, int y, Point p) {

        // Parent node for this port.
        Node node = getNode();

        // Bounding rectangle for the node's drawable object.
        JGoDrawable obj = node.getDrawable();
        Rectangle rect = obj.getBoundingRect();

        // x and y coordinates of the center of the port object.
        int cx = getLeft() + getWidth() / 2;
        int cy = getTop() + getHeight() / 2;

        if (p == null) p = new Point();
        p.x = x;
        p.y = y;

        // If (x, y) is inside the object, just return it instead of finding
        // the edge intersection
        if (!obj.isPointInObj(p)) {
            if (obj instanceof JGoRectangle) {
                JGoRectangle.getNearestIntersectionPoint(rect.x, rect.y,
                        rect.width, rect.height, x, y, cx, cy, p);
            } else if (obj instanceof JGoEllipse) {
                JGoEllipse.getNearestIntersectionPoint(rect.x,
                        rect.y, rect.width, rect.height, x, y, p);
            } else {
                int NE = 0;
                int SE = 1;
                int SW = 2;
                int NW = 3;

                // Get the default point (which is the center of the bounding rectangle).
                Rectangle bRect = getBoundingRect();
                Point defaultPoint = new Point(bRect.x + (int)(.5 * bRect.width), bRect.y + (int)(.5 * bRect.height));

                int direction;
                // Use to determine direction.
                int xdelta = defaultPoint.x - x;
                int ydelta = defaultPoint.y - y;

                // Is the direction up or down?
                boolean up = defaultPoint.y < y;
                // Use pos/neg of deltas to determine direction.
                if (xdelta >= 0) {
                    if (ydelta >= 0) {
                        direction = SE;
                    } else {
                        direction = NE;
                    }
                } else {
                    if (ydelta >= 0) {
                        direction = SW;
                    } else {
                        direction = NW;
                    }
                }

                // Create a stroke which represents the default link.
                JGoStroke stroke = new JGoStroke();
                stroke.addPoint(x, y);
                stroke.addPoint(defaultPoint);

                // Graphics buffer will be big enough to hold the drawable
                // plus a ten pixel inset on all sides.
                int gBufferWidth = rect.width + 20;
                int gBufferHeight = rect.height + 20;

                BufferedImage gBuffer = new BufferedImage(gBufferWidth,
                        gBufferHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) gBuffer.getGraphics();
                // Paint buffer white.
                g.setColor(Color.white);
                g.fillRect(0, 0, gBufferWidth, gBufferHeight);

                // Translate to graphics buffer.
                g.translate(-rect.x, -rect.y);
                // Translate so that drawable draws within insets.
                g.translate(10, 10);

                // Paint the stroke to the buffer first.
                g.setColor(Color.black);
                stroke.paint(g, null);

                // Temporarily set drawable color to white.
                JGoBrush brush = obj.getBrush();
                obj.setBrush(JGoBrush.makeStockBrush(Color.white));
                // Paint the drawable to the buffer.
                // This will overwrite the stroke up to the point where the
                // stroke and drawable meet exactly.
                obj.paint(g, null);
                // Restore drawable to original color.
                obj.setBrush(brush);

                g.translate(-10, -10);
                g.translate(rect.x, rect.y);

                // Now find the last black pixel from the outside of the
                // buffer.
                int rgbBlack = Color.black.getRGB();
                int c;
                int blackX = -1;
                int blackY = -1;
                boolean blackFound = false;
                int iStart, iLimit, iIncr;

                // Use stroke direction to determine search direction and set
                // loop paramaters.
                if ((direction == NW) || (direction == SW)) {
                    // Search from right to left.
                    iStart = gBufferWidth - 1;
                    iLimit = 0;
                    iIncr = -1;
                } else {
                    // Search from left to right.
                    iStart = 0;
                    iLimit = gBufferWidth - 1;
                    iIncr = 1;
                }

                //Search the graphics buffer.
                for (int i = iStart; i != iLimit; i += iIncr) {
                    blackFound = false;
                    for (int j = 0; j < gBufferHeight; j++) {
                        c = gBuffer.getRGB(i, j);
                        if (c == rgbBlack) {
                            blackFound = true;
                            blackX = i;
                            blackY = j;
                            // If direction is up, the first black point is
                            // the one we want so return now.  Otherwise,
                            // continue searching to find the last black point.
                            if (up) break;
                        }
                    }
                    // Only want to break out of outermost loop when an
                    // innermost loop finds no black point and a black point
                    // has already been found.
                    if ((!blackFound) && (blackX != -1) && (blackY != -1)) {
                        break;
                    }
                }
                // Adjust location of black point to screen coords.
                p.x = rect.x + blackX - 10;
                p.y = rect.y + blackY - 10;
                return p;
            }
        }
        return p;
    }

    /**
     * Convenience method for returning the parent as a Node.
     */
    public Node getNode() {
        return (Node) getParent();
    }

}