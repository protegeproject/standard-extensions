package edu.stanford.smi.protegex.widget.contains;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.util.*;
/**
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ContainsWidgetState extends ButtonRelatedWidgetState {

    private final static String CREATE_FORM_FOR_NEW_INSTANCES = ":Create:Form:For:New:Instances";
    private final static String INSERT_AT_CURRENT_SELECTION = ":Insert:At:Current:Selection";
    private final static String SELECT_NEW_INSERTIONS = ":Select:New:Insertions";
    private final static String ALLOW_SUBWIDGET_SELECTION = ":Subwidget:Selection:Enabled";
    private final static String USE_SEPARATORS_BETWEEN_SUBWIDGETS = ":Use:Separators:Between:Subwidgets";
    private final static String DISPLAY_SUMMARY_PANEL = ":Display:Summary:Panel";
    private final static String DISPLAY_NUMBER_OF_SUBORDINATES = ":Display:Number:Of:Subordinates";
    private final static String DISPLAY_SUBORDINATE_BROWSER_KEYS = ":Display:Subordinate:Browser:Keys";
    private final static String SIZE_OF_SPACE_BETWEEN_SUBWIDGETS = ":Size:Of:Space:Between:Subwidgets";
    private final static String CONTAIN_IN_VERTICAL_DIRECTION = ":Comtain:In:Vertical:Direction";

    private boolean _selectNewInsertions;
    private boolean _insertAtCurrentSelection;
    private boolean _createFormForNewInstances;
    private boolean _allowSubwidgetSelection;
    private boolean _displaySummaryPanel;
    private boolean _displayTotalNumberOfSubordinateInstances;
    private boolean _displayBrowserKeysOfSubordinateInstances;
    private boolean _separatorUsedBetweenSubwidgets;
    private boolean _containInVerticalDirection;
    private int _spaceBetweenSubwidgets;

    public ContainsWidgetState(PropertyList properties) {
        super(properties);
    }

    public void dispose() {
    }

    public int getSpaceBetweenSubwidgets() {
        return _spaceBetweenSubwidgets;
    }

    public boolean isContainInVerticalDirection() {
        return _containInVerticalDirection;
    }

    public boolean isCreateFormForNewInstances() {
        return _createFormForNewInstances;
    }

    public boolean isDisplayBrowserKeysOfSubordinateInstances() {
        return _displayBrowserKeysOfSubordinateInstances;
    }

    // summary information
    public boolean isDisplaySummaryPanel() {
        return _displaySummaryPanel;
    }

    public boolean isDisplayTotalNumberOfSubordinateInstances() {
        return _displayTotalNumberOfSubordinateInstances;
    }

    public boolean isInsertAtCurrentSelection() {
        return _insertAtCurrentSelection;
    }

    public boolean isSelectNewInsertions() {
        return _selectNewInsertions;
    }

    public boolean isSeparatorUsedBetweenSubwidgets() {
        return _separatorUsedBetweenSubwidgets;
    }

    public boolean isSubwidgetSelectionAllowed() {
        return _allowSubwidgetSelection;
    }

    public void restore() {
        super.restore();
        _createFormForNewInstances = readBoolean(CREATE_FORM_FOR_NEW_INSTANCES, true);
        _insertAtCurrentSelection = readBoolean(INSERT_AT_CURRENT_SELECTION, true);
        _selectNewInsertions = readBoolean(SELECT_NEW_INSERTIONS, true);
        _allowSubwidgetSelection = readBoolean(ALLOW_SUBWIDGET_SELECTION, true);
        _displaySummaryPanel = readBoolean(DISPLAY_SUMMARY_PANEL, true);
        _displayTotalNumberOfSubordinateInstances = readBoolean(DISPLAY_NUMBER_OF_SUBORDINATES, true);
        _displayBrowserKeysOfSubordinateInstances = readBoolean(DISPLAY_SUBORDINATE_BROWSER_KEYS, true);
        _separatorUsedBetweenSubwidgets = readBoolean(USE_SEPARATORS_BETWEEN_SUBWIDGETS, true);
        _containInVerticalDirection = readBoolean(CONTAIN_IN_VERTICAL_DIRECTION, true);
        _spaceBetweenSubwidgets = readInt(SIZE_OF_SPACE_BETWEEN_SUBWIDGETS, 10);
    }

    public void save() {
        _properties.setBoolean(INSERT_AT_CURRENT_SELECTION, _insertAtCurrentSelection);
        _properties.setBoolean(SELECT_NEW_INSERTIONS, _selectNewInsertions);
        _properties.setBoolean(CREATE_FORM_FOR_NEW_INSTANCES, _createFormForNewInstances);
        _properties.setBoolean(ALLOW_SUBWIDGET_SELECTION, _allowSubwidgetSelection);
        _properties.setBoolean(DISPLAY_SUMMARY_PANEL, _displaySummaryPanel);
        _properties.setBoolean(DISPLAY_NUMBER_OF_SUBORDINATES, _displayTotalNumberOfSubordinateInstances);
        _properties.setBoolean(DISPLAY_SUBORDINATE_BROWSER_KEYS, _displayBrowserKeysOfSubordinateInstances);
        _properties.setBoolean(USE_SEPARATORS_BETWEEN_SUBWIDGETS, _separatorUsedBetweenSubwidgets);
        _properties.setBoolean(CONTAIN_IN_VERTICAL_DIRECTION, _containInVerticalDirection);
        _properties.setInteger(SIZE_OF_SPACE_BETWEEN_SUBWIDGETS, _spaceBetweenSubwidgets);
        super.save();
        return;
    }

    public void setContainInVerticalDirection(Boolean containInVerticalDirection) {
        _containInVerticalDirection = containInVerticalDirection.booleanValue();
    }

    public void setContainInVerticalDirection(boolean containInVerticalDirection) {
        _containInVerticalDirection = containInVerticalDirection;
    }

    public void setCreateFormForNewInstances(Boolean createFormForNewInstances) {
        _createFormForNewInstances = createFormForNewInstances.booleanValue();
    }

    public void setCreateFormForNewInstances(boolean createFormForNewInstances) {
        _createFormForNewInstances = createFormForNewInstances;
    }

    public void setDisplayBrowserKeysOfSubordinateInstances(Boolean displayBrowserKeysOfSubordinateInstances) {
        _displayBrowserKeysOfSubordinateInstances = displayBrowserKeysOfSubordinateInstances.booleanValue();
    }

    public void setDisplayBrowserKeysOfSubordinateInstances(boolean displayBrowserKeysOfSubordinateInstances) {
        _displayBrowserKeysOfSubordinateInstances = displayBrowserKeysOfSubordinateInstances;
    }

    public void setDisplaySummaryPanel(Boolean displaySummaryPanel) {
        _displaySummaryPanel = displaySummaryPanel.booleanValue();
    }

    public void setDisplaySummaryPanel(boolean displaySummaryPanel) {
        _displaySummaryPanel = displaySummaryPanel;
    }

    public void setDisplayTotalNumberOfSubordinateInstances(Boolean displayTotalNumberOfSubordinateInstances) {
        _displayTotalNumberOfSubordinateInstances = displayTotalNumberOfSubordinateInstances.booleanValue();
    }

    public void setDisplayTotalNumberOfSubordinateInstances(boolean displayTotalNumberOfSubordinateInstances) {
        _displayTotalNumberOfSubordinateInstances = displayTotalNumberOfSubordinateInstances;
    }

    public void setInsertAtCurrentSelection(Boolean insertAtCurrentSelection) {
        _insertAtCurrentSelection = insertAtCurrentSelection.booleanValue();
    }

    public void setInsertAtCurrentSelection(boolean insertAtCurrentSelection) {
        _insertAtCurrentSelection = insertAtCurrentSelection;
    }

    public void setSelectNewInsertions(Boolean selectNewInsertions) {
        _selectNewInsertions = selectNewInsertions.booleanValue();
    }

    public void setSelectNewInsertions(boolean selectNewInsertions) {
        _selectNewInsertions = selectNewInsertions;
    }

    public void setSeparatorUsedBetweenSubwidgets(Boolean separatorUsedBetweenSubwidgets) {
        _separatorUsedBetweenSubwidgets = separatorUsedBetweenSubwidgets.booleanValue();
    }

    public void setSeparatorUsedBetweenSubwidgets(boolean separatorUsedBetweenSubwidgets) {
        _separatorUsedBetweenSubwidgets = separatorUsedBetweenSubwidgets;
    }

    public void setSpaceBetweenSubwidgets(int spaceBetweenSubwidgets) {
        _spaceBetweenSubwidgets = spaceBetweenSubwidgets;
    }

    public void setSubwidgetSelectionAllowed(Boolean allowSubwidgetSelection) {
        _allowSubwidgetSelection = allowSubwidgetSelection.booleanValue();
    }

    public void setSubwidgetSelectionAllowed(boolean allowSubwidgetSelection) {
        _allowSubwidgetSelection = allowSubwidgetSelection;
    }
}
