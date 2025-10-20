package com.example.modules.entities;

/**
 * Base class for all entity components
 */
public abstract class Component {
    protected Entity entity;
    protected boolean enabled = true;
    
    /**
     * Called when component is added to an entity
     */
    public void onAttach(Entity entity) {
        this.entity = entity;
    }
    
    /**
     * Called when component is removed from an entity
     */
    public void onDetach() {
        this.entity = null;
    }
    
    /**
     * Update component - called every frame
     */
    public void update(float deltaTime) {
        // Override in subclasses
    }
    
    /**
     * Get the entity this component belongs to
     */
    public Entity getEntity() {
        return entity;
    }
    
    /**
     * Check if component is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Set component enabled state
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}