package fr.ostix.worldCreator.entity;


import com.flowpowered.react.math.*;
import fr.ostix.worldCreator.entity.component.*;
import fr.ostix.worldCreator.entity.component.collision.*;
import fr.ostix.worldCreator.entity.component.particle.*;
import fr.ostix.worldCreator.graphics.model.*;
import org.joml.*;

import java.util.*;


public class Entity {

    protected final Vector3 forceToCenter = new Vector3();
    protected final Vector3 torque = new Vector3();
    private final Model model;
    protected Vector3f position;
    protected Vector3f rotation;
    protected Vector3f scale;
    private Transform transform;
    protected MovementType movement;
    private CollisionComponent collision;
    private int textureIndex = 1;
    private String name;
    private String componentID;

    private List<Component> components = new ArrayList<>();
    private boolean picking = false;

    public Entity(Model model, Vector3f position, Vector3f rotation, float scale) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector3f(scale);
        this.transform = new Transform(position, rotation, scale);
    }

    public Entity(Model m, String name,String componentID) {
        this.model = m;
        this.name = name;
        this.componentID = componentID;
        this.position = new Vector3f(0);
        this.rotation = new Vector3f(0);
        this.scale = new Vector3f(1);
        this.transform = new Transform(position, rotation, 1);
    }

    public Entity(Entity entity) {
        this.model = entity.model;
        this.name = entity.name;
        this.position = new Vector3f(entity.position);
        this.rotation = new Vector3f(entity.rotation);
        this.scale = new Vector3f(entity.scale);
        this.transform = entity.transform;
        this.components = entity.components;
        this.textureIndex = entity.textureIndex;
        this.componentID = entity.componentID;
        this.collision = new CollisionComponent(this,new CollisionProperties(entity.getCollision().getProperties()));
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
        this.position = this.transform.getPosition();
        this.rotation = this.transform.getRotation();
        this.scale = this.transform.getScale();
    }

    public CollisionComponent getCollision() {
        return collision;
    }

    public void setCollision(CollisionComponent collision) {
        this.collision = collision;
    }

    public void addComponent(Component c) {
        components.add(c);
    }

    public void update() {
        for (Component c : components) {
            if (c instanceof ParticleComponent) {
               // ((ParticleComponent) c).setOffset(new Vector3f(0, 8.5f, 0));
            }
            c.update();
        }
    }

    @Override
    public Entity clone() {
        return new Entity(this);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void increasePosition(Vector3f value) {
        position.add(value);
    }

    public void increaseRotation(Vector3f value) {
        rotation.add(value);
        rotation.y %= 360;
        //transform.setRotation(rotation);
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Transform getTransform() {
        transform.setRotation(rotation);
        transform.setPosition(position);
        transform.setScale(scale);
        return transform;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public float getTextureXOffset() {
        if (model != null && model.getTexture() != null) {
            float column = textureIndex % model.getTexture().getNumbersOfRows();
            return column / model.getTexture().getNumbersOfRows();
        }
        return 1;
    }

    public float getTextureYOffset() {
        if (model != null && model.getTexture() != null) {
            float row = textureIndex / (float) model.getTexture().getNumbersOfRows();
            return row / model.getTexture().getNumbersOfRows();
        }
        return 1;
    }

    public MovementType getMovement() {
        return movement;
    }

    public void setMovement(MovementType movement) {
        this.movement = movement;
    }

    public Vector3 getForceToCenter() {
        return forceToCenter;
    }


    public Vector3 getTorque() {
        return torque.multiply(100);
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public void setPicking(boolean v) {
        this.picking = v;
    }

    public boolean isPicking() {
        return picking;
    }

    public String getComponent() {
        return this.componentID;
    }

    public enum MovementType {
        FORWARD("run"),
        BACK("back"),
        JUMP("jump"),
        STATIC("staying");
        String id;

        MovementType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
