package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxDNDObject {
    protected ScatterboxWidget _widget;
    protected ScatterboxTable _underlyingTable;
    protected DataFlavor _flavor;
    protected Cls _entryCls;
    protected KBQueryUtils _queryUtilsObject;

    public ScatterboxDNDObject(ScatterboxWidget widget, ScatterboxTable underlyingTable, KBQueryUtils queryUtilsObject) {
        _queryUtilsObject = queryUtilsObject;
        _widget = widget;
        try {
            _flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
        } catch (Exception e) {
        }
        _underlyingTable = underlyingTable;
        _entryCls = _queryUtilsObject.getEntryCls(_widget.getCls());
    }

    protected Point getTableLocationForEvent(DragGestureEvent dragGestureEvent) {
        Point dragStartPoint = dragGestureEvent.getDragOrigin();
        int column = _underlyingTable.columnAtPoint(dragStartPoint);
        int row = _underlyingTable.rowAtPoint(dragStartPoint);
        if ((row < 0) || (column < 0)) {
            return null;
        }
        return new Point(row, column);
    }

    protected void putEntryAtPoint(Instance entry, Point tableLocation) {
        Order horizontalOrder = _widget.getHorizontalOrder();
        Order verticalOrder = _widget.getVerticalOrder();
        Instance domainObject = _queryUtilsObject.getDomainObject(entry);
        horizontalOrder.fillObjectWithIndexedValue(domainObject, tableLocation.y);
        verticalOrder.fillObjectWithIndexedValue(domainObject, tableLocation.x);
        _widget.addEntry(entry);
    }
}
