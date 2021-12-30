package fr.ostix.worldCreator.graphics.particles;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.toolBox.*;
import org.joml.Vector3f;

public class ParticleTarget {

    private final Entity entity;
    private final ParticleTargetProperties properties;
    private Vector3f position;

    public ParticleTarget(ParticleTargetProperties properties, Entity entity) {
        this.properties = properties;
        this.entity = entity;
        float scale = entity.getScale().y();
        Vector3f offset = Maths.rotateVector(new Vector3f(properties.getxOffset() * scale,
                        properties.getyOffset() * scale, properties.getzOffset() * scale),
                entity.getRotation().x(), entity.getRotation().y(), entity.getRotation().z());

        this.position = new Vector3f(entity.getPosition()).add(offset);
    }

    public void updatePosition() {
        float scale = entity.getScale().y();
        Vector3f offset = Maths.rotateVector(new Vector3f(properties.getxOffset() * scale,
                        properties.getyOffset() * scale, properties.getzOffset() * scale),
                entity.getRotation().x(), entity.getRotation().y(), entity.getRotation().z());

        this.position = new Vector3f(entity.getPosition()).add(offset);
    }

    protected Vector3f getForce(Vector3f particlePos) {
        Vector3f toTarget = new Vector3f(this.position).sub(particlePos);
        float distance = toTarget.length();

        if (distance <= this.properties.getCutOff()) {
            return null;
        }

        float distanceSquared = distance * distance;
        toTarget.normalize();
        toTarget.mul(this.properties.getPullFactor() / distanceSquared);
        return toTarget;
    }
}
