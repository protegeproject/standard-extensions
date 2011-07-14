package edu.stanford.smi.protegex.widget.graph;

import java.awt.Point;

public class GraphPoint extends Point {
    private static final long serialVersionUID = -9007357250148307210L;
    private int x;
    private int y;

    public GraphPoint(String s) {
        int index = s.indexOf(",");
        Integer xInt = new Integer(s.substring(0, index));
        Integer yInt = new Integer(s.substring(index+1, s.length()));
        x = xInt.intValue();
        y = yInt.intValue();
    }
    public String toString() {
        return new String(x + "," + y);
    }

    public int getXInt() {
        return x;
    }

    public int getYInt() {
        return y;
    }

    /*public static void main(String[] args) {
        String s = new String("10,20");

        InstanceGraphPoint p = new InstanceGraphPoint(s);
        System.out.println(p);

        InstanceGraphPoint q = new InstanceGraphPoint(p.toString());
        System.out.println(q);
    }*/
}