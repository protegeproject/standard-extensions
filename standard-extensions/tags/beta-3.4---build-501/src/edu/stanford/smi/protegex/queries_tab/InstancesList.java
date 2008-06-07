package edu.stanford.smi.protegex.queries_tab;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import edu.stanford.smi.protege.action.ExportToCsvAction;
import edu.stanford.smi.protege.action.ReferencersAction;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.DisplayUtilities;
import edu.stanford.smi.protege.ui.ListFinder;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.*;

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
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setInstancesToExport(getModel().getValues());
				super.actionPerformed(arg0);
			}
		};
	}

	
}