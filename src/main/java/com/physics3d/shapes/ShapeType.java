package com.physics3d.shapes;

/**
 * Enumeration of supported collision shape types.
 */
public enum ShapeType {
    /**
     * Box shape with width, height, and depth dimensions.
     */
    BOX,
    
    /**
     * Sphere shape with radius.
     */
    SPHERE,
    
    /**
     * Cylinder shape with radius and height.
     */
    CYLINDER,
    
    /**
     * Capsule shape with radius and height.
     */
    CAPSULE,
    
    /**
     * Cone shape with radius and height.
     */
    CONE,
    
    /**
     * Triangle mesh shape for complex geometry.
     */
    MESH,
    
    /**
     * Convex hull shape generated from point clouds.
     */
    CONVEX_HULL,
    
    /**
     * Compound shape composed of multiple sub-shapes.
     */
    COMPOUND
}