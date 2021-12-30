package fr.ostix.worldCreator.entity;


import com.flowpowered.react.collision.shape.*;

public class BoundingModel {

    private final CollisionModel m;
    private Transform transform;

    public BoundingModel(CollisionModel m) {
        this.m = m;
    }

    public static BoundingModel load(String content) {
        String[] lines = content.split("\n");
        Transform transform = Transform.load(lines[0]);
        CollisionModel cm = loadModel(lines[1]);
        return new BoundingModel(cm).setTransform(transform);
    }

    @Override
    public BoundingModel clone(){
       return new BoundingModel(this.m).setTransform(this.getTransform());
    }

    private static CollisionModel loadModel(String name) {
        return null;
    }

    public CollisionModel getModel() {
        return m;
    }

    public Transform getTransform() {
        return transform;
    }

    public BoundingModel setTransform(Transform transform) {
        this.transform = transform;
        return this;
    }
}
