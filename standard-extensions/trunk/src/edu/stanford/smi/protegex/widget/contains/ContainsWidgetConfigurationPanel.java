package edu.stanford.smi.protegex.widget.contains;

import edu.stanford.smi.protege.widget.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidgetConfigurationPanel extends WidgetConfigurationPanel {

    private static final long serialVersionUID = -4300073822249980383L;

    public ContainsWidgetConfigurationPanel(ContainsWidget widget) {
        super(widget);
        addTab("Action Buttons", new SixButtonsConfigurationPanel(widget.getState()));
        addTab("Appearance", new OverallAppearanceConfigurationPanel(widget.getState()));
        addTab("Instance Creation", new PrototypingAndCreationConfigurationPanel(widget.getState()));
        addTab("Summary Panel", new SummaryConfigurationPanel(widget.getState()));
    }
}
