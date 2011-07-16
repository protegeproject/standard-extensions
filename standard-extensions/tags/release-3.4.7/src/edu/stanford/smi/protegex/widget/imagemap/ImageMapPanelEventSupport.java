package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;
import java.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ImageMapPanelEventSupport {
    protected ArrayList _listeners;

    public ImageMapPanelEventSupport() {
        _listeners = new ArrayList();
    }

    public void addImageMapPanelListener(ImageMapPanelListener newListener) {
        if (!_listeners.contains(newListener)) {
            ArrayList temp = new ArrayList(_listeners);
            temp.add(newListener);
            _listeners = temp;
        }
        return;
    }

    public void callLogicalClick(Point where) {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ((ImageMapPanelListener) i.next()).logicalClick(where);
        }
        return;
    }

    public void callLogicalDrag(Point where) {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ((ImageMapPanelListener) i.next()).logicalDrag(where);
        }
        return;
    }

    public void callLogicalDragFinished(Point where) {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ((ImageMapPanelListener) i.next()).logicalDragFinished(where);
        }
        return;
    }

    public void callLogicalDragStarted(Point where) {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ((ImageMapPanelListener) i.next()).logicalDragStarted(where);
        }
        return;
    }

    public void callLogicalPress(Point where) {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ((ImageMapPanelListener) i.next()).logicalPress(where);
        }
        return;
    }

    public void callLogicalRelease(Point where) {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ((ImageMapPanelListener) i.next()).logicalRelease(where);
        }
        return;
    }

    public void removeImageMapPanelListener(ImageMapPanelListener oldListener) {
        if (_listeners.contains(oldListener)) {
            ArrayList temp = new ArrayList(_listeners);
            temp.remove(oldListener);
            _listeners = temp;
        }
        return;
    }
}
