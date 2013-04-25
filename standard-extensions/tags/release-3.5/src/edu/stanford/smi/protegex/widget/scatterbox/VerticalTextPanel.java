package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;
import java.awt.geom.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class VerticalTextPanel extends AbstractTextPanel {
    private static final long serialVersionUID = -837027690458755678L;
    private AffineTransform _rotation;
    private AffineTransform _rotationBack;
    private AffineTransform _translation;
    private AffineTransform _translationBack;

    private final static double PI_OVER_2 = Math.PI / 2;

    public VerticalTextPanel(String text) {
        super(text);
        _rotation = new AffineTransform();
        _rotation.rotate(-1 * PI_OVER_2);
        _rotationBack = new AffineTransform();
        _rotationBack.rotate(PI_OVER_2);
        _translation = new AffineTransform();
        _translationBack = new AffineTransform();
        setSize(100, 100);
    }

    private void drawRotatedString(Graphics2D g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(_text);
        int stringHeight = fontMetrics.getDescent();
        g.transform(_rotation);
        g.drawString(_text, -stringWidth / 2, stringHeight);
        g.transform(_rotationBack);
    }

    protected void reallyPaint(Graphics2D g) {
        getBounds(_bounds);
        int halfWidth = _bounds.width / 2;
        int halfHeight = _bounds.height / 2;
        int negativeHalfWidth = -1 * halfWidth;
        int negativeHalfHeight = -1 * halfHeight;

        _translation.translate(halfWidth, halfHeight);
        _translationBack.translate(negativeHalfWidth, negativeHalfHeight);

        g.transform(_translation);
        drawRotatedString(g);
        g.transform(_translationBack);

        _translationBack.translate(halfWidth, halfHeight);
        _translation.translate(negativeHalfWidth, negativeHalfHeight);
    }
}
