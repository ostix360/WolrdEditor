package fr.ostix.worldCreator.core.ressources;

import com.flowpowered.react.collision.shape.*;
import com.flowpowered.react.math.*;

public class CollisionShapeResource {
    private final String type;
    private final Vector3 extent;
    private final float radius;
    private final float height;

    public CollisionShapeResource(String type, Vector3 extent, float radius, float height) {
        this.type = type;
        this.extent = extent;
        this.radius = radius;
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public BoxShape getBoxShape() {
        if (type.equals("box")) {
            if (extent == null) {
                new NullPointerException("for a box we need to have the parameter extent!");
                return null;
            }
            return new BoxShape(extent);
        }
        new IllegalStateException("this shape is not a box!");
        return null;
    }


    public CapsuleShape getCapsuleShape() {
        if (type.equals("capsule")) {
            if (radius <= -1) {
                new NullPointerException("for a capsule we need to have the parameter radius more than 0!");
                return null;
            }
            if (height <= 0) {
                new NullPointerException("for a capsule we need to have the parameter radius more than 0!");
                return null;
            }
            return new CapsuleShape(radius, height);
        }
        new IllegalStateException("this shape is not a capsule!");
        return null;
    }

    public ConeShape getConeShape() {
        if (type.equals("capsule")) {
            if (radius <= 0) {
                new NullPointerException("for a cone we need to have the parameter radius more than 0!");
                return null;
            }
            if (height <= 0) {
                new NullPointerException("for a cone we need to have the parameter radius more than 0!");
                return null;
            }
            return new ConeShape(radius, height);
        }
        new IllegalStateException("this shape is not a cone!");
        return null;
    }

    public CylinderShape getCylinderShape() {
        if (type.equals("capsule")) {
            if (radius <= 0) {
                new NullPointerException("for a cylinder we need to have the parameter radius more than 0!");
                return null;
            }
            if (height <= 0) {
                new NullPointerException("for a cylinder we need to have the parameter radius more than 0!");
                return null;
            }
            return new CylinderShape(radius, height);
        }
        new IllegalStateException("this shape is not a cylinder!");
        return null;
    }

    public SphereShape getSphereShape() {
        if (type.equals("capsule")) {
            if (radius <= 0) {
                new NullPointerException("for a sphere we need to have the parameter radius more than 0!");
                return null;
            }
            return new SphereShape(radius);
        }
        new IllegalStateException("this shape is not a sphere!");
        return null;
    }

}
