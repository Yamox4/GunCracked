package com.example.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;

/**
 * JME Input Module - handles keyboard, mouse, and controller input
 */
public class InputModule implements ActionListener, AnalogListener {
    private final InputManager inputManager;
    private final Map<String, Boolean> keyStates = new HashMap<>();
    private final Map<String, Boolean> mouseStates = new HashMap<>();
    private final Set<String> pressedThisFrame = new HashSet<>();
    private final Set<String> releasedThisFrame = new HashSet<>();
    
    private final Vector2f mouseDelta = new Vector2f();
    private float mouseWheelDelta = 0f;
    private boolean initialized = false;
    
    public InputModule(InputManager inputManager) {
        this.inputManager = inputManager;
    }
    
    /**
     * Initialize the input module
     */
    public void initialize() {
        if (initialized) return;
        
        setupDefaultMappings();
        initialized = true;
    }
    
    /**
     * Setup default input mappings
     */
    private void setupDefaultMappings() {
        // Clear existing mappings
        inputManager.clearMappings();
        
        // Movement keys
        addKeyMapping("MOVE_FORWARD", KeyInput.KEY_W);
        addKeyMapping("MOVE_BACKWARD", KeyInput.KEY_S);
        addKeyMapping("MOVE_LEFT", KeyInput.KEY_A);
        addKeyMapping("MOVE_RIGHT", KeyInput.KEY_D);
        addKeyMapping("JUMP", KeyInput.KEY_SPACE);
        addKeyMapping("CROUCH", KeyInput.KEY_LSHIFT);
        
        // Action keys
        addKeyMapping("INTERACT", KeyInput.KEY_E);
        addKeyMapping("RELOAD", KeyInput.KEY_R);
        addKeyMapping("ESCAPE", KeyInput.KEY_ESCAPE);
        
        // Mouse buttons
        addMouseMapping("MOUSE_LEFT", MouseInput.BUTTON_LEFT);
        addMouseMapping("MOUSE_RIGHT", MouseInput.BUTTON_RIGHT);
        addMouseMapping("MOUSE_MIDDLE", MouseInput.BUTTON_MIDDLE);
        
        // Mouse movement
        inputManager.addMapping("MOUSE_X", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MOUSE_X_NEG", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("MOUSE_Y", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("MOUSE_Y_NEG", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MOUSE_WHEEL", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("MOUSE_WHEEL_NEG", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        
        // Register listeners for mouse movement
        inputManager.addListener(this, "MOUSE_X", "MOUSE_X_NEG", "MOUSE_Y", "MOUSE_Y_NEG", 
                                "MOUSE_WHEEL", "MOUSE_WHEEL_NEG");
    }
    
    /**
     * Update input state - call once per frame
     */
    public void update() {
        pressedThisFrame.clear();
        releasedThisFrame.clear();
        mouseDelta.set(0, 0);
        mouseWheelDelta = 0f;
    }
    
    /**
     * Add a key mapping
     */
    public void addKeyMapping(String name, int keyCode) {
        inputManager.addMapping(name, new KeyTrigger(keyCode));
        inputManager.addListener(this, name);
    }
    
    /**
     * Add a mouse button mapping
     */
    public void addMouseMapping(String name, int mouseButton) {
        inputManager.addMapping(name, new MouseButtonTrigger(mouseButton));
        inputManager.addListener(this, name);
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.startsWith("MOUSE_")) {
            mouseStates.put(name, isPressed);
        } else {
            keyStates.put(name, isPressed);
        }
        
        if (isPressed) {
            pressedThisFrame.add(name);
        } else {
            releasedThisFrame.add(name);
        }
    }
    
    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch (name) {
            case "MOUSE_X":
                mouseDelta.x += value;
                break;
            case "MOUSE_X_NEG":
                mouseDelta.x -= value;
                break;
            case "MOUSE_Y":
                mouseDelta.y += value;
                break;
            case "MOUSE_Y_NEG":
                mouseDelta.y -= value;
                break;
            case "MOUSE_WHEEL":
                mouseWheelDelta += value;
                break;
            case "MOUSE_WHEEL_NEG":
                mouseWheelDelta -= value;
                break;
        }
    }
    
    // Input state queries
    public boolean isKeyDown(String keyName) {
        return keyStates.getOrDefault(keyName, false);
    }
    
    public boolean isKeyPressed(String keyName) {
        return pressedThisFrame.contains(keyName);
    }
    
    public boolean isKeyReleased(String keyName) {
        return releasedThisFrame.contains(keyName);
    }
    
    public boolean isMouseDown(String buttonName) {
        return mouseStates.getOrDefault(buttonName, false);
    }
    
    public boolean isMousePressed(String buttonName) {
        return pressedThisFrame.contains(buttonName);
    }
    
    public boolean isMouseReleased(String buttonName) {
        return releasedThisFrame.contains(buttonName);
    }
    
    public Vector2f getMouseDelta() {
        return mouseDelta.clone();
    }
    
    public float getMouseWheelDelta() {
        return mouseWheelDelta;
    }
    
    /**
     * Cleanup the input module
     */
    public void cleanup() {
        if (inputManager != null) {
            inputManager.clearMappings();
        }
        keyStates.clear();
        mouseStates.clear();
        pressedThisFrame.clear();
        releasedThisFrame.clear();
        initialized = false;
    }
}