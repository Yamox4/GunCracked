package com.example.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.modules.entities.Component;
import com.example.modules.entities.Entity;

/**
 * World Module - manages world state and entities
 */
public class WorldModule {
    private final Map<Integer, Entity> entities = new ConcurrentHashMap<>();
    private final List<Entity> entitiesToAdd = new ArrayList<>();
    private final List<Integer> entitiesToRemove = new ArrayList<>();
    private boolean initialized = false;
    
    /**
     * Initialize the world module
     */
    public void initialize() {
        if (initialized) return;
        
        entities.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
        
        initialized = true;
        LoggingModule.info("World module initialized");
    }
    
    /**
     * Update the world - call once per frame
     */
    public void update(float deltaTime) {
        if (!initialized) return;
        
        // Process pending additions
        processPendingAdditions();
        
        // Process pending removals
        processPendingRemovals();
        
        // Update all entities
        for (Entity entity : entities.values()) {
            if (entity.isActive()) {
                entity.update(deltaTime);
            }
        }
    }
    
    /**
     * Create a new entity
     */
    public Entity createEntity() {
        Entity entity = new Entity();
        entitiesToAdd.add(entity);
        return entity;
    }
    
    /**
     * Create a new entity with name
     */
    public Entity createEntity(String name) {
        Entity entity = new Entity(name);
        entitiesToAdd.add(entity);
        return entity;
    }
    
    /**
     * Add an existing entity to the world
     */
    public void addEntity(Entity entity) {
        if (entity != null) {
            entitiesToAdd.add(entity);
        }
    }
    
    /**
     * Remove an entity from the world
     */
    public void removeEntity(Entity entity) {
        if (entity != null) {
            removeEntity(entity.getId());
        }
    }
    
    /**
     * Remove an entity by ID
     */
    public void removeEntity(int entityId) {
        entitiesToRemove.add(entityId);
    }
    
    /**
     * Get an entity by ID
     */
    public Entity getEntity(int entityId) {
        return entities.get(entityId);
    }
    
    /**
     * Get an entity by name (returns first match)
     */
    public Entity getEntity(String name) {
        for (Entity entity : entities.values()) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }
    
    /**
     * Get all entities
     */
    public Collection<Entity> getAllEntities() {
        return new ArrayList<>(entities.values());
    }
    
    /**
     * Get entities with a specific component type
     */
    public <T extends Component> List<Entity> getEntitiesWithComponent(Class<T> componentType) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities.values()) {
            if (entity.hasComponent(componentType)) {
                result.add(entity);
            }
        }
        return result;
    }
    
    /**
     * Get all components of a specific type from all entities
     */
    public <T extends Component> List<T> getAllComponents(Class<T> componentType) {
        List<T> result = new ArrayList<>();
        for (Entity entity : entities.values()) {
            T component = entity.getComponent(componentType);
            if (component != null) {
                result.add(component);
            }
        }
        return result;
    }
    
    /**
     * Get entity count
     */
    public int getEntityCount() {
        return entities.size();
    }
    
    /**
     * Check if world contains entity
     */
    public boolean containsEntity(Entity entity) {
        return entity != null && entities.containsKey(entity.getId());
    }
    
    /**
     * Check if world contains entity by ID
     */
    public boolean containsEntity(int entityId) {
        return entities.containsKey(entityId);
    }
    
    /**
     * Clear all entities from the world
     */
    public void clear() {
        // Destroy all entities
        for (Entity entity : entities.values()) {
            entity.destroy();
        }
        
        entities.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
        
        LoggingModule.info("World cleared");
    }
    
    /**
     * Process pending entity additions
     */
    private void processPendingAdditions() {
        if (entitiesToAdd.isEmpty()) return;
        
        for (Entity entity : entitiesToAdd) {
            entities.put(entity.getId(), entity);
        }
        
        LoggingModule.debug("Added " + entitiesToAdd.size() + " entities to world");
        entitiesToAdd.clear();
    }
    
    /**
     * Process pending entity removals
     */
    private void processPendingRemovals() {
        if (entitiesToRemove.isEmpty()) return;
        
        for (Integer entityId : entitiesToRemove) {
            Entity entity = entities.remove(entityId);
            if (entity != null) {
                entity.destroy();
            }
        }
        
        LoggingModule.debug("Removed " + entitiesToRemove.size() + " entities from world");
        entitiesToRemove.clear();
    }
    
    /**
     * Get world statistics
     */
    public WorldStats getStats() {
        return new WorldStats(
            entities.size(),
            entitiesToAdd.size(),
            entitiesToRemove.size()
        );
    }
    
    /**
     * Cleanup the world module
     */
    public void cleanup() {
        clear();
        initialized = false;
        LoggingModule.info("World module cleaned up");
    }
    
    /**
     * World statistics data class
     */
    public static class WorldStats {
        public final int entityCount;
        public final int pendingAdditions;
        public final int pendingRemovals;
        
        public WorldStats(int entityCount, int pendingAdditions, int pendingRemovals) {
            this.entityCount = entityCount;
            this.pendingAdditions = pendingAdditions;
            this.pendingRemovals = pendingRemovals;
        }
        
        @Override
        public String toString() {
            return String.format("WorldStats[entities=%d, +%d, -%d]", 
                               entityCount, pendingAdditions, pendingRemovals);
        }
    }
}