package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.util.*;

import edu.stanford.smi.protege.model.*;

/** Abstract Query define the basic requirement for a Query in
    a typical search. */
public abstract class AbstractQuery extends Observable{

   protected Vector qClasses;
   protected Vector qSlots;
   protected Vector qOperations;
   protected Vector qObjects;
   protected Vector qCheckStatus;

   protected Vector qClassesNames;
   protected Vector qSlotsNames;
   protected Vector qObjectsNames;

   private int length;
   private boolean isMatchAll;        // True:  MatchAll
                                      // False: MatchAny

   private String itsName;            // The name for the whole Query

    public AbstractQuery(boolean b) {
        isMatchAll = b;
        qClasses = new Vector();
        qSlots = new Vector();
        qOperations = new Vector();
        qObjects = new Vector();
        qCheckStatus = new Vector();

        qClassesNames = new Vector();
        qSlotsNames = new Vector();
        qObjectsNames = new Vector();
    }

    public void addQuery(Object[] objs, String className, String slotName, String objectName) {
        qClasses.addElement((Cls) objs[0]);
        qSlots.addElement((Slot) objs[1]);
        qOperations.addElement((Object) objs[2]);
        qObjects.addElement(objs[3]);
        qCheckStatus.addElement((String) objs[4]);
        qClassesNames.addElement(className);
        qSlotsNames.addElement(slotName);
        qObjectsNames.addElement(objectName);
    }

    public void addQuery(
        Cls cls,
        Slot slot,
        Object operation,
        Object object,
        String check,
        String className,
        String slotName,
        String objectName) {
        qClasses.addElement(cls);
        qSlots.addElement(slot);
        qOperations.addElement(operation);
        qObjects.addElement(object);
        qCheckStatus.addElement(check);
        qClassesNames.addElement(className);
        qSlotsNames.addElement(slotName);
        qObjectsNames.addElement(objectName);
    }

    public void changed(String status) {
        setChanged();
        notifyObservers(status);
    }

    public void cleanQuery() {
        qClasses.removeAllElements();
        qSlots.removeAllElements();
        qOperations.removeAllElements();
        qObjects.removeAllElements();
        qCheckStatus.removeAllElements();
        qClassesNames.removeAllElements();
        qSlotsNames.removeAllElements();
        qObjectsNames.removeAllElements();
    }

    public String getCheckStatus(int index) {
        return (String) qCheckStatus.elementAt(index);
    }

    public Cls getCls(int index) {
        return (Cls) qClasses.elementAt(index);
    }

    public String getClsName(int index) {
        return (String) qClassesNames.elementAt(index);
    }

    public int getIndex(String name) {
        return qObjectsNames.indexOf(name);
    }

    public String getName() {
        return itsName;
    }

    public Object getObject(int index) {
        return (Object) qObjects.elementAt(index);
    }

    public String getObjectName(int index) {
        return (String) qObjectsNames.elementAt(index);
    }

    public String getOperation(int index) {
        return (String) qOperations.elementAt(index);
    }

    public int getSize() {
        length = qClasses.size();
        return length;
    }

    public Slot getSlot(int index) {
        return (Slot) qSlots.elementAt(index);
    }

    public String getSlotName(int index) {
        return (String) qSlotsNames.elementAt(index);
    }

    public abstract void initialize();

    public boolean isMatchAll() {
        return isMatchAll;
    }

    public void removeQuery(int index) {
        qClasses.removeElementAt(index);
        qSlots.removeElementAt(index);
        qOperations.removeElementAt(index);
        qObjects.removeElementAt(index);
        qCheckStatus.removeElementAt(index);
        qClassesNames.removeElementAt(index);
        qSlotsNames.removeElementAt(index);
        qObjectsNames.removeElementAt(index);
    }

    public void replaceObject(Object obj, int index) {
        qObjects.removeElementAt(index);
        qObjects.insertElementAt(obj, index);
    }

    public void setMatchAll(boolean b) {
        isMatchAll = b;
    }

    public void setName(String name) {
        itsName = name;
    }
}
