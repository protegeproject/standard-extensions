package edu.stanford.smi.protegex.widget.abstracttable;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.ui.*;

/**
 *  Pops up the Protege dialog
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ClassEditor_UseDialog extends FrameEditor_UseDialog {
    private static final long serialVersionUID = 8042563874400528713L;
    private String _dialogTitle;

    public ClassEditor_UseDialog(String dialogTitle, JComponent onScreenComponent, Project project) {
        this(dialogTitle, onScreenComponent, project, null, null);
    }

    public ClassEditor_UseDialog(
        String dialogTitle,
        JComponent onScreenComponent,
        Project project,
        Instance instance,
        Slot slot) {
        super(onScreenComponent, project, instance, slot);
        _dialogTitle = dialogTitle;
    }

    protected void getValueFromDialog() {
        _processingUserSelection = true;
        if ((null != _instance) && (null != _slot)) {
            Cls cls = _instance.getDirectType();
            Collection allowedClasses = cls.getTemplateSlotAllowedParents(_slot);
            Instance selection =
                DisplayUtilities.pickCls(
                    _onScreenComponent,
                    _instance.getKnowledgeBase(),
                    allowedClasses,
                    _dialogTitle);
            Frame newValue = (Frame) selection;
            if (null != newValue) {
                _value = newValue;
                storeValueInKB();
                configureUIFromKB();
            }
        }
        _processingUserSelection = false;
        _onScreenComponent.repaint();
        return;
    }
}
