package edu.stanford.smi.protegex.widget.graph;

// java
import java.awt.Color;

// stanford
import edu.stanford.smi.protege.util.PropertyList;

public class NodeProperties extends GraphObjectProperties {
    private String shape;
    private String connectorSlot;
    private String lineType;
    private String arrowheadType;

    private Color shapeColor;
    private Color textColor;

    private boolean bold;
    private boolean italic;
    private boolean displayText;

    public NodeProperties(String clsName, PropertyList propertyList) {
        super(clsName, propertyList);
        initialize();
    }

    private void initialize() {
        int red, green, blue;
        String prefix = getPrefix();

        // Get the node's shape.  Default to Ellipse if nothing is specified.
        shape = getStringProperty(prefix + "shape", "Ellipse");

        // Get the text properties used for the node's label.
        bold = getBooleanProperty(prefix + "bold", new Boolean(false));
        italic = getBooleanProperty(prefix + "italic", new Boolean(false));
        red = getIntProperty(prefix + "textColorRed", new Integer(0));
        green = getIntProperty(prefix + "textColorGreen", new Integer(0));
        blue = getIntProperty(prefix + "textColorBlue", new Integer(0));
        textColor = new Color(red, green, blue);

        // Get the node's color.  Default to "SteelBlue".
        red = getIntProperty(prefix + "shapeColorRed", new Integer(70));
        green = getIntProperty(prefix + "shapeColorGreen", new Integer(130));
        blue = getIntProperty(prefix + "shapeColorBlue", new Integer(180));
        shapeColor = new Color(red, green, blue);

        // TODO: Figure out what the default should be here.
        connectorSlot = getStringProperty(prefix + "connectorSlot", null);
        lineType = getStringProperty(prefix + "lineType", "Solid");
        arrowheadType = getStringProperty(prefix + "arrowheadType", "Arrowhead");
        displayText = getBooleanProperty(prefix + "displayText", new Boolean(true));
    }

    public void save() {
        String prefix = getPrefix();
        PropertyList propertyList = getPropertyList();

        propertyList.setString(prefix + "shape", this.shape);

        propertyList.setInteger(prefix + "shapeColorRed", this.shapeColor.getRed());
        propertyList.setInteger(prefix + "shapeColorGreen", this.shapeColor.getGreen());
        propertyList.setInteger(prefix + "shapeColorBlue", this.shapeColor.getBlue());

        propertyList.setInteger(prefix + "textColorRed", this.textColor.getRed());
        propertyList.setInteger(prefix + "textColorGreen", this.textColor.getGreen());
        propertyList.setInteger(prefix + "textColorBlue", this.textColor.getBlue());

        propertyList.setBoolean(prefix + "bold", this.bold);
        propertyList.setBoolean(prefix + "italic", this.italic);

        propertyList.setString(prefix + "connectorSlot", this.connectorSlot);
        propertyList.setString(prefix + "lineType", this.lineType);
        propertyList.setString(prefix + "arrowheadType", this.arrowheadType);
        propertyList.setBoolean(prefix + "displayText", this.displayText);
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public Color getShapeColor() {
        return shapeColor;
    }

    public void setShapeColor(Color shapeColor) {
        this.shapeColor = shapeColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
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

    public String getConnectorSlot() {
        String retval;

        if (connectorSlot == null) {
            retval = null;
        } else if (connectorSlot.equals(GraphTypes.NONE)) {
            retval = null;
        } else { retval = connectorSlot; }

        return retval;
    }

    public void setConnectorSlot(String connectorSlot) {
        this.connectorSlot = connectorSlot;
    }

    public boolean isTextDisplayed() {
        return this.displayText;
    }

    public void setDisplayText(boolean displayText) {
        this.displayText = displayText;
    }
}