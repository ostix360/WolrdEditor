package fr.ostix.worldCreator.frame;

import fr.ostix.worldCreator.core.resourcesLoader.*;
import fr.ostix.worldCreator.creator.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.entity.component.*;
import fr.ostix.worldCreator.frame.editor.*;
import fr.ostix.worldCreator.graphics.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.world.*;
import org.lwjgl.opengl.awt.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

public class MainFrame {
    public static final String TITLE = "World Creator";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private final JFrame frame;
    private JPanel settingsPanel;
    private JPanel mainPanel;
    private JPanel componentsPanel;
    private GLCanvas canvas;
    private final Semaphore signalTerminate = new Semaphore(0);
    private final Semaphore signalTerminated = new Semaphore(0);
    public static final Font VSMALL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font MEDIUM_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Workspace workspace;
    private final World world;
    private static final List<Entity> entities = new ArrayList<>();

    private TerrainSettingPanel terrainSettingPanel;
    private MainSettingsPanel mainSettings;
    private SettingPanel settingPanel;


    public MainFrame(MasterRenderer renderer, Workspace workspace, Camera cam, World world) {

        this.world = world;
        this.workspace = workspace;
        frame = new JFrame(TITLE) {
            @Override
            public void dispose() {
                // request the cleanup
                signalTerminate.release();
                try {
                    // wait until the thread is done with the cleanup
                    signalTerminated.acquire();
                } catch (InterruptedException ignored) {
                    System.err.println("Terminate without cleaning!");
                } finally {
                    super.dispose();
                }

            }
        };
        initFrame();
        initIcon();
        initMenuBar(workspace);
        initMainPanel();
        initInnerPanel(renderer, cam);
        frame.pack();
        frame.setVisible(true);
        frame.transferFocus();
        this.world.setFrame(this);
    }


    public Runnable getRenderRunnable() {
        return () -> {
            while (true) {
                try {
                    canvas.render();
                    if (signalTerminate.tryAcquire(10, TimeUnit.MILLISECONDS)) {
                        signalTerminated.release();
                        canvas.doDisposeCanvas();
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("Terminate without cleaning!");
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        };
    }

    private void initFrame() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.setResizable(false);
    }

    private void initIcon() {
        BufferedImage image;
        try {
            image = ImageIO.read(Objects.requireNonNull(MainFrame.class.getResourceAsStream("/icon.png")));
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMenuBar(Workspace workspace) {
        MenuBar menu = new MenuBar(this, workspace);
        frame.setJMenuBar(menu);
    }

    private void initMainPanel() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(700, 650));
        frame.add(this.mainPanel, gc);
        gc.gridx = 2;
        settingsPanel = new JPanel();
        settingsPanel.setPreferredSize(new Dimension(420, 650));
        frame.add(this.settingsPanel, gc);
    }

    private void initInnerPanel(MasterRenderer renderer, Camera camera) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        mainSettings = new MainSettingsPanel(680, 180, this);
        mainSettings.setVisible(true);
        mainSettings.setEditPanel(EditionMode.TERRAIN);
        mainPanel.add(this.mainSettings, gc);
        gc.gridy = 1;
//        previewSettings = new PreviewSettingsPanel(580, 105, camera, workspace);
//        mainPanel.add(this.previewSettings, gc);
        GLData data = new GLData();
        data.samples = 2;
        //data.swapInterval = 0;
        data.majorVersion = 3;
        data.minorVersion = 3;
        data.debug = true;
        data.profile = GLData.Profile.CORE;
        canvas = new GLCanvas(data, renderer, camera, world);
        canvas.setPreferredSize(new Dimension(680, 450));
        gc.gridy = 2;
        mainPanel.add(this.canvas);


        settingPanel = new SettingPanel(this, 420, 640);
        settingsPanel.add(settingPanel);


    }

    public static void loadEntities(ResourcePackLoader rpl) throws Exception {
        File entitiesFiles = new File(Config.REPOSITORY_FOLDER + "/entities/data/");
        for (File currentFile : Objects.requireNonNull(entitiesFiles.listFiles())) {
            try (FileInputStream fos = new FileInputStream(currentFile); FileChannel fc = fos.getChannel()) {
                ByteBuffer buffer = ByteBuffer.allocate(48);
                int noOfBytesRead = fc.read(buffer);
                StringBuilder sb = new StringBuilder();
                while (noOfBytesRead != -1) {
                    buffer.flip();

                    while (buffer.hasRemaining()) {
                        sb.append((char) buffer.get());
                    }

                    buffer.clear();
                    noOfBytesRead = fc.read(buffer);
                }
                String content = sb.toString();
                String[] contents = content.split(";");
                Model m = ResourcePackLoader.getModelByName().get(contents[0]);
                if (m.getMeshModel() == null) {
                    Logger.err("The model of  " + contents[0] + " is null");
                }
                Entity e = new Entity(m, contents[0], contents[1]);
                LoadComponents.loadComponents(ResourcePackLoader.getComponentsByID().get(Integer.valueOf(contents[1])), e);
                if (e.getModel() == null) {
                    Logger.err("The model of  " + e + " is null");
                }
                entities.add(e);
            }
        }
    }

    public void notifyNewChunk(int x, int z, int xCoords, int zCoords) {
        World.addTerrain(x, z, xCoords, zCoords);
    }

    public World getWorld() {
        return world;
    }

    public void notifyChangeEditionMode(EditionMode editionMode) {
        world.setEditionMode(editionMode);
        mainSettings.setEditPanel(editionMode);
        this.frame.validate();
        this.frame.repaint();
    }

    public void notifySelectedTerrain(Terrain ter) {
        this.terrainSettingPanel.setTerrain(ter);
    }

    public void setTerrainSettingPanel(TerrainSettingPanel terrainSettingPanel) {
        this.terrainSettingPanel = terrainSettingPanel;
    }

    public List<Entity> getEntityLoaded() {
        return entities;
    }

    public void notifySelectedEntity(Entity entity) {
        settingPanel.initEntityPanel(entity);
    }
}
