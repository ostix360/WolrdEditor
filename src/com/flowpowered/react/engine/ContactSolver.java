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

import com.flowpowered.react.*;
import com.flowpowered.react.body.*;
import com.flowpowered.react.constraint.*;
import com.flowpowered.react.math.*;
import gnu.trove.map.*;

import java.util.*;
import java.util.logging.*;

/**
 * Represents the contact solver that is used to solve rigid bodies contacts. The constraint solver is based on the "Sequential Impulse" technique described by Erin Catto in his GDC slides
 * (http://code.google.com/p/box2d/downloads/list). <p> A constraint between two bodies is represented by a function C(x) which is equal to zero when the constraint is satisfied. The condition C(x)=0
 * describes a valid position and the condition dC(x)/dt=0 describes a valid velocity. We have dC(x)/dt = Jv + b = 0 where J is the Jacobian matrix of the constraint, v is a vector that contains the
 * velocity of both bodies and b is the constraint bias. We are looking for a force F_c that will act on the bodies to keep the constraint satisfied. Note that from the virtual work principle, we have
 * F_c = J^t * lambda where J^t is the transpose of the Jacobian matrix and lambda is a Lagrange multiplier. Therefore, finding the force F_c is equivalent to finding the Lagrange multiplier lambda.
 * <p> An impulse P = F * dt where F is a force and dt is the timestep. We can apply impulses to a body to change its velocity. The idea of the Sequential Impulse technique is to apply impulses to the
 * bodies of each constraints in order to keep the constraint satisfied. <p> --- Step 1 --- <p> First, we integrate the applied force F_a acting on each rigid body (like gravity, ...) and we obtain
 * some new velocities v2' that tends to violate the constraints. <p> v2' = v1 + dt * M^-1 * F_a <p> where M is a matrix that contains mass and inertia tensor information. <p> --- Step 2 --- <p>
 * During the second step, we iterate over all the constraints for a certain number of iterations and for each constraint we compute the impulse to apply to the bodies needed so that the new velocity
 * of the bodies satisfy Jv + b = 0. From the Newton law, we know that M * deltaV = P_c where M is the mass of the body, deltaV is the difference of velocity and P_c is the constraint impulse to apply
 * to the body. Therefore, we have v2 = v2' + M^-1 * P_c. For each constraint, we can compute the Lagrange multiplier lambda using: lambda = -m_c (Jv2' + b) where m_c = 1 / (J * M^-1 * J^t). Now that
 * we have the Lagrange multiplier lambda, we can compute the impulse P_c = J^t * lambda * dt to apply to the bodies to satisfy the constraint. <p> --- Step 3 --- <p> In the third step, we integrate
 * the new position x2 of the bodies using the new velocities v2 computed in the second step with: x2 = x1 + dt * v2. <p> Note that in the following code (as it is also explained in the slides from
 * Erin Catto), the value lambda is not only the lagrange multiplier but is the multiplication of the Lagrange multiplier with the timestep dt. Therefore, in the following code, when we use lambda, we
 * mean (lambda * dt). <p> This implementation uses the accumulated impulse technique that is also described in the slides from Erin Catto. <p> This implementation also uses warm starting. The idea is
 * to warm start the solver at the beginning of each step by applying the last impulses for the constraints from the previous step. This allows the iterative solver to converge faster towards the
 * solution. <p> For contact constraints, this implementation also uses split impulses so that the position correction, which uses Baumgarte stabilization, does not change the momentum of the bodies.
 * <p> There are two ways to apply the friction constraints. Either the friction constraints are applied at each contact point, or they are applied only at the center of the contact manifold between
 * two bodies. If we solve the friction constraints at each contact point, we need two constraints (two tangential friction directions), but if we solve the friction constraints at the center of the
 * contact manifold, we need two constraints for tangential friction and also another twist friction constraint to prevent the body from spinning around the contact manifold center.
 */
public class ContactSolver {
    private static final float BETA = 0.2f;
    private static final float BETA_SPLIT_IMPULSE = 0.2f;
    private static final float SLOP = 0.01f;
    private final boolean mIsWarmStartingActive = true;
    private Vector3[] mSplitLinearVelocities;
    private Vector3[] mSplitAngularVelocities;
    private float mTimeStep;
    private ContactManifoldSolver[] mContactConstraints;
    private int mNbContactManifolds;
    private final Set<RigidBody> mConstraintBodies = new HashSet<>();
    private Vector3[] mLinearVelocities;
    private Vector3[] mAngularVelocities;
    private final TObjectIntMap<RigidBody> mMapBodyToConstrainedVelocityIndex;
    private boolean mIsSplitImpulseActive = true;
    private boolean mIsSolveFrictionAtContactManifoldCenterActive = true;

    /**
     * Constructs a new contact solver from the body to velocity index map.
     *
     * @param mapBodyToVelocityIndex The body to velocity index map
     */
    public ContactSolver(TObjectIntMap<RigidBody> mapBodyToVelocityIndex) {
        mSplitLinearVelocities = null;
        mSplitAngularVelocities = null;
        mContactConstraints = null;
        mLinearVelocities = null;
        mAngularVelocities = null;
        mMapBodyToConstrainedVelocityIndex = mapBodyToVelocityIndex;
    }

    /**
     * Sets the split velocities arrays.
     *
     * @param splitLinearVelocities The split linear velocities
     * @param splitAngularVelocities The split angular velocities
     */
    public void setSplitVelocitiesArrays(Vector3[] splitLinearVelocities, Vector3[] splitAngularVelocities) {
        if (splitLinearVelocities == null) {
            throw new IllegalArgumentException("The constrained linear velocities cannot be null");
        }
        if (splitAngularVelocities == null) {
            throw new IllegalArgumentException("The constrained angular velocities cannot be null");
        }
        mSplitLinearVelocities = splitLinearVelocities;
        mSplitAngularVelocities = splitAngularVelocities;
    }

    /**
     * Sets the constrained velocities arrays.
     *
     * @param constrainedLinearVelocities The constrained linear velocities
     * @param constrainedAngularVelocities The constrained angular velocities
     */
    public void setConstrainedVelocitiesArrays(Vector3[] constrainedLinearVelocities, Vector3[] constrainedAngularVelocities) {
        if (constrainedLinearVelocities == null) {
            throw new IllegalArgumentException("The constrained linear velocities cannot be null");
        }
        if (constrainedAngularVelocities == null) {
            throw new IllegalArgumentException("The constrained angular velocities cannot be null");
        }
        mLinearVelocities = constrainedLinearVelocities;
        mAngularVelocities = constrainedAngularVelocities;
    }

    /**
     * Returns true if the split impulses position correction technique is used for contacts.
     *
     * @return Whether or not the split impulses position correction technique is used
     */
    public boolean isSplitImpulseActive() {
        return mIsSplitImpulseActive;
    }

    /**
     * Activates or deactivates the split impulses for contacts.
     *
     * @param isActive True if the split impulses are active, false if not
     */
    public void setIsSplitImpulseActive(boolean isActive) {
        mIsSplitImpulseActive = isActive;
    }

    /**
     * Activates or deactivates the solving of friction constraints at the center of the contact manifold instead of solving them at each contact point.
     *
     * @param isActive Whether or not to solve the friction constraint at the center of the manifold
     */
    public void setSolveFrictionAtContactManifoldCenterActive(boolean isActive) {
        mIsSolveFrictionAtContactManifoldCenterActive = isActive;
    }

    // Computes the collision restitution factor from the restitution factor of each body.
    private float computeMixedRestitutionFactor(RigidBody body1, RigidBody body2) {
        final float restitution1 = body1.getMaterial().getBounciness();
        final float restitution2 = body2.getMaterial().getBounciness();
        return (restitution1 > restitution2) ? restitution1 : restitution2;
    }

    // Computes the mixed friction coefficient from the friction coefficient of each body.
    private float computeMixedFrictionCoefficient(RigidBody body1, RigidBody body2) {
        return (float) Math.sqrt(body1.getMaterial().getFrictionCoefficient() * body2.getMaterial().getFrictionCoefficient());
    }

    // Computes a penetration constraint impulse.
    private Impulse computePenetrationImpulse(float deltaLambda, ContactPointSolver contactPoint) {
        return new Impulse(
                Vector3.multiply(Vector3.negate(contactPoint.normal), deltaLambda),
                Vector3.multiply(Vector3.negate(contactPoint.r1CrossN), deltaLambda),
                Vector3.multiply(contactPoint.normal, deltaLambda),
                Vector3.multiply(contactPoint.r2CrossN, deltaLambda));
    }

    // Computes the first friction constraint impulse.
    private Impulse computeFriction1Impulse(float deltaLambda, ContactPointSolver contactPoint) {
        return new Impulse(
                Vector3.multiply(Vector3.negate(contactPoint.frictionVector1), deltaLambda),
                Vector3.multiply(Vector3.negate(contactPoint.r1CrossT1), deltaLambda),
                Vector3.multiply(contactPoint.frictionVector1, deltaLambda),
                Vector3.multiply(contactPoint.r2CrossT1, deltaLambda));
    }

    // Computes the second friction constraint impulse.
    private Impulse computeFriction2Impulse(float deltaLambda, ContactPointSolver contactPoint) {
        return new Impulse(
                Vector3.multiply(Vector3.negate(contactPoint.frictionVector2), deltaLambda),
                Vector3.multiply(Vector3.negate(contactPoint.r1CrossT2), deltaLambda),
                Vector3.multiply(contactPoint.frictionVector2, deltaLambda),
                Vector3.multiply(contactPoint.r2CrossT2, deltaLambda));
    }

    /**
     * Initializes the constraint solver for a given island.
     *
     * @param dt The time delta
     * @param island The island
     */
    public void initializeForIsland(float dt, Island island) {
        if (island == null) {
            throw new IllegalArgumentException("Island cannot be null");
        }
        if (island.getNbBodies() <= 0) {
            throw new IllegalArgumentException("The number of bodies in the island must be greater than zero");
        }
        if (island.getNbContactManifolds() <= 0) {
            throw new IllegalArgumentException("The number of contact manifolds in the island must be greater than zero");
        }
        if (mLinearVelocities == null) {
            throw new IllegalStateException("Linear velocities cannot be null");
        }
        if (mAngularVelocities == null) {
            throw new IllegalStateException("Angular velocities cannot be null");
        }
        mTimeStep = dt;
        mNbContactManifolds = island.getNbContactManifolds();
        mContactConstraints = new ContactManifoldSolver[mNbContactManifolds];
        final ContactManifold[] contactManifolds = island.getContactManifolds();
        for (int i = 0; i < mNbContactManifolds; i++) {
            final ContactManifold externalManifold = contactManifolds[i];
            final ContactManifoldSolver internalManifold = new ContactManifoldSolver();
            mContactConstraints[i] = internalManifold;
            if (externalManifold.getNbContactPoints() <= 0) {
                throw new IllegalStateException("external manifold must have at least one contact point");
            }
            final RigidBody body1 = externalManifold.getContactPoint(0).getFirstBody();
            final RigidBody body2 = externalManifold.getContactPoint(0).getSecondBody();
            final Vector3 x1 = body1.getTransform().getPosition();
            final Vector3 x2 = body2.getTransform().getPosition();
            internalManifold.indexBody1 = mMapBodyToConstrainedVelocityIndex.get(body1);
            internalManifold.indexBody2 = mMapBodyToConstrainedVelocityIndex.get(body2);
            internalManifold.inverseInertiaTensorBody1.set(body1.getInertiaTensorInverseWorld());
            internalManifold.inverseInertiaTensorBody2.set(body2.getInertiaTensorInverseWorld());
            internalManifold.isBody1Moving = body1.isMotionEnabled();
            internalManifold.isBody2Moving = body2.isMotionEnabled();
            internalManifold.massInverseBody1 = body1.getMassInverse();
            internalManifold.massInverseBody2 = body2.getMassInverse();
            internalManifold.nbContacts = externalManifold.getNbContactPoints();
            internalManifold.restitutionFactor = computeMixedRestitutionFactor(body1, body2);
            internalManifold.frictionCoefficient = computeMixedFrictionCoefficient(body1, body2);
            internalManifold.externalContactManifold = externalManifold;
            if (mIsSolveFrictionAtContactManifoldCenterActive) {
                internalManifold.frictionPointBody1.setAllValues(0, 0, 0);
                internalManifold.frictionPointBody2.setAllValues(0, 0, 0);
            }
            for (int c = 0; c < externalManifold.getNbContactPoints(); c++) {
                if (internalManifold.contacts[c] == null) {
                    internalManifold.contacts[c] = new ContactPointSolver();
                }
                final ContactPointSolver contactPoint = internalManifold.contacts[c];
                final ContactPoint externalContact = externalManifold.getContactPoint(c);
                final Vector3 p1 = externalContact.getWorldPointOnFirstBody();
                final Vector3 p2 = externalContact.getWorldPointOnSecondBody();
                contactPoint.externalContact = externalContact;
                contactPoint.normal.set(externalContact.getNormal());
                contactPoint.r1.set(Vector3.subtract(p1, x1));
                contactPoint.r2.set(Vector3.subtract(p2, x2));
                contactPoint.penetrationDepth = externalContact.getPenetrationDepth();
                contactPoint.isRestingContact = externalContact.isRestingContact();
                externalContact.setRestingContact(true);
                contactPoint.oldFrictionVector1.set(externalContact.getFirstFrictionVector());
                contactPoint.oldFrictionVector2.set(externalContact.getSecondFrictionVector());
                contactPoint.penetrationImpulse = 0;
                contactPoint.friction1Impulse = 0;
                contactPoint.friction2Impulse = 0;
                if (mIsSolveFrictionAtContactManifoldCenterActive) {
                    internalManifold.frictionPointBody1.add(p1);
                    internalManifold.frictionPointBody2.add(p2);
                }
            }
            if (mIsSolveFrictionAtContactManifoldCenterActive) {
                internalManifold.frictionPointBody1.divide(internalManifold.nbContacts);
                internalManifold.frictionPointBody2.divide(internalManifold.nbContacts);
                internalManifold.r1Friction.set(Vector3.subtract(internalManifold.frictionPointBody1, x1));
                internalManifold.r2Friction.set(Vector3.subtract(internalManifold.frictionPointBody2, x2));
                internalManifold.oldFrictionVector1.set(externalManifold.getFirstFrictionVector());
                internalManifold.oldFrictionVector2.set(externalManifold.getSecondFrictionVector());
                if (mIsWarmStartingActive) {
                    internalManifold.friction1Impulse = externalManifold.getFirstFrictionImpulse();
                    internalManifold.friction2Impulse = externalManifold.getSecondFrictionImpulse();
                    internalManifold.frictionTwistImpulse = externalManifold.getFrictionTwistImpulse();
                } else {
                    internalManifold.friction1Impulse = 0;
                    internalManifold.friction2Impulse = 0;
                    internalManifold.frictionTwistImpulse = 0;
                }
            }
        }
        initializeContactConstraints();
    }

    // Initializes the contact constraints before solving the system.
    private void initializeContactConstraints() {
        for (int c = 0; c < mNbContactManifolds; c++) {
            final ContactManifoldSolver manifold = mContactConstraints[c];
            final Matrix3x3 I1 = manifold.inverseInertiaTensorBody1;
            final Matrix3x3 I2 = manifold.inverseInertiaTensorBody2;
            if (mIsSolveFrictionAtContactManifoldCenterActive) {
                manifold.normal.setAllValues(0, 0, 0);
            }
            final Vector3 v1 = mLinearVelocities[manifold.indexBody1];
            final Vector3 w1 = mAngularVelocities[manifold.indexBody1];
            final Vector3 v2 = mLinearVelocities[manifold.indexBody2];
            final Vector3 w2 = mAngularVelocities[manifold.indexBody2];
            for (int i = 0; i < manifold.nbContacts; i++) {
                final ContactPointSolver contactPoint = manifold.contacts[i];
                final ContactPoint externalContact = contactPoint.externalContact;
                final Vector3 deltaV = Vector3.subtract(
                        Vector3.subtract(Vector3.add(v2, w2.cross(contactPoint.r2)), v1),
                        w1.cross(contactPoint.r1));
                contactPoint.r1CrossN.set(contactPoint.r1.cross(contactPoint.normal));
                contactPoint.r2CrossN.set(contactPoint.r2.cross(contactPoint.normal));
                float massPenetration = 0;
                if (manifold.isBody1Moving) {
                    massPenetration += manifold.massInverseBody1
                            + Matrix3x3.multiply(I1, contactPoint.r1CrossN).cross(contactPoint.r1).dot(contactPoint.normal);
                }
                if (manifold.isBody2Moving) {
                    massPenetration += manifold.massInverseBody2
                            + Matrix3x3.multiply(I2, contactPoint.r2CrossN).cross(contactPoint.r2).dot(contactPoint.normal);
                }
                contactPoint.inversePenetrationMass = massPenetration > 0 ? 1 / massPenetration : 0;
                if (!mIsSolveFrictionAtContactManifoldCenterActive) {
                    computeFrictionVectors(deltaV, contactPoint);
                    contactPoint.r1CrossT1.set(contactPoint.r1.cross(contactPoint.frictionVector1));
                    contactPoint.r1CrossT2.set(contactPoint.r1.cross(contactPoint.frictionVector2));
                    contactPoint.r2CrossT1.set(contactPoint.r2.cross(contactPoint.frictionVector1));
                    contactPoint.r2CrossT2.set(contactPoint.r2.cross(contactPoint.frictionVector2));
                    float friction1Mass = 0;
                    float friction2Mass = 0;
                    if (manifold.isBody1Moving) {
                        friction1Mass += manifold.massInverseBody1
                                + Matrix3x3.multiply(I1, contactPoint.r1CrossT1).cross(contactPoint.r1).dot(contactPoint.frictionVector1);
                        friction2Mass += manifold.massInverseBody1
                                + Matrix3x3.multiply(I1, contactPoint.r1CrossT2).cross(contactPoint.r1).dot(contactPoint.frictionVector2);
                    }
                    if (manifold.isBody2Moving) {
                        friction1Mass += manifold.massInverseBody2
                                + Matrix3x3.multiply(I2, contactPoint.r2CrossT1).cross(contactPoint.r2).dot(contactPoint.frictionVector1);
                        friction2Mass += manifold.massInverseBody2
                                + Matrix3x3.multiply(I2, contactPoint.r2CrossT2).cross(contactPoint.r2).dot(contactPoint.frictionVector2);
                    }
                    contactPoint.inverseFriction1Mass = friction1Mass > 0 ? 1 / friction1Mass : 0;
                    contactPoint.inverseFriction2Mass = friction2Mass > 0 ? 1 / friction2Mass : 0;
                }
                contactPoint.restitutionBias = 0;
                final float deltaVDotN = deltaV.dot(contactPoint.normal);
                if (deltaVDotN < -ReactDefaults.RESTITUTION_VELOCITY_THRESHOLD) {
                    contactPoint.restitutionBias = manifold.restitutionFactor * deltaVDotN;
                }
                if (mIsWarmStartingActive) {
                    contactPoint.penetrationImpulse = externalContact.getPenetrationImpulse();
                    contactPoint.friction1Impulse = externalContact.getFirstFrictionImpulse();
                    contactPoint.friction2Impulse = externalContact.getSecondFrictionImpulse();
                }
                contactPoint.penetrationSplitImpulse = 0;
                if (mIsSolveFrictionAtContactManifoldCenterActive) {
                    manifold.normal.add(contactPoint.normal);
                }
            }
            if (mIsSolveFrictionAtContactManifoldCenterActive) {
                manifold.normal.normalize();
                final Vector3 deltaVFrictionPoint = Vector3.subtract(
                        Vector3.subtract(Vector3.add(v2, w2.cross(manifold.r2Friction)), v1),
                        w1.cross(manifold.r1Friction));
                computeFrictionVectors(deltaVFrictionPoint, manifold);
                manifold.r1CrossT1.set(manifold.r1Friction.cross(manifold.frictionVector1));
                manifold.r1CrossT2.set(manifold.r1Friction.cross(manifold.frictionVector2));
                manifold.r2CrossT1.set(manifold.r2Friction.cross(manifold.frictionVector1));
                manifold.r2CrossT2.set(manifold.r2Friction.cross(manifold.frictionVector2));
                float friction1Mass = 0;
                float friction2Mass = 0;
                if (manifold.isBody1Moving) {
                    friction1Mass += manifold.massInverseBody1
                            + Matrix3x3.multiply(I1, manifold.r1CrossT1).cross(manifold.r1Friction).dot(manifold.frictionVector1);
                    friction2Mass += manifold.massInverseBody1
                            + Matrix3x3.multiply(I1, manifold.r1CrossT2).cross(manifold.r1Friction).dot(manifold.frictionVector2);
                }
                if (manifold.isBody2Moving) {
                    friction1Mass += manifold.massInverseBody2
                            + Matrix3x3.multiply(I2, manifold.r2CrossT1).cross(manifold.r2Friction).dot(manifold.frictionVector1);
                    friction2Mass += manifold.massInverseBody2
                            + Matrix3x3.multiply(I2, manifold.r2CrossT2).cross(manifold.r2Friction).dot(manifold.frictionVector2);
                }
                final float frictionTwistMass =
                        manifold.normal.dot(Matrix3x3.multiply(manifold.inverseInertiaTensorBody1, manifold.normal))
                                + manifold.normal.dot(Matrix3x3.multiply(manifold.inverseInertiaTensorBody2, manifold.normal));
                manifold.inverseFriction1Mass = friction1Mass > 0 ? 1 / friction1Mass : 0;
                manifold.inverseFriction2Mass = friction2Mass > 0 ? 1 / friction2Mass : 0;
                manifold.inverseTwistFrictionMass = frictionTwistMass > 0 ? 1 / frictionTwistMass : 0;
            }
        }
    }

    /**
     * Warm start the solver. For each constraint, we apply the previous impulse (from the previous step) at the beginning. With this technique, we will converge faster towards the solution for the
     * linear system.
     */
    public void warmStart() {
        if (!mIsWarmStartingActive) {
            return;
        }
        for (int c = 0; c < mNbContactManifolds; c++) {
            final ContactManifoldSolver contactManifold = mContactConstraints[c];
            boolean atLeastOneRestingContactPoint = false;
            for (int i = 0; i < contactManifold.nbContacts; i++) {
                final ContactPointSolver contactPoint = contactManifold.contacts[i];
                if (contactPoint.isRestingContact) {
                    atLeastOneRestingContactPoint = true;
                    // --------- Penetration --------- //
                    final Impulse impulsePenetration = computePenetrationImpulse(contactPoint.penetrationImpulse, contactPoint);
                    applyImpulse(impulsePenetration, contactManifold);
                    if (!mIsSolveFrictionAtContactManifoldCenterActive) {
                        final Vector3 oldFrictionImpulse = Vector3.add(
                                Vector3.multiply(contactPoint.friction1Impulse, contactPoint.oldFrictionVector1),
                                Vector3.multiply(contactPoint.friction2Impulse, contactPoint.oldFrictionVector2));
                        contactPoint.friction1Impulse = oldFrictionImpulse.dot(contactPoint.frictionVector1);
                        contactPoint.friction2Impulse = oldFrictionImpulse.dot(contactPoint.frictionVector2);
                        // --------- Friction 1 --------- //
                        final Impulse impulseFriction1 = computeFriction1Impulse(contactPoint.friction1Impulse, contactPoint);
                        applyImpulse(impulseFriction1, contactManifold);
                        // --------- Friction 2 --------- //
                        final Impulse impulseFriction2 = computeFriction2Impulse(contactPoint.friction2Impulse, contactPoint);
                        applyImpulse(impulseFriction2, contactManifold);
                    }
                } else {
                    contactPoint.penetrationImpulse = 0;
                    contactPoint.friction1Impulse = 0;
                    contactPoint.friction2Impulse = 0;
                }
            }
            if (mIsSolveFrictionAtContactManifoldCenterActive && atLeastOneRestingContactPoint) {
                final Vector3 oldFrictionImpulse = Vector3.add(
                        Vector3.multiply(contactManifold.friction1Impulse, contactManifold.oldFrictionVector1),
                        Vector3.multiply(contactManifold.friction2Impulse, contactManifold.oldFrictionVector2));
                contactManifold.friction1Impulse = oldFrictionImpulse.dot(contactManifold.frictionVector1);
                contactManifold.friction2Impulse = oldFrictionImpulse.dot(contactManifold.frictionVector2);
                // ------ First friction constraint at the center of the contact manifold ------ //
                Vector3 linearImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.frictionVector1), contactManifold.friction1Impulse);
                Vector3 angularImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.r1CrossT1), contactManifold.friction1Impulse);
                Vector3 linearImpulseBody2 = Vector3.multiply(contactManifold.frictionVector1, contactManifold.friction1Impulse);
                Vector3 angularImpulseBody2 = Vector3.multiply(contactManifold.r2CrossT1, contactManifold.friction1Impulse);
                final Impulse impulseFriction1 = new Impulse(
                        linearImpulseBody1, angularImpulseBody1,
                        linearImpulseBody2, angularImpulseBody2);
                applyImpulse(impulseFriction1, contactManifold);
                // ------ Second friction constraint at the center of the contact manifold ----- //
                linearImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.frictionVector2), contactManifold.friction2Impulse);
                angularImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.r1CrossT2), contactManifold.friction2Impulse);
                linearImpulseBody2 = Vector3.multiply(contactManifold.frictionVector2, contactManifold.friction2Impulse);
                angularImpulseBody2 = Vector3.multiply(contactManifold.r2CrossT2, contactManifold.friction2Impulse);
                final Impulse impulseFriction2 = new Impulse(
                        linearImpulseBody1, angularImpulseBody1,
                        linearImpulseBody2, angularImpulseBody2);
                applyImpulse(impulseFriction2, contactManifold);
                // ------ Twist friction constraint at the center of the contact manifold ------ //
                linearImpulseBody1 = new Vector3(0, 0, 0);
                angularImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.normal), contactManifold.frictionTwistImpulse);
                linearImpulseBody2 = new Vector3(0, 0, 0);
                angularImpulseBody2 = Vector3.multiply(contactManifold.normal, contactManifold.frictionTwistImpulse);
                final Impulse impulseTwistFriction = new Impulse(
                        linearImpulseBody1, angularImpulseBody1,
                        linearImpulseBody2, angularImpulseBody2);
                applyImpulse(impulseTwistFriction, contactManifold);
            } else {
                contactManifold.friction1Impulse = 0;
                contactManifold.friction2Impulse = 0;
                contactManifold.frictionTwistImpulse = 0;
            }
        }
    }

    /**
     * Solves the contact constraints by applying sequential impulses.
     */
    Logger log = Logger.getAnonymousLogger();
    public void solve() {
        float deltaLambda;
        float lambdaTemp;

        for (int c = 0; c < mNbContactManifolds; c++) {
            ContactManifoldSolver contactManifold = mContactConstraints[c];
            float sumPenetrationImpulse = 0;
            final Vector3 v1 = mLinearVelocities[contactManifold.indexBody1];
            final Vector3 w1 = mAngularVelocities[contactManifold.indexBody1];
            final Vector3 v2 = mLinearVelocities[contactManifold.indexBody2];
            final Vector3 w2 = mAngularVelocities[contactManifold.indexBody2];
            for (int i = 0; i < contactManifold.nbContacts; i++) {
                final ContactPointSolver contactPoint = contactManifold.contacts[i];
                // --------- Penetration --------- //
                Vector3 deltaV = Vector3.subtract(
                        Vector3.subtract(Vector3.add(v2, w2.cross(contactPoint.r2)), v1),
                        w1.cross(contactPoint.r1));
                final float deltaVDotN = deltaV.dot(contactPoint.normal);
                float Jv = deltaVDotN;
                final float beta = mIsSplitImpulseActive ? BETA_SPLIT_IMPULSE : BETA;
                float biasPenetrationDepth = 0;
                if (contactPoint.penetrationDepth > SLOP) {
                    biasPenetrationDepth = -(beta / mTimeStep) * Math.max(0, contactPoint.penetrationDepth - SLOP);
                }
                final float b = biasPenetrationDepth + contactPoint.restitutionBias;
                if (mIsSplitImpulseActive) {
                    deltaLambda = -(Jv + contactPoint.restitutionBias) * contactPoint.inversePenetrationMass;
                } else {
                    deltaLambda = -(Jv + b) * contactPoint.inversePenetrationMass;
                }
                lambdaTemp = contactPoint.penetrationImpulse;
                contactPoint.penetrationImpulse = Math.max(contactPoint.penetrationImpulse + deltaLambda, 0);
                deltaLambda = contactPoint.penetrationImpulse - lambdaTemp;
                //log.log(Level.INFO,"lambda 511: " + deltaLambda);
                final Impulse impulsePenetration = computePenetrationImpulse(deltaLambda, contactPoint);
                applyImpulse(impulsePenetration, contactManifold);
                sumPenetrationImpulse += contactPoint.penetrationImpulse;
                if (mIsSplitImpulseActive) {
                    final Vector3 v1Split = mSplitLinearVelocities[contactManifold.indexBody1];
                    final Vector3 w1Split = mSplitAngularVelocities[contactManifold.indexBody1];
                    final Vector3 v2Split = mSplitLinearVelocities[contactManifold.indexBody2];
                    final Vector3 w2Split = mSplitAngularVelocities[contactManifold.indexBody2];
                    final Vector3 deltaVSplit = Vector3.subtract(
                            Vector3.subtract(Vector3.add(v2Split, w2Split.cross(contactPoint.r2)), v1Split),
                            w1Split.cross(contactPoint.r1));
                    final float JvSplit = deltaVSplit.dot(contactPoint.normal);
                    final float deltaLambdaSplit = -(JvSplit + biasPenetrationDepth) * contactPoint.inversePenetrationMass;
                    final float lambdaTempSplit = contactPoint.penetrationSplitImpulse;
                    contactPoint.penetrationSplitImpulse = Math.max(contactPoint.penetrationSplitImpulse + deltaLambdaSplit, 0);
                    deltaLambda = contactPoint.penetrationSplitImpulse - lambdaTempSplit;
                    //log.log(Level.INFO,"lambda 528: " + deltaLambda);
                    final Impulse splitImpulsePenetration = computePenetrationImpulse(deltaLambdaSplit, contactPoint);
                    applySplitImpulse(splitImpulsePenetration, contactManifold);
                }
                if (!mIsSolveFrictionAtContactManifoldCenterActive) {
                    // --------- Friction 1 --------- //
                    deltaV = Vector3.subtract(
                            Vector3.subtract(Vector3.add(v2, w2.cross(contactPoint.r2)), v1),
                            w1.cross(contactPoint.r1));
                    Jv = deltaV.dot(contactPoint.frictionVector1);
                    deltaLambda = -Jv;
                    deltaLambda *= contactPoint.inverseFriction1Mass;
                    float frictionLimit = contactManifold.frictionCoefficient * contactPoint.penetrationImpulse;
                    lambdaTemp = contactPoint.friction1Impulse;
                    contactPoint.friction1Impulse = Math.max(-frictionLimit,
                            Math.min(contactPoint.friction1Impulse + deltaLambda, frictionLimit));
                    deltaLambda = contactPoint.friction1Impulse - lambdaTemp;
                    final Impulse impulseFriction1 = computeFriction1Impulse(deltaLambda, contactPoint);
                    //log.log(Level.INFO,"lambda 546: " + deltaLambda);
                    applyImpulse(impulseFriction1, contactManifold);
                    // --------- Friction 2 --------- //
                    deltaV = Vector3.subtract(
                            Vector3.subtract(Vector3.add(v2, w2.cross(contactPoint.r2)), v1),
                            w1.cross(contactPoint.r1));
                    Jv = deltaV.dot(contactPoint.frictionVector2);
                    deltaLambda = -Jv;
                    deltaLambda *= contactPoint.inverseFriction2Mass;
                    frictionLimit = contactManifold.frictionCoefficient * contactPoint.penetrationImpulse;
                    lambdaTemp = contactPoint.friction2Impulse;
                    contactPoint.friction2Impulse = Math.max(-frictionLimit,
                            Math.min(contactPoint.friction2Impulse + deltaLambda, frictionLimit));
                    deltaLambda = contactPoint.friction2Impulse - lambdaTemp;
                    final Impulse impulseFriction2 = computeFriction2Impulse(deltaLambda, contactPoint);
                    //log.log(Level.INFO,"lambda 561: " + deltaLambda);
                    applyImpulse(impulseFriction2, contactManifold);
                }
            }
            if (mIsSolveFrictionAtContactManifoldCenterActive) {
                // ------ First friction constraint at the center of the contact manifold ------ //
                Vector3 deltaV = Vector3.subtract(
                        Vector3.subtract(Vector3.add(v2, w2.cross(contactManifold.r2Friction)), v1),
                        w1.cross(contactManifold.r1Friction));
                float Jv = deltaV.dot(contactManifold.frictionVector1);
                deltaLambda = -Jv * contactManifold.inverseFriction1Mass;
                float frictionLimit = contactManifold.frictionCoefficient * sumPenetrationImpulse;
                lambdaTemp = contactManifold.friction1Impulse;
                contactManifold.friction1Impulse = Math.max(-frictionLimit,
                        Math.min(contactManifold.friction1Impulse + deltaLambda, frictionLimit));
                deltaLambda = contactManifold.friction1Impulse - lambdaTemp;
                Vector3 linearImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.frictionVector1), deltaLambda);
                Vector3 angularImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.r1CrossT1), deltaLambda);
                Vector3 linearImpulseBody2 = Vector3.multiply(contactManifold.frictionVector1, deltaLambda);
                Vector3 angularImpulseBody2 = Vector3.multiply(contactManifold.r2CrossT1, deltaLambda);
                //log.log(Level.INFO,"lambda 581: " + deltaLambda);
                final Impulse impulseFriction1 = new Impulse(
                        linearImpulseBody1, angularImpulseBody1,
                        linearImpulseBody2, angularImpulseBody2);
                applyImpulse(impulseFriction1, contactManifold);
                // ------ Second friction constraint at the center of the contact manifold ----- //
                deltaV = Vector3.subtract(
                        Vector3.subtract(Vector3.add(v2, w2.cross(contactManifold.r2Friction)), v1),
                        w1.cross(contactManifold.r1Friction));
                Jv = deltaV.dot(contactManifold.frictionVector2);
                deltaLambda = -Jv * contactManifold.inverseFriction2Mass;
                frictionLimit = contactManifold.frictionCoefficient * sumPenetrationImpulse;
                lambdaTemp = contactManifold.friction2Impulse;
                contactManifold.friction2Impulse = Math.max(-frictionLimit,
                        Math.min(contactManifold.friction2Impulse + deltaLambda, frictionLimit));
                deltaLambda = contactManifold.friction2Impulse - lambdaTemp;
                linearImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.frictionVector2), deltaLambda);
                angularImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.r1CrossT2), deltaLambda);
                linearImpulseBody2 = Vector3.multiply(contactManifold.frictionVector2, deltaLambda);
                angularImpulseBody2 = Vector3.multiply(contactManifold.r2CrossT2, deltaLambda);
                //log.log(Level.INFO,"lambda 601: " + deltaLambda);

                final Impulse impulseFriction2 = new Impulse(
                        linearImpulseBody1, angularImpulseBody1,
                        linearImpulseBody2, angularImpulseBody2);
                applyImpulse(impulseFriction2, contactManifold);
                // ------ Twist friction constraint at the center of the contact manifold ------ //
                deltaV = Vector3.subtract(w2, w1);
                Jv = deltaV.dot(contactManifold.normal);
                deltaLambda = -Jv * (contactManifold.inverseTwistFrictionMass);
                frictionLimit = contactManifold.frictionCoefficient * sumPenetrationImpulse;
                lambdaTemp = contactManifold.frictionTwistImpulse;
                contactManifold.frictionTwistImpulse = Math.max(-frictionLimit,
                        Math.min(contactManifold.frictionTwistImpulse + deltaLambda, frictionLimit));
                deltaLambda = contactManifold.frictionTwistImpulse - lambdaTemp;
                linearImpulseBody1 = new Vector3(0, 0, 0);
                angularImpulseBody1 = Vector3.multiply(Vector3.negate(contactManifold.normal), deltaLambda);
                linearImpulseBody2 = new Vector3(0, 0, 0);
                angularImpulseBody2 = Vector3.multiply(contactManifold.normal, deltaLambda);
                //log.log(Level.INFO,"lambda 619: " + deltaLambda);
                final Impulse impulseTwistFriction = new Impulse(
                        linearImpulseBody1, angularImpulseBody1,
                        linearImpulseBody2, angularImpulseBody2);
                applyImpulse(impulseTwistFriction, contactManifold);
            }
        }
    }

    /**
     * Stores the computed impulses to use them to warm-start the solver for the next iteration.
     */
    public void storeImpulses() {
        for (int c = 0; c < mNbContactManifolds; c++) {
            final ContactManifoldSolver manifold = mContactConstraints[c];
            for (int i = 0; i < manifold.nbContacts; i++) {
                final ContactPointSolver contactPoint = manifold.contacts[i];
                contactPoint.externalContact.setPenetrationImpulse(contactPoint.penetrationImpulse);
                contactPoint.externalContact.setFirstFrictionImpulse(contactPoint.friction1Impulse);
                contactPoint.externalContact.setSecondFrictionImpulse(contactPoint.friction2Impulse);
                contactPoint.externalContact.setFirstFrictionVector(contactPoint.frictionVector1);
                contactPoint.externalContact.setSecondFrictionVector(contactPoint.frictionVector2);
            }
            manifold.externalContactManifold.setFirstFrictionImpulse(manifold.friction1Impulse);
            manifold.externalContactManifold.setSecondFrictionImpulse(manifold.friction2Impulse);
            manifold.externalContactManifold.setFrictionTwistImpulse(manifold.frictionTwistImpulse);
            manifold.externalContactManifold.setFirstFrictionVector(manifold.frictionVector1);
            manifold.externalContactManifold.setSecondFrictionVector(manifold.frictionVector2);
        }
    }

    // Applies an impulse to the two bodies of a constraint.
    private void applyImpulse(Impulse impulse, ContactManifoldSolver manifold) {
        if (manifold.isBody1Moving) {
            mLinearVelocities[manifold.indexBody1].add(Vector3.multiply(manifold.massInverseBody1, impulse.getLinearImpulseFirstBody()));
            mAngularVelocities[manifold.indexBody1].add(Matrix3x3.multiply(manifold.inverseInertiaTensorBody1, impulse.getAngularImpulseFirstBody()));
        }
        if (manifold.isBody2Moving) {
            mLinearVelocities[manifold.indexBody2].add(Vector3.multiply(manifold.massInverseBody2, impulse.getLinearImpulseSecondBody()));
            mAngularVelocities[manifold.indexBody2].add(Matrix3x3.multiply(manifold.inverseInertiaTensorBody2, impulse.getAngularImpulseSecondBody()));
        }
    }

    // Applies an impulse to the two bodies of a constraint.
    private void applySplitImpulse(Impulse impulse, ContactManifoldSolver manifold) {
        if (manifold.isBody1Moving) {
            mSplitLinearVelocities[manifold.indexBody1].add(Vector3.multiply(manifold.massInverseBody1, impulse.getLinearImpulseFirstBody()));
            mSplitAngularVelocities[manifold.indexBody1].add(Matrix3x3.multiply(manifold.inverseInertiaTensorBody1, impulse.getAngularImpulseFirstBody()));
        }
        if (manifold.isBody2Moving) {
            mSplitLinearVelocities[manifold.indexBody2].add(Vector3.multiply(manifold.massInverseBody2, impulse.getLinearImpulseSecondBody()));
            mSplitAngularVelocities[manifold.indexBody2].add(Matrix3x3.multiply(manifold.inverseInertiaTensorBody2, impulse.getAngularImpulseSecondBody()));
        }
    }

    // Computes the two unit orthogonal vectors "t1" and "t2" that span the tangential friction plane
    // for a contact point. The two vectors have to be such that : t1 x t2 = contactNormal.
    private void computeFrictionVectors(Vector3 deltaVelocity, ContactPointSolver contactPoint) {
        if (contactPoint.normal.length() <= 0) {
            throw new IllegalArgumentException("the contact point solver normal must be greater than zero");
        }
        final Vector3 normalVelocity = Vector3.multiply(deltaVelocity.dot(contactPoint.normal), contactPoint.normal);
        final Vector3 tangentVelocity = Vector3.subtract(deltaVelocity, normalVelocity);
        final float lengthTangentVelocity = tangentVelocity.length();
        if (lengthTangentVelocity > ReactDefaults.MACHINE_EPSILON) {
            contactPoint.frictionVector1.set(Vector3.divide(tangentVelocity, lengthTangentVelocity));
        } else {
            contactPoint.frictionVector1.set(contactPoint.normal.getOneUnitOrthogonalVector());
        }
        contactPoint.frictionVector2.set(contactPoint.normal.cross(contactPoint.frictionVector1).getUnit());
    }

    // Computes the two unit orthogonal vectors "t1" and "t2" that span the tangential friction plane
    // for a contact manifold. The two vectors have to be such that : t1 x t2 = contactNormal.
    private void computeFrictionVectors(Vector3 deltaVelocity, ContactManifoldSolver contact) {
        if (contact.normal.length() <= 0) {
            throw new IllegalArgumentException("the contact manifold solver normal must be greater than zero");
        }
        final Vector3 normalVelocity = Vector3.multiply(deltaVelocity.dot(contact.normal), contact.normal);
        final Vector3 tangentVelocity = Vector3.subtract(deltaVelocity, normalVelocity);
        final float lengthTangentVelocity = tangentVelocity.length();
        if (lengthTangentVelocity > ReactDefaults.MACHINE_EPSILON) {
            contact.frictionVector1.set(Vector3.divide(tangentVelocity, lengthTangentVelocity));
        } else {
            contact.frictionVector1.set(contact.normal.getOneUnitOrthogonalVector());
        }
        contact.frictionVector2.set(contact.normal.cross(contact.frictionVector1).getUnit());
    }

    /**
     * Clean up the constraint solver. Clear the last computed data.
     */
    public void cleanup() {
        if (mContactConstraints != null) {
            mContactConstraints = null;
        }
    }

    // Contact solver internal data structure that to store all the information relative to a contact point.
    private static class ContactPointSolver {
        private float penetrationImpulse;
        private float friction1Impulse;
        private float friction2Impulse;
        private float penetrationSplitImpulse;
        private final Vector3 normal = new Vector3();
        private final Vector3 frictionVector1 = new Vector3();
        private final Vector3 frictionVector2 = new Vector3();
        private final Vector3 oldFrictionVector1 = new Vector3();
        private final Vector3 oldFrictionVector2 = new Vector3();
        private final Vector3 r1 = new Vector3();
        private final Vector3 r2 = new Vector3();
        private final Vector3 r1CrossT1 = new Vector3();
        private final Vector3 r1CrossT2 = new Vector3();
        private final Vector3 r2CrossT1 = new Vector3();
        private final Vector3 r2CrossT2 = new Vector3();
        private final Vector3 r1CrossN = new Vector3();
        private final Vector3 r2CrossN = new Vector3();
        private float penetrationDepth;
        private float restitutionBias;
        private float inversePenetrationMass;
        private float inverseFriction1Mass;
        private float inverseFriction2Mass;
        private boolean isRestingContact;
        private ContactPoint externalContact;
    }

    // Contact solver internal data structure to store all the information relative to a contact manifold.
    private static class ContactManifoldSolver {
        private int indexBody1;
        private int indexBody2;
        private float massInverseBody1;
        private float massInverseBody2;
        private final Matrix3x3 inverseInertiaTensorBody1 = new Matrix3x3();
        private final Matrix3x3 inverseInertiaTensorBody2 = new Matrix3x3();
        private boolean isBody1Moving;
        private boolean isBody2Moving;
        private final ContactPointSolver[] contacts = new ContactPointSolver[ContactManifold.MAX_CONTACT_POINTS_IN_MANIFOLD];
        private int nbContacts;
        private float restitutionFactor;
        private float frictionCoefficient;
        private ContactManifold externalContactManifold;
        private final Vector3 normal = new Vector3();
        private final Vector3 frictionPointBody1 = new Vector3();
        private final Vector3 frictionPointBody2 = new Vector3();
        private final Vector3 r1Friction = new Vector3();
        private final Vector3 r2Friction = new Vector3();
        private final Vector3 r1CrossT1 = new Vector3();
        private final Vector3 r1CrossT2 = new Vector3();
        private final Vector3 r2CrossT1 = new Vector3();
        private final Vector3 r2CrossT2 = new Vector3();
        private float inverseFriction1Mass;
        private float inverseFriction2Mass;
        private float inverseTwistFrictionMass;
        private final Vector3 frictionVector1 = new Vector3();
        private final Vector3 frictionVector2 = new Vector3();
        private final Vector3 oldFrictionVector1 = new Vector3();
        private final Vector3 oldFrictionVector2 = new Vector3();
        private float friction1Impulse;
        private float friction2Impulse;
        private float frictionTwistImpulse;
    }
}
