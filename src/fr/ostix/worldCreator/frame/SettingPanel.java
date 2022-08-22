package fr.ostix.worldCreator.frame;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.frame.editor.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SettingPanel extends JPanel {

    private final MainFrame frame;
    private JPanel currentPanel;

    public SettingPanel(MainFrame mainFrame,int width, int height) {
        this.frame = mainFrame;
        setBorder(BorderFactory.createTitledBorder("Parametre secondaire"));
        setPreferredSize(new Dimension(width, height));
        super.setLayout(new GridBagLayout());
    }

    public void initEntityPanel(Entity e){
        setBorder(BorderFactory.createTitledBorder("Parametre de l'entité"));
        if (currentPanel != null){
            this.remove(currentPanel);
        }
        this.add(currentPanel = new EntitySettingPanel(e,this.getWidth(), this.getHeight(),frame));
        this.validate();
        this.repaint();
    }

    public void initWaterTilePanel(){
        if (currentPanel != null){
            this.remove(currentPanel);
        }
        this.add(currentPanel = new WaterTileSettingPanel(frame));
        this.validate();
        this.repaint();
    }

    public void initEntitiesPanel(List<Entity> entities) {
        setBorder(BorderFactory.createTitledBorder("Parametre des entités"));
        if (currentPanel != null){
            this.remove(currentPanel);
        }
        this.add(currentPanel = new EntitiesSettingPanel(entities,this.getWidth(), this.getHeight(),frame));
        this.validate();
        this.repaint();
    }
}
