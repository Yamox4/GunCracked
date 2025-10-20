package com.example.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 * UI Button component with click handling
 */
public class UIButton {
    
    private String id;
    private String text;
    private Node node;
    private Geometry background;
    private BitmapText textElement;
    private float x, y, width, height;
    
    private ColorRGBA normalColor = new ColorRGBA(0.3f, 0.3f, 0.3f, 0.9f);
    private ColorRGBA hoverColor = new ColorRGBA(0.4f, 0.4f, 0.4f, 0.9f);
    private ColorRGBA pressedColor = new ColorRGBA(0.2f, 0.2f, 0.2f, 0.9f);
    
    private Runnable clickAction;
    private boolean isHovered = false;
    private boolean isPressed = false;
    
    public UIButton(String id, String text, float x, float y, float width, float height, 
                   AssetManager assetManager, BitmapFont font) {
        this.id = id;
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        // Create node
        this.node = new Node("UIButton_" + id);
        
        // Create background
        Quad quad = new Quad(width, height);
        this.background = new Geometry("Button_" + id, quad);
        
        // Create material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", normalColor);
        background.setMaterial(mat);
        
        // Position background
        background.setLocalTranslation(x, y, 0);
        
        // Create text
        this.textElement = new BitmapText(font, false);
        textElement.setText(text);
        textElement.setSize(font.getCharSet().getRenderedSize());
        textElement.setColor(ColorRGBA.White);
        
        // Center text on button
        float textWidth = textElement.getLineWidth();
        float textHeight = textElement.getLineHeight();
        textElement.setLocalTranslation(
            x + (width - textWidth) / 2,
            y + (height + textHeight) / 2,
            1
        );
        
        // Add to node
        node.attachChild(background);
        node.attachChild(textElement);
    }
    
    /**
     * Set button click action
     */
    public void setClickAction(Runnable action) {
        this.clickAction = action;
    }
    
    /**
     * Handle button click
     */
    public void onClick() {
        if (clickAction != null) {
            clickAction.run();
        }
    }
    
    /**
     * Set button colors
     */
    public void setColors(ColorRGBA normal, ColorRGBA hover, ColorRGBA pressed) {
        this.normalColor = normal;
        this.hoverColor = hover;
        this.pressedColor = pressed;
        updateColor();
    }
    
    /**
     * Set hover state
     */
    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
        updateColor();
    }
    
    /**
     * Set pressed state
     */
    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
        updateColor();
    }
    
    /**
     * Update button color based on state
     */
    private void updateColor() {
        ColorRGBA color;
        if (isPressed) {
            color = pressedColor;
        } else if (isHovered) {
            color = hoverColor;
        } else {
            color = normalColor;
        }
        
        Material mat = background.getMaterial();
        mat.setColor("Color", color);
    }
    
    /**
     * Set button text
     */
    public void setText(String newText) {
        this.text = newText;
        textElement.setText(newText);
        
        // Re-center text
        float textWidth = textElement.getLineWidth();
        float textHeight = textElement.getLineHeight();
        textElement.setLocalTranslation(
            x + (width - textWidth) / 2,
            y + (height + textHeight) / 2,
            1
        );
    }
    
    /**
     * Set button position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        background.setLocalTranslation(x, y, 0);
        
        // Update text position
        float textWidth = textElement.getLineWidth();
        float textHeight = textElement.getLineHeight();
        textElement.setLocalTranslation(
            x + (width - textWidth) / 2,
            y + (height + textHeight) / 2,
            1
        );
    }
    
    /**
     * Check if point is inside button
     */
    public boolean contains(float pointX, float pointY) {
        return pointX >= x && pointX <= x + width && 
               pointY >= y && pointY <= y + height;
    }
    
    /**
     * Set button enabled/disabled
     */
    public void setEnabled(boolean enabled) {
        if (enabled) {
            textElement.setColor(ColorRGBA.White);
        } else {
            textElement.setColor(ColorRGBA.Gray);
        }
    }
    
    // Getters
    public String getId() { return id; }
    public String getText() { return text; }
    public Node getNode() { return node; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}