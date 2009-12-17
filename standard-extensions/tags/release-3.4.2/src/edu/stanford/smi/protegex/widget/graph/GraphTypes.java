package edu.stanford.smi.protegex.widget.graph;

public interface GraphTypes {
    // Node Types
    String DIAMOND = "Diamond";
    String ELLIPSE = "Ellipse";
    String HEXAGON = "Hexagon";
    String INVERTED_TRIANGLE = "Inverted Triangle";
    String OCTAGON = "Octagon";
    String PENTAGON = "Pentagon";
    String RECTANGLE = "Rectangle";
    String ROUNDED_RECTANGLE = "Rounded Rectangle";
    String TRIANGLE = "Triangle";

    // Line Types
    String SOLID = "Solid";
    String DASHED = "Dashed";
    String DOTTED = "Dotted";
    String DASH_DOT = "Dash Dot";
    String DASH_DOT_DOT = "Dash Dot Dot";

    // Arrowhead Types
    String ARROW_ARROWHEAD = "Arrowhead";
    String ARROW_DIAMOND = "Diamond";
    String ARROW_SIMPLE_LINE_DRAWN = "Simple Line Drawn";
    String ARROW_TRIANGLE = "Triangle";

    // Misc.
    String RELATION_SLOT = "slotForRelations";
    String NONE = "< none >";

    int NODE_WIDTH = 60;
    int NODE_HEIGHT = 60;
    int LINE_WIDTH = 1;

    String GRID_CROSSES = "Crosses";
    String GRID_INVISIBLE = "Invisible";
    String GRID_LINES = "Lines";
    String GRID_DOTS = "Dots";

    String GRID_NO_SNAP = "No snap";
    String GRID_SNAP_DURING = "Jump";
    String GRID_SNAP_AFTER = "Afterwards";
}