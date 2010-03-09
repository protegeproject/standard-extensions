package edu.stanford.smi.protegex.widget.instancetable;

import java.awt.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;

/**
 *  Wrapper around all the information that describes how a slot appears.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class VisibleSlotDescription {
    public Slot slot;
    public String columnName;
    public Color color;
    public int preferredSize;
    private final static String COLUMN_NAME_DESCRIPTOR = ":COLUMN:NAME:DESCRIPTOR";
    private final static String COLOR_RGB_DESCRIPTOR = ":COLOR:RGB:DESCRIPTOR";
    private final static String PREFERRED_SIZE_DESCRIPTOR = ":PREFERRED:SIZE:DESCRIPTOR";

    public VisibleSlotDescription(KnowledgeBase kb, PropertyList plist, String slotName) {
        slot = kb.getSlot(slotName);
        columnName = plist.getString(COLUMN_NAME_DESCRIPTOR + slotName);
        Integer rGB = plist.getInteger(COLOR_RGB_DESCRIPTOR + slotName);
        if (null != rGB) {
            color = new Color(rGB.intValue());
        } else {
            color = Color.black;
        }
        Integer prefSize = plist.getInteger(PREFERRED_SIZE_DESCRIPTOR + slotName);
        if (null != prefSize) {
            preferredSize = prefSize.intValue();
        } else {
            preferredSize = 20;
        }
    }

    public VisibleSlotDescription(Slot theSlot) {
        slot = theSlot;
        columnName = slot.getBrowserText();
        color = Color.black;
        preferredSize = 20;
    }

    public VisibleSlotDescription(Slot theSlot, Color theColor, String theColumnName, int thePreferredSize) {
        slot = theSlot;
        columnName = theColumnName;
        color = theColor;
        preferredSize = thePreferredSize;
    }

    public void writeToPropertyList(PropertyList plist) {
        String slotName = slot.getName();
        plist.setString(COLUMN_NAME_DESCRIPTOR + slotName, columnName);
        plist.setInteger(COLOR_RGB_DESCRIPTOR + slotName, color.getRGB());
        plist.setInteger(PREFERRED_SIZE_DESCRIPTOR + slotName, preferredSize);
    }
}
