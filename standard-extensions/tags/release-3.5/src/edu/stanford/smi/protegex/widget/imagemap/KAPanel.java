package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Wraps an ImageMapPanel and does stuff. Mainly by listening.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class KAPanel extends JPanel {
    private static final long serialVersionUID = -7458973299124364416L;
    private ImageMapPanel _imageMapPanel;
    private ImageMapWidget _widget;
    private ImageMapState _state;
    private JComboBox _activeSymbolChooser;
    private static String NULL_STRING = "<No Value>";

    private class KAPanelListener extends ImageMapPanelAdapter {
        public void logicalPress(Point where) {
            _widget.userClicked(where);
        }
    }

    public KAPanel(ImageMapWidget widget) {
        super(new BorderLayout());
        _widget = widget;
        _state = _widget.getState();
        createImageMap();
        createComboBox();
    }

    public void clearSelection() {
        _imageMapPanel.removeAllRectangles();
        _activeSymbolChooser.setSelectedItem(NULL_STRING);
    }

    private void createComboBox() {
        ArrayList widgetSymbols = new ArrayList(_widget.getSymbols());
        widgetSymbols.add(NULL_STRING);
        _activeSymbolChooser = new JComboBox(new AlphabeticalComboBoxModel(widgetSymbols));
        _activeSymbolChooser.setEditable(false);
        LabeledComponent comboBoxWrapper = new LabeledComponent("Currently Selected Symbol", _activeSymbolChooser);
        _activeSymbolChooser.setEnabled(false);
        add(comboBoxWrapper, BorderLayout.SOUTH);
    }

    private void createImageMap() {
        _imageMapPanel = new ImageMapPanel(_state);
        LabeledComponent imageWrapper = new LabeledComponent(_widget.getLabel(), new JScrollPane(_imageMapPanel));
        _imageMapPanel.addImageMapPanelListener(new KAPanelListener());
        add(imageWrapper, BorderLayout.CENTER);
    }

    public void display(Rectangle rect, Color color, String symbol) {
        displayRect(rect, color);
        displaySymbol(symbol);
    }

    public void displayRect(Rectangle rect, Color color) {
        _imageMapPanel.removeAllRectangles();
        if (rect != null) {
            _imageMapPanel.addRectangle(rect, color);
        }
    }

    public void displaySymbol(String symbol) {
        if (symbol != null) {
            _activeSymbolChooser.setSelectedItem(symbol);
        } else {
            _activeSymbolChooser.setSelectedItem(NULL_STRING);
        }
    }
}
