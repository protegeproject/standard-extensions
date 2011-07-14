package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.queries_tab.*;

public class QueryListWidget extends AbstractQueryListWidget{
  private static final long serialVersionUID = 903696432612020961L;
private Action itsLoadQueryAction;
  private Action itsViewQueryAction;
  private Action itsDeleteQueryAction;

  private InstancesQuery itsQuery;

   /** Define table mouse adapter. Single click will show the search result. Double
     click will start a new presearch. */
   protected class QueryListWidgetMouse extends java.awt.event.MouseAdapter
     {
        QueryListWidget queryList;
        public QueryListWidgetMouse(QueryListWidget t) {
             queryList = t;
        }

        public void mousePressed(java.awt.event.MouseEvent event)
        {
            Object object = event.getSource();
            int index;

            if (!(object instanceof JList) ) return;

      index = queryList.getList().getSelectedIndex();
            if (index < 0) return;
            if (itsModel.getSize() == 0) return;

            if(event.getClickCount() == 2)
            {
        InstancesQuery query = itsModel.getQueryAt(index);
        showQuery(query);
            }  else{
            }
    }


} // end of TableMouse


    public QueryListWidget(QueriesTab tab) {
        super(tab);
    }

    public QueryListWidget(String name, QueriesTab tab) {
        super(tab);
        itsName = name;
    }

    public QueryListWidget(String name, QueryListModel model, QueriesTab tab) {
        super(tab);
        itsName = name;
        initialize(model);
    }

    private int checkQueryUniqe(String name) {
        int length = itsModel.getSize();
        if (length < 1)
            return -1;

        for (int i = 0; i < length; i++) {
            String tmpName = ((InstancesQuery) itsModel.getQueryAt(i)).getName();
            if (tmpName.equalsIgnoreCase(name))
                return i;
        }

        return -1;
    }

    private boolean confirmDelete() {
        String text = "Delete the selected items?";
        int result = ModalDialog.showMessageDialog(this.getWidget(), text, ModalDialog.MODE_YES_NO);
        return result == ModalDialog.OPTION_YES;
    }

    private int confirmOverwrite(String name) {
        String text = "The name: [" + name + "] already exists. Would you like to overwrite it?";
        int result = ModalDialog.showMessageDialog(this.getWidget(), text, ModalDialog.MODE_YES_NO_CANCEL);
        return result;
    }

    public void doUpLoad(InstancesQuery query) {
        itsQuery = query;

        itsTab.clearSearch();
        Vector widgets = itsTab.getWidgets();
        JPanel searchWidgets = itsTab.getSearchPanel();

        itsTab.setupRadios(query.isMatchAll());
        itsTab.setQueryName(query.getName());

        SearchWidget widgetPanel0 = (SearchWidget) widgets.elementAt(0);
        widgetPanel0.setSelectedObjects(Helper.createObjs(query, 0), Helper.createNames(query, 0));

        for (int i = 1; i < query.getSize(); i++) {
 
            SearchWidget widgetPanel = new SearchWidget(itsTab, itsTab.getKnowledgeBase());
            widgetPanel.setSelectedObjects(Helper.createObjs(query, i), Helper.createNames(query, i));
            searchWidgets.add(widgetPanel);
            widgets.addElement(widgetPanel);
        }

        Box emptyBox = itsTab.getEmptyBox();
        searchWidgets.add(emptyBox);

        if (widgets.size() > 1) {
            searchWidgets.revalidate();
            searchWidgets.setPreferredSize(new Dimension(itsTab.getWidgetWidth(), itsTab.getWidgetHeight() * (widgets.size() + 1)));
        }

        searchWidgets.repaint();
        itsTab.setQuery(query);
        itsTab.enableSearch();
        setQueryButtons();

        if (query.getSize() < 2)
            itsTab.setupStatus(false);
        else
            itsTab.setupStatus(true);
    }

    /** This is used to download the current available Query.
           Each SearchWidget corresponds to one item in the InstancesQuery Vector.
    */
    public InstancesQuery downLoadQuery() {
        Vector widgets = itsTab.getWidgets();
        String name;

        name = itsTab.getQueryName();

        InstancesQuery query = new InstancesQuery(itsTab.getMatchAll());

        // create a new Query
        for (int i = 0; i < widgets.size(); i++) {
            SearchWidget tmpSearchWidget = (SearchWidget) widgets.elementAt(i);
            if (tmpSearchWidget.getCls() == null && tmpSearchWidget.getSearchSubject() == null)
                return null;

            Helper.addQuery(tmpSearchWidget, query);
        }

        if (name == null || name.trim().length() < 1) {
            QueryNamePanel panel = new QueryNamePanel();
            int result = ModalDialog.showDialog(this.getWidget(), panel, "Input Query Name", ModalDialog.MODE_OK_CANCEL);
            if (result == ModalDialog.OPTION_OK)
                name = panel.getText();
            itsTab.setQueryName(name);
        }

        if (name != null && name.trim().length() > 0) {
            int index = checkQueryUniqe(name);
            if (index < 0)
                query.setName(name);
            else {

                boolean loop = true;
                itsQuery = itsModel.getQueryAt(index);

                // Compare the two queries, if there are the same, just quite silently
                if (itsTab.compareQuery(itsQuery)) {
                    if (index > -1)
                        itsList.setSelectedIndex(index);
                    return null;
                }

                while (loop) {

                    int returnValue = confirmOverwrite(name);
                    if (returnValue == ModalDialog.OPTION_YES) {
                        query.setName(name);
                        // if there is already a query there, just overwrite it to the model
                        if (itsQuery != null) {
                            loop = false;
                            Helper.copyQuery(query, itsQuery);
                            itsQuery.changed("CHANGED");
                            int position = itsModel.getPosition(itsQuery);
                            if (position > -1)
                                itsList.setSelectedIndex(position);
                            return null;
                        }

                    } else if (returnValue == ModalDialog.OPTION_NO) {
                        QueryNamePanel panel = new QueryNamePanel();
                        int result = ModalDialog.showDialog(this.getWidget(), panel, "Input Query Name", ModalDialog.MODE_OK_CANCEL);
                        if (result == ModalDialog.OPTION_OK) {
                            name = panel.getText();
                            if (name != null && name.trim().length() > 0) {
                                index = checkQueryUniqe(name);
                                if (index < 0) {
                                    query.setName(name);
                                    itsTab.setQueryName(name);
                                    loop = false;
                                }
                            } else {
                                loop = false;
                                return null;
                            }
                        } else {
                            loop = false;
                            return null;
                        }
                    } else { // closed and cancel
                        loop = false;
                        return null;
                    }

                } // end of while
            } // end of else
        } else
            return null;
        return query;

    }

    //private Action getDeleteQueryAction() {
    private Action getDeleteQueryAction() {
        return new AbstractAction("Delete Query", Icons.getDeleteQueryIcon()) {
            private static final long serialVersionUID = 4537581912418530922L;

            public void actionPerformed(ActionEvent event) {

                int index = itsList.getSelectedIndex();
                if (index < 0)
                    return;

                if (confirmDelete()) {
                    itsModel.deleteRow(index);
                    if (itsModel.getSize() < 1)
                        switchActions(false);
                    else {
                        switchActions(true);
                        if (index < itsModel.getSize())
                            itsList.setSelectedIndex(index);
                        else
                            itsList.setSelectedIndex(itsModel.getSize() - 1);
                    }
                }
                setQueryButtons();

            }
        };
    }

    /** Load the existing query to the search panel */
    private Action getLoadQueryAction() {
        return new AbstractAction("Retrieve Query", Icons.getRetrieveQueryLibraryIcon()) {
            private static final long serialVersionUID = 3954405178351744665L;

            public void actionPerformed(ActionEvent event) {
                int index = itsList.getSelectedIndex();
                if (index < 0)
                    return;
                InstancesQuery query = itsModel.getQueryAt(index);
                upLoadQuery(query);
            }
        };
    }

    public String getName() {
        return itsName;
    }

    private Action getViewQueryAction() {
        return new AbstractAction("View Query", Icons.getViewQueryIcon()) {
            private static final long serialVersionUID = 4092658981529047521L;

            public void actionPerformed(ActionEvent event) {

                int index = itsList.getSelectedIndex();
                if (index < 0)
                    return;
                InstancesQuery query = itsModel.getQueryAt(index);
                showQuery(query);
            }
        };
    }

    public void initialize(QueryListModel model) {
        createComponents("Query Library", model);

        QueryListWidgetMouse queryListMouse = new QueryListWidgetMouse(this);
        itsList.addMouseListener(queryListMouse);

        itsViewQueryAction = getViewQueryAction();
        itsDeleteQueryAction = getDeleteQueryAction();
        itsLoadQueryAction = getLoadQueryAction();

        LabeledComponent c = itsLabeledComponent;
        c.addHeaderButton(itsViewQueryAction);
        c.addHeaderButton(itsLoadQueryAction);
        c.addHeaderButton(itsDeleteQueryAction);

        setQueryButtons();
    }

    private boolean preUpLoad(InstancesQuery query) {
        if (!itsTab.isEmptySearchPanel()) {

            if (itsTab.compareQuery(query))
                return false; // the current query are the save as the one which will be loaded.
            if (!itsTab.compareQueries()) {

                String text;
                if (itsTab.getQueryName() != null && itsTab.getQueryName().trim().length() > 1)
                    text = "Do you want to save the changes you made to above [" + itsTab.getQueryName() + "]?";
                else
                    text = "Do you want to save the query you created above? ";
                int result = ModalDialog.showMessageDialog(this.getWidget(), text, ModalDialog.MODE_YES_NO_CANCEL);
                if (result == ModalDialog.OPTION_YES) {
                    // go to save it first and then load it.
                    itsTab.downLoadQuery();
                } else if (result == ModalDialog.OPTION_CANCEL)
                    return false;
            }
        }
        return true;
    }

    public void setQueryButtons() {
        if (itsModel.getSize() == 0)
            itsTab.enableQueryButtons(false);
        else
            itsTab.enableQueryButtons(true);
    }

    public void showQuery(InstancesQuery query) {
        itsTab.showDialog(query);
    }

    public void switchActions(boolean b) {
        itsViewQueryAction.setEnabled(b);
        itsDeleteQueryAction.setEnabled(b);
        itsLoadQueryAction.setEnabled(b);

    }

    /** This is used to load the current available query to the search panel. */
    public void upLoadQuery(InstancesQuery query) {
        if (preUpLoad(query))
            doUpLoad(query);
    }
}
