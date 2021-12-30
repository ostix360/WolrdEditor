package fr.ostix.worldCreator.core.ressources;

import fr.ostix.worldCreator.graphics.textures.*;

public class TextureResources {
    private final String name;
    private final String path;
    private final TextureProperties textureProperties;

    public TextureResources(String name, String path, TextureProperties textureProperties) {
        this.name = name;
        this.path = path;
        this.textureProperties = textureProperties;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public TextureProperties getTextureProperties() {
        return textureProperties;
    }
}
