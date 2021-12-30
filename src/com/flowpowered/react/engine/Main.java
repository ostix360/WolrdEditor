package com.flowpowered.react.engine;

import com.flowpowered.react.*;
import com.flowpowered.react.body.*;
import com.flowpowered.react.collision.shape.*;
import com.flowpowered.react.constraint.*;
import com.flowpowered.react.math.*;

public class Main {
    private static final float RUN_TIME = 2;
    private static int beginContactCount = 0;
    private static int newContactCount = 0;

    public static void main(String[] args) throws InterruptedException {
        final float timeStep = ReactDefaults.DEFAULT_TIMESTEP;
        final DynamicsWorld world = new DynamicsWorld(new Vector3(0, -9.81f, 0), timeStep);
        world.setEventListener(new TestListener());
        world.start();
        Thread.sleep(200);
        // We want to do one update with no bodies
        world.update();
        world.stop();
        final BoxShape floorShape = new BoxShape(new Vector3(10, 0.5f, 10));
        final Transform floorTransform = new Transform(new Vector3(0, 0, 0), Quaternion.identity());
        final Matrix3x3 floorInertia = new Matrix3x3();
        final float floorMass = 100;
        floorShape.computeLocalInertiaTensor(floorInertia, floorMass);
        final RigidBody floor = world.createRigidBody(floorTransform, floorMass, floorInertia, floorShape);
        floor.enableMotion(false);
        final BoxShape boxShape = new BoxShape(new Vector3(1, 1, 1));
        final Transform boxTransform = new Transform(new Vector3(0, 5, 0), Quaternion.identity());
        final Matrix3x3 boxInertia = new Matrix3x3();
        final float boxMass = 5;
        boxShape.computeLocalInertiaTensor(boxInertia, boxMass);
        final RigidBody box = world.createRigidBody(boxTransform, boxMass, boxInertia, boxShape);
        final int stepCount = Math.round((1 / timeStep) * RUN_TIME);
        final int sleepTime = Math.round(timeStep * 1000);
        world.start();
        for (int i = 0; i < stepCount; i++) {
            final long start = System.nanoTime();
            world.update();
            final long delta = Math.round((System.nanoTime() - start) / 1000000d);
            Thread.sleep(Math.max(sleepTime - delta, 0));
        }
        world.destroyRigidBody(floor);
        world.destroyRigidBody(box);
        world.stop();

        System.out.println("There was no contact in the simulation " + (beginContactCount));
        System.out.println("There were more contacts begun than new contacts " + (newContactCount - beginContactCount));
    }


    private static class TestListener implements EventListener {
        @Override
        public void beginContact(ContactPoint.ContactPointInfo contactInfo) {
            beginContactCount++;
        }

        @Override
        public void newContact(ContactPoint.ContactPointInfo contactInfo) {
            newContactCount++;
        }
    }
}
