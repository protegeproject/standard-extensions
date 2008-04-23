package edu.stanford.smi.protegex.export.html;

import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.ExportPlugin;
import edu.stanford.smi.protege.ui.ProjectManager;

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
            dialog = new HTMLExportConfigDialog((java.awt.Frame) window,
                "HTML Export Configuration Options", true, project);
            dialog.setSize(new Dimension(450, 525));
            dialog.setLocationRelativeTo(mainPanel);
            dialog.setVisible(true);

            if (dialog.okPressed == true) {
                ExportConfiguration config = dialog.getExportConfiguration();
                HTMLExport exporter = new HTMLExport(config);
                try {
                    exporter.export();
                } catch (HTMLExportException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    
    public static boolean isSuitable(Project prj) {
    	if (prj == null) return false;
    	
        String factoryName = prj.getKnowledgeBase().getClass().getName();
        return factoryName.indexOf(".owl.") == -1;
    }

    public void dispose() {
    }
}
