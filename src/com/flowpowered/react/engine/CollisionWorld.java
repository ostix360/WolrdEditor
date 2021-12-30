/*
 * This file is part of React, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 Flow Powered <https://flowpowered.com/>
 * Original ReactPhysics3D C++ library by Daniel Chappuis <http://danielchappuis.ch>
 * React is re-licensed with permission from ReactPhysics3D author.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.flowpowered.react.engine;

import com.flowpowered.react.Utilities.*;
import com.flowpowered.react.body.*;
import com.flowpowered.react.collision.*;
import com.flowpowered.react.collision.RayCaster.*;
import com.flowpowered.react.collision.shape.*;
import com.flowpowered.react.constraint.ContactPoint.*;
import com.flowpowered.react.math.*;
import gnu.trove.stack.*;
import gnu.trove.stack.array.*;

import java.util.*;

/**
 * Represents a world where it is possible to move bodies by hand and to test collisions between them. In this kind of world the body movement is not computed using the laws of physics.
 */
public abstract class CollisionWorld {
    protected final CollisionDetection mCollisionDetection;
    protected final Set<CollisionBody> mBodies = new HashSet<>();
    protected final List<CollisionShape> mCollisionShapes = new ArrayList<>();
    protected final Map<IntPair, OverlappingPair> mOverlappingPairs = new HashMap<>();
    protected int mCurrentBodyID = 0;
    protected final TIntStack mFreeBodiesIDs = new TIntArrayStack();

    /**
     * Constructs a new empty collision world.
     */
    protected CollisionWorld() {
        mCollisionDetection = new CollisionDetection(this);
    }

    /**
     * Notifies the world about a new broad-phase overlapping pair.
     *
     * @param addedPair The pair that was added
     */
    public abstract void notifyAddedOverlappingPair(BroadPhasePair addedPair);

    /**
     * Notifies the world about a removed broad-phase overlapping pair.
     *
     * @param removedPair The pair that was removed
     */
    public abstract void notifyRemovedOverlappingPair(BroadPhasePair removedPair);

    /**
     * Notifies the world about a new narrow-phase contact.
     *
     * @param pair The pair of bodies in contact
     * @param contactInfo The information for the contact
     */
    public abstract void notifyNewContact(BroadPhasePair pair, ContactPointInfo contactInfo);

    /**
     * Updates the overlapping pair.
     *
     * @param pair The pair to update
     */
    public abstract void updateOverlappingPair(BroadPhasePair pair);

    /**
     * Gets the set of the bodies of the physics world.
     *
     * @return The {@link Set} of {@link CollisionBody}
     */
    public Set<CollisionBody> getBodies() {
        return mBodies;
    }

    /**
     * Finds the closest of the bodies in the world intersecting with the ray to the ray start. The ray is defined by a starting point and a direction. This method returns an {@link IntersectedBody}
     * object containing the body and the intersection point.
     *
     * @param rayStart The ray starting point
     * @param rayDir The ray direction
     * @return The closest body to the ray start and its intersection point
     */
    public IntersectedBody findClosestIntersectingBody(Vector3 rayStart, Vector3 rayDir) {
        return RayCaster.findClosestIntersectingBody(rayStart, rayDir, mBodies);
    }

    /**
     * Finds the furthest of the bodies in the world intersecting with the ray from the ray start. The ray is defined by a starting point and a direction. This method returns an {@link
     * IntersectedBody} object containing the body and the intersection point.
     *
     * @param rayStart The ray starting point
     * @param rayDir The ray direction
     * @return The furthest body from the ray start and its intersection point
     */
    public IntersectedBody findFurthestIntersectingBody(Vector3 rayStart, Vector3 rayDir) {
        return RayCaster.findFurthestIntersectingBody(rayStart, rayDir, mBodies);
    }

    /**
     * Finds all of the bodies in the world intersecting with the ray. The ray is defined by a starting point and a direction. The bodies are returned mapped with the closest intersection point.
     *
     * @param rayStart The ray starting point
     * @param rayDir The ray direction
     * @return All of the intersection bodies, in no particular order, mapped to the distance vector
     */
    public Map<CollisionBody, Vector3> findIntersectingBodies(Vector3 rayStart, Vector3 rayDir) {
        return RayCaster.findIntersectingBodies(rayStart, rayDir, mBodies);
    }

    /**
     * Returns the next available body ID for this world.
     *
     * @return The next available id
     * @throws IllegalStateException If the id for the body is greater than Integer.MAX_VALUE
     */
    public int getNextFreeID() {
        final int bodyID;
        if (mFreeBodiesIDs.size() != 0) {
            bodyID = mFreeBodiesIDs.pop();
        } else {
            bodyID = mCurrentBodyID;
            mCurrentBodyID++;
        }
        if (bodyID >= Integer.MAX_VALUE) {
            throw new IllegalStateException("body id cannot be larger or equal to the largest integer");
        }
        return bodyID;
    }

    /**
     * Creates a new collision shape. First, this methods checks that the new collision shape does not exist yet in the world. If it already exists, we do not allocate memory for a new one but instead
     * we reuse the existing one. The goal is to only allocate memory for a single collision shape if this one is used for several bodies in the world.
     *
     * @param collisionShape The collision shape to create
     * @return The desired collision shape
     */
    protected CollisionShape createCollisionShape(CollisionShape collisionShape) {
        for (CollisionShape shape : mCollisionShapes) {
            if (collisionShape.equals(shape)) {
                shape.incrementNbSimilarCreatedShapes();
                return shape;
            }
        }
        final CollisionShape newCollisionShape = collisionShape.clone();
        mCollisionShapes.add(newCollisionShape);
        newCollisionShape.incrementNbSimilarCreatedShapes();
        return newCollisionShape;
    }

    /**
     * Removes a collision shape. First, we check if another body is still using the same collision shape. If so, we keep the allocated collision shape. If it is not the case, we can deallocate the
     * memory associated with the collision shape.
     *
     * @param collisionShape The collision shape to remove
     */
    protected void removeCollisionShape(CollisionShape collisionShape) {
        if (collisionShape.getNbSimilarCreatedShapes() == 0) {
            throw new IllegalStateException("Expected at least one similar collision shape remaining");
        }
        collisionShape.decrementNbSimilarCreatedShapes();
        if (collisionShape.getNbSimilarCreatedShapes() == 0) {
            mCollisionShapes.remove(collisionShape);
        }
    }
}
