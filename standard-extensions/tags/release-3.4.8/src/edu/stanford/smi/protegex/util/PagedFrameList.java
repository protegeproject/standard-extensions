package edu.stanford.smi.protegex.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.Finder;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protege.util.SimpleStringMatcher;
import edu.stanford.smi.protege.util.StringMatcher;

public class PagedFrameList extends LabeledComponent {
    private static final long serialVersionUID = 8889044179950621525L;

    private Finder finder;
    
    private int pageSize = 50;
    private List<FrameWithBrowserText> allFrames;
    private int pageLocation;
    private SelectableList framesList;
    private JButton scrollBack;
    private JButton scrollForward;
    private SearchType searchType;
    
    public enum SearchType {
    	STARTS_WITH("Starts With") {
            public String makeSearchString(String s) {
                return s + "*";
            }
    	}, 
        ENDS_WITH("Ends With") {
            public String makeSearchString(String s) {
    			return "*" + s;
            }
        }, 
        CONTAINS("Contains") {
            public String makeSearchString(String s) {
    			return "*" + s + "*";
            }
        }, 
        EXACT_MATCH("Exact Match") {
            public String makeSearchString(String s) {
    			return s;
            }
        };
    	
    	private String name;
    	
    	private SearchType(String name) {
    		this.name = name;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public abstract String makeSearchString(String s);
    }
    
    public PagedFrameList(String title) {
        super(title, new JScrollPane());
        JScrollPane scrollable = (JScrollPane) getCenterComponent();
        framesList = ComponentFactory.createSelectableList(null);
        scrollable.setViewportView(framesList);
        setFooterComponent(makeFooter());
        setSearchType(SearchType.CONTAINS);
    }
    
    public void setSearchType(SearchType searchType) {
    	this.searchType = searchType;
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
        
        finder = new Finder("Find") {
            private static final long serialVersionUID = -2476008720600394755L;

            @Override
            protected int getBestMatch(List matches, String text) {
                return 0;
            }
            
            @Override
            protected List<Frame> getMatches(String text, int maxMatches) {
                StringMatcher matcher = new SimpleStringMatcher(searchType.makeSearchString(text));
                int counter = 0;
                List<Frame> results = new ArrayList<Frame>(); 
                for (FrameWithBrowserText frame : allFrames) {
                    if (matcher.isMatch(frame.getBrowserText())) {
                        results.add(frame.getFrame());
                        if (maxMatches > 0 && ++counter >= maxMatches) {
                            return results;
                        }
                    }
                }
                return results;
            }
            
            @Override
            protected void select(Object o) {
                int i = 0;
                for (FrameWithBrowserText frame : allFrames) {
                    if (frame.getFrame().equals(o)) {
                        break;
                    }
                    i++;
                }
                if (i < allFrames.size()) {
                    setPageLocation(i);
                    int selection = i - pageLocation;
                    framesList.setSelectedIndex(selection);
                }
            }
        };
        footer.add(finder);
        
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
        else if (pageLocation >= allFrames.size()) {
            pageLocation = allFrames.size() - pageSize + 1;
        }
        this.pageLocation = pageLocation;
        
        int startOfNextPage = pageLocation + pageSize;
        if (startOfNextPage >= allFrames.size()) {
            startOfNextPage = allFrames.size();
        }
        List<FrameWithBrowserText> frames = allFrames.subList(pageLocation, startOfNextPage);
        framesList.setListData(frames.toArray());
        scrollForward.setEnabled(pageLocation + pageSize < allFrames.size());
        scrollBack.setEnabled(pageLocation > 0);
    }
    
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
    	if (pageSize >  0) {
    		this.pageSize = pageSize;
    		setPageLocation(pageLocation);
    	}
    }

    public List<FrameWithBrowserText> getAllFrames() {
        return allFrames;
    }

    public void setAllFrames(List<FrameWithBrowserText> allFrames) {
        this.allFrames = allFrames;
        setPageLocation(0);
    }
    
    public SelectableList getSelectableList() {
        return framesList;
    }

    public void setCellRenderer(ListCellRenderer cellRenderer) {
        finder.setCellRenderer(cellRenderer);
        framesList.setCellRenderer(cellRenderer);
    }
    
}
