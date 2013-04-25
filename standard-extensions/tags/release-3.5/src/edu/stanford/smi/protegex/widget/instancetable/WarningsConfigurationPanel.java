package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class WarningsConfigurationPanel extends JPanel implements Validatable {
    private static final long serialVersionUID = -5303790631546834064L;
    private InstanceTableWidget _widget;

    public WarningsConfigurationPanel(InstanceTableWidget widget) {
        super(new BorderLayout());
        _widget = widget;
        add(InstanceTableConfigurationChecks.getDetailedWarnings(_widget.getCls(), _widget.getSlot()), BorderLayout.CENTER);
    }

    public void saveContents() {

    }

    public boolean validateContents() {
        return true;
    }
}
