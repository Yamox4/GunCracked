package com.physics3d.debug;

/**
 * Configuration class for debug and visualization settings. Contains settings
 * for debug rendering, profiling, and visualization options.
 */
public class DebugConfiguration {

    private boolean enableWireframeRendering = false;
    private boolean enableContactPointVisualization = false;
    private boolean enableForceVectorVisualization = false;
    private boolean enableBoundingBoxVisualization = false;
    private boolean enableProfiling = false;
    private boolean enableStatistics = false;
    private float wireframeLineWidth = 1.0f;
    private float contactPointSize = 0.1f;
    private float forceVectorScale = 1.0f;

    /**
     * Creates a default debug configuration.
     */
    public DebugConfiguration() {
        // Default values are set in field declarations
    }

    /**
     * Checks if wireframe rendering is enabled.
     *
     * @return true if wireframe rendering is enabled
     */
    public boolean isEnableWireframeRendering() {
        return enableWireframeRendering;
    }

    /**
     * Enables or disables wireframe rendering.
     *
     * @param enableWireframeRendering true to enable wireframe rendering
     * @return This configuration for method chaining
     */
    public DebugConfiguration setEnableWireframeRendering(boolean enableWireframeRendering) {
        this.enableWireframeRendering = enableWireframeRendering;
        return this;
    }

    /**
     * Checks if contact point visualization is enabled.
     *
     * @return true if contact point visualization is enabled
     */
    public boolean isEnableContactPointVisualization() {
        return enableContactPointVisualization;
    }

    /**
     * Enables or disables contact point visualization.
     *
     * @param enableContactPointVisualization true to enable contact point
     * visualization
     * @return This configuration for method chaining
     */
    public DebugConfiguration setEnableContactPointVisualization(boolean enableContactPointVisualization) {
        this.enableContactPointVisualization = enableContactPointVisualization;
        return this;
    }

    /**
     * Checks if force vector visualization is enabled.
     *
     * @return true if force vector visualization is enabled
     */
    public boolean isEnableForceVectorVisualization() {
        return enableForceVectorVisualization;
    }

    /**
     * Enables or disables force vector visualization.
     *
     * @param enableForceVectorVisualization true to enable force vector
     * visualization
     * @return This configuration for method chaining
     */
    public DebugConfiguration setEnableForceVectorVisualization(boolean enableForceVectorVisualization) {
        this.enableForceVectorVisualization = enableForceVectorVisualization;
        return this;
    }

    /**
     * Checks if bounding box visualization is enabled.
     *
     * @return true if bounding box visualization is enabled
     */
    public boolean isEnableBoundingBoxVisualization() {
        return enableBoundingBoxVisualization;
    }

    /**
     * Enables or disables bounding box visualization.
     *
     * @param enableBoundingBoxVisualization true to enable bounding box
     * visualization
     * @return This configuration for method chaining
     */
    public DebugConfiguration setEnableBoundingBoxVisualization(boolean enableBoundingBoxVisualization) {
        this.enableBoundingBoxVisualization = enableBoundingBoxVisualization;
        return this;
    }

    /**
     * Checks if profiling is enabled.
     *
     * @return true if profiling is enabled
     */
    public boolean isEnableProfiling() {
        return enableProfiling;
    }

    /**
     * Enables or disables profiling.
     *
     * @param enableProfiling true to enable profiling
     * @return This configuration for method chaining
     */
    public DebugConfiguration setEnableProfiling(boolean enableProfiling) {
        this.enableProfiling = enableProfiling;
        return this;
    }

    /**
     * Checks if statistics collection is enabled.
     *
     * @return true if statistics collection is enabled
     */
    public boolean isEnableStatistics() {
        return enableStatistics;
    }

    /**
     * Enables or disables statistics collection.
     *
     * @param enableStatistics true to enable statistics collection
     * @return This configuration for method chaining
     */
    public DebugConfiguration setEnableStatistics(boolean enableStatistics) {
        this.enableStatistics = enableStatistics;
        return this;
    }

    /**
     * Gets the wireframe line width.
     *
     * @return The wireframe line width
     */
    public float getWireframeLineWidth() {
        return wireframeLineWidth;
    }

    /**
     * Sets the wireframe line width.
     *
     * @param wireframeLineWidth The wireframe line width
     * @return This configuration for method chaining
     */
    public DebugConfiguration setWireframeLineWidth(float wireframeLineWidth) {
        this.wireframeLineWidth = wireframeLineWidth;
        return this;
    }

    /**
     * Gets the contact point visualization size.
     *
     * @return The contact point size
     */
    public float getContactPointSize() {
        return contactPointSize;
    }

    /**
     * Sets the contact point visualization size.
     *
     * @param contactPointSize The contact point size
     * @return This configuration for method chaining
     */
    public DebugConfiguration setContactPointSize(float contactPointSize) {
        this.contactPointSize = contactPointSize;
        return this;
    }

    /**
     * Gets the force vector visualization scale.
     *
     * @return The force vector scale
     */
    public float getForceVectorScale() {
        return forceVectorScale;
    }

    /**
     * Sets the force vector visualization scale.
     *
     * @param forceVectorScale The force vector scale
     * @return This configuration for method chaining
     */
    public DebugConfiguration setForceVectorScale(float forceVectorScale) {
        this.forceVectorScale = forceVectorScale;
        return this;
    }
}
