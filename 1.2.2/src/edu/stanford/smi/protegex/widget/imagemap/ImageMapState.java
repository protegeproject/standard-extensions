package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;
import java.net.*;
import java.util.*;

import edu.stanford.smi.protege.util.*;

/**
 *  All the configurable state for an ImageMapToSymbolWidget is stored here. To
 *  make a domain dependent image map, you simply need to subclass
 *  ImageMapToSymbolWidget and override the default constructor to use a
 *  different state object. Note that this object will change when edu.stanford.smi.protege.util.FileUtilies
 *  evolves. In particular, we should be handling the image location in a more
 *  elegant fashion.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ImageMapState extends Observable {
    private Rectangle _logicalCoordinateSystem;
    private Image _associatedImage;
    private ImageMapWidget _owner;
    private PropertyList _properties;

    // state that is stored
    private URI _imageLocation;
    private Color _selectionColor;
    private Color _tooltipColor;
    private boolean _showTooltips;
    private boolean _showSelectedRectanglesDuringKA;
    private BiMap _symbolsToRectangles;
    private int _lineThickness;

    // and the keys for storage
    private final String IMAGE_LOCATION = ":IMAGE-LOCATION";
    private final String SELECTION_COLOR = ":SELECTION-COLOR";
    private final String TOOLTIP_COLOR = ":TOOLTIP-COLOR";
    private final String SHOW_TOOLTIPS = ":SHOW-TOOLTIPS";
    private final String SHOW_SELECTED_RECTANGLES = ":SHOW-SELECTED-RECTANGLES";
    private final String RECTANGLE_PREFIX = ":RECTANGLE-FOR-SYMBOL-";
    private final String LINE_THICKNESS = ":LINE-THICKNESS";

    private final Color DEFAULT_SELECTION_COLOR = Color.red;
    private final Color DEFAULT_TOOLTIP_COLOR = Color.green;
    private final int DEFAULT_LINE_THICKNESS = 5;
    private final boolean DEFAULT_SHOW_TOOLTIPS = true;
    private final boolean DEFAULT_SHOW_RECTANGLES_IN_KA = true;
    private final int MINIMUM_LINE_THICKNESS = 1;
    private final int MAXIMUM_LINE_THICKNESS = 10;

    public ImageMapState(ImageMapWidget owner) {
        _owner = owner;
        _logicalCoordinateSystem = new Rectangle(0, 0, 1000, 1000);
        _properties = _owner.getPropertyList();
        _symbolsToRectangles = new BiMap();
        restore();
    }

    private void broadcast() {
        setChanged();
        notifyObservers();
    }

    public void dispose() {

    }

    public Collection getAllRectangles() {
        return _symbolsToRectangles.values();
    }

    public Image getAssociatedImage() {
        return _associatedImage;
    }

    private Color getColor(String colorKey) {
        String colorString = _properties.getString(colorKey);
        if (null == colorString) {
            return null;
        }
        int rgb = Integer.parseInt(colorString);
        return new Color(rgb);
    }

    public URI getImageLocation() {
        return _imageLocation;
    }

    public int getLineThickness() {
        return _lineThickness;
    }

    public int getLineThicknessMaximum() {
        return MAXIMUM_LINE_THICKNESS;
    }

    public int getLineThicknessMinimum() {
        return MINIMUM_LINE_THICKNESS;
    }

    public Rectangle getLogicalCoordinateSystem() {
        return _logicalCoordinateSystem;
    }

    public ImageMapWidget getOwner() {
        return _owner;
    }

    public Rectangle getRectangleForSymbol(String symbol) {
        return (Rectangle) _symbolsToRectangles.get(symbol);
    }

    public Color getSelectionColor() {
        return _selectionColor;
    }

    public boolean getShowSelectedRectanglesDuringKA() {
        return _showSelectedRectanglesDuringKA;
    }

    public boolean getShowTooltips() {
        return _showTooltips;
    }

    // and lots of get/set/ methods
    public String getSymbolForRectangle(Rectangle rect) {
        Collection values = _symbolsToRectangles.getKeysForValue(rect);
        if (1 == values.size()) {
            return (String) edu.stanford.smi.protege.util.CollectionUtilities.getSoleItem(values);
        }
        return null;
    }

    public Color getTooltipColor() {
        return _tooltipColor;
    }

    private void loadImage() {
        if (null == _imageLocation) {
            return;
        }
        try {
            URI uri = _owner.getProject().getProjectURI().resolve(_imageLocation);
            _associatedImage = Toolkit.getDefaultToolkit().getImage(uri.toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restore() {
        // we also set defaults in here
        setImageLocation(_properties.getString(IMAGE_LOCATION));
        _selectionColor = getColor(SELECTION_COLOR);
        _tooltipColor = getColor(TOOLTIP_COLOR);
        if (null == _selectionColor) {
            _selectionColor = DEFAULT_SELECTION_COLOR;
        }
        if (null == _tooltipColor) {
            _tooltipColor = DEFAULT_TOOLTIP_COLOR;
        }

        Boolean booleanObject = _properties.getBoolean(SHOW_TOOLTIPS);
        if (null != booleanObject) {
            _showTooltips = booleanObject.booleanValue();
        } else {
            _showTooltips = DEFAULT_SHOW_TOOLTIPS;
        }

        booleanObject = _properties.getBoolean(SHOW_SELECTED_RECTANGLES);
        if (null != booleanObject) {
            _showSelectedRectanglesDuringKA = booleanObject.booleanValue();
        } else {
            _showSelectedRectanglesDuringKA = DEFAULT_SHOW_RECTANGLES_IN_KA;
        }

        Integer lineThickness = _properties.getInteger(LINE_THICKNESS);
        if (null != lineThickness) {
            _lineThickness = lineThickness.intValue();
        } else {
            _lineThickness = DEFAULT_LINE_THICKNESS;
        }
        restoreRectangles();
        return;
    }

    private void restoreRectangles() {
        Collection possibleSymbols = _owner.getSymbols();
        Iterator i = possibleSymbols.iterator();
        while (i.hasNext()) {
            String symbol = (String) i.next();
            String recallString = RECTANGLE_PREFIX + symbol;
            Rectangle r = _properties.getRectangle(recallString);
            if (null != r) {
                _symbolsToRectangles.put(symbol, r);
            }
        }
        return;
    }

    public void save() {
        // saves to the property list
        _properties.setString(IMAGE_LOCATION, _imageLocation.toString());
        _properties.setString(SELECTION_COLOR, String.valueOf(_selectionColor.getRGB()));
        _properties.setString(TOOLTIP_COLOR, String.valueOf(_tooltipColor.getRGB()));
        _properties.setBoolean(SHOW_TOOLTIPS, _showTooltips);
        _properties.setBoolean(SHOW_SELECTED_RECTANGLES, _showSelectedRectanglesDuringKA);
        _properties.setInteger(LINE_THICKNESS, _lineThickness);
        saveRectangles();
    }

    // and some private helper methods
    private void saveRectangles() {
        Collection possibleSymbols = _owner.getSymbols();
        Iterator i = possibleSymbols.iterator();
        while (i.hasNext()) {
            String symbol = (String) i.next();
            Rectangle r = (Rectangle) _symbolsToRectangles.get(symbol);
            if (null != r) {
                String recallString = RECTANGLE_PREFIX + symbol;
                _properties.setRectangle(recallString, r);
            }
        }
        return;
    }

    private void setImageLocation(String locationString) {
        setImageLocation(URIUtilities.createURI(locationString));
    }

    public void setImageLocation(URI imageLocation) {
        _imageLocation = imageLocation;
        if (null == _imageLocation) {
            _associatedImage = null;
        } else {
            loadImage();
        }
    }

    public void setLineThickness(int lineThickness) {
        _lineThickness = lineThickness;
        broadcast();
    }

    public void setLogicalCoordinateSystem(Rectangle logicalCoordinateSystem) {
        _logicalCoordinateSystem = logicalCoordinateSystem;
        broadcast();
    }

    public void setOwner(ImageMapWidget owner) {
        _owner = owner;
        broadcast();
    }

    public void setRectangleForSymbol(Rectangle rectangle, String symbol) {
        _symbolsToRectangles.put(symbol, rectangle);
        broadcast();
        return;
    }

    public void setSelectionColor(Color selectionColor) {
        _selectionColor = selectionColor;
        broadcast();
    }

    public void setShowSelectedRectanglesDuringKA(boolean showSelectedRectanglesDuringKA) {
        _showSelectedRectanglesDuringKA = showSelectedRectanglesDuringKA;
        broadcast();
    }

    public void setShowTooltips(boolean showTooltips) {
        _showTooltips = showTooltips;
        broadcast();
    }

    public void setSymbolForRectangle(String symbol, Rectangle rectangle) {
        setRectangleForSymbol(rectangle, symbol);
    }

    public void setTooltipColor(Color tooltipColor) {
        _tooltipColor = tooltipColor;
        broadcast();
    }
}
