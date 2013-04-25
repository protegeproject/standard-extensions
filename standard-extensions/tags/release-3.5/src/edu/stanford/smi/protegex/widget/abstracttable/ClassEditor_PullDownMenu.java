package edu.stanford.smi.protegex.widget.abstracttable;

import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Flattens subclasses to a list for a combo box
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class ClassEditor_PullDownMenu extends FrameEditor_PullDownMenu {

    private static final long serialVersionUID = 2731146051310065446L;

    public ClassEditor_PullDownMenu(JComponent onScreenComponent, Project project) {
        this(onScreenComponent, project, null, null);
    }

    public ClassEditor_PullDownMenu(JComponent onScreenComponent, Project project, Instance instance, Slot slot) {
        super(onScreenComponent, project, instance, slot);
    }

    protected List getPossibleChoices() {
        ArrayList returnValue = new ArrayList();
        if ((null == _instance) || (null == _slot)) {
            return returnValue;
        }
        Cls cls = _instance.getDirectType();
        Collection allowedClasses = cls.getTemplateSlotAllowedParents(_slot);
        Iterator i = allowedClasses.iterator();
        while (i.hasNext()) {
            addUniquely(returnValue, ((Cls) i.next()).getSubclasses());
        }
        return returnValue;
    }
}
