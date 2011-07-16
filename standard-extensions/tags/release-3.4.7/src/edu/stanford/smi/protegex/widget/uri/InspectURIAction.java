package edu.stanford.smi.protegex.widget.uri;

import java.awt.event.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    Qi Li <liq@smi.stanford.edu>
 */
public abstract class InspectURIAction extends AllowableAction {
    private static final long serialVersionUID = 8840305290335979538L;

    public InspectURIAction(String text) {
        super("Inspect URI Site", text, Icons.getInspectIcon(), null);
    }

    public void actionPerformed(ActionEvent event) {
        onInspectURI();
    }

    public abstract void onInspectURI();
}
