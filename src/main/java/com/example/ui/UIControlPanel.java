package com.example.ui;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 * Control panel with multiple buttons and labels
 */
public class UIControlPanel {
    
    private String id;
    private Node node;
    private UIPanel backgroundPanel;
    private List<UIButton> buttons;
    private List<UILabel> labels;
    private float x, y, width;
    private float currentY;
    private float buttonHeight = 30f;
    private float spacing = 5f;
    

    
    private AssetManager assetManager;
    private BitmapFont font;
    
    public UIControlPanel(String id, float x, float y, float width, AssetManager assetManager, BitmapFont font) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.currentY = y + 10f;
        this.assetManager = assetManager;
        this.font = font;
        
        this.buttons = new ArrayList<>();
        this.labels = new ArrayList<>();
        
        // Create node
        this.node = new Node("UIControlPanel_" + id);
        
        // Create background panel (will be resized as content is added)
        this.backgroundPanel = new UIPanel(id + "_bg", x, y, width, 50f, assetManager);
        backgroundPanel.setColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 0.9f));
        node.attachChild(backgroundPanel.getNode());
    }
    
    /**
     * Add a button to the control panel
     */
    public UIButton addButton(String buttonId, String text, Runnable action) {
        UIButton button = new UIButton(
            buttonId, text, 
            x + spacing, currentY, 
            width - (spacing * 2), buttonHeight,
            assetManager, font
        );
        
        button.setClickAction(action);
        buttons.add(button);
        node.attachChild(button.getNode());
        
        currentY += buttonHeight + spacing;
        updateBackgroundSize();
        
        return button;
    }
    
    /**
     * Add a label to the control panel
     */
    public UILabel addLabel(String labelId, String text) {
        UILabel label = new UILabel(
            labelId, text,
            x + spacing, currentY + 20f,
            font
        );
        
        labels.add(label);
        node.attachChild(label.getNode());
        
        currentY += 25f + spacing;
        updateBackgroundSize();
        
        return label;
    }
    
    /**
     * Add a section separator
     */
    public void addSeparator() {
        currentY += spacing * 2;
        updateBackgroundSize();
    }
    
    /**
     * Update background panel size based on content
     */
    private void updateBackgroundSize() {
        float height = currentY - y + 10f;
        backgroundPanel.setSize(width, height);
    }
    
    /**
     * Get button by ID
     */
    public UIButton getButton(String buttonId) {
        return buttons.stream()
                .filter(button -> button.getId().equals(buttonId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get label by ID
     */
    public UILabel getLabel(String labelId) {
        return labels.stream()
                .filter(label -> label.getId().equals(labelId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Clear all content
     */
    public void clear() {
        buttons.forEach(button -> button.getNode().removeFromParent());
        labels.forEach(label -> label.getNode().removeFromParent());
        buttons.clear();
        labels.clear();
        currentY = y + 10f;
        updateBackgroundSize();
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
        
        // Update all buttons and labels
        buttons.forEach(button -> button.setPosition(
            button.getX() + deltaX, 
            button.getY() + deltaY
        ));
        
        labels.forEach(label -> label.setPosition(
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
    public float getHeight() { return currentY - y + 10f; }
}