package edu.stanford.smi.protegex.queries_tab;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protegex.queries_tab.toolbox.*;

public class QueryListModel extends AbstractListModel{
   private static final long serialVersionUID = -4730000527615542083L;
private Vector data = new Vector();

    public QueryListModel() {
    }

    public QueryListModel(Vector v) {
        data = v;
    }

    public int addRow(Object obj) {
        data.addElement(obj);
        fireIntervalAdded(this, data.size() - 1, data.size() - 1);
        return (data.size() - 1);
    }

    public void deleteRow(int row) {
        InstancesQuery query = (InstancesQuery) data.elementAt(row);
        query.changed("DELETED");
        removeQuery(row);
        data.removeElementAt(row);
        fireIntervalRemoved(this, row, row);

    }

    public Object getElementAt(int index) {
        return getQueryAt(index);
    }

    public int getPosition(InstancesQuery query) {
        if (query == null)
            return -1;
        for (int i = 0; i < data.size(); i++) {
            InstancesQuery q = (InstancesQuery) data.elementAt(i);
            if (q.getName().equalsIgnoreCase(query.getName()))
                return i;
        }
        return -1;
    }

    public Collection getQueries() {
        return (Collection) data;
    }

    public InstancesQuery getQuery(InstancesQuery query) {
        for (int i = 0; i < data.size(); i++) {
            if (getQueryAt(i).getName().equalsIgnoreCase(query.getName()))
                return getQueryAt(i);
        }
        return null;
    }

    public InstancesQuery getQueryAt(int index) {
        InstancesQuery query = (InstancesQuery) data.elementAt(index);
        if (query == null)
            return null;
        else
            return query;

    }

    public InstancesQuery getQueryWithName(String name) {
        for (int i = 0; i < data.size(); i++) {
            InstancesQuery query = (InstancesQuery) data.elementAt(i);
            if (query.getName().equalsIgnoreCase(name))
                return query;
        }
        return null;
    }

    public int getSize() {
        return (data.size());
    }

    // this is used to try to remove the query once it is removed from the querylist.
    // One problem happened is what we should do if one of the query is now being viewed.
    private void removeQuery(int index) {
    }
}
