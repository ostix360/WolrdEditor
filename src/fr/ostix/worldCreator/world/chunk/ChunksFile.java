package fr.ostix.worldCreator.world.chunk;

import fr.ostix.worldCreator.frame.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.terrain.texture.*;
import fr.ostix.worldCreator.toolBox.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public class ChunksFile {
    private final List<Chunk> chunks;
    private final int x;
    private final int z;
    private String content;

    private String heightMap;

    private float[][] heights;

    private static final int MAX_HEIGHT = 40;

    public ChunksFile(int x, int z) {
        this.x = x;
        this.z = z;
        chunks = new CopyOnWriteArrayList<>();
    }

    public void export(){
        try (FileOutputStream fos = openWritableFile();
        FileChannel fc = fos.getChannel()) {
            System.out.println("Exporting chunks... : " + x + "," + z);
            fc.position(0);
            for(Chunk c : chunks){
                if(!c.isEmpty()){
                    c.export(fc);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void load(){
        String content = "";
        try (FileChannel fc = openReadableFile()) {
            if (fc == null) {
                this.content = "";
                return;
            }
            ByteBuffer buffer = ByteBuffer.allocate(6);
            int noOfBytesRead = fc.read(buffer);
            StringBuilder sb = new StringBuilder();
            while (noOfBytesRead != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    sb.append((char)buffer.get());
                }
                buffer.clear();
                noOfBytesRead = fc.read(buffer);
            }
            content = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.content = content;
        String[] chunksContent = this.content.split("\n");
        if (chunksContent.length > 0) {
            loadHeightMap(chunksContent[0]);
        }
    }

    public Chunk load(int x,int z){
        for (Chunk c : chunks) {
            if (c.getX() == x && c.getZ() == z) {
                return c;
            }
        }
        String[] chunksContent = this.content.split("\n");
        for (int i = 0; i < chunksContent.length; i++){
            StringBuilder sb = new StringBuilder();
            if (chunksContent[i].contains("CHUNK " + x + ";" + z)){
                i++;
                while (!chunksContent[i].contains("CHUNK")){
                    sb.append(chunksContent[i]).append("\n");
                    i++;
                    if(i>=chunksContent.length)break;
                }
                float[][] heights = chooseHeight(x,z); //TODO
                return this.addPart(Chunk.load(sb.toString(),x,z,heights));
            }
        }
        //System.err.println("Chunk not found in file " + content);
        return this.addPart(new Chunk(x,z,new ArrayList<>()).
                setTerrain(new Terrain(x,z,new TerrainTexturePack(Config.TERRAIN_DEFAULT_PACK),new TerrainTexture(Config.BLEND_MAP),"default")));
    }

    private float[][] chooseHeight(int x, int z) {
        float[][] heights = new float[16][16];
        int xIndex;
        int zIndex;
        if (this.x >= 0){
            xIndex = x/(this.x+1);
        }else{
            xIndex = x/(this.x);
        }
        if (this.z >= 0) {
            zIndex = z/(this.z+1);
        }else {
            zIndex = z/(this.z);
        }

        for (int z1 = 0; z1 < 16; z1++) {            // Boucle de generation de la hauteur
            for (int x1 = 0; x1 < 16; x1++) {
                heights[x][z] = this.heights[xIndex+x1][zIndex+1];
            }
        }

        return heights;
    }

    public void loadHeightMap(String map){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(Config.REPOSITORY_FOLDER + "/textures/terrain/heightMap/" + heightMap + ".png"));
        } catch (IOException e) {
            Logger.err("Couldn't load heightMap ", e);
            new ErrorPopUp("Impossible de lire la heightMap " + Config.REPOSITORY_FOLDER + "/textures/terrain/heightMap/" + heightMap + ".png");
        }
        assert image != null;
        if (image.getHeight() != 1024 || image.getWidth() != 1024) {
            new ErrorPopUp("Votre heightMap doit être de 1024 x 1024");
        }
        for (int z = 0; z < 1024; z++) {            // Boucle de generation de monde
            for (int x = 0; x < 1024; x++) {
                heights[x][z] = getHeight(x, z, image);
            }
        }
        this.heightMap = map;

    }

    private float getHeight(int x, int z, BufferedImage image) {
        if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
            return 0;
        }
        float height = image.getRGB(x, z);
        height += 256 * 256 * 256f / 2f;
        height /= 256 * 256 * 256f / 2f;
        height *= MAX_HEIGHT;
        return height;
    }

    public Chunk addPart(Chunk chunk) {
        this.chunks.add(chunk);
        return chunk;
    }


    private FileOutputStream openWritableFile() throws IOException {
        File f = new File(Config.REPOSITORY_FOLDER + "/world/X" + x + "Z" + z + ".chks");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        return new FileOutputStream(f);
    }

    private FileChannel openReadableFile() throws IOException {
        File f = new File(Config.REPOSITORY_FOLDER + "/world/X" + x + "Z" + z + ".chks");
        if (!f.exists()) {
            return null;
        }
        FileInputStream fos = new FileInputStream(f);
        return fos.getChannel();
    }
}
