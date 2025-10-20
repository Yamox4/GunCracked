package com.example.modules.entities;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Transform component - handles position, rotation, and scale
 */
public class Transform extends Component {
    private Vector3f position;
    private Quaternion rotation;
    private Vector3f scale;
    
    // Cached world transform
    private Vector3f worldPosition;
    private Quaternion worldRotation;
    private Vector3f worldScale;
    private boolean worldTransformDirty = true;
    
    // Parent-child hierarchy
    private Transform parent;
    
    public Transform() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Quaternion();
        this.scale = new Vector3f(1, 1, 1);
        
        this.worldPosition = new Vector3f();
        this.worldRotation = new Quaternion();
        this.worldScale = new Vector3f(1, 1, 1);
    }
    
    public Transform(Vector3f position) {
        this();
        this.position.set(position);
        markWorldTransformDirty();
    }
    
    public Transform(Vector3f position, Quaternion rotation) {
        this(position);
        this.rotation.set(rotation);
        markWorldTransformDirty();
    }
    
    public Transform(Vector3f position, Quaternion rotation, Vector3f scale) {
        this(position, rotation);
        this.scale.set(scale);
        markWorldTransformDirty();
    }
    
    // Local transform getters/setters
    public Vector3f getPosition() {
        return position.clone();
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
        markWorldTransformDirty();
    }
    
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        markWorldTransformDirty();
    }
    
    public Quaternion getRotation() {
        return rotation.clone();
    }
    
    public void setRotation(Quaternion rotation) {
        this.rotation.set(rotation);
        markWorldTransformDirty();
    }
    
    public void setRotation(float x, float y, float z, float w) {
        this.rotation.set(x, y, z, w);
        markWorldTransformDirty();
    }
    
    public Vector3f getScale() {
        return scale.clone();
    }
    
    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        markWorldTransformDirty();
    }
    
    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        markWorldTransformDirty();
    }
    
    public void setScale(float uniformScale) {
        this.scale.set(uniformScale, uniformScale, uniformScale);
        markWorldTransformDirty();
    }
    
    // World transform getters
    public Vector3f getWorldPosition() {
        updateWorldTransform();
        return worldPosition.clone();
    }
    
    public Quaternion getWorldRotation() {
        updateWorldTransform();
        return worldRotation.clone();
    }
    
    public Vector3f getWorldScale() {
        updateWorldTransform();
        return worldScale.clone();
    }
    
    // Transform operations
    public void translate(Vector3f translation) {
        position.addLocal(translation);
        markWorldTransformDirty();
    }
    
    public void translate(float x, float y, float z) {
        position.addLocal(x, y, z);
        markWorldTransformDirty();
    }
    
    public void rotate(Quaternion rotation) {
        this.rotation.multLocal(rotation);
        markWorldTransformDirty();
    }
    
    public void rotate(float x, float y, float z) {
        Quaternion deltaRotation = new Quaternion();
        deltaRotation.fromAngles(x, y, z);
        rotate(deltaRotation);
    }
    
    public void lookAt(Vector3f target, Vector3f up) {
        Vector3f direction = target.subtract(getWorldPosition()).normalizeLocal();
        rotation.lookAt(direction, up);
        markWorldTransformDirty();
    }
    
    // Direction vectors
    public Vector3f getForward() {
        return getWorldRotation().mult(Vector3f.UNIT_Z.negate());
    }
    
    public Vector3f getRight() {
        return getWorldRotation().mult(Vector3f.UNIT_X);
    }
    
    public Vector3f getUp() {
        return getWorldRotation().mult(Vector3f.UNIT_Y);
    }
    
    // Parent-child hierarchy
    public Transform getParent() {
        return parent;
    }
    
    public void setParent(Transform parent) {
        this.parent = parent;
        markWorldTransformDirty();
    }
    
    // World transform calculation
    private void updateWorldTransform() {
        if (!worldTransformDirty) return;
        
        if (parent != null) {
            parent.updateWorldTransform();
            
            // Combine with parent transform
            worldRotation.set(parent.worldRotation).multLocal(rotation);
            worldScale.set(parent.worldScale).multLocal(scale);
            worldPosition.set(position).multLocal(worldScale);
            worldPosition = parent.worldRotation.mult(worldPosition);
            worldPosition.addLocal(parent.worldPosition);
        } else {
            // No parent, world transform equals local transform
            worldPosition.set(position);
            worldRotation.set(rotation);
            worldScale.set(scale);
        }
        
        worldTransformDirty = false;
    }
    
    private void markWorldTransformDirty() {
        worldTransformDirty = true;
    }
    
    @Override
    public String toString() {
        return String.format("Transform[pos=%s, rot=%s, scale=%s]", 
                           position, rotation, scale);
    }
}