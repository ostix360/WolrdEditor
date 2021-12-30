package fr.ostix.worldCreator.graphics.particles;

import fr.ostix.worldCreator.core.loader.*;
import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.toolBox.OpenGL.*;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;

import java.lang.Math;

public class ParticleRenderer {

    private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
    private static final int MAX_INSTANCES = 10000;
    private static final int INSTANCE_DATA_LENGTH = 21;

    public static final FloatBuffer BUFFER = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

    private final MeshModel quad;
    private final ParticleShader shader;

    private Loader loader;
    private final VBO vbo;
    private int pointer = 0;

    protected ParticleRenderer(Matrix4f projectionMatrix) {
        loader = Loader.INSTANCE;
        quad = loader.loadToVAO(VERTICES, 2);
        this.vbo = quad.getVAO().createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
        loader.addInstance(quad.getVAO(), this.vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
        loader.addInstance(quad.getVAO(), this.vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
        loader.addInstance(quad.getVAO(), this.vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
        loader.addInstance(quad.getVAO(), this.vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);
        loader.addInstance(quad.getVAO(), this.vbo, 5, 4, INSTANCE_DATA_LENGTH, 16);
        loader.addInstance(quad.getVAO(), this.vbo, 6, 1, INSTANCE_DATA_LENGTH, 20);
        shader = new ParticleShader();
        shader.bind();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.unBind();
    }

    protected void render(Map<ParticleTexture, List<Particle>> particles, Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        prepare();
        for (ParticleTexture texture : particles.keySet()) {
            bindTexture(texture);
            List<Particle> particlesList = particles.get(texture);
            pointer = 0;
            float[] vboData = new float[particlesList.size() * INSTANCE_DATA_LENGTH];
            for (Particle p : particlesList) {
                updateModelViewMatrix(p.getPosition(), p.getRotation(), p.getScale(), viewMatrix, vboData);
                updateTexCoordsInfo(p, vboData);
            }
            loader.updateVBO(vbo, vboData, BUFFER);
            GL32.glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), particlesList.size());
        }
        finish();
    }

    private void prepare() {
        glEnable(GL_BLEND);
        glDepthMask(false);
        shader.bind();
        quad.getVAO().bind(0,1,2,3,4,5,6);

    }

    private void bindTexture(ParticleTexture texture) {
        if (texture.isAdditive()) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        } else {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
        shader.loadNumberOfRows(texture.getNumberOfRows());
    }

    private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix, float[] vboData) {
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.translate(position);
        modelMatrix.m00(viewMatrix.m00());
        modelMatrix.m01(viewMatrix.m10());
        modelMatrix.m02(viewMatrix.m20());
        modelMatrix.m10(viewMatrix.m01());
        modelMatrix.m11(viewMatrix.m11());
        modelMatrix.m12(viewMatrix.m21());
        modelMatrix.m20(viewMatrix.m02());
        modelMatrix.m21(viewMatrix.m12());
        modelMatrix.m22(viewMatrix.m22());
        modelMatrix.rotate((float) Math.toRadians(rotation), new Vector3f(0, 0, 1));
        modelMatrix.scale(new Vector3f(scale, scale, scale));
        Matrix4f modelViewMatrix = new Matrix4f(viewMatrix).mul(modelMatrix);
        storeMatrixData(modelViewMatrix, vboData);
    }

    private void storeMatrixData(Matrix4f matrix, float[] vboData) {
        vboData[pointer++] = matrix.m00();
        vboData[pointer++] = matrix.m01();
        vboData[pointer++] = matrix.m02();
        vboData[pointer++] = matrix.m03();
        vboData[pointer++] = matrix.m10();
        vboData[pointer++] = matrix.m11();
        vboData[pointer++] = matrix.m12();
        vboData[pointer++] = matrix.m13();
        vboData[pointer++] = matrix.m20();
        vboData[pointer++] = matrix.m21();
        vboData[pointer++] = matrix.m22();
        vboData[pointer++] = matrix.m23();
        vboData[pointer++] = matrix.m30();
        vboData[pointer++] = matrix.m31();
        vboData[pointer++] = matrix.m32();
        vboData[pointer++] = matrix.m33();
    }

    private void updateTexCoordsInfo(Particle p, float[] data) {
        data[pointer++] = p.getOffsets1().x();
        data[pointer++] = p.getOffsets1().y();
        data[pointer++] = p.getOffsets2().x();
        data[pointer++] = p.getOffsets2().y();
        data[pointer++] = p.getBlend();
    }

    private void finish() {
        glDepthMask(true);
        glDisable(GL_BLEND);
        VAO.unbind(0,1,2,3,4,5,6);
        shader.unBind();
    }

    protected void cleanUp() {
        shader.cleanUp();
    }

}
