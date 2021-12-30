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

import com.flowpowered.react.*;
import com.flowpowered.react.math.*;
import org.joml.*;

/**
 * Represents a sphere collision shape that is centered at the origin and defined by its radius. This collision shape does not have an explicit object margin distance. The margin is implicitly the
 * radius of the sphere. Therefore, there is no need to specify an object margin for a sphere shape.
 */
public class SphereShape extends CollisionShape {
    private float mRadius;

    /**
     * Constructs a new sphere from the radius.
     *
     * @param radius The radius
     */
    public SphereShape(float radius) {
        super(CollisionShapeType.SPHERE, radius);
        mRadius = radius;
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than zero");
        }
    }

    /**
     * Copy constructor.
     *
     * @param shape The shape to copy
     */
    public SphereShape(SphereShape shape) {
        super(shape);
        mRadius = shape.mRadius;
    }

    /**
     * Gets the radius.
     *
     * @return The radius
     */
    public float getRadius() {
        return mRadius;
    }

    @Override
    public Vector3 getLocalSupportPointWithMargin(Vector3 direction) {
        if (direction.lengthSquare() >= ReactDefaults.MACHINE_EPSILON * ReactDefaults.MACHINE_EPSILON) {
            return Vector3.multiply(mMargin, direction.getUnit());
        }
        return new Vector3(0, mMargin, 0);
    }

    @Override
    public Vector3 getLocalSupportPointWithoutMargin(Vector3 direction) {
        return new Vector3(0, 0, 0);
    }

    @Override
    public void getLocalBounds(Vector3 min, Vector3 max) {
        max.setX(mRadius);
        max.setY(mRadius);
        max.setZ(mRadius);
        min.setX(-mRadius);
        min.setY(min.getX());
        min.setZ(min.getX());
    }

    @Override
    public void computeLocalInertiaTensor(Matrix3x3 tensor, float mass) {
        final float diag = 0.4f * mass * mRadius * mRadius;
        tensor.setAllValues(
                diag, 0, 0,
                0, diag, 0,
                0, 0, diag);
    }

    @Override
    public void updateAABB(AABB aabb, Transform transform) {
        final Vector3 extents = new Vector3(mRadius, mRadius, mRadius);
        aabb.setMin(Vector3.subtract(transform.getPosition(), extents));
        aabb.setMax(Vector3.add(transform.getPosition(), extents));
    }

    @Override
    public float getHeight() {
        return mRadius;
    }

    @Override
    public Vector3f applyCorrection() {
        return new Vector3f(0,mRadius/2,0);
    }

    @Override
    public void scale(Vector3f scale) {
        mRadius *= scale.y();
    }

    public static SphereShape load(String content) {
        float radius = Float.parseFloat(content);
        return new SphereShape(radius);
    }

    @Override
    public SphereShape clone() {
        return new SphereShape(this);
    }

    @Override
    public boolean isEqualTo(CollisionShape otherCollisionShape) {
        final SphereShape otherShape = (SphereShape) otherCollisionShape;
        return mRadius == otherShape.mRadius;
    }
}
