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

import edu.stanford.smi.protege.action.ReferencersAction;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.DisplayUtilities;
import edu.stanford.smi.protege.ui.ListFinder;
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
		return new AbstractAction("Export Slot Values to file", Icons.getQueryExportIcon()) {

			public void actionPerformed(ActionEvent e) {
				// Strip out system slots
				HashSet<Slot> slots = new HashSet<Slot>();
				Iterator j = itsProject.getKnowledgeBase().getSlots()
						.iterator();
				while (j.hasNext()) {
					Slot s = (Slot) j.next();
					if (!s.isSystem()) {
						slots.add(s);
					}
				}

				// Show util window for multiple slot selection
				Collection slotsToExport = DisplayUtilities.pickSlots(InstancesList.this, slots,
						"Select slots to export (multiple selection)");

				File fileToSave = null;

				JFileChooser chooser = ComponentFactory.createFileChooser(
						"Select file to export query results ",
						"Exported result files", "csv");
				int saveDialogResult = chooser.showSaveDialog(InstancesList.this);
				switch (saveDialogResult) {
				case JFileChooser.ERROR_OPTION:
					break;
				case JFileChooser.CANCEL_OPTION:
					break;
				case JFileChooser.APPROVE_OPTION:
					fileToSave = chooser.getSelectedFile();
					break;
				default:
					Assert.fail("bad result: " + saveDialogResult);
					break;
				}

				if (fileToSave == null) {
					return;
				}

				boolean success = false;
				
				try {
					Writer outputStream = FileUtilities.createBufferedWriter(fileToSave);

					if (outputStream == null) {
						Log.getLogger().log(Level.WARNING, "Unable to open output file.");
					} else {
						printResults(outputStream, getModel().getValues(), slotsToExport);
						success = true;
					}

				} catch (Exception ex) {
					Log.getLogger().log(Level.WARNING, "Errors at writing out query results file.", ex);					
				}
				
				String messageText = success ? "Query results exported successfully to:\n" + fileToSave.getAbsolutePath() :
					"There were errors at saving query results.\n" +
					"Please consult the console for more details.";
				
				ModalDialog.showMessageDialog(InstancesList.this, messageText, success ? "Export successful" : "Errors at export");
				
			}
		};
	}
		
	

	private void printResults(Writer writer, Collection instances, 
									 Collection slots) {
	    PrintWriter output = new PrintWriter(writer);
	    Iterator i = instances.iterator();
	    while (i.hasNext()) {
	        Instance instance = (Instance) i.next();
	        addInstance(output, instance, slots);
	    }
	    output.close();
	}
	
	private static void addInstance(PrintWriter writer, Instance instance, 
									Collection slots) {
        StringBuffer buffer = new StringBuffer();

        // Export the browser text for the current instance.
        buffer.append(instance.getBrowserText());
        buffer.append("\t");
        
        // Export the direct types for the current instance.
        Collection directTypes = instance.getDirectTypes();
        Iterator i = directTypes.iterator();
        while (i.hasNext()) {
        	Cls directType = (Cls) i.next();
        	buffer.append(directType.getBrowserText());
        	if (i.hasNext()) {
        		buffer.append("\t");
        	}
        }
        
        // Export the own slot values for each slot attached to the 
        // current instance.
        if (!slots.isEmpty()) { 

        	// Loop through slots attached to instance.
        	Iterator j = slots.iterator();
            while (j.hasNext()) {
            	Slot slot = (Slot) j.next();

            	Collection values = instance.getOwnSlotValues(slot);
            	if (!values.isEmpty()) {
            		buffer.append("\t");
            	}
            	
            	// Loop through values for particular slot.
            	Iterator k = values.iterator();
            	while (k.hasNext()) {
            		Object value = k.next();
            		if (value instanceof Frame) {
            			Frame frame = (Frame) value;
            			value = frame.getBrowserText();            		
            		}
            		buffer.append(value);
            		
            		if (k.hasNext()) {
            			buffer.append("\t");
            		}
            	}
            }
        }
        
        writer.println(buffer.toString());
	}
}