package edu.stanford.smi.protegex.queries_tab.toolbox;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.queries_tab.*;

public class SelectPanel extends JComponent {
     private static final long serialVersionUID = -8070662094281974006L;
    private JList itsList;

    public SelectPanel(Collection slots) {
        ArrayList slotList = new ArrayList(slots);
        itsList = ComponentFactory.createList(null);
        itsList.setListData(slotList.toArray());
        itsList.setCellRenderer(new QueriesTabRenderer());
        setLayout(new BorderLayout());
        add(new JScrollPane(itsList), BorderLayout.CENTER);
        setPreferredSize(new Dimension(300, 300));
    }

    public Collection getSelection() {
        return ComponentUtilities.getSelection(itsList);
    }
}
