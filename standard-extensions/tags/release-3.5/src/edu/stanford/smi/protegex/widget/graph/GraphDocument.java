package edu.stanford.smi.protegex.widget.graph;

import java.awt.*;
import java.util.*;

import com.nwoods.jgo.*;
import com.nwoods.jgo.layout.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.*;

public class GraphDocument extends JGoDocument {
	
	private static final long serialVersionUID = -2178989375861620481L;

    public static final String POSITION_PREFIX = "InstanceGraphWidget.";
	
    /**
     * Added transient keywords here to prevent serialization of certain
     * items. This is necessary as a workaround for a bug in the way that
     * drag and drop works on the Mac.
     */
    private transient GraphWidget widget = null;
    private transient KnowledgeBase kb = null;
    private transient PropertyList pList = null;
    private transient Slot slot = null;
    private transient HashMap instanceMap = new HashMap();
    private HashMap positionMap;
    private String key = null;
    private boolean externalUpdate = false;
    private Point defaultLocation = new Point(5, 5);

    private transient FrameListener frameListener = new FrameAdapter() {
        public void ownSlotValueChanged(FrameEvent event) {
            super.ownSlotValueChanged(event);
            handleOwnSlotValueChanged(event);
        }
        public void browserTextChanged(FrameEvent event) {
            super.browserTextChanged(event);
            handleBrowserTextChanged(event.getFrame());
        }
    };

    public GraphDocument(GraphWidget widget) {
        super();

        this.widget = widget;
        this.kb = widget.getKnowledgeBase();
        this.pList = widget.getPropertyList();
        this.slot = widget.getSlot();

        key = POSITION_PREFIX + widget.getInstance().getName() + "." + slot.getName();
        positionMap = (HashMap) kb.getProject().getClientInformation(key);

        if (positionMap == null) {
        	
        	// Check for short names from older OWL projects that are 
        	// being read into Protege 3.4.1 for the first time.
        	if (PluginUtilities.isOWL(kb)) {
        		positionMap = PositionInfoFixup.checkForShortNames(kb, key);
        	} else {        	
        		positionMap = new HashMap();
        	}
        }
    }

    public KnowledgeBase getKB() {
        return kb;
    }

    public PropertyList getPropertyList() {
        return pList;
    }

    public Cls getCls() {
        return widget.getCls();
    }

    public FrameListener getFrameListener() {
        return frameListener;
    }

    // Default location to place nodes if they aren't dragged onto the
    // view but added programmatically.
    private Point getDefaultLocation() {
        if (defaultLocation != null) {
            defaultLocation.x += 5;
            defaultLocation.y += 5;
        }
        return defaultLocation;
    }

    @SuppressWarnings("unchecked")
	public void initNodes(Collection instances) {
        setSuspendUpdates(true);

        if (instances != null) {
            if (!isEmpty()) {
                deleteContents();
            }

            Iterator i = instances.iterator();
            while (i.hasNext()) {
                Instance instance = (Instance) i.next();
                instance.addFrameListener(frameListener);

                Cls cls = instance.getDirectType();
                NodeProperties props = new NodeProperties(cls.getName(), cls.getBrowserText(), pList);
                String shape = props.getShape();

                Node node = new Node();
                node.initialize(new Point(), shape, instance.getBrowserText());

                // Set the node's color.
                node.setBrush(JGoBrush.makeStockBrush(props.getShapeColor()));

                // Set the node's size.
                node.setSize(GraphTypes.NODE_WIDTH, GraphTypes.NODE_HEIGHT);

                // Set the node's text properties.
                node.getLabel().setTextColor(props.getTextColor());
                node.getLabel().setBold(props.isBold());
                node.getLabel().setItalic(props.isItalic());

                // Set the node's connector slot.
                String connectorSlot = props.getConnectorSlot();
                if (connectorSlot != null) {
                    node.setConnectorSlot(connectorSlot);
                }

                // Set the node's instance.
                node.setInstance(instance);
                
                node.setUniqueName(cls.getName());

                instanceMap.put(instance, node);
                addObjectAtTail(node);
            }

            if (instances.size() >= 1) {
                initSimpleLinks(instances);
                initComplexLinks();
                layoutNodesAndLinks();
            }
        }

        setSuspendUpdates(false);
    }

    public void initNode(Node node) {
        node.setResizable(true);
        node.setSize(GraphTypes.NODE_WIDTH, GraphTypes.NODE_HEIGHT);

        String uniqueName = node.getUniqueName();
        Cls cls = kb.getCls(uniqueName);
        Instance instance = kb.createInstance(null, cls);
        instance.addFrameListener(frameListener);
        node.setInstance(instance);
        node.setText(instance.getBrowserText());

        widget.valueChanged();
    }

    public void handleBrowserTextChanged(Frame frame) {
        Instance instance = (Instance) frame;
        Node node = (Node) instanceMap.get(instance);
        if (node != null) {
            node.getLabel().setText(instance.getBrowserText());
        } else {
            // User changed browser text for reified relation.
            JGoListPosition pos = getFirstObjectPos();
            while (pos != null) {
                JGoObject obj = getObjectAtPos(pos);
                if (obj instanceof ComplexLink) {
                    ComplexLink cLink = (ComplexLink) obj;
                    Instance instance2 = cLink.getInstance();
                    if (instance.equals(instance2)) {
                        cLink.setText(instance.getBrowserText());
                        break;
                    }
                }
                pos = getNextObjectPosAtTop(pos);
            }
        }
    }

    public void handleOwnSlotValueChanged(FrameEvent event) {
        if (!externalUpdate) {
            Collection values = widget.getValues();
            if (!values.isEmpty()) {
                // Need to save the position info in the event that the :NAME
                // slot is set as the form browser key.  Position information
                // is stored using the underlying instance names as keys in
                // some cases and if this is changed on the fly, we can lose
                // position information when the form browser key is modified.
                Slot slot = event.getSlot();
                if (slot.getName() == Model.Slot.NAME) {
                    savePositionInfo();
                    initNodes(values);
                }
            }
        }
    }

    private void initSimpleLinks(Collection instances) {
        Iterator i = instances.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            Node node = (Node) instanceMap.get(instance);
            Slot connectorSlot = node.getConnectorSlot();
            if (connectorSlot != null) {
                Collection values = instance.getDirectOwnSlotValues(connectorSlot);
                Iterator j = values.iterator();
                while (j.hasNext()) {
                    Instance destInstance = (Instance) j.next();
                    Node destNode = (Node) instanceMap.get(destInstance);
                    if (destNode != null) {
                        LinkUtilities lu = new LinkUtilities(kb, pList, node.getPort(), destNode.getPort());
                        if (lu.hasValidConnectorSlot()) {
                            SimpleLink sLink = lu.restoreSimpleLink();
                            addObjectAtTail(sLink);
                        }
                    }
                }
            }
        }
    }

    private void initComplexLinks() {
        // Get the name of the slot that the user designated to hold reified
        // relations. This property is set via the widget configuration panel.
        String relationSlotName = pList.getString(GraphTypes.RELATION_SLOT);

        // Shortcut.
        if (relationSlotName == null)
            return;

        // Shortcut.
        Slot relationSlot = kb.getSlot(relationSlotName);
        if (relationSlot == null)
            return;

        Slot fromSlot = kb.getReifedRelationFromSlot();
        Slot toSlot = kb.getReifedRelationToSlot();

        Instance currentInstance = widget.getInstance();
        Collection c = currentInstance.getDirectOwnSlotValues(relationSlot);
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            instance.addFrameListener(frameListener);
            Instance fromInstance = (Instance) instance.getOwnSlotValue(fromSlot);
            Instance toInstance = (Instance) instance.getOwnSlotValue(toSlot);

            if ((fromInstance != null) && (toInstance != null)) {
                Node fromNode = (Node) instanceMap.get(fromInstance);
                Node toNode = (Node) instanceMap.get(toInstance);

                if ((fromNode != null) && (toNode != null)) {
                    LinkUtilities lu = new LinkUtilities(kb, pList, fromNode.getPort(), toNode.getPort());
                    ComplexLink cLink = lu.restoreComplexLink(instance);
                    addObjectAtTail(cLink);
                }
            }
        }
    }

    private void layoutNodesAndLinks() {
        if (positionMap.isEmpty()) {
            // Default to downward directed layout.
            performAutomaticLayout(JGoLayeredDigraphAutoLayout.LD_DIRECTION_DOWN);
        } else {
            JGoListPosition pos = getFirstObjectPos();
            while (pos != null) {
                JGoObject obj = getObjectAtPos(pos);
                if (obj instanceof Node) {
                    Node node = (Node) obj;
                    Instance instance = node.getInstance();
                    Rectangle rect = (Rectangle) positionMap.get(instance.getName());
                    if (rect == null) {
                        rect = new Rectangle(GraphTypes.NODE_WIDTH, GraphTypes.NODE_HEIGHT);
                        node.setBoundingRect(rect);
                        node.setTopLeft(getDefaultLocation());
                    } else {
                        node.setBoundingRect(rect);
                    }
                } else if (obj instanceof SimpleLink) {
                    SimpleLink sLink = (SimpleLink) obj;
                    Node fromNode = (Node) sLink.getFromPort().getParent();
                    Node toNode = (Node) sLink.getToPort().getParent();
                    String key = fromNode.getInstance().getName() + "+" + toNode.getInstance().getName();
                    HashMap pointMap = (HashMap) positionMap.get(key);
                    insertPointsIntoLink(pointMap, sLink);
                } else if (obj instanceof ComplexLink) {
                    ComplexLink cLink = (ComplexLink) obj;
                    String key = cLink.getInstance().getName();
                    HashMap pointMap = (HashMap) positionMap.get(key);
                    insertPointsIntoLink(pointMap, cLink);
                }
                pos = getNextObjectPosAtTop(pos);
            }
        }
    }

    public void performAutomaticLayout(int direction) {
        JGoLayeredDigraphAutoLayout layout = new JGoLayeredDigraphAutoLayout(this);
        layout.setDirectionOption(direction);
        layout.performLayout();
        savePositionInfo();
    }

    @SuppressWarnings("unchecked")
	public void savePositionInfo() {
        // This check is necessary as a workaround for a bug where nodes were
        // losing positioning information if a user was editing a label in the
        // view and then clicked on the node's drawable.  Calling
        // getFirstObjectPos() in this obscure case will return null even when
        // there are objects in the document.
        if (isSuspendUpdates())
            return;

        positionMap.clear();

        JGoListPosition pos = getFirstObjectPos();
        while (pos != null) {
            JGoObject obj = getObjectAtPos(pos);
            if (obj instanceof Node) {
                Node node = (Node) obj;
                Instance instance = node.getInstance();
                if (instance != null) {
                    positionMap.put(instance.getName(), obj.getBoundingRect());
                }
            } else if (obj instanceof JGoLink) {
                JGoLink link = (JGoLink) obj;
                int numPoints = link.getNumPoints();
                if (numPoints > 2) {
                    HashMap pointMap = new HashMap();
                    for (int i = 1; i < (numPoints - 1); i++) {
                        Point point = link.getPoint(i);

                        // Need to do this extra work because the
                        // setClientInformation call on the project object
                        // has restrictions on the sort of data it will
                        // accept.
                        Integer xInt = new Integer(point.x);
                        Integer yInt = new Integer(point.y);
                        String s = xInt.toString() + "," + yInt.toString();
                        GraphPoint gp = new GraphPoint(s);
                        Integer count = new Integer(i);
                        pointMap.put(count.toString(), gp);
                    }

                    String key = null;
                    if (link instanceof SimpleLink) {
                        Node fromNode = (Node) link.getFromPort().getParent();
                        Node toNode = (Node) link.getToPort().getParent();
                        key = fromNode.getInstance().getName() + "+" + toNode.getInstance().getName();
                    } else if (link instanceof ComplexLink) {
                        ComplexLink cLink = (ComplexLink) link;
                        Instance instance = cLink.getInstance();
                        key = instance.getName();
                    }

                    positionMap.put(key, pointMap);
                }
            }

            // Increment.
            pos = getNextObjectPosAtTop(pos);
        }

        if (!positionMap.isEmpty()) {
        	kb.getProject().setClientInformation(key, positionMap);
        }
    }

    private void insertPointsIntoLink(HashMap pointMap, JGoLink link) {
        if (pointMap != null) {
            for (int i = 0; i < pointMap.size(); i++) {
                // Add one to 'i' here because we are adding a series of points
                // to the middle of a JGoLink.  In other words, we are doing
                // insertions - don't want to overwrite the first or last point
                // in the link.
                Integer count = new Integer(i + 1);
                GraphPoint gp = (GraphPoint) pointMap.get(count.toString());
                link.insertPoint(count.intValue(), gp.getXInt(), gp.getYInt());
            }
        }
    }

    public static HashMap getPositionInfo(Instance instance, Slot slot) {
        HashMap positionMap = new HashMap();
        if ((instance != null) && (slot != null)) {
            String key = POSITION_PREFIX + instance.getName() + "." + slot.getName();
            KnowledgeBase kb = instance.getKnowledgeBase();
            positionMap = (HashMap) kb.getProject().getClientInformation(key);
        }
        return positionMap;
    }

    public Rectangle getNodeRectangle(Instance instance) {
        Rectangle rect = new Rectangle();
        if (instance != null) {
            Node node = (Node) instanceMap.get(instance);
            if (node != null) {
                rect = node.getBoundingRect();
            }
        }
        return rect;
    }

    public void setExternalUpdate(boolean update) {
        this.externalUpdate = update;
    }

    public void removeValueFromSourceNode(SimpleLink sLink) {
        Node source = (Node) sLink.getFromPort().getParent();
        Instance sourceI = source.getInstance();
        if (!sourceI.isDeleted()) {
            Slot connectorSlot = source.getConnectorSlot();
            Node dest = (Node) sLink.getToPort().getParent();
            Instance destI = dest.getInstance();
            if (!destI.isDeleted()) {
                sourceI.removeOwnSlotValue(connectorSlot, destI);
            }
        }
    }

    public void removeListener() {
        Collection values = widget.getValues();
        Iterator i = values.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            instance.removeFrameListener(frameListener);
        }

        // The call to getValues above only returns a collection of instances
        // that represent nodes in the graph widget.  Need to also remove the
        // frame listener from reified relations in the graph.
        JGoListPosition pos = getFirstObjectPos();
        while (pos != null) {
            JGoObject obj = getObjectAtPos(pos);
            if (obj instanceof ComplexLink) {
                ComplexLink cLink = (ComplexLink) obj;
                Instance instance = cLink.getInstance();
                if (instance != null) {
                    instance.removeFrameListener(frameListener);
                }
            }
            pos = getNextObjectPosAtTop(pos);
        }
    }

    public void deleteContents() {
        instanceMap.clear();
        removeListener();
        super.deleteContents();
    }
}
