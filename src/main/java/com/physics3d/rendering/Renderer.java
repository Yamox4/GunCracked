package com.physics3d.rendering;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 * Simple rendering system for 3D objects.
 */
public class Renderer extends SimpleApplication {
    
    @Override
    public void simpleInitApp() {
        // Set up basic lighting and camera
        setupCamera();
        setupLighting();
        
        // Add some test objects
        addTestObjects();
    }
    
    private void setupCamera() {
        // Position camera
        cam.setLocation(new Vector3f(0, 5, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);
    }
    
    private void setupLighting() {
        // Simple ambient lighting
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
    }
    
    private void addTestObjects() {
        // Create a red box
        Box box = new Box(1, 1, 1);
        Geometry boxGeom = new Geometry("Box", box);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxMat.setColor("Color", ColorRGBA.Red);
        boxGeom.setMaterial(boxMat);
        boxGeom.setLocalTranslation(-3, 0, 0);
        rootNode.attachChild(boxGeom);
        
        // Create a blue sphere
        Sphere sphere = new Sphere(32, 32, 1);
        Geometry sphereGeom = new Geometry("Sphere", sphere);
        Material sphereMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sphereMat.setColor("Color", ColorRGBA.Blue);
        sphereGeom.setMaterial(sphereMat);
        sphereGeom.setLocalTranslation(3, 0, 0);
        rootNode.attachChild(sphereGeom);
        
        // Create a green ground plane
        Box ground = new Box(10, 0.1f, 10);
        Geometry groundGeom = new Geometry("Ground", ground);
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setColor("Color", ColorRGBA.Green);
        groundGeom.setMaterial(groundMat);
        groundGeom.setLocalTranslation(0, -2, 0);
        rootNode.attachChild(groundGeom);
    }
    
    /**
     * Add a simple object to the scene.
     */
    public void addBox(Vector3f position, ColorRGBA color) {
        Box box = new Box(1, 1, 1);
        Geometry boxGeom = new Geometry("Box", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        boxGeom.setMaterial(mat);
        boxGeom.setLocalTranslation(position);
        rootNode.attachChild(boxGeom);
    }
    
    /**
     * Add a simple sphere to the scene.
     */
    public void addSphere(Vector3f position, ColorRGBA color) {
        Sphere sphere = new Sphere(32, 32, 1);
        Geometry sphereGeom = new Geometry("Sphere", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        sphereGeom.setMaterial(mat);
        sphereGeom.setLocalTranslation(position);
        rootNode.attachChild(sphereGeom);
    }
}