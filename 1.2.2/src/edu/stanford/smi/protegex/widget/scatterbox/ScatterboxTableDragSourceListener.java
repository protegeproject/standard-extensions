package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.dnd.*;

/*
 * There really is no reason for this object. None at all.
 */

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ScatterboxTableDragSourceListener extends ScatterboxDNDObject
         implements DragSourceListener {

    public ScatterboxTableDragSourceListener(
        ScatterboxWidget widget,
        ScatterboxTable underlyingTable,
        KBQueryUtils queryUtilsObject) {
        super(widget, underlyingTable, queryUtilsObject);
    }

    public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent) {
    }

    public void dragEnter(DragSourceDragEvent dragSourceDropEvent) {
    }

    public void dragExit(DragSourceEvent dragSourceDropEvent) {
    }

    public void dragOver(DragSourceDragEvent dragSourceDropEvent) {
    }

    public void dropActionChanged(DragSourceDragEvent dragSourceDropEvent) {
    }
}
