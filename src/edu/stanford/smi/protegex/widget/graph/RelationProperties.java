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
        red = getIntProperty(prefix + "lineColorRed", new Integer(0));
        green = getIntProperty(prefix + "lineColorGreen", new Integer(0));
        blue = getIntProperty(prefix + "lineColorBlue", new Integer(0));
        lineColor = new Color(red, green, blue);

        lineType = getStringProperty(prefix + "lineType", "Solid");
        arrowheadType = getStringProperty(prefix + "arrowheadType", "Arrowhead");
        displayText = getBooleanProperty(prefix + "displayText", new Boolean(true));
    }

    public void save() {
        String prefix = getPrefix();
        PropertyList propertyList = getPropertyList();

        propertyList.setInteger(prefix + "lineColorRed", this.lineColor.getRed());
        propertyList.setInteger(prefix + "lineColorGreen", this.lineColor.getGreen());
        propertyList.setInteger(prefix + "lineColorBlue", this.lineColor.getBlue());
        propertyList.setString(prefix + "lineType", this.lineType);
        propertyList.setString(prefix + "arrowheadType", this.arrowheadType);
        propertyList.setBoolean(prefix + "displayText", this.displayText);
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