package fr.ostix.worldCreator.frame;

import fr.ostix.worldCreator.creator.Workspace;
import fr.ostix.worldCreator.toolBox.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar {
    private final MainFrame frame;
    private final Workspace workspace;

    public MenuBar(MainFrame frame, Workspace workspace) {
        this.frame = frame;
        this.workspace = workspace;
        buildBar();
    }

    private void buildBar() {
        Font font = new Font("Segoe UI", Font.BOLD, 12);


        JMenu file = new JMenu("File");             //-------------File-------------
        add(file);
        JMenuItem newFile = new JMenuItem("Nouveau");
        JMenuItem openFile = new JMenuItem("Ouvrir");
        JMenuItem save = new JMenuItem("Sauvagarder");

        file.add(newFile);
        //file.add(openFile);
        file.add(save);

        addNewFileFunction(newFile,frame);
        addOpenFileFunction(openFile, frame);
        addSaveFunction(save);

        file.setFont(font);
        newFile.setFont(font);
        openFile.setFont(font);
        openFile.setFont(font);
        save.setFont(font);


        JMenu edit = new JMenu("Edition");          //-------------Edition-------------
        add(edit);

        JMenuItem terrain = new JMenuItem("Terrain");
        JMenuItem entity = new JMenuItem("Entity");

        edit.add(terrain);
        edit.add(entity);

        edit.setFont(font);
        entity.setFont(font);
        terrain.setFont(font);

        addTerrainFunction(terrain);
        addEntityFunction(entity);


        JMenu others = new JMenu("Option");         //-------------Option-------------
        add(others);

        JMenuItem settings = new JMenuItem("Options");
        others.add(settings);

        others.setFont(font);
        settings.setFont(font);

        addSettingsFunction(settings);
    }



    private void addNewFileFunction(JMenuItem newFile, MainFrame frame) {
        newFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AddChunkFrame(frame);
            }
        });
    }

    private void addOpenFileFunction(JMenuItem open, final MainFrame mainFrame) {
        open.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new PopUp("L'ouverture d'une entité n'est pas encore disponible");
            }
        });
    }

    private void addSaveFunction(JMenuItem save) {
        save.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                frame.getWorld().getChunkManager().save();
                MenuBar.this.workspace.save();
            }
        });
    }


    private void addEntityFunction(JMenuItem entity) {
        entity.addActionListener(e ->{
            frame.notifyChangeEditionMode(EditionMode.ENTITY_ADD);
        });
    }

    private void addTerrainFunction(JMenuItem terrain){
        terrain.addActionListener(e ->{
            frame.notifyChangeEditionMode(EditionMode.TERRAIN);
        });
    }


    private void addSettingsFunction(JMenuItem settings){
        settings.addActionListener(e ->{
            new OptionPanel();
        });
    }

    private void addAboutFunction(JMenuItem about){
        about.addActionListener(e ->{
            //new AboutPanel();
        });
    }


}
