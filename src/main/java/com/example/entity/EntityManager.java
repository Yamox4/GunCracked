package com.example.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;

/**
 * Entity Manager - manages all game entities
 */
public class EntityManager extends BaseAppState {
    
    private Map<String, Entity> entities;
    private boolean initialized = false;
    
    public EntityManager() {
        this.entities = new ConcurrentHashMap<>();
    }
    
    @Override
    protected void initialize(Application app) {
        this.initialized = true;
        System.out.println("EntityManager initialized");
    }
    
    @Override
    protected void cleanup(Application app) {
        // Cleanup all entities
        for (Entity entity : entities.values()) {
            entity.cleanup();
        }
        entities.clear();
        this.initialized = false;
        System.out.println("EntityManager cleaned up");
    }
    
    @Override
    protected void onEnable() {
        // EntityManager enabled
    }
    
    @Override
    protected void onDisable() {
        // EntityManager disabled
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        // Update all active entities
        for (Entity entity : entities.values()) {
            if (entity.isActive()) {
                entity.update(tpf);
            }
        }
    }
    
    /**
     * Add an entity to the manager
     */
    public void addEntity(Entity entity) {
        if (entity == null) {
            System.err.println("Cannot add null entity");
            return;
        }
        
        entities.put(entity.getId(), entity);
        
        // Always initialize entity immediately
        entity.initialize();
        
        System.out.println("Entity added: " + entity.getId());
    }
    
    /**
     * Remove an entity from the manager
     */
    public void removeEntity(String entityId) {
        Entity entity = entities.remove(entityId);
        if (entity != null) {
            entity.cleanup();
            System.out.println("Entity removed: " + entityId);
        }
    }
    
    /**
     * Get an entity by ID
     */
    public Entity getEntity(String entityId) {
        return entities.get(entityId);
    }
    
    /**
     * Get an entity by ID with type casting
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> T getEntity(String entityId, Class<T> type) {
        Entity entity = entities.get(entityId);
        if (entity != null && type.isInstance(entity)) {
            return (T) entity;
        }
        return null;
    }
    
    /**
     * Check if an entity exists
     */
    public boolean hasEntity(String entityId) {
        return entities.containsKey(entityId);
    }
    
    /**
     * Get all entities
     */
    public Map<String, Entity> getAllEntities() {
        return new HashMap<>(entities);
    }
    
    /**
     * Get entity count
     */
    public int getEntityCount() {
        return entities.size();
    }
    
    /**
     * Set entity active/inactive
     */
    public void setEntityActive(String entityId, boolean active) {
        Entity entity = entities.get(entityId);
        if (entity != null) {
            entity.setActive(active);
        }
    }
    
    /**
     * Initialize all entities (called when manager is initialized)
     */
    private void initializeAllEntities() {
        for (Entity entity : entities.values()) {
            entity.initialize();
        }
    }
}