package edu.stanford.smi.protegex.widget.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

public class ColorIcon implements Icon {
    private Color color;
    private int w, h;

    public ColorIcon() {
        this(Color.gray, 150, 15);
    }

    public ColorIcon(Color color, int w, int h) {
        this.color = color;
        this.w = w;
        this.h = h;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.black);
        g.drawRect(x, y, w-1, h-3);
        g.setColor(color);
        g.fillRect(x+1, y+1, w-2, h-4);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getIconWidth() {
        return w;
    }

    public int getIconHeight() {
        return h;
    }
}