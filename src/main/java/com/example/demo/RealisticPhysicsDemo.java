package com.example.demo;

import com.example.rendering.GeometryFactory;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;

/**
 * Physics demo with realistic lighting, shadows, and PBR materials
 */
public class RealisticPhysicsDemo extends SimpleApplication implements ActionListener {
    
    private BulletAppState bulletAppState;
    private GeometryFactory geometryFactory;
    private int objectCounter = 0;
    private float cameraSpeed = 10f;
    
    // Lighting
    private DirectionalLight sunLight;
    private AmbientLight ambientLight;
    private PointLight[] pointLights = new PointLight[3];
    
    // Post-processing
    private FilterPostProcessor fpp;
    private BloomFilter bloomFilter;
    private DirectionalLightShadowFilter shadowFilter;
    
    public static void main(String[] args) {
        RealisticPhysicsDemo app = new RealisticPhysicsDemo();
        
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Realistic Physics Demo - PBR + Shadows + Lighting");
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        settings.setSamples(4);
        settings.setFrameRate(60);
        
        app.setSettings(settings);
        app.setShowSettings(true);
        app.start();
    }    

    @Override
    public void simpleInitApp() {
        // Initialize physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // Initialize geometry factory
        geometryFactory = new GeometryFactory(assetManager);
        
        // Setup realistic lighting
        setupRealisticLighting();
        
        // Setup post-processing effects
        setupPostProcessing();
        
        // Setup camera
        setupCamera();
        
        // Create scene
        createRealisticGround();
        createLightingSamples();
        
        // Setup input
        setupInput();
        
        printControls();
    }
    
    private void setupRealisticLighting() {
        // Soft ambient light (simulates sky light)
        ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.4f, 0.5f, 0.6f, 1.0f).mult(0.3f));
        rootNode.addLight(ambientLight);
        
        // Strong directional sun light
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        sunLight.setColor(new ColorRGBA(1.0f, 0.95f, 0.8f, 1.0f).mult(1.2f));
        rootNode.addLight(sunLight);
        
        // Add some colorful point lights for atmosphere
        pointLights[0] = new PointLight();
        pointLights[0].setPosition(new Vector3f(8, 4, 8));
        pointLights[0].setColor(ColorRGBA.Red.mult(2.0f));
        pointLights[0].setRadius(15f);
        rootNode.addLight(pointLights[0]);
        
        pointLights[1] = new PointLight();
        pointLights[1].setPosition(new Vector3f(-8, 4, -8));
        pointLights[1].setColor(ColorRGBA.Blue.mult(1.5f));
        pointLights[1].setRadius(12f);
        rootNode.addLight(pointLights[1]);
        
        pointLights[2] = new PointLight();
        pointLights[2].setPosition(new Vector3f(0, 6, -10));
        pointLights[2].setColor(ColorRGBA.Green.mult(1.8f));
        pointLights[2].setRadius(10f);
        rootNode.addLight(pointLights[2]);
        
        System.out.println("Realistic lighting setup complete:");
        System.out.println("- Ambient light (sky simulation)");
        System.out.println("- Directional sun light with warm color");
        System.out.println("- 3 colored point lights for atmosphere");
    }
    
    private void setupPostProcessing() {
        fpp = new FilterPostProcessor(assetManager);
        
        // Anti-aliasing
        FXAAFilter fxaaFilter = new FXAAFilter();
        fpp.addFilter(fxaaFilter);
        
        // Bloom for glowing effects
        bloomFilter = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloomFilter.setBloomIntensity(1.5f);
        bloomFilter.setExposurePower(55f);
        bloomFilter.setExposureCutOff(0.0f);
        fpp.addFilter(bloomFilter);
        
        // Shadows
        shadowFilter = new DirectionalLightShadowFilter(assetManager, 2048, 3);
        shadowFilter.setLight(sunLight);
        shadowFilter.setShadowIntensity(0.4f);
        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        fpp.addFilter(shadowFilter);
        
        viewPort.addProcessor(fpp);
        
        System.out.println("Post-processing effects enabled:");
        System.out.println("- FXAA anti-aliasing");
        System.out.println("- Bloom filter for glowing materials");
        System.out.println("- High-quality shadows (2048x2048)");
    }   
 
    private void setupCamera() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(cameraSpeed);
        flyCam.setRotationSpeed(2f);
        
        cam.setLocation(new Vector3f(0, 8, 15));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
    
    private void createRealisticGround() {
        // Create large ground
        Box groundBox = new Box(25f, 1f, 25f);
        Geometry groundGeom = new Geometry("Ground", groundBox);
        
        // Create realistic concrete material using PBR
        Material groundMat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        groundMat.setColor("BaseColor", new ColorRGBA(0.6f, 0.6f, 0.6f, 1.0f));
        groundMat.setFloat("Metallic", 0.0f);
        groundMat.setFloat("Roughness", 0.9f);
        groundGeom.setMaterial(groundMat);
        
        groundGeom.setLocalTranslation(0, -1f, 0);
        
        // Add physics
        RigidBodyControl groundPhysics = new RigidBodyControl(0f);
        groundGeom.addControl(groundPhysics);
        bulletAppState.getPhysicsSpace().add(groundPhysics);
        
        rootNode.attachChild(groundGeom);
        
        System.out.println("Realistic ground created with PBR concrete material");
    }
    
    private void createLightingSamples() {
        // Create sample objects with different PBR materials
        createMetalSphere("Gold", new Vector3f(-6, 2, 0), new ColorRGBA(1.0f, 0.8f, 0.0f, 1.0f), 1.0f, 0.1f);
        createMetalSphere("Silver", new Vector3f(-3, 2, 0), new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f), 1.0f, 0.05f);
        createMetalSphere("Copper", new Vector3f(0, 2, 0), new ColorRGBA(0.9f, 0.4f, 0.2f, 1.0f), 1.0f, 0.2f);
        createPlasticSphere("Red Plastic", new Vector3f(3, 2, 0), ColorRGBA.Red, 0.0f, 0.3f);
        createPlasticSphere("Blue Plastic", new Vector3f(6, 2, 0), ColorRGBA.Blue, 0.0f, 0.7f);
        
        // Create glowing orbs near the point lights
        createGlowingSphere("Red Glow", new Vector3f(8, 4, 8), ColorRGBA.Red, 2.0f);
        createGlowingSphere("Blue Glow", new Vector3f(-8, 4, -8), ColorRGBA.Blue, 1.5f);
        createGlowingSphere("Green Glow", new Vector3f(0, 6, -10), ColorRGBA.Green, 1.8f);
        
        System.out.println("Material samples created:");
        System.out.println("- Metallic materials: Gold, Silver, Copper");
        System.out.println("- Plastic materials: Red (glossy), Blue (matte)");
        System.out.println("- Emissive glowing orbs");
    }
    
    private void createMetalSphere(String name, Vector3f position, ColorRGBA color, float metallic, float roughness) {
        Geometry sphere = geometryFactory.createSphere(name, 0.8f, ColorRGBA.White);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", color);
        mat.setFloat("Metallic", metallic);
        mat.setFloat("Roughness", roughness);
        sphere.setMaterial(mat);
        
        sphere.setLocalTranslation(position);
        rootNode.attachChild(sphere);
        
        // Add physics
        SphereCollisionShape sphereShape = new SphereCollisionShape(0.8f);
        RigidBodyControl spherePhysics = new RigidBodyControl(sphereShape, 2f);
        sphere.addControl(spherePhysics);
        bulletAppState.getPhysicsSpace().add(spherePhysics);
    }
    
    private void createPlasticSphere(String name, Vector3f position, ColorRGBA color, float metallic, float roughness) {
        Geometry sphere = geometryFactory.createSphere(name, 0.8f, ColorRGBA.White);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", color);
        mat.setFloat("Metallic", metallic);
        mat.setFloat("Roughness", roughness);
        sphere.setMaterial(mat);
        
        sphere.setLocalTranslation(position);
        rootNode.attachChild(sphere);
        
        // Add physics
        SphereCollisionShape sphereShape = new SphereCollisionShape(0.8f);
        RigidBodyControl spherePhysics = new RigidBodyControl(sphereShape, 1f);
        sphere.addControl(spherePhysics);
        bulletAppState.getPhysicsSpace().add(spherePhysics);
    }
    
    private void createGlowingSphere(String name, Vector3f position, ColorRGBA color, float intensity) {
        Geometry sphere = geometryFactory.createSphere(name, 0.5f, ColorRGBA.White);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", color);
        mat.setColor("Emissive", color.mult(intensity));
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.8f);
        sphere.setMaterial(mat);
        
        sphere.setLocalTranslation(position);
        rootNode.attachChild(sphere);
        
        // No physics for floating glowing orbs
    }    
    
private void setupInput() {
        inputManager.addMapping("SpawnGold", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("SpawnSilver", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("SpawnCopper", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("SpawnPlastic", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("SpawnGlass", new KeyTrigger(KeyInput.KEY_5));
        inputManager.addMapping("SpawnRandom", new KeyTrigger(KeyInput.KEY_SPACE));
        
        inputManager.addMapping("ToggleBloom", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("ToggleShadows", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("TimeForward", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("TimeBackward", new KeyTrigger(KeyInput.KEY_Y));
        
        inputManager.addMapping("SpeedUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("SpeedDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        
        inputManager.addListener(this, "SpawnGold", "SpawnSilver", "SpawnCopper", 
                "SpawnPlastic", "SpawnGlass", "SpawnRandom", "ToggleBloom", 
                "ToggleShadows", "TimeForward", "TimeBackward", "SpeedUp", "SpeedDown", "Reset");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) return;
        
        Vector3f spawnPos = getSpawnPosition();
        
        switch (name) {
            case "SpawnGold":
                spawnMaterialSphere("Gold", spawnPos, new ColorRGBA(1.0f, 0.8f, 0.0f, 1.0f), 1.0f, 0.1f);
                break;
            case "SpawnSilver":
                spawnMaterialSphere("Silver", spawnPos, new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f), 1.0f, 0.05f);
                break;
            case "SpawnCopper":
                spawnMaterialSphere("Copper", spawnPos, new ColorRGBA(0.9f, 0.4f, 0.2f, 1.0f), 1.0f, 0.2f);
                break;
            case "SpawnPlastic":
                spawnMaterialSphere("Plastic", spawnPos, ColorRGBA.randomColor(), 0.0f, 0.5f);
                break;
            case "SpawnGlass":
                spawnGlassSphere("Glass", spawnPos, new ColorRGBA(0.8f, 0.9f, 1.0f, 0.3f));
                break;
            case "SpawnRandom":
                spawnRandomMaterial(spawnPos);
                break;
            case "ToggleBloom":
                bloomFilter.setEnabled(!bloomFilter.isEnabled());
                System.out.println("Bloom: " + (bloomFilter.isEnabled() ? "ON" : "OFF"));
                break;
            case "ToggleShadows":
                shadowFilter.setEnabled(!shadowFilter.isEnabled());
                System.out.println("Shadows: " + (shadowFilter.isEnabled() ? "ON" : "OFF"));
                break;
            case "TimeForward":
                adjustTimeOfDay(0.1f);
                break;
            case "TimeBackward":
                adjustTimeOfDay(-0.1f);
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
    
    private void spawnMaterialSphere(String materialName, Vector3f position, ColorRGBA color, float metallic, float roughness) {
        String id = materialName + "_" + (++objectCounter);
        Geometry sphere = geometryFactory.createSphere(id, 0.6f, ColorRGBA.White);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", color);
        mat.setFloat("Metallic", metallic);
        mat.setFloat("Roughness", roughness);
        sphere.setMaterial(mat);
        
        sphere.setLocalTranslation(position);
        rootNode.attachChild(sphere);
        
        // Add physics
        SphereCollisionShape sphereShape = new SphereCollisionShape(0.6f);
        RigidBodyControl spherePhysics = new RigidBodyControl(sphereShape, 1f);
        sphere.addControl(spherePhysics);
        bulletAppState.getPhysicsSpace().add(spherePhysics);
        
        System.out.println("Spawned " + materialName + " sphere at: " + position);
    }
    
    private void spawnGlassSphere(String materialName, Vector3f position, ColorRGBA color) {
        String id = materialName + "_" + (++objectCounter);
        Geometry sphere = geometryFactory.createSphere(id, 0.6f, ColorRGBA.White);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", color);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.0f);
        mat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
        mat.setTransparent(true);
        sphere.setMaterial(mat);
        
        sphere.setLocalTranslation(position);
        rootNode.attachChild(sphere);
        
        // Add physics
        SphereCollisionShape sphereShape = new SphereCollisionShape(0.6f);
        RigidBodyControl spherePhysics = new RigidBodyControl(sphereShape, 0.5f);
        sphere.addControl(spherePhysics);
        bulletAppState.getPhysicsSpace().add(spherePhysics);
        
        System.out.println("Spawned transparent " + materialName + " sphere at: " + position);
    } 
   
    private void spawnRandomMaterial(Vector3f position) {
        String[] materials = {"Gold", "Silver", "Copper", "Plastic", "Glass"};
        String material = materials[(int)(Math.random() * materials.length)];
        
        switch (material) {
            case "Gold":
                spawnMaterialSphere("Gold", position, new ColorRGBA(1.0f, 0.8f, 0.0f, 1.0f), 1.0f, 0.1f);
                break;
            case "Silver":
                spawnMaterialSphere("Silver", position, new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f), 1.0f, 0.05f);
                break;
            case "Copper":
                spawnMaterialSphere("Copper", position, new ColorRGBA(0.9f, 0.4f, 0.2f, 1.0f), 1.0f, 0.2f);
                break;
            case "Plastic":
                spawnMaterialSphere("Plastic", position, ColorRGBA.randomColor(), 0.0f, (float)Math.random());
                break;
            case "Glass":
                spawnGlassSphere("Glass", position, new ColorRGBA(0.8f, 0.9f, 1.0f, 0.3f));
                break;
        }
    }
    
    private Vector3f getSpawnPosition() {
        Vector3f cameraPos = cam.getLocation();
        Vector3f cameraDir = cam.getDirection();
        
        Vector3f spawnPos = cameraPos.add(cameraDir.mult(5f));
        spawnPos.y = Math.max(spawnPos.y, 2f);
        
        return spawnPos;
    }
    
    private void adjustTimeOfDay(float delta) {
        // Simulate time of day by changing sun direction and color
        Vector3f currentDir = sunLight.getDirection();
        float angle = (float) Math.atan2(currentDir.z, currentDir.x) + delta;
        
        Vector3f newDir = new Vector3f(
            (float) Math.cos(angle) * 0.5f,
            -0.8f,
            (float) Math.sin(angle) * 0.3f
        ).normalizeLocal();
        
        sunLight.setDirection(newDir);
        
        // Change sun color based on "time"
        float sunHeight = -newDir.y;
        ColorRGBA sunColor;
        if (sunHeight > 0.5f) {
            // Noon - bright white
            sunColor = ColorRGBA.White.mult(1.2f);
        } else if (sunHeight > 0.2f) {
            // Morning/Evening - warm
            sunColor = new ColorRGBA(1.0f, 0.8f, 0.6f, 1.0f).mult(1.0f);
        } else {
            // Night - cool blue
            sunColor = new ColorRGBA(0.3f, 0.4f, 0.6f, 1.0f).mult(0.3f);
        }
        
        sunLight.setColor(sunColor);
        System.out.println("Time adjusted - Sun height: " + String.format("%.2f", sunHeight));
    }
    
    private void changeCameraSpeed(float multiplier) {
        cameraSpeed *= multiplier;
        cameraSpeed = Math.max(1f, Math.min(50f, cameraSpeed));
        flyCam.setMoveSpeed(cameraSpeed);
        System.out.println("Camera speed: " + cameraSpeed);
    }
    
    private void resetScene() {
        // Remove spawned objects (keep samples and ground)
        rootNode.getChildren().removeIf(spatial -> 
            spatial.getName() != null && 
            (spatial.getName().contains("_") && Character.isDigit(spatial.getName().charAt(spatial.getName().length()-1))));
        
        objectCounter = 0;
        System.out.println("Scene reset - spawned objects removed");
    }
    
    private void printControls() {
        System.out.println("\n=== REALISTIC PHYSICS DEMO CONTROLS ===");
        System.out.println("1 - Spawn Gold sphere (metallic, low roughness)");
        System.out.println("2 - Spawn Silver sphere (metallic, very low roughness)");
        System.out.println("3 - Spawn Copper sphere (metallic, medium roughness)");
        System.out.println("4 - Spawn Plastic sphere (non-metallic, random color)");
        System.out.println("5 - Spawn Glass sphere (transparent, smooth)");
        System.out.println("SPACE - Spawn random material sphere");
        System.out.println("B - Toggle bloom effect");
        System.out.println("S - Toggle shadows");
        System.out.println("T/Y - Adjust time of day (sun position/color)");
        System.out.println("R - Reset scene");
        System.out.println("WASD - Move camera");
        System.out.println("Mouse - Look around");
        System.out.println("Mouse wheel - Change camera speed");
        System.out.println("========================================\n");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        // Animate point lights for dynamic lighting
        float time = getTimer().getTimeInSeconds();
        
        for (int i = 0; i < pointLights.length; i++) {
            if (pointLights[i] != null) {
                float intensity = 1.5f + (float) Math.sin(time * (1 + i * 0.5f)) * 0.5f;
                ColorRGBA[] baseColors = {ColorRGBA.Red, ColorRGBA.Blue, ColorRGBA.Green};
                pointLights[i].setColor(baseColors[i].mult(intensity));
            }
        }
    }
}