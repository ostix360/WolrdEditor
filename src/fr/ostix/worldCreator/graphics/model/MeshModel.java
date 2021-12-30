package fr.ostix.worldCreator.graphics.model;


import fr.ostix.worldCreator.toolBox.OpenGL.*;

public class MeshModel {

    private final VAO vaoID;

    public MeshModel(VAO vaoID) {
        this.vaoID = vaoID;
    }

    public VAO getVAO() {
        return vaoID;
    }

    public int getVertexCount() {
        return vaoID.getVertexCount();
    }
}
