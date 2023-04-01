package fr.ostix.worldCreator.entity.camera;

import fr.ostix.worldCreator.core.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.graphics.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.world.*;
import org.joml.*;

import java.awt.event.*;
import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;

public class Camera implements ICamera {

    public static float RUN_SPEED = 860;
    private static final float TURN_SPEED = 780;

    public final int viewDistance = 9;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private final SmoothFloat distanceFromPlayer = new SmoothFloat(2, 8);
    private final SmoothFloat angleAroundPlayer = new SmoothFloat(0, 10);
    private Matrix4f projection;

    private final Vector3f position = new Vector3f(-50, 35, -100);
    float pitch = 20;
    float yaw = 0;
    private final float roll = 0;

    float elapsedMouseDY;

    private final Transform player;

    public Camera(Transform player) {
        this.player = player;
    }

    private float terrainHeight;

    @Override
    public Matrix4f getViewMatrix() {
        return Maths.createViewMatrix(this);
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    @Override
    public Matrix4f getProjectionViewMatrix() {
        return projection.mul(getViewMatrix());
    }

    public void move() {
        checkInput();
        applyToPlayerTransform();
        this.terrainHeight = World.getTerrainHeight(this.position.x(), this.position.z()) + 2;
        //calculateZoom(dWell);
        calculateAngleAroundPlayerAndPitch();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        float yOffset = updateSmooth(player.getPosition().y + 7 + verticalDistance, this.position.y());
        caculateCameraPosition(horizontalDistance, yOffset);
        this.yaw = 180 - (player.getRotation().y() + angleAroundPlayer.get());
        this.projection = MasterRenderer.getProjectionMatrix();
    }

    private void applyToPlayerTransform() {
        player.getRotation().add(0, this.currentTurnSpeed * 0.0023f, 0);
        float distance = currentSpeed * 0.006f;
        float dx = (float) (distance * Math.sin(Math.toRadians(player.getRotation().y())));
        float dz = (float) (distance * Math.cos(Math.toRadians(player.getRotation().y())));
        player.getPosition().add(dx,upwardsSpeed,dz);
        if (player.getPosition().y()<-40){
            player.getPosition().y = -40;
        }
    }

    private void checkInput() {
        if (Input.keys[KeyEvent.VK_Z] || Input.keys[KeyEvent.VK_UP]) {
            this.currentSpeed = RUN_SPEED;
        } else if (Input.keys[KeyEvent.VK_S] || Input.keys[KeyEvent.VK_DOWN]) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (Input.keys[KeyEvent.VK_Q] || Input.keys[KeyEvent.VK_LEFT]) {
            this.currentTurnSpeed = TURN_SPEED;
        } else if (Input.keys[KeyEvent.VK_D] || Input.keys[KeyEvent.VK_RIGHT]) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if (Input.keys[KeyEvent.VK_SPACE]) {
            this.upwardsSpeed = 2;
        }
        else if (Input.keys[KeyEvent.VK_SHIFT]) {
            this.upwardsSpeed = -2;
        }else{
            this.upwardsSpeed = 0;
        }
    }

    private float updateSmooth(float target, float actual) {
        float offset = target - actual;
        float change = offset * 1 / 60 * 5;
        return actual + change;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer.get() * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer.get() * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom(float dWell) {
        float target = this.distanceFromPlayer.getTarget();
        target -= dWell;
        if (target <= 3) {
            target = 3;
        }
        if (target >= 105) {
            target = 105;
        }
        distanceFromPlayer.setTarget(target);
    }

    private void caculateCameraPosition(float horzontalDistance, float yOffset) {
        float theta = player.getRotation().y() + angleAroundPlayer.get();
        float xoffset = (float) (horzontalDistance * Math.sin(Math.toRadians(theta)));
        float zoffset = (float) (horzontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - xoffset;
        position.y = yOffset;
        if (position.y < terrainHeight) {
            position.y = terrainHeight;
        }
        position.z = player.getPosition().z - zoffset;
    }

    private void calculateAngleAroundPlayerAndPitch() {
        if (Input.keysMouse[GLFW_MOUSE_BUTTON_2]) {

            float angleChange = Input.mouseDX * 0.1f;
            angleAroundPlayer.increaseTarget(-angleChange);
            float pitchChange = Input.mouseDY * 0.1f;
            pitch += pitchChange;
            if (pitch >= 90) {
                pitch = 90;
            }
            if (pitch <= -4) {
                if (elapsedMouseDY < pitchChange) distanceFromPlayer.increaseTarget((float) (pitchChange * 1.4));
                pitch = -4;
            }
            elapsedMouseDY = pitchChange;
        }
        angleAroundPlayer.update(1 / 60f);
        distanceFromPlayer.update(1f / 60f);
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void reflect(float height) {
        this.invertPitch();
        this.position.y = position.y - 2 * (position.y - height);
    }
}
