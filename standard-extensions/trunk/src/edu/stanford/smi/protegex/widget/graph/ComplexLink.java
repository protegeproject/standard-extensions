package edu.stanford.smi.protegex.widget.graph;

import com.nwoods.jgo.JGoLabeledLink;
import com.nwoods.jgo.JGoLink;
import com.nwoods.jgo.JGoPort;
import com.nwoods.jgo.JGoText;

import edu.stanford.smi.protege.model.Instance;

public class ComplexLink extends JGoLabeledLink {
    private static final long serialVersionUID = -912304129823993806L;
    private Instance instance = null;

    public ComplexLink() {
        super();
    }

    public ComplexLink(JGoPort from, JGoPort to) {
        super(from, to);
        setGrabChildSelection(false);
        ComplexLinkLabel label = new ComplexLinkLabel();
        setMidLabel(label);
        setAdjustingStyle(JGoLink.AdjustingStyleScale);
    }

    public void setText(String text) {
        JGoText obj = (JGoText) getMidLabel();
        if (obj != null) {
            obj.setText(text);
        }
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }
}