package com.example.demo;

import com.example.physics.PhysicsWorld;
import com.example.rendering.AdvancedMaterialFactory;
import com.example.rendering.GeometryFactory;
import com.example.rendering.ShadingManager;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * Demo showcasing realistic shading and PBR materials
 */
public class RealisticShadingDemo extends SimpleApplication implements ActionListener {
    
    private ShadingManager shadingManager;
    private AdvancedMaterialFactory materialFactory;
    private GeometryFactory geometryFactory;
    private PhysicsWorld physicsWorld;
    
    private int currentPreset = 0;
    private ShadingManager.ShadingPreset[] presets = ShadingManager.ShadingPreset.values();
    private int materialIndex = 0;
    private AdvancedMaterialFactory.MaterialType[] materials = AdvancedMaterialFactory.MaterialType.values();
    
    private float timeOfDay = 0.5f; // Start at noon
    private float cameraSpeed = 10f;
    
    public static void main(String[] args) {
        RealisticShadingDemo app = new RealisticShadingDemo();
        
        // Configure window settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Realistic Shading Demo - PBR Materials & Advanced Lighting");
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        settings.setSamples(4);
        settings.setFrameRate(60);
        settings.setFullscreen(false);
        
        app.setSettings(settings);
        app.setShowSettings(true);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Initialize systems
        shadingManager = new ShadingManager();
        stateManager.attach(shadingManager);
        
        materialFactory = new AdvancedMaterialFactory(assetManager);
        geometryFactory = new GeometryFactory(assetManager);
        
        physicsWorld = new PhysicsWorld();
        stateManager.attach(physicsWorld);
        
        // Setup camera
        setupCamera();
        
        // Create demo scene
        createDemoScene();
        
        // Setup input
        setupInput();
        
        printControls();
    }
    
    private void setupCamera() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(cameraSpeed);
        flyCam.setRotationSpeed(2f);
        
        // Position camera for good view
        cam.setLocation(new Vector3f(0, 8, 15));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
    
    private void createDemoScene() {
        // Create ground with realistic material
        Box groundBox = new Box(20f, 0.5f, 20f);
        Geometry groundGeom = new Geometry("Ground", groundBox);
        Material groundMat = materialFactory.createPBRMaterial(
            AdvancedMaterialFactory.MaterialType.CONCRETE, ColorRGBA.Gray);
        groundGeom.setMaterial(groundMat);
        groundGeom.setLocalTranslation(0, -0.5f, 0);
        rootNode.attachChild(groundGeom);
        
        // Create physics ground
        physicsWorld.createGroundPlane("ground", groundGeom);
        
        // Create material showcase objects
        createMaterialShowcase();
        
        // Create lighting showcase
        createLightingShowcase();
    }
    
    private void createMaterialShowcase() {
        float spacing = 3f;
        int cols = 5;
        int rows = 4;
        
        AdvancedMaterialFactory.MaterialType[] showcaseMaterials = {
            AdvancedMaterialFactory.MaterialType.GOLD,
            AdvancedMaterialFactory.MaterialType.SILVER,
            AdvancedMaterialFactory.MaterialType.COPPER,
            AdvancedMaterialFactory.MaterialType.CHROME,
            AdvancedMaterialFactory.MaterialType.METAL_BRUSHED,
            
            AdvancedMaterialFactory.MaterialType.PLASTIC_GLOSSY,
            AdvancedMaterialFactory.MaterialType.PLASTIC_MATTE,
            AdvancedMaterialFactory.MaterialType.RUBBER,
            AdvancedMaterialFactory.MaterialType.CERAMIC,
            AdvancedMaterialFactory.MaterialType.GLASS,
            
            AdvancedMaterialFactory.MaterialType.WOOD_OAK,
            AdvancedMaterialFactory.MaterialType.WOOD_PINE,
            AdvancedMaterialFactory.MaterialType.LEATHER,
            AdvancedMaterialFactory.MaterialType.FABRIC,
            AdvancedMaterialFactory.MaterialType.CARBON_FIBER,
            
            AdvancedMaterialFactory.MaterialType.STONE_MARBLE,
            AdvancedMaterialFactory.MaterialType.STONE_GRANITE,
            AdvancedMaterialFactory.MaterialType.BRICK,
            AdvancedMaterialFactory.MaterialType.CONCRETE,
            AdvancedMaterialFactory.MaterialType.METAL_POLISHED
        };
        
        for (int i = 0; i < Math.min(showcaseMaterials.length, cols * rows); i++) {
            int row = i / cols;
            int col = i % cols;
            
            float x = (col - cols/2f) * spacing;
            float z = (row - rows/2f) * spacing;
            float y = 1f;
            
            // Create sphere geometry
            String id = "showcase_" + i;
            Geometry sphere = geometryFactory.createSphere(id, 0.8f, GeometryFactory.Colors.WHITE);
            
            // Apply PBR material
            Material mat = materialFactory.createPBRMaterial(showcaseMaterials[i], ColorRGBA.White);
            sphere.setMaterial(mat);
            
            sphere.setLocalTranslation(x, y, z);
            rootNode.attachChild(sphere);
            
            // Add physics
            physicsWorld.createSphere(id, sphere, 0.8f, 1f, new Vector3f(x, y, z));
        }
    }
    
    private void createLightingShowcase() {
        // Create some emissive objects for lighting effects
        
        // Glowing orbs
        for (int i = 0; i < 3; i++) {
            float angle = i * 120f * (float) Math.PI / 180f;
            float radius = 8f;
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            float y = 3f;
            
            String id = "glow_orb_" + i;
            Geometry orb = geometryFactory.createSphere(id, 0.5f, GeometryFactory.Colors.WHITE);
            
            ColorRGBA[] glowColors = {ColorRGBA.Red, ColorRGBA.Green, ColorRGBA.Blue};
            Material glowMat = materialFactory.createEmissiveMaterial(glowColors[i], 2.0f);
            orb.setMaterial(glowMat);
            
            orb.setLocalTranslation(x, y, z);
            rootNode.attachChild(orb);
            
            // Add point lights
            shadingManager.addPointLight("light_" + i, new Vector3f(x, y, z), glowColors[i].mult(2f), 10f);
        }
        
        // Create some transparent objects
        for (int i = 0; i < 2; i++) {
            String id = "glass_" + i;
            Geometry glass = geometryFactory.createBox(id, new Vector3f(1f, 2f, 0.2f), GeometryFactory.Colors.CYAN);
            
            Material glassMat = materialFactory.createTransparentMaterial(ColorRGBA.Cyan, 0.3f);
            glass.setMaterial(glassMat);
            
            float x = (i == 0) ? -6f : 6f;
            glass.setLocalTranslation(x, 2f, 0);
            rootNode.attachChild(glass);
        }
    }
    
    private void setupInput() {
        // Shading presets
        inputManager.addMapping("NextPreset", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("PrevPreset", new KeyTrigger(KeyInput.KEY_O));
        
        // Time of day
        inputManager.addMapping("TimeForward", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("TimeBackward", new KeyTrigger(KeyInput.KEY_Y));
        
        // Material spawning
        inputManager.addMapping("SpawnMaterial", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("NextMaterial", new KeyTrigger(KeyInput.KEY_M));
        
        // Effects
        inputManager.addMapping("ToggleBloom", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("ToggleFXAA", new KeyTrigger(KeyInput.KEY_F));
        
        // Camera speed
        inputManager.addMapping("SpeedUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("SpeedDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        
        // Reset
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        
        inputManager.addListener(this, "NextPreset", "PrevPreset", "TimeForward", "TimeBackward",
                "SpawnMaterial", "NextMaterial", "ToggleBloom", "ToggleFXAA", 
                "SpeedUp", "SpeedDown", "Reset");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) return;
        
        switch (name) {
            case "NextPreset":
                currentPreset = (currentPreset + 1) % presets.length;
                shadingManager.applyShadingPreset(presets[currentPreset]);
                System.out.println("Shading preset: " + presets[currentPreset]);
                break;
                
            case "PrevPreset":
                currentPreset = (currentPreset - 1 + presets.length) % presets.length;
                shadingManager.applyShadingPreset(presets[currentPreset]);
                System.out.println("Shading preset: " + presets[currentPreset]);
                break;
                
            case "TimeForward":
                timeOfDay = (timeOfDay + 0.1f) % 1.0f;
                shadingManager.setTimeOfDay(timeOfDay);
                System.out.println("Time of day: " + String.format("%.1f", timeOfDay * 24) + ":00");
                break;
                
            case "TimeBackward":
                timeOfDay = (timeOfDay - 0.1f + 1.0f) % 1.0f;
                shadingManager.setTimeOfDay(timeOfDay);
                System.out.println("Time of day: " + String.format("%.1f", timeOfDay * 24) + ":00");
                break;
                
            case "SpawnMaterial":
                spawnMaterialSphere();
                break;
                
            case "NextMaterial":
                materialIndex = (materialIndex + 1) % materials.length;
                System.out.println("Selected material: " + materials[materialIndex]);
                break;
                
            case "ToggleBloom":
                // Toggle bloom effect
                System.out.println("Toggled bloom effect");
                break;
                
            case "ToggleFXAA":
                // Toggle FXAA
                System.out.println("Toggled FXAA");
                break;
                
            case "SpeedUp":
                changeCameraSpeed(1.5f);
                break;
                
            case "SpeedDown":
                changeCameraSpeed(0.75f);
                break;
                
            case "Reset":
                resetScene();
                break;
        }
    }
    
    private void spawnMaterialSphere() {
        Vector3f spawnPos = getSpawnPosition();
        String id = "spawned_" + System.currentTimeMillis();
        
        // Create sphere
        Geometry sphere = geometryFactory.createSphere(id, 0.6f, GeometryFactory.Colors.WHITE);
        
        // Apply current material
        Material mat = materialFactory.createPBRMaterial(materials[materialIndex], ColorRGBA.White);
        sphere.setMaterial(mat);
        
        sphere.setLocalTranslation(spawnPos);
        rootNode.attachChild(sphere);
        
        // Add physics
        physicsWorld.createSphere(id, sphere, 0.6f, 1f, spawnPos);
        
        System.out.println("Spawned " + materials[materialIndex] + " sphere at: " + spawnPos);
    }
    
    private Vector3f getSpawnPosition() {
        Vector3f cameraPos = cam.getLocation();
        Vector3f cameraDir = cam.getDirection();
        
        Vector3f spawnPos = cameraPos.add(cameraDir.mult(5f));
        spawnPos.y = Math.max(spawnPos.y, 2f);
        
        return spawnPos;
    }
    
    private void changeCameraSpeed(float multiplier) {
        cameraSpeed *= multiplier;
        cameraSpeed = Math.max(1f, Math.min(50f, cameraSpeed));
        flyCam.setMoveSpeed(cameraSpeed);
        System.out.println("Camera speed: " + cameraSpeed);
    }
    
    private void resetScene() {
        // Remove spawned objects (keep showcase)
        rootNode.getChildren().removeIf(spatial -> 
            spatial.getName() != null && spatial.getName().startsWith("spawned_"));
        
        System.out.println("Scene reset");
    }
    
    private void printControls() {
        System.out.println("\n=== REALISTIC SHADING DEMO CONTROLS ===");
        System.out.println("P/O - Next/Previous shading preset");
        System.out.println("T/Y - Time forward/backward (day/night cycle)");
        System.out.println("SPACE - Spawn sphere with current material");
        System.out.println("M - Next material type");
        System.out.println("B - Toggle bloom effect");
        System.out.println("F - Toggle FXAA anti-aliasing");
        System.out.println("R - Reset scene");
        System.out.println("WASD - Move camera");
        System.out.println("Mouse - Look around");
        System.out.println("Mouse wheel - Change camera speed");
        System.out.println("\nCurrent shading preset: " + presets[currentPreset]);
        System.out.println("Current material: " + materials[materialIndex]);
        System.out.println("==========================================\n");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        // Animate some effects
        float time = getTimer().getTimeInSeconds();
        
        // Animate glowing orbs
        for (int i = 0; i < 3; i++) {
            String lightId = "light_" + i;
            if (shadingManager.getPointLight(lightId) != null) {
                float intensity = 2.0f + (float) Math.sin(time * (2 + i)) * 0.5f;
                ColorRGBA[] colors = {ColorRGBA.Red, ColorRGBA.Green, ColorRGBA.Blue};
                shadingManager.getPointLight(lightId).setColor(colors[i].mult(intensity));
            }
        }
    }
}