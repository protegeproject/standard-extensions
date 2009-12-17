package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;
import java.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class HorizontalWidgetBoxLayoutManager extends WidgetBoxLayoutManager {
    protected int _outsideScrollPaneWidth;

    private int _placeWidgetWrapperInContainer(WidgetWrapper widgetWrapper, int xPosition) {
        Dimension wrapperSize = widgetWrapper.getPreferredSize();
        if (wrapperSize.height < _widgetBoxSize.height) {
            wrapperSize.height = _widgetBoxSize.height;
        }
        _currentContainer.add(widgetWrapper);
        wrapperSize.width += WidgetWrapperHolder.TWO_TIMES_BORDER_THICKNESS;
        wrapperSize.height += WidgetWrapperHolder.TWO_TIMES_BORDER_THICKNESS;
        widgetWrapper.setSize(wrapperSize);
        widgetWrapper.setLocation(xPosition, 0);
        widgetWrapper.doLayout();
        xPosition += wrapperSize.width;
        return xPosition;
    }

    protected void actuallyAddWrappers() {
        Collection currentWidgetWrappers = (_currentContainer.getWidgetWrappers());
        _widgetBoxSize.width = 0;

        _widgetSeparators = _currentContainer.getWidgetSeparators();
        _currentContainer.clearWidgetSeparators();
        _currentContainer.removeAll();
        Iterator i = currentWidgetWrappers.iterator();
        while (i.hasNext()) {
            WidgetWrapper widgetWrapper = (WidgetWrapper) i.next();
            if (i.hasNext()) {
                _widgetBoxSize.width = placeWidgetWrapperInContainer(widgetWrapper, _widgetBoxSize.width);
            } else {
                _widgetBoxSize.width = placeLastWidgetWrapperInContainer(widgetWrapper, _widgetBoxSize.width);
            }
        }
    }

    private int addInWidgetSeparator(int xPosition, int width) {
        WidgetSeparator widgetSeparator = null;
        if (_widgetSeparators.size() > 0) {
            widgetSeparator = (WidgetSeparator) _widgetSeparators.removeFirst();
        } else {
            widgetSeparator = new HorizontalWidgetSeparator(3);
        }
        Dimension widgetSeparatorSize = new Dimension();
        widgetSeparatorSize.setSize(width, _widgetBoxSize.height);
        widgetSeparator.setSize(widgetSeparatorSize);
        widgetSeparator.setLocation(0, xPosition);
        _currentContainer.add(widgetSeparator);
        _currentContainer.addWidgetSeparator(widgetSeparator);
        return xPosition + width;
    }

    protected void adjustBoxSizeForScrollPane() {
        if ((null != _outsideScrollPane) && (_widgetBoxSize.width > _outsideScrollPaneWidth)) {
            resizeForScrollBar();
        }
        _widgetSeparators = null;
        return;
    }

    protected void makeFirstPassAtBoxDimensions() {
        Insets insets = _parentComponent.getInsets();
        Collection currentWidgetWrappers = (_currentContainer.getWidgetWrappers());
        Iterator i = currentWidgetWrappers.iterator();

        _outsideScrollPaneWidth = _parentComponent.getWidth() - insets.left - insets.right;
        _widgetBoxSize.height = _parentComponent.getWidth() - insets.top - insets.bottom;
        _widgetBoxSize.width = 0;
        while (i.hasNext()) {
            WidgetWrapper widgetWrapper = (WidgetWrapper) i.next();
            Dimension wrapperSize = widgetWrapper.getPreferredSize();
            if (wrapperSize.height > _widgetBoxSize.height) {
                _widgetBoxSize.height = wrapperSize.height;
            }
        }
    }

    private int placeLastWidgetWrapperInContainer(WidgetWrapper widgetWrapper, int xPosition) {
        xPosition = _placeWidgetWrapperInContainer(widgetWrapper, xPosition);
        return xPosition;
    }

    private int placeWidgetWrapperInContainer(WidgetWrapper widgetWrapper, int xPosition) {
        xPosition = _placeWidgetWrapperInContainer(widgetWrapper, xPosition);
        if (_currentContainer.isUseSeparators()) {
            xPosition = addInWidgetSeparator(xPosition, _currentContainer.getSpacerSize());
        } else {
            xPosition += _currentContainer.getSpacerSize();
        }
        return xPosition;
    }

    private void resizeForScrollBar() {
        Component[] components = _currentContainer.getComponents();
        int scrollBarheight = (_outsideScrollPane.getHorizontalScrollBar()).getHeight() + 2;
        for (int counter = 0; counter < components.length; counter++) {
            Dimension size = components[counter].getSize();
            size.height -= scrollBarheight;
            components[counter].setSize(size);
        }
    }
}
