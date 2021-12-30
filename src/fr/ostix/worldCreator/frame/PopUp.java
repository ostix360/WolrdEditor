package fr.ostix.worldCreator.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopUp {
    private JDialog frame;

    public PopUp(String... msg) {
        Toolkit.getDefaultToolkit().beep();
        setup();
        addLabel(msg);
        this.addButton(msg.length);
        this.frame.setVisible(true);
    }

    private void addLabel(String[] msg) {
        JLabel text = new JLabel("Attention...");
        text.setForeground(new Color(255, 150, 0));
        text.setFont(new Font("Segoe UI", 1, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        this.frame.add(text, gc);
        gc.gridy = 1;
        for (String str : msg) {
            gc.gridy++;
            JLabel messageText = new JLabel(str);
            messageText.setText(str);
            messageText.setForeground(new Color(0, 150, 0));
            messageText.setFont(new Font("Segoe UI", 1, 15));
            this.frame.add(messageText, gc);
        }
    }

    private void setup() {
        this.frame = new JDialog();
        this.frame.setAlwaysOnTop(true);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.frame.setSize(600, 300);
        this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);
        this.frame.setLayout(new GridBagLayout());
    }

    private void addButton(int length) {
        JButton confirm = new JButton("Close");
        confirm.setFont(new Font("Segoe UI", 1, 15));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = length + 2;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        this.frame.add(confirm, gc);
    }
}
