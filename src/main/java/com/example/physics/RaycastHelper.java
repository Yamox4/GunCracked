package com.example.physics;

import java.util.List;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

/**
 * Helper class for raycasting operations in the physics world
 */
public class RaycastHelper {
    
    private final PhysicsSpace physicsSpace;
    
    public RaycastHelper(PhysicsSpace physicsSpace) {
        this.physicsSpace = physicsSpace;
    }
    
    /**
     * Perform a raycast and return the first hit
     */
    public RaycastResult raycast(Vector3f from, Vector3f to) {
        List<PhysicsRayTestResult> results = physicsSpace.rayTest(from, to);
        
        if (!results.isEmpty()) {
            PhysicsRayTestResult hit = results.get(0);
            return new RaycastResult(
                true,
                (PhysicsRigidBody) hit.getCollisionObject(),
                from.add(to.subtract(from).mult(hit.getHitFraction())),
                Vector3f.UNIT_Y, // Approximate normal
                hit.getHitFraction()
            );
        }
        
        return new RaycastResult(false, null, null, null, 0);
    }
    
    /**
     * Perform a raycast in a direction with specified distance
     */
    public RaycastResult raycast(Vector3f origin, Vector3f direction, float distance) {
        Vector3f to = origin.add(direction.normalize().mult(distance));
        return raycast(origin, to);
    }
    
    /**
     * Perform a raycast using a Ray object
     */
    public RaycastResult raycast(Ray ray, float distance) {
        Vector3f to = ray.origin.add(ray.direction.mult(distance));
        return raycast(ray.origin, to);
    }
    
    /**
     * Get all objects hit by a ray
     */
    public List<PhysicsRayTestResult> raycastAll(Vector3f from, Vector3f to) {
        return physicsSpace.rayTest(from, to);
    }
    
    /**
     * Check if there's a clear line of sight between two points
     */
    public boolean hasLineOfSight(Vector3f from, Vector3f to) {
        List<PhysicsRayTestResult> results = physicsSpace.rayTest(from, to);
        return results.isEmpty();
    }
    
    /**
     * Find the ground below a point
     */
    public RaycastResult findGround(Vector3f position, float maxDistance) {
        Vector3f from = position;
        Vector3f to = position.add(0, -maxDistance, 0);
        return raycast(from, to);
    }
    
    /**
     * Check if a point is above ground
     */
    public boolean isAboveGround(Vector3f position, float maxDistance) {
        RaycastResult result = findGround(position, maxDistance);
        return result.isHit();
    }
    
    /**
     * Get the distance to ground
     */
    public float getDistanceToGround(Vector3f position, float maxDistance) {
        RaycastResult result = findGround(position, maxDistance);
        if (result.isHit()) {
            return position.distance(result.getHitPoint());
        }
        return maxDistance;
    }
    
    /**
     * Perform a sphere cast (approximate)
     */
    public RaycastResult sphereCast(Vector3f from, Vector3f to, float radius) {
        Vector3f direction = to.subtract(from).normalize();
        Vector3f perpendicular = direction.cross(Vector3f.UNIT_Y).normalize();
        
        // Cast multiple rays in a circle pattern
        int rayCount = 8;
        float closestDistance = Float.MAX_VALUE;
        RaycastResult closestHit = null;
        
        for (int i = 0; i < rayCount; i++) {
            float angle = (float) (2 * Math.PI * i / rayCount);
            Vector3f offset = perpendicular.mult((float) Math.cos(angle) * radius);
            
            Vector3f rayStart = from.add(offset);
            Vector3f rayEnd = to.add(offset);
            
            RaycastResult result = raycast(rayStart, rayEnd);
            if (result.isHit()) {
                float distance = result.getDistance(from);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestHit = result;
                }
            }
        }
        
        return closestHit != null ? closestHit : new RaycastResult(false, null, null, null, 0);
    }
    
    /**
     * Find a safe landing spot near a position
     */
    public Vector3f findSafeLandingSpot(Vector3f position, float searchRadius, float maxDropDistance) {
        int attempts = 20;
        
        for (int i = 0; i < attempts; i++) {
            float angle = (float) (Math.random() * 2 * Math.PI);
            float distance = (float) (Math.random() * searchRadius);
            
            Vector3f testPos = position.add(
                (float) Math.cos(angle) * distance,
                0,
                (float) Math.sin(angle) * distance
            );
            
            RaycastResult groundCheck = findGround(testPos, maxDropDistance);
            if (groundCheck.isHit()) {
                Vector3f normal = groundCheck.getHitNormal();
                if (normal != null && normal.dot(Vector3f.UNIT_Y) > 0.7f) {
                    return groundCheck.getHitPoint().add(0, 0.1f, 0);
                }
            }
        }
        
        return position;
    }
    
    /**
     * Raycast result container
     */
    public static class RaycastResult {
        private final boolean hit;
        private final PhysicsRigidBody hitObject;
        private final Vector3f hitPoint;
        private final Vector3f hitNormal;
        private final float hitFraction;
        
        public RaycastResult(boolean hit, PhysicsRigidBody hitObject, Vector3f hitPoint, 
                           Vector3f hitNormal, float hitFraction) {
            this.hit = hit;
            this.hitObject = hitObject;
            this.hitPoint = hitPoint != null ? hitPoint.clone() : null;
            this.hitNormal = hitNormal != null ? hitNormal.clone() : null;
            this.hitFraction = hitFraction;
        }
        
        public boolean isHit() {
            return hit;
        }
        
        public PhysicsRigidBody getHitObject() {
            return hitObject;
        }
        
        public Vector3f getHitPoint() {
            return hitPoint != null ? hitPoint.clone() : null;
        }
        
        public Vector3f getHitNormal() {
            return hitNormal != null ? hitNormal.clone() : null;
        }
        
        public float getHitFraction() {
            return hitFraction;
        }
        
        public float getDistance(Vector3f origin) {
            return hit && hitPoint != null ? origin.distance(hitPoint) : Float.MAX_VALUE;
        }
    }
}