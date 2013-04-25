package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class OverallAppearanceConfigurationPanel extends AbstractWidgetConfigurationPanel {
    private static final long serialVersionUID = -3617237608221617990L;
    private ScatterboxWidgetState _scatterboxWidgetState;
    private ScatterboxWidget _widget;
    private JComboBox _horizontalComboBox;
    private JComboBox _verticalComboBox;
    private JTextField _horizontalAxisLabel;
    private JTextField _verticalAxisLabel;
    private ListCellRenderer _browserTextRenderer;

    private class HorizontalComboBoxListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            _scatterboxWidgetState.setHorizontalSlot((Slot) _horizontalComboBox.getSelectedItem());

            _verticalComboBox.setSelectedItem(_scatterboxWidgetState.getVerticalSlot());

            _horizontalAxisLabel.setText(_scatterboxWidgetState.getHorizontalAxisLabel());

            _verticalAxisLabel.setText(_scatterboxWidgetState.getVerticalAxisLabel());

        }

    }


    private class VerticalComboBoxListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            _scatterboxWidgetState.setVerticalSlot((Slot) _verticalComboBox.getSelectedItem());

            _horizontalComboBox.setSelectedItem(_scatterboxWidgetState.getHorizontalSlot());

            _horizontalAxisLabel.setText(_scatterboxWidgetState.getHorizontalAxisLabel());

            _verticalAxisLabel.setText(_scatterboxWidgetState.getVerticalAxisLabel());

        }

    }


    private abstract class LabelChanged implements DocumentListener {
        public void changedUpdate(DocumentEvent event) {
            propagateChangeToStateObject();
        }

        public void insertUpdate(DocumentEvent event) {
            propagateChangeToStateObject();
        }

        public void removeUpdate(DocumentEvent event) {
            propagateChangeToStateObject();
        }

        protected abstract void propagateChangeToStateObject();
    }

    private class HorizontalLabelChanged extends LabelChanged {
        protected void propagateChangeToStateObject() {
            _scatterboxWidgetState.setHorizontalAxisLabel(_horizontalAxisLabel.getText());
        }
    }

    private class VerticalLabelChanged extends LabelChanged {
        protected void propagateChangeToStateObject() {
            _scatterboxWidgetState.setVerticalAxisLabel(_verticalAxisLabel.getText());
        }
    }

    public OverallAppearanceConfigurationPanel(ScatterboxWidgetState widgetState) {
        super(widgetState);
    }

    protected void buildGUI() {

        _browserTextRenderer = new FrameRenderer();

        _scatterboxWidgetState = (ScatterboxWidgetState) _state;

        _widget = _scatterboxWidgetState.getWidget();

        buildXComboBox(1);

        buildYComboBox(2);

        buildHorizontalLabelTextField(3);

        buildVerticalalLabelTextField(4);

        addVerticalSpace(5);

    }

    private void buildHorizontalLabelTextField(int yPosition) {

        _horizontalAxisLabel =
            createTextField("Horizontal Axis Label", _scatterboxWidgetState.getHorizontalAxisLabel(), yPosition);

        (_horizontalAxisLabel.getDocument()).addDocumentListener(new HorizontalLabelChanged());

    }

    private void buildVerticalalLabelTextField(int yPosition) {

        _verticalAxisLabel = createTextField("Vertical Axis Label", _scatterboxWidgetState.getVerticalAxisLabel(), yPosition);

        (_verticalAxisLabel.getDocument()).addDocumentListener(new VerticalLabelChanged());

    }

    private void buildXComboBox(int yPosition) {

        _horizontalComboBox =
            createComboBox("Horizontal Axis Slot", _widget.getDomainSlots(), _scatterboxWidgetState.getHorizontalSlot(), yPosition);

        _horizontalComboBox.addItemListener(new HorizontalComboBoxListener());

        _horizontalComboBox.setRenderer(_browserTextRenderer);

    }

    private void buildYComboBox(int yPosition) {

        _verticalComboBox =
            createComboBox("Vertical Axis Slot", _widget.getDomainSlots(), _scatterboxWidgetState.getVerticalSlot(), yPosition);

        _verticalComboBox.addItemListener(new VerticalComboBoxListener());

        _verticalComboBox.setRenderer(_browserTextRenderer);

    }

    private JComboBox createComboBox(String label, Collection values, Slot selectedSlot, int yPosition) {

        JComboBox returnValue = new JComboBox();

        Iterator i = values.iterator();

        while (i.hasNext()) {

            returnValue.addItem(i.next());

        }

        returnValue.setSelectedItem(selectedSlot);

        returnValue.setRenderer(new edu.stanford.smi.protege.util.DefaultRenderer());

        LabeledComponent wrapper = new LabeledComponent(label, returnValue, true);
        add(wrapper, buildComponentGridBagConstraints(yPosition));
        return returnValue;
    }

    private JTextField createTextField(String label, String currentValue, int yPosition) {

        JTextField returnValue = new JTextField(40);

        returnValue.setText(currentValue);

        LabeledComponent wrapper = new LabeledComponent(label, returnValue, true);

        add(wrapper, buildComponentGridBagConstraints(yPosition));
        return returnValue;
    }
}
