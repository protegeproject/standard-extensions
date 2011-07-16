package edu.stanford.smi.protegex.widget.contains;

import javax.swing.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class NumberOfSlotValuesComponent extends JLabel {
    private static final long serialVersionUID = 1219832772674332489L;
    private Instance _instance;
    private Slot _slot;
    private CountChangeChecker _listener;

    private class CountChangeChecker implements FrameListener {
        public void ownSlotValueChanged(FrameEvent event) {
            resetLabel();
        }

        public void browserTextChanged(FrameEvent event) {
        }

        public void deleted(FrameEvent event) {
        }

        public void editabilityChanged(FrameEvent event) {
        }

        public void nameChanged(FrameEvent event) {
        }

        public void ownFacetAdded(FrameEvent event) {
        }

        public void ownFacetRemoved(FrameEvent event) {
        }

        public void ownFacetValueChanged(FrameEvent event) {
        }

        public void ownSlotAdded(FrameEvent event) {
        }

        public void ownSlotRemoved(FrameEvent event) {
        }

        public void visibilityChanged(FrameEvent event) {
        }
    }

    public NumberOfSlotValuesComponent(Instance instance, Slot slot) {
        _slot = slot;
        _listener = new CountChangeChecker();
        setInstance(instance);
    }

    public void dispose() {
        if (_instance != null) {
            _instance.removeFrameListener(_listener);
        }
    }

    private void resetLabel() {
        int numberOfValues = (_instance.getOwnSlotValues(_slot)).size();
        switch (numberOfValues) {
            case 0 :
                setText("     This slot has no values.");
                break;
            case 1 :
                setText("     This slot currently has 1 value.");
                break;
            default :
                setText("     This slot currently has " + numberOfValues + " values.");
                break;
        }
    }

    public void setInstance(Instance newInstance) {
        dispose();
        _instance = newInstance;
        if (_instance != null) {
            _instance.addFrameListener(_listener);
            resetLabel();
        }
    }
}
