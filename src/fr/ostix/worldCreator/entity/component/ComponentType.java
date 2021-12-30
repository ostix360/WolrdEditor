package fr.ostix.worldCreator.entity.component;


import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.ai.*;
import fr.ostix.worldCreator.entity.component.animation.*;
import fr.ostix.worldCreator.entity.component.collision.*;
import fr.ostix.worldCreator.entity.component.light.*;
import fr.ostix.worldCreator.entity.component.particle.*;

public enum ComponentType {
    COLLISION_COMPONENT("Collision Component", new CollisionCreator(), 0),
    PARTICLE_COMPONENT("Particle Component", new ParticleCreator(), 7),
    AI_COMPONENT("AI Component", new AICreator(), 1),
    ANIMATED_COMPONENT("Animated Component", new AnimationCreator(), 0),
    LIGHT_COMPONENT("Light Component", new LightCreator(), 4);
    private final String name;
    private final ComponentCreator creator;
    private final int nbLine;

    ComponentType(String name, ComponentCreator creator, int nbLine) {
        this.name = name;
        this.creator = creator;
        this.nbLine = nbLine;
    }


    public Component loadComponent(Entity e, String component) {
        return this.creator.loadComponent(component, e);
    }

    public int getNbLine() {
        return nbLine;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
