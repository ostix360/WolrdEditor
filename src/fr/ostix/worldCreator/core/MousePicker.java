package fr.ostix.worldCreator.core;


import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.toolBox.OpenGL.*;
import fr.ostix.worldCreator.world.chunk.*;
import org.joml.*;

import java.util.*;

public class MousePicker {

    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 600;
    private final Matrix4f projectionMatrix;
    private final Camera cam;
    private final Map<Vector2f, Chunk> terrains;
    private Vector3f currentRay;
    private Matrix4f viewMatrix;
    private Vector3f currentTerrainPoint;
    private Terrain currentTerrain;
    private Chunk currentChunk;


    public MousePicker(Matrix4f projectionMatrix, Camera cam, Map<Vector2f, Chunk> terrains) {
        this.terrains = terrains;
        this.projectionMatrix = projectionMatrix;
        this.cam = cam;
        this.viewMatrix = Maths.createViewMatrix(this.cam);
    }


    public void update() {
        viewMatrix = Maths.createViewMatrix(cam);
        currentRay = calculateMouseRay();
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
        } else {
            currentTerrainPoint = null;
        }
    }

    public Terrain getCurrentTerrain() {
        return currentTerrain;
    }

    private Vector3f calculateMouseRay() {
        float mouseX = (float) Input.getMouseX();
        float mouseY = (float) Input.getMouseY();
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        return toWorldCoords(eyeCoords);
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = viewMatrix.invert(new Matrix4f());
        Vector4f rayWorld = invertedView.transform(eyeCoords, new Vector4f());
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalize();
        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = projectionMatrix.invert(new Matrix4f());
        Vector4f eyeCoords = invertedProjection.transform(clipCoords, new Vector4f());
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / DisplayManager.getWidth() - 1f;
        float y = (2.0f * mouseY) / DisplayManager.getHeight() - 1f;
        return new Vector2f(x, -y);
    }

    //**********************************************************

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = cam.getPosition();
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay, new Vector3f());
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            currentTerrain = getTerrain(endPoint.x(), endPoint.z());
            if (currentTerrain != null) {
                return endPoint;
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        return !isUnderGround(startPoint) && isUnderGround(endPoint);
    }

    private boolean isUnderGround(Vector3f testPoint) {
        Terrain terrain = getTerrain(testPoint.x(), testPoint.z());
        float height = 0;
        if (terrain != null) {
            height = terrain.getHeightOfTerrain(testPoint.x(), testPoint.z());
        }
        return testPoint.y < height;
    }

    private Terrain getTerrain(float worldX, float worldZ) {
        if (worldX < 0) worldX -= 100;
        if (worldZ < 0) worldZ -= 100;
        int x = (int) (worldX / Terrain.getSIZE());
        int z = (int) (worldZ / Terrain.getSIZE());
        if (terrains.isEmpty()) {
            return null;
        }
        if (!terrains.containsKey(new Vector2f(x,z))) {
            return null;
        }
        currentChunk = terrains.get(new Vector2f(x,z));
        return currentChunk.getTerrain();
    }

    public Chunk getCurrentChunk(){
        return this.currentChunk;
    }

    public Vector3f getCurrentTerrainPoint() {
        return currentTerrainPoint;
    }

    public Vector3f getRayEndPoint() {
        return getPointOnRay(calculateMouseRay(),5000f);
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }
}
