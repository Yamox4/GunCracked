package com.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f target;
    private Vector3f up;
    private Matrix4f viewMatrix;
    
    public Camera() {
        position = new Vector3f(0, 0, 0);
        target = new Vector3f(0, 0, -1);
        up = new Vector3f(0, 1, 0);
        viewMatrix = new Matrix4f();
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
        updateViewMatrix();
    }
    
    public void lookAt(Vector3f target) {
        this.target.set(target);
        updateViewMatrix();
    }
    
    private void updateViewMatrix() {
        viewMatrix.identity().lookAt(position, target, up);
    }
    
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
    
    public Vector3f getPosition() {
        return position;
    }
}