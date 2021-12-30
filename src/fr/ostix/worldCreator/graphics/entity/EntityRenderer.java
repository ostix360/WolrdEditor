package fr.ostix.worldCreator.graphics.entity;


import fr.ostix.worldCreator.*;
import fr.ostix.worldCreator.core.resourcesLoader.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.animated.animation.animatedModel.*;
import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.entity.component.light.*;
import fr.ostix.worldCreator.graphics.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.graphics.textures.*;
import fr.ostix.worldCreator.main.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.toolBox.OpenGL.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class EntityRenderer {

    ClassicShader shader;

    public EntityRenderer(ClassicShader shader, Matrix4f projectionMatrix) {

        OpenGlUtils.cullBackFaces(true);
        this.shader = shader;
        this.shader.bind();
        this.shader.loadProjectionMatrix(projectionMatrix);
        this.shader.connectTextureUnits();
        this.shader.unBind();
    }

    public void render(Map<Model, List<Entity>> entities, List<Light> lights, Camera cam, Color skyColor, Vector4f clipPlane) {
        prepare(lights, skyColor, cam, clipPlane);
        for (Model model : entities.keySet()) {
            OpenGlUtils.goWireframe(false);
            if (model.getMeshModel() == null) {
                Main.notifyNullModel(entities.get(model).get(0));
            }
            if (model instanceof AnimatedModel) {
                prepareAnimatedTexturedModel((AnimatedModel) model);
            } else {
                prepareTexturedModel(model);
            }
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                if (clipPlane.equals(MasterRenderer.NO_CLIP)){
                    shader.canLoadFilter(entity.isPicking());
                    entity.setPicking(false);
                }
                prepareInstance(entity);
                glDrawElements(GL_TRIANGLES, entity.getModel().getMeshModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            }

        }
        finish();
    }

    private void prepare(List<Light> lights, Color skyColor, Camera cam, Vector4f clipPlane) {
        shader.bind();
        if (lights != null) shader.loadLight(lights);
        shader.loadSkyColor(skyColor);
        shader.loadViewMatrix(cam);
        shader.clipPlane.loadVec4fToUniform(clipPlane);
        OpenGlUtils.goWireframe(false);
    }

    private void prepareAnimatedTexturedModel(AnimatedModel model) {
        MeshModel mesh = model.getMeshModel();
        mesh.getVAO().bind(0, 1, 2, 3, 4);
        shader.isAnimated.loadBooleanToUniform(true);
        shader.jointTransforms.loadMatrixArray(model.getJointTransforms());

        Texture texture = model.getTexture();
        shader.loadSpecular(texture.getReflectivity(), texture.getShineDamper());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        texture.bindTexture();
        shader.useSpecularMap.loadBooleanToUniform(texture.hasSpecularMap());
        shader.numberOfRows.loadFloatToUniform(texture.getNumbersOfRows());
        if (texture.hasSpecularMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getSpecularMap());
        }
    }

    public void prepareInstance(Entity entity) {
        shader.loadTransformationMatrix(entity.getTransform().getTransformation());
        shader.offset.loadVector2fToUniform(new Vector2f(entity.getTextureXOffset(), entity.getTextureYOffset()));
    }

    public void prepareTexturedModel(Model model) {
        if (model.equals(Config.CUBE)) {
            OpenGlUtils.goWireframe(true);
        }

        MeshModel meshModel = model.getMeshModel();
        meshModel.getVAO().bind(0, 1, 2);
        shader.isAnimated.loadBooleanToUniform(false);

        Texture texture = model.getTexture();
        shader.loadSpecular(texture.getReflectivity(), texture.getShineDamper());
        shader.numberOfRows.loadFloatToUniform(texture.getNumbersOfRows());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        texture.bindTexture();
        shader.useSpecularMap.loadBooleanToUniform(texture.hasSpecularMap());
        if (texture.hasSpecularMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getSpecularMap());
        }
    }

    public void finish() {
        OpenGlUtils.cullBackFaces(true);
        VAO.unbind(0, 1, 2, 3, 4);
        Texture.unBindTexture();

    }


}
