package edu.stanford.smi.protegex.widget.uri;

import java.awt.event.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    Qi Li <liq@smi.stanford.edu>
 */
public abstract class InspectURIAction extends AllowableAction {
    public InspectURIAction(String text) {
        super("Inspect URI Site", text, Icons.getInspectIcon(), null);
    }

    public void actionPerformed(ActionEvent event) {
        onInspectURI();
    }

    public abstract void onInspectURI();
}
