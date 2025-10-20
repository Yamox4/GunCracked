package com.example.input;

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
 * Wrapper around jME3's input system for easier input handling
 */
public class GameInputManager implements ActionListener, AnalogListener {
    private final InputManager inputManager;
    private final Map<String, Boolean> keyStates = new HashMap<>();
    private final Map<String, Boolean> mouseStates = new HashMap<>();
    private final Set<String> pressedThisFrame = new HashSet<>();
    private final Set<String> releasedThisFrame = new HashSet<>();
    
    private final Vector2f mouseDelta = new Vector2f();
    private float mouseWheelDelta = 0f;
    
    public GameInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
        setupDefaultMappings();
    }
    
    /**
     * Setup default key and mouse mappings
     */
    private void setupDefaultMappings() {
        // Movement keys
        inputManager.addMapping("MOVE_FORWARD", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("MOVE_BACKWARD", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("MOVE_LEFT", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("MOVE_RIGHT", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("JUMP", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("CROUCH", new KeyTrigger(KeyInput.KEY_LSHIFT));
        
        // Action keys
        inputManager.addMapping("INTERACT", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("RELOAD", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("ESCAPE", new KeyTrigger(KeyInput.KEY_ESCAPE));
        
        // Mouse
        inputManager.addMapping("MOUSE_LEFT", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("MOUSE_RIGHT", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("MOUSE_MIDDLE", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        
        // Mouse movement
        inputManager.addMapping("MOUSE_X", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MOUSE_X_NEG", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("MOUSE_Y", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("MOUSE_Y_NEG", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MOUSE_WHEEL", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("MOUSE_WHEEL_NEG", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        
        // Register listeners
        inputManager.addListener(this, 
            "MOVE_FORWARD", "MOVE_BACKWARD", "MOVE_LEFT", "MOVE_RIGHT",
            "JUMP", "CROUCH", "INTERACT", "RELOAD", "ESCAPE",
            "MOUSE_LEFT", "MOUSE_RIGHT", "MOUSE_MIDDLE",
            "MOUSE_X", "MOUSE_X_NEG", "MOUSE_Y", "MOUSE_Y_NEG",
            "MOUSE_WHEEL", "MOUSE_WHEEL_NEG"
        );
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
    
    /**
     * Check if a key is currently pressed
     */
    public boolean isKeyDown(String keyName) {
        return keyStates.getOrDefault(keyName, false);
    }
    
    /**
     * Check if a key was pressed this frame
     */
    public boolean isKeyPressed(String keyName) {
        return pressedThisFrame.contains(keyName);
    }
    
    /**
     * Check if a key was released this frame
     */
    public boolean isKeyReleased(String keyName) {
        return releasedThisFrame.contains(keyName);
    }
    
    /**
     * Check if a mouse button is currently pressed
     */
    public boolean isMouseDown(String buttonName) {
        return mouseStates.getOrDefault(buttonName, false);
    }
    
    /**
     * Check if a mouse button was pressed this frame
     */
    public boolean isMousePressed(String buttonName) {
        return pressedThisFrame.contains(buttonName);
    }
    
    /**
     * Check if a mouse button was released this frame
     */
    public boolean isMouseReleased(String buttonName) {
        return releasedThisFrame.contains(buttonName);
    }
    
    /**
     * Get mouse movement delta this frame
     */
    public Vector2f getMouseDelta() {
        return mouseDelta.clone();
    }
    
    /**
     * Get mouse wheel delta this frame
     */
    public float getMouseWheelDelta() {
        return mouseWheelDelta;
    }
    
    /**
     * Add a custom key mapping
     */
    public void addKeyMapping(String name, int keyCode) {
        inputManager.addMapping(name, new KeyTrigger(keyCode));
        inputManager.addListener(this, name);
    }
    
    /**
     * Add a custom mouse button mapping
     */
    public void addMouseMapping(String name, int mouseButton) {
        inputManager.addMapping(name, new MouseButtonTrigger(mouseButton));
        inputManager.addListener(this, name);
    }
}