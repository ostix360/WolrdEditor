package fr.ostix.worldCreator.toolBox.interaction;

import com.flowpowered.react.body.*;
import com.flowpowered.react.collision.*;
import com.flowpowered.react.collision.shape.*;
import com.flowpowered.react.engine.*;
import com.flowpowered.react.math.Quaternion;
import com.flowpowered.react.math.Transform;
import com.flowpowered.react.math.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.world.*;
import gnu.trove.list.*;
import gnu.trove.list.array.*;
import org.joml.Math;
import org.joml.*;

import java.util.*;

public class CollisionSystem {

    private static final Material PHYSICS_MATERIAL = Material.asUnmodifiableMaterial
            (new Material(0.0f, 1.0f));

    private final Vector3 gravity = new Vector3(0, -9.81f, 0);
    private final Map<Entity, List<CollisionBody>> shapes = new HashMap<>();
    private final Map<CollisionBody, Entity> shapes2 = new HashMap<>();
    private final Map<Entity, RigidBody> motionShape = new HashMap<>();
    private static final Map<CollisionBody, Entity> aabbs = new HashMap<>();
    private final TFloatList meshPositions = new TFloatArrayList();
    private final TIntList meshIndices = new TIntArrayList();
    private DynamicsWorld dynamicsWorld;
    private World world;

    public void init(float timeStep, List<Entity> entities, World world) {
        dynamicsWorld = new DynamicsWorld(gravity, timeStep);
        //addImmobileBody(entities.get(0), new BoxShape(new Vector3(25, 1, 25)), 100, new Vector3(0, 1.8f, 0), Quaternion.identity()).setMaterial(PHYSICS_MATERIAL);
        addAllEntity(entities);
        dynamicsWorld.enableSleeping(false);
        // addTerrain(terrains);
        this.world = world;
        dynamicsWorld.start();
    }


    private void addAllEntity(List<Entity> entities) {
        for (Entity e : entities) {
//            if (e.isUseBondingModels()) {
//                int index = 0;
//                for (BoundingModel b : e.getBoundingModels()) {
//                    for (int i = 0; i < b.getModel().getVAO().getPosition().length; i++) {
//                        meshPositions.add(b.getModel().getVAO().getPosition()[i] * b.getScale());
//                    }
//                    meshIndices.addAll(b.getModel().getVAO().getIndices());
//                    TFloatList positions = new TFloatArrayList(meshPositions);
//                    TIntList indices = new TIntArrayList(meshIndices);
//                    MeshGenerator.toWireframe(positions, indices, false);
//                    final ConvexMeshShape meshShape = new ConvexMeshShape(positions.toArray(), positions.size() / 3, 12);
//                    for (int i = 0; i < indices.size(); i += 2) {
//                        meshShape.addEdge(indices.get(i), indices.get(i + 1));
//                    }
//                    meshShape.setIsEdgesInformationUsed(true);
//                    addBody(e, meshShape, 100, index);
//
//                    meshPositions.clear();
//                    meshIndices.clear();
//                    index++;
//                }
//                       } else {

            if (e.getCollision() != null) {
                for (BoundingModel b : e.getCollision().getProperties().getBoundingModels()) {
                    if (b instanceof CollisionShape) {
                        //Transform t = new Transform(b.getTransform());
                        this.addBody(e, (CollisionShape) b);
                    } else {
                        //...
                    }
                }
            }

//            for (int i = 0; i < e.getModel().getMeshModel().getVAO().getPosition().length; i++) {
//                meshPositions.add(e.getModel().getMeshModel().getVAO().getPosition()[i] * e.getScale());
//            }
//            meshIndices.addAll(e.getModel().getMeshModel().getVAO().getIndices());
//            TFloatList positions = new TFloatArrayList(meshPositions);
//            TIntList indices = new TIntArrayList(meshIndices);
//            MeshGenerator.toWireframe(positions, indices, false);
//            final ConvexMeshShape meshShape = new ConvexMeshShape(positions.toArray(), positions.size() / 3, 12);
//            for (int i = 0; i < indices.size(); i += 2) {
//                meshShape.addEdge(indices.get(i), indices.get(i + 1));
//            }
//            meshShape.setIsEdgesInformationUsed(true);
//            addBody(e, meshShape, 100);
//
//            meshPositions.clear();
//            meshIndices.clear();
        }
        World.doAABBToRender();
    }

    public void update(List<Entity> entities) {
        boolean contain;
        for (Entity e : entities) {
            contain = false;
            for (Entity e1 : shapes.keySet()) {
                if (e1.equals(e)) {
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                if (e.getCollision() != null) {
                    for (BoundingModel b : e.getCollision().getProperties().getBoundingModels()) {
                        if (b instanceof CollisionShape) {
                            //Transform t = new Transform(b.getTransform());

                            CollisionShape b1 = ((CollisionShape) b).clone();
                            if (b1 == null) {
                                System.out.println(b + " is nullllll");
                            }

                            assert b1 != null;
                            b1.setTransform(b.getTransform());
                            this.addBody(e, b1);
                        } else {
                            //...
                        }
                    }
                }
            }
        }
        //dynamicsWorld.update();
    }



    public void finish() {
        dynamicsWorld.stop();
        shapes.clear();
        shapes2.clear();
    }

    private void addBody(CollisionShape shape) {
        RigidBody body = dynamicsWorld.createRigidBody(new Transform(new Vector3(50, 0, 0),
                        Quaternion.identity()),
                (float) 100, shape);

        body.enableMotion(false);
        //body.enableMotion(true);
        body.enableGravity(true);
        body.enableCollision(true);
        body.setMaterial(PHYSICS_MATERIAL);
        addBody(body, null);
    }

    public void spawnBody(Entity e) {
        if (e.getCollision() != null) {
            for (BoundingModel b : e.getCollision().getProperties().getBoundingModels()) {
                if (b instanceof CollisionShape) {
                    //Transform t = new Transform(b.getTransform());

                    CollisionShape b1 = ((CollisionShape)b).clone();
                    if (b1 == null) {
                        System.out.println(b + " is nullllll");
                    }

                    assert b1 != null;
                    b1.setTransform(b.getTransform());
                    this.addBody(e, b1);
                } else {
                    //...
                }
            }
        }

    }

    public void removeBody(Entity e) {
        if (e == null) {
            return;
        }
        List<CollisionBody> bodies = shapes.remove(e);

        if (bodies != null) {
            for (CollisionBody b : bodies) {
                dynamicsWorld.destroyRigidBody((RigidBody) b);

                world.remove(aabbs.remove(b), true);
                shapes2.remove(b);
            }
        }
    }

    private void addBody(Entity e, CollisionShape shape) {
        fr.ostix.worldCreator.entity.Transform trans = shape.getTransform();


        shape.scale(e.getScale());

        final Vector3 pos = Maths.toVector3(e.getPosition().add(trans.getPosition().mul(e.getScale(), new Vector3f()),
                new Vector3f()).add(shape.applyCorrection(), new Vector3f()));

        AxisAngle4d angles = new AxisAngle4d();
        Quaternionf q = new Quaternionf();
        q.rotateLocalY(Math.toRadians(e.getRotation().y() * 2));
        trans.getTransformation().getRotation(angles);

        Quaternionf q2 = new Quaternionf(angles);
        q.add(q2);
        Transform theTransform = new Transform(pos, new Quaternion(q.x(), q.y(), q.z(), q.w()));
        //e.getTransform().getTransformation().getUnnormalizedRotation(q);
        RigidBody body = dynamicsWorld.createRigidBody(theTransform,
                (float) 1, shape);


        body.enableMotion(e.getCollision().getProperties().canMove());
        //body.enableMotion(true);
        body.enableGravity(true);
        body.enableCollision(true);
        body.setMaterial(PHYSICS_MATERIAL);
        addBody(body, e);


    }

    private void addBody(RigidBody body, Entity e) {
        if (body.isMotionEnabled() && e instanceof Player) {
            motionShape.put(e, body);
        }
        List<CollisionBody> batch = shapes.get(e);
        if (batch != null) {
            batch.add(body);
        } else {
            List<CollisionBody> newBatch = new ArrayList<>();
            newBatch.add(body);
            shapes.put(e, newBatch);
        }
        shapes2.put(body, e);
        AABB aabb = body.getAABB();
        Transform bodyTransform = body.getTransform();
        Vector3 bodyPosition = bodyTransform.getPosition();
        Entity aabbModel = World.addAABB(bodyPosition, Vector3.subtract(aabb.getMax(), aabb.getMin()).divide(6));
        aabbs.put(body, aabbModel);
    }

    public Entity findEntityInRay(Camera cam, Vector3f currentRay) {
        final RayCaster.IntersectedBody targeted = dynamicsWorld.findClosestIntersectingBody(
                Maths.toVector3(cam.getPosition().add(0, -5f, 0, new Vector3f()))
                , Maths.toVector3(currentRay));
        if (targeted != null) {
            return shapes2.get(targeted.getBody());
        }
        return null;
    }

    public void refresh(List<Entity> entities) {
        for (Entity e : entities) {
            removeBody(e);
        }
        for (Entity e : entities) {
            spawnBody(e);
        }
        World.doAABBToRender();
    }
}
