package edu.stanford.smi.protegex.export.html;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.plugin.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;

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
            dialog.show();

            if (dialog.okPressed == true) {
                ExportConfiguration config = dialog.getExportConfiguration();
                HTMLExport exporter = new HTMLExport(config);
                exporter.export();
            }
        }
    }

    public void dispose() {
    }
}
