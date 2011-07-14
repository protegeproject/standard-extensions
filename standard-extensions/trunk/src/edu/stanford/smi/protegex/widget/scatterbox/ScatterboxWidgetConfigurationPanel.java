package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.widget.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxWidgetConfigurationPanel extends WidgetConfigurationPanel {

    private static final long serialVersionUID = 2311558746294517379L;

    public ScatterboxWidgetConfigurationPanel(ScatterboxWidget widget) {
        super(widget);
        ScatterboxWidgetState widgetState = widget.getState();
        addTab("Action Buttons", new FourButtonsConfigurationPanel(widgetState));
        addTab("In-place Editing Characteristics", new ScatterboxInPlaceEditingConfigurationPanel(widgetState));
        addTab("Appearance", new OverallAppearanceConfigurationPanel(widgetState));
        addTab("Instance Creation", new PrototypingAndCreationConfigurationPanel(widgetState));
        addTab("Terminological Classes", new ClassTermPropertiesConfigurationPanel(widgetState));
        addTab("Terminological Instances", new InstanceTermPropertiesConfigurationPanel(widgetState));
    }
}
