package com.example.demo;

import com.example.physics.PhysicsWorld;
import com.example.rendering.AdvancedMaterialFactory;
import com.example.rendering.GeometryFactory;
import com.example.rendering.ShadingManager;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * Simple demo integrating realistic shading with physics
 */
public class IntegratedShadingDemo extends SimpleApplication implements ActionListener {
    
    private ShadingManager shadingManager;
    private AdvancedMaterialFactory materialFactory;
    private GeometryFactory geometryFactory;
    private PhysicsWorld physicsWorld;
    
    private int objectCounter = 0;
    
    public static void main(String[] args) {
        IntegratedShadingDemo app = new IntegratedShadingDemo();
        
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Integrated Shading + Physics Demo");
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        settings.setSamples(4);
        
        app.setSettings(settings);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Initialize shading system
        shadingManager = new ShadingManager();
        stateManager.attach(shadingManager);
        
        // Initialize material factory
        materialFactory = new AdvancedMaterialFactory(assetManager);
        geometryFactory = new GeometryFactory(assetManager);
        
        // Initialize physics
        physicsWorld = new PhysicsWorld();
        stateManager.attach(physicsWorld);
        
        // Setup scene
        setupScene();
        setupInput();
        
        System.out.println("Integrated Shading Demo Started!");
        System.out.println("SPACE - Spawn realistic objects");
        System.out.println("1-5 - Change shading presets");
    }
    
    private void setupScene() {
        // Create realistic ground
        Box groundBox = new Box(15f, 0.5f, 15f);
        Geometry groundGeom = new Geometry("Ground", groundBox);
        
        // Apply realistic concrete material
        Material groundMat = materialFactory.createPBRMaterial(
            AdvancedMaterialFactory.MaterialType.CONCRETE, ColorRGBA.Gray);
        groundGeom.setMaterial(groundMat);
        
        groundGeom.setLocalTranslation(0, -0.5f, 0);
        rootNode.attachChild(groundGeom);
        
        // Add physics to ground
        physicsWorld.createGroundPlane("ground", groundGeom);
        
        // Setup camera
        flyCam.setMoveSpeed(10f);
        cam.setLocation(new Vector3f(0, 5, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
    
    private void setupInput() {
        inputManager.addMapping("SpawnObject", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Preset1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Preset2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("Preset3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("Preset4", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("Preset5", new KeyTrigger(KeyInput.KEY_5));
        
        inputManager.addListener(this, "SpawnObject", "Preset1", "Preset2", 
                                "Preset3", "Preset4", "Preset5");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) return;
        
        switch (name) {
            case "SpawnObject":
                spawnRealisticObject();
                break;
            case "Preset1":
                shadingManager.applyShadingPreset(ShadingManager.ShadingPreset.REALISTIC);
                break;
            case "Preset2":
                shadingManager.applyShadingPreset(ShadingManager.ShadingPreset.DARK_MOODY);
                break;
            case "Preset3":
                shadingManager.applyShadingPreset(ShadingManager.ShadingPreset.BRIGHT_OUTDOOR);
                break;
            case "Preset4":
                shadingManager.applyShadingPreset(ShadingManager.ShadingPreset.CYBERPUNK);
                break;
            case "Preset5":
                shadingManager.applyShadingPreset(ShadingManager.ShadingPreset.NATURAL);
                break;
        }
    }
    
    private void spawnRealisticObject() {
        Vector3f spawnPos = cam.getLocation().add(cam.getDirection().mult(5f));
        spawnPos.y = Math.max(spawnPos.y, 2f);
        
        String id = "object_" + (++objectCounter);
        
        // Randomly choose material type
        AdvancedMaterialFactory.MaterialType[] types = {
            AdvancedMaterialFactory.MaterialType.GOLD,
            AdvancedMaterialFactory.MaterialType.SILVER,
            AdvancedMaterialFactory.MaterialType.COPPER,
            AdvancedMaterialFactory.MaterialType.METAL_BRUSHED,
            AdvancedMaterialFactory.MaterialType.GLASS
        };
        
        AdvancedMaterialFactory.MaterialType materialType = 
            types[(int)(Math.random() * types.length)];
        
        // Create geometry
        Geometry sphere = geometryFactory.createSphere(id, 0.5f, ColorRGBA.White);
        
        // Apply realistic material
        Material mat = materialFactory.createPBRMaterial(materialType, ColorRGBA.White);
        sphere.setMaterial(mat);
        
        sphere.setLocalTranslation(spawnPos);
        rootNode.attachChild(sphere);
        
        // Add physics
        physicsWorld.createSphere(id, sphere, 0.5f, 1f, spawnPos);
        
        System.out.println("Spawned " + materialType + " sphere");
    }
}