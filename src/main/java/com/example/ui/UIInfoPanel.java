package com.example.ui;

import java.util.HashMap;
import java.util.Map;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 * Info panel for displaying statistics and information
 */
public class UIInfoPanel {
    
    private String id;
    private Node node;
    private UIPanel backgroundPanel;
    private Map<String, UILabel> infoLabels;
    private float x, y, width, height;
    private float currentY;
    private float lineHeight = 20f;
    private float spacing = 2f;
    private AssetManager assetManager;
    private BitmapFont font;
    
    public UIInfoPanel(String id, float x, float y, float width, float height, 
                      AssetManager assetManager, BitmapFont font) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentY = y + height - 25f; // Start from top
        this.assetManager = assetManager;
        this.font = font;
        
        this.infoLabels = new HashMap<>();
        
        // Create node
        this.node = new Node("UIInfoPanel_" + id);
        
        // Create background panel
        this.backgroundPanel = new UIPanel(id + "_bg", x, y, width, height, assetManager);
        backgroundPanel.setColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.7f));
        node.attachChild(backgroundPanel.getNode());
        
        // Add title
        UILabel titleLabel = new UILabel(id + "_title", "Physics Info", x + 10f, currentY, font);
        titleLabel.setColor(ColorRGBA.Yellow);
        node.attachChild(titleLabel.getNode());
        currentY -= lineHeight + spacing * 2;
    }
    
    /**
     * Set or update an info value
     */
    public void setInfo(String key, String value) {
        UILabel label = infoLabels.get(key);
        
        if (label == null) {
            // Create new label
            String text = key + ": " + value;
            label = new UILabel(id + "_" + key, text, x + 10f, currentY, font);
            label.setColor(ColorRGBA.White);
            infoLabels.put(key, label);
            node.attachChild(label.getNode());
            currentY -= lineHeight + spacing;
        } else {
            // Update existing label
            String text = key + ": " + value;
            label.setText(text);
        }
    }
    
    /**
     * Set info with specific color
     */
    public void setInfo(String key, String value, ColorRGBA color) {
        setInfo(key, value);
        UILabel label = infoLabels.get(key);
        if (label != null) {
            label.setColor(color);
        }
    }
    
    /**
     * Remove an info entry
     */
    public void removeInfo(String key) {
        UILabel label = infoLabels.remove(key);
        if (label != null) {
            label.getNode().removeFromParent();
        }
    }
    
    /**
     * Clear all info entries
     */
    public void clearInfo() {
        infoLabels.values().forEach(label -> label.getNode().removeFromParent());
        infoLabels.clear();
        currentY = y + height - 45f; // Reset position below title
    }
    
    /**
     * Update multiple info values at once
     */
    public void updateInfo(Map<String, String> infoMap) {
        infoMap.forEach(this::setInfo);
    }
    
    /**
     * Set panel transparency
     */
    public void setAlpha(float alpha) {
        backgroundPanel.setAlpha(alpha);
    }
    
    /**
     * Set panel position
     */
    public void setPosition(float x, float y) {
        float deltaX = x - this.x;
        float deltaY = y - this.y;
        
        this.x = x;
        this.y = y;
        
        backgroundPanel.setPosition(x, y);
        
        // Update all labels
        infoLabels.values().forEach(label -> label.setPosition(
            label.getX() + deltaX, 
            label.getY() + deltaY
        ));
    }
    
    // Getters
    public String getId() { return id; }
    public Node getNode() { return node; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}