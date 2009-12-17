package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.datatransfer.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class TransferableCell implements Transferable {
    private DataFlavor[] _flavors;
    private Instance _entry;
    private ScatterboxTable _underlyingTable;

    public TransferableCell(Instance entry, ScatterboxTable underlyingTable) {
        _flavors = new DataFlavor[1];
        try {
            _flavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
        } catch (Exception e) {
        }
        _underlyingTable = underlyingTable;
        _entry = entry;
    }

    public ScatterboxTable getSourceTable() {
        return _underlyingTable;
    }

    public Object getTransferData(DataFlavor f) {
        return _entry;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return _flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return _flavors[0].equals(flavor);
    }
}
