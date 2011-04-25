package edu.stanford.smi.protegex.widget.uri;

import java.awt.event.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    Qi Li <liq@smi.stanford.edu>
 */
public abstract class CheckURIAction extends AllowableAction {
    public CheckURIAction(String text) {
        super("Check URI Syntax", text, Icons.getCheckIcon(), null);
    }

    public void actionPerformed(ActionEvent event) {
        onCheckURI();
    }

    public abstract void onCheckURI();
}
