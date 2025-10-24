package com.example.entity;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 * Player entity with FPS camera and capsule physics collider
 */
public class Player extends Entity {
    
    private CharacterControl characterControl;
    private BulletAppState bulletAppState;
    private AssetManager assetManager;
    private Node rootNode;
    
    // Player settings
    private final float capsuleRadius = 0.4f;
    private final float capsuleHeight = 1.8f;
    private float eyeHeight = 1.6f; // Camera height from ground
    private final float mass = 80f;
    private float jumpSpeed = 5f; // Further reduced
    private final float fallSpeed = 20f; // Further reduced
    private float gravity = 20f; // Further reduced
    
    // Movement state
    private boolean onGround = false;
    
    public Player(String id, Vector3f startPosition, 
                  BulletAppState bulletAppState, AssetManager assetManager, Node rootNode) {
        super(id, startPosition);
        this.bulletAppState = bulletAppState;
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
    @Override
    public void initialize() {
        createPlayerCapsule();
        setupPhysics();
        
        // Start inactive - will be activated when switching to FPS mode
        active = false;
        
        System.out.println("Player initialized at: " + position + " (inactive until FPS mode)");
    }
    
    private void createPlayerCapsule() {
        // Create visual representation (optional - can be invisible)
        Cylinder capsuleMesh = new Cylinder(8, 16, capsuleRadius, capsuleHeight, true);
        Geometry capsuleGeom = new Geometry(id + "_visual", capsuleMesh);
        
        // Create semi-transparent material so player can see through it
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.0f, 1.0f, 0.0f, 0.3f));
        mat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
        capsuleGeom.setMaterial(mat);
        
        // Position the visual capsule
        capsuleGeom.setLocalTranslation(position);
        
        this.spatial = capsuleGeom;
        rootNode.attachChild(capsuleGeom);
    }
    
    private void setupPhysics() {
        // Create capsule collision shape
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(capsuleRadius, capsuleHeight);
        
        // Create character control with smaller step height for smoother movement
        characterControl = new CharacterControl(capsuleShape, 0.05f);
        characterControl.setJumpSpeed(jumpSpeed);
        characterControl.setFallSpeed(fallSpeed);
        characterControl.setGravity(gravity);
        characterControl.setPhysicsLocation(position);
        
        // Add physics control to spatial
        spatial.addControl(characterControl);
        
        // Add to physics space
        bulletAppState.getPhysicsSpace().add(characterControl);
        
        System.out.println("Player physics initialized - Jump: " + jumpSpeed + ", Gravity: " + gravity + ", Fall: " + fallSpeed);
    }
    
    @Override
    public void update(float tpf) {
        if (!active || characterControl == null) return;
        
        // Update position from physics
        position = characterControl.getPhysicsLocation().clone();
        
        // Update visual representation position
        if (spatial != null) {
            spatial.setLocalTranslation(position);
        }
        
        // Update ground state
        onGround = characterControl.onGround();
    }
    
    @Override
    public void cleanup() {
        if (characterControl != null) {
            bulletAppState.getPhysicsSpace().remove(characterControl);
        }
        if (spatial != null) {
            spatial.removeFromParent();
        }
        System.out.println("Player cleaned up");
    }
    
    /**
     * Teleport player to a new position
     */
    public void teleport(Vector3f newPosition) {
        // Ensure player is positioned properly above ground
        Vector3f adjustedPosition = newPosition.clone();
        adjustedPosition.y = Math.max(adjustedPosition.y, 1.0f); // Minimum height above ground
        
        this.position = adjustedPosition.clone();
        if (characterControl != null) {
            characterControl.setPhysicsLocation(adjustedPosition);
            // Give physics a moment to settle
            characterControl.setWalkDirection(Vector3f.ZERO);
        }
        
        
        System.out.println("Player teleported to: " + adjustedPosition);
    }
    
    /**
     * Set player visibility (useful for switching between FPS and free camera)
     */
    public void setVisible(boolean visible) {
        if (spatial != null) {
            if (visible) {
                if (spatial.getParent() == null) {
                    rootNode.attachChild(spatial);
                }
            } else {
                spatial.removeFromParent();
            }
        }
    }
    
    // Getters
    
    public CharacterControl getCharacterControl() {
        return characterControl;
    }
    
    public boolean isOnGround() {
        return onGround;
    }
    
    
    public float getEyeHeight() {
        return eyeHeight;
    }
    
    // Setters
    public void setEyeHeight(float eyeHeight) {
        this.eyeHeight = eyeHeight;
    }
    
    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
        if (characterControl != null) {
            characterControl.setJumpSpeed(jumpSpeed);
        }
    }
    
    public void setGravity(float gravity) {
        this.gravity = gravity;
        if (characterControl != null) {
            characterControl.setGravity(gravity);
        }
    }
    
}