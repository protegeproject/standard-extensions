package edu.stanford.smi.protegex.widget.graph;

import com.nwoods.jgo.JGoGridView;
import com.nwoods.jgo.JGoView;
import edu.stanford.smi.protege.util.PropertyList;

public class ViewProperties extends GraphProperties {
    private String gridStyle;
    private String snapOnMove;
    private boolean wrapping;

    public ViewProperties(String clsName, PropertyList propertyList) {
        super(clsName, propertyList);
        initialize();
    }

    private void initialize() {
        String prefix = getPrefix();
        gridStyle = getStringProperty(prefix + "gridStyle", "Crosses");
        snapOnMove = getStringProperty(prefix + "snapOnMove", "Afterwards");
        wrapping = getBooleanProperty(prefix + "wrapping", new Boolean(false));
    }

    public void save() {
        String prefix = getPrefix();
        PropertyList propertyList = getPropertyList();
        propertyList.setString(prefix + "gridStyle", this.gridStyle);
        propertyList.setString(prefix + "snapOnMove", this.snapOnMove);
        propertyList.setBoolean(prefix + "wrapping", wrapping);
    }

    public String getGridStyle() {
        return gridStyle;
    }

    public int getGridStyleInt() {
        // Default to no grid lines.
        int retval = JGoGridView.GridInvisible;

        if (gridStyle != null) {
            if (gridStyle.equals(GraphTypes.GRID_CROSSES)) {
                retval = JGoGridView.GridCross;
            } else if (gridStyle.equals(GraphTypes.GRID_DOTS)) {
                retval = JGoGridView.GridDot;
            } else if (gridStyle.equals(GraphTypes.GRID_INVISIBLE)) {
                retval = JGoGridView.GridInvisible;
            } else if (gridStyle.equals(GraphTypes.GRID_LINES)) {
                retval = JGoGridView.GridLine;
            }
        }

        return retval;
    }

    public void setGridStyle(String gridStyle) {
        this.gridStyle = gridStyle;
    }

    public String getSnapOnMove() {
        return snapOnMove;
    }

    public int getSnapOnMoveInt() {
        // Default to snap aftewards.
        int retval = JGoGridView.SnapAfter;

        if (snapOnMove != null) {
            if (snapOnMove.equals(GraphTypes.GRID_NO_SNAP)) {
                retval = JGoView.NoSnap;
            } else if (snapOnMove.equals(GraphTypes.GRID_SNAP_DURING)) {
                retval = JGoView.SnapJump;
            } else if (snapOnMove.equals(GraphTypes.GRID_SNAP_AFTER)) {
                retval = JGoView.SnapAfter;
            }
        }

        return retval;
    }

    public void setSnapOnMove(String snapOnMove) {
        this.snapOnMove = snapOnMove;
    }

    public boolean hasTextWrap() {
        return wrapping;
    }

    public void setTextWrap(boolean wrapping) {
        this.wrapping = wrapping;
    }
}