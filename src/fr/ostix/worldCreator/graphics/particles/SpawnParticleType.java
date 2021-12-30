package fr.ostix.worldCreator.graphics.particles;


import fr.ostix.worldCreator.graphics.particles.particleSpawn.*;

public enum SpawnParticleType {
    CIRCLE(new Circle(), "Circle spawn"),
    LINE(new Line(), "Line spawn"),
    POINT(new Point(), "Point spawn"),
    SPHERE(new Sphere(), "Sphere spawn");

    private final ParticleSpawn spawn;
    private final String description;

    SpawnParticleType(ParticleSpawn spawn, String description) {
        this.spawn = spawn;
        this.description = description;
    }

    public ParticleSpawn getSpawn() {
        return spawn;
    }

    public String getDescription() {
        return description;
    }
}
