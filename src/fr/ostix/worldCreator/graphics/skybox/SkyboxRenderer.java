package fr.ostix.worldCreator.graphics.skybox;

import fr.ostix.worldCreator.core.loader.*;
import fr.ostix.worldCreator.entity.camera.Camera;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.toolBox.Color;
import fr.ostix.worldCreator.toolBox.OpenGL.OpenGlUtils;
import fr.ostix.worldCreator.toolBox.OpenGL.VAO;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL13;

import static org.lwjgl.opengl.GL11.*;

public class SkyboxRenderer {

    private static final String[] FILES_DAY = {"day/right", "day/left", "day/top", "day/bottom", "day/back", "day/front"};
    private static final String[] FILES_NIGHT = {"night/right", "night/left", "night/top", "night/bottom", "night/back", "night/front"};

    private final MeshModel model;
    private final int dayTexture;
    private final int nightTexture;
    private static final float SIZE = 400f;
    private static final float[] VERTICES = {
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
    };
    private int texture1;
    private final SkyboxShader shader;
    private int texture2;
    private float blendFactor;
    private float time = 0;

    public SkyboxRenderer(Matrix4f projectionMatrix) {
        Loader loader = Loader.INSTANCE;
        model = loader.loadToVAO(VERTICES, 3);
        dayTexture = loader.loadCubMap(FILES_DAY);
        nightTexture = loader.loadCubMap(FILES_NIGHT);
        shader = new SkyboxShader();
        shader.bind();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.unBind();
    }

    private void bindTextures() {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
        shader.loadBlendFactor(blendFactor);
    }

//    private void bindTextures() {
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
//        GL13.glActiveTexture(GL13.GL_TEXTURE1);
//        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
//        shader.loadBlendFactor(blendFactor);
//    }

    public void update() {
        time += 1 / 60f * 500;
        time %= 24000;
        if (time >= 0 && time < 5000) {
            texture1 = nightTexture;
            texture2 = nightTexture;
            blendFactor = (time - 0) / (5000 - 0);
        } else if (time >= 5000 && time < 8000) {
            texture1 = nightTexture;
            texture2 = dayTexture;
            blendFactor = (time - 5000) / (8000 - 5000);
        } else if (time >= 8000 && time < 21000) {
            texture1 = dayTexture;
            texture2 = dayTexture;
            blendFactor = (time - 8000) / (21000 - 8000);
        } else {
            texture1 = dayTexture;
            texture2 = nightTexture;
            blendFactor = (time - 21000) / (24000 - 21000);
        }
    }

    public void render(Camera cam, Color fog) {
        OpenGlUtils.goWireframe(false);
        OpenGlUtils.enableDepthTesting(false);
        glDepthMask(false);
        shader.bind();
        shader.loadViewMatrix(cam);
        shader.loadFogColor(fog);
        model.getVAO().bind(0);
        bindTextures();
        glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
        VAO.unbind(0);
        shader.unBind();
        glDepthMask(true);
        OpenGlUtils.enableDepthTesting(true);
    }

    public void cleanUp() {
        this.shader.cleanUp();
    }
}
