package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidget extends AbstractSlotWidget implements WidgetWrapperActionListener {
    private static final long serialVersionUID = -7974410907279569510L;
    private LabeledComponent _mainComponent;
    private ArrayList _displayedInstances;
    private Instance _selection;
    private Collection _actions = new ArrayList();
    private SummaryPanel _summaryPanel;
    private HashMap _instancesToWidgetWrappers;
    private AnnotationListener _annotationListener;
    private Instance _underlyingInstance;
    private WidgetWrapperHolder _widgetWrapperHolder;

    private ContainsWidgetState _state;
    private Class _formDescriptionClass;

    private class AnnotationListener implements FrameListener {
        public void browserTextChanged(FrameEvent p0) {
        }

        public void deleted(FrameEvent p0) {
        }

        public void editabilityChanged(FrameEvent p0) {
        }

        public void nameChanged(FrameEvent p0) {
        }

        public void ownFacetAdded(FrameEvent p0) {
        }

        public void ownFacetRemoved(FrameEvent p0) {
        }

        public void ownFacetValueChanged(FrameEvent p0) {
        }

        public void ownSlotAdded(FrameEvent p0) {
        }

        public void ownSlotRemoved(FrameEvent p0) {
        }

        public void visibilityChanged(FrameEvent p0) {
        }

        public void ownSlotValueChanged(FrameEvent p0) {
            if (null != _mainComponent) {
                _mainComponent.setHeaderLabel(buildAnnotatedLabel());
            }
        }
    }

    public ContainsWidget() {
        this(null);
    }

    public ContainsWidget(Class formDescriptionClass) {
        _formDescriptionClass = formDescriptionClass;
        setPreferredRows(4);
        setPreferredColumns(2);
        _displayedInstances = new ArrayList();
        _instancesToWidgetWrappers = new HashMap();
    }

    private void _addInstance(Instance instance) {
        if (null == instance) {
            return;
        }
        if (!_displayedInstances.contains(instance)) {
            int insertionPoint = getInsertionPoint();
            _displayedInstances.add(insertionPoint, instance);
            WidgetWrapper wrapper = getWidgetWrapperForInstance(instance);
            _widgetWrapperHolder.addWidgetWrapper(insertionPoint, wrapper);
            updateActions();
        }
        return;
    }

    private void _addInstances(Collection instances) {
        if (null != instances) {
            Iterator i = instances.iterator();
            while (i.hasNext()) {
                _addInstance((Instance) i.next());
            }
        }
        return;
    }

    private void addButtonsToComponent(LabeledComponent component) {
        if (_state.isDisplayViewInstanceButton()) {
            addButtonToComponent(component, new ContainsWidget_ViewAction(this));
        }
        if (_state.isDisplayCreateInstanceButton()) {
            addButtonToComponent(component, new ContainsWidget_CreateAction(this));
        }
        if (_state.isDisplayAddInstanceButton()) {
            addButtonToComponent(component, new ContainsWidget_AddAction(this));
        }
        if (_state.isDisplayRemoveInstanceButton()) {
            addButtonToComponent(component, new ContainsWidget_RemoveAction(this));
        }
        if (_state.isDisplayDeleteInstanceButton()) {
            addButtonToComponent(component, new ContainsWidget_DeleteAction(this));
        }
        if (_state.isDisplayPrototypeButton()) {
            addButtonToComponent(component, new ContainsWidget_PrototypeAction(this, _state.getPrototypeDepth()));
        }
    }

    private void addButtonToComponent(LabeledComponent component, Action action) {
        _actions.add(action);
        component.addHeaderButton(action);
    }

    public void addInstance(Instance instance) {
        _addInstance(instance);
        if (_state.isSelectNewInsertions()) {
            selectInstance(instance);
        }
        valueChanged();
        return;
    }

    public void addInstances(Collection instances) {
        _addInstances(instances);
        valueChanged();
        return;
    }

    private String buildAnnotatedLabel() {
        Instance instance = getInstance();
        Slot slot = getSlot();
        if ((null != instance) && (null != slot)) {
            String returnValue = getLabel() + " (" + instance.getOwnSlotValues(slot).size() + " values)";
            return returnValue;
        }
        return getLabel();
    }

    private void buildGUIWithoutSummaryPanel() {
        _mainComponent = new LabeledComponent(buildAnnotatedLabel(), _widgetWrapperHolder.getUserInterface(), true);
        addButtonsToComponent(_mainComponent);
        add(_mainComponent);
    }

    private void buildGUIWithSummaryPanel() {
        JPanel innerPanel = new JPanel(new BorderLayout());
        LabeledComponent widgetWrapperHolder = new LabeledComponent("", _widgetWrapperHolder.getUserInterface());
        innerPanel.add(widgetWrapperHolder, BorderLayout.CENTER);
        addButtonsToComponent(widgetWrapperHolder);
        _summaryPanel = new SummaryPanel(this);
        innerPanel.add(_summaryPanel, BorderLayout.NORTH);
        _mainComponent = new LabeledComponent(getLabel(), innerPanel, true);
        add(_mainComponent);
    }

    public void clearSelection() {
        _widgetWrapperHolder.clearSelection();
        _selection = null;
    }

    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        _state = new ContainsWidgetState(getPropertyList());
        return new ContainsWidgetConfigurationPanel(this);
    }

    public void dispose() {
        _state.dispose();
        if (null != _summaryPanel) {
            _summaryPanel.dispose();
        }
        if (null != _underlyingInstance) {
            _underlyingInstance.removeFrameListener(_annotationListener);
        }
        super.dispose();
    }

    private int getInsertionPoint() {
        if ((null != _selection) && (_state.isInsertAtCurrentSelection())) {
            return _displayedInstances.indexOf(_selection);
        }
        return _displayedInstances.size();
    }

    // if instance is removed find out who to select
    private Instance getNeighbor(Instance instance) {
        int numberOfInstances = _displayedInstances.size();
        if (numberOfInstances == 1) {
            return null;
        }
        int position = _displayedInstances.indexOf(instance);
        if (position == 0) {
            return (Instance) _displayedInstances.get(1);
        }
        return (Instance) _displayedInstances.get(position - 1);
    }

    public Instance getSelectedInstance() {
        return _selection;
    }

    public ContainsWidgetState getState() {
        return _state;
    }

    public Collection getValues() {
        return _displayedInstances;
    }

    private WidgetWrapper getWidgetWrapperForInstance(Instance instance) {
        WidgetWrapper widgetWrapper = (WidgetWrapper) _instancesToWidgetWrappers.get(instance);
        if (null != widgetWrapper) {
            return widgetWrapper;
        }
        Cls directType = instance.getDirectType();
        WidgetWrapperPool widgetWrapperPool = WidgetWrapperPool.getPoolForClass(directType, _formDescriptionClass, 25, 1);
        widgetWrapper = widgetWrapperPool.getWidgetWrapperForInstance(instance);
        _instancesToWidgetWrappers.put(instance, widgetWrapper);
        return widgetWrapper;
    }

    public void initialize() {
        _state = new ContainsWidgetState(getPropertyList());
        if (_state.isContainInVerticalDirection()) {
            _widgetWrapperHolder = new VerticalWidgetWrapperHolder();
        } else {
            _widgetWrapperHolder = new HorizontalWidgetWrapperHolder();
        }
        _widgetWrapperHolder.addWidgetWrapperActionListener(this);
        _widgetWrapperHolder.setSpacerSize(_state.getSpaceBetweenSubwidgets());
        _widgetWrapperHolder.setUseSeparators(_state.isSeparatorUsedBetweenSubwidgets());
        _annotationListener = new AnnotationListener();
        if (_state.isDisplaySummaryPanel()) {
            buildGUIWithSummaryPanel();
        } else {
            buildGUIWithoutSummaryPanel();
        }
        return;
    }

    public boolean isSelectionEmpty() {
        return null == _selection;
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        if ((null == cls) || (null == slot)) {
            return false;
        }
        if (cls.getTemplateSlotValueType(slot) != ValueType.INSTANCE) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        edu.stanford.smi.protege.Application.main(args);
    }

    public void removeInstance(Instance instance) {
        if (null == instance) {
            return;
        }
        if (_displayedInstances.contains(instance)) {
            if (_selection == instance) {
                Instance newSelection = getNeighbor(instance);
                if (null != newSelection) {
                    selectInstance(newSelection);
                } else {
                    clearSelection();
                }
            }
            _displayedInstances.remove(instance);
            _widgetWrapperHolder.removeWidgetWrapper(getWidgetWrapperForInstance(instance));
            valueChanged();
            updateActions();
        }
        return;
    }

    public void selectInstance(Instance instance) {
        if (_state.isSubwidgetSelectionAllowed()) {
            clearSelection();
            _selection = instance;
            _widgetWrapperHolder.setSelection(getWidgetWrapperForInstance(instance));
            updateActions();
        }
    }

    public void setEditable(boolean b) {
        // do nothing in here
    }

    public void setInstance(Instance instance) {
        super.setInstance(instance);
        if (!_state.isDisplaySummaryPanel()) {
            if (null != _underlyingInstance) {
                _underlyingInstance.removeFrameListener(_annotationListener);
            }
            _underlyingInstance = instance;
            _underlyingInstance.addFrameListener(_annotationListener);
            _mainComponent.setHeaderLabel(buildAnnotatedLabel());
        }
    }

    public void setValues(java.util.Collection values) {
        Instance oldSelection = _selection;
        clearSelection();
        _instancesToWidgetWrappers.clear();
        _displayedInstances.clear();
        _widgetWrapperHolder.removeAllWidgetWrappers();
        if (null != _summaryPanel) {
            _summaryPanel.setInstance(getInstance());
        }
        _addInstances(values);
        if (_displayedInstances.contains(oldSelection)) {
            selectInstance(oldSelection);
        }
        validate();
        updateActions();
        repaint();
    }

    private void updateActions() {
        Iterator i = _actions.iterator();
        while (i.hasNext()) {
            ((ContainsWidget_AbstractAction) (i.next())).updateActivation();
        }
    }

    public void valueChanged() {
        super.valueChanged();
        validate();
        repaint();
    }

    public void widgetWrapperHadActivity(WidgetWrapper wrapper) {
        Instance newSelection = wrapper.getInstance();
        if (getSelectedInstance() != newSelection) {
            clearSelection();
            selectInstance(newSelection);
        }
    }
}
