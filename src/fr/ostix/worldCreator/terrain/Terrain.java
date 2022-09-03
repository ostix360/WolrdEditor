package fr.ostix.worldCreator.terrain;


import com.jme3.bullet.control.*;
import fr.ostix.worldCreator.core.resourcesProcessor.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.terrain.texture.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.world.chunk.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public class Terrain {
    private static final int SIZE = 100;
    private static final int MAX_HEIGHT = 40;
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private float[][] heights;
    private final float x;
    private final float z;
    private MeshModel model;
    private final TerrainTexturePack texturePack;
    private final TerrainTexture blendMap;
    private boolean isPicking;
    private String heightMap;
    private ModelLoaderRequest modelRequest;
    private static Map<Vector2f, Chunk> worldChunk;

    private TerrainControl control = null;

    private final ChunksFile parent;




    public Terrain(float gridX, float gridZ, TerrainTexturePack texturePack, TerrainTexture blendMap, float[][] heights, ChunksFile parent) {
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.heights = heights;
        this.parent = parent;
        generateTerrain();
        this.texturePack = texturePack;
        this.blendMap = blendMap;
    }

    public static void setWorldChunk(Map<Vector2f, Chunk> worldChunk) {
        Terrain.worldChunk = worldChunk;
    }

    public boolean isPicking() {
        return isPicking;
    }

    public void setPicking(boolean picking) {
        isPicking = picking;
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);  // cacul de la grille donc nombre de vertex - 1
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX < 0 || gridX >= heights.length - 1 || gridZ < 0 || gridZ >= heights.length - 1) {
            return 0;
        }
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer;
        if (xCoord <= (1 - zCoord)) {
            answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ], 0), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return answer;
    }
//

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Terrain terrain = (Terrain) o;
        return model.equals(terrain.model) && Objects.equals(texturePack, terrain.texturePack) && Objects.equals(blendMap, terrain.blendMap) && Objects.equals(heightMap, terrain.heightMap);
    }


    public void regenerateTerrain(String heightMap) {
        this.heightMap = heightMap;
        generateTerrain();
    }

    public void regenerateTerrain(float[][] heights) {
        this.model = null;
        this.heights = heights;
        int VERTEX_COUNT = 16;
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        int vertexPointer = 0;
        for (int z = 0; z < VERTEX_COUNT; z++) {            // Boucle de generation de monde
            for (int x = 0; x < VERTEX_COUNT; x++) {
                vertices[vertexPointer * 3] = (float) x / ((float) VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = getHeight(x,z);
                vertices[vertexPointer * 3 + 2] = (float) z / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(x, z);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                vertexPointer++;
            }
        }
        ModelData data = modelRequest.getData();
        modelRequest = new ModelLoaderRequest(new ModelData(vertices, data.getTexcoords(), data.getIndices(), normals));
        GLRequestProcessor.sendRequest(modelRequest);
    }

    private void generateTerrain() {
        this.model = null;
        //long time = System.nanoTime();

        // Logger.log("Height Map reading took " + (System.nanoTime() - time));

        int VERTEX_COUNT = 16;
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;

//        if (worldChunk.size() > 0) {
//            smoothTerrain(worldChunk);
//        }

        for (int z = 0; z < VERTEX_COUNT; z++) {            // Boucle de generation de monde
            for (int x = 0; x < VERTEX_COUNT; x++) {
//                float height = getHeight(x, z, image);
//                heights[x][z] = height;
                vertices[vertexPointer * 3] = (float) x / ((float) VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = getHeight(x,z);
                vertices[vertexPointer * 3 + 2] = (float) z / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(x, z);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) x / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) z / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {       //boucle de generation des indices
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }

        modelRequest = new ModelLoaderRequest(new ModelData(vertices, textureCoords, indices, normals));
        GLRequestProcessor.sendRequest(modelRequest);

        //Logger.err("Terrain mesh generation before the GL access took " + (System.nanoTime() - time));
        // Timer.waitForRequest(modelRequest);
//        Logger.err("Terrain mesh generation took " + (System.nanoTime() - time));
        //return modelRequest.getModel();
    }

    private Vector3f calculateNormal(int x, int z) {
        float heightL = getHeight(x - 1, z);
        float heightR = getHeight(x + 1, z);
        float heightD = getHeight(x, z - 1);
        float heightU = getHeight(x, z + 1);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalize();
        return normal;
    }

    private float getHeight(int x, int z) {
        if (x < -1 || x >= 17 || z < -1 || z >= 17) {
            return 0;
        }
        return heights[x+1][z+1];
    }
//    private float getHeight(int x, int z, BufferedImage image) {
//        if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
//            return 0;
//        }
//        float height = image.getRGB(x, z);
//        height += MAX_PIXEL_COLOR / 2;
//        height /= MAX_PIXEL_COLOR / 2;
//        height *= MAX_HEIGHT;
//        return height;
//    }


    public static int getSIZE() {
        return SIZE;
    }

    public float[][] getHeights() {
        return heights;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public MeshModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public void setModel() {
        if (modelRequest.isExecuted()) {
            this.model = modelRequest.getModel();

        }
    }

    public String getHeightMap() {
        return heightMap;
    }

    public void setControl(TerrainControl tc) {
        this.control = tc;
    }

    public TerrainControl getControl() {
        return control;
    }

    public boolean isDefault() {
        return this.blendMap.getName().equals("fullBlack") && this.texturePack.equals(Config.TERRAIN_DEFAULT_PACK);
    }

    public void setHeightMap(String heightMap) {
        parent.setHeightMap(heightMap);
    }

//    private void smoothTerrain(Map<Vector2f, Chunk> worldChunk) {
////        int edgeL = (int) Math.sqrt(left.heights.length);
////        int edgeR = (int) Math.sqrt(right.heights.length);
////        int edgeU = (int) Math.sqrt(up.heights.length);
////        int edgeD = (int) Math.sqrt(bottom.heights.length);
//
//        Chunk leftC = worldChunk.get(new Vector2f(x / SIZE - 1, z / SIZE));
//        Chunk rightC = worldChunk.get(new Vector2f(x / SIZE + 1, z / SIZE));
//        Chunk upC = worldChunk.get(new Vector2f(x / SIZE, z / SIZE + 1));
//        Chunk downC = worldChunk.get(new Vector2f(x / SIZE, z / SIZE - 1));
//
//        Chunk leftUC = worldChunk.get(new Vector2f(x / SIZE - 1, z / SIZE + 1));
//        Chunk rightUC = worldChunk.get(new Vector2f(x / SIZE + 1, z / SIZE +1));
//        Chunk leftDC = worldChunk.get(new Vector2f(x / SIZE - 1, z / SIZE - 1));
//        Chunk rightDC = worldChunk.get(new Vector2f(x / SIZE + 1, z / SIZE - 1));
//
//
//
//        boolean leftIsModified = false;
//        boolean rightIsModified = false;
//        boolean upIsModified = false;
//        boolean downIsModified = false;
//
//        boolean leftUIsModified = false;
//        boolean rightUIsModified = false;
//        boolean leftDIsModified = false;
//        boolean rightDIsModified = false;
//
//
//
//        Terrain t;
//        for (int z = 0; z < 16; z++) {
//            if (leftC == null && rightC == null) {
//                break;
//            }
//            if (leftC != null) {
//                t = leftC.getTerrain();
//                if (t.heights[15][z] != this.heights[0][z]) {
//                    t.heights[15][z] = this.heights[0][z]  = (t.heights[15][z]  + this.heights[0][z] ) / 2;
//                    leftIsModified = true;
//                }
//
//            }
//            if (rightC != null) {
//                t = rightC.getTerrain();
//                if (t.heights[0][z] != this.heights[15][z]) {
//                    t.heights[0][z] = this.heights[15][z]  = (t.heights[0][z]  + this.heights[15][z] ) / 2;
//                    rightIsModified = true;
//                }
//
//            }
//        }
//        for (int x = 0; x < 16; x++) {
//            if (upC == null && downC == null) {
//                break;
//            }
//            if (upC != null) {
//                t = upC.getTerrain();
//                if (t.heights[x][0] != this.heights[x][15]) {
//                    this.heights[x][15] = (t.heights[x][0] + this.heights[x][15]) / 2;
//                    t.heights[x][0] = this.heights[x][15];
//                    upIsModified = true;
//                }
//
//            }
//            if (downC != null) {
//                t = downC.getTerrain();
//                if (t.heights[x][15] != this.heights[x][0]) {
//                    this.heights[x][0] = (t.heights[x][15] + this.heights[x][0]) / 2;
//                    t.heights[x][15] = this.heights[x][0];
//                    downIsModified = true;
//                }
//            }
//        }
//
//        if (leftUC != null) {
//            t = leftUC.getTerrain();
//            if (t.heights[15][0] != this.heights[0][15]) {
//                this.heights[0][15] = (t.heights[15][0] + this.heights[0][15]) / 2;
//                t.heights[15][0] = this.heights[0][15];
//                leftUIsModified = true;
//            }
//        }
//        if (rightUC != null) {
//            t = rightUC.getTerrain();
//            if (t.heights[0][0] !=  this.heights[15][15]) {
//                this.heights[15][15] = (t.heights[0][0] + this.heights[15][15]) / 2;
//                t.heights[0][0] = this.heights[15][15];
//                rightUIsModified = true;
//            }
//        }
//        if (leftDC != null) {
//            t = leftDC.getTerrain();
//            if (t.heights[15][15] !=  this.heights[0][0]) {
//                this.heights[0][0] = (t.heights[15][15] + this.heights[0][0]) / 2;
//                t.heights[15][15] = this.heights[0][0];
//                leftDIsModified = true;
//            }
//        }
//        if (rightDC != null) {
//            t = rightDC.getTerrain();
//            if (t.heights[0][15] != this.heights[15][0]) {
//                this.heights[15][0] = (t.heights[0][15] + this.heights[15][0]) / 2;
//                t.heights[0][15] = this.heights[15][0];
//                rightDIsModified = true;
//            }
//        }
//
//        if (leftIsModified) {
//            smoothTerrain(worldChunk);
//            leftC.getTerrain().regenerateTerrain();
//        }
//        if (rightIsModified) {
//            smoothTerrain(worldChunk);
//            rightC.getTerrain().regenerateTerrain();
//        }
//        if (upIsModified) {
//            smoothTerrain(worldChunk);
//            upC.getTerrain().regenerateTerrain();
//        }
//        if (downIsModified) {
//            smoothTerrain(worldChunk);
//            downC.getTerrain().regenerateTerrain();
//        }
//        if (leftUIsModified) {
//            smoothTerrain(worldChunk);
//            leftUC.getTerrain().regenerateTerrain();
//        }
//        if (rightUIsModified) {
//            smoothTerrain(worldChunk);
//            rightUC.getTerrain().regenerateTerrain();
//        }
//        if (leftDIsModified) {
//            smoothTerrain(worldChunk);
//            leftDC.getTerrain().regenerateTerrain();
//        }
//        if (rightDIsModified) {
//            smoothTerrain(worldChunk);
//            rightDC.getTerrain().regenerateTerrain();
//        }
//
//    }
}
