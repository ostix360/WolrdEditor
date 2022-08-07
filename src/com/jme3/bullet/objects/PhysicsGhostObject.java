/*
 * Copyright (c) 2009-2021 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.bullet.objects;

import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.linearmath.*;
import com.jme3.bullet.collision.*;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.util.*;
import fr.ostix.worldCreator.entity.*;
import org.joml.*;

import java.util.*;

/**
 * <i>From Bullet manual:</i><br>
 * GhostObject can keep track of all objects that are overlapping.
 * By default, this overlap is based on the AABB.
 * This is useful for creating a character controller,
 * collision sensors/triggers, explosions etc.<br>
 * @author normenhansen
 */
public class PhysicsGhostObject extends PhysicsCollisionObject {

    protected PairCachingGhostObject gObject;
    protected boolean locationDirty = false;
    //TEMP VARIABLES
    protected final Quaternionf tmp_inverseWorldRotation = new Quaternionf();
    protected com.bulletphysics.linearmath.Transform tempTrans = new com.bulletphysics.linearmath.Transform (Converter.convert(new Matrix3f()));
    private fr.ostix.worldCreator.entity.Transform  physicsLocation = new fr.ostix.worldCreator.entity.Transform(new Vector3f(),new Vector3f(),1);
    protected javax.vecmath.Quat4f tempRot = new javax.vecmath.Quat4f();
    private List<PhysicsCollisionObject> overlappingObjects = new LinkedList<>();

    protected PhysicsGhostObject() {
    }

    public PhysicsGhostObject(CollisionShape shape) {
        collisionShape = shape;
        buildObject();
    }

    public PhysicsGhostObject(Entity child, CollisionShape shape) {
        collisionShape = shape;
        buildObject();
    }

    protected void buildObject() {
        if (gObject == null) {
            gObject = new PairCachingGhostObject();
            gObject.setCollisionFlags(gObject.getCollisionFlags() | CollisionFlags.NO_CONTACT_RESPONSE);
        }
        gObject.setCollisionShape(collisionShape.getCShape());
        gObject.setUserPointer(this);
    }

    @Override
    public void setCollisionShape(CollisionShape collisionShape) {
        super.setCollisionShape(collisionShape);
        if (gObject == null) {
            buildObject();
        }else{
            gObject.setCollisionShape(collisionShape.getCShape());
        }
    }

    /**
     * Sets the physics object location
     * @param location the location of the actual physics object
     */
    public void setPhysicsLocation(Vector3f location) {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(location, tempTrans.origin);
        gObject.setWorldTransform(tempTrans);
    }

    /**
     * Sets the physics object rotation
     * @param rotation the rotation of the actual physics object
     */
    public void setPhysicsRotation(Matrix3f rotation) {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(rotation, tempTrans.basis);
        gObject.setWorldTransform(tempTrans);
    }

    /**
     * Sets the physics object rotation
     * @param rotation the rotation of the actual physics object
     */
    public void setPhysicsRotation(Quaternionf rotation) {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(rotation, tempTrans.basis);
        gObject.setWorldTransform(tempTrans);
    }

    /**
     * @return the physicsLocation
     */
    public fr.ostix.worldCreator.entity.Transform getPhysicsTransform() {
        return physicsLocation;
    }

    /**
     * @param trans storage for the result (modified if not null)
     * @return the physicsLocation
     */
    public Vector3f getPhysicsLocation(Vector3f trans) {
        if (trans == null) {
            trans = new Vector3f();
        }
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.origin, physicsLocation.getPosition());
        return trans.set(physicsLocation.getPosition());
    }

    /**
     * @param rot storage for the result (modified if not null)
     * @return the physicsLocation
     */
    public Quaternionf getPhysicsRotation(Quaternionf rot) {
        if (rot == null) {
            rot = new Quaternionf();
        }
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.getRotation(tempRot), physicsLocation.getQRotation());
        return rot.set(physicsLocation.getQRotation());
    }

    /**
     * @param rot storage for the result (modified if not null)
     * @return the physicsLocation
     */
    public Matrix3f getPhysicsRotationMatrix(Matrix3f rot) {
        if (rot == null) {
            rot = new Matrix3f();
        }
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.getRotation(tempRot), physicsLocation.getQRotation());
        return rot.set(physicsLocation.getQRotation());
    }

    /**
     * @return the physicsLocation
     */
    public Vector3f getPhysicsLocation() {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.origin, physicsLocation.getPosition());
        return physicsLocation.getPosition();
    }

    /**
     * @return the physicsLocation
     */
    public Quaternionf getPhysicsRotation() {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.getRotation(tempRot), physicsLocation.getQRotation());
        return physicsLocation.getQRotation();
    }

    public Matrix3f getPhysicsRotationMatrix() {
        gObject.getWorldTransform(tempTrans);
        Converter.convert(tempTrans.getRotation(tempRot), physicsLocation.getQRotation());
        return new Matrix3f().set(physicsLocation.getQRotation());
    }

    /**
     * used internally
     * 
     * @return the pre-existing instance
     */
    public PairCachingGhostObject getObjectId() {
        return gObject;
    }

    /**
     * destroys this PhysicsGhostNode and removes it from memory
     */
    public void destroy() {
    }

    /**
     * Another Object is overlapping with this GhostNode,
     * if and if only there CollisionShapes overlaps.
     * They could be both regular PhysicsRigidBodys or PhysicsGhostObjects.
     * @return All CollisionObjects overlapping with this GhostNode.
     */
    public List<PhysicsCollisionObject> getOverlappingObjects() {
        overlappingObjects.clear();
        for (com.bulletphysics.collision.dispatch.CollisionObject collObj : gObject.getOverlappingPairs()) {
            overlappingObjects.add((PhysicsCollisionObject) collObj.getUserPointer());
        }
        return overlappingObjects;
    }

    /**
     *
     * @return With how many other CollisionObjects this GhostNode is currently overlapping.
     */
    public int getOverlappingCount() {
        return gObject.getNumOverlappingObjects();
    }

    /**
     *
     * @param index The index of the overlapping Node to retrieve.
     * @return The Overlapping CollisionObject at the given index.
     */
    public PhysicsCollisionObject getOverlapping(int index) {
        return overlappingObjects.get(index);
    }

    public void setCcdSweptSphereRadius(float radius) {
        gObject.setCcdSweptSphereRadius(radius);
    }

    public void setCcdMotionThreshold(float threshold) {
        gObject.setCcdMotionThreshold(threshold);
    }

    public float getCcdSweptSphereRadius() {
        return gObject.getCcdSweptSphereRadius();
    }

    public float getCcdMotionThreshold() {
        return gObject.getCcdMotionThreshold();
    }

    public float getCcdSquareMotionThreshold() {
        return gObject.getCcdSquareMotionThreshold();
    }

//    @Override
//    public void write(JmeExporter e) throws IOException {
//        super.write(e);
//        OutputCapsule capsule = e.getCapsule(this);
//        capsule.write(getPhysicsLocation(new Vector3f()), "physicsLocation", new Vector3f());
//        capsule.write(getPhysicsRotationMatrix(new Matrix3f()), "physicsRotation", new Matrix3f());
//        capsule.write(getCcdMotionThreshold(), "ccdMotionThreshold", 0);
//        capsule.write(getCcdSweptSphereRadius(), "ccdSweptSphereRadius", 0);
//    }
//
//    @Override
//    public void read(JmeImporter importer) throws IOException {
//        super.read(importer);
//        InputCapsule capsule = importer.getCapsule(this);
//        buildObject();
//        setPhysicsLocation((Vector3f) capsule.readSavable("physicsLocation", new Vector3f()));
//        setPhysicsRotation(((Matrix3f) capsule.readSavable("physicsRotation", new Matrix3f())));
//        setCcdMotionThreshold(capsule.readFloat("ccdMotionThreshold", 0));
//        setCcdSweptSphereRadius(capsule.readFloat("ccdSweptSphereRadius", 0));
//    }
}
