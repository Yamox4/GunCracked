package com.physics3d.game;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 * Simple game demo using jMonkeyEngine.
 */
public class GameLoop extends SimpleApplication implements ActionListener {
    
    private Geometry playerGeom;
    private Vector3f playerPosition = new Vector3f(0, 1, 0);
    private float moveSpeed = 5.0f;
    private boolean[] keys = new boolean[5]; // left, right, up, down, jump
    
    @Override
    public void simpleInitApp() {
        // Set up camera
        cam.setLocation(new Vector3f(0, 5, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);
        
        // Set background
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        
        // Create ground
        Box ground = new Box(10, 0.1f, 10);
        Geometry groundGeom = new Geometry("Ground", ground);
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setColor("Color", ColorRGBA.Green);
        groundGeom.setMaterial(groundMat);
        groundGeom.setLocalTranslation(0, -2, 0);
        rootNode.attachChild(groundGeom);
        
        // Create player
        Sphere playerSphere = new Sphere(32, 32, 0.5f);
        playerGeom = new Geometry("Player", playerSphere);
        Material playerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        playerMat.setColor("Color", ColorRGBA.Yellow);
        playerGeom.setMaterial(playerMat);
        playerGeom.setLocalTranslation(playerPosition);
        rootNode.attachChild(playerGeom);
        
        // Create some obstacles
        for (int i = 0; i < 5; i++) {
            Box box = new Box(0.5f, 1, 0.5f);
            Geometry boxGeom = new Geometry("Box" + i, box);
            Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            boxMat.setColor("Color", ColorRGBA.Red);
            boxGeom.setMaterial(boxMat);
            boxGeom.setLocalTranslation((i - 2) * 3, 0, -5);
            rootNode.attachChild(boxGeom);
        }
        
        // Set up input
        setupInput();
        
        System.out.println("🎮 3D Game Demo Started!");
        System.out.println("Controls: WASD to move, SPACE to jump");
        System.out.println("Mouse to look around, ESC to exit");
    }
    
    private void setupInput() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        
        inputManager.addListener(this, "Left", "Right", "Forward", "Back", "Jump");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "Left": keys[0] = isPressed; break;
            case "Right": keys[1] = isPressed; break;
            case "Forward": keys[2] = isPressed; break;
            case "Back": keys[3] = isPressed; break;
            case "Jump": keys[4] = isPressed; break;
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        Vector3f movement = new Vector3f();
        
        if (keys[0]) movement.x -= moveSpeed * tpf; // Left
        if (keys[1]) movement.x += moveSpeed * tpf; // Right
        if (keys[2]) movement.z -= moveSpeed * tpf; // Forward
        if (keys[3]) movement.z += moveSpeed * tpf; // Back
        if (keys[4]) movement.y += moveSpeed * tpf; // Jump
        
        if (movement.length() > 0) {
            playerPosition.addLocal(movement);
            playerGeom.setLocalTranslation(playerPosition);
            System.out.println("Player at: " + playerPosition);
        }
    }
}