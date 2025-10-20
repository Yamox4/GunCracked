package com.example.ui;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 * UI Label component for displaying text
 */
public class UILabel {
    
    private String id;
    private String text;
    private Node node;
    private BitmapText textElement;
    private float x, y;
    
    public UILabel(String id, String text, float x, float y, BitmapFont font) {
        this.id = id;
        this.text = text;
        this.x = x;
        this.y = y;
        
        // Create node
        this.node = new Node("UILabel_" + id);
        
        // Create text
        this.textElement = new BitmapText(font, false);
        textElement.setText(text);
        textElement.setSize(font.getCharSet().getRenderedSize());
        textElement.setColor(ColorRGBA.White);
        textElement.setLocalTranslation(x, y, 0);
        
        // Add to node
        node.attachChild(textElement);
    }
    
    /**
     * Set label text
     */
    public void setText(String newText) {
        this.text = newText;
        textElement.setText(newText);
    }
    
    /**
     * Set label color
     */
    public void setColor(ColorRGBA color) {
        textElement.setColor(color);
    }
    
    /**
     * Set label position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        textElement.setLocalTranslation(x, y, 0);
    }
    
    /**
     * Set text size
     */
    public void setSize(float size) {
        textElement.setSize(size);
    }
    
    /**
     * Set label visibility
     */
    public void setVisible(boolean visible) {
        if (visible) {
            textElement.setCullHint(com.jme3.scene.Spatial.CullHint.Never);
        } else {
            textElement.setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        }
    }
    
    // Getters
    public String getId() { return id; }
    public String getText() { return text; }
    public Node getNode() { return node; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return textElement.getLineWidth(); }
    public float getHeight() { return textElement.getLineHeight(); }
}