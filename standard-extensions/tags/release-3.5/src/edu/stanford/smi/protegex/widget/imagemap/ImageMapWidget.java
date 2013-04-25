package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ImageMapWidget extends AbstractSlotWidget implements Observer {

    private static final long serialVersionUID = -5993694705929138540L;
    // actual instance code begins here
    private ImageMapState _state;
    private String _currentValue;
    private KAPanel _kapanel;

    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        return new ImageMapWidgetConfigurationPanel(this);
    }

    private void display(Rectangle rect) {
        if (null == rect) {
            _currentValue = null;
            _kapanel.clearSelection();
        } else {
            display(rect, _state.getSymbolForRectangle(rect));
        }
        return;
    }

    private void display(Rectangle rect, String symbol) {
        if ((rect != null) && (_state.getShowSelectedRectanglesDuringKA())) {
            _kapanel.displayRect(rect, _state.getSelectionColor());
        }
        _currentValue = symbol;
        _kapanel.displaySymbol(symbol);
    }

    private void display(String symbol) {
        display(_state.getRectangleForSymbol(symbol), symbol);
    }

    public void dispose() {
        super.dispose();
        _state.dispose();
    }

    private Rectangle getRectangleForClick(Point where) {
        Iterator i = (_state.getAllRectangles()).iterator();
        while (i.hasNext()) {
            Rectangle rect = (Rectangle) i.next();
            if (rect.contains(where)) {
                return rect;
            }
        }
        return null;
    }

    public ImageMapState getState() {
        return _state;
    }

    public Collection getSymbols() {
        return getCls().getTemplateSlotAllowedValues(getSlot());
    }

    public Collection getValues() {
        return CollectionUtilities.createCollection(_currentValue);
    }

    public void initialize() {
        _state = new ImageMapState(this);
        _kapanel = new KAPanel(this);
        add(_kapanel);
        setPreferredRows(3);
        setPreferredColumns(5);
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        if ((null == cls) || (null == slot)) {
            return false;
        }
        if (cls.getTemplateSlotValueType(slot) != ValueType.SYMBOL) {
            return false;
        }
        // is the slot single valued
        if (cls.getTemplateSlotAllowsMultipleValues(slot)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        edu.stanford.smi.protege.Application.main(args);
    }

    public void setValues(java.util.Collection values) {
        if ((null != values) && (0 != values.size())) {
            _currentValue = (String) CollectionUtilities.getSoleItem(values);
            display(_currentValue);
        } else {
            _currentValue = null;
            _kapanel.clearSelection();
        }
    }

    public void update(Observable observable, Object argument) {

    }

    public void userClicked(Point where) {
        Rectangle rect = getRectangleForClick(where);
        display(rect);
        valueChanged();
        return;
    }
}
