package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class HorizontalTextPanel extends AbstractTextPanel {

    private static final long serialVersionUID = -6833422957783450287L;

    public HorizontalTextPanel(String text) {
        super(text);
    }

    protected void reallyPaint(Graphics2D g) {
        getBounds(_bounds);
        FontMetrics fontMetrics = g.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(_text);
        int stringHeight = fontMetrics.getAscent();
        _bounds.x += (_bounds.width - stringWidth) / 2;
        _bounds.y += (_bounds.height + stringHeight) / 2 - fontMetrics.getDescent();
        g.drawString(_text, _bounds.x, _bounds.y);
    }
}
