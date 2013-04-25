package edu.stanford.smi.protegex.widget.graph;

import edu.stanford.smi.protege.model.Frame;

/**
 * Encapsulates data object to be rendered in various combo boxes and tables 
 * in the widget configuration dialog.  The main reason for this is that OWL 
 * class names are long and we only want to display the short name in the 
 * user interface, but we still need access to the longer name when we store 
 * configuration properties in the PPRJ files.
 * 
 * @author Jennifer Vendetti <vendetti@stanford.edu>
 *
 */
public class FrameData {
	private Frame frame;
	private String displayName;
	private String fullName;
	
	public FrameData (Frame frame) {
		this.frame = frame;
		displayName = frame.getBrowserText();
		fullName = frame.getName();
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getFullName() {
		return fullName;
	}
	
	public Frame getFrame() {
		return frame;
	}
	
	public String toString() {
		return displayName;
	}

	@Override
	public boolean equals(Object obj) {
		boolean retval = false;
		if (obj instanceof FrameData) {
			FrameData data = (FrameData) obj;
			if (this.fullName.equals(data.getFullName())) {
				retval = true;
			}
		}
		return retval;
	}
}