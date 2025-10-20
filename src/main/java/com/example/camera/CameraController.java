package com.example.camera;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 * Advanced camera controller with multiple camera modes
 */
public class CameraController extends BaseAppState implements ActionListener, AnalogListener {
    
    public enum CameraMode {
        FREE_FLY,
        ORBIT,
        FIRST_PERSON,
        THIRD_PERSON
    }
    
    private Camera camera;
    private InputManager inputManager;
    private FlyByCamera flyCam;
    private ChaseCamera chaseCamera;
    
    private CameraMode currentMode = CameraMode.FREE_FLY;
    private Spatial target;
    
    // Camera settings
    private float moveSpeed = 10f;
    private float rotationSpeed = 2f;
    private float zoomSpeed = 5f;
    private float minDistance = 1f;
    private float maxDistance = 50f;
    
    // Input mappings
    private static final String MAPPING_FORWARD = "Forward";
    private static final String MAPPING_BACKWARD = "Backward";
    private static final String MAPPING_LEFT = "Left";
    private static final String MAPPING_RIGHT = "Right";
    private static final String MAPPING_UP = "Up";
    private static final String MAPPING_DOWN = "Down";
    private static final String MAPPING_ROTATE_LEFT = "RotateLeft";
    private static final String MAPPING_ROTATE_RIGHT = "RotateRight";
    private static final String MAPPING_ROTATE_UP = "RotateUp";
    private static final String MAPPING_ROTATE_DOWN = "RotateDown";
    private static final String MAPPING_ZOOM_IN = "ZoomIn";
    private static final String MAPPING_ZOOM_OUT = "ZoomOut";
    private static final String MAPPING_TOGGLE_MODE = "ToggleMode";
    
    @Override
    protected void initialize(Application app) {
        this.camera = app.getCamera();
        this.inputManager = app.getInputManager();
        if (app instanceof SimpleApplication) {
            this.flyCam = ((SimpleApplication) app).getFlyByCamera();
        }
        
        setupInputMappings();
        setMode(CameraMode.FREE_FLY);
    }
    
    @Override
    protected void cleanup(Application app) {
        removeInputMappings();
        if (chaseCamera != null) {
            chaseCamera.setEnabled(false);
        }
    }
    
    @Override
    protected void onEnable() {
        // Camera controller enabled
    }
    
    @Override
    protected void onDisable() {
        // Camera controller disabled
    }
    
    /**
     * Set camera mode
     */
    public void setMode(CameraMode mode) {
        // Cleanup previous mode
        if (chaseCamera != null) {
            chaseCamera.setEnabled(false);
        }
        
        this.currentMode = mode;
        
        switch (mode) {
            case FREE_FLY:
                setupFreeFlyMode();
                break;
            case ORBIT:
                setupOrbitMode();
                break;
            case FIRST_PERSON:
                setupFirstPersonMode();
                break;
            case THIRD_PERSON:
                setupThirdPersonMode();
                break;
        }
    }
    
    /**
     * Set target for orbit and chase cameras
     */
    public void setTarget(Spatial target) {
        this.target = target;
        // ChaseCamera target is set during creation
    }
    
    private void setupFreeFlyMode() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(moveSpeed);
        flyCam.setRotationSpeed(rotationSpeed);
    }
    
    private void setupOrbitMode() {
        if (flyCam != null) {
            flyCam.setEnabled(false);
        }
        if (target != null) {
            chaseCamera = new ChaseCamera(camera, target, inputManager);
            chaseCamera.setDefaultDistance(15f);
            chaseCamera.setMinDistance(minDistance);
            chaseCamera.setMaxDistance(maxDistance);
            chaseCamera.setDefaultHorizontalRotation(FastMath.PI);
            chaseCamera.setDefaultVerticalRotation(0.3f);
            chaseCamera.setRotationSpeed(rotationSpeed);
            chaseCamera.setEnabled(true);
        }
    }
    
    private void setupFirstPersonMode() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(moveSpeed * 0.5f);
        flyCam.setRotationSpeed(rotationSpeed);
        
        // Position camera at eye level
        camera.setLocation(new Vector3f(0, 1.8f, 0));
    }
    
    private void setupThirdPersonMode() {
        if (flyCam != null) {
            flyCam.setEnabled(false);
        }
        if (target != null) {
            chaseCamera = new ChaseCamera(camera, target, inputManager);
            chaseCamera.setDefaultDistance(8f);
            chaseCamera.setMinDistance(2f);
            chaseCamera.setMaxDistance(20f);
            chaseCamera.setDefaultHorizontalRotation(0);
            chaseCamera.setDefaultVerticalRotation(0.3f);
            chaseCamera.setRotationSpeed(rotationSpeed);
            chaseCamera.setEnabled(true);
        }
    }
    
    private void setupInputMappings() {
        // Movement keys
        inputManager.addMapping(MAPPING_FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(MAPPING_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(MAPPING_LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(MAPPING_RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(MAPPING_UP, new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(MAPPING_DOWN, new KeyTrigger(KeyInput.KEY_E));
        
        // Rotation
        inputManager.addMapping(MAPPING_ROTATE_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(MAPPING_ROTATE_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(MAPPING_ROTATE_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(MAPPING_ROTATE_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        
        // Zoom
        inputManager.addMapping(MAPPING_ZOOM_IN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(MAPPING_ZOOM_OUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        
        // Mode toggle
        inputManager.addMapping(MAPPING_TOGGLE_MODE, new KeyTrigger(KeyInput.KEY_C));
        
        // Add listeners
        inputManager.addListener(this, MAPPING_FORWARD, MAPPING_BACKWARD, MAPPING_LEFT, MAPPING_RIGHT,
                                MAPPING_UP, MAPPING_DOWN, MAPPING_TOGGLE_MODE);
        inputManager.addListener(this, MAPPING_ROTATE_LEFT, MAPPING_ROTATE_RIGHT, 
                                MAPPING_ROTATE_UP, MAPPING_ROTATE_DOWN,
                                MAPPING_ZOOM_IN, MAPPING_ZOOM_OUT);
    }
    
    private void removeInputMappings() {
        inputManager.removeListener(this);
        inputManager.deleteMapping(MAPPING_FORWARD);
        inputManager.deleteMapping(MAPPING_BACKWARD);
        inputManager.deleteMapping(MAPPING_LEFT);
        inputManager.deleteMapping(MAPPING_RIGHT);
        inputManager.deleteMapping(MAPPING_UP);
        inputManager.deleteMapping(MAPPING_DOWN);
        inputManager.deleteMapping(MAPPING_ROTATE_LEFT);
        inputManager.deleteMapping(MAPPING_ROTATE_RIGHT);
        inputManager.deleteMapping(MAPPING_ROTATE_UP);
        inputManager.deleteMapping(MAPPING_ROTATE_DOWN);
        inputManager.deleteMapping(MAPPING_ZOOM_IN);
        inputManager.deleteMapping(MAPPING_ZOOM_OUT);
        inputManager.deleteMapping(MAPPING_TOGGLE_MODE);
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(MAPPING_TOGGLE_MODE) && isPressed) {
            toggleCameraMode();
        }
    }
    
    @Override
    public void onAnalog(String name, float value, float tpf) {
        // Custom analog input handling if needed
    }
    
    private void toggleCameraMode() {
        switch (currentMode) {
            case FREE_FLY:
                setMode(CameraMode.ORBIT);
                break;
            case ORBIT:
                setMode(CameraMode.THIRD_PERSON);
                break;
            case THIRD_PERSON:
                setMode(CameraMode.FIRST_PERSON);
                break;
            case FIRST_PERSON:
                setMode(CameraMode.FREE_FLY);
                break;
        }
    }
    
    // Getters and setters
    public CameraMode getCurrentMode() {
        return currentMode;
    }
    
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
        if (flyCam != null) {
            flyCam.setMoveSpeed(moveSpeed);
        }
    }
    
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
        if (flyCam != null) {
            flyCam.setRotationSpeed(rotationSpeed);
        }
        if (chaseCamera != null) {
            chaseCamera.setRotationSpeed(rotationSpeed);
        }
    }
    
    public void setZoomSpeed(float zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
        // Zoom speed is set during camera creation
    }
}