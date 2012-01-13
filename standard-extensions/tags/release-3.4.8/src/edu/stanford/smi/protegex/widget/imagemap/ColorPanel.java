package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;

import javax.swing.*;

/**
 *  Displays a Color used by ColorWell
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ColorPanel extends JPanel {
    private static final long serialVersionUID = -8752534214056126080L;
    private Insets _insets;
    private Rectangle _rect;
    private Color _color;

    public ColorPanel(Color color) {
        _rect = new Rectangle();
        _insets = new Insets(0, 0, 0, 0);
        _color = color;
        setBorder(BorderFactory.createEtchedBorder());
    }

    public void paint(Graphics g) {
        super.paint(g);
        getBounds(_rect);
        getInsets(_insets);
        g.setColor(_color);
        g.fillRect(
            _insets.left,
            _insets.top,
            _rect.width - _insets.left - _insets.right,
            _rect.height - _insets.top - _insets.bottom);
    }

    public void setColor(Color color) {
        _color = color;
        setBackground(_color.darker());
    }
}
