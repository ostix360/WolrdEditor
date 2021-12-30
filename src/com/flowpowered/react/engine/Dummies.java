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

import com.flowpowered.react.body.*;
import com.flowpowered.react.collision.*;
import com.flowpowered.react.collision.shape.*;
import com.flowpowered.react.constraint.ContactPoint.*;
import com.flowpowered.react.math.*;

import java.util.*;

/**
 * Provides fake implementations and objects for tests.
 */
public class Dummies {
    private static final Random RANDOM = new Random();
    /*
           2------4
         / |    / |
       6------7   |
       |   0--|---1
       | /    | /
       3------5
     */
    private static final float[] CUBE_MESH_VERTICES = {
            -1, -1, -1,
            1, -1, -1,
            -1, 1, -1,
            -1, -1, 1,
            1, 1, -1,
            1, -1, 1,
            -1, 1, 1,
            1, 1, 1
    };
    private static final int NB_CUBE_MESH_VERTICES = CUBE_MESH_VERTICES.length / 3;
    private static final int CUBE_MESH_VERTEX_STRIDE = 3 * 4;
    private static final int[][] CUBE_MESH_EDGES = {
            {0, 1}, {0, 2}, {0, 3},
            {1, 4}, {1, 5},
            {2, 4}, {2, 6},
            {3, 5}, {3, 6},
            {7, 4}, {7, 5}, {7, 6}
    };

    public static Vector3 newPosition() {
        return new Vector3(RANDOM.nextInt(21) - 10, RANDOM.nextInt(21) - 10, RANDOM.nextInt(21) - 10);
    }

    public static Quaternion newOrientation() {
        final float phi = RANDOM.nextFloat() * 2 * (float) Math.PI;
        final float theta = RANDOM.nextFloat() * 2 * (float) Math.PI;
        final float x = (float) Math.sin(theta) * (float) Math.cos(phi);
        final float y = (float) Math.sin(theta) * (float) Math.sin(phi);
        final float z = (float) Math.cos(theta);
        final float halfAngle = (float) Math.toRadians(RANDOM.nextFloat() * 2 * Math.PI) / 2;
        final float q = (float) Math.sin(halfAngle);
        return new Quaternion(x * q, y * q, z * q, (float) Math.cos(halfAngle));
    }

    public static Transform newTransform() {
        return new Transform(newPosition(), newOrientation());
    }

    public static CollisionBody newCollisionBody(int id) {
        return new RigidBody(Transform.identity(), 0, Matrix3x3.identity(), new BoxShape(new Vector3(1, 1, 1)), id);
    }

    public static AABB newAABB() {
        final Vector3 min = newPosition();
        return new AABB(min, Vector3.add(min, new Vector3(RANDOM.nextInt(5) + 4, RANDOM.nextInt(5) + 4, RANDOM.nextInt(5) + 4)));
    }

    public static AABB newIntersectingAABB(AABB with) {
        final Vector3 wMin = with.getMin();
        final Vector3 wSize = Vector3.subtract(with.getMax(), wMin);
        final int iSizeX = RANDOM.nextInt((int) wSize.getX() + 1);
        final int iSizeY = RANDOM.nextInt((int) wSize.getY() + 1);
        final int iSizeZ = RANDOM.nextInt((int) wSize.getZ() + 1);
        final int eSizeX = RANDOM.nextInt(5) + 4;
        final int eSizeY = RANDOM.nextInt(5) + 4;
        final int eSizeZ = RANDOM.nextInt(5) + 4;
        final Vector3 min = Vector3.subtract(wMin, new Vector3(eSizeX, eSizeY, eSizeZ));
        final Vector3 max = Vector3.add(wMin, new Vector3(iSizeX, iSizeY, iSizeZ));
        return new AABB(min, max);
    }

    public static BoxShape newBoxShape() {
        return new BoxShape(new Vector3(RANDOM.nextInt(5) + 4, RANDOM.nextInt(5) + 4, RANDOM.nextInt(5) + 4));
    }

    public static ConeShape newConeShape() {
        return new ConeShape(RANDOM.nextInt(5) + 4, RANDOM.nextInt(5) + 4);
    }

    public static CylinderShape newCylinderShape() {
        return new CylinderShape(RANDOM.nextInt(5) + 4, RANDOM.nextInt(5) + 4);
    }

    public static SphereShape newSphereShape() {
        return new SphereShape(RANDOM.nextInt(5) + 4);
    }

    public static CapsuleShape newCapsuleShape() {
        return new CapsuleShape(RANDOM.nextInt(5) + 4, RANDOM.nextInt(5) + 4);
    }

    public static ConvexMeshShape newConvexMeshShape() {
        final ConvexMeshShape shape = new ConvexMeshShape(CUBE_MESH_VERTICES, NB_CUBE_MESH_VERTICES, CUBE_MESH_VERTEX_STRIDE);
        for (int[] edge : CUBE_MESH_EDGES) {
            shape.addEdge(edge[0], edge[1]);
        }
        shape.setIsEdgesInformationUsed(true);
        return shape;
    }

    public static CollisionShape newCollisionShape() {
        switch (RANDOM.nextInt(6)) {
            case 0:
                return newBoxShape();
            case 1:
                return newConeShape();
            case 2:
                return newCylinderShape();
            case 3:
                return newSphereShape();
            case 4:
                return newCapsuleShape();
            case 5:
                return newConvexMeshShape();
            default:
                throw new IllegalStateException("random int larger than shape types count");
        }
    }

    public static CollisionWorld newCollisionWorld() {
        return new DummyCollisionWorld();
    }

    public static CollisionDetection newCollisionDetection() {
        return new CollisionDetection(newCollisionWorld());
    }

    private static class DummyCollisionWorld extends CollisionWorld {
        private DummyCollisionWorld() {
        }

        @Override
        public void notifyAddedOverlappingPair(BroadPhasePair addedPair) {
        }

        @Override
        public void notifyRemovedOverlappingPair(BroadPhasePair removedPair) {
        }

        @Override
        public void notifyNewContact(BroadPhasePair pair, ContactPointInfo contactInfo) {
        }

        @Override
        public void updateOverlappingPair(BroadPhasePair pair) {
        }
    }
}
