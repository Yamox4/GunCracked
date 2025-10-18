# GunCracked - Tron Cube Game

A mini 3D game built with Java and LWJGL featuring a floating cube with third-person camera and Tron-style aesthetics.

## Features

🎮 **Floating Cube Control** - Move your orange cube in 3D space  
📷 **Third-Person Camera** - Smooth camera that follows the cube  
🌐 **Tron Aesthetics** - Glowing cyan grid ground with wireframe styling  
💡 **Real-time Lighting** - Phong lighting model with ambient, diffuse, and specular components  
🎨 **Dual Rendering** - Solid cube with wireframe overlay for that cyberpunk look  

## Controls

- **W/A/S/D** - Move cube horizontally
- **Space** - Move cube up
- **Left Shift** - Move cube down

## Requirements

- Java 11 or higher
- Windows (native libraries included)

## Quick Start

1. **Download dependencies:**
   ```bash
   ./download_libs.bat
   ```

2. **Build and run:**
   ```bash
   ./build_and_run.bat
   ```

## Technical Details

- **Engine:** LWJGL 3.3.3 (OpenGL bindings)
- **Math:** JOML 1.10.5
- **Graphics:** OpenGL 3.3 Core Profile
- **Shaders:** Custom vertex/fragment shaders with lighting
- **Rendering:** Solid geometry + wireframe overlay

## Project Structure

```
src/main/java/com/game/
├── Main.java      # Entry point and window management
├── Game.java      # Game loop and logic
├── Shader.java    # OpenGL shader management
├── Camera.java    # Third-person camera system
├── Cube.java      # 3D cube rendering (solid + wireframe)
└── Ground.java    # Tron-style grid ground
```

## Screenshots

The game features a dark blue background with a glowing cyan grid floor and an orange cube that you can fly around in 3D space. The cube has both solid rendering with realistic lighting and a yellow wireframe overlay for that authentic Tron aesthetic.

---

*Built with ❤️ and lots of caffeine*