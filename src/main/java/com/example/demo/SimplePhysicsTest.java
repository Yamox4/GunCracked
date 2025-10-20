package com.example.demo;

import com.example.physics.PhysicsMaterial;
import com.example.physics.PhysicsWorld;
import com.example.physics.RaycastHelper;
import com.example.rendering.GeometryFactory;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 * Simple physics test to verify all systems work
 */
public class SimplePhysicsTest extends SimpleApplication implements ActionListener {
    
    private PhysicsWorld physicsWorld;
    private GeometryFactory geometryFactory;
    private RaycastHelper raycastHelper;
    private int objectCounter = 0;
    private boolean sceneInitialized = false;
    private float cameraSpeed = 10f;
    
    public static void main(String[] args) {
        SimplePhysicsTest app = new SimplePhysicsTest();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Initialize physics
        physicsWorld = new PhysicsWorld();
        stateManager.attach(physicsWorld);
        
        // Initialize geometry factory
        geometryFactory = new GeometryFactory(assetManager);
        
        // Setup lighting
        setupLighting();
        
        // Setup free fly camera
        setupFreeCamera();
        
        // Note: raycastHelper and scene creation will be done in simpleUpdate after physics is ready
        
        // Setup input
        setupInput();
        
        // Position camera
        cam.setLocation(new Vector3f(0, 10, 20));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        System.out.println("Simple Physics Test Started!");
        System.out.println("Controls:");
        System.out.println("SPACE - Spawn sphere");
        System.out.println("1 - Spawn box");
        System.out.println("2 - Spawn capsule");
        System.out.println("R - Reset scene");
        System.out.println("WASD - Move camera");
        System.out.println("Mouse - Look around");
        System.out.println("Mouse wheel - Change camera speed");
    }
    
    private void setupLighting() {
        // Brighter ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(ambient);
        
        // Stronger directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.2f));
        rootNode.addLight(sun);
        
        System.out.println("Lighting setup complete");
    }
    
    private void createScene() {
        // Create larger ground platform
        Geometry groundGeom = geometryFactory.createGroundPlane("Ground", 100f, GeometryFactory.Colors.LIGHT_GRAY);
        PhysicsRigidBody groundBody = physicsWorld.createGroundPlane("ground", groundGeom);
        PhysicsMaterial.CONCRETE.applyTo(groundBody);
        
        System.out.println("Created large ground platform (100x100)");
        System.out.println("Ready to spawn objects! Use SPACE, 1, or 2 keys.");
    }
    
    private void createTestSphere() {
        Vector3f spawnPosition = getSpawnPositionFromCamera();
        String id = "sphere_" + (++objectCounter);
        
        Geometry sphereGeom = geometryFactory.createSphere(id, 0.5f, GeometryFactory.Colors.RED);
        PhysicsRigidBody sphereBody = physicsWorld.createSphere(id, sphereGeom, 0.5f, 1f, spawnPosition);
        PhysicsMaterial.RUBBER.applyTo(sphereBody);
        
        System.out.println("Spawned RED sphere at camera direction: " + spawnPosition);
    }
    
    private void createTestBox() {
        Vector3f spawnPosition = getSpawnPositionFromCamera();
        String id = "box_" + (++objectCounter);
        Vector3f halfExtents = new Vector3f(0.5f, 0.5f, 0.5f);
        
        Geometry boxGeom = geometryFactory.createBox(id, halfExtents, GeometryFactory.Colors.BLUE);
        PhysicsRigidBody boxBody = physicsWorld.createBox(id, boxGeom, halfExtents, 2f, spawnPosition);
        PhysicsMaterial.WOOD.applyTo(boxBody);
        
        System.out.println("Spawned BLUE box at camera direction: " + spawnPosition);
    }
    
    private void createTestCapsule() {
        Vector3f spawnPosition = getSpawnPositionFromCamera();
        String id = "capsule_" + (++objectCounter);
        
        Geometry capsuleGeom = geometryFactory.createCapsule(id, 0.3f, 1.2f, GeometryFactory.Colors.GREEN);
        PhysicsRigidBody capsuleBody = physicsWorld.createCapsule(id, capsuleGeom, 0.3f, 1.2f, 1.5f, spawnPosition);
        PhysicsMaterial.METAL.applyTo(capsuleBody);
        
        System.out.println("Spawned GREEN capsule at camera direction: " + spawnPosition);
    }
    
    private void setupFreeCamera() {
        // Enable free fly camera
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(cameraSpeed);
        flyCam.setRotationSpeed(2f);
        
        System.out.println("Free fly camera enabled!");
        System.out.println("- WASD: Move camera");
        System.out.println("- Mouse: Look around");
        System.out.println("- Mouse wheel: Change speed");
    }
    
    private void setupInput() {
        inputManager.addMapping("SpawnSphere", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("SpawnBox", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("SpawnCapsule", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        
        // Add mouse wheel for speed control
        inputManager.addMapping("SpeedUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("SpeedDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        
        inputManager.addListener(this, "SpawnSphere", "SpawnBox", "SpawnCapsule", "Reset");
        inputManager.addListener(this, "SpeedUp", "SpeedDown");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) return;
        
        switch (name) {
            case "SpawnSphere":
                createTestSphere();
                System.out.println("Spawned sphere");
                break;
            case "SpawnBox":
                createTestBox();
                System.out.println("Spawned box");
                break;
            case "SpawnCapsule":
                createTestCapsule();
                System.out.println("Spawned capsule");
                break;
            case "Reset":
                resetScene();
                System.out.println("Scene reset");
                break;
            case "SpeedUp":
                changeCameraSpeed(1.5f);
                break;
            case "SpeedDown":
                changeCameraSpeed(0.75f);
                break;
        }
    }
    
    /**
     * Change camera movement speed
     */
    private void changeCameraSpeed(float multiplier) {
        cameraSpeed *= multiplier;
        
        // Clamp speed between reasonable limits
        cameraSpeed = Math.max(1f, Math.min(100f, cameraSpeed));
        
        // Update fly camera speed
        flyCam.setMoveSpeed(cameraSpeed);
        
        System.out.println(String.format("Camera speed: %.1f", cameraSpeed));
    }
    
    private void resetScene() {
        // Clear physics world (simplified)
        objectCounter = 0;
        
        // Recreate scene
        createScene();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        // Initialize scene once physics world is ready
        if (!sceneInitialized && physicsWorld.isPhysicsReady()) {
            try {
                // Initialize raycast helper now that physics is ready
                raycastHelper = new RaycastHelper(physicsWorld.getPhysicsSpace());
                
                // Create initial scene
                createScene();
                
                sceneInitialized = true;
                System.out.println("Scene initialized successfully!");
            } catch (Exception e) {
                System.err.println("Failed to initialize scene: " + e.getMessage());
            }
        }
        
        // Test raycast helper occasionally (only after initialization)
        if (sceneInitialized && raycastHelper != null && Math.random() < 0.01f) {
            Vector3f testPos = new Vector3f(0, 10, 0);
            RaycastHelper.RaycastResult result = raycastHelper.findGround(testPos, 15f);
            if (result.isHit()) {
                // Ground found - could add visual indicator
            }
        }
    }
    
    /**
     * Get spawn position based on camera direction
     */
    private Vector3f getSpawnPositionFromCamera() {
        // Get camera position and direction
        Vector3f cameraPos = cam.getLocation();
        Vector3f cameraDir = cam.getDirection();
        
        // Simple approach: spawn 5 units in front of camera, 3 units above ground
        Vector3f spawnPos = cameraPos.add(cameraDir.mult(5f));
        spawnPos.y = Math.max(spawnPos.y, 3f); // Ensure at least 3 units above ground
        
        return spawnPos;
    }
}