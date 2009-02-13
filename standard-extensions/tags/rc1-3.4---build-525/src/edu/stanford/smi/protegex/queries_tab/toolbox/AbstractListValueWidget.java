package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.queries_tab.*;

abstract public class AbstractListValueWidget extends AbstractSlotValueWidget {
  protected static final Comparator theComparator = new FrameComparator();
  protected JList itsList;
  protected JComponent itsComp;

  protected boolean currentValue;
  protected Slot instanceSlot;
  protected Instance itsInstance;
  protected Action itsViewAction, itsSelectAction, itsRemoveAction;
  protected SlotValueWidgetMouse slotValueListMouse;


  protected KnowledgeBaseListener itsKBListener;

    public AbstractListValueWidget(SearchWidget widget) {
        super(widget);
    }

    protected void addActions() {
    }

    protected void addListener() {
        createListener();
    }

    protected void addMouse() {
        slotValueListMouse = new SlotValueWidgetMouse();
        itsList.addMouseListener(slotValueListMouse);
    }

    /** Create components in this relation display. */
    protected void createComponents(String label) {
        itsList = createList();
        JScrollPane scroll = new JScrollPane(itsList);
        scroll.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        addActions();
        addMouse();
        addListener();
    }

    public JList createList() {

        JList list = ComponentFactory.createSingleItemList(null);
        list.setCellRenderer(new FrameRenderer());
        return list;
    }

    protected void createListener() {
    }

    public void removeListener() {
        itsWidget.getKB().removeKnowledgeBaseListener(itsKBListener);
    }

    public void removeMouse() {
        itsList.removeMouseListener(slotValueListMouse);
    }

    public void setDisplayName(String name) {
        ArrayList displayNames = new ArrayList(CollectionUtilities.createCollection(name));
        ComponentUtilities.setListValues(itsList, displayNames);
        itsList.setForeground(Color.red);
        itsList.setToolTipText("The Specified item can not be founded in the Knowledge Base.");
    }

    protected void updateList() {
        ArrayList displayInstances = new ArrayList(CollectionUtilities.createCollection(itsInstance));
        Collections.sort(displayInstances, theComparator);
        ComponentUtilities.setListValues(itsList, displayInstances);
        itsList.setForeground(Color.black);
        itsList.setToolTipText("");
    }
}
