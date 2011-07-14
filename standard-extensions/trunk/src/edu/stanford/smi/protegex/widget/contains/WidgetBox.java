package edu.stanford.smi.protegex.widget.contains;

import java.util.*;

import javax.swing.*;

/**
 *  Holds all the widget wrappers
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class WidgetBox extends JPanel implements WidgetWrapperActionProcessor {
    private static final long serialVersionUID = 6391893168211273958L;
    protected WidgetBoxLayoutManager _layoutManager;
    protected LinkedList _widgetWrappers;
    protected LinkedList _widgetSeparators;
    protected WidgetListener _widgetListener;
    protected WidgetWrapperActionProcessorImpl _widgetActionProcessor;
    protected boolean _useSeparators;
    protected int _spacerSize;

    private class WidgetListener implements WidgetWrapperActionListener {
        public void widgetWrapperHadActivity(WidgetWrapper wrapper) {
            announceWrapperActivity(wrapper);
        }
    }

    public WidgetBox(WidgetBoxLayoutManager layoutManager, int spacerSize, boolean useSeparators) {
        super(layoutManager);
        _layoutManager = (WidgetBoxLayoutManager) getLayout();
        _widgetSeparators = new LinkedList();
        _widgetWrappers = new LinkedList();
        _widgetActionProcessor = new WidgetWrapperActionProcessorImpl();
        _widgetListener = new WidgetListener();
    }

    public void addWidgetSeparator(WidgetSeparator separator) {
        if (!_widgetSeparators.contains(separator)) {
            _widgetSeparators.addFirst(separator);
        }
    }

    public boolean addWidgetWrapper(int index, WidgetWrapper widgetWrapper) {
        if (!_widgetWrappers.contains(widgetWrapper)) {
            _widgetWrappers.add(index, widgetWrapper);
            widgetWrapper.addWidgetWrapperActionListener(_widgetListener);
            return true;
        }
        return false;
    }

    public boolean addWidgetWrapper(WidgetWrapper widgetWrapper) {
        if (!_widgetWrappers.contains(widgetWrapper)) {
            _widgetWrappers.add(widgetWrapper);
            widgetWrapper.addWidgetWrapperActionListener(_widgetListener);
            return true;
        }
        return false;
    }

    public void addWidgetWrapperActionListener(WidgetWrapperActionListener listener) {
        _widgetActionProcessor.addWidgetWrapperActionListener(listener);
    }

    private void announceWrapperActivity(WidgetWrapper activeWrapper) {
        _widgetActionProcessor.announceWrapperActivity(activeWrapper);
    }

    public void clearWidgetSeparators() {
        _widgetSeparators.clear();
    }

    public int getNumberOfWidgetWrappers() {
        return _widgetWrappers.size();
    }

    public int getSpacerSize() {
        return _spacerSize;
    }

    public boolean getUseSeparators() {
        return _useSeparators;
    }

    public LinkedList getWidgetSeparators() {
        return new LinkedList(_widgetSeparators);
    }

    public Collection getWidgetWrappers() {
        return new ArrayList(_widgetWrappers);
    }

    public boolean isUseSeparators() {
        return _useSeparators;
    }

    public boolean removeAllWidgetWrappers() {
        Iterator i = (new ArrayList(_widgetWrappers)).iterator();
        while (i.hasNext()) {
            removeWidgetWrapper((WidgetWrapper) i.next());
        }
        _widgetWrappers.clear();
        removeAll();
        return true;
    }

    public boolean removeWidgetWrapper(WidgetWrapper widgetWrapper) {
        if (_widgetWrappers.contains(widgetWrapper)) {
            _widgetWrappers.remove(widgetWrapper);
            widgetWrapper.removeWidgetWrapperActionListener(_widgetListener);
            widgetWrapper.returnToPool();
            remove(widgetWrapper);
            return true;
        }
        return false;
    }

    public void removeWidgetWrapperActionListener(WidgetWrapperActionListener listener) {
        _widgetActionProcessor.removeWidgetWrapperActionListener(listener);
    }

    public void setSpacerSize(int spacerSize) {
        _spacerSize = spacerSize;
    }

    public void setUseSeparators(boolean useSeparators) {
        _useSeparators = useSeparators;
    }
}
