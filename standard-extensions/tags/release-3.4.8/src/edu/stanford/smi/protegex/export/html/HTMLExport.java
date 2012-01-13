package edu.stanford.smi.protegex.export.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.ui.FrameComparator;

/**
 *
 * @author Jennifer Vendetti
 */
public class HTMLExport {
    private ExportConfiguration config;
    private HashSet classesToExport = new HashSet();
    private HashSet framesGenerated = new HashSet();

    private final Properties properties = new Properties();
    private final String ABSTRACT_CLASS = "abstract.class";
    private final String ABSTRACT_METACLASS = "abstract.metaclass";
    private final String CARDINALITY = "cardinality";
    private final String CLASS = "class";
    private final String CLASS_HIERARCHY = "class.hierarchy";
    private final String DEFAULT = "default";
    private final String DOCUMENTATION = "documentation";
    private final String FACET = "facet";
    private final String INSTANCE_LOWERCASE = "instance.lowercase";
    private final String INSTANCE_LOWERCASE_PLURAL = "instance.lowercase.plural";
    private final String INSTANCE_UPPERCASE = "instance.uppercase";
    private final String INSTANCE_UPPERCASE_PLURAL = "instance.uppercase.plural";
    private final String MIN_MAX = "min.max";
    private final String METACLASS = "metaclass";
    private final String SLOT = "slot";
    private final String SLOT_PLURAL = "slot.plural";
    private final String SUBCLASSES = "subclasses";
    private final String SUBSLOTS = "subslots";
    private final String SUPERCLASSES = "superclasses";
    private final String SUPERSLOTS = "superslots";
    private final String TEMPLATE_VALUE = "template.value";
    private final String TYPE = "type";
    private final String TYPE_PLURAL = "type.plural";
    private final String VALUE = "value";

    public HTMLExport(ExportConfiguration config) {
        this.config = config;

        String path = PluginUtilities.getPluginsDirectory().getPath() +
                                                       File.separator +
                      "edu.stanford.smi.protegex.standard_extensions" +
													   File.separator +
                                                   		"html_export" +
                                                       File.separator +
                                               "htmlexport.properties";

        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
        	System.out.println(e.getMessage());
        }

        ArrayList rootClasses = config.getRootClasses();
        int size = rootClasses.size();
        for (int i = 0; i < size; i++) {
            Cls cls = (Cls) rootClasses.get(i);
            classesToExport.add(cls);
            Collection subClasses = cls.getSubclasses();
            Iterator j = subClasses.iterator();
            while (j.hasNext()) {
                Cls subCls = (Cls) j.next();
                classesToExport.add(subCls);
            }
        }
    }

	public void export() throws HTMLExportException {
    	try {
        	copyImageFiles();
            copyCSSFile();

            // Generate the class hierarchy page.
            exportHierarchyPage(config.getRootClasses());

            // Generate individual class pages.
            Iterator i = classesToExport.iterator();
            while (i.hasNext()) {
            	Cls cls = (Cls) i.next();
                generateClassPage(cls);
            }
        } catch(Exception e) {
        	HTMLExportException htmlexp = (HTMLExportException) e;
            throw htmlexp;
        }
    }

    private void exportHierarchyPage(ArrayList rootClasses) {
        ArrayList classes = rootClasses;

        if (classes == null) return;
        if (classes.size() == 0) return;

        String pathname = config.getOutputDir() + File.separator + "index.html";
        File f = new File(pathname);
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Print generic stuff at top of page, including custom header
            printTop(pw, properties.getProperty(CLASS_HIERARCHY), "");

            // Print page title
            pw.println("<span class=\"pageTitle\">" + config.getProject().getName() + " " + properties.getProperty(CLASS_HIERARCHY) + "</span><br><br>");
            pw.println("<div class=\"heirarchyBorder\">");

            // Print class hierarchy
            int size = classes.size();
            for (int i = 0; i < size; i++) {
                Cls cls = (Cls) classes.get(i);
                pw.println("<ul class=\"iconList\">");
                exportHierarchy(pw, cls, "");  // pathToRoot is ""  - could be user-configurable  (mh)
                pw.println("</ul>");
            }

            pw.println("<br>");
            pw.println("&nbsp;&nbsp;<a href=\"#top\"><span style=\"font-weight: bold;\">^ back to top</span></a>");
            pw.println("</div><br>");
            pw.println("<span class=\"generalContentBold\">Generated: " + buildDateString() + "</span><br><br>");

            insertCustomHTML(pw, config.getFooterPath());

            pw.close();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void exportHierarchy(PrintWriter pw, Cls cls, String pathToRoot) {
        String style = getListItemStyle(cls);
        String href = pathToRoot + getFrameFileName(cls); // (mh) 12 Feb 2007

        String listItem = "<li class=\"" + style + "\"><a href=\"" + href + "\">" + cls.getBrowserText() + "</a>";

        if ((config.getShowInstances()) && (cls.getDirectInstanceCount() > 0)) {
            int numInstances = cls.getDirectInstanceCount();
            String s = properties.getProperty(INSTANCE_LOWERCASE_PLURAL);
            if (numInstances == 1) {
                s = properties.getProperty(INSTANCE_LOWERCASE);
            }
            listItem += "&nbsp;&nbsp;<a href=\"" + href + "#direct_instances\" class=\"instancesLink\">(" + numInstances + " " + s + ")</a></li>";
        } else {
            listItem += "</li>";
        }

        pw.println(listItem);

        List subClses = new ArrayList(cls.getDirectSubclasses());
        if (config.getSortSubclasses()) {
            Collections.sort(subClses, new FrameComparator());

            // Hack to put system classes back at the top of the sort.
            String clsName = cls.getBrowserText();
            if (clsName.equals(Model.Cls.THING)) {
                int size = subClses.size();
                Cls last = (Cls) subClses.get(size-1);
                subClses.remove(last);
                subClses.add(0, last);
            }
        }
        Iterator i = subClses.iterator();
        while (i.hasNext()) {
            Cls subCls = (Cls) i.next();
            pw.println("<ul class=\"iconList\">");
            exportHierarchy(pw, subCls, pathToRoot);
            pw.println("</ul>");
        }
    }

    private void generateClassPage(Cls cls) {
        if (framesGenerated.contains(cls.getName())) return;

        String clsName = cls.getName();
        clsName = stripIllegalChars(clsName);
        // String pathname = config.getOutputDir() + File.separator + clsName + ".html";  (mh) 12 Feb 2007
        String pathname = config.getOutputDir() + File.separator + getFrameFileName(cls); // (mh) 12 Feb 2007
        File f = new File(pathname);
        
        // For new hierarchical folder output.  
        f.getParentFile().mkdirs(); // (mh) 12 Feb 2007
        String pathToRoot = getRootDirPath(cls);   // Figure out where the root is so we can access the CSS file.
        
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Generic stuff at top of page
            printTop(pw, cls.getBrowserText(), pathToRoot);

            pw.println("<span class=\"pageTitle\">" + getClassPageTitle(cls) + ": " + cls.getBrowserText() + "</span><br><br>");
            pw.println("<div class=\"classBorder\">");

            // Print class documentation
            printDocumentation(pw, cls);

            // Print list of superclasses
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(SUPERCLASSES) + "</span>");
            printClsIconList(pw, cls.getDirectSuperclasses(), pathToRoot);

            // Print list of subclasses
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(SUBCLASSES) + "</span>");
            printClsIconList(pw, cls.getDirectSubclasses(), pathToRoot);

            // Print list of direct types
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(TYPE_PLURAL) + "</span>");
            printClsIconList(pw, cls.getDirectTypes(), pathToRoot);

            if ((config.getShowInstances()) && (cls.getDirectInstanceCount() > 0)) {
                int numInstances = cls.getDirectInstanceCount();
                pw.println("<span class=\"sectionTitle\"><a href=\"#direct_instances\">" + properties.getProperty(INSTANCE_UPPERCASE_PLURAL) + " (" + numInstances + ")</a></span><br><br>");
            }

            // Print template slots table.
            printTemplateSlots(pw, cls, pathToRoot);

            // Print own slots table.
            printOwnSlots(pw, cls, pathToRoot);

            // Print list of instances
            if ((config.getShowInstances()) && (cls.getDirectInstanceCount() > 0)) {
                if (!config.getUseNumbering()) {
                    pw.println("<br>");
                }

                pw.println("<a name=\"direct_instances\"></a>");

                List directInstances = new ArrayList(cls.getDirectInstances());
                Collections.sort(directInstances, new FrameComparator());

                if (!config.getUseNumbering()) {
                    pw.println("<span class=\"sectionTitle\">" + properties.getProperty(INSTANCE_UPPERCASE_PLURAL) + "</span>");
                    printFrameIconList(pw, directInstances, pathToRoot);
                } else {
                    pw.println("<dl>");
                    pw.println("<dt class=\"sectionTitle\">" + properties.getProperty(INSTANCE_UPPERCASE_PLURAL) + "</dt>");
                    pw.println("<dd>");
                    printNumberedInstanceList(pw, directInstances, pathToRoot);
                    pw.println("</dd>");
                    pw.println("</dl><br>");
                }
            } else {
                pw.println("<br>");
            }

            printBottom(pw, true, pathToRoot);
            insertCustomHTML(pw, config.getFooterPath());

            pw.close();
            framesGenerated.add(cls.getName());

            // Generate pages for all of the direct instances that we
            // printed on this class page.  The pages that we generate will
            // depend on the type of the direct instance (i.e. whether
            // the instance is of type SimpleInstance, Cls, Slot, or Facet).
            if ((config.getShowInstances()) && (cls.getDirectInstanceCount() > 0)) {
            	Collection directInstances = cls.getDirectInstances();
                Iterator i = directInstances.iterator();
                while (i.hasNext()) {
                    Instance directInstance = (Instance) i.next();
                    if (directInstance instanceof SimpleInstance) {
                        generateInstancePage(directInstance);
                    } else if (directInstance instanceof Slot) {
                        generateSlotPage((Slot) directInstance);
                        framesGenerated.add(directInstance.getName());
                    } else if (directInstance instanceof Cls) {
                        generateClassPage((Cls) directInstance);
                    } else if (directInstance instanceof Facet) {
                        generateFacetPage((Facet) directInstance);
                    }
                }
            }

            // Generate slot pages for all of the own and template slots
            // that we printed on this class page.
            Set slotsToExport = new HashSet();
            slotsToExport.addAll(cls.getOwnSlots());
            slotsToExport.addAll(cls.getTemplateSlots());
            Iterator iterator = slotsToExport.iterator();
            while (iterator.hasNext()) {
                Slot slot = (Slot) iterator.next();
                generateSlotPage(slot);
                framesGenerated.add(slot.getName());
            }

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void generateSlotPage(Slot slot) {
        if (framesGenerated.contains(slot.getName())) return;

        String slotName = slot.getName();
        slotName = stripIllegalChars(slotName);
        //String pathname = config.getOutputDir() + File.separator + "slot_" + slotName + ".html";
        String pathname = config.getOutputDir() + File.separator + getFrameFileName(slot); // (mh) 12 Feb 2007
        File f = new File(pathname);
        
        // For new hierarchical folder output.  
        f.getParentFile().mkdirs(); // (mh) 12 Feb 2007
        String pathToRoot = getRootDirPath(slot);   // Figure out where the root is so we can access the CSS file.

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Generic stuff at top of page
            String slotBrowserText = slot.getBrowserText();
            printTop(pw, slotBrowserText, pathToRoot);
            pw.println("<span class=\"pageTitle\">" + properties.getProperty(SLOT) + ": " + slotBrowserText + "</span><br><br>");
            pw.println("<div class=\"slotBorder\">");

            // Print slot documentation
            printDocumentation(pw, slot);

            // Print list of superslots
            Collection superslots = slot.getSuperslots();
            if (superslots.size() > 0) {
                pw.println("<span class=\"sectionTitle\">" + properties.getProperty(SUPERSLOTS) + "</span>");
                printSlotIconList(pw, superslots, pathToRoot);
            }

            // Print list of subslots
            Collection subslots = slot.getSubslots();
            if (subslots.size() > 0) {
                pw.println("<span class=\"sectionTitle\">" + properties.getProperty(SUBSLOTS) + "</span>");
                printSlotIconList(pw, slot.getSubslots(), pathToRoot);
            }

            // Print list of direct types
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(TYPE_PLURAL) + "</span>");
            printClsIconList(pw, slot.getDirectTypes(), pathToRoot);

            // Print own slots table
            printOwnSlots(pw, slot, pathToRoot);
            pw.println("<br>");

            printBottom(pw, true, pathToRoot);
            insertCustomHTML(pw, config.getFooterPath());

            pw.close();
            framesGenerated.add(slot.getName());

            // Generate slot pages for all of the own slots, subslots, and
            // superslots that we printed on this slot page.
            Set slotsToExport = new HashSet();
            slotsToExport.addAll(slot.getOwnSlots());
            slotsToExport.addAll(slot.getSuperslots());
            slotsToExport.addAll(slot.getSubslots());
            Iterator slotIterator = slotsToExport.iterator();
            while (slotIterator.hasNext()) {
                Slot slotToExport = (Slot) slotIterator.next();
                generateSlotPage(slotToExport);
            }

        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void generateInstancePage(Instance instance) {
        String instanceName = instance.getName();
        instanceName = stripIllegalChars(instanceName);
        //String pathname = config.getOutputDir() + File.separator + instanceName + ".html";
        String pathname = config.getOutputDir() + File.separator + getFrameFileName(instance); // (mh) 12 Feb 2007
        File f = new File(pathname);
        
        // For new hierarchical folder output.  
        f.getParentFile().mkdirs(); // (mh) 12 Feb 2007
        String pathToRoot = getRootDirPath(instance);   // Figure out where the root is so we can access the CSS file.

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Generic stuff at top of page
            printTop(pw, instance.getBrowserText(), pathToRoot);
            pw.println("<span class=\"pageTitle\">" + properties.getProperty(INSTANCE_UPPERCASE) + ": " + instance.getBrowserText() + "</span><br><br>");
            pw.println("<div class=\"instanceBorder\">");

            // Print list of direct types
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(TYPE_PLURAL) + "</span>");
            printClsIconList(pw, instance.getDirectTypes(), pathToRoot);

            // Print own slots table
            printOwnSlots(pw, instance, pathToRoot);

            pw.println("<br>");

            printBottom(pw, true, pathToRoot);
            insertCustomHTML(pw, config.getFooterPath());

            pw.close();
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void generateFacetPage(Facet facet) {
        String pathname = config.getOutputDir() + File.separator + getFrameFileName(facet);
        File f = new File(pathname);
        
        // For new hierarchical folder output.  
        f.getParentFile().mkdirs(); // (mh) 12 Feb 2007
        String pathToRoot = getRootDirPath(facet);   // Figure out where the root is so we can access the CSS file.

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Generic stuff at top of page
            printTop(pw, facet.getBrowserText(), pathToRoot);
            pw.println("<span class=\"pageTitle\">" + properties.getProperty(FACET) + ": " + facet.getBrowserText() + "</span><br><br>");
            pw.println("<div class=\"facetBorder\">");

            // Print list of direct types
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(TYPE_PLURAL) + "</span>");
            printClsIconList(pw, facet.getDirectTypes(), pathToRoot);

            // Print own slots table
            printOwnSlots(pw, facet, pathToRoot);

            pw.println("<br>");

            printBottom(pw, true, pathToRoot);
            insertCustomHTML(pw, config.getFooterPath());

            pw.close();
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 
     * @param pw  a PrintWriter
     * @param frameName the name of the Frame whose HTML is being exported
     * @param pathToRoot The path to the root folder - so we can locate the image and CSS files
     */
    private void printTop(PrintWriter pw, String frameName, String pathToRoot)
    {
        pw.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        pw.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        pw.println("");
        pw.println("<head>");

        String pageTitleText = config.getProjectName() + " Project: " + frameName;
        pw.println("<title>" + pageTitleText + "</title>");

        pw.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>");

        String cssFileName = getCSSFileName();
        pw.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + pathToRoot + cssFileName + "\"/>");

        pw.println("</head>");
        pw.println("");
        pw.println("<body class=\"generalContent\">");

        pw.println("<a name=\"top\"></a>");

        // Insert custom header content
        pw.println("");
        insertCustomHTML(pw, config.getHeaderPath());
        pw.println("");
    }

    private void printBottom(PrintWriter pw, boolean hierarchyLink, String pathToRoot) {
        pw.println("&nbsp;&nbsp;<a href=\"#top\"><span style=\"font-weight: bold;\">^ back to top</span></a>");
        pw.println("</div><br>");

        if (hierarchyLink) {
            pw.println("<span class=\"generalContentBold\">Return to <a href=\"" + pathToRoot + "index.html\" target=\"_self\">" + properties.getProperty(CLASS_HIERARCHY) + "</a></span><br><br>");
        }

        pw.println("<span class=\"generalContentBold\">Generated: " + buildDateString() + "</span><br><br>");
    }

    /** @todo In the future, might want to flesh out this method to
     * look for things like newline characters and output the appropriate
     * HTML to format documentation a better way. */
    private void printDocumentation(PrintWriter pw, Frame frame) {
        if (frame == null) return;

        Collection docs = frame.getDocumentation();
        String doc = "<span class=\"sectionTitle\">" + properties.getProperty(DOCUMENTATION) + ":</span> ";
        Iterator i = docs.iterator();
        while (i.hasNext()) {
            doc += (String) i.next();
        }
        doc += "<br><br>";
        pw.println(doc);
    }

    private void printOwnSlotsTableHeader(PrintWriter pw) {
        // Open table tag
        pw.println("<table width=\"100%\" border=\"1\" cellpadding=\"3\" cellspacing=\"0\" class=\"mozillaTableHack\">");

        // Table header
        pw.println("\t<th align=\"left\" bgcolor=\"#C4DAE5\" colspan=\"3\" class=\"mozillaTableHack\">Own " + properties.getProperty(SLOT_PLURAL) + "</th>");

        // Table row that contains column titles.
        pw.println("\t<tr class=\"sectionTitle\">");
        pw.println("\t\t<td class=\"mozillaTableHack\">&nbsp;</td>");
        pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(SLOT) + " Name</td>");
        pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(VALUE) + "</td>");
        pw.println("\t</tr>");
    }

    private void insertCustomHTML(PrintWriter pw, String pathname) {
        File f = new File(pathname);
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while (line != null) {
                pw.println(line);
                line = br.readLine();
            }
            br.close();
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printSlotIconList(PrintWriter pw, Collection c, String pathToRoot) {
        // Shortcut
        if (c.size() == 0) return;

        // Open list
        pw.println("<ul class=\"iconList\">");

        // Print list items
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Slot slot = (Slot) i.next();
            String slotFileName = pathToRoot + getFrameFileName(slot);
            pw.println("\t<li class=\"slot\"><a href=\"" + slotFileName + "\">" + slot.getBrowserText() + "</a></li>");
        }

        // Close list
        pw.println("</ul><br>");
    }

    private void printClsIconList(PrintWriter pw, Collection c, String pathToRoot) {
        // Open list
        pw.println("<ul class=\"iconList\">");

        // Print list items
        if (c.size() == 0) {
            pw.println("\t<li>None</li>");
        } else {
            Iterator i = c.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                String clsFileName = pathToRoot + getFrameFileName(cls);
                String style = getListItemStyle(cls);

                if (classesToExport.contains(cls)) {
                    pw.println("\t<li class=\"" + style + "\"><a href=\"" + clsFileName + "\">" + cls.getBrowserText() + "</a></li>");
                } else {
                    pw.println("\t<li class=\"" + style + "\">"  + cls.getBrowserText() + "</li>");
                }
            }
        }

        // Close list
        pw.println("</ul><br>");
    }

    private void printFrameIconList(PrintWriter pw, Collection c, String pathToRoot) {
        // Open list
        pw.println("<ul class=\"iconList\">");

        // Print list items
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Frame frame = (Frame) i.next();
            String fileName = pathToRoot + getFrameFileName(frame);
            if (frame instanceof SimpleInstance) {
                pw.println("\t<li class=\"directInstance\"><A HREF=\"" + fileName + "\">" + frame.getBrowserText() + "</A></li>");
            } else if (frame instanceof Cls) {
                String style = getListItemStyle((Cls) frame);
                pw.println("\t<li class=\"" + style + "\"><A HREF=\"" + fileName + "\">" + frame.getBrowserText() + "</A></li>");
            } else if (frame instanceof Slot) {
                pw.println("\t<li class=\"slot\"><A HREF=\"" + fileName + "\">" + frame.getBrowserText() + "</A></li>");
            } else if (frame instanceof Facet) {
            	pw.println("\t<li class=\"facet\"><A HREF=\"" + fileName + "\">" + frame.getBrowserText() + "</A></li>");
        	}
        }

        // Close list
        pw.println("</ul><br>");
    }

    private void printNumberedInstanceList(PrintWriter pw, Collection c, String pathToRoot) {
        // Open list
        pw.println("<ol style=\"margin-left: 0px; padding-left: 0px;\">");

        // Print list items
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            String instanceFileName = pathToRoot + getFrameFileName(instance);
            pw.println("\t<li><A HREF=\"" + instanceFileName + "\">" + instance.getBrowserText() + "</A></li>");
        }

        // Close list
        pw.println("</ol>");
    }

    private void printTemplateSlots(PrintWriter pw, Cls cls, String pathToRoot) {
        // Open table tag
        pw.println("<table width=\"100%\" border=\"1\" cellpadding=\"3\" cellspacing=\"0\" class=\"mozillaTableHack\">");

        // Table header
        ArrayList facetsToDisplay = config.getFacetsToDisplay();
        int numColumns = facetsToDisplay.size() + 3;
        pw.println("\t<th align=\"left\" bgcolor=\"#C4DAE5\" colspan=\"" + numColumns + "\" class=\"mozillaTableHack\">Template " + properties.getProperty(this.SLOT_PLURAL) + "</th>");

        // Table row that contains column titles.
        pw.println("\t<tr class=\"sectionTitle\">");
        pw.println("\t\t<td class=\"mozillaTableHack\">&nbsp;</td>");
        pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(SLOT) + " Name</td>");
        pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(DOCUMENTATION) + "</td>");

        boolean showType = false;
        boolean showCardinality = false;
        boolean showNumeric = false;
        boolean showDefaultVals = false;
        boolean showTemplateVals = false;

        if (facetsToDisplay.contains("Value Type")) {
            showType = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(TYPE) + "</td>");
        }

        if (facetsToDisplay.contains("Cardinality")) {
            showCardinality = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(CARDINALITY) + "</td>");
        }

        if (facetsToDisplay.contains("Numeric Minimum & Maximum")) {
            showNumeric = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(MIN_MAX) + "</td>");
        }

        if (facetsToDisplay.contains("Default Values")) {
            showDefaultVals = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(DEFAULT) + "</td>");
        }

        if (facetsToDisplay.contains("Template Value")) {
            showTemplateVals = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(TEMPLATE_VALUE) + "</td>");
        }

        pw.println("\t</tr>");

        List templateSlots = new ArrayList(cls.getTemplateSlots());
        Collections.sort(templateSlots, new FrameComparator());
        Iterator i = templateSlots.iterator();
        while (i.hasNext()) {
            Slot slot = (Slot) i.next();
            String slotName = slot.getName();
            ValueType valueType = cls.getTemplateSlotValueType(slot);

            if (config.getSlotsToDisplay().contains(slotName)) {
                // Start row
                pw.println("<tr>");

                // Column 1 - icon
                String iconName = getSlotIconFileName(cls, slot);
                pw.println("<td class=\"mozillaTableHack\"><img src=\"" + pathToRoot + "images/" + iconName + "\" width=\"16\" height=\"16\" border=\"0\" align=\"middle\"></td>");

                // Column 2 - slot name, always present
                String slotFileName = getFrameFileName(slot);
                pw.println("<td class=\"mozillaTableHack\"><a href=\"" + pathToRoot + slotFileName + "\">" + slot.getBrowserText() + "</a></td>");

                // Column 3 - slot documentation, always present
                /** @todo Format documentation better */
                Collection docs = cls.getTemplateSlotDocumentation(slot);
                String docString = "";
                if (docs.size() == 0) {
                    docString = "&nbsp;";
                }

                Iterator j = docs.iterator();
                while (j.hasNext()) {
                    docString += (String) j.next();
                }
                pw.println("<td class=\"mozillaTableHack\">" + docString + "</td>");

                // Column 4 - slot value type, configurable
                if (showType) {
                    String stringValueType = "";

                    if (valueType == ValueType.INSTANCE) {
                        Collection allowedValues = cls.getTemplateSlotAllowedClses(slot);
                        Iterator k = allowedValues.iterator();
                        while (k.hasNext()) {
                            Cls allowedValue = (Cls) k.next();
                            if (classesToExport.contains(allowedValue)) {
                                String fileName = getFrameFileName(allowedValue);
                                stringValueType += "<a href=\"" + pathToRoot + fileName + "\">" + allowedValue.getBrowserText() + "</a>";
                            } else {
                                stringValueType += allowedValue.getBrowserText();
                            }

                            if (k.hasNext()) {
                                stringValueType += ", ";
                            }
                        }
                    } else if (valueType == ValueType.SYMBOL) {
                        stringValueType = "{";
                        Collection allowedValues = cls.getTemplateSlotAllowedValues(slot);
                        Iterator k = allowedValues.iterator();
                        while (k.hasNext()) {
							stringValueType += (String) k.next();
                        	if (k.hasNext()) {
                            	stringValueType += ", ";
                            }
                        }
                        stringValueType += "}";
                    } else {
                        stringValueType = valueType.toString();
                    }
                    pw.println("<td class=\"mozillaTableHack\">" + stringValueType + "</td>");
                }

                // Column 5 - cardinality, configurable
                if (showCardinality) {
                    Integer minimum = new Integer(cls.getTemplateSlotMinimumCardinality(slot));
                    Integer maximum = new Integer(cls.getTemplateSlotMaximumCardinality(slot));
                    String s = minimum.toString() + ":";
                    if (maximum.intValue() == -1) {
                        s += "*";
                    } else {
                        s += maximum.toString();
                    }
                    pw.println("<td class=\"mozillaTableHack\">" + s + "</td>");
                }

                // Column 5 - numeric min and max, configurable
                if (showNumeric) {
                    String s = "";
                    Number min = cls.getTemplateSlotMinimumValue(slot);
                    Number max = cls.getTemplateSlotMaximumValue(slot);
                    s = getNumericString(min, max);
                    pw.println("<td class=\"mozillaTableHack\">" + s + "</td>");
                }

                // Column 6 - default values, configurable
                if (showDefaultVals) {
                    String defaultValues = "";
                    Collection defaultVals = cls.getTemplateSlotDefaultValues(slot);
                    /** @todo Format this in a better way - particulary for instances */
                    Iterator iterator = defaultVals.iterator();
                    if (defaultVals.size() == 0) {
                        defaultValues = "&nbsp;";
                    }
                    while (iterator.hasNext()) {
                        Object o = iterator.next();
                        defaultValues += o.toString();
                        if (iterator.hasNext()) {
                            defaultValues += ", ";
                        }
                    }
                    pw.println("<td class=\"mozillaTableHack\">" + defaultValues + "</td>");
                }

                // Column 7 - template values, configurable
                if (showTemplateVals) {
                    String vals = "";
                    Collection values = cls.getTemplateSlotValues(slot);
                    /** @todo Format this in a better way - particulary for
                     * instances - also, make this a method that could
                     * be used for output in columns 6 and 4. */
                    Iterator iterator = values.iterator();
                    if (values.size() == 0) {
                        vals = "&nbsp;";
                    }
                    while (iterator.hasNext()) {
                        Object o = iterator.next();
                        vals += o.toString();
                        if (iterator.hasNext()) {
                            vals += ", ";
                        }
                    }
                    pw.println("<td class=\"mozillaTableHack\">" + vals + "</td>");
                }

                pw.println("</tr>");
            }
        }

        // Close table tag
        pw.println("</table><br>");
    }

    private String getNumericString(Number min, Number max) {
        String retval = "";

        if ((min != null) && (max != null)) {
            retval = min.toString() + "-" + max.toString();
        } else if ((min != null) && (max == null)) {
            retval = "min: " + min.toString();
        } else if ((min == null) && (max != null)) {
            retval = "max: " + max.toString();
        } else {
            retval = "&nbsp;";
        }

        return retval;
    }

    private String getOwnSlotValuesString(Collection values, String pathToRoot) {
        String retval = "";

        if (values.size() == 0) {
            retval = "&nbsp;";
        }

        Iterator i = values.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            
            // Special handling for classes    (MikeHewett) 13 Feb 2007
            if (obj instanceof Cls)
            {
              if (classesToExport.contains(obj))
              {
                String clsFileName = getFrameFileName((Frame) obj);
                retval += "<a href=\"" + pathToRoot + clsFileName + "\">" + ((Cls)obj).getBrowserText() + "</a>";
              }
            }
            else if (obj instanceof Instance) {
                Instance instance = (Instance) obj;
                Cls directType = instance.getDirectType();
                if (classesToExport.contains(directType)) {
                    // Only make this a hyperlink if we generated a page
                    // for this particular instance.
                    String instanceFileName = getFrameFileName(instance);
                    retval += "<a href=\"" + pathToRoot + instanceFileName + "\">" + instance.getBrowserText() + "</a>";
                } else {
                    retval += instance.getBrowserText();
                }
            } else {
                retval += obj.toString();
            }

            if (i.hasNext()) {
                retval += ", ";
            }
        }

        return retval;
    }

    private void printOwnSlots(PrintWriter pw, Frame frame, String pathToRoot) {
        printOwnSlotsTableHeader(pw);

        List ownSlots = new ArrayList(frame.getOwnSlots());
        Collections.sort(ownSlots, new FrameComparator());
        Iterator i = ownSlots.iterator();
        while (i.hasNext()) {
            Slot ownSlot = (Slot) i.next();
            String ownSlotName = ownSlot.getBrowserText();

            if (config.getSlotsToDisplay().contains(ownSlotName)) {
                // Start row
                pw.println("<tr>");

                // Column 1 - slot icon
                pw.println("<td class=\"mozillaTableHack\"><img src=\"" + pathToRoot + "images/slot.gif\" width=\"16\" height=\"16\" border=\"0\" align=\"middle\"></td>");

                // Column 2 - slot name
                String ownSlotFileName = getFrameFileName(ownSlot);
                pw.println("<td class=\"mozillaTableHack\"><a href=\"" + pathToRoot + ownSlotFileName + "\">" + ownSlot.getBrowserText() + "</a></td>");

                // Column 3 - slot value
                Collection ownSlotValues = frame.getOwnSlotValues(ownSlot);
                String ownSlotValuesString = getOwnSlotValuesString(ownSlotValues, pathToRoot);
                pw.println("<td class=\"mozillaTableHack\">" + ownSlotValuesString + "</td>");

                // End row
                pw.println("</tr>");
            }
        }

        // Close table tag
        pw.println("</table>");
    }

    private String getListItemStyle(Cls cls) {
        String cssStyle = "";

        if (cls.isConcrete() && !cls.isClsMetaCls()) {
            cssStyle = "concreteClass";
        } else if (cls.isConcrete() && cls.isClsMetaCls()) {
            cssStyle = "metaclass";
        } else if (cls.isAbstract() && cls.isClsMetaCls()) {
            cssStyle = "abstractMetaclass";
        } else if (cls.isAbstract()) {
            cssStyle = "abstractClass";
        }

        return cssStyle;
    }

    private String getSlotIconFileName(Cls cls, Slot slot) {
        String name = "slot";
        boolean isInherited = cls.hasInheritedTemplateSlot(slot);
        boolean isOverriden = cls.hasOverriddenTemplateSlot(slot);

        if (isInherited) {
            name += ".inherited";
        }

        if (isOverriden) {
            name += ".overridden";
        }

        name += ".gif";
        return name;
    }

    private String getClassPageTitle(Cls cls) {
        String title = "";

        if (cls.isConcrete() && !cls.isMetaCls()) {
			// Label for class.
            title = properties.getProperty(CLASS);
        } else if (cls.isConcrete() && cls.isMetaCls()) {
			// Label for metclass.
            title = properties.getProperty(METACLASS);
        } else if (cls.isAbstract() && cls.isMetaCls()) {
            // Label for abstract metaclass.
            title = properties.getProperty(ABSTRACT_METACLASS);
        } else if (cls.isAbstract()) {
            // Label for abstract class.
            title = properties.getProperty(ABSTRACT_CLASS);
        }

        return title;
    }

    private String stripIllegalChars(String s) {
        String retval = s;

        // Strip all characters out of class names that are illegal
        // in file names (since HTML files are being named using class
        // names).
        Pattern pattern = Pattern.compile("[:/*?<>|`;()& ]");  // added `';&()<sp> (mh) 15 Feb 2007
        String regexp = pattern.pattern();
        retval = retval.replaceAll(regexp, "_");

        // Special handling for backslash character.  For some reason the
        // Pattern class had trouble with this character.  Need to look into
        // this at later date.
        retval = retval.replace('\\', '_');

        return retval;
    }

    private String getFrameFileName(Frame frame) {
        String name = "";

        if (frame instanceof Slot) {
            name = "slot_";
        }

        name += stripIllegalChars(frame.getName()) + ".html";
        
        // Added (Mike Hewett) 12 Feb 2007   - optionally put the pages in folders corresponding to the class hierarchy
        if (config.getUseHierarchicalFolders())
        {
          Frame currentFrame = frame;
          Frame lastFrame    = null;   // check for loops
          while ((currentFrame != null) && (! config.getRootClasses().contains(currentFrame)) && (! (currentFrame == lastFrame)))
          {
            if (currentFrame instanceof Cls)
            {
              lastFrame    = currentFrame;
              Collection supers = ((Cls)currentFrame).getDirectSuperclasses();
              if (supers.size() == 0)
                break;
              else
              {
                lastFrame = currentFrame;
                currentFrame = (Frame)(supers.iterator().next());  // Just use the first one
              }
            }
            else if (currentFrame instanceof Instance)
            {
              lastFrame    = currentFrame;
              currentFrame = ((Instance)currentFrame).getDirectType();
            }
            else
              break;
    
//            if (config.getRootClasses().contains(currentFrame))
//              break;
//            else
              name = stripIllegalChars(currentFrame.getName()) + "/" + name;  // use / for URIs instead of File.separator
          }
        }
        
        return name;
    }

    /**
     * Returns a path like "../../.." up to the root directory
     * so we can locate the CSS file and Image files.
     * This allows the output to be portable.
     * @param frame the frame being output.
     * @return a path that goes up the hierarchy to the root folder
     */
    private String getRootDirPath(Frame frame) 
    {
      // Get the path to this class and then replace all folder names with "..".
      String name = getFrameFileName(frame);
      
      // name looks like: foo/bar/baz/myFrame.html
      int pos = 0, newPos = 0;
      while (pos < name.length())
      {
        newPos = name.indexOf("/", pos);  // use forward-slash for URIs instead of File.separator
        if (newPos < 0)
          break;
        name = name.substring(0, pos) + ".." + name.substring(newPos);
        pos = pos + 3;  // "../"
      }
        
      // Strip off the filename
      int index = name.lastIndexOf("/") + 1;
      if (index >= 0)
        name = name.substring(0, index);  // include the separator
      
      return name;
    }

    private String buildDateString() {
        String result = "";

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, h:mm:ss a, zzzz");
        result = sdf.format(new Date());

        return result;
    }

    private String getSourceDir() {
        String sourceDir = "";

        sourceDir += PluginUtilities.getPluginsDirectory().getPath();
        sourceDir += File.separator;
        sourceDir += "edu.stanford.smi.protegex.standard_extensions";
        sourceDir += File.separator;
        sourceDir += "html_export";
        sourceDir += File.separator;

        return sourceDir;
    }

    private String getDestinationDir() {
        String destDir = "";

        destDir = config.getOutputDir() + File.separator;

        File f = new File(destDir);
        if (!f.exists()) {
            f.mkdirs();
        }

        return destDir;
    }

    private String getCSSFileName() {
        String fileName = config.getCSSPath();

	int index = fileName.lastIndexOf(File.separator) + 1;
        fileName = fileName.substring(index, fileName.length());

        return fileName;
    }

    private void copyCSSFile() {
        String srcPrefix = getSourceDir();
        String destPrefix = getDestinationDir();
        ArrayList files = new ArrayList();
        files.add(getCSSFileName());
        copyFiles(files, srcPrefix, destPrefix);
    }

    private void copyImageFiles() {
        String srcPrefix = getSourceDir() + "images" + File.separator;
        String destPrefix = getDestinationDir() + "images" + File.separator;

        File f = new File(destPrefix);
        if (!f.exists()) {
            f.mkdirs();
        }

        ArrayList images = new ArrayList();
        images.add("ProtegeLogo.gif");
        images.add("class.abstract.gif");
        images.add("class.gif");
        images.add("class.metaclass.abstract.gif");
        images.add("class.metaclass.gif");
        images.add("facet.gif");
        images.add("instance.gif");
        images.add("slot.gif");
        images.add("slot.inherited.gif");
        images.add("slot.overridden.gif");
        images.add("slot.inherited.overridden.gif");

        copyFiles(images, srcPrefix, destPrefix);
    }

    private void copyFiles(ArrayList fileNames, String srcDir, String destDir) {
        if (fileNames == null) return;
        if (fileNames.size() == 0) return;

		Iterator i = fileNames.iterator();
        while (i.hasNext()) {
            String fileName = (String) i.next();

            try {
                InputStream in = new FileInputStream(new File(srcDir + fileName));
                OutputStream out = new FileOutputStream(destDir + fileName);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

            } catch (java.io.FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (java.io.IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
