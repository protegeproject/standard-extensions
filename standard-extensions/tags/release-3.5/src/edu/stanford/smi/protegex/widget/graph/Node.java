package edu.stanford.smi.protegex.widget.graph;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import com.nwoods.jgo.JGoArea;
import com.nwoods.jgo.JGoBasicNode;
import com.nwoods.jgo.JGoBrush;
import com.nwoods.jgo.JGoCopyEnvironment;
import com.nwoods.jgo.JGoDrawable;
import com.nwoods.jgo.JGoEllipse;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoPen;
import com.nwoods.jgo.JGoPolygon;
import com.nwoods.jgo.JGoPort;
import com.nwoods.jgo.JGoRectangle;
import com.nwoods.jgo.JGoRoundRect;
import com.nwoods.jgo.JGoText;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

/**
 * An area that represents a graph node.  Contains one label, one port, and
 * can have different shapes that are configurable by the user in the graph
 * widget configuration dialog.
 *
 * @author Jennifer Vendetti
 */
public class Node extends JGoBasicNode {
    private static final long serialVersionUID = -5078826264550259620L;
    private Instance instance = null;
    private JGoDrawable drawable = null;
    private NodeLabel label = null;
    private NodePort port = null;
    private String connectorSlot = null;
    private String uniqueName = null;

    public Node() {
        super();
    }

    public JGoDrawable createDrawable(String shape) {
        JGoDrawable d = null;

        if (shape.compareTo(GraphTypes.DIAMOND) == 0) {
            JGoPolygon p = new JGoPolygon();
            p.addPoint(0, 20);
            p.addPoint(20, 0);
            p.addPoint(40, 20);
            p.addPoint(20, 40);
            d = p;
        } else if (shape.compareTo(GraphTypes.ELLIPSE) == 0) {
            d = new JGoEllipse();
        } else if (shape.compareTo(GraphTypes.HEXAGON) == 0) {
            JGoPolygon p = new JGoPolygon();
            p.addPoint(0, 30);
            p.addPoint(20, 0);
            p.addPoint(40, 0);
            p.addPoint(60, 30);
            p.addPoint(40, 60);
            p.addPoint(20, 60);
            d = p;
        } else if (shape.compareTo(GraphTypes.INVERTED_TRIANGLE) == 0) {
            JGoPolygon p = new JGoPolygon();
            p.addPoint(0, 0);
            p.addPoint(40, 0);
            p.addPoint(20, 40);
            d = p;
        } else if (shape.compareTo(GraphTypes.OCTAGON) == 0) {
            JGoPolygon p = new JGoPolygon();
            p.addPoint(20, 0);
            p.addPoint(40, 0);
            p.addPoint(60, 20);
            p.addPoint(60, 40);
            p.addPoint(40, 60);
            p.addPoint(20, 60);
            p.addPoint(0, 40);
            p.addPoint(0, 20);
            d = p;
        } else if (shape.compareTo(GraphTypes.PENTAGON) == 0) {
            JGoPolygon p = new JGoPolygon();
            p.addPoint(30, 0);
            p.addPoint(60, 30);
            p.addPoint(45, 60);
            p.addPoint(15, 60);
            p.addPoint(0, 30);
            d = p;
        } else if (shape.compareTo(GraphTypes.RECTANGLE) == 0) {
            d = new JGoRectangle();
        } else if (shape.compareTo(GraphTypes.ROUNDED_RECTANGLE) == 0) {
            d = new JGoRoundRect(new Dimension(15, 30));
        } else if (shape.compareTo(GraphTypes.TRIANGLE) == 0) {
            JGoPolygon p = new JGoPolygon();
            p.addPoint(20, 0);
            p.addPoint(40, 20);
            p.addPoint(0, 20);
            d = p;
        }

        // Don't want the default black line around the drawable.
        d.setPen(JGoPen.Null);

        d.setSelectable(false);
        d.setDraggable(false);
        d.setSize(50, 50);
        return d;
    }

    public JGoText createLabel(String labelText) {
        NodeLabel label = new NodeLabel(labelText);
        return label;
    }

    public void initialize(Point loc, String shape, String labelText) {
        setSelectable(false);
        setGrabChildSelection(true);
        setDraggable(true);
        setResizable(true);

        drawable = createDrawable(shape);

        // Calculate our desired bounding rectangle.
        Rectangle rect = new Rectangle(loc, new Dimension(drawable.getWidth(),
                drawable.getHeight()));

        // Can't setLocation until drawable exists.
        setLocation(loc);

        if (labelText != null) {
            label = (NodeLabel) createLabel(labelText);
            setLabelSpot(Center);
        }

        port = new NodePort();

        addObjectAtHead(drawable);
        addObjectAtTail(port);
        if (label != null)
            addObjectAtTail(label);

        layoutChildren();

        // After JGoArea has set bounding rectangle according to it's default
        // behavior, set it back to what we want it to be.
        this.setBoundingRect(rect);

        // Change to new geometry.  See geometryChange for details.
        geometryChange(new Rectangle(0, 0));
    }

    public void layoutChildren() {
        JGoDrawable drawable = getDrawable();
        if (drawable == null) { return; }

        if (getLabel() != null) {
            setLabelSpot(Center);
        }

        if (getPort() != null) {
            getPort().setBoundingRect(drawable.getBoundingRect());
            getPort().setSpotLocation(Center, drawable, Center);
        }
    }

    public JGoObject copyObject(JGoCopyEnvironment env) {
        Node newobj = (Node) super.copyObject(env);
        if (newobj != null) {
            // Copy user defined data here.  All of the JGoObjects that are part
            // of this area are copied separately by the copyChildren method.
            newobj.instance = instance;
            newobj.connectorSlot = connectorSlot;
            newobj.uniqueName = uniqueName;
        }
        return newobj;
    }

    public void copyChildren(JGoArea newarea, JGoCopyEnvironment env) {
        super.copyChildren(newarea, env);
        Node newobj = (Node) newarea;

        if (drawable != null) {
            newobj.drawable = (JGoDrawable) env.get(drawable);
        }
        if (port != null) {
            newobj.port = (NodePort) env.get(port);
        }
        if (label != null) {
            newobj.label = (NodeLabel) env.get(label);
        }
    }

    public void geometryChange(Rectangle prevRect) {
        if ((prevRect.width == getWidth()) &&
            (prevRect.height == getHeight())) {
            super.geometryChange(prevRect);
        } else {
            // Default behavior from JGo is that prevRect is calculated as a
            // union of all child rectangles.  This is not what we want - we
            // want it to always be equal to drawable.
            prevRect = drawable.getBoundingRect();

            double scaleFactorX = 1;
            if (prevRect.width != 0) {
                scaleFactorX = ((double) getWidth()) / ((double) prevRect.width);
            }

            double scaleFactorY = 1;
            if (prevRect.height != 0) {
                scaleFactorY = ((double) getHeight()) / ((double) prevRect.height);
            }

            JGoDrawable obj = getDrawable();
            if (obj != null) {
                int newRectx = getLeft() + (int) Math.rint((obj.getLeft() - prevRect.x) * scaleFactorX);
                int newRecty = getTop() + (int) Math.rint((obj.getTop() - prevRect.y) * scaleFactorY);

                int newRectwidth = (int) Math.rint(obj.getWidth() * scaleFactorX);
                int newRectheight = (int) Math.rint(obj.getHeight() * scaleFactorY);

                obj.setBoundingRect(newRectx, newRecty, newRectwidth, newRectheight);
            }

            layoutChildren();
        }
    }

    protected boolean geometryChangeChild(JGoObject child, Rectangle prevRect) {
        if (child instanceof NodeLabel) {
            // We never want the size of the label size to effect the bounding
            // rectangle.
            return false;
        }
        return super.geometryChangeChild(child, prevRect);
    }

    public String getText() {
        String text = null;
        if (label != null) {
            text = label.getText();
        }
        return text;
    }

    public void setText(String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    /**
     * Convenience method for returning the connector slot as a Slot object.
     * Can only store primitive types in the property list.
     */
    public Slot getConnectorSlot() {
        Slot slot = null;

        if ((instance != null) && (connectorSlot != null)) {
            slot = instance.getKnowledgeBase().getSlot(connectorSlot);
        }

        return slot;
    }

    public void setConnectorSlot(String connectorSlot) {
        this.connectorSlot = connectorSlot;
    }

    public String getClsName() {
        String clsName = null;
        if (instance != null) {
            clsName = instance.getDirectType().getName();
        }
        return clsName;
    }

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
    
    // Provide access to the various parts of the diagram node.
    public JGoDrawable getDrawable() { return drawable; }

    public JGoText getLabel() { return label; }
    
    public JGoPort getPort() { return port; }
    
    public JGoPen getPen() { return getDrawable().getPen(); }
    
    public void setPen(JGoPen p) { getDrawable().setPen(p); }
    
    public JGoBrush getBrush() { return getDrawable().getBrush(); }
    
    public void setBrush(JGoBrush b) { getDrawable().setBrush(b); }
}