package edu.stanford.smi.protegex.widget.graph;

import java.awt.Color;

import edu.stanford.smi.protege.util.PropertyList;

public class NodeProperties extends GraphObjectProperties {
	private boolean bold;
    private boolean displayText;
	private boolean italic;

    private Color shapeColor;
    private Color textColor;

    private String arrowheadType;
    private String browserText;
    private String connectorSlot;
    private String customDisplayName;
    private String lineType;
    private String prefix;
    private String shape;

    public NodeProperties(String clsName, String browserText, PropertyList propertyList) {
        super(clsName, propertyList);
        this.browserText = browserText;
        this.prefix = getPrefix();
        initialize();
    }

    private void initialize() {
        int red, green, blue;

        // See if the user has designated a custom display name for the
        // node (custom display names are used in the palette).
        customDisplayName = getStringProperty(prefix + NodePropertyNames.NODE_DISPLAY_NAME, browserText);

        // Get the node's shape.  Default to Ellipse if nothing is specified.
        shape = getStringProperty(prefix + NodePropertyNames.NODE_SHAPE, "Ellipse");

        // Get the text properties used for the node's label.
        bold = getBooleanProperty(prefix + NodePropertyNames.LABEL_BOLD, new Boolean(false));
        italic = getBooleanProperty(prefix + NodePropertyNames.LABEL_ITALIC, new Boolean(false));
        red = getIntProperty(prefix + NodePropertyNames.LABEL_RGB_RED, new Integer(0));
        green = getIntProperty(prefix + NodePropertyNames.LABEL_RGB_GREEN, new Integer(0));
        blue = getIntProperty(prefix + NodePropertyNames.LABEL_RGB_BLUE, new Integer(0));
        textColor = new Color(red, green, blue);

        // Get the node's color.  Default to "SteelBlue".
        red = getIntProperty(prefix + NodePropertyNames.NODE_RGB_RED, new Integer(70));
        green = getIntProperty(prefix + NodePropertyNames.NODE_RGB_GREEN, new Integer(130));
        blue = getIntProperty(prefix + NodePropertyNames.NODE_RGB_BLUE, new Integer(180));
        shapeColor = new Color(red, green, blue);

        /** @todo Figure out what the default should be for the connector slot */
        connectorSlot = getStringProperty(prefix + NodePropertyNames.CONNECTOR_SLOT, null);
        lineType = getStringProperty(prefix + NodePropertyNames.CONNECTOR_LINE_TYPE, "Solid");
        arrowheadType = getStringProperty(prefix + NodePropertyNames.CONNECTOR_ARROWHEAD_TYPE, "Arrowhead");
        displayText = getBooleanProperty(prefix + NodePropertyNames.CONNECTOR_DISPLAY_TEXT, new Boolean(true));
    }

    public void save() {
        PropertyList propertyList = getPropertyList();

        propertyList.setString(prefix + NodePropertyNames.NODE_DISPLAY_NAME, this.customDisplayName);
        propertyList.setString(prefix + NodePropertyNames.NODE_SHAPE, this.shape);

        propertyList.setInteger(prefix + NodePropertyNames.NODE_RGB_RED, this.shapeColor.getRed());
        propertyList.setInteger(prefix + NodePropertyNames.NODE_RGB_GREEN, this.shapeColor.getGreen());
        propertyList.setInteger(prefix + NodePropertyNames.NODE_RGB_BLUE, this.shapeColor.getBlue());
        propertyList.setInteger(prefix + NodePropertyNames.LABEL_RGB_RED, this.textColor.getRed());
        propertyList.setInteger(prefix + NodePropertyNames.LABEL_RGB_GREEN, this.textColor.getGreen());
        propertyList.setInteger(prefix + NodePropertyNames.LABEL_RGB_BLUE, this.textColor.getBlue());

        propertyList.setBoolean(prefix + NodePropertyNames.LABEL_BOLD, this.bold);
        propertyList.setBoolean(prefix + NodePropertyNames.LABEL_ITALIC, this.italic);

        propertyList.setString(prefix + NodePropertyNames.CONNECTOR_SLOT, this.connectorSlot);
        propertyList.setString(prefix + NodePropertyNames.CONNECTOR_LINE_TYPE, this.lineType);
        propertyList.setString(prefix + NodePropertyNames.CONNECTOR_ARROWHEAD_TYPE, this.arrowheadType);

        propertyList.setBoolean(prefix + NodePropertyNames.CONNECTOR_DISPLAY_TEXT, this.displayText);
    }

    public String getCustomDisplayName() {
    	return customDisplayName;
    }

    public void setCustomDisplayName(String name) {
    	this.customDisplayName = name;
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
