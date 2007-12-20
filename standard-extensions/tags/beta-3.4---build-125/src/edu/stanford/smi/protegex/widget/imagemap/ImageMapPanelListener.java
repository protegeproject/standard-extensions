package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;

/**
 *  Description of the Interface
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public interface ImageMapPanelListener {

    public void logicalClick(Point where);

    public void logicalDrag(Point where);

    public void logicalDragFinished(Point where);

    public void logicalDragStarted(Point where);

    public void logicalPress(Point where);

    public void logicalRelease(Point where);
}
