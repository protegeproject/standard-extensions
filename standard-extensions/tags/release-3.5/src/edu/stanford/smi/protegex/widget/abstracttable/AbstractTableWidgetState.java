package edu.stanford.smi.protegex.widget.abstracttable;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.util.*;

/**
 *  State consists of three things-- A set of visible slot descriptions An
 *  ordering on visible slot descriptions Information on the global editing
 *  model of the widget In addition, we have an extensive query API (so that the
 *  associated TableModel is fairly simple) Adds information to button related
 *  information state (1) is inplace editing supported (2) if so, which cell
 *  editors to use For frame there is a choice of two editors (1) combobox
 *  containing all matching instances (2) protege dialog
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public abstract class AbstractTableWidgetState extends ButtonRelatedWidgetState {

    protected boolean _editInPlace;
    protected boolean _useDialogToSelectInstances;
    protected boolean _useDialogToSelectClasses;
    protected String _dialogTitleForSelectingInstances;
    protected String _dialogTitleForSelectingClasses;
    protected final static String IN_PLACE_EDITING = ":Edit:In:Place";

    protected final static String USE_DIALOG_TO_SELECT_INSTANCES = ":Use:Dialog:To:Select:Instances";
    protected final static String USE_DIALOG_TO_SELECT_CLASSES = ":Use:Dialog:To:Select:Classes";
    protected final static String DIALOG_TITLE_FOR_SELECTING_INSTANCES = ":Dialog:Title:For:Selecting:Instances";
    protected final static String DIALOG_TITLE_FOR_SELECTING_CLASSES = ":Dialog:Title:For:Selecting:Classes";
    protected final static String DEFAULT_DIALOG_TITLE_FOR_SELECTING_INSTANCES = "Select an instance";
    protected final static String DEFAULT_DIALOG_TITLE_FOR_SELECTING_CLASSES = "Select a class";

    public AbstractTableWidgetState(PropertyList properties) {
        super(properties);
    }

    public abstract void dispose();

    public String getDialogTitleForSelectingClasses() {
        return _dialogTitleForSelectingClasses;
    }

    public String getDialogTitleForSelectingInstances() {
        return _dialogTitleForSelectingInstances;
    }

    /*
         * public boolean isDisplayCreateInstanceButton() {
         * return _displayCreateInstanceButton;
         * }
         * public boolean isDisplayAddInstanceButton() {
         * return _displayAddInstanceButton;
         * }
         * public boolean isDisplayViewInstanceButton() {
         * return _displayViewInstanceButton;
         * }
         * public boolean isDisplayRemoveInstanceButton() {
         * return _displayRemoveInstanceButton;
         * }
         */

    public boolean isEditInPlace() {
        return _editInPlace;
    }

    public boolean isUseDialogToSelectClasses() {
        return _useDialogToSelectClasses;
    }

    public boolean isUseDialogToSelectInstances() {
        return _useDialogToSelectInstances;
    }

    public void restore() {
        super.restore();
        _editInPlace = readBoolean(IN_PLACE_EDITING, false);
        _useDialogToSelectInstances = readBoolean(USE_DIALOG_TO_SELECT_INSTANCES, false);
        _useDialogToSelectClasses = readBoolean(USE_DIALOG_TO_SELECT_CLASSES, true);
        _dialogTitleForSelectingInstances =
            readString(DIALOG_TITLE_FOR_SELECTING_INSTANCES, DEFAULT_DIALOG_TITLE_FOR_SELECTING_INSTANCES);
        _dialogTitleForSelectingClasses =
            readString(DIALOG_TITLE_FOR_SELECTING_CLASSES, DEFAULT_DIALOG_TITLE_FOR_SELECTING_CLASSES);
    }

    public void save() {
        _properties.setBoolean(IN_PLACE_EDITING, _editInPlace);
        _properties.setBoolean(USE_DIALOG_TO_SELECT_INSTANCES, _useDialogToSelectInstances);
        _properties.setBoolean(USE_DIALOG_TO_SELECT_CLASSES, _useDialogToSelectClasses);
        _properties.setString(DIALOG_TITLE_FOR_SELECTING_INSTANCES, _dialogTitleForSelectingInstances);
        _properties.setString(DIALOG_TITLE_FOR_SELECTING_CLASSES, _dialogTitleForSelectingClasses);
        super.save();
        return;
    }

    public void setDialogTitleForSelectingClasses(String dialogTitleForSelectingClasses) {
        _dialogTitleForSelectingClasses = dialogTitleForSelectingClasses;
    }

    public void setDialogTitleForSelectingInstances(String dialogTitleForSelectingInstances) {
        _dialogTitleForSelectingInstances = dialogTitleForSelectingInstances;
    }

    public void setDisplayAddInstanceButton(Boolean displayAddInstanceButton) {
        _displayAddInstanceButton = displayAddInstanceButton.booleanValue();
    }

    public void setDisplayAddInstanceButton(boolean displayAddInstanceButton) {
        _displayAddInstanceButton = displayAddInstanceButton;
    }

    public void setDisplayCreateInstanceButton(Boolean displayCreateInstanceButton) {
        _displayCreateInstanceButton = displayCreateInstanceButton.booleanValue();
    }

    public void setDisplayCreateInstanceButton(boolean displayCreateInstanceButton) {
        _displayCreateInstanceButton = displayCreateInstanceButton;
    }

    public void setDisplayRemoveInstanceButton(Boolean displayRemoveInstanceButton) {
        _displayRemoveInstanceButton = displayRemoveInstanceButton.booleanValue();
    }

    public void setDisplayRemoveInstanceButton(boolean displayRemoveInstanceButton) {
        _displayRemoveInstanceButton = displayRemoveInstanceButton;
    }

    public void setDisplayViewInstanceButton(Boolean displayViewInstanceButton) {
        _displayViewInstanceButton = displayViewInstanceButton.booleanValue();
    }

    public void setDisplayViewInstanceButton(boolean displayViewInstanceButton) {
        _displayViewInstanceButton = displayViewInstanceButton;
    }

    public void setEditInPlace(Boolean editInPlace) {
        _editInPlace = editInPlace.booleanValue();
    }

    public void setEditInPlace(boolean editInPlace) {
        _editInPlace = editInPlace;
    }

    public void setUseDialogToSelectClasses(Boolean useDialogToSelectClasses) {
        _useDialogToSelectClasses = useDialogToSelectClasses.booleanValue();
    }

    public void setUseDialogToSelectClasses(boolean useDialogToSelectClasses) {
        _useDialogToSelectClasses = useDialogToSelectClasses;
    }

    public void setUseDialogToSelectInstances(Boolean useDialogToSelectInstances) {
        _useDialogToSelectInstances = useDialogToSelectInstances.booleanValue();
    }

    public void setUseDialogToSelectInstances(boolean useDialogToSelectInstances) {
        _useDialogToSelectInstances = useDialogToSelectInstances;
    }
}
