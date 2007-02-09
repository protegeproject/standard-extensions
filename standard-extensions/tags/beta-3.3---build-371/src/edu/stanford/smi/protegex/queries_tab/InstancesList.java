package edu.stanford.smi.protegex.queries_tab;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.action.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.resource.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;

/**
 * 
 * TODO Class Comment
 * 
 * @author Qi Li
 * @author Tania Tudorache
 * @author Daniel Schober
 */

public class InstancesList extends SelectableContainer implements Disposable {
    private static final String EXPORT_FILENAME = "protege_query_results.txt";
    private SelectableList itsList;
    private Project itsProject;

    private LabeledComponent c;

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
        // Log.enter(this, "onSelectionChange");
        boolean editable = isSelectionEditable();
        ComponentUtilities.setDragAndDropEnabled(itsList, editable);
    }

    // This is the part of code which is different from directInstancesList
    public void setInstances(Collection instances) {
        if (instances == null) {
            getModel().setValues(new ArrayList());
            return;
        }
        getModel().setValues(instances);
    }

    //	New createExportAction Method (when Export-Button is pressed)...
    private Action createExportAction() {
        //		 A new Icon (Exp.gif in recources) was designed and the Method
        // getExpIcon() was added to Icons.java in resources
        return new AbstractAction("Export Slot Values to " + EXPORT_FILENAME, Icons
                .getQueryExportIcon()) {
            public void actionPerformed(ActionEvent e) {

                //find out all the slots except system slots
                HashSet slots = new HashSet();
                Iterator j = itsProject.getKnowledgeBase().getSlots().iterator();
                while (j.hasNext()) {
                    // only domain-specific-, not the system-slots
                    Slot s = (Slot) j.next();
                    if (!s.isSystem())
                        slots.add(s);
                }
                //Show Util Window for multiple Slot selection
                Collection slotsToExport = DisplayUtilities.pickSlots(InstancesList.this, slots,
                        "Pick slots to export (multiple selection)");
                //                System.out.println("Slots to export: " + slotsToExport);

                File file = new File(itsProject.getProjectDirectoryFile(), EXPORT_FILENAME);
                Writer ausgabestrom = FileUtilities.createBufferedWriter(file);
                // filename
                if (ausgabestrom == null) {
                    Log.getLogger().log(Level.WARNING, "Unable to open output file.");
                } else {
                    printResults(ausgabestrom, getModel().getValues(), slotsToExport);
                }
            }
        };
    }

	private static void printResults(Writer writer, Collection instances, Collection slots) {
	    PrintWriter output = new PrintWriter(writer);
	    Iterator i = instances.iterator();
	    while (i.hasNext()) {
	        Instance instance = (Instance) i.next();
	        addInstance(output, instance, slots);
	    }
	    output.close();
	}
	
	private static void addInstance(PrintWriter writer, Instance instance, Collection slots) {
        //for current Instance write in tab delim txt file:
        StringBuffer buffer = new StringBuffer();
        buffer.append(instance.getBrowserText());
        buffer.append("\t");
        addValues(buffer, instance.getDirectTypes());
        buffer.append("\t");
        Iterator i = slots.iterator();
        while (i.hasNext()) {
            Slot slot = (Slot) i.next();
            Collection values = instance.getOwnSlotValues(slot);
            addValues(buffer, values);
        }
        writer.println(buffer.toString());
	}
	
	private static void addValues(StringBuffer buffer, Collection values) {
	    boolean isFirst = true;
	    Iterator i = values.iterator();
	    while (i.hasNext()) {
	        Object value = i.next();
	        if (isFirst) {
	            isFirst = false;
	        } else {
	            buffer.append('|');
	        }
	        if (value instanceof Frame) {
	            Frame frameValue = (Frame) value;
	            value = frameValue.getBrowserText();
	        }
	        buffer.append(value);
	    }
	}
}