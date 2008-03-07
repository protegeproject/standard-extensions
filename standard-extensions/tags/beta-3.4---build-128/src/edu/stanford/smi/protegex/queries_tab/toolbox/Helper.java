package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protegex.queries_tab.SearchWidget;

public final class Helper {

    /** Constructor. */
    private Helper() {
    }

    public static void addQuery(SearchWidget widget, InstancesQuery query) {
        Object[] objs = widget.getSelectedObjects();
        String clsName = null;
        String slotName = null;
        String objName = null;
        if (objs[0] != null)
            clsName = ((Cls) objs[0]).getName();
        if (objs[1] != null)
            slotName = ((Slot) objs[1]).getName();
        if (objs[3] != null) {
            if (objs[3] instanceof InstancesQuery)
                objName = ((InstancesQuery) objs[3]).getName();
            else if (objs[3] instanceof Instance)
                objName = ((Instance) objs[3]).getBrowserText();
        }
        query.addQuery(objs, clsName, slotName, objName);
    }

    public static boolean compareMatchStatus(InstancesQuery query, boolean f) {
        if (query.isMatchAll() && f)
            return true;
        if (!query.isMatchAll() && !f)
            return true;
        return false;
    }

    public static boolean compareMatchStatus(boolean f1, boolean f2) {
        if (f1 && f1)
            return true;
        if (!f1 && !f2)
            return true;
        return false;
    }

    public static boolean compareName(InstancesQuery query, String name) {
        if (query.getName().equalsIgnoreCase(name))
            return true;
        else
            return false;
    }

    public static boolean compareName(String name1, String name2) {
        if (name1.equalsIgnoreCase(name2))
            return true;
        else
            return false;
    }

    public static boolean compareObject(Object obj1, Object obj2, String type) {
        if (type == null)
            return true;
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 != null && obj2 != null) {
            if (type.equalsIgnoreCase("class") || type.equalsIgnoreCase("cls"))
                return ((Cls) obj1).getName().equalsIgnoreCase(((Cls) obj2).getName());
            if (type.equalsIgnoreCase("instance")) {
                if (obj1 instanceof InstancesQuery) {
                    if (obj2 instanceof InstancesQuery)
                        return ((InstancesQuery) obj1).getName().equalsIgnoreCase(((InstancesQuery) obj2).getName());
                    else
                        return false;
                } else {
                    if (obj2 instanceof InstancesQuery)
                        return false;
                    else
                        return ((Instance) obj1).getName().equalsIgnoreCase(((Instance) obj2).getName());
                }
            }

            if (type.equalsIgnoreCase("query"))
                return ((InstancesQuery) obj1).getName().equalsIgnoreCase(((InstancesQuery) obj2).getName());

            if (type.equalsIgnoreCase("slot"))
                return ((Slot) obj1).getName().equalsIgnoreCase(((Slot) obj2).getName());

            if (type.equalsIgnoreCase("string"))
                return ((String) obj1).equalsIgnoreCase((String) obj2);

            if (type.equalsIgnoreCase("float"))
                return ((String) obj1).equalsIgnoreCase((String) obj2);

            if (type.equalsIgnoreCase("integer"))
                return ((String) obj1).equalsIgnoreCase((String) obj2);

            if (type.equalsIgnoreCase("boolean") || type.equalsIgnoreCase("symbol"))
                return ((String) obj1).equalsIgnoreCase((String) obj2);
            return true;

        }
        return false;
    }

    public static boolean compareSearchWidget(Object[] objs0, Object[] objs1) {

        if (!Helper.compareObject(objs0[0], objs1[0], "class"))
            return false;
        if (!Helper.compareObject(objs0[1], objs1[1], "slot"))
            return false;
        if (!Helper.compareObject(objs0[2], objs1[2], "string"))
            return false;
        if (!Helper.compareObject(objs0[3], objs1[3], Helper.getSlotType((Slot) objs0[1])))
            return false;
        if (!Helper.compareObject(objs0[4], objs1[4], "string"))
            return false;

        return true;
    }

    public static boolean compareSearchWidgets(InstancesQuery query, Vector widgets0) {
        int length = query.getSize();

        if (!(length == widgets0.size()))
            return false;

        for (int i = 0; i < length; i++) {
            if (!compareSearchWidget(Helper.createObjs(query, i),
                ((SearchWidget) widgets0.elementAt(i)).getSelectedObjects()))
                return false;
        }

        return true;
    }

    public static boolean compareSearchWidgets(Vector widgets1, Vector widgets2) {

        int length = widgets1.size();
        if (!(widgets1.size() == widgets2.size()))
            return false;

        for (int i = 0; i < length; i++) {
            if (!compareSearchWidget(((SearchWidget) widgets1.elementAt(i)).getSelectedObjects(),
                ((SearchWidget) widgets2.elementAt(i)).getSelectedObjects()))
                return false;
        }

        return true;
    }

    // copy query1 to query2
    public static void copyQuery(InstancesQuery query1, InstancesQuery query2) {
        query2.setName(query1.getName());
        query2.setMatchAll(query1.isMatchAll());

        query2.cleanQuery();
        for (int i = 0; i < query1.getSize(); i++) {
            query2.addQuery(
                query1.getCls(i),
                query1.getSlot(i),
                query1.getOperation(i),
                query1.getObject(i),
                query1.getCheckStatus(i),
                query1.getClsName(i),
                query1.getSlotName(i),
                query1.getObjectName(i));
        }
    }

    /** Deep copy the Vector. */
    public static void copyVec(Vector newvec, Vector oldvec) {
        newvec.clear();
        for (int i = 0; i < oldvec.size(); i++) {
            newvec.addElement(oldvec.elementAt(i));
        }
    }

    public static String[] createNames(InstancesQuery query, int index) {
        String[] names = new String[3];

        names[0] = query.getClsName(index);
        names[1] = query.getSlotName(index);
        names[2] = query.getObjectName(index);

        return names;
    }

    public static Object[] createObjs(InstancesQuery query, int index) {
        Object[] objs = new Object[5];

        objs[0] = query.getCls(index);
        objs[1] = query.getSlot(index);
        if (objs[0] != null && objs[1] != null) {
            if (!((Cls) objs[0]).hasTemplateSlot((Slot) objs[1]))
                objs[1] = null;
        }
        objs[2] = query.getOperation(index);
        objs[3] = query.getObject(index);
        objs[4] = query.getCheckStatus(index);
        return objs;

    }

    /** Get the first Element of earch line. */
    public static String getFirstPart(String resultLine) {

        if (resultLine == null)
            return null;
        StringTokenizer tokenizer = new StringTokenizer(resultLine, ":");
        String tmpStr = new String();
        while (tokenizer.hasMoreTokens()) {
            tmpStr = tokenizer.nextToken();
            return tmpStr.trim();
        }
        return null;
    }

    // This part is used to get instances name and its browser text.
    public static String[] getNames(String inputLine) {
        String names[] = new String[2];
        if (inputLine == null)
            return null;

        StringTokenizer tokenizer = new StringTokenizer(inputLine, "|");
        String tmpStr = new String();
        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            tmpStr = tokenizer.nextToken();
            names[count] = tmpStr;
            count++;
            if (count > 1)
                break;
        }
        return names;
    }

    /** Get the second Element of earch line. */
    public static String getSecondPart(String resultLine) {
        if (resultLine == null)
            return null;
        StringTokenizer tokenizer = new StringTokenizer(resultLine, ":");
        String tmpStr = new String();
        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            tmpStr = tokenizer.nextToken();
            count++;
            if (count > 1)
                return tmpStr.trim();
        }
        return null;
    }

    public static String getSlotType(Slot slot) {
        String slotType;

        if (slot == null) {
            slotType = null;
        } else if (slot.getValueType() == ValueType.INSTANCE) {
            slotType = "INSTANCE";
        } else if (slot.getValueType() == ValueType.BOOLEAN) {
            slotType = "BOOLEAN";
        } else if (slot.getValueType() == ValueType.CLS) {
            slotType = "CLS";
        } else if (slot.getValueType() == ValueType.FLOAT) {
            slotType = "FLOAT";
        } else if (slot.getValueType() == ValueType.INTEGER) {
            slotType = "INTEGER";
        } else if (slot.getValueType() == ValueType.STRING) {
            slotType = "STRING";
        } else if (slot.getValueType() == ValueType.SYMBOL) {
            slotType = "SYMBOL";
        } else {
            slotType = "INSTANCE";
        }
        return slotType;
    }

    /** This function is used to get the content of
          Concept Name: UI:, Query Term:, and Semantic type:
    */
    public static String getSpecifiedString(String symbol, String targetLine) {

        StringTokenizer tokenizer = new StringTokenizer(targetLine, ":");
        String tmpStr = new String();
        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            tmpStr = tokenizer.nextToken();
            count++;
            if (count == 1) {
                if (tmpStr.equals(symbol))
                    continue;
                else
                    return null;
            }
            if (count > 1)
                return tmpStr.trim();
        }
        return null;
    }

    /** Get the values from specified list and return them as a string array. */
    public static String[] listToStringArray(java.util.List list) {
        if (list == null)
            return null;
        String[] result = new String[list.size()];
        ListIterator iterator = list.listIterator();
        int i = 0;
        while (iterator.hasNext()) {
            result[i] = (String) iterator.next();
            i++;
        }
        return result;
    }
} // end of class
