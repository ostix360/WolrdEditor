package fr.ostix.worldCreator.frame.editor;

import fr.ostix.worldCreator.core.Timer;
import fr.ostix.worldCreator.core.resourcesProcessor.*;
import fr.ostix.worldCreator.frame.*;
import fr.ostix.worldCreator.frame.editor.*;
import fr.ostix.worldCreator.graphics.textures.*;
import fr.ostix.worldCreator.toolBox.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.*;

public class TextureChooserScreen {
    private JDialog frame;
    private JList<FileInList> list;
    private File chosen;

    public TextureChooserScreen(String type) {
        setUpFrame();
        addLabel();
        addFileList(getAllIconFiles(type));
        addButton();
        this.frame.setVisible(true);
    }

    public File getChosen() {
        return chosen;
    }

    public TextureLoader getTextureChosen() {
        try {
            if (chosen != null) {
                TextureLoaderRequest tr = new TextureLoaderRequest(new FileInputStream(chosen));
                GLRequestProcessor.sendRequest(tr);
                Timer.waitForRequest(tr);
                return tr.getTexture().setFile(chosen);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setUpFrame() {
        this.frame = new JDialog();
        this.frame.setAlwaysOnTop(true);
        this.frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setSize(300, 500);
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);
        this.frame.setLayout(new GridBagLayout());
    }

    private void addButton() {
        JButton confirm = new JButton("Open");
        confirm.setFont(new Font("Segoe UI", Font.BOLD, 15));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 1.0D;
        gc.weighty = 0.4D;
        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (!list.isSelectionEmpty()) {
                    chosen = (list.getSelectedValue()).getFile();

                    frame.setVisible(false);
                    frame.dispose();
                } else {
                    new ErrorPopUp("Couldn't open texture file!");
                }
            }
        });
        this.frame.add(confirm, gc);
    }

    private void addLabel() {
        JLabel text = new JLabel("Choose a texture!");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.0D;
        gc.weighty = 0.4D;
        this.frame.add(text, gc);
    }


    private void addFileList(List<File> files) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        FileInList[] data = getAllFilesInList(files);
        this.list = new JList<>(data);
        this.list.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.list.setLayoutOrientation(JList.VERTICAL);
        this.list.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(this.list);
        listScroller.setPreferredSize(new Dimension(250, 350));
        this.frame.add(listScroller, gc);
    }

    private FileInList[] getAllFilesInList(List<File> files) {
        FileInList[] listedFiles = new FileInList[files.size()];
        for (int i = 0; i < listedFiles.length; i++) {
            listedFiles[i] = new FileInList(files.get(i));
        }
        return listedFiles;
    }

    private List<File> getAllIconFiles(String type) {
        File folder = new File(Config.REPOSITORY_FOLDER + "/textures/terrain/" + type + "/");
        File[] allFiles = folder.listFiles();
        List<File> goodFiles = new ArrayList<>();
        if (allFiles == null) {
            System.err.println("Nothing found in " + folder.getAbsolutePath());
        }
        assert allFiles != null;
        for (File file : allFiles) {
            if (file.getName().endsWith(".png")) {
                goodFiles.add(file);
            }
        }
        return goodFiles;
    }

}
