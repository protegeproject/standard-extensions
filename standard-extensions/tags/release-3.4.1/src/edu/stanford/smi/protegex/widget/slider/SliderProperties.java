package edu.stanford.smi.protegex.widget.slider;

import java.util.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;

/**
 *  This is the "state" for the sliderwidget.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SliderProperties extends Observable {
    private final String displayTicksKey = ":SliderWidget:Display-Ticks";
    private final String tickIncrementKey = ":SliderWidget:Tick-Increment";

    private Boolean _displayTicks;
    private Integer _tickIncrement;
    private PropertyList _properties;
    private Cls _cls;
    private Slot _slot;
    private ListensToClass _classChangeListener;

    private class ListensToClass implements ClsListener {
        public void directInstanceAdded(ClsEvent event) {
        }

        public void directInstanceRemoved(ClsEvent event) {
        }

        public void directSubclassAdded(ClsEvent event) {
        }

        public void directSubclassRemoved(ClsEvent event) {
        }

        public void directSubclassMoved(ClsEvent event) {
        }

        public void directSuperclassAdded(ClsEvent event) {
        }

        public void directSuperclassRemoved(ClsEvent event) {
        }

        public void templateFacetAdded(ClsEvent event) {
        }

        public void templateFacetRemoved(ClsEvent event) {
        }

        public void templateSlotAdded(ClsEvent event) {
        }

        public void templateSlotValueChanged(ClsEvent event) {
        }

        public void directSubclassesReordered(ClsEvent p0) {
        }

        public void templateFacetValueChanged(ClsEvent event) {
            broadcast();
        }

        public void templateSlotRemoved(ClsEvent event) {
            if ((event.getSlot()).equals(_slot)) {
                _cls.removeClsListener(_classChangeListener);
            }
        }
    }

    public SliderProperties(PropertyList properties, Cls cls, Slot slot) {
        _properties = properties;
        _cls = cls;
        _slot = slot;
        _classChangeListener = new ListensToClass();
        _cls.addClsListener(_classChangeListener);
        restore();
    }

    private void broadcast() {
        setChanged();
        notifyObservers();
    }

    public void dispose() {
        _classChangeListener = null;
    }

    public int getMaximum() {
        return ((Integer) _cls.getTemplateSlotMaximumValue(_slot)).intValue();
    }

    public int getMinimum() {
        return ((Integer) _cls.getTemplateSlotMinimumValue(_slot)).intValue();
    }

    // and a whole lot of get/set methods
    public int getTickIncrement() {
        return _tickIncrement.intValue();
    }

    public boolean isDisplayTicks() {
        return _displayTicks.booleanValue();
    }

    public void restore() {
        _displayTicks = _properties.getBoolean(displayTicksKey);
        _tickIncrement = _properties.getInteger(tickIncrementKey);

        // default values
        if (null == _displayTicks) {
            _displayTicks = new Boolean(true);
        }
        if (null == _tickIncrement) {
            _tickIncrement = new Integer(1);
        }
    }

    public void save() {
        _properties.setBoolean(displayTicksKey, _displayTicks);
        _properties.setInteger(tickIncrementKey, _tickIncrement);
        broadcast();
    }

    public void setDisplayTicks(Boolean displayTicks) {
        _displayTicks = displayTicks;
    }

    public void setDisplayTicks(boolean displayTicks) {
        _displayTicks = new Boolean(displayTicks);
    }

    public void setTickIncrement(int tickIncrement) {
        _tickIncrement = new Integer(tickIncrement);
    }

    public void setTickIncrement(Integer tickIncrement) {
        _tickIncrement = tickIncrement;
    }
}
