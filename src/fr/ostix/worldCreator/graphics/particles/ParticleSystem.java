package fr.ostix.worldCreator.graphics.particles;


import fr.ostix.worldCreator.graphics.particles.particleSpawn.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;

public class ParticleSystem {

    private final float pps;
    private final float averageSpeed;
    private final float gravity;
    private final float averageLifeLength;
    private final float averageScale;
    private ParticleSpawn spawn = SpawnParticleType.POINT.getSpawn();
    private Vector3f positionOffset = new Vector3f(0, 0, 0);
    private ParticleTarget target = null;


    private final ParticleTexture texture;
    private final Random random = new Random();
    private float speedError, lifeError, scaleError = 0;
    private boolean randomRotation = false;
    private Vector3f direction;
    private float directionDeviation = 0;

    public ParticleSystem(ParticleTexture texture, float pps, float speed, float gravity, float lifeLength, float scale) {
        this.texture = texture;
        this.pps = pps;
        this.averageSpeed = speed;
        this.gravity = gravity;
        this.averageLifeLength = lifeLength;
        this.averageScale = scale;
    }

    private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
        float cosAngle = (float) Math.cos(angle);
        Random random = new Random();
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));

        Vector4f direction = new Vector4f(x, y, z, 1);
        if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
            Vector3f rotateAxis = new Vector3f(coneDirection).cross(new Vector3f(0, 0, 1));
            rotateAxis.normalize();
            float rotateAngle = (float) Math.acos(new Vector3f(coneDirection).dot(new Vector3f(0, 0, 1)));
            Matrix4f rotationMatrix = new Matrix4f();
            rotationMatrix.rotate(-rotateAngle, rotateAxis);
            direction = rotationMatrix.transform(direction);
        } else if (coneDirection.z == -1) {
            direction.z *= -1;
        }
        return new Vector3f(direction.x(), direction.y(), direction.z());
    }

    public void update(Vector3f pos, Vector3f rot, Vector3f scale) {
        generateParticles(spawn.getParticleSpawnPosition(pos.x(), pos.y(), pos.z(),
                rot.x(), rot.y(), rot.z(), scale.y()));
    }

    public void setSpawn(ParticleSpawn spawn) {
        this.spawn = spawn;
    }

    public void generateParticles(Vector3f systemCenter) {
        float delta = 60;
        float particlesToCreate = pps / delta;
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 60;
        for (int i = 0; i < count; i++) {
            emitParticle(systemCenter);
        }
        if (Math.random() < partialParticle) {
            emitParticle(systemCenter);
        }
    }

    private void emitParticle(Vector3f center) {
        Vector3f velocity;
        if (direction != null) {
            velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
        } else {
            velocity = generateRandomUnitVector();
        }
        velocity.normalize();
        velocity.mul(generateValue(averageSpeed, speedError));
        float scale = generateValue(averageScale, scaleError);
        float lifeLength = generateValue(averageLifeLength, lifeError);
        Vector3f particlePos = new Vector3f(positionOffset).add(center);
        if (target != null) {
            target.updatePosition();
            Vector3f magnet = target.getForce(particlePos);
            if (magnet == null) {
                return;
            } else {
                velocity.add(magnet.mul(5));
            }
        }
        new Particle(particlePos, velocity, texture, lifeLength, gravity, generateRotation(), scale, target);
    }

    public ParticleTarget getTarget() {
        return target;
    }

    public void setTarget(ParticleTarget target) {
        this.target = target;
    }

    private float generateValue(float average, float errorMargin) {
        float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }

    private float generateRotation() {
        if (randomRotation) {
            return random.nextFloat() * 360f;
        } else {
            return 0;
        }
    }

    private Vector3f generateRandomUnitVector() {
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = (random.nextFloat() * 2) - 1;
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));
        return new Vector3f(x, y, z);
    }


    /**
     * @param direction - The average direction in which particles are emitted.
     * @param deviation - A value between 0 and 1 indicating how far from the chosen direction particles can deviate.
     */
    public void setDirection(Vector3f direction, float deviation) {
        this.direction = new Vector3f(direction);
        this.directionDeviation = (float) (deviation * Math.PI);
    }

    public void setPositionOffset(Vector3f positionOffset) {
        this.positionOffset = positionOffset;
    }

    public void randomizeRotation() {
        randomRotation = true;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setSpeedError(float error) {
        this.speedError = error * averageSpeed;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setLifeError(float error) {
        this.lifeError = error * averageLifeLength;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setScaleError(float error) {
        this.scaleError = error * averageScale;
    }

}

