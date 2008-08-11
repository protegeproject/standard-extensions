package edu.stanford.smi.protegex.util;

import edu.stanford.smi.protege.util.*;

/**
 *  Maintains state for action buttons Abstract superclass for lots of state
 *  objects
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ButtonRelatedWidgetState extends AbstractWidgetState {

    protected int _prototypeDepth;

    protected boolean _displayCreateInstanceButton;
    protected boolean _displayAddInstanceButton;
    protected boolean _displayViewInstanceButton;
    protected boolean _displayRemoveInstanceButton;
    protected boolean _displayDeleteInstanceButton;
    protected boolean _displayMoveInstanceButtons;
    protected boolean _displayPrototypeButton;

    protected String _createInstanceButtonTooltip;
    protected String _addInstanceButtonTooltip;
    protected String _viewInstanceButtonTooltip;
    protected String _removeInstanceButtonTooltip;
    protected String _deleteInstanceButtonTooltip;
    protected String _moveInstanceButtonsTooltip;
    protected String _prototypeButtonTooltip;

    protected String _createInstanceDialogTitle;
    protected String _addInstanceDialogTitle;
    // button-related state
    protected final static String PROTOTYPE_DEPTH = ":Prototype:Depth";

    protected final static String DISPLAY_CREATE_INSTANCE_BUTTON = ":Display:Create:Instance:Button";
    protected final static String DISPLAY_ADD_INSTANCE_BUTTON = ":Display:Add:Instance:Button";
    protected final static String DISPLAY_VIEW_INSTANCE_BUTTON = ":Display:View:Instance:Button";
    protected final static String DISPLAY_REMOVE_INSTANCE_BUTTON = ":Display:Remove:Instance:Button";
    protected final static String DISPLAY_DELETE_INSTANCE_BUTTON = ":Display:Delete:Instance:Button";
    protected final static String DISPLAY_PROTOTYPE_BUTTON = ":Display:Prototype:Button";
    protected final static String DISPLAY_MOVE_INSTANCE_BUTTONS = ":Display:Move:Instance:Buttons";

    protected final static String CREATE_INSTANCE_BUTTON_TOOLTIP = ":Create:Instance:Button:Tooltip";
    protected final static String ADD_INSTANCE_BUTTON_TOOLTIP = ":Add:Instance:Button:Tooltip";
    protected final static String VIEW_INSTANCE_BUTTON_TOOLTIP = ":View:Instance:Button:Tooltip";
    protected final static String REMOVE_INSTANCE_BUTTON_TOOLTIP = ":Remove:Instance:Button:Tooltip";
    protected final static String DELETE_INSTANCE_BUTTON_TOOLTIP = ":Delete:Instance:Button:Tooltip";
    protected final static String MOVE_INSTANCE_BUTTONS_TOOLTIP = ":Move:Instance:Buttons:Tooltip";
    protected final static String PROTOTYPE_BUTTON_TOOLTIP = ":Prototype:Button:Tooltip";

    protected final static String CREATE_INSTANCE_DIALOG_TITLE = ":Create:Instance:Dialog:Title";
    protected final static String ADD_INSTANCE_DIALOG_TITLE = ":Add:Instance:Dialog:Title";

    protected final static String CREATE_INSTANCE_BUTTON_TOOLTIP_DEFAULT = "Create a new instance";
    protected final static String ADD_INSTANCE_BUTTON_TOOLTIP_DEFAULT = "Select an already existing instance";
    protected final static String VIEW_INSTANCE_BUTTON_TOOLTIP_DEFAULT = "View the selected instance";
    protected final static String REMOVE_INSTANCE_BUTTON_TOOLTIP_DEFAULT = "Remove the selected instance(s)";
    protected final static String DELETE_INSTANCE_BUTTON_TOOLTIP_DEFAULT = "Delete the selected instance(s) from the knowledge base";
    protected final static String PROTOTYPE_BUTTON_TOOLTIP_DEFAULT = "Create a new instance which is a duplicate of the selected instance";
    protected final static String MOVE_INSTANCE_BUTTONS_TOOLTIP_DEFAULT = "Move the selected instance";

    protected final static String CREATE_INSTANCE_DIALOG_TITLE_DEFAULT = "Create a new instance";
    protected final static String ADD_INSTANCE_DIALOG_TITLE_DEFAULT = "Select an already existing instance";

    public ButtonRelatedWidgetState(PropertyList properties) {
        super(properties);
    }

    public void dispose() {
    }

    public String getAddInstanceButtonTooltip() {
        return _addInstanceButtonTooltip;
    }

    // move buttons
    public String getMoveInstanceTooltip() {
        return _moveInstanceButtonsTooltip;
    }

    public String getMoveInstanceUpTooltip() {
        return _moveInstanceButtonsTooltip + " up";
    }

    public String getMoveInstanceDownTooltip() {
        return _moveInstanceButtonsTooltip + " down";
    }

    // dialog titles
    public String getAddInstanceDialogTitle() {
        return _addInstanceDialogTitle;
    }

    public String getCreateInstanceButtonTooltip() {
        return _createInstanceButtonTooltip;
    }

    public String getCreateInstanceDialogTitle() {
        return _createInstanceDialogTitle;
    }

    public String getDeleteInstanceButtonTooltip() {
        return _deleteInstanceButtonTooltip;
    }

    // tooltips
    public String getPrototypeButtonTooltip() {
        return _prototypeButtonTooltip;
    }

    // prototype depth
    public int getPrototypeDepth() {
        return _prototypeDepth;
    }

    public String getRemoveInstanceButtonTooltip() {
        return _removeInstanceButtonTooltip;
    }

    public String getViewInstanceButtonTooltip() {
        return _viewInstanceButtonTooltip;
    }

    public boolean isDisplayAddInstanceButton() {
        return _displayAddInstanceButton;
    }

    // display buttons
    public boolean isDisplayCreateInstanceButton() {
        return _displayCreateInstanceButton;
    }

    public boolean isDisplayDeleteInstanceButton() {
        return _displayDeleteInstanceButton;
    }

    public boolean isDisplayPrototypeButton() {
        return _displayPrototypeButton;
    }

    public boolean isDisplayRemoveInstanceButton() {
        return _displayRemoveInstanceButton;
    }

    public boolean isDisplayMoveInstanceButtons() {
        return _displayMoveInstanceButtons;
    }

    public boolean isDisplayViewInstanceButton() {
        return _displayViewInstanceButton;
    }

    public void restore() {
        _prototypeDepth = readInt(PROTOTYPE_DEPTH, 1);

        _displayCreateInstanceButton = readBoolean(DISPLAY_CREATE_INSTANCE_BUTTON, true);
        _displayAddInstanceButton = readBoolean(DISPLAY_ADD_INSTANCE_BUTTON, true);
        _displayViewInstanceButton = readBoolean(DISPLAY_VIEW_INSTANCE_BUTTON, true);
        _displayRemoveInstanceButton = readBoolean(DISPLAY_REMOVE_INSTANCE_BUTTON, true);
        _displayDeleteInstanceButton = readBoolean(DISPLAY_DELETE_INSTANCE_BUTTON, false);
        _displayPrototypeButton = readBoolean(DISPLAY_PROTOTYPE_BUTTON, false);
        _displayMoveInstanceButtons = readBoolean(DISPLAY_MOVE_INSTANCE_BUTTONS, false);

        _createInstanceButtonTooltip = readString(CREATE_INSTANCE_BUTTON_TOOLTIP, CREATE_INSTANCE_BUTTON_TOOLTIP_DEFAULT);
        _addInstanceButtonTooltip = readString(ADD_INSTANCE_BUTTON_TOOLTIP, ADD_INSTANCE_BUTTON_TOOLTIP_DEFAULT);
        _viewInstanceButtonTooltip = readString(VIEW_INSTANCE_BUTTON_TOOLTIP, VIEW_INSTANCE_BUTTON_TOOLTIP_DEFAULT);
        _removeInstanceButtonTooltip = readString(REMOVE_INSTANCE_BUTTON_TOOLTIP, REMOVE_INSTANCE_BUTTON_TOOLTIP_DEFAULT);
        _deleteInstanceButtonTooltip = readString(DELETE_INSTANCE_BUTTON_TOOLTIP, DELETE_INSTANCE_BUTTON_TOOLTIP_DEFAULT);
        _prototypeButtonTooltip = readString(PROTOTYPE_BUTTON_TOOLTIP, PROTOTYPE_BUTTON_TOOLTIP_DEFAULT);
        _moveInstanceButtonsTooltip = readString(MOVE_INSTANCE_BUTTONS_TOOLTIP, MOVE_INSTANCE_BUTTONS_TOOLTIP_DEFAULT);

        _createInstanceDialogTitle = readString(CREATE_INSTANCE_DIALOG_TITLE, CREATE_INSTANCE_DIALOG_TITLE_DEFAULT);
        _addInstanceDialogTitle = readString(ADD_INSTANCE_DIALOG_TITLE, ADD_INSTANCE_DIALOG_TITLE_DEFAULT);
    }

    public void save() {
        _properties.setInteger(PROTOTYPE_DEPTH, _prototypeDepth);

        _properties.setBoolean(DISPLAY_CREATE_INSTANCE_BUTTON, _displayCreateInstanceButton);
        _properties.setBoolean(DISPLAY_ADD_INSTANCE_BUTTON, _displayAddInstanceButton);
        _properties.setBoolean(DISPLAY_VIEW_INSTANCE_BUTTON, _displayViewInstanceButton);
        _properties.setBoolean(DISPLAY_REMOVE_INSTANCE_BUTTON, _displayRemoveInstanceButton);
        _properties.setBoolean(DISPLAY_DELETE_INSTANCE_BUTTON, _displayDeleteInstanceButton);
        _properties.setBoolean(DISPLAY_PROTOTYPE_BUTTON, _displayPrototypeButton);
        _properties.setBoolean(DISPLAY_MOVE_INSTANCE_BUTTONS, _displayMoveInstanceButtons);

        _properties.setString(CREATE_INSTANCE_BUTTON_TOOLTIP, _createInstanceButtonTooltip);
        _properties.setString(ADD_INSTANCE_BUTTON_TOOLTIP, _addInstanceButtonTooltip);
        _properties.setString(VIEW_INSTANCE_BUTTON_TOOLTIP, _viewInstanceButtonTooltip);
        _properties.setString(REMOVE_INSTANCE_BUTTON_TOOLTIP, _removeInstanceButtonTooltip);
        _properties.setString(DELETE_INSTANCE_BUTTON_TOOLTIP, _deleteInstanceButtonTooltip);
        _properties.setString(PROTOTYPE_BUTTON_TOOLTIP, _prototypeButtonTooltip);
        _properties.setString(MOVE_INSTANCE_BUTTONS_TOOLTIP, _moveInstanceButtonsTooltip);

        _properties.setString(CREATE_INSTANCE_DIALOG_TITLE, _createInstanceDialogTitle);
        _properties.setString(ADD_INSTANCE_DIALOG_TITLE, _addInstanceDialogTitle);
        return;
    }

    public void setAddInstanceButtonTooltip(String addInstanceButtonTooltip) {
        _addInstanceButtonTooltip = addInstanceButtonTooltip;
    }

    public void setAddInstanceDialogTitle(String addInstanceDialogTitle) {
        _addInstanceDialogTitle = addInstanceDialogTitle;
    }

    public void setCreateInstanceButtonTooltip(String createInstanceButtonTooltip) {
        _createInstanceButtonTooltip = createInstanceButtonTooltip;
    }

    public void setCreateInstanceDialogTitle(String createInstanceDialogTitle) {
        _createInstanceDialogTitle = createInstanceDialogTitle;
    }

    public void setDeleteInstanceButtonTooltip(String removeInstanceButtonTooltip) {
        _deleteInstanceButtonTooltip = removeInstanceButtonTooltip;
    }

    public void setDisplayAddInstanceButton(Boolean displayAddInstanceButton) {
        _displayAddInstanceButton = displayAddInstanceButton.booleanValue();
    }

    public void setDisplayAddInstanceButton(boolean displayAddInstanceButton) {
        _displayAddInstanceButton = displayAddInstanceButton;
    }

    public void setDisplayMoveInstanceButtons(boolean display) {
        _displayMoveInstanceButtons = display;
    }

    public void setDisplayCreateInstanceButton(Boolean displayCreateInstanceButton) {
        _displayCreateInstanceButton = displayCreateInstanceButton.booleanValue();
    }

    public void setDisplayCreateInstanceButton(boolean displayCreateInstanceButton) {
        _displayCreateInstanceButton = displayCreateInstanceButton;
    }

    public void setDisplayDeleteInstanceButton(Boolean displayDeleteInstanceButton) {
        _displayDeleteInstanceButton = displayDeleteInstanceButton.booleanValue();
    }

    public void setDisplayDeleteInstanceButton(boolean displayDeleteInstanceButton) {
        _displayDeleteInstanceButton = displayDeleteInstanceButton;
    }

    public void setDisplayPrototypeButton(Boolean displayPrototypeButton) {
        _displayPrototypeButton = displayPrototypeButton.booleanValue();
    }

    public void setDisplayPrototypeButton(boolean displayPrototypeButton) {
        _displayPrototypeButton = displayPrototypeButton;
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

    public void setPrototypeButtonTooltip(String prototypeButtonTooltip) {
        _prototypeButtonTooltip = prototypeButtonTooltip;
    }

    public void setPrototypeDepth(int prototypeDepth) {
        _prototypeDepth = prototypeDepth;
    }

    public void setPrototypeDepth(Integer prototypeDepth) {
        _prototypeDepth = prototypeDepth.intValue();
    }

    public void setRemoveInstanceButtonTooltip(String removeInstanceButtonTooltip) {
        _removeInstanceButtonTooltip = removeInstanceButtonTooltip;
    }

    public void setMoveInstanceButtonsTooltip(String moveInstanceButtonsTooltip) {
        _moveInstanceButtonsTooltip = moveInstanceButtonsTooltip;
    }

    public void setViewInstanceButtonTooltip(String viewInstanceButtonTooltip) {
        _viewInstanceButtonTooltip = viewInstanceButtonTooltip;
    }
}
