package edu.stanford.smi.protegex.widget.imagemap;

import javax.swing.*;
import java.awt.*;

/**
 *  Description of the Class Line whose width is equal to the value of the
 *  annotated slider position
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class LineView extends JPanel implements SecondaryView {
    private static final long serialVersionUID = 4138435177070044265L;
    private Color _color;
    private Insets _insets;
    private Dimension _size;
    private BasicStroke _stroke;
    public static Color DEFAULT_COLOR = Color.black;

    public LineView() {
        this(DEFAULT_COLOR);
    }

    public LineView(Color color) {
        _color = color;
        _insets = new Insets(0, 0, 0, 0);
        _size = new Dimension(0, 0);
        setPreferredSize(new Dimension(40, 40));
        setOpaque(true);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (g instanceof Graphics2D) {
            twoDPaint((Graphics2D) g);
        }
    }

    public void setValue(int value) {
        _stroke = new BasicStroke((float) value);
        repaint();
    }

    private void twoDPaint(Graphics2D g) {
        if (null == _stroke) {
            return;
        }
        getInsets(_insets);
        getSize(_size);
        int startingY = _insets.top;
        int endingY = _size.height - (_insets.top + _insets.bottom);
        int xValue = _insets.left + ((int) (.5 * _size.width));
        g.setColor(_color);
        g.setStroke(_stroke);
        g.drawLine(xValue, startingY, xValue, endingY);
    }
}
