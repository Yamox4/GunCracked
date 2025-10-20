# jMonkeyEngine Project

This project is set up with jMonkeyEngine 3.6.1-stable, the latest stable version.

## Requirements

- Java 11 or higher
- Gradle (included via wrapper)

## Running the Application

### Windows
```cmd
mvnw.cmd exec:java
```

### Linux/Mac
```bash
./mvnw exec:java
```

## Building the Project

```cmd
mvnw.cmd compile
```

## Downloading Dependencies

```cmd
mvnw.cmd dependency:resolve
```

## Project Structure

- `src/main/java/` - Java source files
- `src/main/resources/` - Resources (textures, models, sounds, etc.)
- `build.gradle` - Build configuration with jME3 dependencies

## Included jMonkeyEngine Libraries

- Core engine (`jme3-core`)
- Desktop backend (`jme3-desktop`)
- LWJGL3 renderer (`jme3-lwjgl3`)
- Effects system (`jme3-effects`)
- Networking (`jme3-networking`)
- Asset plugins (`jme3-plugins`)
- Audio support (`jme3-jogg`)
- Terrain system (`jme3-terrain`)
- Bullet physics (`jme3-jbullet`)
- Blender asset loading (`jme3-blender`)

## Getting Started

The main application class is `com.example.Main` which creates a simple rotating blue cube to verify everything is working correctly.