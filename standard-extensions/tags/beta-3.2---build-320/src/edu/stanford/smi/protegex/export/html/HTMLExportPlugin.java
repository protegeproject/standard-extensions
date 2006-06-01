package edu.stanford.smi.protegex.export.html;

import java.awt.*;
import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.plugin.*;
import edu.stanford.smi.protege.ui.*;

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
            dialog.setSize(new Dimension(450, 500));
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

    public void dispose() {
    }
}
