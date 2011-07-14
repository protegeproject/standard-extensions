package edu.stanford.smi.protegex.queries_tab;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.smi.protege.action.ExportToCsvAction;
import edu.stanford.smi.protege.action.ReferencersAction;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.ListFinder;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableContainer;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protege.util.SimpleListModel;
import edu.stanford.smi.protege.util.ViewAction;

/**
 * @author Qi Li
 * @author Tania Tudorache
 * @author Daniel Schober
 *
 * April 27, 2007 - Fixed a bug in the addInstance method that was preventing
 * the tab delimiters from being output properly in the export text file.
 * Also fixed deprecation warnings.  Jennifer Vendetti (vendetti@stanford.edu).
 */

public class InstancesList extends SelectableContainer implements Disposable {
	private static final long serialVersionUID = 7235075988010484643L;
    private Project itsProject;
    private LabeledComponent c;
    private SelectableList itsList;


    public InstancesList(Project project) {
        itsProject = project;
        Action viewAction = createViewAction();
        itsList = ComponentFactory.createSelectableList(viewAction, true);
        QueriesTabRenderer itsRenderer = new QueriesTabRenderer();
        itsRenderer.setDisplayType(true);
        itsList.setCellRenderer(itsRenderer);

        itsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                notifySelectionListeners();
            }
        });
        setLayout(new BorderLayout());
        c = new LabeledComponent("Search Results", new JScrollPane(itsList));
        c.addHeaderButton(viewAction);
        c.addHeaderButton(createReferencersAction());
        c.addHeaderButton(createExportAction());
        c.setFooterComponent(new ListFinder(itsList, "Find Instance"));
        add(c);
        setSelectable(itsList);
    }

    private Action createReferencersAction() {
        return new ReferencersAction(ResourceKey.INSTANCE_VIEW_REFERENCES, this);
    }

    private Action createViewAction() {
        return new ViewAction("View Instance", this, Icons.getViewInstanceIcon()) {
            private static final long serialVersionUID = 6623885256271126189L;

            @Override
			public void onView(Object o) {
                itsProject.show((Instance) o);
            }
        };
    }

    public LabeledComponent getLabeledComponent() {
        return c;
    }

    private SimpleListModel getModel() {
        return (SimpleListModel) itsList.getModel();
    }

    private boolean isSelectionEditable() {
        boolean isEditable = true;
        Iterator i = getSelection().iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (!instance.isEditable()) {
                isEditable = false;
                break;
            }
        }
        return isEditable;
    }

    @Override
	public void onSelectionChange() {
        boolean editable = isSelectionEditable();
        ComponentUtilities.setDragAndDropEnabled(itsList, editable);
    }

    /**
     *
     * This is the part of code which is different from directInstancesList
     */
    public void setInstances(Collection instances) {
        if (instances == null) {
            getModel().setValues(new ArrayList());
            return;
        }
        getModel().setValues(instances);
    }

    /**
     *
     * New createExportAction Method (when Export-Button is pressed).
     */
    private Action createExportAction() {
		return new ExportToCsvAction(itsProject.getKnowledgeBase()) {
			private static final long serialVersionUID = -1864487535530038067L;

            @Override
			public void actionPerformed(ActionEvent arg0) {
				setInstancesToExport(getModel().getValues());
				super.actionPerformed(arg0);
			}
		};
	}


}