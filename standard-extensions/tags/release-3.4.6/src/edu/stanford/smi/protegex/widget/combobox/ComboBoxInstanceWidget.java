package edu.stanford.smi.protegex.widget.combobox;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.ui.DisplayUtilities;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.ComboBoxWidget;

/**
 * Slot widget that allows the selection from a drop-down list of instance values. 
 * This widget can be used with instance slots of cardinality single. 
 * 
 * @author Vivek Tripathi <vivekyt@stanford.edu>
 */

public class ComboBoxInstanceWidget extends ComboBoxWidget {

	private static final long serialVersionUID = -4066393183923030991L;
	
	protected final static String NONE = "";
	private SteppedComboBox _comboBox;

	//private JButton addButton;
	private JLabel label;
	private List values;
	private Instance _instance = null;
	private boolean _showNewInstances = true;

	private FrameListener _instanceListener = new FrameAdapter() {
		public void browserTextChanged(FrameEvent event) {
			_comboBox.repaint();
		}
	};
	
	public JComboBox createComboBox() {		
		_comboBox = new SteppedComboBox();
		_comboBox.setPreferredSize(new Dimension(1, 25));
		return _comboBox;
	}

	public void initialize() {
		super.initialize();
			
		label = new JLabel(getLabel());
		//TODO: make it configurable
		/*
		addButton = new JButton("New");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleCreateAction();
			}
		});
		*/
		
		setRenderer(FrameRenderer.createInstance());
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.X_AXIS));
		footerPanel.add(label);
		footerPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		JComboBox cb = getComboBox();
		cb.setPreferredSize(new Dimension(1, 25));

		footerPanel.add(cb);	
		footerPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		
		//footerPanel.add(addButton);
		footerPanel.setPreferredSize(new Dimension(1, 25));
		add(footerPanel);
		setPreferredColumns(2);
		setPreferredRows(1);
		setPreferredSize(new Dimension(200, 35));
	}


	public ComboBoxModel createModel() {
		SteppedComboBox combo;
		ComboBoxModel model;
		Slot slot = getSlot();
		if (slot == null) {
			Log.getLogger().warning("No slot");
			model = new DefaultComboBoxModel();
		} else {
			values = new ArrayList();
			values.add(NONE);
			
			ValueType type = getCls().getTemplateSlotValueType(slot);
			Collection clses = getCls().getTemplateSlotAllowedClses(getSlot());

			for (Iterator it = clses.iterator(); it.hasNext();) {
				Cls cls = (Cls) it.next();
				for (Iterator it1 = cls.getInstances().iterator(); it1.hasNext();) {
					Instance inst = (Instance) it1.next();
					values.add(inst);
				}
			}
			if (type == ValueType.SYMBOL) {
				values.addAll(getCls().getTemplateSlotAllowedValues(slot));
			}

			model = new DefaultComboBoxModel(values.toArray());
		}

		combo = new SteppedComboBox(model);
		Dimension d = combo.getPreferredSize();
		_comboBox.setPopupWidth(d.width);

		return model;
	}

	protected void setDisplayedInstance(Instance instance) {
		if (_instance != null) {
			_instance.removeFrameListener(_instanceListener);
		}

		_instance = instance;
		if (_instance != null) {
			_instance.addFrameListener(_instanceListener);
		}
		_comboBox.addItem(instance);
		_comboBox.setSelectedItem(instance);
		comboBoxValueChanged();
	}

	protected void handleCreateAction() {
		Collection clses = getCls().getTemplateSlotAllowedClses(getSlot());
		Cls cls = DisplayUtilities.pickConcreteCls(ComboBoxInstanceWidget.this,
				getKnowledgeBase(), clses);
		if (cls != null) {
			Instance instance = getKnowledgeBase().createInstance(null, cls);
			if (instance instanceof Cls) {
				Cls newcls = (Cls) instance;
				if (newcls.getDirectSuperclassCount() == 0) {
					newcls.addDirectSuperclass(getKnowledgeBase().getRootCls());
				}
			}
			if (_showNewInstances) {
				showInstance(instance);
			}
			setDisplayedInstance(instance);

		}
	}

	public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
		boolean isSuitable;
		if (cls == null || slot == null) {
			isSuitable = false;
		} else {
			boolean isInstance = cls.getTemplateSlotValueType(slot) == ValueType.INSTANCE;
			boolean isMultiple = cls.getTemplateSlotAllowsMultipleValues(slot);
			isSuitable = isInstance && !isMultiple;
		}
		return isSuitable;
	}

	public Collection getValues() {
		Object value = _comboBox.getSelectedItem();
		if (value == NONE) {
			value = null;
			_instance = null;

		}
		return CollectionUtilities.createList(value);
	}

	public void setValues(Collection values) {
		Object value = CollectionUtilities.getFirstItem(values);
		if (value == null)
			_comboBox.setSelectedItem(NONE);
		else {
			Instance instance = (Instance) value;
			_comboBox.setSelectedItem(value);
		}
	}

	public void dispose() {
		if (_instance != null) {
			_instance.removeFrameListener(_instanceListener);
		}
		super.dispose();
	}	
}
