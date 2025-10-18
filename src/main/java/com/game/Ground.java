package com.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        
        // Create grid lines (black)
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
        
        // Create square geometry for filled squares
        float[] squareVertices = {
            -1.0f, 0.01f, -1.0f,  0.0f, 1.0f, 0.0f, // Slightly above ground
             1.0f, 0.01f, -1.0f,  0.0f, 1.0f, 0.0f,
             1.0f, 0.01f,  1.0f,  0.0f, 1.0f, 0.0f,
            -1.0f, 0.01f,  1.0f,  0.0f, 1.0f, 0.0f
        };
        
        int[] squareIndices = {0, 1, 2, 2, 3, 0};
        
        squareVaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(squareVaoId);
        
        squareVboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, squareVboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, squareVertices, GL15.GL_STATIC_DRAW);
        
        squareEboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, squareEboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, squareIndices, GL15.GL_STATIC_DRAW);
        
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
        
        GL30.glBindVertexArray(0);
        
        // Create all squares filled with white
        squares = new ArrayList<>();
        
        for (int x = -gridSize/2; x < gridSize/2; x++) {
            for (int z = -gridSize/2; z < gridSize/2; z++) {
                float posX = x * spacing;
                float posZ = z * spacing;
                
                // All squares are pure white
                Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f); // Pure white
                
                squares.add(new GridSquare(posX, posZ, color));
            }
        }
    }
    
    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        // Render black grid lines
        GL30.glBindVertexArray(gridVaoId);
        
        Matrix4f modelMatrix = new Matrix4f().identity();
        Matrix4f mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(0.0f, 0.0f, 0.0f)); // Black grid lines
        shader.setUniform("isWireframe", true);
        
        GL11.glDrawArrays(GL11.GL_LINES, 0, gridVertexCount);
        GL30.glBindVertexArray(0);
        
        // Render filled squares with black outlines
        GL30.glBindVertexArray(squareVaoId);
        
        for (GridSquare square : squares) {
            // Render white filled square
            Matrix4f squareMatrix = new Matrix4f().identity()
                .translate(square.x, 0.0f, square.z)
                .scale(0.9f, 1.0f, 0.9f); // Slightly smaller than grid cell
            Matrix4f squareMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(squareMatrix);
            
            shader.setUniform("mvpMatrix", squareMvp);
            shader.setUniform("modelMatrix", squareMatrix);
            shader.setUniform("color", square.color); // Pure white
            shader.setUniform("isWireframe", false);
            
            GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
            
            // Render black outline
            shader.setUniform("color", new Vector3f(0.0f, 0.0f, 0.0f)); // Black outline
            shader.setUniform("isWireframe", true);
            
            GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        }
        
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