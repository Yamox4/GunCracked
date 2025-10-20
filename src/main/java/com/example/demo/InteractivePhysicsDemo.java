package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.camera.CameraController;
import com.example.physics.CollisionListener;
import com.example.physics.PhysicsMaterial;
import com.example.physics.PhysicsWorld;
import com.example.physics.RaycastHelper;
import com.example.rendering.GeometryFactory;
import com.example.ui.UIControlPanel;
import com.example.ui.UIHUD;
import com.example.ui.UIInfoPanel;
import com.example.ui.UIManager;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 * Interactive Physics Demo with full UI and object spawning
 */
public class InteractivePhysicsDemo extends SimpleApplication implements ActionListener {
    
    // Core systems
    private PhysicsWorld physicsWorld;
    private CameraController cameraController;
    private GeometryFactory geometryFactory;
    private UIManager uiManager;
    private RaycastHelper raycastHelper;
    
    // UI Components
    private UIControlPanel spawnPanel;
    private UIControlPanel materialPanel;
    private UIControlPanel settingsPanel;
    private UIInfoPanel infoPanel;
    private UIHUD hud;
    
    // Demo state
    private ObjectType selectedObjectType = ObjectType.SPHERE;
    private PhysicsMaterial selectedMaterial = PhysicsMaterial.RUBBER;
    private List<String> spawnedObjects = new ArrayList<>();
    private int objectCounter = 0;
    private boolean showUI = true;
    private boolean showHelp = true;
    
    // Input mappings
    private static final String MAPPING_SPAWN = "Spawn";
    private static final String MAPPING_FORCE = "Force";
    private static final String MAPPING_RESET = "Reset";
    private static final String MAPPING_TOGGLE_UI = "ToggleUI";
    private static final String MAPPING_TOGGLE_HELP = "ToggleHelp";
    
    public enum ObjectType {
        SPHERE, BOX, CAPSULE, CYLINDER, CONE
    }
    
    public static void main(String[] args) {
        InteractivePhysicsDemo app = new InteractivePhysicsDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Initialize core systems
        initializePhysics();
        initializeCamera();
        initializeLighting();
        initializeGeometryFactory();
        initializeUI();
        initializeInput();
        
        // Create initial scene
        createInitialScene();
        
        System.out.println("Interactive Physics Demo Started!");
        System.out.println("Use the UI panels to spawn objects and test physics!");
    }
    
    private void initializePhysics() {
        physicsWorld = new PhysicsWorld();
        stateManager.attach(physicsWorld);
        
        raycastHelper = new RaycastHelper(physicsWorld.getPhysicsSpace());
        
        // Setup collision handlers
        setupCollisionHandlers();
    }
    
    private void initializeCamera() {
        cameraController = new CameraController();
        stateManager.attach(cameraController);
        
        // Position camera for good view
        cam.setLocation(new Vector3f(0, 10, 20));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
    
    private void initializeLighting() {
        // Add ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(ambient);
        
        // Add directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.8f));
        rootNode.addLight(sun);
    }
    
    private void initializeGeometryFactory() {
        geometryFactory = new GeometryFactory(assetManager);
    }
    
    private void initializeUI() {
        uiManager = new UIManager();
        stateManager.attach(uiManager);
        
        // Create HUD
        hud = uiManager.createHUD();
        
        // Create control panels
        createSpawnPanel();
        createMaterialPanel();
        createSettingsPanel();
        createInfoPanel();
    }
    
    private void createSpawnPanel() {
        spawnPanel = uiManager.createControlPanel("spawnPanel", 10, 10, 200);
        
        spawnPanel.addLabel("spawnTitle", "Spawn Objects");
        spawnPanel.addSeparator();
        
        // Object type buttons
        spawnPanel.addButton("sphereBtn", "Sphere", () -> {
            selectedObjectType = ObjectType.SPHERE;
            updateSpawnPanelSelection();
        });
        
        spawnPanel.addButton("boxBtn", "Box", () -> {
            selectedObjectType = ObjectType.BOX;
            updateSpawnPanelSelection();
        });
        
        spawnPanel.addButton("capsuleBtn", "Capsule", () -> {
            selectedObjectType = ObjectType.CAPSULE;
            updateSpawnPanelSelection();
        });
        
        spawnPanel.addButton("cylinderBtn", "Cylinder", () -> {
            selectedObjectType = ObjectType.CYLINDER;
            updateSpawnPanelSelection();
        });
        
        spawnPanel.addButton("coneBtn", "Cone", () -> {
            selectedObjectType = ObjectType.CONE;
            updateSpawnPanelSelection();
        });
        
        spawnPanel.addSeparator();
        spawnPanel.addButton("spawnBtn", "Spawn at Center", this::spawnObjectAtCenter);
        spawnPanel.addButton("clearBtn", "Clear All", this::clearAllObjects);
        
        updateSpawnPanelSelection();
    }
    
    private void createMaterialPanel() {
        materialPanel = uiManager.createControlPanel("materialPanel", 220, 10, 180);
        
        materialPanel.addLabel("materialTitle", "Materials");
        materialPanel.addSeparator();
        
        // Material buttons
        materialPanel.addButton("rubberBtn", "Rubber", () -> {
            selectedMaterial = PhysicsMaterial.RUBBER;
            updateMaterialPanelSelection();
        });
        
        materialPanel.addButton("metalBtn", "Metal", () -> {
            selectedMaterial = PhysicsMaterial.METAL;
            updateMaterialPanelSelection();
        });
        
        materialPanel.addButton("woodBtn", "Wood", () -> {
            selectedMaterial = PhysicsMaterial.WOOD;
            updateMaterialPanelSelection();
        });
        
        materialPanel.addButton("iceBtn", "Ice", () -> {
            selectedMaterial = PhysicsMaterial.ICE;
            updateMaterialPanelSelection();
        });
        
        materialPanel.addButton("concreteBtn", "Concrete", () -> {
            selectedMaterial = PhysicsMaterial.CONCRETE;
            updateMaterialPanelSelection();
        });
        
        materialPanel.addButton("glassBtn", "Glass", () -> {
            selectedMaterial = PhysicsMaterial.GLASS;
            updateMaterialPanelSelection();
        });
        
        updateMaterialPanelSelection();
    }
    
    private void createSettingsPanel() {
        settingsPanel = uiManager.createControlPanel("settingsPanel", 410, 10, 180);
        
        settingsPanel.addLabel("settingsTitle", "Settings");
        settingsPanel.addSeparator();
        
        settingsPanel.addButton("gravityNormalBtn", "Normal Gravity", () -> {
            physicsWorld.setGravity(new Vector3f(0, -9.81f, 0));
            hud.updateStatus("Normal Gravity", ColorRGBA.Green);
        });
        
        settingsPanel.addButton("gravityLowBtn", "Low Gravity", () -> {
            physicsWorld.setGravity(new Vector3f(0, -2f, 0));
            hud.updateStatus("Low Gravity", ColorRGBA.Blue);
        });
        
        settingsPanel.addButton("gravityZeroBtn", "Zero Gravity", () -> {
            physicsWorld.setGravity(Vector3f.ZERO);
            hud.updateStatus("Zero Gravity", ColorRGBA.Yellow);
        });
        
        settingsPanel.addSeparator();
        
        settingsPanel.addButton("cameraFreeBtn", "Free Camera", () -> {
            cameraController.setMode(CameraController.CameraMode.FREE_FLY);
            hud.updateStatus("Free Camera", ColorRGBA.White);
        });
        
        settingsPanel.addButton("cameraOrbitBtn", "Orbit Camera", () -> {
            cameraController.setMode(CameraController.CameraMode.ORBIT);
            hud.updateStatus("Orbit Camera", ColorRGBA.White);
        });
    }
    
    private void createInfoPanel() {
        infoPanel = uiManager.createInfoPanel("infoPanel", 
            settings.getWidth() - 250, 10, 240, 200);
    }
    
    private void initializeInput() {
        // Mouse input
        inputManager.addMapping(MAPPING_SPAWN, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(MAPPING_FORCE, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        // Keyboard input
        inputManager.addMapping(MAPPING_RESET, new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping(MAPPING_TOGGLE_UI, new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping(MAPPING_TOGGLE_HELP, new KeyTrigger(KeyInput.KEY_H));
        
        // Add listeners
        inputManager.addListener(this, MAPPING_SPAWN, MAPPING_FORCE, MAPPING_RESET, 
                                MAPPING_TOGGLE_UI, MAPPING_TOGGLE_HELP);
    }
    
    private void createInitialScene() {
        // Create ground
        Geometry groundGeom = geometryFactory.createGroundPlane("Ground", 50f, GeometryFactory.Colors.GRAY);
        PhysicsRigidBody groundBody = physicsWorld.createGroundPlane("ground", groundGeom);
        PhysicsMaterial.CONCRETE.applyTo(groundBody);
        
        // Create some initial objects for demonstration
        spawnObjectAtPosition(ObjectType.SPHERE, new Vector3f(-3, 5, 0), PhysicsMaterial.RUBBER);
        spawnObjectAtPosition(ObjectType.BOX, new Vector3f(0, 5, 0), PhysicsMaterial.WOOD);
        spawnObjectAtPosition(ObjectType.CAPSULE, new Vector3f(3, 5, 0), PhysicsMaterial.METAL);
    }
    
    private void setupCollisionHandlers() {
        CollisionListener collisionListener = physicsWorld.getCollisionListener();
        
        // Add collision sound/effect handlers
        collisionListener.addCollisionHandler("any-any", (bodyA, bodyB) -> {
            // Update collision counter in info panel
            updateInfoPanel();
        });
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) return;
        
        switch (name) {
            case MAPPING_SPAWN:
                spawnObjectAtCursor();
                break;
            case MAPPING_FORCE:
                applyForceAtCursor();
                break;
            case MAPPING_RESET:
                resetScene();
                break;
            case MAPPING_TOGGLE_UI:
                toggleUI();
                break;
            case MAPPING_TOGGLE_HELP:
                toggleHelp();
                break;
        }
    }
    
    private void spawnObjectAtCursor() {
        Vector2f cursorPos = inputManager.getCursorPosition();
        Vector3f worldPos = getWorldPositionFromCursor(cursorPos);
        
        if (worldPos != null) {
            spawnObjectAtPosition(selectedObjectType, worldPos, selectedMaterial);
            hud.updateStatus("Spawned " + selectedObjectType.name(), ColorRGBA.Green);
        }
    }
    
    private void applyForceAtCursor() {
        Vector2f cursorPos = inputManager.getCursorPosition();
        Vector3f worldPos = getWorldPositionFromCursor(cursorPos);
        
        if (worldPos != null) {
            // Find nearby objects and apply force
            for (String objectId : spawnedObjects) {
                PhysicsRigidBody body = physicsWorld.getPhysicsObject(objectId);
                if (body != null) {
                    Vector3f objectPos = body.getPhysicsLocation();
                    float distance = objectPos.distance(worldPos);
                    
                    if (distance < 3f) { // Within 3 units
                        Vector3f force = worldPos.subtract(objectPos).normalize().mult(50f);
                        force.y = Math.abs(force.y) + 10f; // Add upward force
                        body.applyCentralForce(force);
                    }
                }
            }
            hud.updateStatus("Applied Force", ColorRGBA.Orange);
        }
    }
    
    private Vector3f getWorldPositionFromCursor(Vector2f cursorPos) {
        // Convert cursor position to world coordinates using raycasting
        Vector3f origin = cam.getWorldCoordinates(cursorPos, 0f);
        Vector3f direction = cam.getWorldCoordinates(cursorPos, 1f).subtract(origin);
        
        RaycastHelper.RaycastResult result = raycastHelper.raycast(origin, direction, 100f);
        
        if (result.isHit()) {
            return result.getHitPoint().add(0, 1f, 0); // Spawn slightly above hit point
        } else {
            // Spawn at a default distance from camera
            return origin.add(direction.normalize().mult(10f));
        }
    }
    
    private void spawnObjectAtCenter() {
        Vector3f position = new Vector3f(0, 8f, 0);
        spawnObjectAtPosition(selectedObjectType, position, selectedMaterial);
        hud.updateStatus("Spawned " + selectedObjectType.name() + " at center", ColorRGBA.Green);
    }
    
    private void spawnObjectAtPosition(ObjectType type, Vector3f position, PhysicsMaterial material) {
        String objectId = "object_" + (++objectCounter);
        Geometry visual = null;
        PhysicsRigidBody body = null;
        
        // Create visual and physics based on type
        switch (type) {
            case SPHERE:
                visual = geometryFactory.createSphere(objectId, 0.5f, getColorForMaterial(material));
                body = physicsWorld.createSphere(objectId, visual, 0.5f, 1f, position);
                break;
            case BOX:
                Vector3f boxSize = new Vector3f(0.5f, 0.5f, 0.5f);
                visual = geometryFactory.createBox(objectId, boxSize, getColorForMaterial(material));
                body = physicsWorld.createBox(objectId, visual, boxSize, 2f, position);
                break;
            case CAPSULE:
                visual = geometryFactory.createCapsule(objectId, 0.3f, 1.2f, getColorForMaterial(material));
                body = physicsWorld.createCapsule(objectId, visual, 0.3f, 1.2f, 1.5f, position);
                break;
            case CYLINDER:
                Vector3f cylSize = new Vector3f(0.4f, 0.6f, 0.4f);
                visual = geometryFactory.createCylinder(objectId, 0.4f, 1.2f, getColorForMaterial(material));
                body = physicsWorld.createCylinder(objectId, visual, cylSize, 1.8f, position);
                break;
            case CONE:
                visual = geometryFactory.createCone(objectId, 0.5f, 1.2f, getColorForMaterial(material));
                body = physicsWorld.createCone(objectId, visual, 0.5f, 1.2f, 1.2f, position);
                break;
        }
        
        if (body != null) {
            material.applyTo(body);
            spawnedObjects.add(objectId);
        }
    }
    
    private ColorRGBA getColorForMaterial(PhysicsMaterial material) {
        if (material == PhysicsMaterial.RUBBER) return GeometryFactory.Colors.RED;
        if (material == PhysicsMaterial.METAL) return GeometryFactory.Colors.GRAY;
        if (material == PhysicsMaterial.WOOD) return GeometryFactory.Colors.BROWN;
        if (material == PhysicsMaterial.ICE) return GeometryFactory.Colors.CYAN;
        if (material == PhysicsMaterial.CONCRETE) return GeometryFactory.Colors.DARK_GRAY;
        if (material == PhysicsMaterial.GLASS) return GeometryFactory.Colors.LIGHT_GRAY;
        return GeometryFactory.Colors.WHITE;
    }
    
    private void clearAllObjects() {
        for (String objectId : spawnedObjects) {
            physicsWorld.removePhysicsObject(objectId);
        }
        spawnedObjects.clear();
        objectCounter = 0;
        hud.updateStatus("Cleared all objects", ColorRGBA.Yellow);
    }
    
    private void resetScene() {
        clearAllObjects();
        createInitialScene();
        physicsWorld.setGravity(new Vector3f(0, -9.81f, 0));
        hud.updateStatus("Scene reset", ColorRGBA.Green);
    }
    
    private void toggleUI() {
        showUI = !showUI;
        // Toggle visibility of UI panels
        // Implementation depends on UI framework capabilities
        hud.updateStatus("UI " + (showUI ? "shown" : "hidden"), ColorRGBA.White);
    }
    
    private void toggleHelp() {
        showHelp = !showHelp;
        hud.setInstructionsVisible(showHelp);
        hud.updateStatus("Help " + (showHelp ? "shown" : "hidden"), ColorRGBA.White);
    }
    
    private void updateSpawnPanelSelection() {
        // Update button colors to show selection
        spawnPanel.getButton("sphereBtn").setColors(
            selectedObjectType == ObjectType.SPHERE ? ColorRGBA.Green : new ColorRGBA(0.3f, 0.3f, 0.3f, 0.9f),
            ColorRGBA.Gray, ColorRGBA.DarkGray);
        // Similar for other buttons...
    }
    
    private void updateMaterialPanelSelection() {
        // Update button colors to show selection
        materialPanel.getButton("rubberBtn").setColors(
            selectedMaterial == PhysicsMaterial.RUBBER ? ColorRGBA.Green : new ColorRGBA(0.3f, 0.3f, 0.3f, 0.9f),
            ColorRGBA.Gray, ColorRGBA.DarkGray);
        // Similar for other buttons...
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        // Update HUD
        hud.updateFPS(timer.getFrameRate());
        
        // Update info panel
        updateInfoPanel();
    }
    
    private void updateInfoPanel() {
        Map<String, String> info = new HashMap<>();
        info.put("Objects", String.valueOf(spawnedObjects.size()));
        info.put("Selected Type", selectedObjectType.name());
        info.put("Selected Material", getMaterialName(selectedMaterial));
        info.put("Camera Mode", cameraController.getCurrentMode().name());
        
        Vector3f gravity = physicsWorld.getPhysicsSpace().getGravity(null);
        info.put("Gravity", String.format("%.1f", gravity.y));
        
        infoPanel.updateInfo(info);
    }
    
    private String getMaterialName(PhysicsMaterial material) {
        if (material == PhysicsMaterial.RUBBER) return "Rubber";
        if (material == PhysicsMaterial.METAL) return "Metal";
        if (material == PhysicsMaterial.WOOD) return "Wood";
        if (material == PhysicsMaterial.ICE) return "Ice";
        if (material == PhysicsMaterial.CONCRETE) return "Concrete";
        if (material == PhysicsMaterial.GLASS) return "Glass";
        return "Unknown";
    }
}