package com.game;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Ground {
    private int vaoId;
    private int vboId;
    private int vertexCount;
    
    public Ground() {
        createCheckerboardGrid();
    }
    
    private void createCheckerboardGrid() {
        List<Float> vertices = new ArrayList<>();
        int gridSize = 200;
        float spacing = 1.0f;
        float halfGrid = gridSize * spacing * 0.5f;
        
        // Create grid lines
        for (int i = 0; i <= gridSize; i++) {
            float pos = i * spacing - halfGrid;
            
            // Horizontal lines
            vertices.add(-halfGrid); // x1
            vertices.add(0.0f);      // y1
            vertices.add(pos);       // z1
            
            vertices.add(halfGrid);  // x2
            vertices.add(0.0f);      // y2
            vertices.add(pos);       // z2
            
            // Vertical lines
            vertices.add(pos);       // x1
            vertices.add(0.0f);      // y1
            vertices.add(-halfGrid); // z1
            
            vertices.add(pos);       // x2
            vertices.add(0.0f);      // y2
            vertices.add(halfGrid);  // z2
        }
        
        // Convert to array
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        
        vertexCount = vertices.size() / 3;
        
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexArray, GL15.GL_STATIC_DRAW);
        
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
        
        GL30.glBindVertexArray(0);
    }
    
    public void render() {
        GL30.glBindVertexArray(vaoId);
        GL11.glDrawArrays(GL11.GL_LINES, 0, vertexCount);
        GL30.glBindVertexArray(0);
    }
    
    public void cleanup() {
        GL15.glDeleteBuffers(vboId);
        GL30.glDeleteVertexArrays(vaoId);
    }
}