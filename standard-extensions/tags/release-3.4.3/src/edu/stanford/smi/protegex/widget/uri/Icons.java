package edu.stanford.smi.protegex.widget.uri;

import java.net.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;

/**
 *  Description of the class
 *
 * @author    Qi Li <liq@smi.stanford.edu>
 */
public class Icons {

    public static Icon getCheckIcon() {
        return loadIcon("uri_check");
    }

    public static Icon getInspectIcon() {
        return loadIcon("uri_inspect");
    }

    private static ImageIcon loadIcon(String name) {
        ImageIcon icon = null;
        String path = "images/" + name + ".gif";
        URL url = Icons.class.getResource(path);
        if (url != null) {
            icon = new ImageIcon(url);
            if (icon.getIconWidth() == -1) {
                Log.error("failed to load", Icons.class, "loadIcon", name);
            }
        }
        if (icon == null) {
            // icon = getImageIcon("Ugly");
            Log.error("failed to create", Icons.class, "loadIcon", name);
        }
        return icon;
    }
}
