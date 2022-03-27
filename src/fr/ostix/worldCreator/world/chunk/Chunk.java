package fr.ostix.worldCreator.world.chunk;

import fr.ostix.worldCreator.core.exporter.*;
import fr.ostix.worldCreator.core.resourcesLoader.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.terrain.texture.*;
import fr.ostix.worldCreator.toolBox.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public class Chunk {

    private List<Entity> entities;
    private Terrain terrain;
    private final int x;
    private final int z;

    public Chunk(int x, int z,List<Entity> entities) {
        this.entities = entities;
        this.x = x;
        this.z = z;
    }

    public void addEntity(Entity e) {
        this.entities.add(e);
    }

    public void removePart(Entity e) {
        this.entities.remove(e);
    }

    public void export(FileChannel fc) throws IOException {
        fc.write(DataTransformer.lineBuffer("CHUNK " + x + ";" + z));
        exportTerrain(fc);
        exportEntities(fc);

    }

    public boolean isEmpty() {
        if (this.entities.isEmpty()) {
                return true;
        }

        return false;
    }

    private void exportEntities(FileChannel fc) throws IOException {
        fc.write(DataTransformer.lineBuffer("ENTITIES"));
        for (Entity e : this.entities) {
            String entityContent = e.toString() +";"+ e.getId() +";"+e.getComponent();
            fc.write(DataTransformer.lineBuffer(entityContent));
            e.getTransform().export(fc);
        }
    }

    public Chunk setTerrain(Terrain t) {
        this.terrain = t;
        return this;
    }

    private void exportTerrain(FileChannel fc) throws IOException {
        float x = terrain.getX() / Terrain.getSIZE();
        float z = terrain.getZ() / Terrain.getSIZE();
        fc.write(DataTransformer.lineBuffer(x + ";" + z));
        terrain.getTexturePack().export(fc);
        String blendMap = terrain.getBlendMap().getName().replaceAll(".png","");
        String heightMap = terrain.getHeightMap();
        fc.write(DataTransformer.lineBuffer(blendMap + ";" + heightMap));
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public static Chunk load(String content, int x, int z) {
        Terrain t = importTerrain(content);
        List<Entity> entities = importEntities(content);
        return new Chunk(x,z,entities).setTerrain(t);
    }

    private static List<Entity> importEntities(String content) {
        String[] lines = content.split("\n");
        List<Entity> entities = new ArrayList<>();
        int index = 4;
//        while (!lines[index].equals("ENTITIES")){
//            index++;
//        }
//        index++;
        while (index < lines.length) {
            String[] values = lines[index++].split(";");
            String entityName = values[0];
            int id = Integer.parseInt(values[1]);
            int component = Integer.parseInt(values[2]);
            Model m = ResourcePackLoader.getModelByName().get(entityName);
            Entity e = new Entity(m, entityName, String.valueOf(component), id);
            LoadComponents.loadComponents(ResourcePackLoader.getComponentsByID().get(component), e);
            if (e.getModel() == null) {
                Logger.err("The model of  " + e + " is null");
            }
            e.setTransform(Transform.load(lines[index++]));
            entities.add(e);
        }
        return entities;
    }

    private static Terrain importTerrain(String content) {
        String[] lines = content.split("\n");
        int index = 0;

        String[] values = lines[index++].split(";");

        float x = Float.parseFloat(values[0]) * Terrain.getSIZE();
        float z = Float.parseFloat(values[1]) * Terrain.getSIZE();
        TerrainTexturePack ttp = TerrainTexturePack.load(lines[index++]);
        values = lines[index].split(";");
        TerrainTexture blendMap = TerrainTexture.load(values[0],true);
        String heightMap = values[1];
        return new Terrain(x / Terrain.getSIZE(), z / Terrain.getSIZE(), ttp, blendMap, heightMap);

    }
}
