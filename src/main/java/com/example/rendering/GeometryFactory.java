package com.example.rendering;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;

/**
 * Factory for creating visual geometries that match physics shapes
 */
public class GeometryFactory {
    
    private AssetManager assetManager;
    
    public GeometryFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    /**
     * Create a sphere geometry
     */
    public Geometry createSphere(String name, float radius, ColorRGBA color) {
        Sphere sphere = new Sphere(32, 32, radius);
        Geometry geom = new Geometry(name, sphere);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    /**
     * Create a box geometry
     */
    public Geometry createBox(String name, Vector3f halfExtents, ColorRGBA color) {
        Box box = new Box(halfExtents.x, halfExtents.y, halfExtents.z);
        Geometry geom = new Geometry(name, box);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    /**
     * Create a box geometry from dimensions
     */
    public Geometry createBox(String name, float width, float height, float depth, ColorRGBA color) {
        Box box = new Box(width/2, height/2, depth/2);
        Geometry geom = new Geometry(name, box);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    /**
     * Create a capsule geometry
     */
    public Geometry createCapsule(String name, float radius, float height, ColorRGBA color) {
        // Create capsule using cylinder with spheres (approximation)
        Cylinder cylinder = new Cylinder(16, 32, radius, height, true);
        Geometry geom = new Geometry(name, cylinder);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    /**
     * Create a cylinder geometry
     */
    public Geometry createCylinder(String name, Vector3f halfExtents, ColorRGBA color) {
        Cylinder cylinder = new Cylinder(16, 32, halfExtents.x, halfExtents.y * 2, true);
        Geometry geom = new Geometry(name, cylinder);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    /**
     * Create a cylinder geometry from dimensions
     */
    public Geometry createCylinder(String name, float radius, float height, ColorRGBA color) {
        Cylinder cylinder = new Cylinder(16, 32, radius, height, true);
        Geometry geom = new Geometry(name, cylinder);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    /**
     * Create a cone geometry
     */
    public Geometry createCone(String name, float radius, float height, ColorRGBA color) {
        // Create cone using cylinder with top radius 0
        Cylinder cone = new Cylinder(16, 32, 0, radius, height, true, false);
        Geometry geom = new Geometry(name, cone);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    /**
     * Create a ground plane geometry
     */
    public Geometry createGroundPlane(String name, float size, ColorRGBA color) {
        Quad quad = new Quad(size, size);
        Geometry geom = new Geometry(name, quad);
        geom.setMaterial(createMaterial(color));
        geom.rotate(-1.5708f, 0, 0); // Rotate to lie flat
        geom.move(-size/2, 0, size/2); // Center it
        return geom;
    }
    
    /**
     * Create a wireframe geometry for debugging
     */
    public Geometry createWireframe(String name, Mesh mesh, ColorRGBA color) {
        Geometry geom = new Geometry(name, mesh);
        Material mat = createWireframeMaterial(color);
        geom.setMaterial(mat);
        return geom;
    }
    
    /**
     * Create a basic unshaded material
     */
    private Material createMaterial(ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        return mat;
    }
    
    /**
     * Create a lighting material
     */
    public Material createLitMaterial(ColorRGBA diffuse, ColorRGBA ambient, ColorRGBA specular, float shininess) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", diffuse);
        mat.setColor("Ambient", ambient);
        mat.setColor("Specular", specular);
        mat.setFloat("Shininess", shininess);
        mat.setBoolean("UseMaterialColors", true);
        return mat;
    }
    
    /**
     * Create a wireframe material
     */
    private Material createWireframeMaterial(ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setWireframe(true);
        return mat;
    }
    
    /**
     * Create a textured material
     */
    public Material createTexturedMaterial(String texturePath) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture(texturePath));
        return mat;
    }
    
    /**
     * Get predefined colors
     */
    public static class Colors {
        public static final ColorRGBA RED = ColorRGBA.Red;
        public static final ColorRGBA GREEN = ColorRGBA.Green;
        public static final ColorRGBA BLUE = ColorRGBA.Blue;
        public static final ColorRGBA YELLOW = ColorRGBA.Yellow;
        public static final ColorRGBA CYAN = ColorRGBA.Cyan;
        public static final ColorRGBA MAGENTA = ColorRGBA.Magenta;
        public static final ColorRGBA ORANGE = ColorRGBA.Orange;
        public static final ColorRGBA PINK = ColorRGBA.Pink;
        public static final ColorRGBA WHITE = ColorRGBA.White;
        public static final ColorRGBA GRAY = ColorRGBA.Gray;
        public static final ColorRGBA DARK_GRAY = ColorRGBA.DarkGray;
        public static final ColorRGBA LIGHT_GRAY = ColorRGBA.LightGray;
        public static final ColorRGBA BROWN = ColorRGBA.Brown;
    }
}