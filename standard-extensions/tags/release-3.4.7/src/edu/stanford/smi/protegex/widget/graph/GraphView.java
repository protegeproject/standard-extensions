package edu.stanford.smi.protegex.widget.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;

import com.nwoods.jgo.JGoCopyEnvironment;
import com.nwoods.jgo.JGoDocument;
import com.nwoods.jgo.JGoLink;
import com.nwoods.jgo.JGoListPosition;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoPen;
import com.nwoods.jgo.JGoPort;
import com.nwoods.jgo.JGoSelection;
import com.nwoods.jgo.JGoView;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.PropertyList;

public class GraphView extends JGoView {
    private static final long serialVersionUID = 6869305676538645015L;
    private JPopupMenu popupMenu = new JPopupMenu();
    private Point mouseUpDocPoint = new Point();

    private GraphWidget widget;
    private KnowledgeBase kb;
    private PropertyList pList;

    private Collection myHighlights;
    private Color myHighlightColor;

    AbstractAction insertPointAction = new AbstractAction("Insert Point") {
        private static final long serialVersionUID = 8382382001535570478L;

        public void actionPerformed(ActionEvent ae) { insertPointIntoLink(); }
    };

    AbstractAction removeSegmentAction = new AbstractAction("Remove Segment") {
        private static final long serialVersionUID = -3664006420155825391L;

        public void actionPerformed(ActionEvent ae) { removeSegmentFromLink(); }
    };

    AbstractAction makeAllNodesDefaultSizeAction = new AbstractAction("Make All Nodes Default Size") {
        private static final long serialVersionUID = -5148414796518922332L;

        public void actionPerformed(ActionEvent ae) { makeAllNodesDefaultSize(); }
    };

    public GraphView(GraphWidget widget) {
        super();

        this.widget = widget;
        this.pList = widget.getPropertyList();
        this.kb = widget.getKnowledgeBase();

        setGridHeight(20);
        setGridWidth(20);
        setSnapMove(JGoView.SnapAfter);
        setHidingDisabledScrollbars(true);

        Cls cls = widget.getCls();
        ViewProperties vProps = new ViewProperties(cls.getName(), pList);
        if (vProps != null) {
            setGridStyle(vProps.getGridStyleInt());
            setSnapMove(vProps.getSnapOnMoveInt());
        }

        myHighlights = null;
        myHighlightColor = null;
    }

    public void drop(DropTargetDropEvent e) {
        JGoCopyEnvironment map = getDocument().createDefaultCopyEnvironment();

        if (doDrop(e, map)) {
            Iterator i = map.values().iterator();
            while (i.hasNext()) {
                Object o = i.next();
                if (o instanceof Node) {
                    Node node = (Node) o;
                    if (node.isTopLevel()) {
                        GraphDocument myDoc = (GraphDocument) getDocument();
                        myDoc.initNode(node);
                    }
                }
            }
        }
    }

    public boolean doMouseDblClick(int modifiers, Point dc, Point vc) {
        JGoObject obj = pickDocObject(dc, false);
        if (obj instanceof NodePort) {
            JGoSelection selection = getSelection();
            selection.clearSelection();
            selection.selectObject(obj.getParent());
            widget.handleDoubleClick();
        } else if (obj instanceof ComplexLink) {
            widget.handleDoubleClick();
        }
        return super.doMouseDblClick(modifiers, dc, vc);
    }

    public boolean doMouseUp(int modifiers, Point dc, Point vc) {
        mouseUpDocPoint.setLocation(dc);

        // Right mouse click.
        if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
            JGoObject obj = pickDocObject(dc, true);
            if (obj != null) {
                if (obj instanceof JGoLink) {
                    JGoLink link = (JGoLink) obj;

                    // Re-initialize popup menu items.
                    popupMenu.removeAll();
                    popupMenu.add(insertPointAction);
                    if (link.getNumPoints() > 2) {
                        popupMenu.add(removeSegmentAction);
                    }
                    popupMenu.show(this, vc.x, vc.y);
                } else if (obj instanceof Node) {
                    popupMenu.removeAll();
                } else {
                    // Default handling.
                    return super.doMouseUp(modifiers, dc, vc);
                }
            }
        }

        // Default handling.
        return super.doMouseUp(modifiers, dc, vc);
    }

    public void doBackgroundClick(int modifiers, Point dc, Point vc) {
        if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
            popupMenu.removeAll();
            popupMenu.add(makeAllNodesDefaultSizeAction);
            popupMenu.show(this, vc.x, vc.y);
        }
    }

    public void onKeyEvent(KeyEvent evt) {
        int keyCode = evt.getKeyCode();

        if (keyCode == KeyEvent.VK_DELETE) {
            JGoSelection selectedObjects = getSelection();

            // Shortcut.
            if (selectedObjects.isEmpty()) return;

            ArrayList instances = new ArrayList();
            JGoListPosition pos = selectedObjects.getFirstObjectPos();
            while (pos != null) {
                JGoObject obj = selectedObjects.getObjectAtPos(pos);
                if (obj instanceof Node) {
                    Node node = (Node) obj;
                    Instance instance = node.getInstance();
                    instances.add(instance);

                    // If the user deletes a node that has complex links either
                    // coming into it or going out of it, we need to delete
                    // the instances associated with those links so that we aren't
                    // left with "dangling" reified relations in the knowledge
                    // base.
                    HashSet associatedLinks = widget.resolveComplexLinks(node);
                    instances.addAll(associatedLinks);
                } else if (obj instanceof SimpleLink) {
                    // User deleted a simple connector.  This means that the
                    // list of own slot values for the source node's connector
                    // slot need to be updated.
                    SimpleLink sLink = (SimpleLink) obj;
                    GraphDocument doc = (GraphDocument) getDocument();
                    doc.removeValueFromSourceNode(sLink);
                } else if (obj instanceof ComplexLink) {
                    ComplexLink cLink = (ComplexLink) obj;
                    Instance instance = cLink.getInstance();
                    instances.add(instance);
                }

                pos = selectedObjects.getNextObjectPosAtTop(pos);
            }

            deleteSelection();

            // Necessary to delete the instances in a group after looping
            // through selected objects above.  If you call kb.deleteInstace()
            // while looping through selected objects, it fires a setValues()
            // call which regenerates the graph widget and you lose your
            // group of selected objects.
            Iterator i = instances.iterator();
            while (i.hasNext()) {
                Instance instance = (Instance) i.next();
                kb.deleteInstance(instance);
            }

            widget.valueChanged();
        }
    }

    public void insertPointIntoLink() {
        JGoObject obj = pickDocObject(mouseUpDocPoint, true);
        if ((obj != null) && (obj instanceof JGoLink)) {
            JGoLink link = (JGoLink) obj;
            int index = link.getSegmentNearPoint(mouseUpDocPoint);
            link.insertPoint(index + 1, mouseUpDocPoint);
            getSelection().toggleSelection(link);
            selectObject(link);
        }
    }

    public void removeSegmentFromLink() {
        JGoObject obj = pickDocObject(mouseUpDocPoint, true);
        if ((obj != null) && (obj instanceof JGoLink)) {
            JGoLink link = (JGoLink) obj;
            int index = link.getSegmentNearPoint(mouseUpDocPoint);
            // Don't remove the first point.
            index = Math.max(index, 1);
            // Don't remove the last point.
            index = Math.min(index, link.getNumPoints()-2);
            link.removePoint(index);
            getSelection().toggleSelection(link);
            selectObject(link);
        }
    }

    public void makeAllNodesDefaultSize() {
        JGoDocument doc = getDocument();
        JGoListPosition pos = doc.getFirstObjectPos();
        while (pos != null) {
            JGoObject obj = doc.getObjectAtPos(pos);
            if (obj instanceof Node) {
                Node node = (Node) obj;
                node.setSize(GraphTypes.NODE_WIDTH, GraphTypes.NODE_HEIGHT);
            }
            pos = doc.getNextObjectPosAtTop(pos);
        }
    }

    public void newLink(JGoPort from, JGoPort to) {
        GraphDocument doc = (GraphDocument) getDocument();
        LinkUtilities lu = new LinkUtilities(kb, pList, from, to);
        boolean hasConnector = lu.hasValidConnectorSlot();
        boolean hasRelations = lu.hasValidRelations();

        Slot relationSlot = null;
        if (hasRelations) {
            // Get the slot that the user designated to hold
            // reified relations. (This is done on the widget
            // configuration panel).
            String slotName = pList.getString(GraphTypes.RELATION_SLOT);
            if (slotName != null) {
                relationSlot = kb.getSlot(slotName);
            }
        }

        // Case where there is just a connector slot.
        if (hasConnector && !hasRelations) {
            doc.setExternalUpdate(true);
            SimpleLink sLink = lu.makeSimpleLink();
            doc.setExternalUpdate(false);
            doc.addObjectAtTail(sLink);
        }
        // Case where there is just a reified relation.
        else if (!hasConnector && hasRelations) {
            ArrayList relations = lu.getValidRelations();
            Cls relation = null;

            if (relations.size() == 1) {
                // There is only one valid reified relation.
                relation = (Cls) relations.get(0);
            } else if (relations.size() > 1) {
                // There is more than one valid reified relation.  We need
                // to ask the user which one they want to use.
                ChooseRelationDialog dialog =
                        new ChooseRelationDialog(this.getFrame(),
                        "Reified Relation Chooser", true, relations);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                relation = dialog.getRelation();
            }

            if (relation != null) {
                Instance instance = kb.createInstance(null, relation);
                instance.addFrameListener(doc.getFrameListener());

                lu.setComplexLinkValues(instance);

                Instance topLevelInstance = widget.getInstance();
                topLevelInstance.addOwnSlotValue(relationSlot, instance);
            }
        }
        // Case where there is both connector slot and reified relation.
        else if (hasConnector && hasRelations) {
            Slot connectorSlot = lu.getConnectorSlot();
            ArrayList relations = lu.getValidRelations();

            ChooseLinkDialog dlg = new ChooseLinkDialog(this.getFrame(),
                    "Choose Connector Type", true, connectorSlot, relations);
            dlg.setSize(new Dimension(350, 250));
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);

            Object obj = dlg.getLinkType();
            if (obj != null) {
                if (obj instanceof Slot) {

                    // User chose connector slot.
                    doc.setExternalUpdate(true);
                    SimpleLink sLink = lu.makeSimpleLink();
                    doc.setExternalUpdate(false);

                    getDocument().addObjectAtTail(sLink);
                } else if (obj instanceof Cls) {
                    // User chose reified relation.
                    Cls cls = (Cls) obj;
                    Instance instance = kb.createInstance(null, cls);
                    instance.addFrameListener(doc.getFrameListener());

                    lu.setComplexLinkValues(instance);

                    Instance topLevelInstance = widget.getInstance();
                    topLevelInstance.addOwnSlotValue(relationSlot, instance);
                }
            }
        }
    }

    public void reLink(JGoLink oldlink, JGoPort from, JGoPort to) {
        // Call super first so we can cast port objects to NodePorts.
        super.reLink(oldlink, from, to);

        GraphDocument myDoc = (GraphDocument) getDocument();
        NodePort fromPort = (NodePort) from;
        NodePort toPort = (NodePort) to;

        if (oldlink instanceof SimpleLink) {
            Slot connectorSlot = fromPort.getNode().getConnectorSlot();
            Instance inst1 = fromPort.getNode().getInstance();
            Instance inst2 = toPort.getNode().getInstance();

            myDoc.setExternalUpdate(true);
            inst1.addOwnSlotValue(connectorSlot, inst2);
            myDoc.setExternalUpdate(false);

        } else if (oldlink instanceof ComplexLink) {
            ComplexLink cLink = (ComplexLink) oldlink;
            Instance instance = cLink.getInstance();
            Slot toSlot = kb.getReifedRelationToSlot();
            Slot fromSlot = kb.getReifedRelationFromSlot();
            instance.setOwnSlotValue(fromSlot, fromPort.getNode().getInstance());
            instance.setOwnSlotValue(toSlot, toPort.getNode().getInstance());
        }
    }

    public boolean startReLink(JGoLink oldlink, JGoPort oldport, Point dc) {
        GraphDocument myDoc = (GraphDocument) getDocument();
        NodePort from = (NodePort) oldlink.getFromPort();
        NodePort to = (NodePort) oldlink.getToPort();
        NodePort old = (NodePort) oldport;

        if (from == null) {
            // User is relinking the from/source port.
            if (oldlink instanceof SimpleLink) {
                Slot connectorSlot = old.getNode().getConnectorSlot();
                Instance inst1 = old.getNode().getInstance();
                Instance inst2 = to.getNode().getInstance();

                myDoc.setExternalUpdate(true);
                inst1.removeOwnSlotValue(connectorSlot, inst2);
                myDoc.setExternalUpdate(false);
            }
        } else if (to == null) {
            // User is relinking the to/destination port.
            if (oldlink instanceof SimpleLink) {
                Slot connectorSlot = from.getNode().getConnectorSlot();
                Instance inst1 = from.getNode().getInstance();
                Instance inst2 = old.getNode().getInstance();

                myDoc.setExternalUpdate(true);
                inst1.removeOwnSlotValue(connectorSlot, inst2);
                myDoc.setExternalUpdate(false);
            }
        }
        return super.startReLink(oldlink, oldport, dc);
    }

    public void highlightInstances(Collection instances, Color color) {
        if (instances == null) return;

        if (color == null) {
            color = Color.yellow;
        }

        myHighlights = instances;
        myHighlightColor = color;

        Iterator i = instances.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();

            JGoListPosition pos = getDocument().getFirstObjectPos();
            while (pos != null) {
                JGoObject obj = getDocument().getObjectAtPos(pos);
                if (obj instanceof Node) {
                    Node node = (Node) obj;
                    Instance instance2 = node.getInstance();
                    if (instance.equals(instance2)) {
                        node.setPen(JGoPen.make(JGoPen.SOLID, 3, color));
                    }
                } else if (obj instanceof ComplexLink) {
                    ComplexLink cLink = (ComplexLink) obj;
                    Instance instance2 = cLink.getInstance();
                    if (instance.equals(instance2)) {
                        cLink.setHighlight(JGoPen.make(JGoPen.SOLID, 5, color));
                    }
                }
                pos = getDocument().getNextObjectPosAtTop(pos);
            }
        }
    }

    public void updateView() {
        super.updateView();
        if ((myHighlights != null) || (myHighlightColor != null)) {
            highlightInstances(myHighlights, myHighlightColor);
        }
    }

    public void saveGraphAsImage() {
        JFileChooser fc = ComponentFactory.createFileChooser("Save As", "jpg");

        int rval = fc.showSaveDialog(this);
        if (rval == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
			BufferedImage image = getImage();
            try {
                ImageIO.write(image, "jpg", file);
            } catch (java.io.IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public BufferedImage getImage() {
        BufferedImage image;
        Dimension size = widget.getDocument().getDocumentSize();
        image = new BufferedImage(size.width, size.height,
                                  BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle clipRect = new Rectangle(0, 0, size.width, size.height);
        paintView(g2, clipRect);
        return image;
    }
}
