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
package com.flowpowered.react.collision.shape;

import com.flowpowered.react.math.*;
import com.flowpowered.react.math.Transform;
import fr.ostix.worldCreator.entity.*;
import org.joml.*;

/**
 * Represents the collision shape associated with a body that is used during the narrow-phase collision detection.
 */
public abstract class CollisionShape extends BoundingModel {
    protected final CollisionShapeType mType;
    private int mNbSimilarCreatedShapes;
    protected final float mMargin;

    /**
     * Constructs a new collision shape from its type.
     *
     * @param type The type of the collision shape
     */
    protected CollisionShape(CollisionShapeType type, float margin) {
        super(null);
        mType = type;
        mNbSimilarCreatedShapes = 0;
        mMargin = margin;
    }

    /**
     * Copy constructor.
     *
     * @param shape The shape to copy
     */
    protected CollisionShape(CollisionShape shape) {
        super(null);
        mType = shape.mType;
        mNbSimilarCreatedShapes = shape.mNbSimilarCreatedShapes;
        mMargin = shape.mMargin;
    }

    /**
     * Gets the type of collision shape associated to this shape.
     *
     * @return The collision shape type
     */
    public CollisionShapeType getType() {
        return mType;
    }

    /**
     * Gets the margin distance around the shape.
     *
     * @return The margin for the shape
     */
    public float getMargin() {
        return mMargin;
    }

    /**
     * Gets a local support point in a given direction with the object margin.
     *
     * @param direction The desired direction
     * @return The local support point as a vector3
     */
    public abstract Vector3 getLocalSupportPointWithMargin(Vector3 direction);

    /**
     * Gets a local support point in a given direction without the object margin.
     *
     * @param direction The desired direction
     * @return The local support point as a vector3
     */
    public abstract Vector3 getLocalSupportPointWithoutMargin(Vector3 direction);

    /**
     * Gets the local extents in x,y and z direction.
     *
     * @param min Where to store the minimum point of the bounds
     * @param max Where to store the maximum point of the bounds
     */
    public abstract void getLocalBounds(Vector3 min, Vector3 max);

    /**
     * Computes the local inertia tensor of the collision shape for the mass. Stores the results in the passed matrix3x3.
     *
     * @param tensor The matrix3x3 in which the tensor should be stored
     * @param mass The mass of the shape
     */
    public abstract void computeLocalInertiaTensor(Matrix3x3 tensor, float mass);

    /**
     * Allocates and returns a copy of the object.
     *
     * @return A copy of the objects
     */
    @Override
    public abstract CollisionShape clone();

    /**
     * Tests equality between two collision shapes of the same type (same derived classes).
     *
     * @param otherCollisionShape The shape to test for equality
     */
    public abstract boolean isEqualTo(CollisionShape otherCollisionShape);

    /**
     * Update the AABB of a body using its collision shape.
     *
     * @param aabb The AABB to update
     * @param transform The AABB's transform
     */
    public void updateAABB(AABB aabb, Transform transform) {
        final Vector3 minBounds = new Vector3();
        final Vector3 maxBounds = new Vector3();
        getLocalBounds(minBounds, maxBounds);
        final Matrix3x3 worldAxis = transform.getOrientation().getMatrix().getAbsoluteMatrix();
        final Vector3 worldMinBounds = new Vector3(
                worldAxis.getColumn(0).dot(minBounds),
                worldAxis.getColumn(1).dot(minBounds),
                worldAxis.getColumn(2).dot(minBounds));
        final Vector3 worldMaxBounds = new Vector3(
                worldAxis.getColumn(0).dot(maxBounds),
                worldAxis.getColumn(1).dot(maxBounds),
                worldAxis.getColumn(2).dot(maxBounds));
        final Vector3 minCoordinates = Vector3.add(transform.getPosition(), worldMinBounds);
        final Vector3 maxCoordinates = Vector3.add(transform.getPosition(), worldMaxBounds);
        aabb.setMin(minCoordinates);
        aabb.setMax(maxCoordinates);
    }

    public abstract float getHeight();

    public abstract Vector3f applyCorrection();

    /**
     * Returns the number of similar created shapes.
     *
     * @return The number of similar created shapes
     */
    public int getNbSimilarCreatedShapes() {
        return mNbSimilarCreatedShapes;
    }

    /**
     * Increments the number of similar allocated collision shapes.
     */
    public void incrementNbSimilarCreatedShapes() {
        mNbSimilarCreatedShapes++;
    }

    /**
     * Decrements the number of similar allocated collision shapes.
     */
    public void decrementNbSimilarCreatedShapes() {
        mNbSimilarCreatedShapes--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollisionShape)) {
            return false;
        }
        final CollisionShape that = (CollisionShape) o;
        return mMargin == that.mMargin && mType == that.mType && that.isEqualTo(this);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mNbSimilarCreatedShapes != 0) {
                // Thrown exceptions are ignored, so we need to print instead
                System.err.println("The number of similar created shapes should be 0, is " + mNbSimilarCreatedShapes + " instead");
            }
        } finally {
            super.finalize();
        }
    }

    public abstract void scale(Vector3f scale);

    /**
     * An enumeration of the possible collision shape (box, sphere, cone and cylinder).
     */
    public enum CollisionShapeType {
        BOX("Box"),
        SPHERE("Sphere"),
        CONE("Cone"),
        CYLINDER("Cylinder"),
        CAPSULE("Capsule"),
        CONVEX_MESH("Convex");

        private final String name;

        CollisionShapeType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
