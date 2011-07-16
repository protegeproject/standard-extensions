package edu.stanford.smi.protegex.widget.uri;

import java.awt.event.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    Qi Li <liq@smi.stanford.edu>
 */
public abstract class CheckURIAction extends AllowableAction {
    private static final long serialVersionUID = 1274239534707522703L;

    public CheckURIAction(String text) {
        super("Check URI Syntax", text, Icons.getCheckIcon(), null);
    }

    public void actionPerformed(ActionEvent event) {
        onCheckURI();
    }

    public abstract void onCheckURI();
}
