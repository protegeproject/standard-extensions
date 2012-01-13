package edu.stanford.smi.protegex.widget.instancetable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Assert;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.widget.abstracttable.AbstractTableWidgetState;

/**
 *  Stores list is slots to use as column and for each of these it stores a
 *  VisibleSlotDescription in a subproperty list Wrapper around property list
 *  Visible slot list property is a property list whose names are slots and
 *  whose values are property lists contained visible slot descriptions The
 *  order of slot names (columns) in the table is maintained in the top level
 *  property list as a set of names VISIBLE_SLOT_INDEX_N State consists of three
 *  things-- A set of visible slot descriptions An ordering on visible slot
 *  descriptions Information on the global editing model of the widget In
 *  addition, we have an extensive query API (so that the associated TableModel
 *  is fairly simple)
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InstanceTableWidgetState extends AbstractTableWidgetState {

    private boolean _autoSelectInsertions;
    private boolean _createFormForNewInstances;
    private boolean _highlightSelectedRow;

    protected ArrayList _availableSlots;
    protected ArrayList _visibleSlots;
    protected HashMap _slotToVisibleSlotDescriptions;
    protected Collection _allowedClses;
    protected KnowledgeBase _kb;

    private final static String VISIBLE_SLOT_LIST = ":Visible:Slot:List";
    private final static String VISIBLE_SLOT_INDEX = ":Visible:Slot:Index";
    private final static String NUMBER_OF_VISIBLE_SLOTS = ":Number:Of:Visible:Slots";
    private final static String AUTO_SELECT_INSERTIONS = ":Automatically:Select:Insertions";

    private final static String CREATE_FORM_FOR_NEW_INSTANCES = ":Create:Form:For:New:Instances";
    private final static String HIGHLIGHT_SELECTED_ROW = ":HighLight:Selected:Row";

    public InstanceTableWidgetState(PropertyList properties, Collection allowedClses, KnowledgeBase kb) {
        super(properties);
        Assert.assertNotNull("allowed classes", allowedClses);
        _allowedClses = new ArrayList(allowedClses);
        _kb = kb;
        restore(); // call this again because when the superclass called it it failed.
    }

    public void addVisibleSlotDescription(VisibleSlotDescription visibleSlotDescription) {
        _availableSlots.remove(visibleSlotDescription.slot);
        _slotToVisibleSlotDescriptions.put(visibleSlotDescription.slot, visibleSlotDescription);
        _visibleSlots.add(visibleSlotDescription.slot);
        broadcast();
    }

    @Override
	public void dispose() {
    }

    protected void getAllSlots() {
        // this hack of a test is necessary because this method is called by the superclass constructor before the variable
        // has been initialized.
        Collection allowedSlots = null;
        if (_allowedClses != null) {
            Iterator i = _allowedClses.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                Collection slots = cls.getTemplateSlots();
                if (allowedSlots == null) {
                    allowedSlots = new HashSet(slots);
                } else {
                    allowedSlots.retainAll(slots);
                }
            }
        }

        _availableSlots.clear();
        if (allowedSlots != null) {
            _availableSlots.addAll(allowedSlots);
        }
    }

    public VisibleSlotDescription getDescriptionForIndex(int index) {
        return getDescriptionForSlot((Slot) _visibleSlots.get(index));
    }

    // find a particular VSD
    public VisibleSlotDescription getDescriptionForSlot(Slot slot) {
        return (VisibleSlotDescription) _slotToVisibleSlotDescriptions.get(slot);
    }

    public Collection getRemainingSlots() {
        return _availableSlots;
    }

    public Collection getSlotVisibilityDescriptions() {
        return _slotToVisibleSlotDescriptions.values();
    }

    // information about the VSDS
    public int getTotalNumberOfVisibleSlots() {
        return _visibleSlots.size();
    }

    public Collection getVisibleSlots() {
        return _visibleSlots;
    }

    public boolean isAutoSelectInsertions() {
        return _autoSelectInsertions;
    }

    public boolean isCreateFormForNewInstances() {
        return _createFormForNewInstances;
    }

    public boolean isHighlightSelectedRow() {
        return _highlightSelectedRow;
    }

    // The Visibility API
    public void makeSlotVisible(Slot slot) {
        addVisibleSlotDescription(new VisibleSlotDescription(slot));
    }

    public void moveSlotToIndex(int fromIndex, int toIndex) {
        Slot slot = (Slot) _visibleSlots.remove(fromIndex);
        if (slot != null) {
            if (toIndex < _visibleSlots.size()) {
                _visibleSlots.add(toIndex, slot);
            } else {
                _visibleSlots.add(slot);
            }
        }
    }

    private void removeBogusVisibleSlotDescriptions() {
        int numberOfVisibleSlots = _visibleSlots.size();
        int index;
        for (index = numberOfVisibleSlots - 1; index >= 0; index--) {
            Slot nextSlot = (Slot) _visibleSlots.get(index);
            if (null == nextSlot) {
                removeSlotVisibility(nextSlot);
            }
        }
    }

    private void removeColumnOrderings() {
        Iterator i = (_properties.getNames()).iterator();
        while (i.hasNext()) {
            String nextName = (String) i.next();
            if (nextName.startsWith(VISIBLE_SLOT_INDEX)) {
                _properties.remove(nextName);
            }
        }
    }

    private void removeSlotVisibility(Slot slot) {
        if (!_availableSlots.contains(slot)) {
            _availableSlots.add(slot);
        }
        _visibleSlots.remove(slot);
        _slotToVisibleSlotDescriptions.remove(slot);
        broadcast();
    }

    public void removeVisibleSlotDescriptionAtIndex(int index) {
        removeSlotVisibility((Slot) _visibleSlots.get(index));
    }

    @Override
	public void restore() {
        // This gets called by the superclass before the local variables are initialized.  It gets called again
        // by the constructor after they are initialized.  This is a horrible hack.
        if (_kb != null) {
            _availableSlots = new ArrayList();
            _visibleSlots = new ArrayList();
            _slotToVisibleSlotDescriptions = new HashMap();
            _autoSelectInsertions = readBoolean(AUTO_SELECT_INSERTIONS, false);
            _createFormForNewInstances = readBoolean(CREATE_FORM_FOR_NEW_INSTANCES, true);
            _highlightSelectedRow = readBoolean(HIGHLIGHT_SELECTED_ROW, true);
            if (isCustomized()) {
                restoreVisibleSlotDescriptions();
                restoreColumnOrderings();
                removeBogusVisibleSlotDescriptions();
            } else {
                getAllSlots();
                ArrayList temporary = new ArrayList();
                temporary.addAll(_availableSlots);
                Iterator i = temporary.iterator();
                while (i.hasNext()) {
                    makeSlotVisible((Slot) i.next());
                }
            }
            super.restore();
        }
    }

    private void restoreColumnOrderings() {
        getAllSlots();
        Integer numberOfVisibleSlots = _properties.getInteger(NUMBER_OF_VISIBLE_SLOTS);
        if (null == numberOfVisibleSlots) {
            return;
        }
        int loopCounter;
        int numberOfVisibleSlotsIntValue = numberOfVisibleSlots.intValue();
        for (loopCounter = 0; loopCounter < numberOfVisibleSlotsIntValue; loopCounter++) {
            String indexString = VISIBLE_SLOT_INDEX + String.valueOf(loopCounter);
            String slotName = _properties.getString(indexString);
            Slot slot = _kb.getSlot(slotName);
            int fromIndex = _visibleSlots.indexOf(slot);
            moveSlotToIndex(fromIndex, loopCounter);
        }
    }

    private void restoreVisibleSlotDescriptions() {
        PropertyList visibleSlotsPropertyList = _properties.getPropertyList(VISIBLE_SLOT_LIST);
        if (null == visibleSlotsPropertyList) {
            return;
        }
        Iterator names = (visibleSlotsPropertyList.getNames()).iterator();
        while (names.hasNext()) {
            String nextName = (String) names.next();
            VisibleSlotDescription nextDescription = new VisibleSlotDescription(_kb, _properties, nextName);
            addVisibleSlotDescription(nextDescription);
        }
        return;
    }

    @Override
	public void save() {
        saveVisibleSlotDescriptions();
        saveColumnOrderings();
        _properties.setBoolean(AUTO_SELECT_INSERTIONS, _autoSelectInsertions);
        _properties.setBoolean(CREATE_FORM_FOR_NEW_INSTANCES, _createFormForNewInstances);
        _properties.setBoolean(HIGHLIGHT_SELECTED_ROW, _highlightSelectedRow);
        super.save();
        return;
    }

    private void saveColumnOrderings() {
        removeColumnOrderings();
        int loopCounter;
        int numberOfVisibleSlots = _visibleSlots.size();
        _properties.setInteger(NUMBER_OF_VISIBLE_SLOTS, numberOfVisibleSlots);
        for (loopCounter = 0; loopCounter < numberOfVisibleSlots; loopCounter++) {
            String indexString = VISIBLE_SLOT_INDEX + String.valueOf(loopCounter);
            _properties.setString(indexString, ((Slot) _visibleSlots.get(loopCounter)).getName());
        }
    }

    // and some helper methods
    private void saveVisibleSlotDescriptions() {
        _properties.remove(VISIBLE_SLOT_LIST);
        PropertyList visiblePropertyList = _properties.getPropertyList(VISIBLE_SLOT_LIST);
        Iterator visibleSlotDescriptions = (_slotToVisibleSlotDescriptions.values()).iterator();
        while (visibleSlotDescriptions.hasNext()) {
            VisibleSlotDescription visibleSlotDescription = (VisibleSlotDescription) visibleSlotDescriptions.next();
            String slotName = (visibleSlotDescription.slot).getName();
            visibleSlotDescription.writeToPropertyList(_properties);
            visiblePropertyList.setString(slotName, slotName);
        }
    }

    public void setAutoSelectInsertions(Boolean autoSelectInsertions) {
        _autoSelectInsertions = autoSelectInsertions.booleanValue();
    }

    public void setAutoSelectInsertions(boolean autoSelectInsertions) {
        _autoSelectInsertions = autoSelectInsertions;
    }

    public void setCreateFormForNewInstances(Boolean createFormForNewInstances) {
        _createFormForNewInstances = createFormForNewInstances.booleanValue();
    }

    public void setCreateFormForNewInstances(boolean createFormForNewInstances) {
        _createFormForNewInstances = createFormForNewInstances;
    }

    public void setHighlightSelectedRow(Boolean highlightSelectedRow) {
        _highlightSelectedRow = highlightSelectedRow.booleanValue();
    }

    public void setHighlightSelectedRow(boolean highlightSelectedRow) {
        _highlightSelectedRow = highlightSelectedRow;
    }
}
