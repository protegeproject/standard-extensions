package edu.stanford.smi.protegex.util;

import edu.stanford.smi.protege.resource.*;

/**
 * 
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class LocalizedText {
    private static BundleHelper helper = new BundleHelper("standard_extensions_text", LocalizedText.class);

    private static String getText(String text) {
        return helper.getText(new ResourceKey(text));
    }

    public static String getQueriesTab() {
        return getText("tab.queries");
    }
}
