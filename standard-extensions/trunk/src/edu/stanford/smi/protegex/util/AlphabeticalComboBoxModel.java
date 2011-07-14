package edu.stanford.smi.protegex.util;

import java.util.*;

import javax.swing.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class AlphabeticalComboBoxModel extends AbstractListModel implements ComboBoxModel {
    private static final long serialVersionUID = 6716420400889974829L;
    private ArrayList _contents;
    private Object _selectedItem;
    private int _selectionIndex;
    private int _oldSize;

    public AlphabeticalComboBoxModel() {
        _selectionIndex = -1;
        _oldSize = 0;
        setContents(new ArrayList());
    }

    public AlphabeticalComboBoxModel(Collection contents) {
        _selectionIndex = -1;
        _oldSize = 0;
        setContents(contents);
    }

    public Object getElementAt(int index) {
        return _contents.get(index);
    }

    public Object getSelectedItem() {
        return _selectedItem;
    }

    public int getSize() {
        return _contents.size();
    }

    public void setContents(Collection newContents) {
        _contents = new ArrayList(newContents);
        Collections.sort(_contents);
        fireContentsChanged(this, 0, _oldSize);
        _oldSize = _contents.size();
    }

    public void setSelectedItem(Object anItem) {
        _selectedItem = anItem;
        _selectionIndex = Collections.binarySearch(_contents, _selectedItem);
        fireContentsChanged(this, -1, -1);
    }
}
