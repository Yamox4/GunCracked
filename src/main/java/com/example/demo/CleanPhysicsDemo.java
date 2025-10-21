package com.example.demo;

import com.example.ui.UIManager;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
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
 * Clean physics demo with realistic shading - empty level, 3 spawn buttons only
 */
public class CleanPhysicsDemo extends SimpleApplication implements ActionListener, UIManager.UIActionListener {

    private BulletAppState bulletAppState;
    private UIManager uiManager;
    private int objectCounter = 0;
    private float cameraSpeed = 10f;

    // Lighting
    private DirectionalLight sunLight;
    private AmbientLight ambientLight;

    // Post-processing
    private FilterPostProcessor fpp;
    private BloomFilter bloomFilter;
    private DirectionalLightShadowFilter shadowFilter;

    public static void main(String[] args) {
        CleanPhysicsDemo app = new CleanPhysicsDemo();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Clean Physics Demo - 3 Shapes Only");
        settings.setResolution(1400, 720);
        settings.setVSync(true);
        settings.setSamples(4);
        settings.setFrameRate(60);

        app.setSettings(settings);
        app.setShowSettings(true);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // FIRST THING: Hide cursor immediately
        inputManager.setCursorVisible(false);

        // Disable default ESC key behavior (prevents dialog and auto-close)
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

        // Initialize physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Initialize UI Manager
        uiManager = new UIManager(this);
        stateManager.attach(uiManager);

        // Setup realistic lighting
        setupRealisticLighting();

        // Setup post-processing effects
        setupPostProcessing();

        // Setup camera
        setupCamera();

        // Create ONLY the ground - no other objects
        createRealisticGround();

        // Setup input
        setupInput();

        printControls();
    }

    private void setupRealisticLighting() {
        // Bright ambient light to show colors clearly
        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.8f));
        rootNode.addLight(ambientLight);

        // Strong directional sun light
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        sunLight.setColor(new ColorRGBA(1.0f, 0.95f, 0.8f, 1.0f).mult(1.2f));
        rootNode.addLight(sunLight);

        System.out.println("Realistic lighting setup:");
        System.out.println("- Ambient light (sky simulation)");
        System.out.println("- Directional sun light with shadows");
    }

    private void setupPostProcessing() {
        fpp = new FilterPostProcessor(assetManager);

        // Anti-aliasing
        FXAAFilter fxaaFilter = new FXAAFilter();
        fpp.addFilter(fxaaFilter);

        // Bloom for glowing effects
        bloomFilter = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloomFilter.setBloomIntensity(1.2f);
        bloomFilter.setExposurePower(55f);
        bloomFilter.setExposureCutOff(0.0f);
        fpp.addFilter(bloomFilter);

        // High-quality shadows
        shadowFilter = new DirectionalLightShadowFilter(assetManager, 2048, 3);
        shadowFilter.setLight(sunLight);
        shadowFilter.setShadowIntensity(0.4f);
        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        fpp.addFilter(shadowFilter);

        viewPort.addProcessor(fpp);

        System.out.println("Post-processing enabled: FXAA + Bloom + Shadows");
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

        System.out.println("Ground created - level is now empty and ready");
    }

    private void setupInput() {
        // ONLY 3 spawn buttons as requested
        inputManager.addMapping("SpawnSphere", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("SpawnBox", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("SpawnCapsule", new KeyTrigger(KeyInput.KEY_3));

        // Camera speed control
        inputManager.addMapping("SpeedUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("SpeedDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        // Reset scene
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));

        inputManager.addListener(this, "SpawnSphere", "SpawnBox", "SpawnCapsule",
                "SpeedUp", "SpeedDown", "Reset");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            return;
        }

        Vector3f spawnPos = getSpawnPosition();

        switch (name) {
            case "SpawnSphere":
                spawnSphere(spawnPos);
                break;
            case "SpawnBox":
                spawnBox(spawnPos);
                break;
            case "SpawnCapsule":
                spawnCapsule(spawnPos);
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

    private void spawnSphere(Vector3f position) {
        String id = "sphere_" + (++objectCounter);

        // Create sphere geometry manually to avoid GeometryFactory's material
        com.jme3.scene.shape.Sphere sphereMesh = new com.jme3.scene.shape.Sphere(32, 32, 0.6f);
        Geometry sphere = new Geometry(id, sphereMesh);

        // Create bright red material
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", ColorRGBA.Red.mult(1.5f));
        mat.setFloat("Metallic", 0.3f);
        mat.setFloat("Roughness", 0.2f);
        sphere.setMaterial(mat);

        sphere.setLocalTranslation(position);
        rootNode.attachChild(sphere);

        // Add physics
        SphereCollisionShape sphereShape = new SphereCollisionShape(0.6f);
        RigidBodyControl spherePhysics = new RigidBodyControl(sphereShape, 1f);
        sphere.addControl(spherePhysics);
        bulletAppState.getPhysicsSpace().add(spherePhysics);

        System.out.println("Spawned METALLIC RED SPHERE at: " + position);
    }

    private void spawnBox(Vector3f position) {
        String id = "box_" + (++objectCounter);
        Vector3f halfExtents = new Vector3f(0.6f, 0.6f, 0.6f);

        // Create box geometry manually to avoid GeometryFactory's material
        com.jme3.scene.shape.Box boxMesh = new com.jme3.scene.shape.Box(halfExtents.x, halfExtents.y, halfExtents.z);
        Geometry box = new Geometry(id, boxMesh);

        // Create bright blue material
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", ColorRGBA.Blue.mult(1.5f));
        mat.setFloat("Metallic", 0.3f);
        mat.setFloat("Roughness", 0.2f);
        box.setMaterial(mat);

        box.setLocalTranslation(position);
        rootNode.attachChild(box);

        // Add physics
        BoxCollisionShape boxShape = new BoxCollisionShape(halfExtents);
        RigidBodyControl boxPhysics = new RigidBodyControl(boxShape, 2f);
        box.addControl(boxPhysics);
        bulletAppState.getPhysicsSpace().add(boxPhysics);

        System.out.println("Spawned METALLIC BLUE BOX at: " + position);
    }

    private void spawnCapsule(Vector3f position) {
        String id = "capsule_" + (++objectCounter);

        // Create capsule geometry manually to avoid GeometryFactory's material
        com.jme3.scene.shape.Cylinder capsuleMesh = new com.jme3.scene.shape.Cylinder(16, 32, 0.4f, 1.2f, true);
        Geometry capsule = new Geometry(id, capsuleMesh);

        // Create bright green material
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", ColorRGBA.Green.mult(1.5f));
        mat.setFloat("Metallic", 0.3f);
        mat.setFloat("Roughness", 0.2f);
        capsule.setMaterial(mat);

        capsule.setLocalTranslation(position);
        rootNode.attachChild(capsule);

        // Add physics - use box collision that exactly matches cylinder dimensions
        // Visual cylinder: radius=0.4, height=1.2 -> Box: half-extents(0.4, 0.6, 0.4)
        BoxCollisionShape capsuleShape = new BoxCollisionShape(new Vector3f(0.4f, 0.6f, 0.4f));
        RigidBodyControl capsulePhysics = new RigidBodyControl(capsuleShape, 1.5f);
        capsule.addControl(capsulePhysics);
        bulletAppState.getPhysicsSpace().add(capsulePhysics);

        System.out.println("Spawned METALLIC GREEN CYLINDER at: " + position);
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
        // Remove all spawned objects (keep only ground)
        rootNode.getChildren().removeIf(spatial
                -> spatial.getName() != null
                && (spatial.getName().startsWith("sphere_")
                || spatial.getName().startsWith("box_")
                || spatial.getName().startsWith("capsule_")));

        objectCounter = 0;
        System.out.println("Scene reset - all spawned objects removed");
    }

    private void printControls() {
        System.out.println("\n=== CLEAN PHYSICS DEMO - 3 SHAPES ONLY ===");
        System.out.println("1 - Spawn METALLIC RED SPHERE");
        System.out.println("2 - Spawn METALLIC BLUE BOX");
        System.out.println("3 - Spawn METALLIC GREEN CYLINDER");
        System.out.println("R - Reset scene (remove all objects)");
        System.out.println("H - Toggle UI visibility");
        System.out.println("ESC - Toggle mouse lock/unlock");
        System.out.println("WASD - Move camera");
        System.out.println("Mouse - Look around");
        System.out.println("Mouse wheel - Change camera speed");
        System.out.println("==========================================");
        System.out.println("Level starts EMPTY - only spawn what you want!");
        System.out.println("UI Menu buttons are visible on screen!");
        System.out.println("==========================================\n");
    }

    // UI Action Listener implementations
    @Override
    public void onSpawnSphere() {
        Vector3f spawnPos = getSpawnPosition();
        spawnSphere(spawnPos);
        updateUIStats();
    }

    @Override
    public void onSpawnBox() {
        Vector3f spawnPos = getSpawnPosition();
        spawnBox(spawnPos);
        updateUIStats();
    }

    @Override
    public void onSpawnCapsule() {
        Vector3f spawnPos = getSpawnPosition();
        spawnCapsule(spawnPos);
        updateUIStats();
    }

    @Override
    public void onResetScene() {
        resetScene();
        updateUIStats();
    }

    @Override
    public void onToggleUI() {
        System.out.println("UI visibility toggled");
    }

    @Override
    public void onCloseApp() {
        System.out.println("Closing application...");
        stop();
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        // Force cursor to be hidden on first few frames to override any default behavior
        if (timer.getTimeInSeconds() < 1.0f) {
            inputManager.setCursorVisible(false);
        }
    }

    private void updateUIStats() {
        if (uiManager != null) {
            uiManager.updateObjectCount(objectCounter);
        }
    }
}
