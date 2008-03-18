package edu.stanford.smi.protegex.widget.graph;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

import com.nwoods.jgo.*;

public class Node_Test {

    public Node_Test() {
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.setTitle("Test Nodes");
        frame.setSize(800, 600);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JGoView view = new JGoView();
        contentPane.add(view, BorderLayout.CENTER);
        contentPane.validate();
        frame.setVisible(true);

        Node node = new Node();

        /** @todo Rewrite test code.  Had to delete it because it was outdated
         * and wasn't compiling anymore. */

        JGoDocument doc = view.getDocument();
        doc.addObjectAtTail(node);
    }
}