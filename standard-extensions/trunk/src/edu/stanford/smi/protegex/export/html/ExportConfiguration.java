package edu.stanford.smi.protegex.export.html;

import java.util.ArrayList;
import edu.stanford.smi.protege.model.Project;

/**
 * @author Jennifer Vendetti
 */
public class ExportConfiguration {
    private boolean showInstances = false;
    private boolean useNumbering = false;

    private ArrayList rootClasses = new ArrayList();
    private ArrayList slotsToDisplay = new ArrayList();
    private ArrayList facetsToDisplay = new ArrayList();

    private String outputDir;
    private String headerPath;
    private String footerPath;
    private String cssPath;

    private Project project;

    public void setShowInstances(boolean b) {
        showInstances = b;
    }

    public boolean getShowInstances() {
        return showInstances;
    }

    public void setUseNumbering(boolean b) {
        useNumbering = b;
    }

    public boolean getUseNumbering() {
        return useNumbering;
    }

    public void setRootClasses(ArrayList rc) {
        if (rc == null) return;
        rootClasses = rc;
    }

    public ArrayList getRootClasses() {
        return rootClasses;
    }

    public void setSlotsToDisplay(ArrayList slots) {
        if (slots == null) return;
        slotsToDisplay = slots;
    }

    public ArrayList getSlotsToDisplay() {
        return slotsToDisplay;
    }

    public void setFacetsToDisplay(ArrayList facets) {
        if (facets == null) return;
        facetsToDisplay = facets;
    }

    public ArrayList getFacetsToDisplay() {
        return facetsToDisplay;
    }

    public void setOutputDir(String s) {
        outputDir = s;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setHeaderPath(String s) {
        headerPath = s;
    }

    public String getHeaderPath() {
        return headerPath;
    }

    public void setFooterPath(String s) {
        this.footerPath = s;
    }

    public String getFooterPath() {
        return footerPath;
    }

    public void setCSSPath(String s) {
        this.cssPath = s;
    }

    public String getCSSPath() {
        return cssPath;
    }

    public void setProject(Project p) {
        if (p == null) return;
        project = p;
    }

    public Project getProject() {
        return project;
    }

    public String getProjectName() {
        String s = "";
        if (project != null) { s = project.getName(); }
        return s;
    }
}
