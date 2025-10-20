# JMonkeyEngine 3D Physics Engine

A comprehensive 3D physics engine built with JMonkeyEngine featuring collision detection for various shapes including spheres, capsules, boxes, cylinders, cones, and more.

## Features

### Physics Shapes Supported
- **Spheres** - Perfect for balls, projectiles
- **Boxes** - Cubes and rectangular objects
- **Capsules** - Character controllers, pills
- **Cylinders** - Barrels, columns
- **Cones** - Traffic cones, projectiles
- **Planes** - Ground, walls
- **Mesh-based** - Complex 3D models
- **Compound shapes** - Multiple shapes combined

### Physics Features
- **Collision Detection** - Between all shape types
- **Material Properties** - Friction, restitution, damping
- **Force Application** - Forces, impulses, torques
- **Raycasting** - Line-of-sight, ground detection
- **Predefined Materials** - Rubber, metal, wood, ice, concrete, glass, plastic

### Camera System
- **Free Fly Mode** - WASD movement with mouse look
- **Orbit Mode** - Rotate around target object
- **First Person** - Character perspective
- **Third Person** - Chase camera behind target

## Controls

- **WASD** - Move camera (free fly mode)
- **Mouse** - Look around / rotate camera
- **C** - Toggle camera mode
- **Mouse Wheel** - Zoom (in orbit modes)
- **Q/E** - Move up/down (free fly mode)

## Project Structure

```
src/main/java/com/example/
├── physics/
│   ├── PhysicsEngine.java          # Core physics system
│   ├── PhysicsWorld.java           # High-level world manager
│   ├── PhysicsShapeFactory.java    # Shape creation utilities
│   ├── CollisionListener.java      # Collision event handling
│   ├── CollisionInfo.java          # Collision data container
│   ├── PhysicsMaterial.java        # Material properties
│   └── RaycastHelper.java          # Raycasting utilities
├── rendering/
│   └── GeometryFactory.java        # Visual geometry creation
├── camera/
│   └── CameraController.java       # Camera system
└── game/
    ├── PhysicsDemo.java            # Demo application
    └── GameApplication.java        # Main entry point
```

## Running the Demo

### Windows
```bash
run.bat
```

### Manual Maven Commands
```bash
# Compile
mvn clean compile

# Run demo
mvn exec:java -Dexec.mainClass="com.example.game.PhysicsDemo"
```

## Usage Examples

### Creating Physics Objects

```java
// Initialize physics world
PhysicsWorld physicsWorld = new PhysicsWorld();
stateManager.attach(physicsWorld);

// Create a bouncing sphere
Geometry sphereGeom = geometryFactory.createSphere("Ball", 0.5f, ColorRGBA.Red);
PhysicsRigidBody sphere = physicsWorld.createSphere("ball", sphereGeom, 0.5f, 1f, 
    new Vector3f(0, 10, 0));
PhysicsMaterial.RUBBER.applyTo(sphere);

// Create a static ground
Geometry groundGeom = geometryFactory.createGroundPlane("Ground", 50f, ColorRGBA.Gray);
PhysicsRigidBody ground = physicsWorld.createGroundPlane("ground", groundGeom);
PhysicsMaterial.CONCRETE.applyTo(ground);
```

### Collision Handling

```java
CollisionListener collisionListener = physicsWorld.getCollisionListener();

// Handle sphere-box collisions
collisionListener.addCollisionHandler("sphere-box", (bodyA, bodyB) -> {
    System.out.println("Sphere hit box!");
    Vector3f bounceImpulse = new Vector3f(0, 5f, 0);
    bodyA.applyImpulse(bounceImpulse, Vector3f.ZERO);
});
```

### Applying Forces

```java
// Apply continuous force
physicsWorld.applyForce("objectId", new Vector3f(10, 0, 0));

// Apply instant impulse
physicsWorld.applyImpulse("objectId", new Vector3f(0, 10, 0));
```

### Raycasting

```java
RaycastHelper raycast = new RaycastHelper(physicsWorld.getPhysicsSpace());

// Check ground below player
RaycastHelper.RaycastResult result = raycast.findGround(playerPosition, 10f);
if (result.isHit()) {
    float distanceToGround = result.getDistance(playerPosition);
}
```

## Dependencies

- JMonkeyEngine 3.6.1
- Bullet Physics (jme3-jbullet)
- Java 11+

## Architecture

The physics engine is built in layers:

1. **PhysicsEngine** - Low-level Bullet Physics wrapper
2. **PhysicsWorld** - High-level world management with visual integration
3. **Collision System** - Event-driven collision handling
4. **Material System** - Physical property management
5. **Rendering System** - Visual representation of physics objects
6. **Camera System** - Multiple camera modes for different use cases

This architecture allows for easy extension and customization while maintaining performance and usability.