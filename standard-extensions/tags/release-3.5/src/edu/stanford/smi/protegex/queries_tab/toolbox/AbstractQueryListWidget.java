package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.awt.*;

import javax.swing.*;

import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.queries_tab.*;

public abstract class AbstractQueryListWidget extends JComponent {
    private static final long serialVersionUID = 3030054171940539496L;
    protected JList itsList;
    protected LabeledComponent itsLabeledComponent;

    protected Action itsViewAction, itsSelectAction, itsRemoveAction;
    protected InstancesQuery query;

    protected String itsName;
    protected QueriesTab itsTab;

    protected QueryListModel itsModel;

    public AbstractQueryListWidget(QueriesTab tab) {
        itsTab = tab;
    }

    /** Create components in this relation display. */
    protected void createComponents(String label, QueryListModel model) {

        itsModel = model;
        itsList = createList(itsModel);
        JScrollPane scroll = new JScrollPane(itsList);
        scroll.setPreferredSize(new Dimension(150, 40));

        itsLabeledComponent = new LabeledComponent(label, scroll);
        itsList.setCellRenderer(new QueriesTabRenderer());
    }

    public JList createList() {

        JList list = ComponentFactory.createList(null);
        list.setCellRenderer(new FrameRenderer());
        return list;
    }

    public JList createList(QueryListModel model) {

        JList list = new JList(model);
        list.setCellRenderer(new FrameRenderer());
        return list;
    }

    public JComponent getComponent() {
        return itsLabeledComponent;
    }

    public JList getList() {
        return itsList;
    }

    public QueryListModel getModel() {
        return itsModel;
    }

    public abstract String getName();

    public JPanel getPanel() {
        JPanel itsComp = new JPanel();
        itsComp.setLayout(new BorderLayout(10, 10));
        itsComp.add(itsLabeledComponent, BorderLayout.CENTER);
        return itsComp;
    }

    /** Return the embedded widget in the Query List */
    public JComponent getWidget() {
        return itsLabeledComponent;
    }
}
