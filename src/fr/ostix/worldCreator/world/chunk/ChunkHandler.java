package fr.ostix.worldCreator.world.chunk;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.world.*;
import org.joml.Math;
import org.joml.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

public class ChunkHandler {
    private final Lock writeLock = new ReentrantReadWriteLock().writeLock();

    private final Map<Vector2f, Chunk> chunkList = new ConcurrentHashMap<>();

    private static final float CHUNK_FILE_SIZE = 32f;
    private final Map<Vector2f, ChunksFile> chunksFileList = new ConcurrentHashMap<>();
    private final Camera cam;
    private final Map<Vector2f, Chunk> worldChunk;
    private final Map<Chunk, List<Entity>> entitiesChunk = new ConcurrentHashMap<>();
    private final List<Entity> entities;

    private final World world;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public ChunkHandler(Camera cam, Map<Vector2f, Chunk> worldChunk, List<Entity> entities, World world) {
        this.world = world;
        this.cam = cam;
        this.worldChunk = worldChunk;
        this.entities = entities;
    }

    public void run() {
        executor.execute(() -> {
            int playerChunkX;
            int playerChunkZ;
            if (cam.getPosition().x() >= 0) {
                playerChunkX = (int) Math.floor(cam.getPosition().x() / Terrain.getSIZE() / CHUNK_FILE_SIZE); // Get the current chunk that the player is in on the X - Axis
            } else {
                playerChunkX = (int) Math.floor((cam.getPosition().x() - 100) / Terrain.getSIZE() / CHUNK_FILE_SIZE); // Get the current chunk that the player is in on the X - Axis
            }
            if (cam.getPosition().z() >= 0) {
                playerChunkZ = (int) Math.floor(cam.getPosition().z() / Terrain.getSIZE() / CHUNK_FILE_SIZE); // Get the current chunk that the player is in on the Z - Axis

            } else {
                playerChunkZ = (int) Math.floor((cam.getPosition().z() - 100) / Terrain.getSIZE() / CHUNK_FILE_SIZE); // Get the current chunk that the player is in on the Z - Axis
            }


            // System.out.println(playerChunkX + ", " + playerChunkZ);

            int viewDistanceChunkFile = cam.viewDistance;
            if (viewDistanceChunkFile <= CHUNK_FILE_SIZE) {
                viewDistanceChunkFile = (int) (CHUNK_FILE_SIZE + 1);
            }
            for (int x1 = 0; x1 <= viewDistanceChunkFile; x1 += CHUNK_FILE_SIZE) { // X - Axis iteration
                for (int z1 = 0; z1 <= viewDistanceChunkFile; z1 += CHUNK_FILE_SIZE) { // Z - Axis iteration
                    int x = (int) (x1 / CHUNK_FILE_SIZE);
                    int z = (int) (z1 / CHUNK_FILE_SIZE);
                    if (!(chunksFileList.containsKey(new Vector2f(playerChunkX + x, playerChunkZ)))) {
                        chunksFileList.put(new Vector2f(playerChunkX + x, playerChunkZ), new ChunksFile(playerChunkX + x, playerChunkZ));
                        chunksFileList.get(new Vector2f(playerChunkX + x, playerChunkZ)).load();
                    }

                    if (!(chunksFileList.containsKey(new Vector2f(playerChunkX, playerChunkZ + z)))) {
                        chunksFileList.put(new Vector2f(playerChunkX, playerChunkZ + z), new ChunksFile(playerChunkX, playerChunkZ + z));
                        chunksFileList.get(new Vector2f(playerChunkX, playerChunkZ + z)).load();
                    }

                    if (!(chunksFileList.containsKey(new Vector2f(playerChunkX + x, playerChunkZ + z)))) {
                        chunksFileList.put(new Vector2f(playerChunkX + x, playerChunkZ + z), new ChunksFile(playerChunkX + x, playerChunkZ + z));
                        chunksFileList.get(new Vector2f(playerChunkX + x, playerChunkZ + z)).load();
                    }

                    if (!(chunksFileList.containsKey(new Vector2f(playerChunkX + x, playerChunkZ - z)))) {
                        chunksFileList.put(new Vector2f(playerChunkX + x, playerChunkZ - z), new ChunksFile(playerChunkX + x, playerChunkZ - z));
                        chunksFileList.get(new Vector2f(playerChunkX + x, playerChunkZ - z)).load();
                    }

                    if (!(chunksFileList.containsKey(new Vector2f(playerChunkX, playerChunkZ - z)))) {
                        chunksFileList.put(new Vector2f(playerChunkX, playerChunkZ - z), new ChunksFile(playerChunkX, playerChunkZ - z));
                        chunksFileList.get(new Vector2f(playerChunkX, playerChunkZ - z)).load();
                    }

                    if (!(chunksFileList.containsKey(new Vector2f(playerChunkX - x, playerChunkZ - z)))) {
                        chunksFileList.put(new Vector2f(playerChunkX - x, playerChunkZ - z), new ChunksFile(playerChunkX - x, playerChunkZ - z));
                        chunksFileList.get(new Vector2f(playerChunkX - x, playerChunkZ - z)).load();
                    }


                    if (!(chunksFileList.containsKey(new Vector2f(playerChunkX - x, playerChunkZ + z)))) {
                        chunksFileList.put(new Vector2f(playerChunkX - x, playerChunkZ + z), new ChunksFile(playerChunkX - x, playerChunkZ + z));
                        chunksFileList.get(new Vector2f(playerChunkX - x, playerChunkZ + z)).load();
                    }
                    if (!(chunksFileList.containsKey(new Vector2f(playerChunkX - x, playerChunkZ)))) {
                        chunksFileList.put(new Vector2f(playerChunkX - x, playerChunkZ), new ChunksFile(playerChunkX - x, playerChunkZ));
                        chunksFileList.get(new Vector2f(playerChunkX - x, playerChunkZ)).load();
                    }

                }
            }
            viewDistanceChunkFile = cam.viewDistance;
            ChunksFile currentChunkFile;
            currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX, playerChunkZ + viewDistanceChunkFile + 1));
            if (currentChunkFile != null)
                currentChunkFile.export();

            currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX, playerChunkZ - viewDistanceChunkFile - 1));
            if (currentChunkFile != null) currentChunkFile.export();

            currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX + viewDistanceChunkFile + 1, playerChunkZ));
            if (currentChunkFile != null) currentChunkFile.export();

            currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX - viewDistanceChunkFile - 1, playerChunkZ));
            if (currentChunkFile != null) currentChunkFile.export();


            currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX + viewDistanceChunkFile + 1, playerChunkZ + viewDistanceChunkFile + 1));
            if (currentChunkFile != null) currentChunkFile.export();

            currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX - viewDistanceChunkFile - 1, playerChunkZ - viewDistanceChunkFile - 1));
            if (currentChunkFile != null) currentChunkFile.export();

            currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX + viewDistanceChunkFile + 1, playerChunkZ - viewDistanceChunkFile - 1));
            if (currentChunkFile != null) currentChunkFile.export();

            currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX + viewDistanceChunkFile - 1, playerChunkZ + viewDistanceChunkFile + 1));
            if (currentChunkFile != null) currentChunkFile.export();


            for (int x = 0; x < cam.viewDistance / CHUNK_FILE_SIZE + 1; x++) {

                currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX + x, playerChunkZ + viewDistanceChunkFile + 1));
                if (currentChunkFile != null) currentChunkFile.export();

                currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX - x, playerChunkZ + viewDistanceChunkFile + 1));
                if (currentChunkFile != null) currentChunkFile.export();

                currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX + x, playerChunkZ - viewDistanceChunkFile - 1));
                if (currentChunkFile != null) currentChunkFile.export();

                currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX - x, playerChunkZ - viewDistanceChunkFile - 1));
                if (currentChunkFile != null) currentChunkFile.export();

            }

            for (int z = 0; z < cam.viewDistance / CHUNK_FILE_SIZE + 1; z++) {
                currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX + viewDistanceChunkFile + 1, playerChunkZ + z));
                if (currentChunkFile != null) currentChunkFile.export();

                currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX + viewDistanceChunkFile + 1, playerChunkZ - z));
                if (currentChunkFile != null) currentChunkFile.export();

                currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX - viewDistanceChunkFile - 1, playerChunkZ + z));
                if (currentChunkFile != null) currentChunkFile.export();

                currentChunkFile = chunksFileList.remove(new Vector2f(playerChunkX - viewDistanceChunkFile - 1, playerChunkZ - z));
                if (currentChunkFile != null) currentChunkFile.export();
            }

            if (cam.getPosition().x() >= 0) {
                playerChunkX = (int) Math.floor(cam.getPosition().x() / Terrain.getSIZE()); // Get the current chunk that the player is in on the X - Axis
            } else {
                playerChunkX = (int) Math.floor((cam.getPosition().x() - 100) / Terrain.getSIZE()); // Get the current chunk that the player is in on the X - Axis
            }
            if (cam.getPosition().z() >= 0) {
                playerChunkZ = (int) Math.floor(cam.getPosition().z() / Terrain.getSIZE()); // Get the current chunk that the player is in on the Z - Axis

            } else {
                playerChunkZ = (int) Math.floor((cam.getPosition().z() - 100) / Terrain.getSIZE()); // Get the current chunk that the player is in on the Z - Axis
            }
            Terrain.setWorldChunk(worldChunk);
            for (int x = 0; x < cam.viewDistance; x++) { // X - Axis iteration
                for (int z = 0; z < cam.viewDistance; z++) { // Z - Axis iteration
                    loadChunks(playerChunkX, playerChunkZ, x, z);

                }
            }


            unLoadChunks(playerChunkX, playerChunkZ, -cam.viewDistance, -cam.viewDistance,
                    cam.viewDistance, cam.viewDistance);

//            synchronized (worldChunk) {
//
//                worldChunk.clear();
//                worldChunk.putAll(Collections.synchronizedMap(new HashMap<>(chunkList)));
//            }
//            synchronized (entities) {
//                entities.clear();
//                entities.addAll(entitiesChunk);
//            }
        });
    }

    public static void stopChunkHandler() {
        executor.shutdown();
    }

    private void unLoadChunks(int playerChunkX, int playerChunkZ, int xMin, int zMin, int xMax, int zMax) {
        Chunk chunk;

        chunk = worldChunk.remove(new Vector2f(playerChunkX, playerChunkZ + zMax + 1));
        if (chunk != null) {
            world.getPhysics().remove(chunk);
            entities.removeAll(chunk.getEntities());
        }

        chunk = worldChunk.remove(new Vector2f(playerChunkX, playerChunkZ + zMin - 1));
        if (chunk != null) {
            world.getPhysics().remove(chunk);
            entities.removeAll(chunk.getEntities());
        }

        chunk = worldChunk.remove(new Vector2f(playerChunkX + xMax + 1, playerChunkZ));
        if (chunk != null) {
            world.getPhysics().remove(chunk);
            entities.removeAll(chunk.getEntities());
        }

        chunk = worldChunk.remove(new Vector2f(playerChunkX + xMin - 1, playerChunkZ));
        if (chunk != null) {
            world.getPhysics().remove(chunk);
            entities.removeAll(chunk.getEntities());
        }


        chunk = worldChunk.remove(new Vector2f(playerChunkX + xMax + 1, playerChunkZ + zMax + 1));
        if (chunk != null) {
            world.getPhysics().remove(chunk);
            entities.removeAll(chunk.getEntities());
        }

        chunk = worldChunk.remove(new Vector2f(playerChunkX + xMin - 1, playerChunkZ + zMin - 1));
        if (chunk != null) {
            world.getPhysics().remove(chunk);
            entities.removeAll(chunk.getEntities());
        }

        chunk = worldChunk.remove(new Vector2f(playerChunkX + xMax + 1, playerChunkZ + zMin - 1));
        if (chunk != null) {
            world.getPhysics().remove(chunk);
            entities.removeAll(chunk.getEntities());
        }

        chunk = worldChunk.remove(new Vector2f(playerChunkX + xMin - 1, playerChunkZ + zMax + 1));
        if (chunk != null) {
            world.getPhysics().remove(chunk);
            entities.removeAll(chunk.getEntities());
        }

        for (int x = 0; x < cam.viewDistance + 1; x++) {
            chunk = worldChunk.remove(new Vector2f(playerChunkX + x, playerChunkZ + zMax + 1));
            if (chunk != null) {
                world.getPhysics().remove(chunk);
                entities.removeAll(chunk.getEntities());
            }

            chunk = worldChunk.remove(new Vector2f(playerChunkX - x, playerChunkZ + zMax + 1));
            if (chunk != null) {
                world.getPhysics().remove(chunk);
                entities.removeAll(chunk.getEntities());
            }

            chunk = worldChunk.remove(new Vector2f(playerChunkX + x, playerChunkZ + zMin - 1));
            if (chunk != null) {
                world.getPhysics().remove(chunk);
                entities.removeAll(chunk.getEntities());
            }

            chunk = worldChunk.remove(new Vector2f(playerChunkX - x, playerChunkZ + zMin - 1));
            if (chunk != null) {
                world.getPhysics().remove(chunk);
                entities.removeAll(chunk.getEntities());
            }
        }

        for (int z = 0; z < cam.viewDistance + 1; z++) {
            chunk = worldChunk.remove(new Vector2f(playerChunkX + xMax + 1, playerChunkZ + z));
            if (chunk != null) {
                world.getPhysics().remove(chunk);
                entities.removeAll(chunk.getEntities());
            }

            chunk = worldChunk.remove(new Vector2f(playerChunkX + xMax + 1, playerChunkZ - z));
            if (chunk != null) {
                world.getPhysics().remove(chunk);
                entities.removeAll(chunk.getEntities());
            }

            chunk = worldChunk.remove(new Vector2f(playerChunkX + xMin - 1, playerChunkZ + z));
            if (chunk != null) {
                world.getPhysics().remove(chunk);
                entities.removeAll(chunk.getEntities());
            }

            chunk = worldChunk.remove(new Vector2f(playerChunkX + xMin - 1, playerChunkZ - z));
            if (chunk != null) {
                world.getPhysics().remove(chunk);
                entities.removeAll(chunk.getEntities());
            }
        }
    }

    private void loadChunks(int playerChunkX, int playerChunkZ, int x, int z) {
        int chunkFileIndexX;
        int chunkFileIndexZ;

        int chunkFileIndexXPositive = (int) Math.floor((playerChunkX + x) / CHUNK_FILE_SIZE);

        int chunkFileIndexZPositive = (int) Math.floor((playerChunkZ + z) / CHUNK_FILE_SIZE);

//        if (!(worldChunk.containsKey(new Vector2f(playerChunkX, playerChunkZ)))) {
//            chunkFileIndexX = playerChunkX / CHUNK_FILE_SIZE;
//            chunkFileIndexZ = playerChunkZ / CHUNK_FILE_SIZE;
//            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ)).load(playerChunkX, playerChunkZ);
//            worldChunk.put(new Vector2f(playerChunkX, playerChunkZ), chunk); // Create new chunk
//        }


        if (!(worldChunk.containsKey(new Vector2f(playerChunkX + x, playerChunkZ)))) {
            chunkFileIndexX = chunkFileIndexXPositive;
            chunkFileIndexZ = (int) Math.floor(playerChunkZ / CHUNK_FILE_SIZE);
            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ)).load(playerChunkX + x, playerChunkZ);
            worldChunk.putIfAbsent(new Vector2f(playerChunkX + x, playerChunkZ), chunk); // Create new chunk
            entitiesChunk.putIfAbsent(chunk, chunk.getEntities());
            entities.addAll(chunk.getEntities());
            world.getPhysics().add(chunk);
        }

        if (!(worldChunk.containsKey(new Vector2f(playerChunkX, playerChunkZ + z)))) {
            chunkFileIndexX = (int) Math.floor(playerChunkX / CHUNK_FILE_SIZE);
            chunkFileIndexZ = chunkFileIndexZPositive;
            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ)).load(playerChunkX, playerChunkZ + z);
            worldChunk.put(new Vector2f(playerChunkX, playerChunkZ + z), chunk); // Create new chunk
            entitiesChunk.put(chunk, chunk.getEntities());
            entities.addAll(chunk.getEntities());
            world.getPhysics().add(chunk);
        }

        if (!(worldChunk.containsKey(new Vector2f(playerChunkX + x, playerChunkZ + z)))) {
            chunkFileIndexX = chunkFileIndexXPositive;
            chunkFileIndexZ = chunkFileIndexZPositive;
            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ))
                    .load(playerChunkX + x, playerChunkZ + z);
            worldChunk.put(new Vector2f(playerChunkX + x, playerChunkZ + z), chunk); // Create new chunk
            entitiesChunk.put(chunk, chunk.getEntities());
            entities.addAll(chunk.getEntities());
            world.getPhysics().add(chunk);
        }

        if (!(worldChunk.containsKey(new Vector2f(playerChunkX + x, playerChunkZ - z)))) {
            chunkFileIndexX = chunkFileIndexXPositive;
            chunkFileIndexZ = (int) Math.floor((playerChunkZ - z) / CHUNK_FILE_SIZE);
            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ)).load(playerChunkX + x, playerChunkZ - z);
            worldChunk.put(new Vector2f(playerChunkX + x, playerChunkZ - z), chunk); // Create a new chunk
            entitiesChunk.put(chunk, chunk.getEntities());
            entities.addAll(chunk.getEntities());
            world.getPhysics().add(chunk);
        }

        if (!(worldChunk.containsKey(new Vector2f(playerChunkX, playerChunkZ - z)))) {
            chunkFileIndexX = (int) Math.floor(playerChunkX / CHUNK_FILE_SIZE);
            chunkFileIndexZ = (int) Math.floor((playerChunkZ - z) / CHUNK_FILE_SIZE);
            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ)).load(playerChunkX, playerChunkZ - z);
            worldChunk.put(new Vector2f(playerChunkX, playerChunkZ - z), chunk); // Create a new chunk
            entitiesChunk.put(chunk, chunk.getEntities());
            entities.addAll(chunk.getEntities());
            world.getPhysics().add(chunk);
        }


        if (!(worldChunk.containsKey(new Vector2f(playerChunkX - x, playerChunkZ - z)))) {
            chunkFileIndexX = (int) Math.floor((playerChunkX - x) / CHUNK_FILE_SIZE);
            chunkFileIndexZ = (int) Math.floor((playerChunkZ - z) / CHUNK_FILE_SIZE);
            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ)).load(playerChunkX - x, playerChunkZ - z);
            worldChunk.put(new Vector2f(playerChunkX - x, playerChunkZ - z), chunk); // Create new chunk
            entitiesChunk.put(chunk, chunk.getEntities());
            entities.addAll(chunk.getEntities());
            world.getPhysics().add(chunk);
        }


        if (!(worldChunk.containsKey(new Vector2f(playerChunkX - x, playerChunkZ + z)))) {
            chunkFileIndexX = (int) Math.floor((playerChunkX - x) / CHUNK_FILE_SIZE);
            chunkFileIndexZ = chunkFileIndexZPositive;
            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ)).load(playerChunkX - x, playerChunkZ + z);
            worldChunk.put(new Vector2f(playerChunkX - x, playerChunkZ + z), chunk); // Create new chunk
            entitiesChunk.put(chunk, chunk.getEntities());
            entities.addAll(chunk.getEntities());
            world.getPhysics().add(chunk);
        }
        if (!(worldChunk.containsKey(new Vector2f(playerChunkX - x, playerChunkZ)))) {
            chunkFileIndexX = (int) Math.floor((playerChunkX - x) / CHUNK_FILE_SIZE);
            chunkFileIndexZ = (int) Math.floor(playerChunkZ / CHUNK_FILE_SIZE);
            Chunk chunk = chunksFileList.get(new Vector2f(chunkFileIndexX, chunkFileIndexZ)).load(playerChunkX - x, playerChunkZ);
            worldChunk.put(new Vector2f(playerChunkX - x, playerChunkZ), chunk); // Create new chunk
            entitiesChunk.put(chunk, chunk.getEntities());
            entities.addAll(chunk.getEntities());
            world.getPhysics().add(chunk);
        }

    }

    public Map<Vector2f, Chunk> getChunkMap() {
        synchronized (chunkList) {
            return new HashMap<>(Collections.synchronizedMap(chunkList));
        }
    }

    public void save() {
        for (ChunksFile cf : chunksFileList.values()) {
            cf.export();
        }
    }

    public void addEntity(Chunk c, Entity entityPicked) {
//        entitiesChunk.get(c).add(entityPicked);
        c.getEntities().add(entityPicked);
    }

    private final Chunk badChunk = new Chunk(-50000, -50000, new ArrayList<>());

    public void addAllEntities(List<Entity> aabbs) {
        entitiesChunk.put(badChunk, new ArrayList<>(aabbs));
    }

    public void remove(Entity e) {
        synchronized (entitiesChunk) {
            for (Chunk c : entitiesChunk.keySet()) {
                List<Entity> entities = new ArrayList<>(entitiesChunk.get(c));
                for (Entity e1 : entities) {
                    if (e1.equals(e)) {
                        entitiesChunk.get(c).remove(e);
                    }
                }

            }
        }
    }

    public List<Entity> getEntities() {
        entities.clear();
        entitiesChunk.values().forEach(entities::addAll);
        return entities;
    }
}
