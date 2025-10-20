package com.example.modules.physics;

import com.example.modules.entities.Component;
import com.example.modules.entities.Transform;
import com.jme3.math.Vector3f;

/**
 * Base class for all colliders
 */
public abstract class Collider extends Component {
    protected boolean isTrigger = false;
    protected boolean isStatic = false;
    protected CollisionLayer layer = CollisionLayer.DEFAULT;
    
    /**
     * Check if this collider intersects with another
     */
    public abstract boolean intersects(Collider other);
    
    /**
     * Get collision info with another collider
     */
    public abstract CollisionInfo getCollisionInfo(Collider other);
    
    /**
     * Get the world center of this collider
     */
    public Vector3f getWorldCenter() {
        Transform transform = entity.getComponent(Transform.class);
        return transform != null ? transform.getWorldPosition() : new Vector3f();
    }
    
    /**
     * Get the world bounds of this collider
     */
    public abstract Bounds getWorldBounds();
    
    // Getters/Setters
    public boolean isTrigger() { return isTrigger; }
    public void setTrigger(boolean trigger) { this.isTrigger = trigger; }
    
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    
    public CollisionLayer getLayer() { return layer; }
    public void setLayer(CollisionLayer layer) { this.layer = layer; }
    
    /**
     * Collision layers for filtering
     */
    public enum CollisionLayer {
        DEFAULT(1),
        PLAYER(2),
        ENEMY(4),
        PROJECTILE(8),
        ENVIRONMENT(16),
        TRIGGER(32);
        
        public final int mask;
        
        CollisionLayer(int mask) {
            this.mask = mask;
        }
    }
    
    /**
     * Bounds class for collision detection optimization
     */
    public static class Bounds {
        public final Vector3f min;
        public final Vector3f max;
        
        public Bounds(Vector3f min, Vector3f max) {
            this.min = min.clone();
            this.max = max.clone();
        }
        
        public boolean intersects(Bounds other) {
            return min.x <= other.max.x && max.x >= other.min.x &&
                   min.y <= other.max.y && max.y >= other.min.y &&
                   min.z <= other.max.z && max.z >= other.min.z;
        }
        
        public Vector3f getCenter() {
            return min.add(max).multLocal(0.5f);
        }
        
        public Vector3f getSize() {
            return max.subtract(min);
        }
    }
}