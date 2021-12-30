package fr.ostix.worldCreator.frame.editor;

import fr.ostix.worldCreator.frame.*;
import fr.ostix.worldCreator.graphics.textures.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.terrain.texture.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class TerrainSettingPanel extends JPanel {

    private final MainFrame mainFrame;
    private Terrain ter;
    private TerrainTexturePack pack;
    private TerrainTexture r;
    private TerrainTexture g;
    private TerrainTexture b;
    private TerrainTexture blackTexture;
    private TerrainTexture blendMap;

    public TerrainSettingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setup();
        this.mainFrame.setTerrainSettingPanel(this);
    }

    private void setup() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = 2;
        gc.gridx = 0;
        gc.gridy = 0;
        addRButton(gc);
        gc.gridx = 1;
        addGButton(gc);
        gc.gridx = 2;
        addBButton(gc);
        gc.gridy = 1;
        gc.gridx = 0;
        addHeightMapButton(gc);
        gc.gridx = 1;
        addBackButton(gc);
        gc.gridx = 2;
        addBlendButton(gc);

    }

    private void addHeightMapButton(GridBagConstraints gc) {
        final JButton btn = new JButton("Height Map");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ter != null) {
                    File tex = new TextureChooserScreen("heightMap").getChosen();
                    if(tex!= null) {
                        ter.regenerateTerrain(tex.getName().replaceAll(".png", ""));
                    }
                }
            }
        });
        this.add(btn,gc);
    }

    private void addBlendButton(GridBagConstraints gc) {
        final JButton btn = new JButton("blend Texture");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (blendMap != null) {
                    TextureLoader tex = new TextureChooserScreen("blendMap").getTextureChosen();
                    if(tex!= null) {
                        blendMap.setTextureID(tex.getId());
                        blendMap.setName(tex.getFile().getName());
                    }
                }
            }
        });
        this.add(btn,gc);
    }

    private void addBackButton(GridBagConstraints gc) {
        final JButton btn = new JButton("Black Texture");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (blackTexture != null) {
                    TextureLoader tex = new TextureChooserScreen("pack").getTextureChosen();
                    if(tex!= null) {
                        blackTexture.setTextureID(tex.getId());
                        blackTexture.setName(tex.getFile().getName());
                    }
                }
            }
        });
        this.add(btn,gc);
    }

    private void addRButton(GridBagConstraints gc) {
        final JButton btn = new JButton("R Texture");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (r != null) {
                    TextureLoader tex = new TextureChooserScreen("pack").getTextureChosen();
                    if(tex!= null) {
                        r.setTextureID(tex.getId());
                        r.setName(tex.getFile().getName());
                    }
                }
            }
        });
        this.add(btn,gc);
    }

    private void addGButton(GridBagConstraints gc) {
        final JButton btn = new JButton("G Texture");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (g != null) {
                    TextureLoader tex = new TextureChooserScreen("pack").getTextureChosen();
                    if(tex!= null) {
                        g.setTextureID(tex.getId());
                        g.setName(tex.getFile().getName());
                    }
                }
            }
        });
        this.add(btn,gc);
    }

    private void addBButton(GridBagConstraints gc) {
        final JButton btn = new JButton("B Texture");
        btn.setPreferredSize(new Dimension(200,50));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (b != null) {
                    TextureLoader tex = new TextureChooserScreen("pack").getTextureChosen();
                    if(tex!= null) {
                        b.setTextureID(tex.getId());
                        b.setName(tex.getFile().getName());
                    }
                }
            }
        });
        this.add(btn,gc);
    }

    public void setTerrain(Terrain ter) {
        this.ter = ter;
        this.pack = ter.getTexturePack();
        this.r = pack.getrTexture();
        this.g = pack.getgTexture();
        this.b = pack.getbTexture();
        this.blackTexture = pack.getBackgroundTexture();
        this.blendMap = ter.getBlendMap();
    }
}
