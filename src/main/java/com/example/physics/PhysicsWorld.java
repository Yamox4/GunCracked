package com.example.physics;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * High-level physics world manager Integrates the physics engine with the scene
 * graph
 */
public class PhysicsWorld extends BaseAppState {

    private PhysicsEngine physicsEngine;
    private CollisionListener collisionListener;
    private Node physicsNode;
    private Map<String, Spatial> visualObjects;
    private Map<String, PhysicsRigidBody> physicsObjects;

    public PhysicsWorld() {
        this.visualObjects = new HashMap<>();
        this.physicsObjects = new HashMap<>();
    }

    @Override
    protected void initialize(Application app) {
        physicsEngine = new PhysicsEngine();
        collisionListener = new CollisionListener();
        physicsNode = new Node("Physics Node");

        // Attach physics to application
        app.getStateManager().attach(physicsEngine.getBulletAppState());

        // Configure physics after attachment
        physicsEngine.configurePhysics();

        // Add collision listener
        physicsEngine.getPhysicsSpace().addCollisionListener(collisionListener);

        // Attach physics node to root
        ((Node) app.getViewPort().getScenes().get(0)).attachChild(physicsNode);
    }

    @Override
    protected void cleanup(Application app) {
        if (physicsEngine != null) {
            physicsEngine.cleanup();
        }
        if (collisionListener != null) {
            collisionListener.clear();
        }
        visualObjects.clear();
        physicsObjects.clear();
    }

    @Override
    protected void onEnable() {
        // Physics world is enabled
    }

    @Override
    protected void onDisable() {
        // Physics world is disabled
    }

    /**
     * Create a physics object with visual representation
     */
    public PhysicsRigidBody createPhysicsObject(String id, Spatial visual, CollisionShape shape,
            float mass, Vector3f position) {
        // Create physics body
        PhysicsRigidBody body = new PhysicsRigidBody(shape, mass);
        body.setPhysicsLocation(position);

        // Add to physics world
        physicsEngine.getPhysicsSpace().add(body);

        // Set up visual
        if (visual != null) {
            visual.setLocalTranslation(position);

            // Add rigid body control to sync physics with visual
            RigidBodyControl control = new RigidBodyControl(shape, mass);
            visual.addControl(control);
            physicsEngine.getPhysicsSpace().add(control);

            // Attach to scene
            physicsNode.attachChild(visual);
            visualObjects.put(id, visual);
        }

        // Register with collision listener
        collisionListener.registerBody(id, body);
        physicsObjects.put(id, body);

        return body;
    }

    /**
     * Create a sphere object
     */
    public PhysicsRigidBody createSphere(String id, Spatial visual, float radius,
            float mass, Vector3f position) {
        CollisionShape shape = PhysicsShapeFactory.createSphere(radius);
        return createPhysicsObject(id, visual, shape, mass, position);
    }

    /**
     * Create a box object
     */
    public PhysicsRigidBody createBox(String id, Spatial visual, Vector3f halfExtents,
            float mass, Vector3f position) {
        CollisionShape shape = PhysicsShapeFactory.createBox(halfExtents);
        return createPhysicsObject(id, visual, shape, mass, position);
    }

    /**
     * Create a capsule object
     */
    public PhysicsRigidBody createCapsule(String id, Spatial visual, float radius, float height,
            float mass, Vector3f position) {
        CollisionShape shape = PhysicsShapeFactory.createCapsule(radius, height);
        return createPhysicsObject(id, visual, shape, mass, position);
    }

    /**
     * Create a cylinder object
     */
    public PhysicsRigidBody createCylinder(String id, Spatial visual, Vector3f halfExtents,
            float mass, Vector3f position) {
        CollisionShape shape = PhysicsShapeFactory.createCylinder(halfExtents);
        return createPhysicsObject(id, visual, shape, mass, position);
    }

    /**
     * Create a cone object
     */
    public PhysicsRigidBody createCone(String id, Spatial visual, float radius, float height,
            float mass, Vector3f position) {
        CollisionShape shape = PhysicsShapeFactory.createCone(radius, height);
        return createPhysicsObject(id, visual, shape, mass, position);
    }

    /**
     * Create a ground plane
     */
    public PhysicsRigidBody createGroundPlane(String id, Spatial visual) {
        CollisionShape shape = PhysicsShapeFactory.createGroundPlane();
        return createPhysicsObject(id, visual, shape, 0, Vector3f.ZERO);
    }

    /**
     * Remove a physics object
     */
    public void removePhysicsObject(String id) {
        PhysicsRigidBody body = physicsObjects.get(id);
        if (body != null) {
            physicsEngine.getPhysicsSpace().remove(body);
            collisionListener.unregisterBody(body);
            physicsObjects.remove(id);
        }

        Spatial visual = visualObjects.get(id);
        if (visual != null) {
            visual.removeFromParent();
            visualObjects.remove(id);
        }
    }

    /**
     * Get physics engine
     */
    public PhysicsEngine getPhysicsEngine() {
        return physicsEngine;
    }

    /**
     * Get collision listener
     */
    public CollisionListener getCollisionListener() {
        return collisionListener;
    }

    /**
     * Check if physics world is ready
     */
    public boolean isPhysicsReady() {
        return physicsEngine != null;
    }

    /**
     * Get physics space
     */
    public PhysicsSpace getPhysicsSpace() {
        if (physicsEngine == null) {
            throw new IllegalStateException("PhysicsWorld not initialized yet. Make sure to attach it to StateManager first.");
        }
        return physicsEngine.getPhysicsSpace();
    }

    /**
     * Get physics object by ID
     */
    public PhysicsRigidBody getPhysicsObject(String id) {
        return physicsObjects.get(id);
    }

    /**
     * Get visual object by ID
     */
    public Spatial getVisualObject(String id) {
        return visualObjects.get(id);
    }

    /**
     * Apply force to object
     */
    public void applyForce(String id, Vector3f force) {
        physicsEngine.applyForce(id, force);
    }

    /**
     * Apply impulse to object
     */
    public void applyImpulse(String id, Vector3f impulse) {
        physicsEngine.applyImpulse(id, impulse);
    }

    /**
     * Set gravity
     */
    public void setGravity(Vector3f gravity) {
        physicsEngine.setGravity(gravity);
    }
}
