package com.example.modules;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * JME Camera Module - camera control and management
 */
public class CameraModule {
    private Camera camera;
    private Vector3f position;
    private Quaternion rotation;
    private float yaw = 0f;
    private float pitch = 0f;
    private float mouseSensitivity = 1.0f;
    private boolean initialized = false;
    
    // Camera constraints
    private float minPitch = -FastMath.HALF_PI + 0.1f;
    private float maxPitch = FastMath.HALF_PI - 0.1f;
    
    public CameraModule(Camera camera) {
        this.camera = camera;
        this.position = new Vector3f();
        this.rotation = new Quaternion();
    }
    
    /**
     * Initialize the camera module
     */
    public void initialize() {
        if (initialized) return;
        
        // Set initial camera state
        position.set(camera.getLocation());
        rotation.set(camera.getRotation());
        
        // Extract initial yaw and pitch from camera rotation
        float[] angles = rotation.toAngles(null);
        yaw = angles[1];
        pitch = angles[0];
        
        // Load mouse sensitivity from config
        mouseSensitivity = ConfigModule.getFloat("input.mouse_sensitivity", 1.0f);
        
        initialized = true;
        LoggingModule.info("Camera module initialized");
    }
    
    /**
     * Update camera - call once per frame
     */
    public void update(float deltaTime) {
        if (!initialized) return;
        
        // Apply position and rotation to camera
        camera.setLocation(position);
        camera.setRotation(rotation);
    }
    
    /**
     * Handle mouse movement for camera rotation
     */
    public void handleMouseMovement(float deltaX, float deltaY) {
        if (!initialized) return;
        
        // Apply mouse sensitivity
        deltaX *= mouseSensitivity * 0.01f;
        deltaY *= mouseSensitivity * 0.01f;
        
        // Invert Y if configured
        if (ConfigModule.isMouseInverted()) {
            deltaY = -deltaY;
        }
        
        // Update yaw and pitch
        yaw -= deltaX;
        pitch -= deltaY;
        
        // Clamp pitch to prevent camera flipping
        pitch = FastMath.clamp(pitch, minPitch, maxPitch);
        
        // Update rotation quaternion
        rotation.fromAngles(pitch, yaw, 0);
    }
    
    /**
     * Move camera relative to its current orientation
     */
    public void moveRelative(Vector3f movement) {
        if (!initialized) return;
        
        Vector3f worldMovement = rotation.mult(movement);
        position.addLocal(worldMovement);
    }
    
    /**
     * Move camera in world space
     */
    public void moveWorld(Vector3f movement) {
        if (!initialized) return;
        
        position.addLocal(movement);
    }
    
    /**
     * Set camera position
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }
    
    /**
     * Get camera position
     */
    public Vector3f getPosition() {
        return position.clone();
    }
    
    /**
     * Set camera rotation
     */
    public void setRotation(Quaternion rotation) {
        this.rotation.set(rotation);
        
        // Update yaw and pitch
        float[] angles = rotation.toAngles(null);
        yaw = angles[1];
        pitch = angles[0];
    }
    
    /**
     * Get camera rotation
     */
    public Quaternion getRotation() {
        return rotation.clone();
    }
    
    /**
     * Look at a target position
     */
    public void lookAt(Vector3f target, Vector3f up) {
        if (!initialized) return;
        
        Vector3f direction = target.subtract(position).normalizeLocal();
        rotation.lookAt(direction, up);
        
        // Update yaw and pitch
        float[] angles = rotation.toAngles(null);
        yaw = angles[1];
        pitch = angles[0];
    }
    
    /**
     * Get camera forward direction
     */
    public Vector3f getForward() {
        return rotation.mult(Vector3f.UNIT_Z.negate());
    }
    
    /**
     * Get camera right direction
     */
    public Vector3f getRight() {
        return rotation.mult(Vector3f.UNIT_X);
    }
    
    /**
     * Get camera up direction
     */
    public Vector3f getUp() {
        return rotation.mult(Vector3f.UNIT_Y);
    }
    
    /**
     * Set mouse sensitivity
     */
    public void setMouseSensitivity(float sensitivity) {
        this.mouseSensitivity = sensitivity;
    }
    
    /**
     * Get mouse sensitivity
     */
    public float getMouseSensitivity() {
        return mouseSensitivity;
    }
    
    /**
     * Set pitch constraints
     */
    public void setPitchConstraints(float minPitch, float maxPitch) {
        this.minPitch = minPitch;
        this.maxPitch = maxPitch;
    }
    
    /**
     * Get current yaw angle
     */
    public float getYaw() {
        return yaw;
    }
    
    /**
     * Get current pitch angle
     */
    public float getPitch() {
        return pitch;
    }
    
    /**
     * Cleanup the camera module
     */
    public void cleanup() {
        initialized = false;
        LoggingModule.info("Camera module cleaned up");
    }
}