package fr.ostix.worldCreator.frame.editor;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.frame.*;
import fr.ostix.worldCreator.toolBox.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class EntityEditionPanel extends JPanel {

    private final MainFrame frame;
    private JComboBox<Entity> entity;

    public EntityEditionPanel(MainFrame frame) {
        this.frame = frame;
        this.setupList(frame.getEntityLoaded());
        this.setup();
    }

    private void setupList(List<Entity> entities) {
        this.entity = new JComboBox<>();
        this.entity.setModel(new DefaultComboBoxModel<>(entities.toArray(new Entity[0])));
        this.entity.addActionListener(e -> frame.getWorld().setEntity((Entity) entity.getSelectedItem()));
    }

    private void setup() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = 2;
        gc.gridx = 0;
        addPlayerModel(gc);
        gc.gridx = 1;
        gc.gridy = 0;
        addPickModeButton(gc);
        gc.gridy = 1;
        addAddEntityModeButton(gc);
        gc.gridx = 2;
        this.add(this.entity,gc);
    }

    private void addPlayerModel(GridBagConstraints gc) {
        final JButton btn = new JButton("Ajouter le joueur");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(e -> frame.getWorld().addPlayer());
        this.add(btn,gc);
    }

    private void addAddEntityModeButton(GridBagConstraints gc) {
        final JButton btn = new JButton("Ajouter l'entité");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(e -> frame.getWorld().setEditionMode(EditionMode.ENTITY_ADD));
        this.add(btn,gc);
    }


    private void addPickModeButton(GridBagConstraints gc) {
        final JButton btn = new JButton("Mode Selection");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(e -> frame.getWorld().setEditionMode(EditionMode.ENTITY_PICK));
        this.add(btn,gc);
    }
}
