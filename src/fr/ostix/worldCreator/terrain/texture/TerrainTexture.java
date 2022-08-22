package fr.ostix.worldCreator.terrain.texture;

import fr.ostix.worldCreator.core.resourcesProcessor.*;

import java.util.*;

public class TerrainTexture {

    private int textureID;
    private String name;
    private TextureLoaderRequest tlr;

    public TerrainTexture(int textureID) {
        this.textureID = textureID;
    }

    public TerrainTexture(TerrainTexture texture) {
        this.textureID = texture.getTextureID();
        this.name = texture.getName();
    }

    private TerrainTexture(String name, TextureLoaderRequest tlr) {

        this.name = name;
        this.tlr = tlr;
    }

    public static TerrainTexture load(String name,boolean isBlendMap) {
        String str = "";
        if (isBlendMap) {
            str = "blendMap/";
        }else{
            str = "pack/";
        }
        TextureLoaderRequest tlr = new TextureLoaderRequest("terrain/"+ str + name);
        GLRequestProcessor.sendRequest(tlr);
        return new TerrainTexture(name, tlr);
    }


    public void setTexture() {
        if (tlr != null) {
            if (tlr.isExecuted()) {
                this.textureID = tlr.getTexture().getId();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TerrainTexture)) return false;
        TerrainTexture that = (TerrainTexture) o;
        return getName().equals(that.getName());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public int getTextureID() {
        return textureID;
    }
}
