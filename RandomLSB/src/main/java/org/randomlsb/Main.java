package org.randomlsb;


import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;

import java.io.File;
import java.io.FileReader;


class DragAndDrop extends JFrame {
    private JTextArea jt;

    public DragAndDrop() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        setTitle("Drag and Drop Example");
        setSize(400, 400);
        setVisible(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        jt = new JTextArea();
        add(jt);

        enableDragAndDrop();

        setLocationRelativeTo(null);
    }

    private void enableDragAndDrop() {
        DropTarget target = new DropTarget(jt, new DropTargetListener() {
            public void dragEnter(DropTargetDragEvent e) {
            }

            public void dragExit(DropTargetEvent e) {
            }

            public void dragOver(DropTargetDragEvent e) {
            }

            public void dropActionChanged(DropTargetDragEvent e) {

            }

            public void drop(DropTargetDropEvent e) {
                try {

                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);


                    java.util.List list = (java.util.List) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);


                    File file = (File) list.get(0);
                    jt.read(new FileReader(file), null);

                } catch (Exception ex) {
                }
            }
        });
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        new form();
    }
}
