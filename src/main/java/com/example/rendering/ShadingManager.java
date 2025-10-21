package com.example.rendering;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.texture.Texture;

/**
 * Advanced shading and lighting manager for realistic rendering
 */
public class ShadingManager extends BaseAppState {
    
    private AssetManager assetManager;
    private ViewPort viewPort;
    private Node rootNode;
    private FilterPostProcessor fpp;
    
    // Lighting
    private AmbientLight ambientLight;
    private DirectionalLight sunLight;
    private Map<String, PointLight> pointLights;
    private Map<String, SpotLight> spotLights;
    
    // Post-processing filters
    private BloomFilter bloomFilter;
    private FXAAFilter fxaaFilter;
    private ToneMapFilter toneMapFilter;
    private DirectionalLightShadowFilter shadowFilter;
    
    // Shading presets
    public enum ShadingPreset {
        REALISTIC,
        STYLIZED,
        DARK_MOODY,
        BRIGHT_OUTDOOR,
        INDOOR_WARM,
        CYBERPUNK,
        NATURAL
    }
    
    public ShadingManager() {
        this.pointLights = new HashMap<>();
        this.spotLights = new HashMap<>();
    }
    
    @Override
    protected void initialize(Application app) {
        this.assetManager = app.getAssetManager();
        this.viewPort = app.getViewPort();
        
        if (app instanceof com.jme3.app.SimpleApplication) {
            this.rootNode = ((com.jme3.app.SimpleApplication) app).getRootNode();
        }
        
        // Initialize post-processing
        initializePostProcessing();
        
        // Set default realistic shading
        applyShadingPreset(ShadingPreset.REALISTIC);
        
        System.out.println("ShadingManager initialized with realistic lighting");
    }
    
    @Override
    protected void cleanup(Application app) {
        if (fpp != null) {
            viewPort.removeProcessor(fpp);
        }
        clearAllLights();
    }
    
    @Override
    protected void onEnable() {
        // Shading manager enabled
    }
    
    @Override
    protected void onDisable() {
        // Shading manager disabled
    }
    
    /**
     * Initialize post-processing effects
     */
    private void initializePostProcessing() {
        fpp = new FilterPostProcessor(assetManager);
        
        // FXAA Anti-aliasing
        fxaaFilter = new FXAAFilter();
        fpp.addFilter(fxaaFilter);
        
        // Tone mapping for HDR-like effects
        toneMapFilter = new ToneMapFilter(Vector3f.UNIT_XYZ.mult(4.0f));
        fpp.addFilter(toneMapFilter);
        
        // Bloom for glowing effects
        bloomFilter = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloomFilter.setBloomIntensity(1.5f);
        bloomFilter.setExposurePower(55f);
        bloomFilter.setExposureCutOff(0.0f);
        fpp.addFilter(bloomFilter);
        
        viewPort.addProcessor(fpp);
    }
    
    /**
     * Apply a shading preset
     */
    public void applyShadingPreset(ShadingPreset preset) {
        clearAllLights();
        
        switch (preset) {
            case REALISTIC:
                setupRealisticLighting();
                break;
            case STYLIZED:
                setupStylizedLighting();
                break;
            case DARK_MOODY:
                setupDarkMoodyLighting();
                break;
            case BRIGHT_OUTDOOR:
                setupBrightOutdoorLighting();
                break;
            case INDOOR_WARM:
                setupIndoorWarmLighting();
                break;
            case CYBERPUNK:
                setupCyberpunkLighting();
                break;
            case NATURAL:
                setupNaturalLighting();
                break;
        }
        
        System.out.println("Applied shading preset: " + preset);
    }
    
    /**
     * Setup realistic lighting (default)
     */
    private void setupRealisticLighting() {
        // Soft ambient light
        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(ambientLight);
        
        // Strong directional sun light
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        sunLight.setColor(ColorRGBA.White.mult(1.2f));
        rootNode.addLight(sunLight);
        
        // Add shadows
        setupShadows();
        
        // Adjust post-processing for realism
        bloomFilter.setBloomIntensity(1.2f);
        toneMapFilter.setWhitePoint(Vector3f.UNIT_XYZ.mult(3.0f));
    }
    
    /**
     * Setup stylized lighting
     */
    private void setupStylizedLighting() {
        // Bright ambient
        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(ambientLight);
        
        // Colorful directional light
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.3f, -1f, -0.2f).normalizeLocal());
        sunLight.setColor(new ColorRGBA(1.0f, 0.9f, 0.7f, 1.0f).mult(0.8f));
        rootNode.addLight(sunLight);
        
        // Reduce shadows for stylized look
        bloomFilter.setBloomIntensity(2.0f);
    }
    
    /**
     * Setup dark moody lighting
     */
    private void setupDarkMoodyLighting() {
        // Very low ambient
        ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.1f, 0.1f, 0.2f, 1.0f).mult(0.2f));
        rootNode.addLight(ambientLight);
        
        // Dim directional light
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.7f, -1f, -0.5f).normalizeLocal());
        sunLight.setColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 1.0f).mult(0.4f));
        rootNode.addLight(sunLight);
        
        // Add dramatic point lights
        addPointLight("mood1", new Vector3f(5, 3, 5), ColorRGBA.Blue.mult(2.0f), 15f);
        addPointLight("mood2", new Vector3f(-5, 3, -5), ColorRGBA.Red.mult(1.5f), 12f);
        
        setupShadows();
        bloomFilter.setBloomIntensity(2.5f);
    }
    
    /**
     * Setup bright outdoor lighting
     */
    private void setupBrightOutdoorLighting() {
        // Bright ambient
        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.7f));
        rootNode.addLight(ambientLight);
        
        // Very bright sun
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.3f, -1f, -0.1f).normalizeLocal());
        sunLight.setColor(ColorRGBA.White.mult(1.8f));
        rootNode.addLight(sunLight);
        
        setupShadows();
        bloomFilter.setBloomIntensity(0.8f);
        toneMapFilter.setWhitePoint(Vector3f.UNIT_XYZ.mult(5.0f));
    }
    
    /**
     * Setup indoor warm lighting
     */
    private void setupIndoorWarmLighting() {
        // Warm ambient
        ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(1.0f, 0.9f, 0.7f, 1.0f).mult(0.4f));
        rootNode.addLight(ambientLight);
        
        // Warm directional light (window light)
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.8f, -1f, 0.2f).normalizeLocal());
        sunLight.setColor(new ColorRGBA(1.0f, 0.95f, 0.8f, 1.0f).mult(0.6f));
        rootNode.addLight(sunLight);
        
        // Add warm point lights (lamps)
        addPointLight("lamp1", new Vector3f(3, 2, 3), new ColorRGBA(1.0f, 0.8f, 0.6f, 1.0f).mult(1.5f), 8f);
        addPointLight("lamp2", new Vector3f(-3, 2, -3), new ColorRGBA(1.0f, 0.8f, 0.6f, 1.0f).mult(1.2f), 6f);
        
        bloomFilter.setBloomIntensity(1.8f);
    }
    
    /**
     * Setup cyberpunk lighting
     */
    private void setupCyberpunkLighting() {
        // Dark blue ambient
        ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.1f, 0.2f, 0.4f, 1.0f).mult(0.3f));
        rootNode.addLight(ambientLight);
        
        // Cool directional light
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        sunLight.setColor(new ColorRGBA(0.7f, 0.9f, 1.0f, 1.0f).mult(0.5f));
        rootNode.addLight(sunLight);
        
        // Neon lights
        addPointLight("neon1", new Vector3f(4, 2, 0), ColorRGBA.Magenta.mult(3.0f), 10f);
        addPointLight("neon2", new Vector3f(-4, 2, 0), ColorRGBA.Cyan.mult(2.5f), 8f);
        addPointLight("neon3", new Vector3f(0, 2, 4), new ColorRGBA(0.0f, 1.0f, 0.5f, 1.0f).mult(2.0f), 12f);
        
        bloomFilter.setBloomIntensity(3.5f);
        bloomFilter.setExposurePower(75f);
    }
    
    /**
     * Setup natural lighting
     */
    private void setupNaturalLighting() {
        // Natural ambient
        ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.9f, 0.95f, 1.0f, 1.0f).mult(0.4f));
        rootNode.addLight(ambientLight);
        
        // Natural sun light
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-0.4f, -1f, -0.2f).normalizeLocal());
        sunLight.setColor(new ColorRGBA(1.0f, 0.98f, 0.9f, 1.0f).mult(1.0f));
        rootNode.addLight(sunLight);
        
        setupShadows();
        bloomFilter.setBloomIntensity(1.0f);
        toneMapFilter.setWhitePoint(Vector3f.UNIT_XYZ.mult(2.5f));
    }
    
    /**
     * Setup shadow mapping
     */
    private void setupShadows() {
        if (sunLight != null) {
            shadowFilter = new DirectionalLightShadowFilter(assetManager, 2048, 3);
            shadowFilter.setLight(sunLight);
            shadowFilter.setShadowIntensity(0.4f);
            shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
            fpp.addFilter(shadowFilter);
        }
    }
    
    /**
     * Add a point light
     */
    public PointLight addPointLight(String id, Vector3f position, ColorRGBA color, float radius) {
        PointLight pointLight = new PointLight();
        pointLight.setPosition(position);
        pointLight.setColor(color);
        pointLight.setRadius(radius);
        
        rootNode.addLight(pointLight);
        pointLights.put(id, pointLight);
        
        return pointLight;
    }
    
    /**
     * Add a spot light
     */
    public SpotLight addSpotLight(String id, Vector3f position, Vector3f direction, 
                                  ColorRGBA color, float range, float innerAngle, float outerAngle) {
        SpotLight spotLight = new SpotLight();
        spotLight.setPosition(position);
        spotLight.setDirection(direction);
        spotLight.setColor(color);
        spotLight.setSpotRange(range);
        spotLight.setSpotInnerAngle(innerAngle * FastMath.DEG_TO_RAD);
        spotLight.setSpotOuterAngle(outerAngle * FastMath.DEG_TO_RAD);
        
        rootNode.addLight(spotLight);
        spotLights.put(id, spotLight);
        
        return spotLight;
    }
    
    /**
     * Remove a point light
     */
    public void removePointLight(String id) {
        PointLight light = pointLights.remove(id);
        if (light != null) {
            rootNode.removeLight(light);
        }
    }
    
    /**
     * Remove a spot light
     */
    public void removeSpotLight(String id) {
        SpotLight light = spotLights.remove(id);
        if (light != null) {
            rootNode.removeLight(light);
        }
    }
    
    /**
     * Clear all lights
     */
    private void clearAllLights() {
        if (rootNode != null) {
            if (ambientLight != null) rootNode.removeLight(ambientLight);
            if (sunLight != null) rootNode.removeLight(sunLight);
            
            for (PointLight light : pointLights.values()) {
                rootNode.removeLight(light);
            }
            for (SpotLight light : spotLights.values()) {
                rootNode.removeLight(light);
            }
        }
        
        pointLights.clear();
        spotLights.clear();
        
        // Remove shadow filter
        if (shadowFilter != null && fpp != null) {
            fpp.removeFilter(shadowFilter);
            shadowFilter = null;
        }
    }
    
    /**
     * Create a PBR (Physically Based Rendering) material
     */
    public Material createPBRMaterial(ColorRGBA baseColor, float metallic, float roughness) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", baseColor);
        mat.setFloat("Metallic", metallic);
        mat.setFloat("Roughness", roughness);
        return mat;
    }
    
    /**
     * Create a PBR material with textures
     */
    public Material createPBRMaterial(String baseColorMap, String normalMap, 
                                      String metallicRoughnessMap, String aoMap) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        
        if (baseColorMap != null) {
            Texture baseColor = assetManager.loadTexture(baseColorMap);
            mat.setTexture("BaseColorMap", baseColor);
        }
        
        if (normalMap != null) {
            Texture normal = assetManager.loadTexture(normalMap);
            mat.setTexture("NormalMap", normal);
        }
        
        if (metallicRoughnessMap != null) {
            Texture metallicRoughness = assetManager.loadTexture(metallicRoughnessMap);
            mat.setTexture("MetallicRoughnessMap", metallicRoughness);
        }
        
        if (aoMap != null) {
            Texture ao = assetManager.loadTexture(aoMap);
            mat.setTexture("LightMap", ao);
        }
        
        return mat;
    }
    
    /**
     * Enable/disable bloom effect
     */
    public void setBloomEnabled(boolean enabled) {
        if (bloomFilter != null) {
            bloomFilter.setEnabled(enabled);
        }
    }
    
    /**
     * Set bloom intensity
     */
    public void setBloomIntensity(float intensity) {
        if (bloomFilter != null) {
            bloomFilter.setBloomIntensity(intensity);
        }
    }
    
    /**
     * Enable/disable FXAA
     */
    public void setFXAAEnabled(boolean enabled) {
        if (fxaaFilter != null) {
            fxaaFilter.setEnabled(enabled);
        }
    }
    
    /**
     * Set time of day (0.0 = midnight, 0.5 = noon, 1.0 = midnight)
     */
    public void setTimeOfDay(float time) {
        if (sunLight != null && ambientLight != null) {
            // Calculate sun angle
            float sunAngle = time * FastMath.TWO_PI;
            float sunHeight = FastMath.sin(sunAngle);
            
            // Update sun direction
            Vector3f sunDir = new Vector3f(
                FastMath.cos(sunAngle) * 0.5f,
                -FastMath.abs(sunHeight),
                FastMath.sin(sunAngle) * 0.3f
            ).normalizeLocal();
            sunLight.setDirection(sunDir);
            
            // Update sun color based on time
            ColorRGBA sunColor;
            if (sunHeight > 0) {
                // Day time
                float intensity = sunHeight * 1.2f;
                sunColor = ColorRGBA.White.mult(intensity);
            } else {
                // Night time
                sunColor = new ColorRGBA(0.2f, 0.2f, 0.4f, 1.0f).mult(0.1f);
            }
            sunLight.setColor(sunColor);
            
            // Update ambient based on time
            float ambientIntensity = Math.max(0.1f, sunHeight * 0.4f + 0.2f);
            ColorRGBA ambientColor = ColorRGBA.White.mult(ambientIntensity);
            ambientLight.setColor(ambientColor);
        }
    }
    
    // Getters
    public AmbientLight getAmbientLight() { return ambientLight; }
    public DirectionalLight getSunLight() { return sunLight; }
    public PointLight getPointLight(String id) { return pointLights.get(id); }
    public SpotLight getSpotLight(String id) { return spotLights.get(id); }
    public FilterPostProcessor getPostProcessor() { return fpp; }
}