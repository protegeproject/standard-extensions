package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;
import java.util.*;

import javax.swing.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class WidgetBoxLayoutManager implements LayoutManager {
    protected WidgetBox _currentContainer;
    protected JScrollPane _outsideScrollPane;
    protected Container _parentComponent;
    protected LinkedList _widgetSeparators;

    protected Dimension _widgetBoxSize;

    public WidgetBoxLayoutManager() {
        _widgetBoxSize = new Dimension();
    }

    protected abstract void actuallyAddWrappers();

    public void addLayoutComponent(String name, Component comp) {
    }

    protected abstract void adjustBoxSizeForScrollPane();

    private void computeWidgetBoxSize() {
        makeFirstPassAtBoxDimensions();
        actuallyAddWrappers();
        adjustBoxSizeForScrollPane();
    }

    public void layoutContainer(Container parent) {
        verifyThatContainerIsAWidgetBox(parent);
        computeWidgetBoxSize();
        return;
    }

    protected abstract void makeFirstPassAtBoxDimensions();

    public Dimension minimumLayoutSize(Container parent) {
        layoutContainer(parent);
        return _widgetBoxSize;
    }

    public Dimension preferredLayoutSize(Container parent) {
        return minimumLayoutSize(parent);
    }

    public void removeLayoutComponent(Component comp) {
    }

    private void verifyThatContainerIsAWidgetBox(Component comp) {
        if (comp instanceof WidgetBox) {
            _currentContainer = (WidgetBox) comp;
            Container parentComponent = _currentContainer.getParent().getParent();
            if ((null != parentComponent) && (parentComponent instanceof JScrollPane)) {
                _outsideScrollPane = (JScrollPane) parentComponent;
                _parentComponent = parentComponent;
            } else {
                _parentComponent = _currentContainer.getParent();
                _outsideScrollPane = null;
            }

            return;
        }
        throw new Error(
            "Attempt to use WidgetBoxLayoutManager to lay out a component that isn't a WidgetBox (container is"
                + comp.toString()
                + ")");
    }
}
