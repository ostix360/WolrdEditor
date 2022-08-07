package fr.ostix.worldCreator.graphics;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.entity.component.light.*;
import fr.ostix.worldCreator.graphics.entity.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.graphics.skybox.*;
import fr.ostix.worldCreator.graphics.terrain.*;
import fr.ostix.worldCreator.graphics.water.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.toolBox.OpenGL.*;
import fr.ostix.worldCreator.water.*;
import fr.ostix.worldCreator.world.chunk.*;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.opengl.*;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class MasterRenderer {

    public static final float FOV = Math.toRadians(70f);
    public static final float NEAR = 0.01f;
    public static final float FAR = 1200;

    public static final Vector4f NO_CLIP = new Vector4f(0, 0, 0, 1);

    public static Color skyColor = new Color(0.5444f, 0.62f, 0.69f);
    private static final Vector3f SUN_DIR = new Vector3f(0, -1, 0);

    private EntityRenderer entityRenderer;
    private ClassicShader shader;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader;
    private SkyboxRenderer skyboxRenderer;

    private WaterFrameBuffers waterFbos;
    private WaterRenderer waterRenderer;

    private Map<Vector2f, Chunk> terrains;
    private List<Light> lights;
    private List<WaterTile> waterTiles;
    private static Matrix4f projectionMatrix;

    private final Map<Model, List<Entity>> entities = new HashMap<>();

    public MasterRenderer() {
        projectionMatrix = createProjectionMatrix();
    }


    public void init() {
        OpenGlUtils.cullBackFaces(true);
        this.shader = new ClassicShader();
        this.terrainShader = new TerrainShader();
        this.entityRenderer = new EntityRenderer(shader, projectionMatrix);
        this.terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        this.skyboxRenderer = new SkyboxRenderer(projectionMatrix);
        this.waterFbos = new WaterFrameBuffers();
        this.waterRenderer = new WaterRenderer(waterFbos);
    }

    public void initFrame() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(skyColor.getRed(), skyColor.getGreen(), skyColor.getBlue(), skyColor.getAlpha());
        createProjectionMatrix();
    }


    private void processEntity(Entity e) {
        Model model = e.getModel();
        if (model == null) {
             Logger.err("The model of " + e + " is null");
        }
        List<Entity> batch = entities.get(model);
        if (batch != null) {
            batch.add(e);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(e);
            entities.put(model, newBatch);
        }
    }

    public void setEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            processEntity(entity);
        }
    }

    public void initToRender(List<WaterTile> waterTiles, Map<Vector2f, Chunk> terrains, List<Light> lights) {

        this.terrains = terrains;
        this.lights = lights;
        this.waterTiles = waterTiles;
    }

    public void renderScene(Camera camera) {

        if (!waterTiles.isEmpty()) {
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            renderWaterRefractionPass(lights, camera);
            renderWaterReflectionPass(lights, waterTiles, camera);
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        }
        render(lights, waterTiles, camera);
    }

    private void renderWaterReflectionPass(List<Light> lights, List<WaterTile> waterTiles, Camera camera) {
        waterFbos.bindReflectionFrameBuffer();
        this.initFrame();
        camera.reflect(waterTiles.get(0).getHeight());
        skyboxRenderer.render(camera, skyColor);
        entityRenderer.render(entities, lights, camera, skyColor, new Vector4f(0, 1, 0, -waterTiles.get(0).getHeight() - 0.001f));
        //terrainRenderer.render(terrains, lights,camera, skyColor,new Vector4f(0, 1, 0, -waterTiles.get(0).getHeight()-0.1f));
        waterFbos.unbindCurrentFrameBuffer();
        camera.reflect(waterTiles.get(0).getHeight());
    }

    private void renderWaterRefractionPass(List<Light> lights, Camera camera) {
        waterFbos.bindRefractionFrameBuffer();
        this.initFrame();
        entityRenderer.render(entities, lights, camera, skyColor, new Vector4f(0, -1, 0, -10f));
        terrainRenderer.render(terrains, lights, camera, skyColor, new Vector4f(0, -1, 0, -10f));
        waterFbos.unbindCurrentFrameBuffer();
    }

    private void render(List<Light> lights, List<WaterTile> waterTiles, Camera cam) {
        this.initFrame();


        skyboxRenderer.render(cam, skyColor);
        skyboxRenderer.update();

        entityRenderer.render(entities, lights, cam, skyColor, NO_CLIP);
        shader.unBind();

        terrainRenderer.render(terrains, lights, cam, skyColor, NO_CLIP);
        terrainShader.unBind();

        waterRenderer.render(waterTiles, cam, SUN_DIR);

        entities.clear();
    }

    public void cleanUp() {
        this.waterRenderer.cleanUp();
        this.skyboxRenderer.cleanUp();
        this.terrainShader.cleanUp();
        this.shader.cleanUp();
        glDisable(GL_BLEND);
    }

    private Matrix4f createProjectionMatrix() {
        projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
        float aspectRatio = (float) DisplayManager.getWidth() / (float) DisplayManager.getHeight();
        projectionMatrix.perspective(FOV, aspectRatio, NEAR, FAR);
        return projectionMatrix;
//        projectionMatrix = new Matrix4f();
//        float aspectRatio = (float) DisplayManager.getWidth() / (float) DisplayManager.getHeight();
//        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
//        float x_scale = y_scale / aspectRatio;
//        float frustum_length = far - near;
//
//        projectionMatrix.set(0,0,x_scale);
//        projectionMatrix.set(1,1,y_scale);
//        projectionMatrix.set(2,2,-((far + near) / frustum_length));
//        projectionMatrix.set(2,3, -1);
//        projectionMatrix.set(3,2, -((2 * near * far) / frustum_length));
//        projectionMatrix.set(3,3, 0);
    }

    public static Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
