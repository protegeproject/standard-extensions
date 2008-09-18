package edu.stanford.smi.protegex.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.Finder;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;

public class PagedFrameList<X extends Frame> extends LabeledComponent {
    private static final long serialVersionUID = 8889044179950621525L;
    private static final transient Logger log = Log.getLogger(PagedFrameList.class);
    
    private int pageSize = 50;
    private List<X> allFrames;
    private int pageLocation;
    private JList framesList;
    private JButton scrollBack;
    private JButton scrollForward;
    
    public PagedFrameList(String title) {
        super(title, new JScrollPane());
        JScrollPane scrollable = (JScrollPane) getCenterComponent();
        scrollable.setViewportView(framesList = new JList());
        setFooterComponent(makeFooter());
    }
    
    private JComponent makeFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        scrollBack = new JButton(Icons.getBackIcon());
        scrollBack.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setPageLocation(pageLocation - pageSize);
            }
            
        });
        scrollBack.setEnabled(false);
        footer.add(scrollBack);
        
        footer.add(new Finder("Find") {
            @Override
            protected int getBestMatch(List matches, String text) {
                return 0;
            }
            
            @Override
            protected List<Frame> getMatches(String text, int maxMatches) {
                int counter = 0;
                List<Frame> results = new ArrayList<Frame>(); 
                for (Frame frame : allFrames) {
                    if (frame.getBrowserText().toLowerCase().contains(text.toLowerCase())) {
                        results.add(frame);
                        if (maxMatches > 0 && ++counter >= maxMatches) {
                            return results;
                        }
                    }
                }
                return results;
            }
            
            @Override
            protected void select(Object o) {
                int i = allFrames.indexOf(o);
                if (i != -1) {
                    setPageLocation(i);
                }
            }
        });
        
        scrollForward = new JButton(Icons.getForwardIcon());
        scrollForward.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               setPageLocation(pageLocation + pageSize);
            } 
        });
        scrollForward.setEnabled(false);
        footer.add(scrollForward);
        
        return footer;
    }
    
    public void setPageLocation(int pageLocation) {
        if (pageLocation < 0) {
            pageLocation = 0;
        }
        else if (pageSize >= allFrames.size()) {
            pageLocation = 0;
        }
        else if (pageLocation > allFrames.size() - pageSize) {
            pageLocation = allFrames.size() - pageSize + 1;
        }
        this.pageLocation = pageLocation;
        
        int startOfNextPage = pageLocation + pageSize;
        if (startOfNextPage >= allFrames.size()) {
            startOfNextPage = allFrames.size();
        }
        List<X> frames = allFrames.subList(pageLocation, startOfNextPage);
        framesList.setListData(frames.toArray());
        scrollForward.setEnabled(pageLocation + pageSize < allFrames.size());
        scrollBack.setEnabled(pageLocation > 0);
    }
    
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        setPageLocation(pageLocation);
    }

    public List<X> getAllFrames() {
        return allFrames;
    }

    public void setAllFrames(List<X> allFrames) {
        this.allFrames = allFrames;
        setPageLocation(0);
    }
    
    public static void main(String [] args) throws InterruptedException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Collection errors = new ArrayList();
                KnowledgeBase kb = new Project("examples/newspaper/newspaper.pprj", errors).getKnowledgeBase();
                if (!errors.isEmpty()) {
                    log.warning("failed to load newspaper project");
                    return;
                }
                List<Cls> classes = new ArrayList<Cls>(kb.getClses());
                Collections.sort(classes);
                PagedFrameList<Cls> panel = new PagedFrameList<Cls>("Sample");
                panel.setAllFrames(classes);
                panel.setPageSize(15);
                
                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(panel);
                frame.pack();
                frame.setVisible(true);
            }
        });

    }

    
}
