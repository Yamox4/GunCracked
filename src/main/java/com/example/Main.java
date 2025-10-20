package com.example;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * Basic jMonkeyEngine application
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        
        // Configure application settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("jMonkeyEngine 3.6.1 Demo");
        settings.setResolution(1024, 768);
        settings.setVSync(true);
        settings.setSamples(4); // Anti-aliasing
        
        app.setSettings(settings);
        app.setShowSettings(false); // Skip settings dialog
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Create a simple box
        Box box = new Box(1, 1, 1);
        Geometry boxGeometry = new Geometry("Box", box);
        
        // Create a material and set its color
        Material boxMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxMaterial.setColor("Color", ColorRGBA.Blue);
        boxGeometry.setMaterial(boxMaterial);
        
        // Attach the box to the scene
        rootNode.attachChild(boxGeometry);
        
        // Position the camera
        cam.setLocation(new Vector3f(0, 0, 5));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        System.out.println("jMonkeyEngine application initialized successfully!");
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Rotate the box
        Geometry box = (Geometry) rootNode.getChild("Box");
        if (box != null) {
            box.rotate(tpf, tpf * 0.5f, 0);
        }
    }
}