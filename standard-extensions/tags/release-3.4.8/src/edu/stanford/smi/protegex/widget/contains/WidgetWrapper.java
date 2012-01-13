package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.widget.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class WidgetWrapper extends JPanel implements WidgetWrapperActionProcessor {
    private static final long serialVersionUID = -5606596964642743694L;
    private Instance _instance;
    private ClsWidget _centerWidget;
    private WidgetWrapperPool _widgetWrapperPool;
    private Project _project;
    private ListenForWidgetLayoutChanges _widgetLayoutListener;
    private MousePressListener _mousePressListener;
    private WidgetWrapperActionProcessorImpl _widgetActionProcessor;
    private FormDescription _formDescription;

    private class MousePressListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            _widgetActionProcessor.announceWrapperActivity(WidgetWrapper.this);
        }
    }

    private class ListenForWidgetLayoutChanges implements WidgetListener {
        public void labelChanged(WidgetEvent event) {
        }

        public void layoutChanged(WidgetEvent event) {
            replaceCenterWidget();
        }
    }

    public WidgetWrapper(
        Project project,
        Instance instance,
        WidgetWrapperPool widgetWrapperPool,
        FormDescription formDescription) {
        super(new BorderLayout());
        _project = project;
        _widgetLayoutListener = new ListenForWidgetLayoutChanges();
        _mousePressListener = new MousePressListener();
        _widgetWrapperPool = widgetWrapperPool;
        _widgetActionProcessor = new WidgetWrapperActionProcessorImpl();
        _formDescription = formDescription;
        setInstance(instance);
    }

    public void addWidgetWrapperActionListener(WidgetWrapperActionListener listener) {
        _widgetActionProcessor.addWidgetWrapperActionListener(listener);
    }

    public void announceWrapperActivity(WidgetWrapper activeWrapper) {
        _widgetActionProcessor.announceWrapperActivity(activeWrapper);
    }

    private void configureCenterWidget() {
        if (null != _instance) {
            if (null == _centerWidget) {
                _centerWidget = _project.createRuntimeClsWidget(_instance);
                _centerWidget.addWidgetListener(_widgetLayoutListener);
                JComponent component = (JComponent) _centerWidget;
                _centerWidget.setInstance(_instance);
                add(component, BorderLayout.CENTER);
                if (null != _formDescription) {
                    add(_formDescription, BorderLayout.SOUTH);
                    _formDescription.setInstance(_instance);
                }
                recursivelyRegisterForMousePresses(this);
            } else {
                if (null != _formDescription) {
                    _formDescription.setInstance(_instance);
                }
                _centerWidget.setInstance(_instance);
            }
        }
    }

    public void dispose() {
        if (_centerWidget != null) {
            _centerWidget.dispose();
            _centerWidget.removeWidgetListener(_widgetLayoutListener);
            _centerWidget = null;
        }
        return;
    }

    public Instance getInstance() {
        return _instance;
    }

    public Dimension getPreferredSize() {
        Dimension widgetPreferredSize;
        if (null == _centerWidget) {
            widgetPreferredSize = super.getPreferredSize();
        } else {
            widgetPreferredSize = ((JComponent) _centerWidget).getPreferredSize();
        }
        if (null != _formDescription) {
            Dimension formPreferredSize = _formDescription.getPreferredSize();
            widgetPreferredSize.height += formPreferredSize.height;
        }
        return widgetPreferredSize;
    }

    private void recursivelyRegisterForMousePresses(Container component) {
        component.addMouseListener(_mousePressListener);
        Component[] components = component.getComponents();
        for (int i = 0; i < components.length; i++) {
            components[i].addMouseListener(_mousePressListener);
            if (components[i] instanceof Container) {
                recursivelyRegisterForMousePresses((Container) components[i]);
            }
        }
        return;
    }

    private void recursivelyUnregisterForMousePresses(Container component) {
        component.removeMouseListener(_mousePressListener);
        Component[] components = component.getComponents();
        for (int i = 0; i < components.length; i++) {
            components[i].removeMouseListener(_mousePressListener);
            if (components[i] instanceof Container) {
                recursivelyUnregisterForMousePresses((Container) components[i]);
            }
        }
        return;
    }

    public void removeWidgetWrapperActionListener(WidgetWrapperActionListener listener) {
        _widgetActionProcessor.removeWidgetWrapperActionListener(listener);
    }

    private void replaceCenterWidget() {
        recursivelyUnregisterForMousePresses(this);
        dispose();
        configureCenterWidget();
    }

    public void returnToPool() {
        _widgetWrapperPool.returnWidgetWrapper(this);
    }

    public void setInstance(Instance instance) {
        _instance = instance;
        configureCenterWidget();
    }
}
