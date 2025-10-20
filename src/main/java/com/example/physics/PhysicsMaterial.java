package com.example.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;

/**
 * Physics material properties for different object types
 * Defines friction, restitution, and other physical properties
 */
public class PhysicsMaterial {
    
    private float friction;
    private float restitution;
    private float rollingFriction;
    private float spinningFriction;
    private float contactDamping;
    private float contactStiffness;
    
    // Predefined materials
    public static final PhysicsMaterial RUBBER = new PhysicsMaterial(0.8f, 0.9f, 0.1f, 0.1f);
    public static final PhysicsMaterial METAL = new PhysicsMaterial(0.3f, 0.2f, 0.05f, 0.05f);
    public static final PhysicsMaterial WOOD = new PhysicsMaterial(0.6f, 0.4f, 0.2f, 0.1f);
    public static final PhysicsMaterial ICE = new PhysicsMaterial(0.1f, 0.1f, 0.01f, 0.01f);
    public static final PhysicsMaterial CONCRETE = new PhysicsMaterial(0.7f, 0.3f, 0.3f, 0.2f);
    public static final PhysicsMaterial GLASS = new PhysicsMaterial(0.4f, 0.1f, 0.1f, 0.05f);
    public static final PhysicsMaterial PLASTIC = new PhysicsMaterial(0.5f, 0.6f, 0.15f, 0.1f);
    
    // Additional predefined materials
    public static final PhysicsMaterial SAND = new PhysicsMaterial(0.9f, 0.1f, 0.8f, 0.6f);
    public static final PhysicsMaterial MUD = new PhysicsMaterial(0.95f, 0.05f, 0.9f, 0.8f);
    public static final PhysicsMaterial SNOW = new PhysicsMaterial(0.2f, 0.2f, 0.3f, 0.2f);
    public static final PhysicsMaterial FABRIC = new PhysicsMaterial(0.8f, 0.3f, 0.4f, 0.3f);
    public static final PhysicsMaterial LEATHER = new PhysicsMaterial(0.7f, 0.4f, 0.3f, 0.2f);
    public static final PhysicsMaterial CERAMIC = new PhysicsMaterial(0.6f, 0.1f, 0.2f, 0.1f);
    public static final PhysicsMaterial FOAM = new PhysicsMaterial(0.9f, 0.8f, 0.5f, 0.4f);
    public static final PhysicsMaterial STEEL = new PhysicsMaterial(0.2f, 0.1f, 0.02f, 0.02f);
    public static final PhysicsMaterial ALUMINUM = new PhysicsMaterial(0.25f, 0.15f, 0.03f, 0.03f);
    public static final PhysicsMaterial COPPER = new PhysicsMaterial(0.35f, 0.2f, 0.04f, 0.04f);
    
    public PhysicsMaterial(float friction, float restitution, float rollingFriction, float spinningFriction) {
        this.friction = friction;
        this.restitution = restitution;
        this.rollingFriction = rollingFriction;
        this.spinningFriction = spinningFriction;
        this.contactDamping = 0.1f;
        this.contactStiffness = 1e30f;
    }
    
    public PhysicsMaterial(float friction, float restitution) {
        this(friction, restitution, 0.1f, 0.1f);
    }
    
    /**
     * Apply this material to a physics body
     */
    public void applyTo(PhysicsRigidBody body) {
        body.setFriction(friction);
        body.setRestitution(restitution);
        body.setLinearDamping(contactDamping);
        body.setAngularDamping(contactDamping);
    }
    
    /**
     * Create a bouncy material
     */
    public static PhysicsMaterial createBouncy(float bounciness) {
        return new PhysicsMaterial(0.5f, bounciness, 0.1f, 0.1f);
    }
    
    /**
     * Create a slippery material
     */
    public static PhysicsMaterial createSlippery(float slipperiness) {
        float friction = 1.0f - slipperiness;
        return new PhysicsMaterial(friction, 0.1f, friction * 0.1f, friction * 0.1f);
    }
    
    /**
     * Combine two materials
     */
    public static PhysicsMaterial combine(PhysicsMaterial mat1, PhysicsMaterial mat2) {
        float avgFriction = (mat1.friction + mat2.friction) * 0.5f;
        float avgRestitution = (mat1.restitution + mat2.restitution) * 0.5f;
        float avgRollingFriction = (mat1.rollingFriction + mat2.rollingFriction) * 0.5f;
        float avgSpinningFriction = (mat1.spinningFriction + mat2.spinningFriction) * 0.5f;
        
        return new PhysicsMaterial(avgFriction, avgRestitution, avgRollingFriction, avgSpinningFriction);
    }
    
    /**
     * Get material density for mass calculations
     */
    public float getDensity() {
        if (this == STEEL) return 7850f;
        if (this == ALUMINUM) return 2700f;
        if (this == COPPER) return 8960f;
        if (this == CONCRETE) return 2400f;
        if (this == WOOD) return 600f;
        if (this == PLASTIC) return 950f;
        if (this == RUBBER) return 920f;
        if (this == GLASS) return 2500f;
        if (this == ICE) return 917f;
        if (this == FOAM) return 100f;
        
        return 1000f; // Default density
    }
    
    // Getters and setters
    public float getFriction() { return friction; }
    public void setFriction(float friction) { this.friction = friction; }
    
    public float getRestitution() { return restitution; }
    public void setRestitution(float restitution) { this.restitution = restitution; }
    
    public float getRollingFriction() { return rollingFriction; }
    public void setRollingFriction(float rollingFriction) { this.rollingFriction = rollingFriction; }
    
    public float getSpinningFriction() { return spinningFriction; }
    public void setSpinningFriction(float spinningFriction) { this.spinningFriction = spinningFriction; }
    
    public float getContactDamping() { return contactDamping; }
    public void setContactDamping(float contactDamping) { this.contactDamping = contactDamping; }
    
    public float getContactStiffness() { return contactStiffness; }
    public void setContactStiffness(float contactStiffness) { this.contactStiffness = contactStiffness; }
}