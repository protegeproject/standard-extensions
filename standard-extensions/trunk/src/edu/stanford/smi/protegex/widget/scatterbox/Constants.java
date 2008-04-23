package edu.stanford.smi.protegex.widget.scatterbox;

import java.awt.*;

/**
 *  Description of the Interface
 *
 * @author William Grosso <grosso@smi.stanford.edu>
 */
public interface Constants {
    public final static int RECURSIVE_COPY_DEPTH_FOR_DND = 2;

    public final static String ENTRY_METACLASS_NAME = "EntryMetaclass";
    public final static String ENTRY_SLOT_NAME = "entry-slot";
    public final static String DOMAIN_VALUE_SLOTS = "domain-value-slots";
    public final static String RANGE_VALUE_SLOTS = "range-value-slots";

    public final static Color NULL_SELECTED_COLOR = (Color.darkGray).brighter();
    public final static double TEXT_SCALING_FACTOR = 1.5;
    public final static String FUNCTION_NAME = "BoundedDomain_EnumeratedFunction";

    // policies for classes as indexes
    public final static String HIERARCHICAL_INCLUDE_EVERYTHING = ":HIERARCHICAL:INCLUDE:EVERYTHING";
    public final static String HIERARCHICAL_OMIT_ROOTS = ":HIERARCHICAL:OMIT:ROOTS";
    public final static String HIERARCHICAL_ONLY_INCLUDE_LEVEL_1_CHILDREN = ":HIERARCHICAL:ONLY:INCLUDE:LEVEL:1:CHILDREN";
    public final static String HIERARCHICAL_ONLY_INCLUDE_LEAVES = ":HIERARCHICALONLY:INCLUDE:LEAVES";

    // policies for instances as indexes
    public final static String DIRECT_INSTANCES_ONLY = ":DIRECT:INSTANCES:ONLY";
    public final static String USE_ALL_INSTANCES = ":ALL:INSTANCES";

}
