package edu.stanford.smi.protegex.queries_tab;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.queries_tab.toolbox.*;

public abstract class AbstractSlotValueWidget implements SlotValueWidget {

    public final static int FIELDWIDTH = 135;
    public final static int FIELDHEIGHT = 25;

    private Project project;
    protected String slotName;
    protected String label;
    protected boolean visible;

    protected SlotSpecification specification;
    public Cls selection;
    public Slot itsSlot;
    public QueriesTab itsTab;
    protected Collection itsInstances;

    protected String[] constraints;
    public SearchWidget itsWidget;
    protected boolean isViewEnabled = true;

    public AbstractTemplateSlotNumberValidator _validator;

    /** Define table mouse adapter. Single click will show the search result. Double
      click will start a new presearch. */
    protected class SlotValueWidgetMouse extends java.awt.event.MouseAdapter {

        public SlotValueWidgetMouse() {

        }

        public void mousePressed(java.awt.event.MouseEvent event) {

            if (event.getClickCount() == 2) {
                viewObject();
            }
        }

    } // end of TableMouse

    /** Constructor of class AbstractRelationDisplay. */
    public AbstractSlotValueWidget(SearchWidget widget) {
        itsWidget = widget;
        visible = true;
        itsTab = widget.getTab();
    }

    /** flexible task */
    public void doTask(String type) {

    }

    /** Get the reference of current knowledge base in protege. */
    public KnowledgeBase getKB() {
        return itsTab.getKnowledgeBase();
    }

    /** Get the label of RelationDisplay. */
    public String getLabel() {
        if (label != null)
            return label;
        else {
            return new String("");
        }
    }

    /** Get the component which will locate in the north of relation display. */
    public JComponent getNorthComponent() {
        return null;
    }

    /** Get Protege project */
    public Project getProject() {
        return project;
    }

    /** Get the selected items. Return is an string array. */
    public String[] getSelectedItems() {
        return null;
    }

    /** Get the name of the slot for this relation display. */
    public String getSlotName() {
        return slotName;
    }

    public QueriesTab getTab() {
        return itsTab;
    }

    public void initialized() {

    }

    public boolean isComplete() {
        return true;
    }

    /** If the slot is single value, return true. Otherwise, return false. */
    public boolean isSlotSingleValued() {
        return true;
    }

    public boolean isViewEnabled() {
        return isViewEnabled;
    }

    /** get visible */
    public boolean isVisible() {
        return visible;
    }

    public void removeListener() {
    }

    public void removeMouse() {
    }

    /** For the formed search result, change the format. */
    public Collection search() {
        return null;
    }

    /** Enable/Disable the icons. */
    public void setActionsEnabled(boolean b) {

    }

    /** Setup the selected cls */
    public void setCls(Cls cls) {
        selection = cls;
    }

    public void setDisplayName(String name) {
    }

    /** Setup the instances collection */
    public void setInstances(Collection instances) {
        itsInstances = instances;
    }

    /** Set the Protege project. */
    public void setProject(Project proj) {
        project = proj;
    }

    /** Set the name of the slot for this relation display. */
    public void setSlotName(String n) {
        slotName = n;
    }

    /** Setup the search specification. */
    public void setSpecification(SlotSpecification spec) {
        specification = spec;
    }

    public void setViewEnabled(boolean b) {
        isViewEnabled = b;
    }

    /** Set visible */
    public void setVisible(boolean v) {
        visible = v;
    }

    public void viewObject() {

    }
}
