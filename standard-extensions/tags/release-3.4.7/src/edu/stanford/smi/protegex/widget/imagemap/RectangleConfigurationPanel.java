package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Wraps an ImageMapPanel and does stuff. Mainly by listening.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class RectangleConfigurationPanel extends JPanel implements Observer, Validatable {
    private static final long serialVersionUID = 4984035057541146673L;
    private final static int MAX_PREFERRED_WIDTH = 350;
    private final static int MAX_PREFERRED_HEIGHT = 350;
    private ImageMapPanel _imageMapPanel;
    private ImageMapWidget _widget;
    private ImageMapState _state;
    private JComboBox _activeSymbolChooser;
    private String _chosenSymbol;
    private Rectangle _highlightedRectangle;
    private Color _selectionColor;
    private Color _tooltipColor;

    private class RCFIMPListener extends ImageMapPanelAdapter {
        private Point _basePoint;
        private Point _currentDragPoint;
        private Rectangle rect;

        public void logicalDrag(Point where) {
            _currentDragPoint = where;
            showRectangle();
        }

        public void logicalDragStarted(Point where) {
            _basePoint = new Point(where);
        }

        public void logicalDragFinished(Point where) {
            _currentDragPoint = where;
            persistRectangle();
        }

        private void persistRectangle() {
            rect = createRectangle();
            deleteHighlightedRectangle();
            displayRectangle(rect);
            _state.setRectangleForSymbol(rect, _chosenSymbol);
        }

        private void showRectangle() {
            deleteHighlightedRectangle();
            displayRectangle(createRectangle());
        }

        private Rectangle createRectangle() {
            int width = _currentDragPoint.x - _basePoint.x;
            int height = _currentDragPoint.y - _basePoint.y;
            return new Rectangle(_basePoint.x, _basePoint.y, width, height);
        }
    }

    private class RCFItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            setChosenSymbol((String) e.getItem());
        }
    }

    public RectangleConfigurationPanel(ImageMapWidget widget) {
        super(new BorderLayout());
        _widget = widget;
        _state = _widget.getState();
        createComboBox();
        createImageMap();
        setUIFromState();
        _state.addObserver(this);
    }

    private void createComboBox() {
        _activeSymbolChooser = new JComboBox((_widget.getSymbols()).toArray());
        _activeSymbolChooser.setEditable(false);
        LabeledComponent comboBoxWrapper = new LabeledComponent("Currently Selected Symbol", _activeSymbolChooser);
        _activeSymbolChooser.addItemListener(new RCFItemListener());
        add(comboBoxWrapper, BorderLayout.NORTH);
    }

    private void createImageMap() {
        _imageMapPanel = new ImageMapPanel(_state);
        _imageMapPanel.addImageMapPanelListener(new RCFIMPListener());
        JScrollPane scrollPane = new JScrollPane(_imageMapPanel);
        Dimension scrollPanePreferredSize = new Dimension(_imageMapPanel.getPreferredSize());
        if (scrollPanePreferredSize.width > MAX_PREFERRED_WIDTH) {
            scrollPanePreferredSize.width = MAX_PREFERRED_WIDTH;
        }
        if (scrollPanePreferredSize.height > MAX_PREFERRED_HEIGHT) {
            scrollPanePreferredSize.height = MAX_PREFERRED_HEIGHT;
        }
        scrollPane.setPreferredSize(scrollPanePreferredSize);
        LabeledComponent imageWrapper = new LabeledComponent("Image", scrollPane);
        add(imageWrapper, BorderLayout.CENTER);
    }

    public void deleteHighlightedRectangle() {
        if (null != _highlightedRectangle) {
            _imageMapPanel.removeRectangle(_highlightedRectangle, _selectionColor);
            _highlightedRectangle = null;
        }
    }

    public void displayRectangle(Rectangle r) {
        if (null != _highlightedRectangle) {
            _imageMapPanel.changeRectangleColor(_highlightedRectangle, _selectionColor, _tooltipColor);
        }
        _highlightedRectangle = r;
        if (null != _highlightedRectangle) {
            _imageMapPanel.changeRectangleColor(_highlightedRectangle, _tooltipColor, _selectionColor);
        }
        repaint();
        return;
    }

    private void displayRectangleForSymbol(String symbol) {
        displayRectangle(_state.getRectangleForSymbol(symbol));
    }

    public void saveContents() {
    }

    private void setChosenSymbol(String chosenSymbol) {
        if (chosenSymbol != _chosenSymbol) {
            _chosenSymbol = chosenSymbol;
            displayRectangleForSymbol(chosenSymbol);
        }
        return;
    }

    private void setUIFromState() {
        _selectionColor = _state.getSelectionColor();
        _tooltipColor = _state.getTooltipColor();
        _imageMapPanel.removeAllRectangles();
        Iterator i = (_state.getAllRectangles()).iterator();
        while (i.hasNext()) {
            Rectangle rect = (Rectangle) i.next();
            _imageMapPanel.addRectangle(rect, _tooltipColor);
        }
        setChosenSymbol((String) _activeSymbolChooser.getSelectedItem());
        repaint();
    }

    public void update(Observable o, Object arg) {
        setUIFromState();
    }

    public boolean validateContents() {
        return true;
    }
}
