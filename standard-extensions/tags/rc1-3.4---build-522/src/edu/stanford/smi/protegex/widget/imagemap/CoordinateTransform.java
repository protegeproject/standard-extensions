package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;

/**
 *  Takes two rectangles, does the calculations both forwards and back.
 *  Basically a simple matrix transformation that restricts itself to integers
 *  (the domain of the AWT universe). Map from coordinates in one rectangle to
 *  coordinates in another rectangle. Used in diagrams.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class CoordinateTransform {
    private int _firstX;
    private int _firstY;
    private int _firstHeight;
    private int _firstWidth;
    private int _secondX;
    private int _secondY;
    private int _secondHeight;
    private int _secondWidth;
    private boolean _flipX;
    private boolean _flipY;

    public CoordinateTransform(Rectangle firstRect, Rectangle secondRect) {
        _flipX = false;
        _flipY = false;
        setFirstCoordinateSystem(firstRect);
        setSecondCoordinateSystem(secondRect);
        return;
    }

    private int computeTransform(
        int value,
        int fromFrameOrigin,
        int fromFrameLength,
        int toFrameOrigin,
        int toFrameLength,
        boolean flipped) {
        double diff = (value - fromFrameOrigin);
        double fraction = diff / fromFrameLength;
        if (!flipped) {
            return (int) (fraction * toFrameLength + toFrameOrigin);
        } else {
            return (int) (toFrameOrigin + toFrameLength - fraction * toFrameLength);
        }
    }

    public Point mapPointIntoFirstRect(int xCoord, int yCoord) {
        int x = computeTransform(xCoord, _secondX, _secondWidth, _firstX, _firstWidth, _flipX);
        int y = computeTransform(yCoord, _secondY, _secondHeight, _firstY, _firstHeight, _flipY);
        return new Point(x, y);
    }

    public Point mapPointIntoFirstRect(Point point) {
        if (point == null) {
            return null;
        }
        int x = computeTransform(point.x, _secondX, _secondWidth, _firstX, _firstWidth, _flipX);
        int y = computeTransform(point.y, _secondY, _secondHeight, _firstY, _firstHeight, _flipY);
        return new Point(x, y);
    }

    public Point mapPointIntoSecondRect(int xCoord, int yCoord) {
        int x = computeTransform(xCoord, _firstX, _firstWidth, _secondX, _secondWidth, _flipX);
        int y = computeTransform(yCoord, _firstY, _firstHeight, _secondY, _secondHeight, _flipY);
        return new Point(x, y);
    }

    public Point mapPointIntoSecondRect(Point point) {
        if (point == null) {
            return null;
        }
        int x = computeTransform(point.x, _firstX, _firstWidth, _secondX, _secondWidth, _flipX);
        int y = computeTransform(point.y, _firstY, _firstHeight, _secondY, _secondHeight, _flipY);
        return new Point(x, y);
    }

    public Rectangle mapRectIntoFirstRect(Rectangle rectangle) {
        if (rectangle == null) {
            return null;
        }
        int x = computeTransform(rectangle.x, _secondX, _secondWidth, _firstX, _firstWidth, _flipX);
        int y = computeTransform(rectangle.y, _secondY, _secondHeight, _firstY, _firstHeight, _flipY);
        int width = computeTransform(rectangle.x + rectangle.width, _secondX, _secondWidth, _firstX, _firstWidth, _flipX) - x;
        int height =
            computeTransform(rectangle.y + rectangle.height, _secondY, _secondHeight, _firstY, _firstHeight, _flipY) - y;
        return new Rectangle(x, y, width, height);
    }

    public Rectangle mapRectIntoSecondRect(Rectangle rectangle) {
        if (rectangle == null) {
            return null;
        }

        int x = computeTransform(rectangle.x, _firstX, _firstWidth, _secondX, _secondWidth, _flipX);
        int y = computeTransform(rectangle.y, _firstY, _firstHeight, _secondY, _secondHeight, _flipY);
        int width = computeTransform(rectangle.x + rectangle.width, _firstX, _firstWidth, _secondX, _secondWidth, _flipX) - x;
        int height =
            computeTransform(rectangle.y + rectangle.height, _firstY, _firstHeight, _secondY, _secondHeight, _flipY) - y;
        return new Rectangle(x, y, width, height);
    }

    public void setFirstCoordinateSystem(Rectangle rectangle) {
        _firstX = rectangle.x;
        _firstY = rectangle.y;
        _firstHeight = rectangle.height;
        _firstWidth = rectangle.width;
    }

    public void setFlipX(boolean flipX) {
        _flipX = flipX;
    }

    public void setFlipY(boolean flipY) {
        _flipY = flipY;
    }

    public void setSecondCoordinateSystem(Rectangle rectangle) {
        _secondX = rectangle.x;
        _secondY = rectangle.y;
        _secondHeight = rectangle.height;
        _secondWidth = rectangle.width;
    }
}
