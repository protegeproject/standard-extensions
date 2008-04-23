package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxTableDragGestureListener extends ScatterboxDNDObject
         implements DragGestureListener {

    public ScatterboxTableDragGestureListener(
        ScatterboxWidget widget,
        ScatterboxTable underlyingTable,
        KBQueryUtils queryUtilsObject) {
        super(widget, underlyingTable, queryUtilsObject);
    }

    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
        if (!isThisAnAcceptableEvent(dragGestureEvent)) {
            return;
        }
        Instance entry = getEntryForDragGestureEvent(dragGestureEvent);
        if (null != entry) {
            TransferableCell transferableCell = new TransferableCell(entry, _underlyingTable);
            _underlyingTable.stopEditing();
            dragGestureEvent.startDrag(
                DragSource.DefaultMoveDrop,
                transferableCell,
                new ScatterboxTableDragSourceListener(_widget, _underlyingTable, _queryUtilsObject));
        }
        return;
    }

    private Instance getEntryForDragGestureEvent(DragGestureEvent dragGestureEvent) {
        Point dragStartPoint = getTableLocationForEvent(dragGestureEvent);
        if (dragStartPoint == null) {
            return null;
        }
        Instance entry = (Instance) _underlyingTable.getValueAt(dragStartPoint.x, dragStartPoint.y);
        return entry;
    }

    private boolean isThisAnAcceptableEvent(DragGestureEvent dragGestureEvent) {
        Iterator i = dragGestureEvent.iterator();
        if (!i.hasNext()) {
            return false;
        }
        MouseEvent firstEvent = (MouseEvent) i.next();
        if (!i.hasNext()) {
            return false;
        }
        MouseEvent secondEvent = (MouseEvent) i.next();
        if ((firstEvent.getID() == MouseEvent.MOUSE_PRESSED) && (secondEvent.getID() == MouseEvent.MOUSE_DRAGGED)) {
            return true;
        }
        return false;
    }
}
