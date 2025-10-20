package com.example.physics;

import java.util.HashMap;
import java.util.Map;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Comprehensive 3D Physics Engine using JMonkeyEngine's Bullet Physics Supports
 * collision detection for spheres, capsules, boxes, and other shapes
 */
public class PhysicsEngine {

    private BulletAppState bulletAppState;
    private PhysicsSpace physicsSpace;
    private Map<String, PhysicsRigidBody> physicsObjects;

    public PhysicsEngine() {
        this.physicsObjects = new HashMap<>();
        initializePhysics();
    }

    /**
     * Initialize the physics system
     */
    private void initializePhysics() {
        bulletAppState = new BulletAppState();
        // Note: physicsSpace will be available after BulletAppState is attached to application
    }
    
    /**
     * Configure physics settings after BulletAppState is attached
     */
    public void configurePhysics() {
        physicsSpace = bulletAppState.getPhysicsSpace();
        if (physicsSpace != null) {
            // Configure physics world settings
            physicsSpace.setGravity(new Vector3f(0, -9.81f, 0));
            physicsSpace.setAccuracy(1f / 60f); // 60 FPS physics
            physicsSpace.setMaxSubSteps(4);
        }
    }

    /**
     * Get the BulletAppState for attachment to application
     */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    /**
     * Get the physics space
     */
    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    /**
     * Create a sphere physics body
     */
    public PhysicsRigidBody createSphere(String id, float radius, float mass, Vector3f position) {
        CollisionShape sphereShape = new SphereCollisionShape(radius);
        PhysicsRigidBody sphereBody = new PhysicsRigidBody(sphereShape, mass);
        sphereBody.setPhysicsLocation(position);

        physicsSpace.add(sphereBody);
        physicsObjects.put(id, sphereBody);

        return sphereBody;
    }

    /**
     * Create a box physics body
     */
    public PhysicsRigidBody createBox(String id, Vector3f halfExtents, float mass, Vector3f position) {
        CollisionShape boxShape = new BoxCollisionShape(halfExtents);
        PhysicsRigidBody boxBody = new PhysicsRigidBody(boxShape, mass);
        boxBody.setPhysicsLocation(position);

        physicsSpace.add(boxBody);
        physicsObjects.put(id, boxBody);

        return boxBody;
    }

    /**
     * Create a capsule physics body
     */
    public PhysicsRigidBody createCapsule(String id, float radius, float height, float mass, Vector3f position) {
        CollisionShape capsuleShape = new CapsuleCollisionShape(radius, height);
        PhysicsRigidBody capsuleBody = new PhysicsRigidBody(capsuleShape, mass);
        capsuleBody.setPhysicsLocation(position);

        physicsSpace.add(capsuleBody);
        physicsObjects.put(id, capsuleBody);

        return capsuleBody;
    }

    /**
     * Create a cylinder physics body
     */
    public PhysicsRigidBody createCylinder(String id, Vector3f halfExtents, float mass, Vector3f position) {
        CollisionShape cylinderShape = new CylinderCollisionShape(halfExtents);
        PhysicsRigidBody cylinderBody = new PhysicsRigidBody(cylinderShape, mass);
        cylinderBody.setPhysicsLocation(position);

        physicsSpace.add(cylinderBody);
        physicsObjects.put(id, cylinderBody);

        return cylinderBody;
    }

    /**
     * Create a cone physics body
     */
    public PhysicsRigidBody createCone(String id, float radius, float height, float mass, Vector3f position) {
        CollisionShape coneShape = new ConeCollisionShape(radius, height);
        PhysicsRigidBody coneBody = new PhysicsRigidBody(coneShape, mass);
        coneBody.setPhysicsLocation(position);

        physicsSpace.add(coneBody);
        physicsObjects.put(id, coneBody);

        return coneBody;
    }

    /**
     * Create a plane physics body (infinite ground)
     */
    public PhysicsRigidBody createPlane(String id, Vector3f normal, float constant) {
        Plane plane = new Plane(normal, constant);
        CollisionShape planeShape = new PlaneCollisionShape(plane);
        PhysicsRigidBody planeBody = new PhysicsRigidBody(planeShape, 0); // Static body

        physicsSpace.add(planeBody);
        physicsObjects.put(id, planeBody);

        return planeBody;
    }

    /**
     * Create a mesh-based physics body from a spatial
     */
    public PhysicsRigidBody createMeshBody(String id, Spatial spatial, float mass, Vector3f position) {
        CollisionShape meshShape = CollisionShapeFactory.createMeshShape(spatial);
        PhysicsRigidBody meshBody = new PhysicsRigidBody(meshShape, mass);
        meshBody.setPhysicsLocation(position);

        physicsSpace.add(meshBody);
        physicsObjects.put(id, meshBody);

        return meshBody;
    }

    /**
     * Create a compound shape (multiple shapes combined)
     */
    public PhysicsRigidBody createCompoundBody(String id, float mass, Vector3f position) {
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        PhysicsRigidBody compoundBody = new PhysicsRigidBody(compoundShape, mass);
        compoundBody.setPhysicsLocation(position);

        physicsSpace.add(compoundBody);
        physicsObjects.put(id, compoundBody);

        return compoundBody;
    }

    /**
     * Add a child shape to a compound body
     */
    public void addChildShape(String compoundId, CollisionShape childShape, Vector3f offset) {
        PhysicsRigidBody compoundBody = physicsObjects.get(compoundId);
        if (compoundBody != null && compoundBody.getCollisionShape() instanceof CompoundCollisionShape) {
            CompoundCollisionShape compound = (CompoundCollisionShape) compoundBody.getCollisionShape();
            compound.addChildShape(childShape, offset);
        }
    }

    /**
     * Apply force to a physics body
     */
    public void applyForce(String id, Vector3f force) {
        PhysicsRigidBody body = physicsObjects.get(id);
        if (body != null) {
            body.applyCentralForce(force);
        }
    }

    /**
     * Apply impulse to a physics body
     */
    public void applyImpulse(String id, Vector3f impulse) {
        PhysicsRigidBody body = physicsObjects.get(id);
        if (body != null) {
            body.applyImpulse(impulse, Vector3f.ZERO);
        }
    }

    /**
     * Apply torque to a physics body
     */
    public void applyTorque(String id, Vector3f torque) {
        PhysicsRigidBody body = physicsObjects.get(id);
        if (body != null) {
            body.applyTorque(torque);
        }
    }

    /**
     * Set linear velocity of a physics body
     */
    public void setLinearVelocity(String id, Vector3f velocity) {
        PhysicsRigidBody body = physicsObjects.get(id);
        if (body != null) {
            body.setLinearVelocity(velocity);
        }
    }

    /**
     * Set angular velocity of a physics body
     */
    public void setAngularVelocity(String id, Vector3f velocity) {
        PhysicsRigidBody body = physicsObjects.get(id);
        if (body != null) {
            body.setAngularVelocity(velocity);
        }
    }

    /**
     * Get physics body by ID
     */
    public PhysicsRigidBody getPhysicsBody(String id) {
        return physicsObjects.get(id);
    }

    /**
     * Remove physics body
     */
    public void removePhysicsBody(String id) {
        PhysicsRigidBody body = physicsObjects.get(id);
        if (body != null) {
            physicsSpace.remove(body);
            physicsObjects.remove(id);
        }
    }

    /**
     * Set gravity for the physics world
     */
    public void setGravity(Vector3f gravity) {
        physicsSpace.setGravity(gravity);
    }

    /**
     * Update physics simulation
     */
    public void update(float tpf) {
        // Physics is automatically updated by BulletAppState
        // This method can be used for custom physics logic
    }

    /**
     * Clean up physics resources
     */
    public void cleanup() {
        physicsObjects.clear();
        if (bulletAppState != null) {
            bulletAppState.cleanup();
        }
    }
}
