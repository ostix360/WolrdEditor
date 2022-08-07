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
package com.jme3.bullet.control;

import com.jme3.bullet.*;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.objects.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.toolBox.*;
import org.joml.*;

/**
 * You might want to try <code>BetterCharacterControl</code> as well.
 * @author normenhansen
 */
public class CharacterControl extends PhysicsCharacter implements PhysicsControl {

    protected Entity entity;
    protected boolean enabled = true;
    protected boolean added = false;
    protected PhysicsSpace space = null;
    protected Vector3f viewDirection = new Vector3f(Maths.UNIT_Z);
    protected boolean useViewDirection = true;
    protected boolean applyLocal = false;

    public CharacterControl() {
    }

    public CharacterControl(CollisionShape shape, float stepHeight) {
        super(shape, stepHeight);
    }

    public boolean isApplyPhysicsLocal() {
        return applyLocal;
    }

    /**
     * When set to true, the physics coordinates will be applied to the local
     * translation of the Spatial
     *
     * @param applyPhysicsLocal true&rarr;match local coordinates,
     * false&rarr;match world coordinates (default=false)
     */
    public void setApplyPhysicsLocal(boolean applyPhysicsLocal) {
        applyLocal = applyPhysicsLocal;
    }

    private Vector3f getSpatialTranslation() {
//        if (applyLocal) {
//            return entity.getLocalTranslation();
//        }
        return entity.getPosition();
    }

//    @Deprecated
//    @Override
//    public Control cloneForSpatial(Spatial spatial) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public Object jmeClone() {
//        CharacterControl control = new CharacterControl(collisionShape, stepHeight);
//        control.setCcdMotionThreshold(getCcdMotionThreshold());
//        control.setCcdSweptSphereRadius(getCcdSweptSphereRadius());
//        control.setCollideWithGroups(getCollideWithGroups());
//        control.setCollisionGroup(getCollisionGroup());
//        control.setContactResponse(isContactResponse());
//        control.setFallSpeed(getFallSpeed());
//        control.setGravity(getGravity());
//        control.setJumpSpeed(getJumpSpeed());
//        control.setMaxSlope(getMaxSlope());
//        control.setPhysicsLocation(getPhysicsLocation());
//        control.setUpAxis(getUpAxis());
//        control.setApplyPhysicsLocal(isApplyPhysicsLocal());
//        control.spatial = this.spatial;
//        control.setEnabled(isEnabled());
//        return control;
//    }
//
//    @Override
//    public void cloneFields( Cloner cloner, Object original ) {
//        this.spatial = cloner.clone(spatial);
//    }
//
//    @Override
//    public void setSpatial(Spatial spatial) {
//        this.spatial = spatial;
//        setUserObject(spatial);
//        if (spatial == null) {
//            return;
//        }
//        setPhysicsLocation(getSpatialTranslation());
//    }

    /**
     * @return returns the spatial the control is added to, or null if the control is not attached to a spatial yet.
     */
    public Entity getEntity(){
        return this.entity;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (space != null) {
            if (enabled && !added) {
                if (entity != null) {
                    warp(getSpatialTranslation());
                }
                space.addCollisionObject(this);
                added = true;
            } else if (!enabled && added) {
                space.removeCollisionObject(this);
                added = false;
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setViewDirection(Vector3f vec) {
        viewDirection.set(vec);
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public boolean isUseViewDirection() {
        return useViewDirection;
    }

    public void setUseViewDirection(boolean viewDirectionEnabled) {
        this.useViewDirection = viewDirectionEnabled;
    }

   // @Override
    public void update(float tpf) {
        if (enabled && entity != null) {
            Quaternionf localRotationQuat = entity.getTransform().getQRotation();
//            Vector3f localLocation = entity.getLocalTranslation();
//            if (!applyLocal && entity.getParent() != null) {
//                getPhysicsLocation(localLocation);
//                localLocation.sub(entity.getParent().getWorldTranslation());
//                localLocation.div(entity.getParent().getWorldScale());
//                tmp_inverseWorldRotation.set(entity.getParent().getWorldRotation()).inverse().mul(localLocation);
//                entity.setLocalTranslation(localLocation);
//
//                if (useViewDirection) {
//                    localRotationQuat.lookAt(viewDirection, Maths.UNIT_Y);
//                    entity.setLocalRotation(localRotationQuat);
//                }
//            } else {
                entity.setPosition(getPhysicsLocation());
                localRotationQuat.lookAlong(viewDirection, Maths.UNIT_Y);
                entity.getTransform().setQ(localRotationQuat);
//            }
        }
    }

//    @Override
//    public void render(RenderManager rm, ViewPort vp) {
//    }

    /**
     * If enabled, add this control's physics object to the specified physics
     * space. If not enabled, alter where the object would be added. The object
     * is removed from any other space it's currently in.
     *
     * @param newSpace where to add, or null to simply remove
     */
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
         * If this control isn't enabled, its physics object will be
         * added to the new space when the control becomes enabled.
         */
        space = newSpace;
    }

    @Override
    public void setSpatial(Entity e) {
        this.entity = e;
    }

    @Override
    public PhysicsSpace getPhysicsSpace() {
        return space;
    }

//    @Override
//    public void write(JmeExporter ex) throws IOException {
//        super.write(ex);
//        OutputCapsule oc = ex.getCapsule(this);
//        oc.write(enabled, "enabled", true);
//        oc.write(applyLocal, "applyLocalPhysics", false);
//        oc.write(useViewDirection, "viewDirectionEnabled", true);
//        oc.write(viewDirection, "viewDirection", new Vector3f(Vector3f.UNIT_Z));
//        oc.write(spatial, "spatial", null);
//    }
//
//    @Override
//    public void read(JmeImporter im) throws IOException {
//        super.read(im);
//        InputCapsule ic = im.getCapsule(this);
//        enabled = ic.readBoolean("enabled", true);
//        useViewDirection = ic.readBoolean("viewDirectionEnabled", true);
//        viewDirection = (Vector3f) ic.readSavable("viewDirection", new Vector3f(Vector3f.UNIT_Z));
//        applyLocal = ic.readBoolean("applyLocalPhysics", false);
//        spatial = (Spatial) ic.readSavable("spatial", null);
//        setUserObject(spatial);
//    }
}
