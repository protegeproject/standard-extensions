package edu.stanford.smi.protegex.export.html;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import edu.stanford.smi.protege.model.*;
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

        ArrayList rootClasses = config.rootClasses;
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

	public void export() {
    	copyImageFiles();
        copyCSSFile();

		// Generate the class hierarchy page.
		exportHierarchyPage(config.rootClasses);

		// Generate individual class pages.
        Iterator i = classesToExport.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            generateClassPage(cls);
        }
    }

    private void exportHierarchyPage(ArrayList rootClasses) {
        ArrayList classes = rootClasses;

        if (classes == null) return;

        int size = classes.size();
        if (classes.size() == 0) return;

        String pathname = config.outputDir + File.separator + "index.html";
        File f = new File(pathname);
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Print generic stuff at top of page, including custom header
            printTop(pw, properties.getProperty(CLASS_HIERARCHY));

            // Print page title
            pw.println("<span class=\"pageTitle\">" + config.project.getName() + " " + properties.getProperty(CLASS_HIERARCHY) + "</span><br><br>");
            pw.println("<div class=\"heirarchyBorder\">");

            // Print class hierarchy
            for (int i = 0; i < size; i++) {
                Cls cls = (Cls) classes.get(i);
                pw.println("<ul class=\"iconList\">");
                exportHierarchy(pw, cls);
                pw.println("</ul>");
            }

            pw.println("<br>");
            pw.println("&nbsp;&nbsp;<a href=\"#top\"><span style=\"font-weight: bold;\">^ back to top</span></a>");
            pw.println("</div><br>");
            pw.println("<span class=\"generalContentBold\">Generated: " + buildDateString() + "</span><br><br>");

            insertCustomHTML(pw, config.footerPath);

            pw.close();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void exportHierarchy(PrintWriter pw, Cls cls) {
        String style = getListItemStyle(cls);
        String clsName = cls.getName();
        String href = stripIllegalChars(clsName) + ".html";

        String listItem = "<li class=\"" + style + "\"><a href=\"" + href + "\">" + cls.getBrowserText() + "</a>";

        if ((config.showInstances) && (cls.getDirectInstanceCount() > 0)) {
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

        Collection subClses = cls.getDirectSubclasses();
        Iterator i = subClses.iterator();
        while (i.hasNext()) {
            Cls subCls = (Cls) i.next();
            pw.println("<ul class=\"iconList\">");
            exportHierarchy(pw, subCls);
            pw.println("</ul>");
        }
    }

    private void generateClassPage(Cls cls) {
        String clsName = cls.getName();
        clsName = stripIllegalChars(clsName);
        String pathname = config.outputDir + File.separator + clsName + ".html";
        File f = new File(pathname);

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Generic stuff at top of page
            printTop(pw, cls.getBrowserText());

            pw.println("<span class=\"pageTitle\">" + getClassPageTitle(cls) + ": " + cls.getBrowserText() + "</span><br><br>");
            pw.println("<div class=\"classBorder\">");

            // Print class documentation
            printDocumentation(pw, cls);

            // Print list of superclasses
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(SUPERCLASSES) + "</span>");
            printClsIconList(pw, cls.getDirectSuperclasses());

            // Print list of subclasses
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(SUBCLASSES) + "</span>");
            printClsIconList(pw, cls.getDirectSubclasses());

            // Print list of direct types
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(TYPE_PLURAL) + "</span>");
            printClsIconList(pw, cls.getDirectTypes());

            if ((config.showInstances) && (cls.getDirectInstanceCount() > 0)) {
                int numInstances = cls.getDirectInstanceCount();
                pw.println("<span class=\"sectionTitle\"><a href=\"#direct_instances\">" + properties.getProperty(INSTANCE_UPPERCASE_PLURAL) + " (" + numInstances + ")</a></span><br><br>");
            }

            // Print template slots table.
            Collection templateSlots = cls.getTemplateSlots();
            if (templateSlots.size() > 0) {
                printTemplateSlots(pw, cls);
            }

            // Print own slots table.
            printOwnSlots(pw, cls);

            // Print list of instances
            if ((config.showInstances) && (cls.getDirectInstanceCount() > 0)) {
                if (!config.useNumbering) {
                    pw.println("<br>");
                }

                pw.println("<a name=\"direct_instances\"></a>");

                /** @todo don't print any of this if there aren't any instances? */
                Collection directInstances = cls.getDirectInstances();
                if (!config.useNumbering) {
                    pw.println("<span class=\"sectionTitle\">" + properties.getProperty(INSTANCE_UPPERCASE_PLURAL) + "</span>");
                    printInstanceIconList(pw, directInstances);
                } else {
                    pw.println("<dl>");
                    pw.println("<dt class=\"sectionTitle\">" + properties.getProperty(INSTANCE_UPPERCASE_PLURAL) + "</dt>");
                    pw.println("<dd>");
                    printNumberedInstanceList(pw, directInstances);
                    pw.println("</dd>");
                    pw.println("</dl><br>");
                }

                // Generate HTML pages for all instances that are listed
                // for this class.
                Iterator j = directInstances.iterator();
                while (j.hasNext()) {
                    Instance directInstance = (Instance) j.next();
                    generateInstancePage(directInstance);
                }
            } else {
                pw.println("<br>");
            }

            printBottom(pw, true);
            insertCustomHTML(pw, config.footerPath);

            pw.close();

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
        String pathname = config.outputDir + File.separator + slotName + ".html";
        File f = new File(pathname);

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Generic stuff at top of page
            String slotBrowserText = slot.getBrowserText();
            printTop(pw, slotBrowserText);
            pw.println("<span class=\"pageTitle\">" + properties.getProperty(SLOT) + ": " + slotBrowserText + "</span><br><br>");
            pw.println("<div class=\"slotBorder\">");

            // Print slot documentation
            printDocumentation(pw, slot);

            // Print list of superslots
            Collection superslots = slot.getSuperslots();
            if (superslots.size() > 0) {
                pw.println("<span class=\"sectionTitle\">" + properties.getProperty(SUPERSLOTS) + "</span>");
                printSlotIconList(pw, superslots);
            }

            // Print list of subslots
            Collection subslots = slot.getSubslots();
            if (subslots.size() > 0) {
                pw.println("<span class=\"sectionTitle\">" + properties.getProperty(SUBSLOTS) + "</span>");
                printSlotIconList(pw, slot.getSubslots());
            }

            // Print list of direct types
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(TYPE_PLURAL) + "</span>");
            printClsIconList(pw, slot.getDirectTypes());

            // Print own slots table
            printOwnSlots(pw, slot);
            pw.println("<br>");

            printBottom(pw, true);
            insertCustomHTML(pw, config.footerPath);

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
        String pathname = config.outputDir + File.separator + instanceName + ".html";
        File f = new File(pathname);

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(f));

            // Generic stuff at top of page
            printTop(pw, instance.getBrowserText());
            pw.println("<span class=\"pageTitle\">" + properties.getProperty(INSTANCE_UPPERCASE) + ": " + instance.getBrowserText() + "</span><br><br>");
            pw.println("<div class=\"instanceBorder\">");

            // Print list of direct types
            pw.println("<span class=\"sectionTitle\">" + properties.getProperty(TYPE_PLURAL) + "</span>");
            printClsIconList(pw, instance.getDirectTypes());

            // Print own slots table
            printOwnSlots(pw, instance);

            pw.println("<br>");

            printBottom(pw, true);
            insertCustomHTML(pw, config.footerPath);

            pw.close();
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printTop(PrintWriter pw, String frameName) {
        pw.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        pw.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        pw.println("");
        pw.println("<head>");

        String pageTitleText = config.project.getName() + " Project: " + frameName;
        pw.println("<title>" + pageTitleText + "</title>");

        pw.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\"/>");

        String cssFileName = getCSSFileName();
        pw.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssFileName + "\"/>");

        pw.println("</head>");
        pw.println("");
        pw.println("<body class=\"generalContent\">");

        pw.println("<a name=\"top\"></a>");

        // Insert custom header content
        pw.println("");
        insertCustomHTML(pw, config.headerPath);
        pw.println("");
    }

    private void printBottom(PrintWriter pw, boolean hierarchyLink) {
        pw.println("&nbsp;&nbsp;<a href=\"#top\"><span style=\"font-weight: bold;\">^ back to top</span></a>");
        pw.println("</div><br>");

        if (hierarchyLink) {
            pw.println("<span class=\"generalContentBold\">Return to <a href=\"index.html\" target=\"_self\">" + properties.getProperty(CLASS_HIERARCHY) + "</a></span><br><br>");
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

    private void printSlotIconList(PrintWriter pw, Collection c) {
        // Shortcut
        if (c.size() == 0) return;

        // Open list
        pw.println("<ul class=\"iconList\">");

        // Print list items
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Slot slot = (Slot) i.next();
            String slotFileName = stripIllegalChars(slot.getName()) + ".html";
            pw.println("\t<li class=\"slot\"><a href=\"" + slotFileName + "\">" + slot.getBrowserText() + "</a></li>");
        }

        // Close list
        pw.println("</ul><br>");
    }

    private void printClsIconList(PrintWriter pw, Collection c) {
        // Open list
        pw.println("<ul class=\"iconList\">");

        // Print list items
        if (c.size() == 0) {
            pw.println("\t<li>None</li>");
        } else {
            Iterator i = c.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                String clsFileName = stripIllegalChars(cls.getName()) + ".html";
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

    private void printInstanceIconList(PrintWriter pw, Collection c) {
        // Open list
        pw.println("<ul class=\"iconList\">");

        // Print list items
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            String instanceFileName = stripIllegalChars(instance.getName()) + ".html";
            pw.println("\t<li class=\"directInstance\"><A HREF=\"" + instanceFileName + "\">" + instance.getBrowserText() + "</A></li>");
        }

        // Close list
        pw.println("</ul><br>");
    }

    private void printNumberedInstanceList(PrintWriter pw, Collection c) {
        // Open list
        pw.println("<ol style=\"margin-left: 0px; padding-left: 0px;\">");

        // Print list items
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            String instanceFileName = stripIllegalChars(instance.getName()) + ".html";
            pw.println("\t<li><A HREF=\"" + instanceFileName + "\">" + instance.getBrowserText() + "</A></li>");
        }

        // Close list
        pw.println("</ol>");
    }

    private void printTemplateSlots(PrintWriter pw, Cls cls) {
        // Open table tag
        pw.println("<table width=\"100%\" border=\"1\" cellpadding=\"3\" cellspacing=\"0\" class=\"mozillaTableHack\">");

        // Table header
        int numColumns = config.facetsToDisplay.size() + 3;
        pw.println("\t<th align=\"left\" bgcolor=\"#C4DAE5\" colspan=\"" + numColumns + "\" class=\"mozillaTableHack\">Template " + properties.getProperty(this.SLOT_PLURAL) + "</th>");

        // Table row that contains column titles.
        pw.println("\t<tr class=\"sectionTitle\">");
        pw.println("\t\t<td class=\"mozillaTableHack\">&nbsp;</td>");
        pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(SLOT) + " Name</td>");
        pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(DOCUMENTATION) + "</td>");

        ArrayList columnTitles = config.facetsToDisplay;
        boolean showType = false;
        boolean showCardinality = false;
        boolean showNumeric = false;
        boolean showDefaultVals = false;
        boolean showTemplateVals = false;

        if (config.facetsToDisplay.contains("Value Type")) {
            showType = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(TYPE) + "</td>");
        }

        if (config.facetsToDisplay.contains("Cardinality")) {
            showCardinality = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(CARDINALITY) + "</td>");
        }

        if (config.facetsToDisplay.contains("Numeric Minimum & Maximum")) {
            showNumeric = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(MIN_MAX) + "</td>");
        }

        if (config.facetsToDisplay.contains("Default Values")) {
            showDefaultVals = true;
            pw.println("\t\t<td class=\"mozillaTableHack\">" + properties.getProperty(DEFAULT) + "</td>");
        }

        if (config.facetsToDisplay.contains("Template Value")) {
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

            if (config.slotsToDisplay.contains(slotName)) {
                // Start row
                pw.println("<tr>");

                // Column 1 - icon
                String iconName = getSlotIconFileName(cls, slot);
                pw.println("<td class=\"mozillaTableHack\"><img src=\"images/" + iconName + "\" width=\"16\" height=\"16\" border=\"0\" align=\"middle\"></td>");

                // Column 2 - slot name, always present
                String fixedSlotName = stripIllegalChars(slotName);
                fixedSlotName += ".html";
                pw.println("<td class=\"mozillaTableHack\"><a href=\"" + fixedSlotName + "\">" + slot.getBrowserText() + "</a></td>");

                // Column 3 - slot documentation, always present
                /** @todo Format documentation better */
                Collection docs = cls.getTemplateSlotDocumentation(slot);
                String docString = "";
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
                            String avName = allowedValue.getName();
                            if (classesToExport.contains(allowedValue)) {
                                String fixedAVName = stripIllegalChars(avName) + ".html";
                                stringValueType += "<a href=\"" + fixedAVName + "\">" + allowedValue.getBrowserText() + "</a>";
                            } else {
                                stringValueType += allowedValue.getBrowserText();
                            }

                            if (k.hasNext()) {
                                stringValueType += ", ";
                            }
                        }
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
        }

        return retval;
    }

    private String getOwnSlotValuesString(Collection values) {
        String retval = "";

        if (values.size() == 0) {
            retval = "&nbsp";
        }

        Iterator i = values.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof Instance) {
                /** @todo Add an href to this string if an instance page
                 * was generated for this instance. */
                Instance instance = (Instance) obj;
                retval += instance.getBrowserText();
            } else {
                retval += obj.toString();
            }

            if (i.hasNext()) {
                retval += ", ";
            }
        }

        return retval;
    }

    private void printOwnSlots(PrintWriter pw, Frame frame) {
        printOwnSlotsTableHeader(pw);

        List ownSlots = new ArrayList(frame.getOwnSlots());
        Collections.sort(ownSlots, new FrameComparator());
        Iterator i = ownSlots.iterator();
        while (i.hasNext()) {
            Slot ownSlot = (Slot) i.next();
            String ownSlotName = ownSlot.getName();

            if (config.slotsToDisplay.contains(ownSlotName)) {
                // Start row
                pw.println("<tr>");

                // Column 1 - slot icon
                pw.println("<td class=\"mozillaTableHack\"><img src=\"images/slot.gif\" width=\"16\" height=\"16\" border=\"0\" align=\"middle\"></td>");

                // Column 2 - slot name
                String legalOwnSlotName = stripIllegalChars(ownSlotName);
                legalOwnSlotName += ".html";
                pw.println("<td class=\"mozillaTableHack\"><a href=\"" + legalOwnSlotName + "\">" + ownSlot.getBrowserText() + "</a></td>");

                // Column 3 - slot value
                Collection ownSlotValues = frame.getOwnSlotValues(ownSlot);
                String ownSlotValuesString = getOwnSlotValuesString(ownSlotValues);
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
        Pattern pattern = Pattern.compile("[:/*?<>|]");
        String regexp = pattern.pattern();
        retval = retval.replaceAll(regexp, "_");

        // Special handling for backslash character.  For some reason the
        // Pattern class had trouble with this character.  Need to look into
        // this at later date.
        retval = retval.replace('\\', '_');

        return retval;
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

        destDir = config.outputDir + File.separator;

        File f = new File(destDir);
        if (!f.exists()) {
            f.mkdirs();
        }

        return destDir;
    }

    private String getCSSFileName() {
        String fileName = config.cssPath;

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
