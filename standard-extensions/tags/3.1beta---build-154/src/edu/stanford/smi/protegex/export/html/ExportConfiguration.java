package edu.stanford.smi.protegex.export.html;

import java.util.ArrayList;

import edu.stanford.smi.protege.model.Project;

/**
 *
 * @author Jennifer Vendetti
 */
public class ExportConfiguration {
    public boolean showInstances = false;
    public boolean useNumbering = false;

    public ArrayList rootClasses = new ArrayList();
    public ArrayList slotsToDisplay = new ArrayList();
    public ArrayList facetsToDisplay = new ArrayList();

    public String outputDir;
    public String headerPath;
    public String footerPath;
    public String cssPath;

    public Project project;
}
