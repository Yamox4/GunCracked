package com.game;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Ground {
    private int gridVaoId, gridVboId, gridVertexCount;
    private int squareVaoId, squareVboId, squareEboId;
    private List<GridSquare> squares;
    
    private static class GridSquare {
        float x, z;
        Vector3f color;
        
        GridSquare(float x, float z, Vector3f color) {
            this.x = x;
            this.z = z;
            this.color = color;
        }
    }
    
    public Ground() {
        createGridAndSquares();
    }
    
    private void createGridAndSquares() {
        int gridSize = 100;
        float spacing = 2.0f;
        float halfGrid = gridSize * spacing * 0.5f;
        
        // Create Tron-style glowing white grid lines
        List<Float> gridVertices = new ArrayList<>();
        
        for (int i = 0; i <= gridSize; i++) {
            float pos = i * spacing - halfGrid;
            
            // Horizontal lines
            gridVertices.add(-halfGrid); // x1
            gridVertices.add(0.0f);      // y1
            gridVertices.add(pos);       // z1
            
            gridVertices.add(halfGrid);  // x2
            gridVertices.add(0.0f);      // y2
            gridVertices.add(pos);       // z2
            
            // Vertical lines
            gridVertices.add(pos);       // x1
            gridVertices.add(0.0f);      // y1
            gridVertices.add(-halfGrid); // z1
            
            gridVertices.add(pos);       // x2
            gridVertices.add(0.0f);      // y2
            gridVertices.add(halfGrid);  // z2
        }
        
        // Convert grid to array
        float[] gridArray = new float[gridVertices.size()];
        for (int i = 0; i < gridVertices.size(); i++) {
            gridArray[i] = gridVertices.get(i);
        }
        gridVertexCount = gridVertices.size() / 3;
        
        // Create grid VAO
        gridVaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(gridVaoId);
        
        gridVboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, gridVboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, gridArray, GL15.GL_STATIC_DRAW);
        
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
        
        GL30.glBindVertexArray(0);
        
        // No filled squares - just wireframe checkerboard
        squares = new ArrayList<>();
    }
    
    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        // Render glowing white Tron grid lines with multiple layers for glow effect
        GL30.glBindVertexArray(gridVaoId);
        
        Matrix4f modelMatrix = new Matrix4f().identity();
        Matrix4f mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        
        // Set thicker lines for Tron effect
        GL11.glLineWidth(3.0f);
        
        // Layer 1: Outer glow (thicker, more transparent)
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(0.8f, 0.9f, 1.0f).mul(0.3f)); // Soft white glow
        shader.setUniform("isWireframe", true);
        GL11.glDrawArrays(GL11.GL_LINES, 0, gridVertexCount);
        
        // Layer 2: Core lines (bright white)
        GL11.glLineWidth(2.0f);
        shader.setUniform("color", new Vector3f(1.0f, 1.0f, 1.0f).mul(0.9f)); // Bright white core
        GL11.glDrawArrays(GL11.GL_LINES, 0, gridVertexCount);
        
        // Layer 3: Inner core (brightest)
        GL11.glLineWidth(1.0f);
        shader.setUniform("color", new Vector3f(1.0f, 1.0f, 1.0f)); // Pure bright white
        GL11.glDrawArrays(GL11.GL_LINES, 0, gridVertexCount);
        
        // Reset line width
        GL11.glLineWidth(1.0f);
        GL30.glBindVertexArray(0);
    }
    
    public void cleanup() {
        GL15.glDeleteBuffers(gridVboId);
        GL30.glDeleteVertexArrays(gridVaoId);
        GL15.glDeleteBuffers(squareVboId);
        GL15.glDeleteBuffers(squareEboId);
        GL30.glDeleteVertexArrays(squareVaoId);
    }
}