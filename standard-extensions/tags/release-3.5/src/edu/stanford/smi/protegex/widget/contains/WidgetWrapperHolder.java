package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class WidgetWrapperHolder implements WidgetWrapperActionProcessor {

    protected int _verticalScrollIncrement;
    protected int _horizontalScrollIncrement;

    protected WidgetWrapper _selection;
    protected WidgetWrapper _activeWidgetWrapper;
    protected Border _defaultBorder;
    protected Border _selectionBorder;
    protected JScrollPane _scrollPaneAroundWidgetBox;
    protected WidgetBox _widgetBox;
    protected JViewport _viewport;
    protected Rectangle _selectionRectangle;
    public final static int BORDER_THICKNESS = 2;
    public final static int TWO_TIMES_BORDER_THICKNESS = 4;
    public final static boolean DEFAULT_USE_SEPARATORS = true;
    public final static int DEFAULT_SEPARATION_DISTANCE = 6;
    public final static int DEFAULT_VERTICAL_SCROLL_INCREMENT = 20;
    public final static int DEFAULT_HORIZONTAL_SCROLL_INCREMENT = 10;

    public WidgetWrapperHolder() {
        this(
            DEFAULT_SEPARATION_DISTANCE,
            DEFAULT_USE_SEPARATORS,
            DEFAULT_VERTICAL_SCROLL_INCREMENT,
            DEFAULT_HORIZONTAL_SCROLL_INCREMENT);
    }

    public WidgetWrapperHolder(int spacerSize, boolean useSpacers) {
        this(spacerSize, useSpacers, DEFAULT_VERTICAL_SCROLL_INCREMENT, DEFAULT_HORIZONTAL_SCROLL_INCREMENT);
    }

    public WidgetWrapperHolder(
        int spacerSize,
        boolean useSpacers,
        int verticalScrollIncrement,
        int horizontalScrollIncrement) {
        _widgetBox = getWidgetBox(spacerSize, useSpacers);

        _selection = null;
        _defaultBorder =
            BorderFactory.createEmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS);
        _selectionBorder = BorderFactory.createLineBorder(Color.black, BORDER_THICKNESS);

        _verticalScrollIncrement = verticalScrollIncrement;
        _horizontalScrollIncrement = horizontalScrollIncrement;
        _scrollPaneAroundWidgetBox = new JScrollPane(_widgetBox);
        _viewport = _scrollPaneAroundWidgetBox.getViewport();
        (_scrollPaneAroundWidgetBox.getVerticalScrollBar()).setUnitIncrement(_verticalScrollIncrement);
        (_scrollPaneAroundWidgetBox.getHorizontalScrollBar()).setUnitIncrement(_horizontalScrollIncrement);
        _selectionRectangle = new Rectangle();
    }

    public void addWidgetWrapper(int index, WidgetWrapper widgetWrapper) {
        if (_widgetBox.addWidgetWrapper(index, widgetWrapper)) {
            widgetWrapper.setBorder(_defaultBorder);
            refreshBoxState();
        }
        return;
    }

    public void addWidgetWrapperActionListener(WidgetWrapperActionListener listener) {
        _widgetBox.addWidgetWrapperActionListener(listener);
    }

    public void clearSelection() {
        unselectWidgetWrapper(_selection);
    }

    public int getSpacerSize() {
        return _widgetBox.getSpacerSize();
    }

    public Component getUserInterface() {
        return _scrollPaneAroundWidgetBox;
    }

    public boolean getUseSeparators() {
        return _widgetBox.isUseSeparators();
    }

    protected abstract WidgetBox getWidgetBox(int spacerSize, boolean useSpacers);

    public boolean isUseSeparators() {
        return _widgetBox.isUseSeparators();
    }

    private void refreshBoxState() {
        _widgetBox.doLayout();
        scrollToCorrectLocation();
    }

    public void removeAllWidgetWrappers() {
        unselectWidgetWrapper(_selection);
        _widgetBox.removeAllWidgetWrappers();
        return;
    }

    public void removeWidgetWrapper(WidgetWrapper widgetWrapper) {
        _widgetBox.removeWidgetWrapper(widgetWrapper);
        refreshBoxState();
        return;
    }

    public void removeWidgetWrapperActionListener(WidgetWrapperActionListener listener) {
        _widgetBox.removeWidgetWrapperActionListener(listener);
    }

    protected abstract void scrollToCorrectLocation();

    public void setSelection(WidgetWrapper component) {
        unselectWidgetWrapper(_selection);
        _selection = component;
        if (null == _selection) {
            return;
        }
        _selection.setBorder(_selectionBorder);
    }

    public void setSpacerSize(int spacerSize) {
        _widgetBox.setSpacerSize(spacerSize);
    }

    public void setUseSeparators(boolean useSeparators) {
        _widgetBox.setUseSeparators(useSeparators);
    }

    public void unselectWidgetWrapper(WidgetWrapper widgetWrapper) {
        if (null == widgetWrapper) {
            return;
        }
        if (widgetWrapper != _selection) {
            return;
        }
        widgetWrapper.setBorder(_defaultBorder);
        _selection = null;
        return;
    }
}
