package fr.ostix.worldCreator.graphics.terrain;


import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.entity.component.light.*;
import fr.ostix.worldCreator.graphics.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.graphics.textures.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.terrain.texture.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.toolBox.OpenGL.*;
import fr.ostix.worldCreator.world.chunk.*;
import org.joml.*;
import org.lwjgl.opengl.*;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class TerrainRenderer {
    private final TerrainShader shader;

    public TerrainRenderer(TerrainShader terrainShader, Matrix4f projectionMatrix) {
        this.shader = terrainShader;
        shader.bind();
        shader.connectTerrainUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.unBind();
    }

    public void render(Map<Vector2f, Chunk> terrains, List<Light> lights, Camera cam, Color skyColor, Vector4f clipPlane) {
        prepare(lights, skyColor, cam, clipPlane);
        OpenGlUtils.goWireframe(false);
        // shader.loadShaderMapSpace(toShadowSpace);
       // terrains = Collections.synchronizedMap(new HashMap<>(terrains));

        for (Chunk chunk : terrains.values()) {
            Terrain ter = chunk.getTerrain();
            if (ter.getModel() == null || ter.getModel().getVAO() == null) {
                ter.setModel();
                continue;
            }
            if (clipPlane.equals(MasterRenderer.NO_CLIP)) {
                shader.canLoadFilter(ter.isPicking());
                ter.setPicking(false);
            }
            prepareTerrain(ter);
            loadModelMatrix(ter);
            glDrawElements(GL_TRIANGLES, ter.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            unbindTexturedModel();

        }


        shader.canLoadFilter(false);
    }


    private void prepare(List<Light> lights, Color skyColor, Camera cam, Vector4f clipPlane) {
        shader.bind();
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColour(skyColor);
        if (lights != null) shader.loadLight(lights);
        shader.loadViewMatrix(cam);
    }

    private void prepareTerrain(Terrain terrain) {
        MeshModel model = terrain.getModel();
        model.getVAO().bind(0, 1, 2);
        shader.loadSpecular(0, 1);
        bindTexture(terrain);
    }

    private void bindTexture(Terrain ter) {
        TerrainTexturePack texturePack = ter.getTexturePack();
        if (texturePack.getBackgroundTexture().getTextureID() == 0) {
            texturePack.getBackgroundTexture().setTexture();
        }
        if (texturePack.getrTexture().getTextureID() == 0) {
            texturePack.getrTexture().setTexture();
        }
        if (texturePack.getgTexture().getTextureID() == 0) {
            texturePack.getgTexture().setTexture();
        }
        if (texturePack.getbTexture().getTextureID() == 0) {
            texturePack.getbTexture().setTexture();
        }
        if (ter.getBlendMap().getTextureID() == 0) {
            ter.getBlendMap().setTexture();
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, ter.getBlendMap().getTextureID());
    }

    private void unbindTexturedModel() {
        Texture.unBindTexture();
        VAO.unbind(0, 1, 2);
    }

    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()),
                new Vector3f(0, 0, 0), new Vector3f(1));
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
