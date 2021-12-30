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
package com.flowpowered.react.constraint;

import com.flowpowered.react.ReactDefaults.*;
import com.flowpowered.react.body.*;
import com.flowpowered.react.constraint.ConstraintSolver.*;

/**
 * This abstract class represents a joint between two bodies.
 */
public abstract class Joint {
    protected final RigidBody mBody1;
    protected final RigidBody mBody2;
    protected final JointType mType;
    protected final JointsPositionCorrectionTechnique mPositionCorrectionTechnique;
    protected final boolean mIsCollisionEnabled;
    protected int mIndexBody1;
    protected int mIndexBody2;
    protected boolean mIsAlreadyInIsland;

    /**
     * Constructs a new joint from the provided joint info.
     *
     * @param jointInfo The joint info for this joint
     */
    public Joint(JointInfo jointInfo) {
        mBody1 = jointInfo.getFirstBody();
        mBody2 = jointInfo.getSecondBody();
        if (mBody1 == null) {
            throw new IllegalArgumentException("First body cannot be null");
        }
        if (mBody2 == null) {
            throw new IllegalArgumentException("Second body cannot be null");
        }
        mType = jointInfo.getType();
        mPositionCorrectionTechnique = jointInfo.getPositionCorrectionTechnique();
        mIsCollisionEnabled = jointInfo.isCollisionEnabled();
        mIsAlreadyInIsland = false;
    }

    /**
     * Initializes before solving the joint.
     *
     * @param constraintSolverData The related data
     */
    public abstract void initBeforeSolve(ConstraintSolverData constraintSolverData);

    /**
     * Warm-starts the joint (apply the previous impulse at the beginning of the step).
     *
     * @param constraintSolverData The related data
     */
    public abstract void warmstart(ConstraintSolverData constraintSolverData);

    /**
     * Solves the joint velocity.
     *
     * @param constraintSolverData The related data
     */
    public abstract void solveVelocityConstraint(ConstraintSolverData constraintSolverData);

    /**
     * Solves the joint position.
     *
     * @param constraintSolverData The related data
     */
    public abstract void solvePositionConstraint(ConstraintSolverData constraintSolverData);

    /**
     * Gets the first body.
     *
     * @return The first body
     */
    public RigidBody getFirstBody() {
        return mBody1;
    }

    /**
     * Gets the second body.
     *
     * @return The second body
     */
    public RigidBody getSecondBody() {
        return mBody2;
    }

    /**
     * Returns true if the joint is active, false if not.
     *
     * @return Whether or not the joint is active
     */
    public boolean isActive() {
        return mBody1.isActive() && mBody2.isActive();
    }

    /**
     * Gets the type of joint.
     *
     * @return The joint type
     */
    public JointType getType() {
        return mType;
    }

    /**
     * Returns true if the collision between the two bodies of the joint is enabled.
     *
     * @return Whether or not the collision is enabled
     */
    public boolean isCollisionEnabled() {
        return mIsCollisionEnabled;
    }

    /**
     * Returns true if the joint has already been added into an island.
     *
     * @return Whether or not the joint is already in an island
     */
    public boolean isAlreadyInIsland() {
        return mIsAlreadyInIsland;
    }

    /**
     * Sets whether or not this joint has already been added into an island.
     *
     * @param isAlreadyInIsland Whether or not the joint is already in an island
     */
    public void setIsAlreadyInIsland(boolean isAlreadyInIsland) {
        mIsAlreadyInIsland = isAlreadyInIsland;
    }

    /**
     * An enumeration of the possible joint types (contact).
     */
    public enum JointType {
        CONTACT,
        BALLSOCKETJOINT,
        SLIDERJOINT,
        HINGEJOINT,
        FIXEDJOINT
    }

    /**
     * This structure is used to gather the information needed to create a joint.
     */
    public static class JointInfo {
        private final JointType type;
        private RigidBody body1;
        private RigidBody body2;
        private boolean isCollisionEnabled;
        private JointsPositionCorrectionTechnique positionCorrectionTechnique;

        /**
         * Constructs a new joint info from the joint type.
         *
         * @param type The type of this joint
         */
        public JointInfo(JointType type) {
            body1 = null;
            body2 = null;
            this.type = type;
            positionCorrectionTechnique = JointsPositionCorrectionTechnique.NON_LINEAR_GAUSS_SEIDEL;
            isCollisionEnabled = true;
        }

        /**
         * Constructs a new joint info from the joint type and the two bodies involved.
         *
         * @param body1 The first involved body
         * @param body2 The second involved body
         * @param type  The type of this joint
         */
        public JointInfo(RigidBody body1, RigidBody body2, JointType type) {
            this.body1 = body1;
            this.body2 = body2;
            this.type = type;
            positionCorrectionTechnique = JointsPositionCorrectionTechnique.NON_LINEAR_GAUSS_SEIDEL;
            isCollisionEnabled = true;
        }

        /**
         * Returns the joint type.
         *
         * @return The type
         */
        public JointType getType() {
            return type;
        }

        /**
         * Returns the first body involved in the joint.
         *
         * @return The first body
         */
        public RigidBody getFirstBody() {
            return body1;
        }

        /**
         * Sets the first body involved in the joint.
         *
         * @param body1 The The first involved body
         */
        public void setFirstBody(RigidBody body1) {
            this.body1 = body1;
        }

        /**
         * Returns the second body involved in the joint.
         *
         * @return The second body
         */
        public RigidBody getSecondBody() {
            return body2;
        }

        /**
         * Sets the second body involved in the joint.
         *
         * @param body2 The second involved body
         */
        public void setSecondBody(RigidBody body2) {
            this.body2 = body2;
        }

        /**
         * Returns true if collisions are enabled.
         *
         * @return Whether or not collisions are enabled
         */
        public boolean isCollisionEnabled() {
            return isCollisionEnabled;
        }

        /**
         * Sets collisions as enabled or not.
         *
         * @param isCollisionEnabled The new state of collision enabled
         */
        public void setCollisionEnabled(boolean isCollisionEnabled) {
            this.isCollisionEnabled = isCollisionEnabled;
        }

        /**
         * Returns the joints position correction technique.
         *
         * @return The correction technique
         */
        public JointsPositionCorrectionTechnique getPositionCorrectionTechnique() {
            return positionCorrectionTechnique;
        }

        /**
         * Sets the position correction technique.
         *
         * @param positionCorrectionTechnique The position correction technique
         */
        public void setPositionCorrectionTechnique(JointsPositionCorrectionTechnique positionCorrectionTechnique) {
            this.positionCorrectionTechnique = positionCorrectionTechnique;
        }
    }

    /**
     * This structure represents a single element of a linked list of joints.
     */
    public static class JointListElement {
        private Joint joint;
        private JointListElement next;

        /**
         * Constructs a new joint list element from the initial joint and next list element.
         *
         * @param initJoint The joint
         * @param initNext  The next element
         */
        public JointListElement(Joint initJoint, JointListElement initNext) {
            joint = initJoint;
            next = initNext;
        }

        /**
         * Returns the joint in this list element.
         *
         * @return The joint
         */
        public Joint getJoint() {
            return joint;
        }

        /**
         * Sets the joint in this list element.
         *
         * @param joint The joint
         */
        public void setJoint(Joint joint) {
            this.joint = joint;
        }

        /**
         * Returns the next element in the list.
         *
         * @return The next element
         */
        public JointListElement getNext() {
            return next;
        }

        /**
         * Sets the next element in the list.
         *
         * @param next The next element
         */
        public void setNext(JointListElement next) {
            this.next = next;
        }
    }
}
