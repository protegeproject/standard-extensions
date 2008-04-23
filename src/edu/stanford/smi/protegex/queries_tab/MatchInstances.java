package edu.stanford.smi.protegex.queries_tab;

import java.util.*;

import edu.stanford.smi.protege.model.*;

public class MatchInstances {
   private boolean matchOption;
   private Vector widgets;
   private QueriesTab itsTab;

   private Collection instances;
   private Collection resultInstances;          // for combined instances
   private boolean ready;

    public MatchInstances() {
        matchOption = true;
    }

    public MatchInstances(QueriesTab tab, Cls cls, boolean o, Vector widgets) {
        itsTab = tab;
        matchOption = o;
        this.widgets = widgets;
    }

    private Collection combineCollection(Vector collectionVec) {

        resultInstances = (Collection) collectionVec.elementAt(0);
        if (resultInstances == null)
            resultInstances = new ArrayList();

        for (int i = 1; i < collectionVec.size(); i++) {
            Collection tmpInstances = (Collection) collectionVec.elementAt(i);
            if (tmpInstances == null)
                continue;
            Iterator j = tmpInstances.iterator();
            while (j.hasNext()) {
                Instance tmpInstance = (Instance) j.next();
                if (resultInstances.contains(tmpInstance))
                    continue;
                else
                    resultInstances.add(tmpInstance);
            }
        }
        return resultInstances;
    }

    public Collection getResult() {
        if (matchOption)
            return instances;
        else
            return resultInstances;
    }

    public Collection search() {

        ready = true;
        for (int i = 0; i < widgets.size(); i++) {
            if (!((SearchWidget) widgets.elementAt(i)).isReady()) {
                ready = false;
                break;
            }
        }

        if (!ready) {
            resultInstances = null;
            instances = null;
            return null;
        }

        // Match all or match any.

        itsTab.getQueryStack().push("Root");
        if (matchOption) {
            instances = ((SearchWidget) widgets.elementAt(0)).search();
            for (int i = 1; i < widgets.size(); i++) {
                instances = ((SearchWidget) widgets.elementAt(i)).search(instances);
                if (itsTab.getQueryStack().isEmpty()) {
                    return null;
                }
            }
            itsTab.getQueryStack().clear();
            return instances;
        } else {
            Vector instancesCol = new Vector();
            for (int i = 0; i < widgets.size(); i++) {
                instancesCol.addElement(((SearchWidget) widgets.elementAt(i)).search());
                if (itsTab.getQueryStack().isEmpty()) {
                    return null;
                }
            }

            itsTab.getQueryStack().clear();
            return combineCollection(instancesCol);
        }
    }
}
