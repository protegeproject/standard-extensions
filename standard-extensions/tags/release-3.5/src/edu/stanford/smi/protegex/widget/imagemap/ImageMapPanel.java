package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 *  Handles all the display and translation into display coordinates The rest of
 *  the widgets all deal with the "logical coordinate" system.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ImageMapPanel extends JPanel {
    private static final long serialVersionUID = 6481052313012821946L;
    protected Map _colorsToRectangles;
    protected HashMap _logicalRectanglesToCoordinateRectangles;
    protected JScrollPane _container;
    protected ImageMapState _state;
    protected CoordinateTransform _mapping;
    protected ImageMapPanelEventSupport _broadcaster;
    private boolean _mouseButtonDown;
    private boolean _mouseDragging;

    // and now, the fun part.
    // listening to ourselves to propagate logical mouse events
    private class LogicalMouseMotionListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            if (_mouseDragging) {
                _broadcaster.callLogicalDrag(getLogicalPointForMouseEvent(e));
            } else {
                if (_mouseButtonDown) {
                    _mouseDragging = true;
                    _broadcaster.callLogicalDragStarted(getLogicalPointForMouseEvent(e));
                }
            }
        }
    }

    private class LogicalMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            _broadcaster.callLogicalClick(getLogicalPointForMouseEvent(e));
        }

        public void mousePressed(MouseEvent e) {
            _mouseButtonDown = true;
            _broadcaster.callLogicalPress(getLogicalPointForMouseEvent(e));
        }

        public void mouseReleased(MouseEvent e) {
            _mouseButtonDown = false;
            if (_mouseDragging) {
                _mouseDragging = false;
                _broadcaster.callLogicalDragFinished(getLogicalPointForMouseEvent(e));
            }
            _broadcaster.callLogicalRelease(getLogicalPointForMouseEvent(e));
        }
    }

    private class ColorComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            return ((Color) o1).getRGB() - ((Color) o2).getRGB();
        }
    }

    public ImageMapPanel(ImageMapState state) {
        _colorsToRectangles = new TreeMap(new ColorComparator());
        _logicalRectanglesToCoordinateRectangles = new HashMap();
        _state = state;
        _broadcaster = new ImageMapPanelEventSupport();
        setOpaque(true);
        addMouseMotionListener(new LogicalMouseMotionListener());
        addMouseListener(new LogicalMouseListener());
        return;
    }

    public void addImageMapPanelListener(ImageMapPanelListener newListener) {
        _broadcaster.addImageMapPanelListener(newListener);
    }

    public void addRectangle(Rectangle r, Color color) {
        ArrayList coloredRects = (ArrayList) _colorsToRectangles.get(color);
        if (null == coloredRects) {
            coloredRects = new ArrayList();
            _colorsToRectangles.put(color, coloredRects);
        }
        if (!coloredRects.contains(r)) {
            coloredRects.add(r);
        }
        repaint();
        return;
    }

    public void changeRectangleColor(Rectangle r, Color oldColor, Color newColor) {
        removeRectangle(r, oldColor);
        addRectangle(r, newColor);
    }

    // support for coordinate transformations
    private Rectangle convertLogicalRectangleToScreen(Rectangle r) {
        if (null == _mapping) {
            _mapping = new CoordinateTransform(_state.getLogicalCoordinateSystem(), this.getBounds());
        }
        return _mapping.mapRectIntoSecondRect(r);
    }

    private Point convertScreenPointIntoLogicalRectangle(Point p) {
        if (null == _mapping) {
            _mapping = new CoordinateTransform(_state.getLogicalCoordinateSystem(), this.getBounds());
        }
        return _mapping.mapPointIntoFirstRect(p);
    }

    private Point getLogicalPointForMouseEvent(MouseEvent e) {
        Point where = new Point(e.getX(), e.getY());
        return convertScreenPointIntoLogicalRectangle(where);
    }

    public Dimension getPreferredSize() {
        Image _imageToDisplay = _state.getAssociatedImage();
        if (null != _imageToDisplay) {
            Dimension returnValue = new Dimension(_imageToDisplay.getWidth(null), _imageToDisplay.getHeight(null));
            return returnValue;
        } else {
            return super.getPreferredSize();
        }
    }

    public void paint(Graphics g) {
        Image _imageToDisplay = _state.getAssociatedImage();
        if (null != _imageToDisplay) {
            g.drawImage(_imageToDisplay, 0, 0, null);
            if (g instanceof Graphics2D) {
                ((Graphics2D) g).setStroke(new BasicStroke((float) _state.getLineThickness()));
            }
            Iterator i = (_colorsToRectangles.keySet()).iterator();
            while (i.hasNext()) {
                Color newColor = (Color) i.next();
                paintRectangles(g, newColor);
            }
        } else {
            super.paint(g);
        }
        return;
    }

    // and lots of private methods to support this
    private void paintRectangles(Graphics g, Color color) {
        ArrayList rectangles = (ArrayList) _colorsToRectangles.get(color);
        g.setColor(color);
        int numberOfRects = rectangles.size();
        // not using iterators for speed / object creation reasons
        int loop;
        for (loop = 0; loop < numberOfRects; loop++) {
            Rectangle logicalRect = (Rectangle) rectangles.get(loop);
            Rectangle screenR = (Rectangle) _logicalRectanglesToCoordinateRectangles.get(logicalRect);
            if (null == screenR) {
                screenR = convertLogicalRectangleToScreen(logicalRect);
                _logicalRectanglesToCoordinateRectangles.put(logicalRect, screenR);
            }
            g.drawRect(screenR.x, screenR.y, screenR.width, screenR.height);
        }
        return;
    }

    public void removeAllRectangles() {
        _colorsToRectangles.clear();
        repaint();
    }

    public void removeImageMapPanelListener(ImageMapPanelListener oldListener) {
        _broadcaster.removeImageMapPanelListener(oldListener);
    }

    public void removeRectangle(Rectangle r, Color color) {
        ArrayList coloredRects = (ArrayList) _colorsToRectangles.get(color);
        if (null != coloredRects) {
            coloredRects.remove(r);
            if (0 == coloredRects.size()) {
                _colorsToRectangles.remove(color);
                repaint();
            }
        }
        return;
    }
}
