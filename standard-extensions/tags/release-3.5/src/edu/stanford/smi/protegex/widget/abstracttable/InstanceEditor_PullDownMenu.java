package edu.stanford.smi.protegex.widget.abstracttable;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class InstanceEditor_PullDownMenu extends FrameEditor_PullDownMenu {

    private static final long serialVersionUID = 5427494373272302420L;

    public InstanceEditor_PullDownMenu(JComponent onScreenComponent, Project project) {
        this(onScreenComponent, project, null, null);
    }

    public InstanceEditor_PullDownMenu(JComponent onScreenComponent, Project project, Instance instance, Slot slot) {
        super(onScreenComponent, project, instance, slot);
    }

    protected List getPossibleChoices() {
        ArrayList returnValue = new ArrayList();
        if ((null == _instance) || (null == _slot)) {
            return returnValue;
        }
        Cls cls = _instance.getDirectType();
        Collection allowedClasses = cls.getTemplateSlotAllowedClses(_slot);
        Iterator i = allowedClasses.iterator();
        while (i.hasNext()) {
            returnValue.addAll(((Cls) i.next()).getInstances());
        }
        returnValue.remove(_value);
        return returnValue;
    }
}
