package fr.ostix.worldCreator.terrain.texture;

import fr.ostix.worldCreator.core.exporter.*;
import fr.ostix.worldCreator.core.resourcesProcessor.*;

import java.io.*;
import java.nio.channels.*;

public class TerrainTexturePack {
    private final TerrainTexture backgroundTexture;
    private final TerrainTexture rTexture;
    private final TerrainTexture gTexture;
    private final TerrainTexture bTexture;

    public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture rTexture,
                              TerrainTexture gTexture, TerrainTexture bTexture) {
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
        this.backgroundTexture.setName("grassy2");
        this.rTexture.setName("mud");
        this.gTexture.setName("grassFlowers");
        this.bTexture.setName("path");

    }

    public TerrainTexturePack(TerrainTexturePack terrainDefaultPack) {
        this.backgroundTexture = new TerrainTexture(terrainDefaultPack.getBackgroundTexture());
        this.rTexture = new TerrainTexture(terrainDefaultPack.getrTexture());
        this.gTexture = new TerrainTexture(terrainDefaultPack.getgTexture());
        this.bTexture = new TerrainTexture(terrainDefaultPack.getbTexture());
    }

    public static TerrainTexturePack load(String line) {
        String[] values = line.split(";");
        TerrainTexture back = TerrainTexture.load(values[0],false);
        TerrainTexture r = TerrainTexture.load(values[1],false);
        TerrainTexture g = TerrainTexture.load(values[2],false);
        TerrainTexture b = TerrainTexture.load(values[3],false);
        return new TerrainTexturePack(back,r,g,b);
    }

    public TerrainTexture getBackgroundTexture() {
        return backgroundTexture;
    }

    public TerrainTexture getrTexture() {
        return rTexture;
    }

    public TerrainTexture getgTexture() {
        return gTexture;
    }

    public TerrainTexture getbTexture() {
        return bTexture;
    }

    public void export(FileChannel fc) throws IOException {
        String back = getBackgroundTexture().getName().replaceAll(".png","");
        String r = getrTexture().getName().replaceAll(".png","");
        String g = getgTexture().getName().replaceAll(".png","");
        String b = getbTexture().getName().replaceAll(".png","");
        fc.write(DataTransformer.lineBuffer(back + ";" + r + ";" + g + ";" + b));
    }
}
