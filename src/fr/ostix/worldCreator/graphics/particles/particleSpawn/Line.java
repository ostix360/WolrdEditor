package fr.ostix.worldCreator.graphics.particles.particleSpawn;

import fr.ostix.worldCreator.toolBox.*;
import org.joml.Vector3f;

public class Line implements ParticleSpawn {

    private Vector3f axis;
    private float length;

    @Override
    public Vector3f getParticleSpawnPosition(float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
        Vector3f actualAxis = Maths.rotateVector(this.axis, rotX, rotY, rotZ);
        actualAxis.normalize();
        actualAxis.mul(this.length * scale);
        actualAxis.mul(this.random.nextFloat() - 0.5F);
        Vector3f offset = new Vector3f(x, y, z);
        offset.add(actualAxis, actualAxis);
        return actualAxis;
    }

    @Override
    public void load(String[] values) {
        this.length = Float.parseFloat(values[1]);
        this.axis = new Vector3f(Float.parseFloat(values[2]), Float.parseFloat(values[3]), Float.parseFloat(values[4]));
    }

    public Line setAxis(Vector3f axis) {
        this.axis = axis;
        return this;
    }

    public Line setLength(float length) {
        this.length = length;
        return this;
    }
}
