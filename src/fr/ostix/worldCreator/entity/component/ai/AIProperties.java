package fr.ostix.worldCreator.entity.component.ai;

import org.joml.*;

public class AIProperties {
    private final float updatePerSecond;
    private final float speed;
    private final float speedError;
    private final float speedTurn;
    private final float speedTurnError;
    private final float rotateProbabilities;
    private final float staticTime;

    private final Vector3f pos;
    private final float distance;

    public AIProperties(float updatePerSecond, float speed, float speedError, float speedTurn, float speedTurnError, float rotateProbabilities, float staticTime, Vector3f pos, float distance) {
        this.updatePerSecond = updatePerSecond * 60;
        this.speed = speed;
        this.speedError = speedError;
        this.speedTurn = speedTurn;
        this.speedTurnError = speedTurnError;
        this.rotateProbabilities = rotateProbabilities;
        this.staticTime = staticTime;
        this.pos = pos;
        this.distance = distance;
    }

    public float getRotateProbabilities() {
        return rotateProbabilities;
    }

    public float getSpeedError() {
        return speedError;
    }

    public float getUpdate() {
        return updatePerSecond;
    }

    public float getSpeed() {
        return speed;
    }

    public float getSpeedTurn() {
        return speedTurn;
    }

    public float getSpeedTurnError() {
        return speedTurnError;
    }

    public float getStaticTime() {
        return staticTime;
    }
}
