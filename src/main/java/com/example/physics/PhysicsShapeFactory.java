package com.example.physics;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;

/**
 * Factory class for creating various collision shapes
 */
public class PhysicsShapeFactory {

    /**
     * Create a sphere collision shape
     */
    public static SphereCollisionShape createSphere(float radius) {
        return new SphereCollisionShape(radius);
    }

    /**
     * Create a box collision shape
     */
    public static BoxCollisionShape createBox(Vector3f halfExtents) {
        return new BoxCollisionShape(halfExtents);
    }

    /**
     * Create a box collision shape from dimensions
     */
    public static BoxCollisionShape createBox(float width, float height, float depth) {
        return new BoxCollisionShape(new Vector3f(width / 2, height / 2, depth / 2));
    }

    /**
     * Create a capsule collision shape
     */
    public static CapsuleCollisionShape createCapsule(float radius, float height) {
        return new CapsuleCollisionShape(radius, height);
    }

    /**
     * Create a cylinder collision shape
     */
    public static CylinderCollisionShape createCylinder(Vector3f halfExtents) {
        return new CylinderCollisionShape(halfExtents);
    }

    /**
     * Create a cylinder collision shape from dimensions
     */
    public static CylinderCollisionShape createCylinder(float radius, float height) {
        return new CylinderCollisionShape(new Vector3f(radius, height / 2, radius));
    }

    /**
     * Create a cone collision shape
     */
    public static ConeCollisionShape createCone(float radius, float height) {
        return new ConeCollisionShape(radius, height);
    }

    /**
     * Create a plane collision shape
     */
    public static PlaneCollisionShape createPlane(Vector3f normal, float constant) {
        Plane plane = new Plane(normal, constant);
        return new PlaneCollisionShape(plane);
    }

    /**
     * Create a ground plane
     */
    public static PlaneCollisionShape createGroundPlane() {
        Plane plane = new Plane(Vector3f.UNIT_Y, 0);
        return new PlaneCollisionShape(plane);
    }

    /**
     * Create a mesh collision shape from a spatial
     */
    public static CollisionShape createMeshShape(Spatial spatial) {
        return CollisionShapeFactory.createMeshShape(spatial);
    }

    /**
     * Create a dynamic mesh shape
     */
    public static CollisionShape createDynamicMeshShape(Spatial spatial) {
        return CollisionShapeFactory.createDynamicMeshShape(spatial);
    }

    /**
     * Create a convex hull shape from a mesh
     */
    public static HullCollisionShape createHullShape(Mesh mesh) {
        return new HullCollisionShape(mesh);
    }

    /**
     * Create a compound collision shape
     */
    public static CompoundCollisionShape createCompoundShape() {
        return new CompoundCollisionShape();
    }

    /**
     * Create a heightfield collision shape
     */
    public static HeightfieldCollisionShape createHeightfield(float[] heightmap, Vector3f scale) {
        return new HeightfieldCollisionShape(heightmap, scale);
    }

    /**
     * Create a multi-sphere compound shape
     */
    public static CompoundCollisionShape createMultiSphere(Vector3f[] centers, float[] radii) {
        CompoundCollisionShape compound = new CompoundCollisionShape();

        for (int i = 0; i < centers.length && i < radii.length; i++) {
            SphereCollisionShape sphere = new SphereCollisionShape(radii[i]);
            compound.addChildShape(sphere, centers[i]);
        }

        return compound;
    }

    /**
     * Create a simplified box shape from spatial bounds
     */
    public static BoxCollisionShape createBoxFromBounds(Spatial spatial) {
        return (BoxCollisionShape) CollisionShapeFactory.createBoxShape(spatial);
    }

    /**
     * Create a character capsule
     */
    public static CapsuleCollisionShape createCharacterCapsule(float radius, float height) {
        return new CapsuleCollisionShape(radius, height - 2 * radius, 1);
    }

    /**
     * Create an optimized shape based on spatial geometry
     */
    public static CollisionShape createOptimizedShape(Spatial spatial, boolean dynamic) {
        if (dynamic) {
            return CollisionShapeFactory.createDynamicMeshShape(spatial);
        } else {
            return CollisionShapeFactory.createMeshShape(spatial);
        }
    }

    /**
     * Create a hollow box using compound shapes
     */
    public static CompoundCollisionShape createHollowBox(Vector3f outerExtents, Vector3f wallThickness) {
        CompoundCollisionShape compound = new CompoundCollisionShape();
        Vector3f innerExtents = outerExtents.subtract(wallThickness);

        // Create walls
        BoxCollisionShape bottom = new BoxCollisionShape(new Vector3f(outerExtents.x, wallThickness.y / 2, outerExtents.z));
        compound.addChildShape(bottom, new Vector3f(0, -outerExtents.y + wallThickness.y / 2, 0));

        BoxCollisionShape top = new BoxCollisionShape(new Vector3f(outerExtents.x, wallThickness.y / 2, outerExtents.z));
        compound.addChildShape(top, new Vector3f(0, outerExtents.y - wallThickness.y / 2, 0));

        return compound;
    }

    /**
     * Create stairs shape
     */
    public static CompoundCollisionShape createStairs(int stepCount, float stepWidth, float stepHeight, float stepDepth) {
        CompoundCollisionShape compound = new CompoundCollisionShape();

        for (int i = 0; i < stepCount; i++) {
            float y = i * stepHeight + stepHeight / 2;
            float z = i * stepDepth + stepDepth / 2;

            BoxCollisionShape step = new BoxCollisionShape(new Vector3f(stepWidth / 2, stepHeight / 2, stepDepth / 2));
            compound.addChildShape(step, new Vector3f(0, y, z));
        }

        return compound;
    }
}
