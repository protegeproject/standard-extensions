package edu.stanford.smi.protegex.util;

import java.awt.*;
import java.lang.reflect.*;

import edu.stanford.smi.protege.util.*;

/**
 *  This class defines static methods which can be used to help store a bean in
 *  a Protege property list. The basic idea is this: an objec thas a set of
 *  eligible fields. In order to be eligible, a field must (1) Point to a
 *  primitive type (or a related object version of a primitive type) (2) Not end
 *  in "_KEYNAME" (capitalization significant) (3) Not end in "_DEFAULTVALUE"
 *  (capitalization significant) Fields of type 2 and 3 are useful as "hinting
 *  mechanisms" to the storage engine. That is, if they exist and have values,
 *  they will be used in the reading and writing process. If no value exists in
 *  reading, the default value will be used instead If the key name is defined,
 *  it will be used to store values in and out of the property list (otherwise
 *  the fieldname will be used as the key). Not used
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */

public class ObjectStorer {

    private static void readField(PropertyList plist, Field field, Object object) {

    }

    public void readFromPropertyList(PropertyList pList) {
        readObjectFromPropertyList(pList, this);
    }

    public static void readObjectFromPropertyList(PropertyList pList, Object object) {
        Field[] publicFields = (object.getClass()).getFields();
        for (int i = 0; i < publicFields.length; i++) {
            if (validField(publicFields[i])) {
                readField(pList, publicFields[i], object);
            }
        }
        return;
    }

    private static boolean validField(Field field) {
        Class type = field.getType();
        if (type.isPrimitive()) {
            return true;
        }
        if (type == Boolean.class) {
            return true;
        }
        if (type == Double.class) {
            return true;
        }
        if (type == Float.class) {
            return true;
        }
        if (type == String.class) {
            return true;
        }
        if (type == Integer.class) {
            return true;
        }
        if (type == Rectangle.class) {
            return true;
        }
        if (type == Dimension.class) {
            return true;
        }
        return false;
    }

    public void writeToPropertyList(PropertyList pList) {
        writeToPropertyList(pList, this);
    }

    public static void writeToPropertyList(PropertyList pList, Object object) {

    }
}
