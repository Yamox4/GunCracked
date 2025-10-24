package com.example.entity;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Base Entity class for all game objects
 */
public abstract class Entity {

    protected String id;
    protected Vector3f position;
    protected Vector3f rotation;
    protected Spatial spatial;
    protected boolean active = true;

    public Entity(String id) {
        this.id = id;
        this.position = new Vector3f();
        this.rotation = new Vector3f();
    }

    public Entity(String id, Vector3f position) {
        this.id = id;
        this.position = position.clone();
        this.rotation = new Vector3f();
    }

    /**
     * Initialize the entity
     */
    public abstract void initialize();

    /**
     * Update the entity
     */
    public abstract void update(float tpf);

    /**
     * Cleanup the entity
     */
    public abstract void cleanup();

    // Getters and setters
    public String getId() {
        return id;
    }

    public Vector3f getPosition() {
        return position.clone();
    }

    public void setPosition(Vector3f position) {
        this.position = position.clone();
        if (spatial != null) {
            spatial.setLocalTranslation(position);
        }
    }

    public Vector3f getRotation() {
        return rotation.clone();
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation.clone();
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
        if (spatial != null) {
            spatial.setLocalTranslation(position);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
