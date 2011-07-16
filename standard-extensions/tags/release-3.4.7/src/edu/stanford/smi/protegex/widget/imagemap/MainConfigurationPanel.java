package edu.stanford.smi.protegex.widget.imagemap;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class MainConfigurationPanel extends JPanel implements Validatable, Observer {
    private static final long serialVersionUID = 1585325870972140655L;

    private ImageMapState _state;

    private URIField _imageLocation;
    private AnnotatedSlider _lineWidthSlider;
    private ColorWell _selectionColorWell;
    private ColorWell _tooltipColorWell;
    private JCheckBox _showTooltips;
    private JCheckBox _showSelectedRectanglesDuringKA;
    private GenericListener _gcl;
    private FileLocationListener _fcl;

    private boolean _preventEdits;

    private class GenericListener extends FocusAdapter implements ActionListener, ChangeListener {
        public void stateChanged(ChangeEvent e) {
            setStatefromUI();
        }

        public void focusLost(FocusEvent e) {
            setStatefromUI();
        }

        public void actionPerformed(ActionEvent e) {
            setStatefromUI();
        }
    }

    private class FileLocationListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            if (_preventEdits) {
                return;
            }
            setStateImageLocationFromFileField();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setFileField();
                }
            });
        }
    }

    public MainConfigurationPanel(ImageMapWidget widget) {
        super(new GridLayout(6, 1));
        _state = widget.getState();
        _gcl = new GenericListener();
        _fcl = new FileLocationListener();
        buildGUI();
        addActions();
        setUIFromState();
    }

    private void addActions() {
        _imageLocation.addChangeListener(_fcl);
        _showTooltips.addActionListener(_gcl);
        _showSelectedRectanglesDuringKA.addActionListener(_gcl);
        _lineWidthSlider.addChangeListener(_gcl);
    }

    private void buildGUI() {
        _selectionColorWell = createColorChooser("Color for Selected Rectangles");
        _tooltipColorWell = createColorChooser("Color for Tooltips");
        _showTooltips = createCheckBox("Show tooltips");
        _showSelectedRectanglesDuringKA = createCheckBox("Show Rectangles during KA");
        _imageLocation = new URIField("Image Location", null, "", "Where the undelrying image is stored");
        add(_imageLocation);
        _lineWidthSlider = createSlider("Line Thickness");
        _state.addObserver(this);
    }

    private JCheckBox createCheckBox(String name) {
        JCheckBox returnValue = new JCheckBox(name);
        LabeledComponent surroundingBox = new LabeledComponent("", returnValue);
        add(surroundingBox);
        return returnValue;
    }

    private ColorWell createColorChooser(String name) {
        ColorWell returnValue = new ColorWell(Color.white, _gcl);
        LabeledComponent lc = new LabeledComponent(name, returnValue);
        add(lc);
        return returnValue;
    }

    private AnnotatedSlider createSlider(String name) {
        AnnotatedSlider returnValue =
            AnnotatedSlider.getAnnotatedSlider(
                _state.getLineThicknessMinimum(),
                _state.getLineThicknessMaximum(),
                _state.getLineThickness(),
                AnnotatedSlider.LINE_VIEW);
        LabeledComponent surroundingBox = new LabeledComponent(name, returnValue);
        add(surroundingBox);
        return returnValue;
    }

    /*
    private boolean isFile(String location) {
        // hack to handle Ray's API
        if (location.startsWith("http:")) {
            return false;
        }
        if (location.startsWith("ftp:")) {
            return false;
        }
        return true;
    }
    
    private String relativiseLocation(String initialLocation) {
        // hack until Ray gives me a real file API
        if (null == initialLocation) {
            return null;
        }
        if (!isFile(initialLocation)) {
            return initialLocation;
        }
        File file = new File(initialLocation);
        return file.getName();
    }
    */

    private URI relativiseLocation(URI uri) {
        URI projectURI = _state.getOwner().getProject().getProjectURI();
        return URIUtilities.relativize(projectURI, uri);
    }

    public void saveContents() {
        _state.save();
    }

    private void setFileField() {
        if (_preventEdits) {
            return;
        }
        _preventEdits = true;
        // _state.setImageLocation(relativiseLocation(_imageLocation.getAbsoluteURI()));
        _state.setImageLocation(_imageLocation.getRelativeURI());
        _imageLocation.setURI(_state.getImageLocation());
        _preventEdits = false;
    }

    private void setStatefromUI() {
        if (_preventEdits) {
            return;
        }
        _preventEdits = true;
        _state.setSelectionColor(_selectionColorWell.getColor());
        _state.setTooltipColor(_tooltipColorWell.getColor());
        _state.setShowTooltips(_showTooltips.isSelected());
        _state.setShowSelectedRectanglesDuringKA(_showSelectedRectanglesDuringKA.isSelected());
        _state.setImageLocation(relativiseLocation(_imageLocation.getAbsoluteURI()));
        _state.setLineThickness(_lineWidthSlider.getValue());
        _preventEdits = false;
    }

    private void setStateImageLocationFromFileField() {
        if (_preventEdits) {
            return;
        }
        _preventEdits = true;
        // _state.setImageLocation(relativiseLocation(_imageLocation.getAbsoluteURI()));
        _state.setImageLocation(_imageLocation.getRelativeURI());
        _preventEdits = false;
    }

    private void setUIFromState() {
        if (_preventEdits) {
            return;
        }
        _preventEdits = true;
        _selectionColorWell.setColor(_state.getSelectionColor());
        _tooltipColorWell.setColor(_state.getTooltipColor());
        _showTooltips.setSelected(_state.getShowTooltips());
        _showSelectedRectanglesDuringKA.setSelected(_state.getShowSelectedRectanglesDuringKA());
        _imageLocation.setURI(_state.getImageLocation());
        _lineWidthSlider.setValue(_state.getLineThickness());
        _preventEdits = false;
    }

    public void update(Observable o, Object arg) {
        setUIFromState();
    }

    public boolean validateContents() {
        return true;
    }
}
