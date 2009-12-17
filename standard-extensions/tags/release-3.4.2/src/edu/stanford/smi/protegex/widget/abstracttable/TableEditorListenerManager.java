package edu.stanford.smi.protegex.widget.abstracttable;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *  Unused
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class TableEditorListenerManager {
    private ArrayList _listeners;
    private ChangeEvent _event;

    public TableEditorListenerManager(JTable underlyingTable) {
        _listeners = new ArrayList();
        _event = new ChangeEvent(underlyingTable);
    }

    public void addCellEditorListener(CellEditorListener l) {
        ArrayList newList = new ArrayList(_listeners);
        newList.add(l);
        _listeners = newList;
    }

    public void fireEditingCanceled() {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            CellEditorListener nextListener = (CellEditorListener) i.next();
            nextListener.editingCanceled(_event);
        }
    }

    public void fireEditingStopped() {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            CellEditorListener nextListener = (CellEditorListener) i.next();
            nextListener.editingStopped(_event);
        }
    }

    public void removeCellEditorListener(CellEditorListener l) {
        ArrayList newList = new ArrayList(_listeners);
        newList.remove(l);
        _listeners = newList;
    }
}
