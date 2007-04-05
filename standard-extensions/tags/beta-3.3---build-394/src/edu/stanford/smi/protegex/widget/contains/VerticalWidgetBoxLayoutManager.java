package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;
import java.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class VerticalWidgetBoxLayoutManager extends WidgetBoxLayoutManager {
    protected int _outsideScrollPaneHeight;

    private int _placeWidgetWrapperInContainer(WidgetWrapper widgetWrapper, int yPosition) {
        Dimension wrapperSize = widgetWrapper.getPreferredSize();
        if (wrapperSize.width < _widgetBoxSize.width) {
            wrapperSize.width = _widgetBoxSize.width;
        }
        _currentContainer.add(widgetWrapper);
        wrapperSize.width += WidgetWrapperHolder.TWO_TIMES_BORDER_THICKNESS;
        wrapperSize.height += WidgetWrapperHolder.TWO_TIMES_BORDER_THICKNESS;
        widgetWrapper.setSize(wrapperSize);
        widgetWrapper.setLocation(0, yPosition);
        widgetWrapper.doLayout();
        wrapperSize = widgetWrapper.getPreferredSize();
        yPosition += wrapperSize.height;
        return yPosition;
    }

    protected void actuallyAddWrappers() {
        Collection currentWidgetWrappers = (_currentContainer.getWidgetWrappers());
        _widgetBoxSize.height = 0;

        _widgetSeparators = _currentContainer.getWidgetSeparators();
        _currentContainer.clearWidgetSeparators();
        _currentContainer.removeAll();
        Iterator i = currentWidgetWrappers.iterator();
        while (i.hasNext()) {
            WidgetWrapper widgetWrapper = (WidgetWrapper) i.next();
            if (i.hasNext()) {
                _widgetBoxSize.height = placeWidgetWrapperInContainer(widgetWrapper, _widgetBoxSize.height);
            } else {
                _widgetBoxSize.height = placeLastWidgetWrapperInContainer(widgetWrapper, _widgetBoxSize.height);
            }
        }
    }

    private int addInWidgetSeparator(int yPosition, int height) {
        WidgetSeparator widgetSeparator = null;
        if (_widgetSeparators.size() > 0) {
            widgetSeparator = (WidgetSeparator) _widgetSeparators.removeFirst();
        } else {
            widgetSeparator = new VerticalWidgetSeparator(3);
        }
        Dimension widgetSeparatorSize = new Dimension();
        widgetSeparatorSize.setSize(_widgetBoxSize.width, height);
        widgetSeparator.setSize(widgetSeparatorSize);
        widgetSeparator.setLocation(0, yPosition);
        _currentContainer.add(widgetSeparator);
        _currentContainer.addWidgetSeparator(widgetSeparator);
        return yPosition + height;
    }

    protected void adjustBoxSizeForScrollPane() {
        if ((null != _outsideScrollPane) && (_widgetBoxSize.height > _outsideScrollPaneHeight)) {
            resizeForScrollBar();
        }
        _widgetSeparators = null;
        return;
    }

    protected void makeFirstPassAtBoxDimensions() {
        Insets insets = _parentComponent.getInsets();
        Collection currentWidgetWrappers = (_currentContainer.getWidgetWrappers());
        Iterator i = currentWidgetWrappers.iterator();

        _outsideScrollPaneHeight = _parentComponent.getHeight() - insets.top - insets.bottom;
        _widgetBoxSize.width = _parentComponent.getWidth() - insets.left - insets.right;
        _widgetBoxSize.height = 0;
        while (i.hasNext()) {
            WidgetWrapper widgetWrapper = (WidgetWrapper) i.next();
            Dimension wrapperSize = widgetWrapper.getPreferredSize();
            if (wrapperSize.width > _widgetBoxSize.width) {
                _widgetBoxSize.width = wrapperSize.width;
            }
        }
    }

    private int placeLastWidgetWrapperInContainer(WidgetWrapper widgetWrapper, int yPosition) {
        yPosition = _placeWidgetWrapperInContainer(widgetWrapper, yPosition);
        return yPosition;
    }

    private int placeWidgetWrapperInContainer(WidgetWrapper widgetWrapper, int yPosition) {
        yPosition = _placeWidgetWrapperInContainer(widgetWrapper, yPosition);
        if (_currentContainer.isUseSeparators()) {
            yPosition = addInWidgetSeparator(yPosition, _currentContainer.getSpacerSize());
        } else {
            yPosition += _currentContainer.getSpacerSize();
        }
        return yPosition;
    }

    private void resizeForScrollBar() {
        Component[] components = _currentContainer.getComponents();
        int scrollBarWidth = (_outsideScrollPane.getVerticalScrollBar()).getWidth() + 2;
        for (int counter = 0; counter < components.length; counter++) {
            Dimension size = components[counter].getSize();
            size.width -= scrollBarWidth;
            components[counter].setSize(size);
        }
    }
}
