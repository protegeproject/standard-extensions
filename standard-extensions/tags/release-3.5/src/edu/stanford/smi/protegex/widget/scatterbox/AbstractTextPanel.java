package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;

import javax.swing.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class AbstractTextPanel extends JPanel implements Constants {
    private static final long serialVersionUID = 4647457494783249198L;
    protected String _text;
    protected Rectangle _bounds;

    public AbstractTextPanel(String text) {
        _text = text;
        _bounds = new Rectangle();
    }

    public Dimension getPreferredSize() {
        Dimension returnValue = new Dimension();
        Graphics g = getGraphics();
        FontMetrics fontMetrics = g.getFontMetrics();
        returnValue.height = returnValue.width = (int) (TEXT_SCALING_FACTOR * fontMetrics.getHeight());
        g.dispose();
        return returnValue;
    }

    public void paint(Graphics g) {
        super.paint(g);
        if ((g instanceof Graphics2D) && (null != _text)) {
            reallyPaint((Graphics2D) g);
        }
    }

    protected abstract void reallyPaint(Graphics2D g);
}
