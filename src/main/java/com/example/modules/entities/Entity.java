package com.example.modules.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity class - container for components
 */
public class Entity {
    private static int nextId = 1;
    
    private final int id;
    private String name;
    private boolean active = true;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    
    public Entity() {
        this.id = nextId++;
        this.name = "Entity_" + id;
    }
    
    public Entity(String name) {
        this.id = nextId++;
        this.name = name;
    }
    
    /**
     * Add a component to this entity
     */
    public <T extends Component> T addComponent(T component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        
        Class<? extends Component> componentType = component.getClass();
        
        // Remove existing component of same type
        if (components.containsKey(componentType)) {
            removeComponent(componentType);
        }
        
        components.put(componentType, component);
        component.onAttach(this);
        
        return component;
    }
    
    /**
     * Remove a component from this entity
     */
    public <T extends Component> boolean removeComponent(Class<T> componentType) {
        Component component = components.remove(componentType);
        if (component != null) {
            component.onDetach();
            return true;
        }
        return false;
    }
    
    /**
     * Get a component of specified type
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentType) {
        return (T) components.get(componentType);
    }
    
    /**
     * Check if entity has a component of specified type
     */
    public <T extends Component> boolean hasComponent(Class<T> componentType) {
        return components.containsKey(componentType);
    }
    
    /**
     * Get all components
     */
    public Collection<Component> getComponents() {
        return components.values();
    }
    
    /**
     * Update all components
     */
    public void update(float deltaTime) {
        if (!active) return;
        
        for (Component component : components.values()) {
            if (component.isEnabled()) {
                component.update(deltaTime);
            }
        }
    }
    
    /**
     * Get entity ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Get entity name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set entity name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Check if entity is active
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Set entity active state
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Destroy entity - removes all components
     */
    public void destroy() {
        for (Component component : components.values()) {
            component.onDetach();
        }
        components.clear();
        active = false;
    }
    
    @Override
    public String toString() {
        return String.format("Entity[id=%d, name='%s', components=%d]", 
                           id, name, components.size());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Entity)) return false;
        Entity other = (Entity) obj;
        return id == other.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}