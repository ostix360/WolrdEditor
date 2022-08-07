package com.jme3.bullet.control;

import com.jme3.bullet.*;
import com.jme3.bullet.objects.*;
import com.jme3.bullet.util.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.world.*;
import org.joml.*;

public class TerrainControl extends PhysicsRigidBody implements PhysicsControl{
    /**
     * spatial to which this control is added, or null if none
     */
    protected Terrain terrain;
    /**
     * true&rarr;control is enabled, false&rarr;control is disabled
     */
    protected boolean enabled = true;
    /**
     * true&rarr;body is added to the physics space, false&rarr;not added
     */
    protected boolean added = false;
    /**
     * space to which the body is (or would be) added
     */
    protected PhysicsSpace space = null;


    public TerrainControl(Terrain ter,float mass) {
        this.terrain = ter;
        this.mass = mass;
    }

    @Override
    public void setPhysicsSpace(PhysicsSpace newSpace) {
        if (space == newSpace) {
            return;
        }
        if (added) {
            space.removeCollisionObject(this);
            added = false;
        }
        if (newSpace != null && isEnabled()) {
            newSpace.addCollisionObject(this);
            added = true;
        }
        /*
         * If this control isn't enabled, its body will be
         * added to the new space when the control becomes enabled.
         */
        space = newSpace;
    }

    @Override
    public void setSpatial(Entity e) {
        throw new RuntimeException("Unavailable");
    }

    public void setSpatial(Terrain t) {
        this.terrain = t;
        setUserObject(t);
        if (t == null) {
            return;
        }
        if (collisionShape == null) {
            createCollisionShape();
            rebuildRigidBody();
        }
        setPhysicsLocation(getSpatialTranslation());
        setPhysicsRotation(getSpatialRotation());
    }

    protected void createCollisionShape() {
        if (terrain == null) {
            return;
        }
//        if (spatial instanceof Geometry) {
//            Geometry geom = (Geometry) spatial;
//            Mesh mesh = geom.getMesh();
//            if (mesh instanceof Sphere) {
//                collisionShape = new SphereCollisionShape(((Sphere) mesh).getRadius());
//                return;
//            } else if (mesh instanceof Box) {
//                collisionShape = new BoxCollisionShape(new Vector3f(((Box) mesh).getXExtent(), ((Box) mesh).getYExtent(), ((Box) mesh).getZExtent()));
//                return;
//            }
//        }

        collisionShape = CollisionShapeFactory.createMeshShape(terrain);

    }

    @Override
    public PhysicsSpace getPhysicsSpace() {
        return space;
    }

    @Override
    public void update(float tpf) {

    }

    @Override
    public void setEnabled(boolean state) {
        this.enabled = enabled;
        if (space != null) {
            if (enabled && !added) {
                if (terrain != null) {
                    setPhysicsLocation(getSpatialTranslation());
                    setPhysicsRotation(getSpatialRotation());
                }
                space.addCollisionObject(this);
                added = true;
            } else if (!enabled && added) {
                space.removeCollisionObject(this);
                added = false;
            }
        }
    }

    private Vector3f getSpatialTranslation(){
//        if(motionState.isApplyPhysicsLocal()){
//            return entity.getLocalTranslation();
//        }
        return new Vector3f(terrain.getX(),0,terrain.getZ());
    }

    /**
     * Access whichever spatial rotation corresponds to the physics rotation.
     *
     * @return the pre-existing quaternion (not null)
     */
    private Quaternionf getSpatialRotation(){
//        if(motionState.isApplyPhysicsLocal()){
//            return spatial.getLocalRotation();
//        }
        return new Quaternionf();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
