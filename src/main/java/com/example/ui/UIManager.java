package com.example.ui;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 * Modular UI Manager for creating and managing UI elements
 */
public class UIManager extends BaseAppState {
    
    private Node guiNode;
    private AssetManager assetManager;
    private BitmapFont guiFont;
    private AppSettings settings;
    
    private List<UIPanel> panels;
    private List<UIButton> buttons;
    private List<UILabel> labels;
    
    public UIManager() {
        this.panels = new ArrayList<>();
        this.buttons = new ArrayList<>();
        this.labels = new ArrayList<>();
    }
    
    @Override
    protected void initialize(Application app) {
        if (app instanceof SimpleApplication) {
            this.guiNode = ((SimpleApplication) app).getGuiNode();
        }
        this.assetManager = app.getAssetManager();
        this.settings = app.getContext().getSettings();
        
        // Load default font
        this.guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }
    
    @Override
    protected void cleanup(Application app) {
        clearAll();
    }
    
    @Override
    protected void onEnable() {
        // UI Manager enabled
    }
    
    @Override
    protected void onDisable() {
        // UI Manager disabled
    }
    
    /**
     * Create a UI panel
     */
    public UIPanel createPanel(String id, float x, float y, float width, float height) {
        UIPanel panel = new UIPanel(id, x, y, width, height, assetManager);
        panels.add(panel);
        guiNode.attachChild(panel.getNode());
        return panel;
    }
    
    /**
     * Create a UI button
     */
    public UIButton createButton(String id, String text, float x, float y, float width, float height) {
        UIButton button = new UIButton(id, text, x, y, width, height, assetManager, guiFont);
        buttons.add(button);
        guiNode.attachChild(button.getNode());
        return button;
    }
    
    /**
     * Create a UI label
     */
    public UILabel createLabel(String id, String text, float x, float y) {
        UILabel label = new UILabel(id, text, x, y, guiFont);
        labels.add(label);
        guiNode.attachChild(label.getNode());
        return label;
    }
    
    /**
     * Create a control panel with multiple buttons
     */
    public UIControlPanel createControlPanel(String id, float x, float y, float width) {
        UIControlPanel controlPanel = new UIControlPanel(id, x, y, width, assetManager, guiFont);
        guiNode.attachChild(controlPanel.getNode());
        return controlPanel;
    }
    
    /**
     * Create an info panel for displaying statistics
     */
    public UIInfoPanel createInfoPanel(String id, float x, float y, float width, float height) {
        UIInfoPanel infoPanel = new UIInfoPanel(id, x, y, width, height, assetManager, guiFont);
        guiNode.attachChild(infoPanel.getNode());
        return infoPanel;
    }
    
    /**
     * Remove a panel by ID
     */
    public void removePanel(String id) {
        panels.removeIf(panel -> {
            if (panel.getId().equals(id)) {
                panel.getNode().removeFromParent();
                return true;
            }
            return false;
        });
    }
    
    /**
     * Remove a button by ID
     */
    public void removeButton(String id) {
        buttons.removeIf(button -> {
            if (button.getId().equals(id)) {
                button.getNode().removeFromParent();
                return true;
            }
            return false;
        });
    }
    
    /**
     * Remove a label by ID
     */
    public void removeLabel(String id) {
        labels.removeIf(label -> {
            if (label.getId().equals(id)) {
                label.getNode().removeFromParent();
                return true;
            }
            return false;
        });
    }
    
    /**
     * Get panel by ID
     */
    public UIPanel getPanel(String id) {
        return panels.stream()
                .filter(panel -> panel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get button by ID
     */
    public UIButton getButton(String id) {
        return buttons.stream()
                .filter(button -> button.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get label by ID
     */
    public UILabel getLabel(String id) {
        return labels.stream()
                .filter(label -> label.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Clear all UI elements
     */
    public void clearAll() {
        panels.forEach(panel -> panel.getNode().removeFromParent());
        buttons.forEach(button -> button.getNode().removeFromParent());
        labels.forEach(label -> label.getNode().removeFromParent());
        
        panels.clear();
        buttons.clear();
        labels.clear();
    }
    
    /**
     * Get screen dimensions
     */
    public Vector3f getScreenSize() {
        return new Vector3f(settings.getWidth(), settings.getHeight(), 0);
    }
    
    /**
     * Create a heads-up display
     */
    public UIHUD createHUD() {
        UIHUD hud = new UIHUD(assetManager, guiFont, settings);
        guiNode.attachChild(hud.getNode());
        return hud;
    }
}