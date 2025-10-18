package com.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;
    private Matrix4f viewMatrix;
    
    private float yaw = -90.0f;   // Yaw is initialized to -90.0 degrees since a yaw of 0.0 results in a direction vector pointing to the right
    private float pitch = 0.0f;
    private float movementSpeed = 5.0f;
    private float mouseSensitivity = 0.1f;
    
    public Camera() {
        position = new Vector3f(0, 5, 10);
        worldUp = new Vector3f(0, 1, 0);
        front = new Vector3f(0, 0, -1);
        viewMatrix = new Matrix4f();
        updateCameraVectors();
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
        updateViewMatrix();
    }
    
    public void lookAt(Vector3f target) {
        Vector3f direction = new Vector3f(target).sub(position).normalize();
        front.set(direction);
        right = new Vector3f(front).cross(worldUp).normalize();
        up = new Vector3f(right).cross(front).normalize();
        updateViewMatrix();
    }
    
    public void processMouseMovement(float xOffset, float yOffset) {
        xOffset *= mouseSensitivity;
        yOffset *= mouseSensitivity;
        
        yaw += xOffset;
        pitch += yOffset;
        
        // Constrain pitch to prevent screen flipping
        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;
        
        updateCameraVectors();
    }
    
    public void processKeyboard(CameraMovement direction, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        
        switch (direction) {
            case FORWARD:
                position.add(new Vector3f(front).mul(velocity));
                break;
            case BACKWARD:
                position.sub(new Vector3f(front).mul(velocity));
                break;
            case LEFT:
                position.sub(new Vector3f(right).mul(velocity));
                break;
            case RIGHT:
                position.add(new Vector3f(right).mul(velocity));
                break;
            case UP:
                position.add(new Vector3f(worldUp).mul(velocity));
                break;
            case DOWN:
                position.sub(new Vector3f(worldUp).mul(velocity));
                break;
        }
        updateViewMatrix();
    }
    
    private void updateCameraVectors() {
        // Calculate the new front vector
        Vector3f newFront = new Vector3f();
        newFront.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        newFront.y = (float) Math.sin(Math.toRadians(pitch));
        newFront.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front = newFront.normalize();
        
        // Re-calculate the right and up vector
        right = new Vector3f(front).cross(worldUp).normalize();
        up = new Vector3f(right).cross(front).normalize();
        
        updateViewMatrix();
    }
    
    private void updateViewMatrix() {
        Vector3f target = new Vector3f(position).add(front);
        viewMatrix.identity().lookAt(position, target, up);
    }
    
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public Vector3f getFront() {
        return front;
    }
    
    public float getYaw() {
        return yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }
    
    public void setMouseSensitivity(float sensitivity) {
        this.mouseSensitivity = sensitivity;
    }
    
    public enum CameraMovement {
        FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN
    }
}