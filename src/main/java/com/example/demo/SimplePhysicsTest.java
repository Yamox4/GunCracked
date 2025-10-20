package com.example.demo;

import com.example.physics.PhysicsMaterial;
import com.example.physics.PhysicsWorld;
import com.example.physics.RaycastHelper;
import com.example.rendering.GeometryFactory;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
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
    }
    
    private void setupLighting() {
        // Ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(ambient);
        
        // Directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.8f));
        rootNode.addLight(sun);
    }
    
    private void createScene() {
        // Create ground
        Geometry groundGeom = geometryFactory.createGroundPlane("Ground", 20f, GeometryFactory.Colors.GRAY);
        PhysicsRigidBody groundBody = physicsWorld.createGroundPlane("ground", groundGeom);
        PhysicsMaterial.CONCRETE.applyTo(groundBody);
        
        // Create initial test objects
        createTestSphere();
        createTestBox();
        createTestCapsule();
    }
    
    private void createTestSphere() {
        String id = "sphere_" + (++objectCounter);
        Geometry sphereGeom = geometryFactory.createSphere(id, 0.5f, GeometryFactory.Colors.RED);
        PhysicsRigidBody sphereBody = physicsWorld.createSphere(id, sphereGeom, 0.5f, 1f, new Vector3f(-2, 5, 0));
        PhysicsMaterial.RUBBER.applyTo(sphereBody);
    }
    
    private void createTestBox() {
        String id = "box_" + (++objectCounter);
        Vector3f halfExtents = new Vector3f(0.5f, 0.5f, 0.5f);
        Geometry boxGeom = geometryFactory.createBox(id, halfExtents, GeometryFactory.Colors.BLUE);
        PhysicsRigidBody boxBody = physicsWorld.createBox(id, boxGeom, halfExtents, 2f, new Vector3f(0, 5, 0));
        PhysicsMaterial.WOOD.applyTo(boxBody);
    }
    
    private void createTestCapsule() {
        String id = "capsule_" + (++objectCounter);
        Geometry capsuleGeom = geometryFactory.createCapsule(id, 0.3f, 1.2f, GeometryFactory.Colors.GREEN);
        PhysicsRigidBody capsuleBody = physicsWorld.createCapsule(id, capsuleGeom, 0.3f, 1.2f, 1.5f, new Vector3f(2, 5, 0));
        PhysicsMaterial.METAL.applyTo(capsuleBody);
    }
    
    private void setupInput() {
        inputManager.addMapping("SpawnSphere", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("SpawnBox", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("SpawnCapsule", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        
        inputManager.addListener(this, "SpawnSphere", "SpawnBox", "SpawnCapsule", "Reset");
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
        }
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
}