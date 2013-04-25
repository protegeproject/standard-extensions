package edu.stanford.smi.protegex.export.html;

import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.ExportPlugin;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;

/**
 *
 * @author Jennifer Vendetti
 */
public class HTMLExportPlugin implements ExportPlugin {
    private HTMLExportConfigDialog dialog;

    public String getName() {
        return "HTML";
    }

    public void handleExportRequest(Project project) {
    	JComponent mainPanel = ProjectManager.getProjectManager().getMainPanel();
    	Window window = SwingUtilities.windowForComponent(mainPanel);
    	if (window instanceof java.awt.Frame) {

    		String prefix = PluginUtilities.getPluginsDirectory().getPath() +
    						File.separator +
    						"edu.stanford.smi.protegex.standard_extensions" +
    						File.separator +
    						"html_export" +
    						File.separator;

    		String path = prefix + "HTMLExportConfigurations.xml";

    		File file = new File(path);
    		if (!file.exists()) {
    			ModalDialog.showMessageDialog(window, "Export configuration file not found at:\n" + path
    					, "Error");
    			return;
    		}

    		dialog = new HTMLExportConfigDialog((java.awt.Frame) window,
    				"HTML Export Configuration Options", true, project, file);
    		dialog.setSize(new Dimension(450, 525));
    		dialog.setLocationRelativeTo(mainPanel);
    		dialog.setVisible(true);

    		if (dialog.okPressed == true) {
    			ExportConfiguration config = dialog.getExportConfiguration();
    			HTMLExport exporter = new HTMLExport(config);
    			try {
    				exporter.export();
    				ModalDialog.showMessageDialog(mainPanel, "HTML export successful. Export path:\n" +
    						dialog.getExportConfiguration().getOutputDir(), "Export successful");
    			} catch (HTMLExportException e) {
    				Log.getLogger().log(Level.WARNING, "There were errors at HTML export.", e);
    				ModalDialog.showMessageDialog(mainPanel, "There were errors at export.\n" +
    						"See console for details.", "Errors at export");
    			}
    		}
    	}
    }

    public static boolean isSuitable(Project prj) {
    	if (prj == null) {
			return false;
		}

        String factoryName = prj.getKnowledgeBase().getClass().getName();
        return factoryName.indexOf(".owl.") == -1;
    }

    public void dispose() {
    }
}
