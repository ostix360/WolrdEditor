package fr.ostix.worldCreator.world.chunk;

import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.toolBox.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class ChunksFile {
    private List<Chunk> chunks;
    private int x,z;
    private String content;

    public ChunksFile(int x, int z) {
        this.x = x;
        this.z = z;
        chunks = new ArrayList<>();
    }

    public void export(){
        try (FileOutputStream fos = openWritableFile();
        FileChannel fc = fos.getChannel()) {
            fc.position(0);
            for(Chunk c : chunks){
                if(!c.isEmpty()){
                    c.export(fc);
                }
            }
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put((byte) 'c');
            buffer.flip();
            //fc.write(buffer);
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
    }

    public Chunk load(int x,int z){
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
                return this.addPart(Chunk.load(sb.toString(),x,z));
            }
        }
        //System.err.println("Chunk not found in file " + content);
        return this.addPart(new Chunk(x,z,new ArrayList<>()).
                setTerrain(new Terrain(x,z,Config.TERRAIN_DEFAULT_PACK,Config.BLEND_MAP,"default")));
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
