package fr.ostix.worldCreator.graphics.particles;

import fr.ostix.worldCreator.entity.camera.*;
import org.joml.*;

import java.lang.Math;

public class Particle {
    private final Vector3f position;
    private final Vector3f velocity;
    private final float lifeLenght;
    private final float gravityEffect;
    private final float rotation;
    private final float scale;
    private final ParticleTarget target;

    private final ParticleTexture texture;
    private final Vector2f offsets1 = new Vector2f();
    private final Vector2f offsets2 = new Vector2f();
    private float blend;

    private float distance;

    private float elapsedTime = 0;

    private final Vector3f reusableChange = new Vector3f();

    public Particle(Vector3f position, Vector3f velocity, ParticleTexture texture, float lifeLenght, float gravityEffect, float rotation, float scale, ParticleTarget target) {
        this.position = position;
        this.velocity = velocity;
        this.texture = texture;
        this.lifeLenght = lifeLenght;
        this.gravityEffect = gravityEffect;
        this.rotation = rotation;
        this.scale = scale;
        this.target = target;
        MasterParticle.addParticle(this);
    }

    protected boolean isInLife(Camera cam) {
        velocity.y += gravityEffect * 0.1f;
        if (target != null) {
            Vector3f magnet = target.getForce(this.position);
            if (magnet != null) {
                velocity.add(magnet);
            }
        }
        reusableChange.set(velocity);
        reusableChange.mul(0.1f);
        position.add(reusableChange);
        distance = new Vector3f(cam.getPosition()).sub(position).lengthSquared();
        updateTextureCoordsInfo();
        elapsedTime += 1;
        return elapsedTime < lifeLenght;
    }

    private void updateTextureCoordsInfo() {
        float lifeFactor = elapsedTime / lifeLenght;
        int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
        float atlasProgression = lifeFactor * stageCount;
        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount ? index1 - 1 : index1;
        this.blend = atlasProgression % 1;
        setTextureOffset(offsets1, index1);
        setTextureOffset(offsets2, index2);
    }

    private void setTextureOffset(Vector2f offsets, int index) {
        int colomn = index % texture.getNumberOfRows();
        int row = index / texture.getNumberOfRows();
        offsets.x = (float) colomn / texture.getNumberOfRows();
        offsets.y = (float) row / texture.getNumberOfRows();
    }

    public float getDistance() {
        return distance;
    }

    public Vector2f getOffsets1() {
        return offsets1;
    }

    public Vector2f getOffsets2() {
        return offsets2;
    }

    public float getBlend() {
        return blend;
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    public Vector3f getPosition() {
        return new Vector3f(position.x(), position.y(), position.z());
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }
}
