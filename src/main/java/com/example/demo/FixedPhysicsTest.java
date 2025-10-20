package com.example.demo;

import com.example.rendering.GeometryFactory;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
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
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * Fixed physics test with solid ground and camera-based spawning
 */
public class FixedPhysicsTest extends SimpleApplication implements ActionListener {
    
    private BulletAppState bulletAppState;
    private GeometryFactory geometryFactory;
    private int objectCounter = 0;
    private float cameraSpeed = 10f;
    
    public static void main(String[] args) {
        FixedPhysicsTest app = new FixedPhysicsTest();
        
        // Configure window settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Fixed 3D Physics Demo - Solid Ground");
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        settings.setSamples(4);
        settings.setFrameRate(60);
        settings.setFullscreen(false);
        
        app.setSettings(settings);
        app.setShowSettings(true); // Show JMonkey settings GUI
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Initialize Bullet Physics directly
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // Initialize geometry factory
        geometryFactory = new GeometryFactory(assetManager);
        
        // Setup lighting
        setupLighting();
        
        // Setup camera
        setupCamera();
        
        // Create solid ground
        createSolidGround();
        
        // Setup input
        setupInput();
        
        System.out.println("Fixed Physics Test Started!");
        System.out.println("Controls:");
        System.out.println("SPACE - Spawn RED sphere at camera direction");
        System.out.println("1 - Spawn BLUE box at camera direction");
        System.out.println("2 - Spawn GREEN capsule at camera direction");
        System.out.println("R - Reset scene");
        System.out.println("WASD - Move camera");
        System.out.println("Mouse - Look around");
        System.out.println("Mouse wheel - Change camera speed");
    }
    
    private void setupLighting() {
        // Bright ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.7f));
        rootNode.addLight(ambient);
        
        // Strong directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.0f));
        rootNode.addLight(sun);
    }
    
    private void setupCamera() {
        // Enable free fly camera
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(cameraSpeed);
        flyCam.setRotationSpeed(2f);
        
        // Position camera for good view
        cam.setLocation(new Vector3f(0, 5, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
    
    private void createSolidGround() {
        // Create large ground box geometry
        Box groundBox = new Box(50f, 1f, 50f); // 100x2x100 ground
        Geometry groundGeom = new Geometry("Ground", groundBox);
        groundGeom.setMaterial(geometryFactory.createLitMaterial(
            ColorRGBA.Gray, ColorRGBA.Gray.mult(0.3f), ColorRGBA.White, 1f));
        
        // Position ground
        groundGeom.setLocalTranslation(0, -1f, 0);
        
        // Add physics - SOLID static collision
        RigidBodyControl groundPhysics = new RigidBodyControl(0f); // mass = 0 for static
        groundGeom.addControl(groundPhysics);
        bulletAppState.getPhysicsSpace().add(groundPhysics);
        
        // Attach to scene
        rootNode.attachChild(groundGeom);
        
        System.out.println("SOLID GROUND CREATED:");
        System.out.println("- Size: 100x2x100 units");
        System.out.println("- Position: (0, -1, 0)");
        System.out.println("- Physics: Static collision (mass=0)");
        System.out.println("- Objects should NOT fall through!");
    }
    
    private void setupInput() {
        inputManager.addMapping("SpawnSphere", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("SpawnBox", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("SpawnCapsule", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        
        // Mouse wheel for speed control
        inputManager.addMapping("SpeedUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("SpeedDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        
        inputManager.addListener(this, "SpawnSphere", "SpawnBox", "SpawnCapsule", "Reset", "SpeedUp", "SpeedDown");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) return;
        
        switch (name) {
            case "SpawnSphere":
                spawnSphere();
                break;
            case "SpawnBox":
                spawnBox();
                break;
            case "SpawnCapsule":
                spawnCapsule();
                break;
            case "Reset":
                resetScene();
                break;
            case "SpeedUp":
                changeCameraSpeed(1.5f);
                break;
            case "SpeedDown":
                changeCameraSpeed(0.75f);
                break;
        }
    }
    
    private void spawnSphere() {
        Vector3f spawnPos = getSpawnPosition();
        String id = "sphere_" + (++objectCounter);
        
        // Create visual
        Geometry sphereGeom = geometryFactory.createSphere(id, 0.5f, GeometryFactory.Colors.RED);
        sphereGeom.setLocalTranslation(spawnPos);
        
        // Add physics
        SphereCollisionShape sphereShape = new SphereCollisionShape(0.5f);
        RigidBodyControl spherePhysics = new RigidBodyControl(sphereShape, 1f); // mass = 1
        sphereGeom.addControl(spherePhysics);
        bulletAppState.getPhysicsSpace().add(spherePhysics);
        
        // Apply material properties
        spherePhysics.setFriction(0.8f);
        spherePhysics.setRestitution(0.9f); // Very bouncy
        
        // Attach to scene
        rootNode.attachChild(sphereGeom);
        
        System.out.println("Spawned RED bouncy sphere at: " + spawnPos);
    }
    
    private void spawnBox() {
        Vector3f spawnPos = getSpawnPosition();
        String id = "box_" + (++objectCounter);
        
        // Create visual
        Vector3f halfExtents = new Vector3f(0.5f, 0.5f, 0.5f);
        Geometry boxGeom = geometryFactory.createBox(id, halfExtents, GeometryFactory.Colors.BLUE);
        boxGeom.setLocalTranslation(spawnPos);
        
        // Add physics
        BoxCollisionShape boxShape = new BoxCollisionShape(halfExtents);
        RigidBodyControl boxPhysics = new RigidBodyControl(boxShape, 2f); // mass = 2
        boxGeom.addControl(boxPhysics);
        bulletAppState.getPhysicsSpace().add(boxPhysics);
        
        // Apply material properties
        boxPhysics.setFriction(0.6f);
        boxPhysics.setRestitution(0.3f); // Less bouncy
        
        // Attach to scene
        rootNode.attachChild(boxGeom);
        
        System.out.println("Spawned BLUE wooden box at: " + spawnPos);
    }
    
    private void spawnCapsule() {
        Vector3f spawnPos = getSpawnPosition();
        String id = "capsule_" + (++objectCounter);
        
        // Create visual
        Geometry capsuleGeom = geometryFactory.createCapsule(id, 0.3f, 1.2f, GeometryFactory.Colors.GREEN);
        capsuleGeom.setLocalTranslation(spawnPos);
        
        // Add physics
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.3f, 1.2f);
        RigidBodyControl capsulePhysics = new RigidBodyControl(capsuleShape, 1.5f); // mass = 1.5
        capsuleGeom.addControl(capsulePhysics);
        bulletAppState.getPhysicsSpace().add(capsulePhysics);
        
        // Apply material properties
        capsulePhysics.setFriction(0.4f);
        capsulePhysics.setRestitution(0.2f); // Low bounce
        
        // Attach to scene
        rootNode.attachChild(capsuleGeom);
        
        System.out.println("Spawned GREEN metal capsule at: " + spawnPos);
    }
    
    /**
     * Get spawn position 5 units in front of camera
     */
    private Vector3f getSpawnPosition() {
        Vector3f cameraPos = cam.getLocation();
        Vector3f cameraDir = cam.getDirection();
        
        // Spawn 5 units in front of camera, ensure minimum height
        Vector3f spawnPos = cameraPos.add(cameraDir.mult(5f));
        spawnPos.y = Math.max(spawnPos.y, 2f); // At least 2 units above ground
        
        return spawnPos;
    }
    
    private void changeCameraSpeed(float multiplier) {
        cameraSpeed *= multiplier;
        cameraSpeed = Math.max(1f, Math.min(50f, cameraSpeed));
        flyCam.setMoveSpeed(cameraSpeed);
        System.out.println("Camera speed: " + cameraSpeed);
    }
    
    private void resetScene() {
        // Simple reset - remove all children except ground
        rootNode.detachAllChildren();
        objectCounter = 0;
        
        // Recreate ground
        createSolidGround();
        
        System.out.println("Scene reset - ground recreated");
    }
}