package edu.stanford.smi.protegex.widget.contains;

import javax.swing.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;

/**
 *  Not available from UI
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class FormDescription extends JLabel {

    private static final long serialVersionUID = 2951715720232312763L;
    protected Instance _instance;
    private SentenceUpdater _sentenceUpdater;
    private String _description;

    private class SentenceUpdater implements FrameListener {
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

        public void ownSlotValueChanged(FrameEvent event) {
            updateLabel();
        }
    }

    public FormDescription(Instance instance) {
        _sentenceUpdater = new SentenceUpdater();
        setHorizontalAlignment(SwingConstants.CENTER);
        setInstance(instance);
    }

    private void addListener() {
        if (null != _instance) {
            _instance.addFrameListener(_sentenceUpdater);
        }
    }

    protected abstract String getDescriptiveSentence();

    private void removeListener() {
        if (null != _instance) {
            _instance.removeFrameListener(_sentenceUpdater);
        }
    }

    public void setInstance(Instance instance) {
        removeListener();
        _instance = instance;
        updateLabel();
        addListener();
    }

    private void updateLabel() {
        _description = getDescriptiveSentence();
        setText(_description);
    }
}
