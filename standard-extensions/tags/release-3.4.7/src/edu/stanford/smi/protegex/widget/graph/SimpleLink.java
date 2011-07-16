package edu.stanford.smi.protegex.widget.graph;

import java.awt.Color;
import com.nwoods.jgo.*;

public class SimpleLink extends JGoLabeledLink {

    private static final long serialVersionUID = 1693064977873440928L;

    public SimpleLink() {
        super();
    }

    public SimpleLink(JGoPort from, JGoPort to) {
        super(from, to);

        JGoLinkLabel text = new JGoLinkLabel("");
        text.setAlignment(JGoText.ALIGN_CENTER);
        text.setSelectable(true);
        text.setEditOnSingleClick(false);
        text.setTransparent(false);

        // Partly transparent white.
        text.setBkColor(new Color(255, 255, 255, 200));

        setMidLabel(text);
        setAdjustingStyle(JGoLink.AdjustingStyleScale);
    }

    public void initialize(String text) {
        setText(text);
    }

    public void setText(String text)
    {
        JGoText obj = (JGoText) getMidLabel();
        if (obj != null) {
            obj.setText(text);
        }
    }
}