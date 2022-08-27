package fr.ostix.worldCreator.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ErrorPopUp {

    private JDialog frame;
    private String location;

    public ErrorPopUp(String msg) {
        Toolkit.getDefaultToolkit().beep();
        setup();
        addLabel(msg);
        this.addButton();
        this.frame.setVisible(true);
    }


    public ErrorPopUp(String msg,String location) {
        Toolkit.getDefaultToolkit().beep();
        this.location = location;
        setup();
        addLabel(msg);
        this.addButton();
        this.frame.setVisible(true);
    }

    private void addLabel(String msg) {
        JLabel text = new JLabel("Error!");
        text.setForeground(new Color(255, 0, 0));
        text.setFont(new Font("Segoe UI", 1, 40));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        this.frame.add(text, gc);
        gc.gridy = 1;
        JLabel messageText = new JLabel(msg);
        messageText.setForeground(new Color(255, 0, 0));
        messageText.setFont(new Font("Segoe UI", 1, 15));
        this.frame.add(messageText, gc);
    }

    private void setup() {
        this.frame = new JDialog();
        this.frame.setAlwaysOnTop(true);
        this.frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setSize(400, 200);
        this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);
        this.frame.setLayout(new GridBagLayout());
    }

    private void addButton() {
        JButton confirm = new JButton("Close");
        confirm.setFont(new Font("Segoe UI", 1, 15));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        confirm.addActionListener(arg0 -> {
            frame.setVisible(false);
            System.out.println("ErrorPopUp 58");
            System.err.println(location);
            frame.dispose();
            System.exit(-168);
        });
        this.frame.add(confirm, gc);
    }
}
