package fr.ostix.worldCreator.toolBox.OpenGL;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class VAO {
    public final int id;
    private final List<VBO> VBOs = new ArrayList<>();
    private int vertexCount;
    private float[] position;
    private int[] indices;

    private VAO(int id) {
        this.id = id;
    }

    public static VAO createVAO() {
        int id = GL30.glGenVertexArrays();
        return new VAO(id);
    }
    public VBO createEmptyVBO(int count) {
        VBO vbo = VBO.createEmptyVBO(count);
        VBOs.add(vbo);
        return vbo;
    }

    public void storeIndicesInVAO(int[] indices) {
        VBO vbo = VBO.createVBO();
        VBOs.add(vbo);
        vbo.storeIndicesDataInAttributeList(indices);
        this.vertexCount = indices.length;
        this.indices = indices;
    }
    public void storePositionInAttributeList(int attrib, int dataSize, float[] position) {
        VBO vbo = VBO.createVBO();
        VBOs.add(vbo);
        vbo.storeDataInAttributeList(attrib, dataSize, position);
        this.position = position;
    }

    public void storeDataInAttributeList(int attrib, int dataSize, float[] data) {
        VBO vbo = VBO.createVBO();
        VBOs.add(vbo);
        vbo.storeDataInAttributeList(attrib, dataSize, data);
    }


    public void storeIntDataInAttributeList(int attrib, int dataSize, int[] data) {
        VBO vbo = VBO.createVBO();
        VBOs.add(vbo);
        vbo.storeIntDataInAttributeList(attrib, dataSize, data);
    }

    public void bind(int... attributes) {
        bind();
        for (int i : attributes) {
            GL20.glEnableVertexAttribArray(i);
        }
    }

    public static void unbind(int... attributes) {
        for (int i : attributes) {
            GL20.glDisableVertexAttribArray(i);
        }
        unbind();
    }

    public void cleanUP() {
        GL30.glDeleteVertexArrays(id);
        for (VBO vbo : VBOs) {
            vbo.delete();
        }
        VBOs.clear();
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void bind() {
        GL30.glBindVertexArray(id);
    }

    private static void unbind() {
        GL30.glBindVertexArray(0);
    }

    public float[] getPosition() {
        return position;
    }

    public int[] getIndices() {
        return indices;
    }

    public void addInstance(VBO vbo, int attrib, int dataSize, int instanceDataLength, int offset) {
        this.bind();
        vbo.addInstance(attrib,dataSize,instanceDataLength,offset);
        VAO.unbind();
    }

}
