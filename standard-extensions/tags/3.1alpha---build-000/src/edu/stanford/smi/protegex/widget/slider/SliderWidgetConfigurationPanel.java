package edu.stanford.smi.protegex.widget.slider;

import edu.stanford.smi.protege.widget.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SliderWidgetConfigurationPanel extends WidgetConfigurationPanel {

    public SliderWidgetConfigurationPanel(SliderWidget widget) {
        super(widget);
        addTab("Configure Slider", new MainConfigurationPanel(widget));
    }
}
