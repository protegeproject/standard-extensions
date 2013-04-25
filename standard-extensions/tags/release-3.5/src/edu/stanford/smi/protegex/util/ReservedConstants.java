package edu.stanford.smi.protegex.util;



/**
 *  The protegex utils are pieces of funcitonality that are used across several
 *  distinct widgets. As such, they may need to save state to the widget's
 *  property list. The mechanism adopted for this is a keyword is used to
 *  identify a sub property list all information is stored in the sub property
 *  list. This, of course, necessitates reserving some keywords. This interface
 *  serves as a clearinghouse of reserved keywords.
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public interface ReservedConstants {
    // For the table editors
    public final static String BOOLEAN_EDITOR = ":PROTEGEX:BOOLEAN:EDITOR";
    public final static String CLASS_EDITOR = ":PROTEGEX:CLASS:EDITOR";
    public final static String FLOAT_EDITOR = ":PROTEGEX:FLOAT:EDITOR";
    public final static String INSTANCE_EDITOR = ":PROTEGEX:INSTANCE:EDITOR";
    public final static String INTEGER_EDITOR = ":PROTEGEX:INTEGER:EDITOR";
    public final static String STRING_EDITOR = ":PROTEGEX:STRING:EDITOR";
    public final static String SYMBOL_EDITOR = ":PROTEGEX:SYMBOL:EDITOR";

    // For recursive copying preferences
    public final static String RECURSIVE_COPYING = ":PROTEGEX:RECURSIVE:COPYING";

}
