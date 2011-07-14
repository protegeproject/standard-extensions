package edu.stanford.smi.protegex.widget.instancetable;

import edu.stanford.smi.protege.widget.*;
import edu.stanford.smi.protegex.util.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InstanceRowConfigurationPanel extends WidgetConfigurationPanel {

    private static final long serialVersionUID = 2314322872731122370L;

    public InstanceRowConfigurationPanel(InstanceTableWidget widget) {
        super(widget);
        InstanceTableWidgetState state = widget.getState();
        addTab("Define columns of instance table", new ColumnConfigurationPanel(state));
        addTab("New instances", new NewInstanceConfigurationPanel(state));
        addTab("In-place Editing Characteristics", new InPlaceEditingConfigurationPanel(state));
        addTab("Warnings And Suggestions", new WarningsConfigurationPanel(widget));
        addTab("Action Buttons ", new FourButtonsConfigurationPanel(state));
    }
}
