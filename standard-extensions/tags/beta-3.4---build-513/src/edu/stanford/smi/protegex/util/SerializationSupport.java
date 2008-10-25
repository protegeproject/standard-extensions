package edu.stanford.smi.protegex.util;

import java.io.*;

/**
 *  A simple class, designed to help us store state in strings. The other half
 *  of ObjectStorer
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class SerializationSupport {

    public static Object reconstituteObjectFromString(String serializedString) {
        Object returnValue = null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedString.getBytes());
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            returnValue = objectStream.readObject();
        } catch (Exception e) {
            // That's right. We do nothing with the exception.
        }
        return returnValue;
    }

    public static String serializeObjectToString(Object object) {
        String returnValue = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            objectStream.writeObject(object);
            objectStream.flush();
            returnValue = outputStream.toString();
        } catch (Exception e) {
            // That's right. We do nothing with the exception.
        }
        return returnValue;
    }
}
