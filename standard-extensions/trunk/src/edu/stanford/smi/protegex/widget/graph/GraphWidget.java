package edu.stanford.smi.protegex.widget.graph;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import javax.swing.*;

import com.nwoods.jgo.*;
import com.nwoods.jgo.layout.JGoLayeredDigraphAutoLayout;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.DisplayUtilities;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.*;

public class GraphWidget extends AbstractSlotWidget {
    private JPanel mainPanel = new JPanel(new BorderLayout(0, 2));
    private JSplitPane splitPane;

    private GraphPalette palette;
    private GraphView view;
    private GraphDocument doc;

    private KnowledgeBase kb;
    private Cls cls;
    private Slot slot;
    private PropertyList pList;
    private LabeledComponent labeledComponent;
    private Collection currentValues;

    private boolean paletteStuffed = false;
    private boolean docInitialized = false;

    private AllowableAction createInstanceAction;
    private AllowableAction viewSelectedInstancesAction;
    private AllowableAction addExistingInstancesAction;
    private AllowableAction removeSelectedInstancesAction;
    private AllowableAction deleteSelectedInstancesAction;
    private AllowableAction performAutomaticLayoutAction;
    private AllowableAction performAutomaticLayoutRightAction;
    private AllowableAction saveAsImageAction;

    private FrameListener frameListener = new FrameAdapter() {
        public void ownSlotValueChanged(FrameEvent event) {
            super.ownSlotValueChanged(event);
            handleOwnSlotValueChanged(event);
        }
    };

    public GraphWidget() {
        setPreferredColumns(4);
        setPreferredRows(4);
    }

    public void initialize() {
        kb = getKnowledgeBase();
        cls = getCls();
        slot = getSlot();
        pList = getPropertyList();

        palette = new GraphPalette();

        view = createGraphView(this);
        view.addViewListener(new JGoViewListener() {
            public void viewChanged(JGoViewEvent e) {
                processViewChange(e);
            }
        });

        if (isDesignTime()) {
            view.getDocument().setModifiable(false);
        }

        // Populate the split pane.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, palette, view);

        // Populate the main panel.
        mainPanel.add(splitPane, BorderLayout.CENTER);
        labeledComponent = new LabeledComponent(getLabel(), mainPanel, true);
        addButtons(labeledComponent);
        add(labeledComponent);
    }

    protected GraphView createGraphView(GraphWidget widget) {
        return new GraphView(this);
    }

    public void addNotify() {
        super.addNotify();

        if (!paletteStuffed) {
            stuffPalette();
            paletteStuffed = true;
        }

        if (!isDesignTime()) {
            if (!docInitialized) {
                doc = new GraphDocument(this);
                doc.initNodes(currentValues);
                view.setDocument(doc);
                docInitialized = true;
            }
        }
    }

    public void setValues(Collection c) {
        if (!isDesignTime()) {
            currentValues = c;
            if (view.isShowing()) {
                doc = new GraphDocument(this);
                doc.initNodes(c);
                view.setDocument(doc);
                docInitialized = true;
            }
            else {
                // Need to initialize document in addNotify instead.
                docInitialized = false;
            }
        }
    }

    public void setInstance(Instance instance) {
        super.setInstance(instance);
        instance.addFrameListener(frameListener);
    }

    public Collection getValues() {
        ArrayList values = new ArrayList();

        JGoListPosition pos = doc.getFirstObjectPos();
        while (pos != null) {
            JGoObject obj = doc.getObjectAtPos(pos);
            if (obj instanceof Node) {
                Node node = (Node) obj;
                Instance instance = node.getInstance();
                values.add(instance);
            }
            pos = doc.getNextObjectPosAtTop(pos);
        }

        return values;
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        boolean isSuitable;
        if (cls == null || slot == null) {
            isSuitable = false;
        } else {
            boolean isInstance = cls.getTemplateSlotValueType(slot) == ValueType.INSTANCE;
            boolean isMultiple = cls.getTemplateSlotAllowsMultipleValues(slot);
            isSuitable = isInstance && isMultiple;
        }
        return isSuitable;
    }

    public void handleViewSelectedInstances() {
        Collection selectedObjs = getSelection();
        Iterator i = selectedObjs.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            showInstance(instance);
        }
    }

    public void handleCreateInstance() {
        if (slot != null) {
            KnowledgeBase kb = getKnowledgeBase();

            int xPos = view.getGridOrigin().x + 10;
            int yPos = view.getGridOrigin().y + 10;

            Collection c = DisplayUtilities.pickClses(this, getKnowledgeBase(), 
            							cls.getTemplateSlotAllowedClses(slot));
            Iterator i = c.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                if (cls.isConcrete()) {

                    // Add new node to diagram.
                    String name = cls.getName();
                    Node node = makeNewNode(name, new Point(xPos, yPos));
                    Instance instance = kb.createInstance(null, cls);
                    node.setInstance(instance);
                    node.setText(instance.getBrowserText());
                    node.setSize(GraphTypes.NODE_WIDTH, GraphTypes.NODE_HEIGHT);
                    doc.addObjectAtTail(node);

                    valueChanged();

                    xPos = xPos + 10;
                    yPos = yPos + 10;
                }
            }
        }
    }

    public void handleAddExistingInstance() {
        if (slot != null) {
            int xPos = view.getGridOrigin().x + 10;
            int yPos = view.getGridOrigin().y + 10;

            Collection instances = DisplayUtilities.pickInstances(this,
                getKnowledgeBase(), cls.getTemplateSlotAllowedClses(slot));
            Iterator i = instances.iterator();
            while (i.hasNext()) {
                Instance instance = (Instance) i.next();

                // Add new node to diagram.
                String name = instance.getDirectType().getName();
                Node node = makeNewNode(name, new Point(xPos, yPos));
                node.setInstance(instance);
                node.setText(instance.getBrowserText());
                node.setSize(GraphTypes.NODE_WIDTH, GraphTypes.NODE_HEIGHT);

                doc.addObjectAtTail(node);
                xPos = xPos + 10;
                yPos = yPos + 10;
            }
            valueChanged();
        }
    }

    public void handleRemoveSelectedInstances() {
        view.deleteSelection();
        valueChanged();
    }

    protected HashSet resolveComplexLinks(Node node) {
        HashSet instances = new HashSet();

        JGoPort port = node.getPort();
        JGoListPosition pos = port.getFirstLinkPos();
        while (pos != null) {
            JGoLink link = port.getLinkAtPos(pos);
            if (link instanceof ComplexLink) {
            	ComplexLink cLink = (ComplexLink) link;
				Instance instance = cLink.getInstance();
                instances.add(instance);
            }
            pos = port.getNextLinkPos(pos);
        }

        return instances;
    }

    public void handleDeleteSelectedInstances() {
        JGoSelection selectedObjects = view.getSelection();

        // Shortcut.
        if (selectedObjects.isEmpty()) return;
        
        // Shortcut.
        String text = LocalizedText.getText(ResourceKey.DIALOG_CONFIRM_DELETE_TEXT);
        int result = ModalDialog.showMessageDialog(view, text, ModalDialog.MODE_YES_NO);
        if (result == ModalDialog.OPTION_NO) return;

        HashSet instances = new HashSet();
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
                HashSet associatedLinks = resolveComplexLinks(node);
                instances.addAll(associatedLinks);
            } else if (obj instanceof SimpleLink) {
                // User deleted a simple connector.  This means that the list
                // of own slot values for the source node's connector slot need
                // to be updated.
                SimpleLink sLink = (SimpleLink) obj;
                doc.removeValueFromSourceNode(sLink);
            } else if (obj instanceof ComplexLink) {
                ComplexLink cLink = (ComplexLink) obj;
                Instance instance = cLink.getInstance();
                instances.add(instance);
            }
            pos = selectedObjects.getNextObjectPosAtTop(pos);
        }

        view.deleteSelection();

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

        valueChanged();
    }

    public void handlePerformAutomaticLayout(int direction) {
        doc.performAutomaticLayout(direction);
    }

    public void handleSaveAsImage() {
        view.saveGraphAsImage();
    }

    public void handleOwnSlotValueChanged(FrameEvent event) {
        // Shortcut.
        String slotName = pList.getString(GraphTypes.RELATION_SLOT);
        if (slotName == null) return;

        // Shortcut.
        Slot relationSlot = kb.getSlot(slotName);
        if (relationSlot == null) return;

        // If user has added or removed values from the slot that holds
        // reified relations, update the graph widget.
        Slot slot = event.getSlot();
        if (slot == relationSlot) {
            doc.savePositionInfo();
            doc.initNodes(getValues());
        }
    }

    private void addButtons(LabeledComponent lb) {
        // View button.
        viewSelectedInstancesAction = new AllowableAction("View Selected Instances", Icons.getViewIcon(), null) {
            public void actionPerformed(ActionEvent ae) {
                handleViewSelectedInstances();
            }
        };
        lb.addHeaderButton(viewSelectedInstancesAction);

        // Create button.
        createInstanceAction = new CreateAction("Create Instance") {
            public void onCreate() {
                handleCreateInstance();
            }
        };
        lb.addHeaderButton(createInstanceAction);

        // Add button.
        addExistingInstancesAction = new AddAction("Add Existing Instance") {
            public void onAdd() {
                handleAddExistingInstance();
            }
        };
        lb.addHeaderButton(addExistingInstancesAction);

        // Remove button.
        removeSelectedInstancesAction = new AllowableAction("Remove Selected Instances", Icons.getRemoveIcon(), null) {
            public void actionPerformed(ActionEvent ae) {
                handleRemoveSelectedInstances();
            }
        };
        lb.addHeaderButton(removeSelectedInstancesAction);

        // Delete button.
        deleteSelectedInstancesAction = new AllowableAction("Delete Selected Instances", Icons.getDeleteIcon(), null) {
            public void actionPerformed(ActionEvent ae) {
                handleDeleteSelectedInstances();
            }
        };
        lb.addHeaderButton(deleteSelectedInstancesAction);

        URL url = GraphWidget.class.getResource("images/Flowchart.gif");
        ImageIcon icon = new ImageIcon(url);
        performAutomaticLayoutAction = new AllowableAction("Perform Automatic Layout", icon, null) {
            public void actionPerformed(ActionEvent ae) {
                handlePerformAutomaticLayout(JGoLayeredDigraphAutoLayout.LD_DIRECTION_DOWN);
            }
        };
        lb.addHeaderButton(performAutomaticLayoutAction);

        url = GraphWidget.class.getResource("images/FlowchartRight.gif");
        icon = new ImageIcon(url);
        performAutomaticLayoutRightAction = new AllowableAction("Perform Automatic Layout", icon, null) {
            public void actionPerformed(ActionEvent ae) {
                handlePerformAutomaticLayout(JGoLayeredDigraphAutoLayout.LD_DIRECTION_RIGHT);
            }
        };
        lb.addHeaderButton(performAutomaticLayoutRightAction);

        saveAsImageAction = new AllowableAction("Save Graph As Image", Icons.getSaveProjectIcon(), null) {
            public void actionPerformed(ActionEvent ae) {
                handleSaveAsImage();
            }
        };
        lb.addHeaderButton(saveAsImageAction);
    }

    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        GraphConfigurationPanel dwcp = new GraphConfigurationPanel(this);
        return dwcp;
    }

    private void stuffPalette() {
    	palette.getDocument().setSuspendUpdates(true);
    	
        ArrayList nodes = new ArrayList();

        ArrayList allowedClses = new ArrayList(cls.getTemplateSlotAllowedClses(slot));
        for (int i = 0; i < allowedClses.size(); i++) {
            Cls cls = (Cls) allowedClses.get(i);
            if (cls.isConcrete() && cls.isVisible()) {
                nodes.add(cls);
            }

            ArrayList subClses = new ArrayList(cls.getSubclasses());
            for (int j = 0; j < subClses.size(); j++) {
                Cls subCls = (Cls) subClses.get(j);
                if (subCls.isConcrete() && subCls.isVisible()) {
                    nodes.add(subCls);
                }
            }
        }

        for (int i = 0; i < nodes.size(); i++) {
            Cls cls = (Cls) nodes.get(i);
            String clsName = cls.getName();
            Node node = makeNewNode(clsName, new Point());
            JGoDocument doc = palette.getDocument();
            doc.addObjectAtTail(node);
        }
        
        palette.getDocument().setSuspendUpdates(false);

        // Need to call this here - otherwise nodes aren't properly
        // positioned in a couple of obscure cases.
        palette.layoutItems();
    }

    private Node makeNewNode(String clsName, Point point) {
        Node node = new Node();

        // Initialize.
        NodeProperties props = new NodeProperties(clsName, pList);
        String shape = props.getShape();
        //node.initialize(point, shape, clsName);
        node.initialize(point, shape, props.getCustomDisplayName());

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

        node.setUniqueName(clsName);
        
        return node;
    }

    public void processViewChange(JGoViewEvent e) {
        //System.out.println("JGoViewEvent flag is: " + e.getFlags());
        //System.out.println("JGoViewEvent hint is: " + e.getHint());
        //System.out.println("");

        if (e.getHint() == JGoViewEvent.SELECTION_GAINED) {
            if (isLinkInSelection() && doc.isModifiable()) {
                removeSelectedInstancesAction.setEnabled(false);
            }
        }

        if (e.getHint() == JGoViewEvent.SELECTION_LOST) {
            if (!isLinkInSelection() && doc.isModifiable()) {
                removeSelectedInstancesAction.setEnabled(true);
            }
        }

        /**
         * @todo See if we can call savePositionInfo() on a subset of view
         * change events instead of all of them.
         */
        if (doc != null) {
            doc.savePositionInfo();
        }
    }

    private boolean isLinkInSelection() {
        boolean retval = false;

        JGoSelection selectedObjects = view.getSelection();
        JGoListPosition pos = selectedObjects.getFirstObjectPos();
        while (pos != null) {
            JGoObject obj = selectedObjects.getObjectAtPos(pos);
            if (obj instanceof JGoLink) {
                retval = true;
                break;
            }
            pos = selectedObjects.getNextObjectPosAtTop(pos);
        }

        return retval;
    }

    public void setEditable(boolean b) {
        if (doc != null) {
            doc.setModifiable(b);
        }

        setAllowed(addExistingInstancesAction, b);
        setAllowed(createInstanceAction, b);
        setAllowed(deleteSelectedInstancesAction, b);
        setAllowed(performAutomaticLayoutAction, b);
        setAllowed(performAutomaticLayoutRightAction, b);
        setAllowed(removeSelectedInstancesAction, b);
    }

    public GraphDocument getDocument() {
        return doc;
    }

    public GraphView getView() {
        return view;
    }

    public Collection getSelection() {
        ArrayList selectedInstances = new ArrayList();
        JGoSelection selectedObjects = view.getSelection();

        JGoListPosition pos = selectedObjects.getFirstObjectPos();
        while (pos != null) {
            JGoObject obj = selectedObjects.getObjectAtPos(pos);
            if (obj instanceof Node) {
                Node node = (Node) obj;
                selectedInstances.add(node.getInstance());
            } else if (obj instanceof ComplexLink) {
                ComplexLink cl = (ComplexLink) obj;
                selectedInstances.add(cl.getInstance());
            }
            pos = selectedObjects.getNextObjectPosAtTop(pos);
        }

        return selectedInstances;
    }

    protected void handleDoubleClick() {
        super.handleDoubleClick();
    }

    public void dispose() {
        if (!isDesignTime()) {
            getInstance().removeFrameListener(frameListener);
            doc.removeListener();
        }
        super.dispose();
    }

    /**
     * Provides access to position information for either:<br><br>
     * a) nodes<br>
     * b) elbows in connectors (only works for connectors that have instances
     * associated with them -- i.e. reified relations)
     *
     * @param instance the instance containing the slot that is using the graph widget
     * @param slot the slot that is using the graph widget
     * @param slotValue the slot value for which position information is desired
     * @return Two possible return values:<br><br>
     * a) Rectangle object if slotValue parameter is associated with a node<br>
     * b) Point or collection of Point objects if slotValue parameter is
     * associated with a connector that has elbows
     */
    public static Object getPositionInfo(Instance instance, Slot slot,
                                         Instance slotValue) {
        Object obj;

        // Shortcut.
        if ((instance == null) || (slot == null) || (slotValue == null))
            return null;

        HashMap positionMap = new HashMap();
        String key = "InstanceGraphWidget." + instance.getName() + "." + slot.getName();
        KnowledgeBase kb = instance.getKnowledgeBase();
        positionMap = (HashMap) kb.getProject().getClientInformation(key);
        obj = positionMap.get(slotValue.getName());

        return obj;
    }

    public BufferedImage getImage() {
        return view.getImage();
    }
}
