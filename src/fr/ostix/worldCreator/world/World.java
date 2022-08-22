package fr.ostix.worldCreator.world;

import com.jme3.bullet.*;
import com.jme3.bullet.collision.*;
import fr.ostix.worldCreator.audio.*;
import fr.ostix.worldCreator.core.Timer;
import fr.ostix.worldCreator.core.*;
import fr.ostix.worldCreator.core.resourcesLoader.*;
import fr.ostix.worldCreator.core.resourcesProcessor.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.frame.*;
import fr.ostix.worldCreator.graphics.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.main.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.terrain.texture.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.water.*;
import fr.ostix.worldCreator.world.chunk.*;
import org.joml.*;

import java.awt.event.*;
import java.lang.*;
import java.lang.Math;
import java.util.*;
import java.util.concurrent.*;

import static org.lwjgl.glfw.GLFW.*;

public class World {
    private static Map<Vector2f, Chunk> terrains = null;
    private final List<WaterTile> waterTiles = new ArrayList<>();

    private final List<Entity> entitiesPicked = new ArrayList<>();
    private static final List<Entity> entities = new CopyOnWriteArrayList<>();
    private static final List<Entity> aabbs = new ArrayList<>();
    private final MousePicker mousePicker;
    private EditionMode editionMode = EditionMode.TERRAIN;
    private MainFrame frame;
    private SoundListener listener;
    private Camera cam;
    private Terrain terrainPicked;
    private Entity entityPicked;
    private MasterRenderer renderer;
    private boolean canAddEntity;
    private int timer = 0;

    private Entity p;

    private final BulletAppState physics = new BulletAppState();
    ;
    private static ChunkHandler chunkHandler;
    private boolean terIsMissing = true;
    private boolean threadStart = false;

    private static final Color FILTER = new Color(0.3f, 0.3f, 0.3f);
    private boolean chunkRunning = false;

    public World(MasterRenderer renderer, Map<Vector2f, Chunk> terrains, Camera cam) {
        World.terrains = terrains;
        this.renderer = renderer;
        this.cam = cam;
        chunkHandler = new ChunkHandler(cam, World.terrains, entities, this);
        this.mousePicker = new MousePicker(MasterRenderer.getProjectionMatrix(), cam, terrains);
        physics.startPhysics();
    }


//    public static Entity addAABB(Vector3 bodyPosition, Vector3 size) {
//        Entity entity = new Entity(Config.CUBE, Maths.toVector3f(bodyPosition), new Vector3f(), 1, -1);
//        entity.setScale(Maths.toVector3f(size));
//        aabbs.add(entity);
//        return entity;
//    }

    public static void doAABBToRender() {
        chunkHandler.addAllEntities(aabbs);
        aabbs.clear();
    }

    public static Vector3f getFilter() {
        return FILTER.getVec3f();
    }

    public void remove(Entity e) {
        if (entities.remove(e)) {
            chunkHandler.remove(e);
            physics.getPhysicsSpace().remove(e);
        }
    }

    public void setEditionMode(EditionMode editionMode) {
        if (editionMode.equals(this.editionMode)) {
            return;
        }
        this.editionMode = editionMode;
        if (editionMode.equals(EditionMode.TERRAIN)) {
            if (entityPicked != null) {
                entityPicked.setPicking(false);
                entityPicked = null;
            }
        } else if (editionMode.equals(EditionMode.ENTITY_ADD) ||
                editionMode.equals(EditionMode.ENTITY_PICK)) {
            if (terrainPicked != null) {
                terrainPicked.setPicking(false);
                terrainPicked = null;
            }

            if (editionMode.equals(EditionMode.ENTITY_PICK)) {
                chunkHandler.remove(entityPicked);
                entityPicked = null;
            } else {
                if (entityPicked != null) {
                    entityPicked = entityPicked.clone();
                    if (entityPicked.getModel() == null) {
                        Logger.err("The model of  " + entityPicked + " is null");
                    }
                    chunkHandler.addEntity(mousePicker.getCurrentChunk(), entityPicked);
                }
            }
        }
    }

    public void update() {
        if (!ResourcePackLoader.isLoaded()) {
            return;
        }
        renderer.setEntities(entities);

//        terrains.clear();
//        terrains.putAll(chunkHandler.getChunkMap());


        for (Entity entity : entities) {
            entity.update();
        }


        if (!canAddEntity) {
            timer++;
            if (timer >= 30) {
                canAddEntity = true;
                timer = 0;
            }
        }
        mousePicker.update();
        if (editionMode.equals(EditionMode.TERRAIN)) {
            Terrain t = mousePicker.getCurrentTerrain();
            if (t != null) {
                if (Input.keysMouse[GLFW_MOUSE_BUTTON_1]) {
                    frame.notifySelectedTerrain(terrainPicked = t);
                }
                t.setPicking(true);
            }
            if (terrainPicked != null) {
                terrainPicked.setPicking(true);
            }
        } else if (editionMode.equals(EditionMode.ENTITY_ADD)) {
            if (mousePicker.getCurrentTerrainPoint() != null && entityPicked != null) {
                entityPicked.setPosition(mousePicker.getCurrentTerrainPoint());
                if (Input.keys[KeyEvent.VK_R]) {
                    entityPicked.increaseRotation(new Vector3f(0, 1, 0));
                }
                entityPicked.getScale().add(new Vector3f(GLCanvas.mouseDWheel * entityPicked.getScale().y() / 25f));
                if (Input.keysMouse[GLFW_MOUSE_BUTTON_1] && canAddEntity) {
                    Entity clone = entityPicked.clone();

                    entities.add(clone);
                    chunkHandler.addEntity(mousePicker.getCurrentChunk(), clone);
                    //collisionSystem.spawnBody(clone);
                    this.physics.getPhysicsSpace().add(clone);

                    canAddEntity = false;
                }
            }
        } else if (editionMode.equals(EditionMode.ENTITY_PICK)) {
            if (mousePicker.getCurrentRay() != null) {
                List<PhysicsRayTestResult> results = physics.getPhysicsSpace().rayTest(cam.getPosition(), mousePicker.getRayEndPoint());
                if (results.size() > 0) {
                    Entity e = (Entity) results.get(0).getCollisionObject().getUserObject(); //TODO collisionSystem.findEntityInRay(cam, mousePicker.getCurrentRay());
                    if (e != null) {
                        if (Input.keysMouse[GLFW_MOUSE_BUTTON_1]) {
                            if (Input.keys[KeyEvent.VK_CONTROL]){
                                entitiesPicked.add(e);
                                frame.notifySelectedEntities(entitiesPicked);
                            }else{
                                entitiesPicked.clear();
                                frame.notifySelectedEntity(entityPicked = e);
                                entitiesPicked.add(e);
                            }
                            e.setPicking(true);
                        }
                        e.setPicking(true);
                    }

                }
            }
            if (entityPicked != null) {
                entityPicked.setPicking(true);
                entitiesPicked.forEach(entity -> entity.setPicking(true));
            }
        }
        if (ResourcePackLoader.isLoaded()) {
            updateChunks();
        }

    }

    private int t = 0;
    public void updateChunks() { // Update all the chunks
        notifyTerrainResourcesMissing();
        if (!terIsMissing) {
            t++;
            if (t > 20) {
                chunkHandler.run();
                t = 0;
            }
        }
    }

    public void notifyTerrainResourcesMissing() {
        Thread t = new Thread(() -> {
            threadStart = true;
            while (Config.TERRAIN_DEFAULT_PACK == null || Config.TERRAIN_DEFAULT_PACK.getBackgroundTexture() == null) {

                TextureLoaderRequest backgroundTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/grassy2.png"));
                TextureLoaderRequest rTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/mud.png"));
                TextureLoaderRequest gTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/grassFlowers.png"));
                TextureLoaderRequest bTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/path.png"));
                TextureLoaderRequest blendRequest = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/blendMap.png"));
                GLRequestProcessor.sendRequest(backgroundTexture, rTexture, gTexture, bTexture, blendRequest);


                Timer.waitForRequest(blendRequest);
                Timer.waitForRequest(bTexture);
                Timer.waitForRequest(gTexture);
                Timer.waitForRequest(rTexture);
                Timer.waitForRequest(backgroundTexture);

                TerrainTexture backt = new TerrainTexture(backgroundTexture.getTexture().getId());
                TerrainTexture rt = new TerrainTexture(rTexture.getTexture().getId());
                TerrainTexture gt = new TerrainTexture(gTexture.getTexture().getId());
                TerrainTexture bt = new TerrainTexture(bTexture.getTexture().getId());
                TerrainTexture blendt = new TerrainTexture(blendRequest.getTexture().getId());
                blendt.setName("blendMap");
                backt.setName("grassy2");
                rt.setName("mud");
                gt.setName("grassFlowers");
                bt.setName("path");

                TerrainTexturePack tp = new TerrainTexturePack(backt, rt, gt, bt);
                blendt.setName("blendMap");
                Config.BLEND_MAP = blendt;
                Config.TERRAIN_DEFAULT_PACK = tp;

                //new ErrorPopUp("The Default terrain texture pack is null try to chang it... (merci de cliquer sur la croix si cela se reproduit trop de fois cliquer sur close)");

            }
            terIsMissing = false;
            threadStart = false;
        });
        terIsMissing = Config.TERRAIN_DEFAULT_PACK == null || Config.TERRAIN_DEFAULT_PACK.getBackgroundTexture() == null;
        if (terIsMissing && !threadStart) {
            t.start();
        }
    }

    public static void addTerrain(int x, int z, int xCoords, int zCoords) {
        while (Config.TERRAIN_DEFAULT_PACK == null || Config.TERRAIN_DEFAULT_PACK.getBackgroundTexture() == null) {
            TextureLoaderRequest backgroundTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/grassy2.png"));
            TextureLoaderRequest rTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/mud.png"));
            TextureLoaderRequest gTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/grassFlowers.png"));
            TextureLoaderRequest bTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/path.png"));
            TextureLoaderRequest blendRequest = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/blendMap.png"));
            GLRequestProcessor.sendRequest(backgroundTexture, rTexture, gTexture, bTexture, blendRequest);

            Timer.waitForRequest(blendRequest);
            Timer.waitForRequest(bTexture);
            Timer.waitForRequest(gTexture);
            Timer.waitForRequest(rTexture);
            Timer.waitForRequest(backgroundTexture);
            new ErrorPopUp("The Default terrain texture pack is null try to chang it... (merci de cliquer sur la croix si cela se reproduit trop de fois cliquer sur close)");

            TerrainTexture backt = new TerrainTexture(backgroundTexture.getTexture().getId());
            TerrainTexture rt = new TerrainTexture(rTexture.getTexture().getId());
            TerrainTexture gt = new TerrainTexture(gTexture.getTexture().getId());
            TerrainTexture bt = new TerrainTexture(bTexture.getTexture().getId());
            TerrainTexture blendt = new TerrainTexture(blendRequest.getTexture().getId());
            blendt.setName("blendMap");
            backt.setName("grassy2");
            rt.setName("mud");
            gt.setName("grassFlowers");
            bt.setName("path");

            TerrainTexturePack tp = new TerrainTexturePack(backt, rt, gt, bt);
            blendt.setName("blendMap");
            Config.BLEND_MAP = blendt;
            Config.TERRAIN_DEFAULT_PACK = tp;
        }
        long time = System.nanoTime();
        for (int x1 = xCoords; x1 < xCoords + x; x1++) {
            for (int z1 = zCoords; z1 < zCoords + z; z1++) {
                terrains.put(new Vector2f(x1, z1), new Chunk(x1, z1, new ArrayList<>()).setTerrain(new Terrain(x1, z1, new TerrainTexturePack(Config.TERRAIN_DEFAULT_PACK), new TerrainTexture(Config.BLEND_MAP), "default")));
            }
        }
        Logger.err("Terrain mesh generation took " + (System.nanoTime() - time));
    }

    public void cleanup() {
        physics.stopPhysics();
        ChunkHandler.stopChunkHandler();
    }

    public static float getTerrainHeight(float worldX, float worldZ) {
        int x = (int) Math.floor(worldX / Terrain.getSIZE());
        int z = (int) Math.floor(worldZ / Terrain.getSIZE());
        try {
            return terrains.get(new Vector2f(x, z)).getTerrain().getHeightOfTerrain(worldX, worldZ);
        } catch (Exception e) {
            // Logger.err("World doesn't exist in this coordinates xIndex : " + x + " ZIndex : " + z);
        }
        return 0;
    }

    public void setEntity(Entity entity) {
        if (entityPicked != null) entities.remove(entityPicked);
        entityPicked = entity;
        entities.add(entityPicked);
        if (entity.getModel().getMeshModel() == null) {
            Logger.err("The model of  " + entity + " is null");
        }
    }


    public void setFrame(MainFrame mainFrame) {
        this.frame = mainFrame;
    }

    public PhysicsSpace getPhysics() {
        return physics.getPhysicsSpace();
    }

    public void refreshCollisions() {
        //TODO
    }

    public ChunkHandler getChunkManager() {
        return chunkHandler;
    }

    public void addPlayer() {
        if (!entities.contains(p)){
            Model player = ResourcePackLoader.getModelByName().get("player");
            p = new Entity(player,new Vector3f(0),new Vector3f(0),0.5f,-1);
            entities.add(p);
        }
        p.setPosition(new Vector3f(cam.getPosition().x(),getTerrainHeight(cam.getPosition().x(),cam.getPosition().z()), cam.getPosition().z()));

    }
}
