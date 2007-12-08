package edu.stanford.smi.protegex.widget.contains;

import java.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class WidgetWrapperActionProcessorImpl implements WidgetWrapperActionProcessor {

    private ArrayList _widgetWrapperActionListeners;

    public WidgetWrapperActionProcessorImpl() {
        _widgetWrapperActionListeners = new ArrayList();
    }

    public void addWidgetWrapperActionListener(WidgetWrapperActionListener listener) {
        _widgetWrapperActionListeners.add(listener);
    }

    public void announceWrapperActivity(WidgetWrapper activeWrapper) {
        Iterator i = _widgetWrapperActionListeners.iterator();
        while (i.hasNext()) {
            ((WidgetWrapperActionListener) (i.next())).widgetWrapperHadActivity(activeWrapper);
        }
    }

    public void removeWidgetWrapperActionListener(WidgetWrapperActionListener listener) {
        _widgetWrapperActionListeners.remove(listener);
    }
}
