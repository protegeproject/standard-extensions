package edu.stanford.smi.protegex.widget.graph;

// java
import java.awt.Color;

// stanford
import edu.stanford.smi.protege.util.PropertyList;

public class RelationProperties extends GraphObjectProperties {
    private String lineType;
    private String arrowheadType;

    private Color lineColor;

    private boolean displayText;

    public RelationProperties(String clsName, PropertyList propertyList) {
        super(clsName, propertyList);
        initialize();
    }

    private void initialize() {
        int red, green, blue;
        String prefix = getPrefix();

        // Get the line's color.  Default to black.
        red = getIntProperty(prefix + RelationPropertyNames.LINE_RGB_RED, new Integer(0));
        green = getIntProperty(prefix + RelationPropertyNames.LINE_RGB_GREEN, new Integer(0));
        blue = getIntProperty(prefix + RelationPropertyNames.LINE_RGB_BLUE, new Integer(0));
        lineColor = new Color(red, green, blue);

        lineType = getStringProperty(prefix + RelationPropertyNames.LINE_TYPE, "Solid");
        arrowheadType = getStringProperty(prefix + RelationPropertyNames.ARROWHEAD_TYPE, "Arrowhead");
        displayText = getBooleanProperty(prefix + RelationPropertyNames.LINE_DISPLAY_TEXT, new Boolean(true));
    }

    public void save() {
        String prefix = getPrefix();
        PropertyList propertyList = getPropertyList();

        propertyList.setInteger(prefix + RelationPropertyNames.LINE_RGB_RED, this.lineColor.getRed());
        propertyList.setInteger(prefix + RelationPropertyNames.LINE_RGB_GREEN, this.lineColor.getGreen());
        propertyList.setInteger(prefix + RelationPropertyNames.LINE_RGB_BLUE, this.lineColor.getBlue());
        propertyList.setString(prefix + RelationPropertyNames.LINE_TYPE, this.lineType);
        propertyList.setString(prefix + RelationPropertyNames.ARROWHEAD_TYPE, this.arrowheadType);
        propertyList.setBoolean(prefix + RelationPropertyNames.LINE_DISPLAY_TEXT, this.displayText);
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getArrowheadType() {
        return arrowheadType;
    }

    public void setArrowheadType(String arrowheadType) {
        this.arrowheadType = arrowheadType;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public boolean isTextDisplayed() {
        return displayText;
    }

    public void setDisplayText(boolean displayText) {
        this.displayText = displayText;
    }
}
