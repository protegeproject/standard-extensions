package edu.stanford.smi.protegex.export.html;

public class FrameData {

    private String name;
    private boolean selected;

    public FrameData(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void invertSelected() {
        selected = !selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String toString() {
        return name;
    }
}