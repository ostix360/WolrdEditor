package fr.ostix.worldCreator.entity;

import fr.ostix.worldCreator.graphics.model.*;
import org.joml.*;

import java.lang.Math;

public class Player extends Entity {

    private static final float RUN_SPEED = 160;
    private static final float TURN_SPEED = 780;
    public static final float GRAVITY = 0.12f;
    private static final float JUMP_POWER = 2;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    public boolean canJump = true;

    private final int health = 10;
    private int sprintTime = 60 * 5;
    private final boolean isSprinting = false;


    public Player(Model model, Vector3f position, Vector3f rotation, float scale) {
        super(model, position, rotation, scale, 0);
    }

//    public Player(Entity e) {
//        super(e.getModel(), e.getPosition(), e.getRotation(), e.getScale());
//    }

    @Override
    public void update() {
        this.move();
        if (this.getMovement() == MovementType.FORWARD) {
            this.sprintTime--;
            if (this.sprintTime < 0) {
                sprintTime = 0;
            }
        } else {
            this.sprintTime++;
            if (this.sprintTime > 60 * 5) {
                sprintTime = 60 * 5;
            }
        }
        super.update();
    }

    private void move() {
        checkInputs();
        super.increaseRotation(new Vector3f(0, this.currentTurnSpeed * 0.0023f, 0));

        float distance = currentSpeed * 0.006f;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotation().y())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotation().y())));

        upwardsSpeed -= GRAVITY;

        if (upwardsSpeed <= -9.18f) {
            upwardsSpeed = -9.18f;
        }
        //TODO

        this.upwardsSpeed = 0;
        //super.increasePosition(new Vector3f(dx, upwardsSpeed, dz));
        //if (!canJump) {
        //  }
//        float terrainHeight = World.getTerrainHeight(this.getPosition().x(), this.getPosition().z())+2.4f;
//        if (this.getPosition().y() <= terrainHeight) {
//            canJump = true;
//            position.set(this.getPosition().x(), terrainHeight, this.getPosition().z());
//        }

    }

    private void jump() {
        //if (canJump) {
        this.upwardsSpeed = 2;
        canJump = false;
        // }
    }

    private void checkInputs() {
       this.movement = MovementType.STATIC;
//        if (Input.keys[GLFW_KEY_W] || Input.keys[GLFW_KEY_UP]) {
//            this.movement = MovementType.FORWARD;
//            this.currentSpeed = RUN_SPEED;
//        } else if (Input.keys[GLFW_KEY_S] || Input.keys[GLFW_KEY_DOWN]) {
//            this.movement = MovementType.BACK;
//            this.currentSpeed = -RUN_SPEED;
//        } else {
//            this.currentSpeed = 0;
//        }
//
//        if (Input.keys[GLFW_KEY_A] || Input.keys[GLFW_KEY_LEFT]) {
//            this.currentTurnSpeed = TURN_SPEED;
//        } else if (Input.keys[GLFW_KEY_D] || Input.keys[GLFW_KEY_RIGHT]) {
//            this.currentTurnSpeed = -TURN_SPEED;
//        } else {
//            this.currentTurnSpeed = 0;
//        }
//
//        if (Input.keys[GLFW_KEY_SPACE]) {
//            this.movement = MovementType.JUMP;
//            this.jump();
//        }
//        if (Input.keys[GLFW_KEY_LEFT_SHIFT]) {
//            this.upwardsSpeed = -2;
//        }
    }


    public int getHealth() {
        return health;
    }

    public float getSprintTime() {
        return (float) sprintTime / 5;
    }


}

