package edu.stanford.smi.protegex.widget.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class PositionInfoFixup {
	
	@SuppressWarnings("unchecked")
	public static HashMap checkForShortNames(KnowledgeBase kb, String key) {
		HashMap retval = new HashMap();
		OWLModel model = (OWLModel) kb;

		HashMap temp = (HashMap) kb.getProject().getClientInformation(key);
		if (temp != null) {
			Set keySet = temp.keySet();
			Iterator iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String shortName = (String) iterator.next();
				Object value = temp.get(shortName);
				String longName = NamespaceUtil.getFullName(model, shortName);
				if (longName != null) {
					retval.put(longName, value);
				}
			}
		}
		
		return retval;
	}
	
}
