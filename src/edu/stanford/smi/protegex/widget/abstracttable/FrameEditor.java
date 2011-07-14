package edu.stanford.smi.protegex.widget.abstracttable;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class FrameEditor extends ComboBoxEditor {
    private static final long serialVersionUID = -1637769648570262739L;
    protected Frame _value;
    protected Project _project;
    protected JComponent _onScreenComponent;
    protected boolean _hasModelBeenConfigured;
    protected boolean _processingUserSelection;

    protected EditableComboBoxModel _model;
    protected final static String REMOVE = "Remove";
    protected final static String CHOOSE = "Choose ...";
    protected final static String EDIT = "View ...";

    public FrameEditor(JComponent onScreenComponent, Project project) {
        this(onScreenComponent, project, null, null);
    }

    public FrameEditor(JComponent onScreenComponent, Project project, Instance instance, Slot slot) {
        super(instance, slot);
        _project = project;
        _onScreenComponent = onScreenComponent;
        _processingUserSelection = false;
        configureUIFromKB();
        setRenderer(new FrameRenderer_ListCell());
    }

    protected void addUniquely(Collection addToMe, Collection addFromMe) {
        Iterator i = addFromMe.iterator();
        while (i.hasNext()) {
            Object nextObject = (i.next());
            if (!addToMe.contains(nextObject)) {
                addToMe.add(nextObject);
            }
        }
    }

    protected abstract void configureComboBoxModel();

    public void configureUIFromKB() {
        if (_readingFromModel) {
            return;
        }
        _readingFromModel = true;
        _value = (Frame) getValue();
        configureComboBoxModel();
        selectValueInComboBox();
        _readingFromModel = false;
    }

    private void editCurrentValue() {
        if (_value != null) {
            _project.show((Instance) _value);
        }
        selectValueInComboBox();
    }

    protected ComboBoxModel getComboBoxModel() {
        _model = new EditableComboBoxModel();
        return _model;
    }

    protected abstract List getPossibleChoices();

    private void getUserChoice() {
        getValueFromDialog();
        selectValueInComboBox();
        return;
    }

    protected abstract void getValueFromDialog();

    protected void handleSelectionChange() {
        if (_processingSelection) {
            return;
        }
        _processingSelection = true;
        if (!_readingFromModel) {
            processUserSelection();
        }
        _processingSelection = false;
    }

    protected void processUserSelection() {
        Object selection = getSelectedItem();
        if (REMOVE == selection) {
            removeCurrentValue();
            return;
        }
        if (EDIT == selection) {
            editCurrentValue();
            return;
        }
        if (CHOOSE == selection) {
            getUserChoice();
            return;
        }
        if (selection instanceof Frame) {
            _value = (Frame) selection;
            storeValueInKB();
        }
    }

    private void removeCurrentValue() {
        _value = null;
        storeValueInKB();
        configureUIFromKB();
    }

    protected void selectValueInComboBox() {
        if (null == _value) {
            _model.setSelectedItem(NULL_STRING);
        } else {
            _model.setSelectedItem(_value);
        }
    }

    public void storeValueInKB() {
        if ((_instance == null) || (_slot == null)) {
            return;
        }
        if (_readingFromModel) {
            return;
        }
        if ((null != _instance) && (null != _slot)) {
            _instance.setOwnSlotValue(_slot, _value);
        }
    }
}
