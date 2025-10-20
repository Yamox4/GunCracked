package com.example.modules;

import java.util.ArrayList;
import java.util.List;

import com.example.modules.entities.Entity;
import com.example.modules.physics.Collider;
import com.example.modules.physics.CollisionInfo;
import com.example.modules.physics.RigidBody;
import com.example.modules.physics.SphereCollider;
import com.jme3.math.Vector3f;

/**
 * Physics Module - handles collision detection and physics simulation
 */
public class PhysicsModule {
    private WorldModule worldModule;
    private List<CollisionPair> collisionPairs = new ArrayList<>();
    private Vector3f gravity = new Vector3f(0, -9.81f, 0);
    private boolean initialized = false;
    
    public PhysicsModule(WorldModule worldModule) {
        this.worldModule = worldModule;
    }
    
    /**
     * Initialize the physics module
     */
    public void initialize() {
        if (initialized) return;
        
        collisionPairs.clear();
        initialized = true;
        LoggingModule.info("Physics module initialized");
    }
    
    /**
     * Update physics simulation - call once per frame
     */
    public void update(float deltaTime) {
        if (!initialized) return;
        
        // Clear previous frame collisions
        collisionPairs.clear();
        
        // Get all colliders
        List<Entity> collidableEntities = worldModule.getEntitiesWithComponent(Collider.class);
        
        // Broad phase collision detection
        List<ColliderPair> potentialCollisions = broadPhaseDetection(collidableEntities);
        
        // Narrow phase collision detection
        narrowPhaseDetection(potentialCollisions);
        
        // Handle collision responses
        handleCollisionResponses();
        
        // Update rigid bodies (done after collision handling)
        updateRigidBodies(deltaTime);
    }
    
    /**
     * Broad phase collision detection using AABB
     */
    private List<ColliderPair> broadPhaseDetection(List<Entity> entities) {
        List<ColliderPair> pairs = new ArrayList<>();
        
        for (int i = 0; i < entities.size(); i++) {
            Entity entityA = entities.get(i);
            Collider colliderA = entityA.getComponent(Collider.class);
            if (colliderA == null || !colliderA.isEnabled()) continue;
            
            for (int j = i + 1; j < entities.size(); j++) {
                Entity entityB = entities.get(j);
                Collider colliderB = entityB.getComponent(Collider.class);
                if (colliderB == null || !colliderB.isEnabled()) continue;
                
                // Check if collision layers can interact
                if (!canCollide(colliderA, colliderB)) continue;
                
                // AABB test
                if (colliderA.getWorldBounds().intersects(colliderB.getWorldBounds())) {
                    pairs.add(new ColliderPair(colliderA, colliderB));
                }
            }
        }
        
        return pairs;
    }
    
    /**
     * Narrow phase collision detection
     */
    private void narrowPhaseDetection(List<ColliderPair> potentialCollisions) {
        for (ColliderPair pair : potentialCollisions) {
            CollisionInfo collision = pair.colliderA.getCollisionInfo(pair.colliderB);
            
            if (collision.hasCollision) {
                collisionPairs.add(new CollisionPair(collision, 
                    pair.colliderA.getEntity().getComponent(RigidBody.class),
                    pair.colliderB.getEntity().getComponent(RigidBody.class)));
            }
        }
    }
    
    /**
     * Handle collision responses
     */
    private void handleCollisionResponses() {
        for (CollisionPair pair : collisionPairs) {
            CollisionInfo collision = pair.collision;
            
            // Handle trigger collisions (no physics response)
            if (collision.colliderA.isTrigger() || collision.colliderB.isTrigger()) {
                handleTriggerCollision(collision);
                continue;
            }
            
            // Handle physics collision response
            if (pair.rigidBodyA != null) {
                pair.rigidBodyA.handleCollision(collision, pair.rigidBodyB);
            }
            if (pair.rigidBodyB != null) {
                // Create flipped collision info for the other object
                CollisionInfo flippedCollision = new CollisionInfo(
                    collision.colliderB, collision.colliderA,
                    collision.contactPoint, collision.normal.negate(),
                    collision.penetrationDepth
                );
                pair.rigidBodyB.handleCollision(flippedCollision, pair.rigidBodyA);
            }
        }
    }
    
    /**
     * Handle trigger collision events
     */
    private void handleTriggerCollision(CollisionInfo collision) {
        // In a full implementation, this would fire trigger events
        LoggingModule.debug("Trigger collision: " + 
            collision.colliderA.getEntity().getName() + " <-> " + 
            collision.colliderB.getEntity().getName());
    }
    
    /**
     * Update all rigid bodies
     */
    private void updateRigidBodies(float deltaTime) {
        List<RigidBody> rigidBodies = worldModule.getAllComponents(RigidBody.class);
        for (RigidBody rigidBody : rigidBodies) {
            if (rigidBody.isEnabled()) {
                rigidBody.update(deltaTime);
            }
        }
    }
    
    /**
     * Check if two colliders can collide based on their layers
     */
    private boolean canCollide(Collider a, Collider b) {
        // Simple layer mask check (can be expanded)
        return true; // For now, all layers can collide
    }
    
    /**
     * Raycast from origin in direction
     */
    public RaycastHit raycast(Vector3f origin, Vector3f direction, float maxDistance) {
        List<Entity> collidableEntities = worldModule.getEntitiesWithComponent(Collider.class);
        
        RaycastHit closestHit = null;
        float closestDistance = maxDistance;
        
        for (Entity entity : collidableEntities) {
            Collider collider = entity.getComponent(Collider.class);
            if (collider == null || !collider.isEnabled()) continue;
            
            RaycastHit hit = raycastCollider(origin, direction, collider, maxDistance);
            if (hit != null && hit.distance < closestDistance) {
                closestHit = hit;
                closestDistance = hit.distance;
            }
        }
        
        return closestHit;
    }
    
    /**
     * Raycast against a specific collider
     */
    private RaycastHit raycastCollider(Vector3f origin, Vector3f direction, 
                                     Collider collider, float maxDistance) {
        // Simplified raycast implementation for spheres
        if (collider instanceof SphereCollider) {
            SphereCollider sphere = (SphereCollider) collider;
            Vector3f center = sphere.getWorldCenter();
            Vector3f oc = origin.subtract(center);
            
            float a = direction.dot(direction);
            float b = 2.0f * oc.dot(direction);
            float c = oc.dot(oc) - sphere.getRadius() * sphere.getRadius();
            
            float discriminant = b * b - 4 * a * c;
            if (discriminant < 0) return null;
            
            float t = (-b - (float)Math.sqrt(discriminant)) / (2 * a);
            if (t < 0 || t > maxDistance) return null;
            
            Vector3f hitPoint = origin.add(direction.mult(t));
            Vector3f normal = hitPoint.subtract(center).normalizeLocal();
            
            return new RaycastHit(collider, hitPoint, normal, t);
        }
        
        return null; // Other collider types not implemented
    }
    
    // Getters/Setters
    public Vector3f getGravity() { return gravity.clone(); }
    public void setGravity(Vector3f gravity) { this.gravity.set(gravity); }
    public void setGravity(float x, float y, float z) { this.gravity.set(x, y, z); }
    
    public List<CollisionPair> getCollisionPairs() { return new ArrayList<>(collisionPairs); }
    
    /**
     * Get physics statistics
     */
    public PhysicsStats getStats() {
        int colliderCount = worldModule.getAllComponents(Collider.class).size();
        int rigidBodyCount = worldModule.getAllComponents(RigidBody.class).size();
        return new PhysicsStats(colliderCount, rigidBodyCount, collisionPairs.size());
    }
    
    /**
     * Cleanup the physics module
     */
    public void cleanup() {
        collisionPairs.clear();
        initialized = false;
        LoggingModule.info("Physics module cleaned up");
    }
    
    // Helper classes
    private static class ColliderPair {
        final Collider colliderA;
        final Collider colliderB;
        
        ColliderPair(Collider a, Collider b) {
            this.colliderA = a;
            this.colliderB = b;
        }
    }
    
    private static class CollisionPair {
        final CollisionInfo collision;
        final RigidBody rigidBodyA;
        final RigidBody rigidBodyB;
        
        CollisionPair(CollisionInfo collision, RigidBody a, RigidBody b) {
            this.collision = collision;
            this.rigidBodyA = a;
            this.rigidBodyB = b;
        }
    }
    
    /**
     * Raycast hit information
     */
    public static class RaycastHit {
        public final Collider collider;
        public final Vector3f point;
        public final Vector3f normal;
        public final float distance;
        
        public RaycastHit(Collider collider, Vector3f point, Vector3f normal, float distance) {
            this.collider = collider;
            this.point = point.clone();
            this.normal = normal.clone();
            this.distance = distance;
        }
        
        public Entity getEntity() {
            return collider.getEntity();
        }
    }
    
    /**
     * Physics statistics
     */
    public static class PhysicsStats {
        public final int colliderCount;
        public final int rigidBodyCount;
        public final int activeCollisions;
        
        public PhysicsStats(int colliderCount, int rigidBodyCount, int activeCollisions) {
            this.colliderCount = colliderCount;
            this.rigidBodyCount = rigidBodyCount;
            this.activeCollisions = activeCollisions;
        }
        
        @Override
        public String toString() {
            return String.format("PhysicsStats[colliders=%d, rigidbodies=%d, collisions=%d]", 
                               colliderCount, rigidBodyCount, activeCollisions);
        }
    }
}