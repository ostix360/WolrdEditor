package fr.ostix.worldCreator.graphics.model;


import fr.ostix.worldCreator.graphics.textures.*;

public class Model {

    private final MeshModel meshModel;
    private final Texture texture;

    public Model(MeshModel meshModel, Texture texture) {
        this.meshModel = meshModel;
        this.texture = texture;
    }

    public MeshModel getMeshModel() {
        return meshModel;
    }

    public Texture getTexture() {
        return texture;
    }
}
