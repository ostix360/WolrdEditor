package fr.ostix.worldCreator.core.ressources;

public class ModelResources {
    private final String name;
    private final String path;
    private final String texture;
    private final boolean canAnimated;

    public ModelResources(String name, String path, String texture, boolean canAnimated) {
        this.name = name;
        this.path = path;
        this.texture = texture;
        this.canAnimated = canAnimated;
    }

    public boolean canAnimated() {
        return canAnimated;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getTexture() {
        return texture;
    }
}
