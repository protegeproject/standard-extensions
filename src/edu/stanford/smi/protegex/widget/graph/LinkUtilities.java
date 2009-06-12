package edu.stanford.smi.protegex.widget.graph;

import java.awt.*;
import java.util.*;

import com.nwoods.jgo.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;

/**
 * This class contains the logic for whether or not the user can draw a line
 * between two nodes.  If it's possible to connect two nodes, it also
 * determines what all the valid connectors are (since there could be more
 * than one).  Also deals with the appearance of the links as specified by the
 * user in the diagram widget configuration dialog.
 *
 * @author Jennifer Vendetti
 */
public class LinkUtilities {
    NodePort from = null;
    NodePort to = null;
    Node sourceNode = null;
    Node destNode = null;
    Instance sourceInstance = null;
    Instance destInstance = null;
    Slot fromSlot = null;
    Slot toSlot = null;
    Slot connectorSlot = null;

    HashMap lineTypes = new HashMap();

    KnowledgeBase kb = null;
    Cls cls = null;
    PropertyList pList = null;
    ArrayList relations = new ArrayList();

    public LinkUtilities(KnowledgeBase kb, PropertyList pList, JGoPort from, JGoPort to) {
        this.kb = kb;
        this.pList = pList;
        this.from = (NodePort) from;
        this.to = (NodePort) to;
        initialize();
    }

    private void initialize() {
    	fromSlot = kb.getReifedRelationFromSlot();
    	toSlot = kb.getReifedRelationToSlot();

        sourceNode = from.getNode();
        sourceInstance = sourceNode.getInstance();
        destNode = to.getNode();
        destInstance = destNode.getInstance();

        // Figure out the class whose slot we are graphing.  This is a
        // bug fix that was added quickly for a demo.  There should be a
        // better way to do this.
        GraphDocument doc = (GraphDocument) from.getDocument();
        if (doc != null) {
            cls = doc.getCls();
        }

        initConnectorSlot();
        initRelations();
    }

    private void initConnectorSlot() {
        connectorSlot = sourceNode.getConnectorSlot();
    }

    private void initRelations() {
        // Get the name of the slot that the user designated to hold reified
        // relations. This property is set via the widget configuration panel.
        String slotName = pList.getString(GraphTypes.RELATION_SLOT);

        // Shortcut.
        if (slotName == null) return;

        // Shortcut.
        Slot relationSlot = kb.getSlot(slotName);
        if (relationSlot == null) return;

        Collection allowedClses = getTemplateSlotAllowedClses(cls, relationSlot);

        // For each allowed class, what are the values in the from and to
        // slots?
        Iterator i = allowedClses.iterator();
        while (i.hasNext()) {
            Cls allowedCls = (Cls) i.next();
            if (allowedCls.isConcrete()) {
                Collection fromClses = getTemplateSlotAllowedClses(allowedCls, fromSlot);

                // Do any of the values in the from slot match the source node?
                Iterator j = fromClses.iterator();
                while (j.hasNext()) {
                    Cls fromCls = (Cls) j.next();
                    if (fromCls.equals(sourceInstance.getDirectType())) {

                        // If yes, do any of the values in the to slot match the destination node?
                        Collection toClses = getTemplateSlotAllowedClses(allowedCls, toSlot);
                        Iterator k = toClses.iterator();
                        while (k.hasNext()) {
                            Cls toCls = (Cls) k.next();
                            if (toCls.equals(destInstance.getDirectType())) {
                                relations.add(allowedCls);
                            }
                        }
                    }
                }
            }
        }
    }

    private ArrayList getTemplateSlotAllowedClses(Cls cls, Slot slot) {
        ArrayList allowedClses = new ArrayList();

        Collection c = cls.getTemplateSlotAllowedClses(slot);
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Cls allowedCls = (Cls) i.next();
            allowedClses.add(allowedCls);

            Collection sc = allowedCls.getSubclasses();
            Iterator j = sc.iterator();
            while (j.hasNext()) {
                Cls allowedSubCls = (Cls) j.next();
                allowedClses.add(allowedSubCls);
            }
        }

        return allowedClses;
    }

    public Slot getConnectorSlot() {
        return connectorSlot;
    }

    public ArrayList getValidRelations() {
        return relations;
    }

    public boolean hasValidConnectorSlot() {
        boolean retval = false;

        if (connectorSlot != null) {
            Cls type = sourceInstance.getDirectType();
            Collection allowedClses = getTemplateSlotAllowedClses(type, connectorSlot);
            Iterator i = allowedClses.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                if (destInstance.hasType(cls)) {
                    retval = true;
                }
            }
        }

        return retval;
    }

    public boolean hasValidRelations() {
        boolean retval = false;
        if (relations.size() > 0) {
            retval = true;
        }
        return retval;
    }

    private SimpleLink createSimpleLink() {
        SimpleLink sLink = new SimpleLink(from, to);

        // Initialize.
        sLink.initialize(connectorSlot.getBrowserText());

        // Get properties.
        Cls directType = sourceInstance.getDirectType();
        String clsName = directType.getName();
        String browserText = directType.getBrowserText();
        NodeProperties props = new NodeProperties(clsName, browserText, pList);

        setLineType(props, sLink, Color.black);
        setArrowheadType(props, sLink);

        // Set whether or not browser text is displayed.
        boolean displayText = props.isTextDisplayed();
        sLink.getMidLabel().setVisible(displayText);

        return sLink;
    }

    public SimpleLink makeSimpleLink() {
        SimpleLink sLink = createSimpleLink();
        // Add own slot value.
        sourceInstance.addOwnSlotValue(connectorSlot, destInstance);
        return sLink;
    }

    public SimpleLink restoreSimpleLink() {
        return createSimpleLink();
    }

    public ComplexLink createComplexLink(Instance newInstance) {
        ComplexLink cLink = new ComplexLink(from, to);

        // Initialize.
        cLink.setText(newInstance.getBrowserText());
        cLink.setInstance(newInstance);

        // Get properties.
        String clsName = newInstance.getDirectType().getName();
        RelationProperties props = new RelationProperties(clsName, pList);

        setLineType(props, cLink, props.getLineColor());
        setArrowheadType(props, cLink);

        // Set whether or not browser text is displayed.
        boolean displayText = props.isTextDisplayed();
        cLink.getMidLabel().setVisible(displayText);

        return cLink;
    }

    public void setComplexLinkValues(Instance instance) {
        instance.setOwnSlotValue(fromSlot, sourceInstance);
        instance.setOwnSlotValue(toSlot, destInstance);
    }

    public ComplexLink restoreComplexLink(Instance instance) {
        return createComplexLink(instance);
    }

    private void setLineType(GraphObjectProperties props, JGoLink link, Color lineColor) {
        String lineType = props.getLineType();
        if (lineType.equals(GraphTypes.SOLID)) {
            link.setPen(JGoPen.make(JGoPen.SOLID, GraphTypes.LINE_WIDTH, lineColor));
        } else if (lineType.equals(GraphTypes.DASHED)) {
            link.setPen(JGoPen.make(JGoPen.DASHED, GraphTypes.LINE_WIDTH, lineColor));
        } else if (lineType.equals(GraphTypes.DOTTED)) {
            link.setPen(JGoPen.make(JGoPen.DOTTED, GraphTypes.LINE_WIDTH, lineColor));
        } else if (lineType.equals(GraphTypes.DASH_DOT)) {
            link.setPen(JGoPen.make(JGoPen.DASHDOT, GraphTypes.LINE_WIDTH, lineColor));
        } else if (lineType.equals(GraphTypes.DASH_DOT_DOT)) {
            link.setPen(JGoPen.make(JGoPen.DASHDOTDOT, GraphTypes.LINE_WIDTH, lineColor));
        }
    }

    private void setArrowheadType(GraphObjectProperties props, JGoLink link) {
        String arrowheadType = props.getArrowheadType();
        if (arrowheadType.equals(GraphTypes.ARROW_ARROWHEAD)) {
            link.setArrowHeads(false, true);
        } else if (arrowheadType.equals(GraphTypes.NONE)) {
            link.setArrowHeads(false, false);
        } else if (arrowheadType.equals(GraphTypes.ARROW_DIAMOND)) {
            link.setArrowHeads(false, true);
            link.setArrowShaftLength(link.getArrowLength() * 2);
        } else if (arrowheadType.equals(GraphTypes.ARROW_SIMPLE_LINE_DRAWN)) {
            link.setArrowHeads(false, true);
            link.setArrowShaftLength(0);
        } else if (arrowheadType.equals(GraphTypes.ARROW_TRIANGLE)) {
            link.setArrowHeads(false, true);
            link.setArrowShaftLength(link.getArrowLength());
        }
    }
}