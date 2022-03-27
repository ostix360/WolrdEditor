package fr.ostix.worldCreator.frame;

import fr.ostix.worldCreator.world.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ExitMenu {

    private JFrame frame;
    private World world;

    public ExitMenu(World world) {
        this.world = world;
        setup();
        this.addButton();
        this.frame.setVisible(false);
    }

    private void addLabel(boolean error) {
        JLabel text = new JLabel(error? "Quelque chose s'est mal passé \n vous pouvez relancer l'application avec le cmd pour voir l'erreur et la rapporter au developper \n Voulez vous sauvegarder (déconseiller)?" : "Voulez vous sauvegarder (conseiller)?");
        text.setForeground(new Color(255, 150, 0));
        text.setFont(new Font("Segoe UI", 1, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        this.frame.add(text, gc);
        gc.gridy = 1;
    }

    private void setup() {
        this.frame = new JFrame();
        this.frame.setAlwaysOnTop(true);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setSize(600, 300);
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);

        this.frame.setLayout(new GridBagLayout());
    }

    private void addButton() {
        JButton confirm = new JButton("Save");
        confirm.setFont(new Font("Segoe UI", 1, 15));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Saving world...");
                world.getChunkManager().save();
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        });
        this.frame.add(confirm, gc);
    }

    public void show(boolean error) {
        addLabel(error);
        frame.setVisible(true);
        System.out.println();
    }
}
