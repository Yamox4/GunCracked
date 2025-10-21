package com.example.rendering;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;

/**
 * Advanced material factory for realistic shading and PBR materials
 */
public class AdvancedMaterialFactory {
    
    private final AssetManager assetManager;
    
    // Material presets
    public enum MaterialType {
        METAL_BRUSHED,
        METAL_POLISHED,
        PLASTIC_MATTE,
        PLASTIC_GLOSSY,
        WOOD_OAK,
        WOOD_PINE,
        CONCRETE,
        BRICK,
        GLASS,
        RUBBER,
        LEATHER,
        FABRIC,
        CERAMIC,
        STONE_MARBLE,
        STONE_GRANITE,
        CARBON_FIBER,
        GOLD,
        SILVER,
        COPPER,
        CHROME
    }
    
    public AdvancedMaterialFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    /**
     * Create a PBR material with realistic properties
     */
    public Material createPBRMaterial(MaterialType type, ColorRGBA tint) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        
        switch (type) {
            case METAL_BRUSHED:
                setupBrushedMetal(mat, tint);
                break;
            case METAL_POLISHED:
                setupPolishedMetal(mat, tint);
                break;
            case PLASTIC_MATTE:
                setupMattePlastic(mat, tint);
                break;
            case PLASTIC_GLOSSY:
                setupGlossyPlastic(mat, tint);
                break;
            case WOOD_OAK:
                setupOakWood(mat, tint);
                break;
            case WOOD_PINE:
                setupPineWood(mat, tint);
                break;
            case CONCRETE:
                setupConcrete(mat, tint);
                break;
            case BRICK:
                setupBrick(mat, tint);
                break;
            case GLASS:
                setupGlass(mat, tint);
                break;
            case RUBBER:
                setupRubber(mat, tint);
                break;
            case LEATHER:
                setupLeather(mat, tint);
                break;
            case FABRIC:
                setupFabric(mat, tint);
                break;
            case CERAMIC:
                setupCeramic(mat, tint);
                break;
            case STONE_MARBLE:
                setupMarble(mat, tint);
                break;
            case STONE_GRANITE:
                setupGranite(mat, tint);
                break;
            case CARBON_FIBER:
                setupCarbonFiber(mat, tint);
                break;
            case GOLD:
                setupGold(mat);
                break;
            case SILVER:
                setupSilver(mat);
                break;
            case COPPER:
                setupCopper(mat);
                break;
            case CHROME:
                setupChrome(mat);
                break;
            default:
                setupDefault(mat, tint);
                break;
        }
        
        return mat;
    }
    
    /**
     * Create a simple PBR material with custom values
     */
    public Material createCustomPBR(ColorRGBA baseColor, float metallic, float roughness, float emissive) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", baseColor);
        mat.setFloat("Metallic", metallic);
        mat.setFloat("Roughness", roughness);
        
        if (emissive > 0) {
            mat.setColor("Emissive", baseColor.mult(emissive));
        }
        
        return mat;
    }
    
    /**
     * Create an emissive (glowing) material
     */
    public Material createEmissiveMaterial(ColorRGBA color, float intensity) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", color);
        mat.setColor("Emissive", color.mult(intensity));
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.8f);
        
        // Make it glow
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
        
        return mat;
    }
    
    /**
     * Create a transparent material
     */
    public Material createTransparentMaterial(ColorRGBA color, float alpha) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        
        ColorRGBA transparentColor = color.clone();
        transparentColor.a = alpha;
        
        mat.setColor("BaseColor", transparentColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.1f);
        
        // Enable transparency
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTransparent(true);
        
        return mat;
    }
    
    // Material setup methods
    private void setupBrushedMetal(Material mat, ColorRGBA tint) {
        mat.setColor("BaseColor", tint.mult(0.8f));
        mat.setFloat("Metallic", 1.0f);
        mat.setFloat("Roughness", 0.4f);
    }
    
    private void setupPolishedMetal(Material mat, ColorRGBA tint) {
        mat.setColor("BaseColor", tint.mult(0.9f));
        mat.setFloat("Metallic", 1.0f);
        mat.setFloat("Roughness", 0.05f);
    }
    
    private void setupMattePlastic(Material mat, ColorRGBA tint) {
        mat.setColor("BaseColor", tint);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.8f);
    }
    
    private void setupGlossyPlastic(Material mat, ColorRGBA tint) {
        mat.setColor("BaseColor", tint);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.2f);
    }
    
    private void setupOakWood(Material mat, ColorRGBA tint) {
        ColorRGBA woodColor = new ColorRGBA(0.6f, 0.4f, 0.2f, 1.0f).mult(tint);
        mat.setColor("BaseColor", woodColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.7f);
    }
    
    private void setupPineWood(Material mat, ColorRGBA tint) {
        ColorRGBA woodColor = new ColorRGBA(0.8f, 0.6f, 0.3f, 1.0f).mult(tint);
        mat.setColor("BaseColor", woodColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.6f);
    }
    
    private void setupConcrete(Material mat, ColorRGBA tint) {
        ColorRGBA concreteColor = new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f).mult(tint);
        mat.setColor("BaseColor", concreteColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.9f);
    }
    
    private void setupBrick(Material mat, ColorRGBA tint) {
        ColorRGBA brickColor = new ColorRGBA(0.7f, 0.3f, 0.2f, 1.0f).mult(tint);
        mat.setColor("BaseColor", brickColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.8f);
    }
    
    private void setupGlass(Material mat, ColorRGBA tint) {
        ColorRGBA glassColor = tint.clone();
        glassColor.a = 0.3f;
        mat.setColor("BaseColor", glassColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.0f);
        
        // Enable transparency
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTransparent(true);
    }
    
    private void setupRubber(Material mat, ColorRGBA tint) {
        mat.setColor("BaseColor", tint.mult(0.3f));
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 1.0f);
    }
    
    private void setupLeather(Material mat, ColorRGBA tint) {
        ColorRGBA leatherColor = new ColorRGBA(0.4f, 0.2f, 0.1f, 1.0f).mult(tint);
        mat.setColor("BaseColor", leatherColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.6f);
    }
    
    private void setupFabric(Material mat, ColorRGBA tint) {
        mat.setColor("BaseColor", tint);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.9f);
    }
    
    private void setupCeramic(Material mat, ColorRGBA tint) {
        mat.setColor("BaseColor", tint);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.1f);
    }
    
    private void setupMarble(Material mat, ColorRGBA tint) {
        ColorRGBA marbleColor = new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f).mult(tint);
        mat.setColor("BaseColor", marbleColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.2f);
    }
    
    private void setupGranite(Material mat, ColorRGBA tint) {
        ColorRGBA graniteColor = new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f).mult(tint);
        mat.setColor("BaseColor", graniteColor);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.7f);
    }
    
    private void setupCarbonFiber(Material mat, ColorRGBA tint) {
        ColorRGBA carbonColor = new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f).mult(tint);
        mat.setColor("BaseColor", carbonColor);
        mat.setFloat("Metallic", 0.8f);
        mat.setFloat("Roughness", 0.3f);
    }
    
    private void setupGold(Material mat) {
        mat.setColor("BaseColor", new ColorRGBA(1.0f, 0.8f, 0.0f, 1.0f));
        mat.setFloat("Metallic", 1.0f);
        mat.setFloat("Roughness", 0.1f);
    }
    
    private void setupSilver(Material mat) {
        mat.setColor("BaseColor", new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        mat.setFloat("Metallic", 1.0f);
        mat.setFloat("Roughness", 0.05f);
    }
    
    private void setupCopper(Material mat) {
        mat.setColor("BaseColor", new ColorRGBA(0.9f, 0.4f, 0.2f, 1.0f));
        mat.setFloat("Metallic", 1.0f);
        mat.setFloat("Roughness", 0.2f);
    }
    
    private void setupChrome(Material mat) {
        mat.setColor("BaseColor", new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f));
        mat.setFloat("Metallic", 1.0f);
        mat.setFloat("Roughness", 0.0f);
    }
    
    private void setupDefault(Material mat, ColorRGBA tint) {
        mat.setColor("BaseColor", tint);
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Roughness", 0.5f);
    }
    
    /**
     * Create a material with procedural noise for variation
     */
    public Material createNoisyMaterial(MaterialType baseType, ColorRGBA tint, float noiseScale) {
        Material mat = createPBRMaterial(baseType, tint);
        
        // Add some variation to roughness based on noise
        // This would require a custom shader or texture generation
        // For now, just add slight variation
        float baseRoughness = 0.5f;
        if (mat.getParam("Roughness") != null && mat.getParam("Roughness").getValue() instanceof Float) {
            baseRoughness = (Float) mat.getParam("Roughness").getValue();
        }
        
        float variation = (float) (Math.random() * 0.2f - 0.1f) * noiseScale;
        mat.setFloat("Roughness", Math.max(0.0f, Math.min(1.0f, baseRoughness + variation)));
        
        return mat;
    }
    
    /**
     * Create a weathered version of a material
     */
    public Material createWeatheredMaterial(MaterialType baseType, ColorRGBA tint, float weathering) {
        Material mat = createPBRMaterial(baseType, tint);
        
        // Increase roughness for weathering
        float baseRoughness = 0.5f;
        if (mat.getParam("Roughness") != null && mat.getParam("Roughness").getValue() instanceof Float) {
            baseRoughness = (Float) mat.getParam("Roughness").getValue();
        }
        
        float weatheredRoughness = Math.min(1.0f, baseRoughness + weathering * 0.5f);
        mat.setFloat("Roughness", weatheredRoughness);
        
        // Darken the color slightly
        ColorRGBA weatheredColor = tint.mult(1.0f - weathering * 0.3f);
        mat.setColor("BaseColor", weatheredColor);
        
        return mat;
    }
    
    /**
     * Create a material that changes properties over time (simplified version)
     */
    public Material createAnimatedMaterial(ColorRGBA baseColor, float metallic, float roughness) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", baseColor);
        mat.setFloat("Metallic", metallic);
        mat.setFloat("Roughness", roughness);
        
        return mat;
    }
    
    /**
     * Update animated material properties (simplified - just changes color)
     */
    public void updateAnimatedMaterial(Material mat, ColorRGBA baseColor, float time) {
        // Simple color animation
        float colorMod = 1.0f + 0.2f * (float) Math.sin(time * 2.0f);
        mat.setColor("BaseColor", baseColor.mult(colorMod));
    }
}