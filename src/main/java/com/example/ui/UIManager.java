package com.example.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.QuadBackgroundComponent;

/**
 * UI Manager using Lemur for physics demo - handles all UI elements including
 * menu buttons
 */
public class UIManager extends BaseAppState implements ActionListener {

    // Button actions interface
    public interface UIActionListener {

        void onSpawnSphere();

        void onSpawnBox();

        void onSpawnCapsule();

        void onResetScene();

        void onToggleUI();

        void onToggleCameraMode();

        void onCloseApp();
    }

    private final UIActionListener actionListener;

    // UI Elements
    private Container mainPanel;
    private Container buttonPanel;
    private Label titleLabel;
    private Label instructionsLabel;
    private Label statsLabel;
    private Button sphereButton;
    private Button boxButton;
    private Button capsuleButton;
    private Button resetButton;
    private Button cameraModeButton;
    private Button closeButton;

    // UI State
    private boolean showUI = true;
    private boolean mouseLocked = true;

    public UIManager(UIActionListener listener) {
        this.actionListener = listener;
    }

    @Override
    protected void initialize(Application app) {
        // Initialize Lemur without styles
        GuiGlobals.initialize(app);

        createUIElements();
        setupInputMappings(app);

        System.out.println("Lemur UI Manager initialized successfully");
    }

    private void createUIElements() {
        // Main container for all UI elements
        mainPanel = new Container();

        // Title
        titleLabel = new Label("Physics Demo - 3 Shapes");
        titleLabel.setFontSize(24);
        titleLabel.setColor(ColorRGBA.White);

        // Instructions
        instructionsLabel = new Label("Controls:\n"
                + "1 - Spawn Red Sphere\n"
                + "2 - Spawn Blue Box\n"
                + "3 - Spawn Green Cylinder\n"
                + "R - Reset Scene\n"
                + "H - Toggle UI\n"
                + "ESC - Toggle Mouse Lock\n"
                + "WASD - Move Camera\n"
                + "Mouse - Look Around");
        instructionsLabel.setFontSize(12);
        instructionsLabel.setColor(ColorRGBA.Yellow);

        // Stats
        statsLabel = new Label("Objects: 0");
        statsLabel.setFontSize(16);
        statsLabel.setColor(ColorRGBA.Green);

        // Create button panel
        createButtonPanel();

        // Position UI elements
        positionUIElements();

        // Attach to GUI node
        ((SimpleApplication) getApplication()).getGuiNode().attachChild(mainPanel);
        ((SimpleApplication) getApplication()).getGuiNode().attachChild(buttonPanel);
    }

    private void createButtonPanel() {
        buttonPanel = new Container();

        // Create buttons with proper styling
        sphereButton = new Button("Spawn Sphere");
        sphereButton.setFontSize(14);
        sphereButton.setColor(ColorRGBA.White);
        sphereButton.setBackground(new QuadBackgroundComponent(ColorRGBA.Red.mult(0.8f)));
        sphereButton.addClickCommands(source -> {
            if (actionListener != null) {
                actionListener.onSpawnSphere();
            }
        });

        boxButton = new Button("Spawn Box");
        boxButton.setFontSize(14);
        boxButton.setColor(ColorRGBA.White);
        boxButton.setBackground(new QuadBackgroundComponent(ColorRGBA.Blue.mult(0.8f)));
        boxButton.addClickCommands(source -> {
            if (actionListener != null) {
                actionListener.onSpawnBox();
            }
        });

        capsuleButton = new Button("Spawn Cylinder");
        capsuleButton.setFontSize(14);
        capsuleButton.setColor(ColorRGBA.White);
        capsuleButton.setBackground(new QuadBackgroundComponent(ColorRGBA.Green.mult(0.8f)));
        capsuleButton.addClickCommands(source -> {
            if (actionListener != null) {
                actionListener.onSpawnCapsule();
            }
        });

        resetButton = new Button("Reset Scene");
        resetButton.setFontSize(14);
        resetButton.setColor(ColorRGBA.White);
        resetButton.setBackground(new QuadBackgroundComponent(ColorRGBA.Orange.mult(0.8f)));
        resetButton.addClickCommands(source -> {
            if (actionListener != null) {
                actionListener.onResetScene();
            }
        });

        cameraModeButton = new Button("FPS Mode");
        cameraModeButton.setFontSize(14);
        cameraModeButton.setColor(ColorRGBA.White);
        cameraModeButton.setBackground(new QuadBackgroundComponent(ColorRGBA.Magenta.mult(0.8f)));
        cameraModeButton.addClickCommands(source -> {
            if (actionListener != null) {
                actionListener.onToggleCameraMode();
            }
        });

        closeButton = new Button("✕ Close");
        closeButton.setFontSize(14);
        closeButton.setColor(ColorRGBA.White);
        closeButton.setBackground(new QuadBackgroundComponent(ColorRGBA.Red.mult(0.9f)));
        closeButton.addClickCommands(source -> {
            if (actionListener != null) {
                actionListener.onCloseApp();
            }
        });

        // Add buttons to panel
        buttonPanel.addChild(sphereButton);
        buttonPanel.addChild(boxButton);
        buttonPanel.addChild(capsuleButton);
        buttonPanel.addChild(resetButton);
        buttonPanel.addChild(cameraModeButton);
        buttonPanel.addChild(closeButton);
    }

    private void positionUIElements() {
        // Position main info panel (top-left)
        mainPanel.addChild(titleLabel);
        mainPanel.addChild(instructionsLabel);
        mainPanel.addChild(statsLabel);

        mainPanel.setLocalTranslation(10, getApplication().getCamera().getHeight() - 10, 0);

        // Position button panel (top-right)
        float buttonPanelX = getApplication().getCamera().getWidth() - 150;
        float buttonPanelY = getApplication().getCamera().getHeight() - 50;
        buttonPanel.setLocalTranslation(buttonPanelX, buttonPanelY, 0);
    }

    private void setupInputMappings(Application app) {
        // UI toggle
        app.getInputManager().addMapping("ToggleUI", new KeyTrigger(KeyInput.KEY_H));
        app.getInputManager().addListener(this, "ToggleUI");

        // Mouse lock toggle (ESC key) - handled in UI module
        app.getInputManager().addMapping("ToggleMouseLock", new KeyTrigger(KeyInput.KEY_ESCAPE));
        app.getInputManager().addListener(this, "ToggleMouseLock");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            return;
        }

        if ("ToggleUI".equals(name)) {
            toggleUI();
        } else if ("ToggleMouseLock".equals(name)) {
            toggleMouseLock();
        }
    }

    public void toggleUI() {
        showUI = !showUI;

        if (showUI) {
            ((SimpleApplication) getApplication()).getGuiNode().attachChild(mainPanel);
            ((SimpleApplication) getApplication()).getGuiNode().attachChild(buttonPanel);
        } else {
            mainPanel.removeFromParent();
            buttonPanel.removeFromParent();
        }

        if (actionListener != null) {
            actionListener.onToggleUI();
        }

        System.out.println("UI visibility toggled: " + showUI);
    }

    public void updateObjectCount(int count) {
        if (statsLabel != null) {
            statsLabel.setText("Objects: " + count);
        }
    }

    public void showMessage(String message) {
        // Just print to console instead of creating UI elements that cause threading issues
        System.out.println("UI Message: " + message);
    }

    private void toggleMouseLock() {
        SimpleApplication app = (SimpleApplication) getApplication();
        mouseLocked = !mouseLocked;

        if (mouseLocked) {
            app.getInputManager().setCursorVisible(false);
            app.getFlyByCamera().setEnabled(true);
            System.out.println("Mouse locked - camera control enabled");
        } else {
            app.getInputManager().setCursorVisible(true);
            app.getFlyByCamera().setEnabled(false);
            System.out.println("Mouse unlocked - UI interaction enabled");
        }
    }

    @Override
    protected void cleanup(Application app) {
        // Cleanup UI elements
        if (mainPanel != null) {
            mainPanel.removeFromParent();
        }
        if (buttonPanel != null) {
            buttonPanel.removeFromParent();
        }

        System.out.println("UI Manager cleaned up");
    }

    @Override
    protected void onEnable() {
        // UI is enabled by default
    }

    @Override
    protected void onDisable() {
        // Hide all UI elements when disabled
        if (showUI) {
            toggleUI();
        }
    }

    public boolean isUIVisible() {
        return showUI;
    }

    public void updateCameraModeButton(String modeText) {
        if (cameraModeButton != null) {
            cameraModeButton.setText(modeText);
        }
    }
}
