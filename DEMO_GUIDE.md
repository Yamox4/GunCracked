# Physics Engine Demo Guide

## Overview

This project includes two demo applications to showcase the comprehensive 3D physics engine:

1. **Simple Physics Test** - Basic functionality demonstration
2. **Interactive Physics Demo** - Full-featured UI with object spawning and testing

## Running the Demos

### Quick Start
```bash
# Windows
build-and-run.bat

# Manual Maven
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.demo.SimplePhysicsTest"
```

## Demo 1: Simple Physics Test

A basic demonstration showing core physics functionality.

### Features:
- ✅ Physics shapes (Sphere, Box, Capsule)
- ✅ Material properties (Rubber, Wood, Metal)
- ✅ Collision detection
- ✅ Ground plane
- ✅ Gravity simulation

### Controls:
- **SPACE** - Spawn bouncy sphere
- **1** - Spawn wooden box
- **2** - Spawn metal capsule
- **R** - Reset scene
- **WASD** - Move camera
- **Mouse** - Look around

### What to Test:
1. **Material Differences**: Notice how rubber spheres bounce more than wooden boxes
2. **Collision Detection**: Objects collide realistically with each other and ground
3. **Physics Simulation**: Gravity affects all objects naturally
4. **Shape Behavior**: Different shapes have different collision characteristics

## Demo 2: Interactive Physics Demo (Advanced)

Full-featured demo with comprehensive UI system for testing all physics features.

### UI Components:

#### Spawn Panel
- **Object Types**: Sphere, Box, Capsule, Cylinder, Cone
- **Spawn Methods**: Click to spawn at cursor, spawn at center
- **Clear Function**: Remove all spawned objects

#### Material Panel
- **Materials**: Rubber, Metal, Wood, Ice, Concrete, Glass
- **Real Properties**: Each material has realistic friction, bounce, density
- **Visual Coding**: Objects are colored based on their material

#### Settings Panel
- **Gravity Control**: Normal, Low, Zero gravity
- **Camera Modes**: Free fly, Orbit camera
- **Real-time Changes**: Modify physics while simulation runs

#### Info Panel
- **Live Statistics**: Object count, selected types, camera mode
- **Physics Info**: Current gravity, material properties
- **Performance**: Real-time updates

### Controls:
- **Left Click** - Spawn selected object at cursor position
- **Right Click** - Apply force to nearby objects
- **WASD** - Move camera
- **Mouse** - Look around / rotate camera
- **C** - Toggle camera mode
- **Mouse Wheel** - Zoom (in orbit mode)
- **R** - Reset entire scene
- **U** - Toggle UI visibility
- **H** - Toggle help text

### Advanced Testing Scenarios:

#### 1. Material Comparison
1. Spawn multiple spheres with different materials
2. Drop them from same height
3. Observe bounce differences:
   - **Rubber**: High bounce, good grip
   - **Metal**: Low bounce, slides easily
   - **Ice**: Very slippery, minimal bounce
   - **Glass**: Brittle behavior, low bounce

#### 2. Shape Dynamics
1. Spawn different shapes with same material
2. Apply forces and observe:
   - **Spheres**: Roll smoothly
   - **Boxes**: Tumble and slide
   - **Capsules**: Stable upright, good for characters
   - **Cylinders**: Roll like wheels
   - **Cones**: Tip over easily

#### 3. Gravity Experiments
1. Set to **Zero Gravity**
2. Spawn objects - they float
3. Apply forces - objects drift in space
4. Switch to **Low Gravity** - moon-like physics
5. **Normal Gravity** - Earth-like behavior

#### 4. Collision Chains
1. Build a tower of boxes
2. Spawn a heavy sphere above
3. Watch realistic collision propagation
4. Try different materials for different effects

#### 5. Force Application
1. Spawn several objects
2. Right-click near them to apply forces
3. Create chain reactions
4. Test with different materials and shapes

## Physics Features Demonstrated

### ✅ **Collision Shapes**
- Sphere, Box, Capsule, Cylinder, Cone
- Compound shapes, Mesh-based collision
- Optimized shape creation

### ✅ **Material System**
- 10+ realistic materials with proper physics properties
- Friction, restitution, density simulation
- Visual material coding

### ✅ **Physics Simulation**
- Bullet Physics integration
- Real-time collision detection
- Force and impulse application
- Gravity control

### ✅ **Raycasting System**
- Cursor-to-world position conversion
- Ground detection
- Line-of-sight calculations
- Spatial queries

### ✅ **Camera System**
- Multiple camera modes
- Smooth transitions
- Mouse and keyboard control
- Orbit and free-fly modes

### ✅ **UI System**
- Modular UI components
- Real-time information display
- Interactive controls
- Professional game-like interface

## Performance Notes

- **Optimized Physics**: Uses efficient Bullet Physics engine
- **Shape Optimization**: Automatic complexity management
- **Memory Management**: Proper cleanup and resource management
- **60 FPS Target**: Smooth simulation at 60 frames per second

## Troubleshooting

### Common Issues:
1. **Build Fails**: Ensure Java 11+ and Maven are installed
2. **Graphics Issues**: Update graphics drivers
3. **Performance**: Reduce anti-aliasing in settings
4. **Controls**: Check that input devices are properly connected

### System Requirements:
- **Java**: 11 or higher
- **Memory**: 2GB RAM minimum
- **Graphics**: OpenGL 3.0+ support
- **OS**: Windows, Mac, or Linux

## Educational Value

This demo showcases:
- **Professional Game Physics**: Industry-standard Bullet Physics
- **Modular Architecture**: Clean, extensible code structure
- **UI Design**: Professional game interface patterns
- **Physics Concepts**: Real-world physics simulation
- **Interactive Learning**: Hands-on experimentation

Perfect for:
- Learning game physics programming
- Understanding material properties
- Testing physics scenarios
- Prototyping game mechanics
- Educational demonstrations

## Next Steps

After exploring the demos:
1. **Modify Materials**: Create custom material properties
2. **Add Shapes**: Implement new collision shapes
3. **Extend UI**: Add more control options
4. **Create Scenarios**: Build specific physics puzzles
5. **Optimize Performance**: Profile and improve performance

The codebase is designed to be easily extensible for your own physics projects!