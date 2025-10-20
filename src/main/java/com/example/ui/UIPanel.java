package com.example.ui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 * UI Panel component for creating background panels
 */
public class UIPanel {
    
    private String id;
    private Node node;
    private Geometry background;
    private float x, y, width, height;
    
    public UIPanel(String id, float x, float y, float width, float height, AssetManager assetManager) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        // Create node
        this.node = new Node("UIPanel_" + id);
        
        // Create background
        Quad quad = new Quad(width, height);
        this.background = new Geometry("Panel_" + id, quad);
        
        // Create material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.2f, 0.2f, 0.2f, 0.8f));
        background.setMaterial(mat);
        
        // Position
        background.setLocalTranslation(x, y, 0);
        
        // Add to node
        node.attachChild(background);
    }
    
    /**
     * Set panel color
     */
    public void setColor(ColorRGBA color) {
        Material mat = background.getMaterial();
        mat.setColor("Color", color);
    }
    
    /**
     * Set panel transparency
     */
    public void setAlpha(float alpha) {
        Material mat = background.getMaterial();
        ColorRGBA currentColor = (ColorRGBA) mat.getParam("Color").getValue();
        mat.setColor("Color", new ColorRGBA(currentColor.r, currentColor.g, currentColor.b, alpha));
    }
    
    /**
     * Set panel position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        background.setLocalTranslation(x, y, 0);
    }
    
    /**
     * Set panel size
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        
        // Update geometry
        Quad newQuad = new Quad(width, height);
        background.setMesh(newQuad);
    }
    
    /**
     * Show/hide panel
     */
    public void setVisible(boolean visible) {
        if (visible) {
            if (node.getParent() == null) {
                // Re-attach if needed
            }
        } else {
            node.removeFromParent();
        }
    }
    
    // Getters
    public String getId() { return id; }
    public Node getNode() { return node; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}