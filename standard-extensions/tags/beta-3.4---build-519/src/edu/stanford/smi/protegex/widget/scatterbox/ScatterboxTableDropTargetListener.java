package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxTableDropTargetListener extends ScatterboxDNDObject
         implements DropTargetListener {
    private class DropData {
        Point whereInTable;
        Instance whatToDrop;

        public DropData(Point where, Instance what) {
            whereInTable = where;
            whatToDrop = what;
        }
    }

    public ScatterboxTableDropTargetListener(
        ScatterboxWidget widget,
        ScatterboxTable underlyingTable,
        KBQueryUtils queryUtilsObject) {
        super(widget, underlyingTable, queryUtilsObject);
    }

    private DropData convertEventIntoDropData(DropTargetDropEvent dropTargetDropEvent) {
        Transferable transferable = dropTargetDropEvent.getTransferable();
        Point tableLocation = getTableLocationForEvent(dropTargetDropEvent);
        if (null != _underlyingTable.getValueAt(tableLocation.x, tableLocation.y)) {
            return null;
        }
        Instance entry;
        try {
            entry = (Instance) transferable.getTransferData(_flavor);
        } catch (Exception e) {
            return null;
        }
        if ((null == entry) || !entry.hasType(_entryCls)) {
            return null;
        }
        return new DropData(tableLocation, entry);
    }

    public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
        highlightBox(dropTargetDragEvent);
    }

    public void dragExit(DropTargetEvent dropTargetDragEvent) {
        _widget.setIsCurrentlyDragging(false);
        _underlyingTable.invalidate();
        _underlyingTable.repaint();
    }

    public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
        highlightBox(dropTargetDragEvent);
    }

    public void drop(DropTargetDropEvent dropTargetDropEvent) {
        DropData dropData = convertEventIntoDropData(dropTargetDropEvent);
        if (null == dropData) {
            dropTargetDropEvent.rejectDrop();
        } else {
            int dropAction = dropTargetDropEvent.getDropAction();
            if (DnDConstants.ACTION_MOVE == dropAction) {
                _widget.removeEntry(dropData.whatToDrop);
                processDrop(dropData, dropTargetDropEvent);
            }
            if (DnDConstants.ACTION_COPY == dropAction) {
                dropData.whatToDrop =
                    RecursiveCopy.recursivelyCopyInstance(dropData.whatToDrop, Constants.RECURSIVE_COPY_DEPTH_FOR_DND);
                processDrop(dropData, dropTargetDropEvent);
            }
        }
        _widget.setIsCurrentlyDragging(false);
        return;
    }

    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
    }

    protected Point getTableLocationForEvent(DropTargetDragEvent dropTargetDragEvent) {
        Point dragTargetPoint = dropTargetDragEvent.getLocation();
        int column = _underlyingTable.columnAtPoint(dragTargetPoint);
        int row = _underlyingTable.rowAtPoint(dragTargetPoint);
        if ((row < 0) || (column < 0)) {
            return null;
        }
        return new Point(row, column);
    }

    protected Point getTableLocationForEvent(DropTargetDropEvent dropTargetDropEvent) {
        Point dropTargetPoint = dropTargetDropEvent.getLocation();
        int column = _underlyingTable.columnAtPoint(dropTargetPoint);
        int row = _underlyingTable.rowAtPoint(dropTargetPoint);
        if ((row < 0) || (column < 0)) {
            return null;
        }
        return new Point(row, column);
    }

    private void highlightBox(DropTargetDragEvent dropTargetDragEvent) {
        Point dragPoint = getTableLocationForEvent(dropTargetDragEvent);
        _widget.setIsCurrentlyDragging(true);
        _widget.setCurrentlyDraggingOverRow(dragPoint.x);
        _widget.setCurrentlyDraggingOverColumn(dragPoint.y);
        _underlyingTable.invalidate();
        _underlyingTable.repaint();
    }

    private void processDrop(DropData dropData, DropTargetDropEvent dropTargetDropEvent) {
        putEntryAtPoint(dropData.whatToDrop, dropData.whereInTable);
        _widget.addEntry(dropData.whatToDrop);
        _underlyingTable.setRowSelectionInterval(dropData.whereInTable.x, dropData.whereInTable.x);
        _underlyingTable.setColumnSelectionInterval(dropData.whereInTable.y, dropData.whereInTable.y);
        dropTargetDropEvent.acceptDrop(dropTargetDropEvent.getDropAction());
        dropTargetDropEvent.dropComplete(true);
    }
}
