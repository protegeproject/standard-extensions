package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Action_CreateInstance extends AbstractAction {
    private static final long serialVersionUID = 2118792969566825716L;
    private String _dialogTitle;
    private InstanceTableWidget _widget;
    private InstanceTableWidgetState _widgetState;
    private KnowledgeBase _kb;

    public Action_CreateInstance(InstanceTableWidget widget) {
        super((widget.getState()).getCreateInstanceButtonTooltip(), Icons.getCreateIcon());
        _widget = widget;
        _widgetState = _widget.getState();
        _dialogTitle = _widgetState.getCreateInstanceDialogTitle();
        _kb = _widget.getKnowledgeBase();
    }

    public void actionPerformed(ActionEvent e) {
        if (_widget.isEditable()) {
            Cls concreteCls = getClsForInstance();
            if (null == concreteCls) {
                return;
            }
            Instance instance = createInstance(concreteCls);
            if (null == instance) {
                return;
            }
            _widget.addValue(instance);
            _widget.selectInstanceIfAppropriate(instance);
            if (_widgetState.isCreateFormForNewInstances()) {
                displayInstanceForm(instance);
            }
        }
    }

    private Instance createInstance(Cls cls) {
        Instance instance = (_widget.getKnowledgeBase()).createInstance(null, cls);
        if (instance instanceof Cls) {
            Cls newcls = (Cls) instance;
            if (newcls.getDirectSuperclassCount() == 0) {
                newcls.addDirectSuperclass(_widget.getKnowledgeBase().getRootCls());
            }
        }
        return instance;
    }

    private void displayInstanceForm(Instance instance) {
        (_widget.getProject()).show(instance);
    }

    protected Collection getAllowedClasses() {
        Instance instance = _widget.getInstance();
        Slot slot = _widget.getSlot();
        Cls cls = instance.getDirectType();
        return new ArrayList(cls.getTemplateSlotAllowedClses(slot));
    }

    private Cls getClsForInstance() {
        Collection allowedClasses = getAllowedClasses();
        if (allowedClasses.size() == 0) {
            allowedClasses.add(_kb.getRootCls());
        }
        return DisplayUtilities.pickConcreteCls((JComponent) _widget, _kb, allowedClasses, _dialogTitle);
    }
}
