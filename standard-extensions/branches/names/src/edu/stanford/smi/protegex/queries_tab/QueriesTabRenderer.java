package edu.stanford.smi.protegex.queries_tab;

import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protegex.queries_tab.toolbox.*;

public class QueriesTabRenderer extends FrameRenderer {

    public void load(Object value) {
        if (value instanceof InstancesQuery) {
            loadQuery((InstancesQuery) value);
        } else {
            super.load(value);
        }
    }

 
    private void loadQuery(InstancesQuery query) {
        setMainText(query.getName());
        setMainIcon(Icons.getQueryIcon());
    }

}
