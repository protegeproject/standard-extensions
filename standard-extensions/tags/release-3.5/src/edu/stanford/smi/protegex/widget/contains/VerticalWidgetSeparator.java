package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class VerticalWidgetSeparator extends WidgetSeparator {

    private static final long serialVersionUID = -5456836455925090388L;

    public VerticalWidgetSeparator(int divisor) {
        super(divisor);
    }

    public void paint(Graphics g) {
        Rectangle paintingArea = getBounds();
        paintingArea.height = (paintingArea.height) / _divisor;
        paintingArea.x = 5;
        paintingArea.width -= 10;
        paintingArea.y = paintingArea.height;
        g.setColor(Color.black);
        g.fillRoundRect(paintingArea.x, paintingArea.y, paintingArea.width, paintingArea.height, 3, 3);
    }
}
