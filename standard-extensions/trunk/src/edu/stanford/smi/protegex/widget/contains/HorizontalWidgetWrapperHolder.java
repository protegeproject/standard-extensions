package edu.stanford.smi.protegex.widget.contains;

import javax.swing.*;

/**
 *  Description of the Class
 *
 * @author William Grosso <grosso@smi.stanford.edu>
 */
public class HorizontalWidgetWrapperHolder extends WidgetWrapperHolder {

    public HorizontalWidgetWrapperHolder() {
        super(
            DEFAULT_SEPARATION_DISTANCE,
            DEFAULT_USE_SEPARATORS,
            DEFAULT_VERTICAL_SCROLL_INCREMENT,
            DEFAULT_HORIZONTAL_SCROLL_INCREMENT);
    }

    public HorizontalWidgetWrapperHolder(int spacerSize, boolean useSpacers) {
        super(spacerSize, useSpacers, DEFAULT_VERTICAL_SCROLL_INCREMENT, DEFAULT_HORIZONTAL_SCROLL_INCREMENT);
    }

    public HorizontalWidgetWrapperHolder(
        int spacerSize,
        boolean useSpacers,
        int verticalScrollIncrement,
        int horizontalScrollIncrement) {
        super(spacerSize, useSpacers, verticalScrollIncrement, horizontalScrollIncrement);
    }

    protected WidgetBox getWidgetBox(int spacerSize, boolean useSpacers) {
        return new WidgetBox(new HorizontalWidgetBoxLayoutManager(), spacerSize, useSpacers);
    }

    protected void scrollToCorrectLocation() {
        JScrollBar verticalScrollBarForScrollPane = _scrollPaneAroundWidgetBox.getVerticalScrollBar();
        if ((_selection != null) && (_selection.getParent() != null)) {
            _selectionRectangle = _selection.getBounds();
            _selectionRectangle.y += (_selection.getParent()).getY();
            _viewport.scrollRectToVisible(_selectionRectangle);
            _viewport.repaint();
        } else {
            verticalScrollBarForScrollPane.setValue(0);
        }
        return;
    }
}
