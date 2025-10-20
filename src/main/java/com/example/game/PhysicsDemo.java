package com.example.game;

import com.example.camera.CameraController;
import com.example.physics.CollisionListener;
import com.example.physics.PhysicsMaterial;
import com.example.physics.PhysicsWorld;
import com.example.rendering.GeometryFactory;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 * Physics demonstration application showing various shapes with physics
 */
public class PhysicsDemo extends SimpleApplication {
    
    private PhysicsWorld physicsWorld;
    private CameraController cameraController;
    private GeometryFactory geometryFactory;
    
    public static void main(String[] args) {
        PhysicsDemo app = new PhysicsDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Initialize systems
        initializePhysics();
        initializeCamera();
        initializeLighting();
        initializeGeometryFactory();
        
        // Create demo objects
        createDemoObjects();
        
        // Setup collision handlers
        setupCollisionHandlers();
        
        System.out.println("Physics Demo Started!");
        System.out.println("Controls:");
        System.out.println("- WASD: Move camera");
        System.out.println("- Mouse: Look around");
        System.out.println("- C: Toggle camera mode");
        System.out.println("- Mouse wheel: Zoom (in orbit mode)");
    }
    
    private void initializePhysics() {
        physicsWorld = new PhysicsWorld();
        stateManager.attach(physicsWorld);
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
        
        // Add directional light (sun)
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.8f));
        rootNode.addLight(sun);
    }
    
    private void initializeGeometryFactory() {
        geometryFactory = new GeometryFactory(assetManager);
    }
    
    private void createDemoObjects() {
        // Create ground plane
        createGround();
        
        // Create various physics objects
        createSphereDemo();
        createBoxDemo();
        createCapsuleDemo();
        createCylinderDemo();
        createConeDemo();
    }
    
    private void createGround() {
        Geometry groundGeom = geometryFactory.createGroundPlane("Ground", 50f, GeometryFactory.Colors.GRAY);
        PhysicsRigidBody groundBody = physicsWorld.createGroundPlane("ground", groundGeom);
        
        // Apply material properties
        PhysicsMaterial.CONCRETE.applyTo(groundBody);
    }
    
    private void createSphereDemo() {
        // Create multiple spheres at different positions
        for (int i = 0; i < 3; i++) {
            float radius = 0.5f + i * 0.2f;
            Vector3f position = new Vector3f(-6 + i * 2, 8 + i * 2, 0);
            
            Geometry sphereGeom = geometryFactory.createSphere("Sphere" + i, radius, GeometryFactory.Colors.RED);
            PhysicsRigidBody sphereBody = physicsWorld.createSphere("sphere" + i, sphereGeom, radius, 1f, position);
            
            // Apply rubber material for bouncy behavior
            PhysicsMaterial.RUBBER.applyTo(sphereBody);
        }
    }
    
    private void createBoxDemo() {
        // Create boxes with different sizes
        for (int i = 0; i < 3; i++) {
            Vector3f halfExtents = new Vector3f(0.5f + i * 0.2f, 0.5f, 0.5f + i * 0.1f);
            Vector3f position = new Vector3f(-2 + i * 2, 6 + i * 2, 2);
            
            Geometry boxGeom = geometryFactory.createBox("Box" + i, halfExtents, GeometryFactory.Colors.BLUE);
            PhysicsRigidBody boxBody = physicsWorld.createBox("box" + i, boxGeom, halfExtents, 2f, position);
            
            // Apply wood material
            PhysicsMaterial.WOOD.applyTo(boxBody);
        }
    }
    
    private void createCapsuleDemo() {
        // Create capsules
        for (int i = 0; i < 2; i++) {
            float radius = 0.4f + i * 0.1f;
            float height = 1.5f + i * 0.3f;
            Vector3f position = new Vector3f(2 + i * 2, 7 + i * 2, -2);
            
            Geometry capsuleGeom = geometryFactory.createCapsule("Capsule" + i, radius, height, GeometryFactory.Colors.GREEN);
            PhysicsRigidBody capsuleBody = physicsWorld.createCapsule("capsule" + i, capsuleGeom, radius, height, 1.5f, position);
            
            // Apply plastic material
            PhysicsMaterial.PLASTIC.applyTo(capsuleBody);
        }
    }
    
    private void createCylinderDemo() {
        // Create cylinders
        for (int i = 0; i < 2; i++) {
            float radius = 0.5f + i * 0.1f;
            float height = 1.2f + i * 0.2f;
            Vector3f halfExtents = new Vector3f(radius, height/2, radius);
            Vector3f position = new Vector3f(6 + i * 2, 6 + i * 2, 1);
            
            Geometry cylinderGeom = geometryFactory.createCylinder("Cylinder" + i, radius, height, GeometryFactory.Colors.YELLOW);
            PhysicsRigidBody cylinderBody = physicsWorld.createCylinder("cylinder" + i, cylinderGeom, halfExtents, 1.8f, position);
            
            // Apply metal material
            PhysicsMaterial.METAL.applyTo(cylinderBody);
        }
    }
    
    private void createConeDemo() {
        // Create cones
        for (int i = 0; i < 2; i++) {
            float radius = 0.6f + i * 0.1f;
            float height = 1.5f + i * 0.2f;
            Vector3f position = new Vector3f(-8 + i * 2, 8 + i * 2, -3);
            
            Geometry coneGeom = geometryFactory.createCone("Cone" + i, radius, height, GeometryFactory.Colors.MAGENTA);
            PhysicsRigidBody coneBody = physicsWorld.createCone("cone" + i, coneGeom, radius, height, 1.2f, position);
            
            // Apply glass material
            PhysicsMaterial.GLASS.applyTo(coneBody);
        }
    }
    
    private void setupCollisionHandlers() {
        CollisionListener collisionListener = physicsWorld.getCollisionListener();
        
        // Add collision handler for sphere-box collisions
        collisionListener.addCollisionHandler("sphere-box", (bodyA, bodyB) -> {
            System.out.println("Sphere hit box!");
            // Add some extra bounce
            Vector3f impulse = new Vector3f(0, 2f, 0);
            bodyA.applyImpulse(impulse, Vector3f.ZERO);
        });
        
        // Add collision handler for any object hitting the ground
        collisionListener.addCollisionHandler("any-plane", (bodyA, bodyB) -> {
            // Reduce bouncing on ground
            bodyA.setRestitution(0.3f);
        });
        
        // Add general collision handler
        collisionListener.addCollisionHandler("any-any", (bodyA, bodyB) -> {
            // General collision sound or effect could go here
            System.out.println("Collision detected between objects");
        });
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        // Add some random forces occasionally to keep things interesting
        if (Math.random() < 0.001f) { // Very low probability
            addRandomForce();
        }
    }
    
    private void addRandomForce() {
        // Apply random force to a random sphere
        int sphereIndex = (int) (Math.random() * 3);
        String sphereId = "sphere" + sphereIndex;
        
        Vector3f randomForce = new Vector3f(
            (float) (Math.random() - 0.5) * 10f,
            (float) Math.random() * 5f,
            (float) (Math.random() - 0.5) * 10f
        );
        
        physicsWorld.applyImpulse(sphereId, randomForce);
    }
}