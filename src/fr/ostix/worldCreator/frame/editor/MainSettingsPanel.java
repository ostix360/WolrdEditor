package fr.ostix.worldCreator.frame.editor;

import fr.ostix.worldCreator.frame.*;
import fr.ostix.worldCreator.toolBox.*;

import javax.swing.*;
import java.awt.*;

public class MainSettingsPanel extends JPanel {
    private final int width;
    private final int height;
    private final MainFrame frame;
    private boolean isSetUp = false;
    private JPanel currentPanel;

    public MainSettingsPanel(int width, int height, MainFrame frame) {
        this.width = width;
        this.height = height;
        this.frame = frame;
        setBorder(BorderFactory.createTitledBorder("Parametre principaux"));
        setPreferredSize(new Dimension(width, height));
        super.setLayout(new GridBagLayout());
    }


    public void setEditPanel(EditionMode editionMode) {
        if (currentPanel != null){
            this.remove(currentPanel);
        }
        if (editionMode.equals(EditionMode.TERRAIN)){
            this.add(currentPanel = new TerrainSettingPanel(frame));
        }else if (editionMode.equals(EditionMode.ENTITY_ADD)){
            this.add(currentPanel = new EntityEditionPanel(frame));
        }

    }
}
