package edu.stanford.smi.protegex.util;

import java.util.*;

import javax.swing.*;

/**
 *  Description of the class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class EditableComboBoxModel extends AbstractListModel implements ComboBoxModel {
    private static final long serialVersionUID = 454792201352890366L;
    private ArrayList _contents;
    private Object _selectedItem;
    private int _oldSize;

    public EditableComboBoxModel() {
        _oldSize = 0;
        setContents(new ArrayList());
    }

    public EditableComboBoxModel(Collection contents) {
        _oldSize = 0;
        setContents(contents);
    }

    public void add(Object item) {
        _contents.add(item);
        fireContentsChanged(this, 0, _oldSize);
        _oldSize = _contents.size();
    }

    public void fireContentsChanged() {
        this.fireContentsChanged(this, 0, getSize());
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

    public void insertItemAt(int index, Object item) {
        if (index < _contents.size()) {
            _contents.add(index, item);
        } else {
            _contents.add(item);
        }
        fireContentsChanged(this, 0, _oldSize);
        _oldSize = _contents.size();
    }

    public void setContents(Collection newContents) {
        _contents = new ArrayList(newContents);
        fireContentsChanged(this, 0, _oldSize);
        _oldSize = _contents.size();
    }

    public void setSelectedItem(Object anItem) {
        _selectedItem = anItem;
        fireContentsChanged(this, -1, -1);
    }
}
