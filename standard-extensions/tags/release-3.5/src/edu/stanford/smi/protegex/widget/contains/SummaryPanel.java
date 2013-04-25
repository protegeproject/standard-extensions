package edu.stanford.smi.protegex.widget.contains;

import java.awt.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Number of slot values in a panel
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SummaryPanel extends LabeledComponent {
    private static final long serialVersionUID = -8822928563474441108L;
    private ContainsWidget _widget;
    private ContainsWidgetState _state;
    private JPanel _interior;
    private NumberOfSlotValuesComponent _numberOfSlotValuesComponent;

    public SummaryPanel(ContainsWidget widget) {
        super("Summary Information", new JPanel(new GridBagLayout()), true);
        _interior = (JPanel) getCenterComponent();
        _widget = widget;
        _state = _widget.getState();
        GridBagConstraints runningConstraint = getGridBagConstraints();
        if (_state.isDisplayTotalNumberOfSubordinateInstances()) {
            addNumberOfSlotValuesComponent(runningConstraint);
        }
        if (_state.isDisplayBrowserKeysOfSubordinateInstances()) {
            addListOfBrowserKeys(runningConstraint);
        }
        // more summary stuff is easy to add via same code pattern
    }

    private void addListOfBrowserKeys(GridBagConstraints constraint) {

    }

    private void addNumberOfSlotValuesComponent(GridBagConstraints constraint) {
        _numberOfSlotValuesComponent = new NumberOfSlotValuesComponent(_widget.getInstance(), _widget.getSlot());
        _interior.add(_numberOfSlotValuesComponent, constraint);
        constraint.gridx++;
    }

    public void dispose() {
        if (null != _numberOfSlotValuesComponent) {
            _numberOfSlotValuesComponent.dispose();
        }
    }

    private GridBagConstraints getGridBagConstraints() {
        GridBagConstraints returnValue = new GridBagConstraints();
        returnValue.gridx = 0;
        returnValue.gridy = 0;
        returnValue.gridheight = 1;
        returnValue.gridwidth = 1;
        returnValue.weightx = 1;
        returnValue.weighty = 0;
        returnValue.fill = GridBagConstraints.BOTH;
        return returnValue;
    }

    public void setInstance(Instance newInstance) {
        if (null != _numberOfSlotValuesComponent) {
            _numberOfSlotValuesComponent.setInstance(newInstance);
        }
    }
}
