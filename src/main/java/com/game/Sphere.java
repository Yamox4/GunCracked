package com.game;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Sphere {
    private int vaoId;
    private int vboId;
    private int eboId;
    private int vertexCount;
    
    public Sphere() {
        createSphere();
    }
    
    private void createSphere() {
        // Simple icosphere approximation for performance
        float[] vertices = {
            // Top pyramid
             0.0f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f, // Top
             0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f, // Right
             0.0f,  0.0f,  0.5f,  0.0f,  0.0f,  1.0f, // Front
            -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f, // Left
             0.0f,  0.0f, -0.5f,  0.0f,  0.0f, -1.0f, // Back
             0.0f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f  // Bottom
        };
        
        int[] indices = {
            // Top pyramid
            0, 1, 2,  0, 2, 3,  0, 3, 4,  0, 4, 1,
            // Bottom pyramid  
            5, 2, 1,  5, 3, 2,  5, 4, 3,  5, 1, 4
        };
        
        vertexCount = indices.length;
        
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        
        eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        
        // Position attribute
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
        
        // Normal attribute
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
        
        GL30.glBindVertexArray(0);
    }
    
    public void render() {
        GL30.glBindVertexArray(vaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }
    
    public void cleanup() {
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
        GL30.glDeleteVertexArrays(vaoId);
    }
}