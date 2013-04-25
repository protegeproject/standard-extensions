package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;

/**
 *  Description of the Class
 *
 * @author William Grosso <grosso@smi.stanford.edu>
 */
public class HorizontalWidgetSeparator extends WidgetSeparator {

    private static final long serialVersionUID = 8406596363131244697L;

    public HorizontalWidgetSeparator(int divisor) {
        super(divisor);
    }

    public void paint(Graphics g) {
        Rectangle paintingArea = getBounds();
        paintingArea.width = (paintingArea.width) / _divisor;
        paintingArea.y = 5;
        paintingArea.height -= 10;
        paintingArea.x = paintingArea.width;
        g.setColor(Color.black);
        g.fillRoundRect(paintingArea.x, paintingArea.y, paintingArea.width, paintingArea.height, 3, 3);
    }
}
