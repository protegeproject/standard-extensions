package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.event.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class Action_DeleteInstance extends Action_AbstractTableListener {
    private static final long serialVersionUID = 2846989099625618392L;
    private KnowledgeBase _kb;

    public Action_DeleteInstance(InstanceTableWidget widget, InstanceTable table) {
        super((widget.getState()).getDeleteInstanceButtonTooltip(), Icons.getDeleteIcon(), widget, table);
        _kb = widget.getKnowledgeBase();
    }

    public void actionPerformed(ActionEvent e) {
        if (isSlotEditable()) {
            Collection selectedInstances = new ArrayList(_table.getSelectedInstances());
            Iterator j = selectedInstances.iterator();
            while (j.hasNext()) {
                Instance instance = (Instance) j.next();
                if (!instance.isEditable()) {
                    j.remove();
                }
            }

            _widget.removeValues(selectedInstances);
            Iterator i = selectedInstances.iterator();
            while (i.hasNext()) {
                Frame nextInstance = (Frame) i.next();
                _kb.deleteFrame(nextInstance);
            }
        }
    }
}
