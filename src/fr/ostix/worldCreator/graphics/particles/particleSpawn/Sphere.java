package fr.ostix.worldCreator.graphics.particles.particleSpawn;

import fr.ostix.worldCreator.toolBox.*;
import org.joml.*;

import java.util.Random;

public class Sphere implements ParticleSpawn {

    private float radius;

    @Override
    public Vector3f getParticleSpawnPosition(float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
        Vector3f spherePoint = Maths.generateRandomUnitVector();

        spherePoint.mul(this.radius * scale);

        float fromCenter = new Random().nextFloat();

        spherePoint.mul(fromCenter);

        Vector3f offset = new Vector3f(x, y, z);

        spherePoint.add(offset, spherePoint);

        return spherePoint;
    }

    @Override
    public void load(String[] values) {
        this.radius = Float.parseFloat(values[1]);
    }

    public Sphere setRadius(float radius) {
        this.radius = radius;
        return this;
    }
}
