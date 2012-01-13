package edu.stanford.smi.protegex.widget.graph;

import java.util.ArrayList;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.widget.WidgetConfigurationPanel;

public class GraphConfigurationPanel extends WidgetConfigurationPanel {
    private static final long serialVersionUID = 2522822069038437603L;
    private NodeConfigurationPanel nodePanel;
    private RelationConfigurationPanel relationPanel;
    private ViewConfigurationPanel viewPanel;
    private Slot slot;
    private Cls cls;
    private PropertyList propertyList;

    public GraphConfigurationPanel(GraphWidget widget) {
        super(widget);

        this.slot = widget.getSlot();
        this.cls = widget.getCls();
        this.propertyList = widget.getPropertyList();

        initialize();
    }

    private void initialize() {
        ArrayList allowedClses = new ArrayList(cls.getTemplateSlotAllowedClses(slot));
        nodePanel = new NodeConfigurationPanel(allowedClses, propertyList);
        addTab("Nodes", nodePanel);

        relationPanel = new RelationConfigurationPanel(cls, propertyList);
        addTab("Reified Relations", relationPanel);

        viewPanel = new ViewConfigurationPanel(cls, propertyList);
        addTab("View Properties", viewPanel);
    }

    public void saveContents() {
        super.saveContents();
        nodePanel.saveContents();
        viewPanel.saveContents();
        if (relationPanel.hasReifiedRelations()) {
            relationPanel.saveContents();
        }
    }
}