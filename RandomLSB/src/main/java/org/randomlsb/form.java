package org.randomlsb;

import org.randomlsb.Core.StegoCore;
import org.randomlsb.Core.StegoUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class form extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField keyField;
    private JTextField textField;
    private WorkPanel image;
    private JLabel OutText;
    private JButton цветButton;
    private JLabel OutText2;
    Color color;

    public form() throws Exception {
        image.setImage(StegoUtils.fetchImage("none.png"));
        setPreferredSize(new Dimension(500, 500));
        setContentPane(contentPane);

        color = new Color(255, 0, 0);
        enableDragAndDrop();

        setLocationRelativeTo(null);

        цветButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                color = JColorChooser.showDialog(null, "Выберите цвет", Color.RED);
                цветButton.setBackground(color);
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void enableDragAndDrop() {
        DropTarget target = new DropTarget(image, new DropTargetListener() {
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
                    StegoCore core = new StegoCore();
                    Path pathToImage = Paths.get(file.getAbsolutePath());
                    String nameFile = pathToImage.getFileName().toString();
                    String outPath = pathToImage.getParent().toString() + "\\" + nameFile.substring(0, nameFile.lastIndexOf('.')) + "-MAP.png";
                    BufferedImage img = core.EncryptToImgMap(pathToImage.toString(), keyField.getText(), textField.getText(), color.getRGB());
                    StegoUtils.saveImgAsPNG(img, outPath);
                    image.setImage(img);
                    OutText.setText("Готово! Результирующий файл сохранен в: " + outPath);
                    OutText2.setText(core.info(pathToImage.toString(), textField.getText()));
                } catch (Exception ex) {
                    OutText.setText(ex.toString());
                }
            }
        });
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) throws Exception {
        form dialog = new form();
        dialog.pack();
        dialog.setVisible(true);
    }


}
