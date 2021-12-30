package fr.ostix.worldCreator.frame;

import fr.ostix.worldCreator.toolBox.Config;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class OptionPanel {

    private JDialog frame;
    private String output = Config.OUTPUT_FOLDER.getAbsolutePath();
    private String rep = Config.REPOSITORY_FOLDER.getAbsolutePath();

    public OptionPanel() {
        setupFrame();
        addButton();

        this.frame.setVisible(true);
    }

    private void addButton() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = 3;
        gc.gridx = 0;
        gc.gridy = 0;
        outputFolder(gc);
        gc.gridx = 0;
        gc.gridy = 1;
        modelFolder(gc);
        gc.gridx = 0;
        gc.gridy = 2;
        JButton button = new JButton("Valider");
        button.addActionListener((e) -> {
            try (
                    FileWriter writer = new FileWriter(Config.optionFile);) {
                if (!Config.optionFile.exists()){
                    Config.optionFile.getParentFile().mkdirs();
                    Config.optionFile.createNewFile();
                }
                writer.write(output + ";" + rep);
                this.frame.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        button.setForeground(Color.GREEN);
        frame.add(button,gc);

    }

    private void modelFolder(GridBagConstraints gc) {
        JLabel label = new JLabel(rep);
        gc.gridx = 0;
        this.frame.add(label, gc);
        JButton modelsFolder = new JButton("Dossier Ressources");
        modelsFolder.addActionListener(e -> {
            JFileChooser output = new JFileChooser();
            output.setDialogTitle("Selectionner votre dossier contenent vos models");
            output.setDialogType(JFileChooser.OPEN_DIALOG);
            output.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            output.setMultiSelectionEnabled(false);
            output.setApproveButtonText("Ouvrir");
            output.setVisible(true);
            while (output.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
                System.out.println("not selected");
            }
            Config.REPOSITORY_FOLDER = output.getSelectedFile();
            this.rep = output.getSelectedFile().getAbsolutePath();
            this.frame.validate();
            this.frame.repaint();
        });
        gc.gridx = 2;
        this.frame.add(modelsFolder,gc);
    }

    private void outputFolder(GridBagConstraints gc){
        JLabel label = new JLabel(output);
        gc.gridx = 0;
        this.frame.add(label, gc);
        JButton outputFolder = new JButton("Dossier de sauvegarde");
        outputFolder.addActionListener(e -> {
            JFileChooser output = new JFileChooser();
            output.setDialogTitle("Selectionner votre dossier de sauvegarde");
            output.setDialogType(JFileChooser.OPEN_DIALOG);
            output.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            output.setMultiSelectionEnabled(false);
            output.setApproveButtonText("Ouvrir");
            output.setVisible(true);
            while (output.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
                System.out.println("not selected");
            }
            Config.OUTPUT_FOLDER = output.getSelectedFile();
            this.output = output.getSelectedFile().getAbsolutePath();
            this.frame.validate();
            this.frame.repaint();
        });
        gc.gridx = 2;
        this.frame.add(outputFolder,gc);
    }

    private void setupFrame() {
        this.frame = new JDialog(){
            @Override
            public void dispose() {
                super.dispose();
            }
        };
        this.frame.setTitle("Option");
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setAlwaysOnTop(true);
        this.frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.frame.setSize(600, 200);
        this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);
        this.frame.setLayout(new GridBagLayout());
    }
}
