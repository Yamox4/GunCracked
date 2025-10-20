package com.physics3d.input;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 * Simple input handling system.
 */
public class InputHandler implements ActionListener {
    
    private InputManager inputManager;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean spacePressed = false;
    
    public InputHandler(InputManager inputManager) {
        this.inputManager = inputManager;
        setupKeys();
    }
    
    private void setupKeys() {
        // Map keys to actions
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        
        // Add listener
        inputManager.addListener(this, "Left", "Right", "Up", "Down", "Jump");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "Left":
                leftPressed = isPressed;
                break;
            case "Right":
                rightPressed = isPressed;
                break;
            case "Up":
                upPressed = isPressed;
                break;
            case "Down":
                downPressed = isPressed;
                break;
            case "Jump":
                spacePressed = isPressed;
                break;
        }
    }
    
    // Simple getters for input state
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isJumpPressed() { return spacePressed; }
}