package edu.stanford.smi.protegex.widget.scatterbox;

import java.util.*;

import edu.stanford.smi.protege.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.widget.abstracttable.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxWidgetState extends AbstractTableWidgetState implements Constants {
    private ScatterboxWidget _widget;
    private KnowledgeBase _kb;

    private final static String HORIZONTAL_SLOT = ":HORIZONTAL:SLOT";
    private final static String VERTICAL_SLOT = ":VERTICAL:SLOT";
    private final static String HORIZONTAL_AXIS_LABEL = ":HORIZONTAL:AXIS:LABEL";
    private final static String VERTICAL_AXIS_LABEL = ":VERTICAL:AXIS:LABEL";
    private final static String AUTOCREATE_WHEN_EDITING = ":AUTOCREATE:WHEN:EDITING";
    private final static String HIERARCHICAL_POLICY_WHEN_CLASSES_ARE_DOMAIN_INDICES =
        ":HIERARCHICAL:POLICY:WHEN:CLASSES:ARE:DOMAIN:INDICES";
    private final static String INCLUDE_ABSTRACT_CLASSES_IN_TERM_LIST = ":INCLUDE:ABSTRACT:CLASSES:IN:TERM:LIST";
    private final static String POLICY_WHEN_INSTANCES_ARE_DOMAIN_INDICES = ":POLICY:WHEN:INSTANCES:ARE:DOMAIN:INDICES";
    private Slot _horizontalSlot;
    private Slot _verticalSlot;
    private String _horizontalSlotName;
    private String _verticalSlotName;
    private boolean _autocreateWhenEditing;
    private boolean _automaticallyDisplayFormsForCreatedInstances;
    private boolean _includeAbstractClassesInTermList;
    private String _hierarchicalPolicyWhenClassesAreDomainIndices;
    private String _policyWhenInstancesAreDomainIndices;
    private String _horizontalAxisLabel;
    private String _verticalAxisLabel;

    public static void main(String[] args) {
        Application.main(args);
    }
    public ScatterboxWidgetState(ScatterboxWidget widget, PropertyList properties) {
        super(properties);
        _widget = widget;
        _kb = _widget.getKnowledgeBase();
        _horizontalSlot = getSlot(_horizontalSlotName);
        _verticalSlot = getSlot(_verticalSlotName);
        if ((null == _horizontalSlot) && (null == _verticalSlot)) {
            setSlotsToDefaultValues();
        }
    }

    private Slot getSlot(String name) {
        return (name == null) ? null : _kb.getSlot(name);
    }

    public void dispose() {

    }

    public String getHierarchicalPolicyWhenClassesAreDomainIndices() {
        return _hierarchicalPolicyWhenClassesAreDomainIndices;
    }

    public String getHorizontalAxisLabel() {
        return _horizontalAxisLabel;
    }

    public Slot getHorizontalSlot() {
        return _horizontalSlot;
    }

    public String getPolicyWhenInstancesAreDomainIndices() {
        return _policyWhenInstancesAreDomainIndices;
    }

    public String getVerticalAxisLabel() {
        return _verticalAxisLabel;
    }

    public Slot getVerticalSlot() {
        return _verticalSlot;
    }

    public ScatterboxWidget getWidget() {
        return _widget;
    }

    public boolean isAutocreateWhenEditing() {
        return _autocreateWhenEditing;
    }

    public boolean isAutomaticallyDisplayFormsForCreatedInstances() {
        return _automaticallyDisplayFormsForCreatedInstances;
    }

    public boolean isIncludeAbstractClassesInTermList() {
        return _includeAbstractClassesInTermList;
    }

    public void restore() {
        _horizontalSlotName = readString(HORIZONTAL_SLOT, null);
        _verticalSlotName = readString(VERTICAL_SLOT, null);
        if (null != _kb) {
            _horizontalSlot = getSlot(_horizontalSlotName);
            _verticalSlot = getSlot(_verticalSlotName);
        }
        _horizontalAxisLabel = readString(HORIZONTAL_AXIS_LABEL, _horizontalSlotName);
        _verticalAxisLabel = readString(VERTICAL_AXIS_LABEL, _verticalSlotName);
        _autocreateWhenEditing = readBoolean(AUTOCREATE_WHEN_EDITING, true);
        _hierarchicalPolicyWhenClassesAreDomainIndices =
            readString(HIERARCHICAL_POLICY_WHEN_CLASSES_ARE_DOMAIN_INDICES, HIERARCHICAL_ONLY_INCLUDE_LEAVES);
        _policyWhenInstancesAreDomainIndices = readString(POLICY_WHEN_INSTANCES_ARE_DOMAIN_INDICES, USE_ALL_INSTANCES);
        _includeAbstractClassesInTermList = readBoolean(INCLUDE_ABSTRACT_CLASSES_IN_TERM_LIST, true);
        super.restore();
    }

    public void save() {
        _properties.setString(HORIZONTAL_SLOT, _horizontalSlot.getName());
        _properties.setString(VERTICAL_SLOT, _verticalSlot.getName());
        _properties.setBoolean(AUTOCREATE_WHEN_EDITING, _autocreateWhenEditing);
        _properties.setString(POLICY_WHEN_INSTANCES_ARE_DOMAIN_INDICES, _policyWhenInstancesAreDomainIndices);
        _properties.setString(
            HIERARCHICAL_POLICY_WHEN_CLASSES_ARE_DOMAIN_INDICES,
            _hierarchicalPolicyWhenClassesAreDomainIndices);
        _properties.setBoolean(INCLUDE_ABSTRACT_CLASSES_IN_TERM_LIST, _includeAbstractClassesInTermList);
        _properties.setString(HORIZONTAL_AXIS_LABEL, _horizontalAxisLabel);
        _properties.setString(VERTICAL_AXIS_LABEL, _verticalAxisLabel);
        super.save();
    }

    public void setAutocreateWhenEditing(Boolean autocreateWhenEditing) {
        _autocreateWhenEditing = autocreateWhenEditing.booleanValue();
    }

    public void setAutocreateWhenEditing(boolean autocreateWhenEditing) {
        _autocreateWhenEditing = autocreateWhenEditing;
    }

    public void setAutomaticallyDisplayFormsForCreatedInstances(Boolean automaticallyDisplayFormsForCreatedInstances) {
        _automaticallyDisplayFormsForCreatedInstances = automaticallyDisplayFormsForCreatedInstances.booleanValue();
    }

    public void setAutomaticallyDisplayFormsForCreatedInstances(boolean automaticallyDisplayFormsForCreatedInstances) {
        _automaticallyDisplayFormsForCreatedInstances = automaticallyDisplayFormsForCreatedInstances;
    }

    private void setAxisLabelsIfNecessary() {
        if ((null == _horizontalAxisLabel) && (null != _horizontalSlot)) {
            _horizontalAxisLabel = _horizontalSlot.getBrowserText();
        }
        if ((null == _verticalAxisLabel) && (null != _verticalSlot)) {
            _verticalAxisLabel = _verticalSlot.getBrowserText();
        }
    }

    public void setHierarchicalPolicyWhenClassesAreDomainIndices(String hierarchicalPolicyWhenClassesAreDomainIndices) {
        _hierarchicalPolicyWhenClassesAreDomainIndices = hierarchicalPolicyWhenClassesAreDomainIndices;
    }

    public void setHorizontalAxisLabel(String horizontalAxisLabel) {
        _horizontalAxisLabel = horizontalAxisLabel;
    }

    public void setHorizontalSlot(Slot horizontalSlot) {
        if ((null != _verticalAxisLabel)
            && (null != _horizontalSlot)
            && ((_horizontalSlot.getBrowserText()).equals(_verticalAxisLabel))) {
            _verticalAxisLabel = null;
        }
        _horizontalSlot = horizontalSlot;
        Collection slots = new ArrayList(_widget.getDomainSlots());
        slots.remove(horizontalSlot);
        if (0 != slots.size() && (_horizontalSlot.equals(_verticalSlot))) {
            setVerticalSlot((Slot) CollectionUtilities.getFirstItem(slots));
        }
        setAxisLabelsIfNecessary();
    }

    public void setIncludeAbstractClassesInTermList(boolean includeAbstractClassesInTermList) {
        _includeAbstractClassesInTermList = includeAbstractClassesInTermList;
    }

    public void setPolicyWhenInstancesAreDomainIndices(String policyWhenInstancesAreDomainIndices) {
        _policyWhenInstancesAreDomainIndices = policyWhenInstancesAreDomainIndices;
    }

    private void setSlotsToDefaultValues() {
        Collection slots = _widget.getDomainSlots();
        if ((null != slots) && (0 != slots.size())) {
            Slot slot = (Slot) CollectionUtilities.getFirstItem(slots);
            setHorizontalSlot(slot);
            setVerticalSlot(slot);
        }
    }

    public void setVerticalAxisLabel(String verticalAxisLabel) {
        _verticalAxisLabel = verticalAxisLabel;
    }

    public void setVerticalSlot(Slot verticalSlot) {
        if ((null != _horizontalAxisLabel)
            && (null != _verticalSlot)
            && ((_verticalSlot.getBrowserText()).equals(_horizontalAxisLabel))) {
            _horizontalAxisLabel = null;
        }
        _verticalSlot = verticalSlot;
        Collection slots = new ArrayList(_widget.getDomainSlots());
        slots.remove(_verticalSlot);
        if (0 != slots.size() && (_verticalSlot.equals(_horizontalSlot))) {
            setHorizontalSlot((Slot) CollectionUtilities.getFirstItem(slots));
        }
        setAxisLabelsIfNecessary();
    }
}
