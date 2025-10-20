package com.example.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 * Heads-Up Display for showing game information
 */
public class UIHUD {
    
    private Node node;
    private BitmapText fpsText;
    private BitmapText instructionsText;
    private BitmapText statusText;
    private AppSettings settings;
    
    public UIHUD(AssetManager assetManager, BitmapFont font, AppSettings settings) {
        this.settings = settings;
        this.node = new Node("HUD");
        
        // FPS counter
        fpsText = new BitmapText(font, false);
        fpsText.setSize(font.getCharSet().getRenderedSize());
        fpsText.setText("FPS: 0");
        fpsText.setLocalTranslation(10, settings.getHeight() - 10, 0);
        fpsText.setColor(ColorRGBA.Yellow);
        node.attachChild(fpsText);
        
        // Instructions
        instructionsText = new BitmapText(font, false);
        instructionsText.setSize(font.getCharSet().getRenderedSize() * 0.8f);
        instructionsText.setText(getInstructionsText());
        instructionsText.setLocalTranslation(10, settings.getHeight() - 50, 0);
        instructionsText.setColor(ColorRGBA.White);
        node.attachChild(instructionsText);
        
        // Status text
        statusText = new BitmapText(font, false);
        statusText.setSize(font.getCharSet().getRenderedSize());
        statusText.setText("Ready");
        statusText.setLocalTranslation(settings.getWidth() - 200, settings.getHeight() - 10, 0);
        statusText.setColor(ColorRGBA.Green);
        node.attachChild(statusText);
    }
    
    /**
     * Update FPS display
     */
    public void updateFPS(float fps) {
        fpsText.setText(String.format("FPS: %.1f", fps));
    }
    
    /**
     * Update status text
     */
    public void updateStatus(String status) {
        statusText.setText(status);
    }
    
    /**
     * Update status with color
     */
    public void updateStatus(String status, ColorRGBA color) {
        statusText.setText(status);
        statusText.setColor(color);
    }
    
    /**
     * Show/hide instructions
     */
    public void setInstructionsVisible(boolean visible) {
        if (visible) {
            instructionsText.setCullHint(com.jme3.scene.Spatial.CullHint.Never);
        } else {
            instructionsText.setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        }
    }
    
    /**
     * Get instructions text
     */
    private String getInstructionsText() {
        return "Controls:\n" +
               "WASD - Move Camera\n" +
               "Mouse - Look Around\n" +
               "C - Toggle Camera Mode\n" +
               "Mouse Wheel - Zoom\n" +
               "Left Click - Spawn Object\n" +
               "Right Click - Apply Force\n" +
               "R - Reset Scene\n" +
               "H - Toggle Help";
    }
    
    /**
     * Update instructions text
     */
    public void setInstructions(String instructions) {
        instructionsText.setText(instructions);
    }
    
    public Node getNode() {
        return node;
    }
}