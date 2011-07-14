package edu.stanford.smi.protegex.widget.graph;

// java
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

// nwoods
import com.nwoods.jgo.JGoLinkLabel;
import com.nwoods.jgo.JGoText;
import com.nwoods.jgo.JGoView;

// stanford
import edu.stanford.smi.protege.model.BrowserSlotPattern;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;

public class ComplexLinkLabel extends JGoLinkLabel {

    private static final long serialVersionUID = -3345846393267642643L;

    public ComplexLinkLabel() {
        setAlignment(JGoText.ALIGN_CENTER);
        setDraggable(false);
        setSelectable(true);
        setEditable(true);
        setEditOnSingleClick(true);

        // Partly transparent white.
        setBkColor(new Color(255, 255, 255, 200));

        setTransparent(false);
    }

    public void doEndEdit() {
        ComplexLink cLink = (ComplexLink) getLabeledLink();
        Instance instance = cLink.getInstance();
        Cls cls = instance.getDirectType();
        Slot slot = cls.getBrowserSlotPattern().getFirstSlot();
        instance.setOwnSlotValue(slot, getText());

        // Call super.doEndEdit so that the JTextComponent will go away.
        super.doEndEdit();
    }

    public void doStartEdit(JGoView view, Point vc) {
        ComplexLink cLink = (ComplexLink) getLabeledLink();
        Instance instance = cLink.getInstance();
        Cls cls = instance.getDirectType();
        BrowserSlotPattern pattern = cls.getBrowserSlotPattern();

        // Disallow in-place text editing if there is no display slot set.
        if (pattern == null) {
            setEditable(false);
            return;
        }

        // Disallow in-place text editing if multiple display slots are
        // configured.
        ArrayList browserSlots = new ArrayList(pattern.getSlots());
        if (browserSlots.size() != 1) {
            setEditable(false);
            return;
        }

        // Disallow in-place text editing if the display slot is not of type
        // String.
        Slot browserSlot = pattern.getFirstSlot();
        if (browserSlot.getValueType() != ValueType.STRING) {
            setEditable(false);
            return;
        }

        // Disallow in-place text editing if the display slot is the :NAME
        // system slot.
        if (browserSlot.getName().equals(Model.Slot.NAME)) {
            setEditable(false);
            return;
        }

        super.doStartEdit(view, vc);
    }
}