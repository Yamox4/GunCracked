package com.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class UIRenderer {
    private int quadVaoId;
    private int quadVboId;
    private int quadEboId;
    
    // Simple 8x8 bitmap font for digits and letters
    private final int[][][] fontBitmaps = {
        // 0
        {{0,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,1,1,1},
         {1,1,0,0,1,0,1,1},
         {1,1,0,1,0,0,1,1},
         {1,1,1,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,0}},
        // 1
        {{0,0,0,1,1,0,0,0},
         {0,0,1,1,1,0,0,0},
         {0,1,0,1,1,0,0,0},
         {0,0,0,1,1,0,0,0},
         {0,0,0,1,1,0,0,0},
         {0,0,0,1,1,0,0,0},
         {0,0,0,1,1,0,0,0},
         {0,1,1,1,1,1,1,0}},
        // 2
        {{0,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {0,0,0,0,0,0,1,1},
         {0,0,0,0,0,1,1,0},
         {0,0,0,1,1,0,0,0},
         {0,0,1,1,0,0,0,0},
         {0,1,1,0,0,0,0,0},
         {1,1,1,1,1,1,1,1}},
        // 3
        {{0,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {0,0,0,0,0,0,1,1},
         {0,0,0,1,1,1,1,0},
         {0,0,0,0,0,0,1,1},
         {0,0,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,0}},
        // 4
        {{0,0,0,0,1,1,0,0},
         {0,0,0,1,1,1,0,0},
         {0,0,1,0,1,1,0,0},
         {0,1,0,0,1,1,0,0},
         {1,0,0,0,1,1,0,0},
         {1,1,1,1,1,1,1,1},
         {0,0,0,0,1,1,0,0},
         {0,0,0,0,1,1,0,0}},
        // 5
        {{1,1,1,1,1,1,1,1},
         {1,1,0,0,0,0,0,0},
         {1,1,0,0,0,0,0,0},
         {1,1,1,1,1,1,1,0},
         {0,0,0,0,0,0,1,1},
         {0,0,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,0}},
        // 6
        {{0,0,1,1,1,1,1,0},
         {0,1,1,0,0,0,0,0},
         {1,1,0,0,0,0,0,0},
         {1,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,0}},
        // 7
        {{1,1,1,1,1,1,1,1},
         {0,0,0,0,0,0,1,1},
         {0,0,0,0,0,1,1,0},
         {0,0,0,0,1,1,0,0},
         {0,0,0,1,1,0,0,0},
         {0,0,1,1,0,0,0,0},
         {0,1,1,0,0,0,0,0},
         {1,1,0,0,0,0,0,0}},
        // 8
        {{0,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,0}},
        // 9
        {{0,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,1},
         {0,0,0,0,0,0,1,1},
         {0,0,0,0,0,1,1,0},
         {0,1,1,1,1,1,0,0}},
        // : (colon) - index 10
        {{0,0,0,0,0,0,0,0},
         {0,0,0,1,1,0,0,0},
         {0,0,0,1,1,0,0,0},
         {0,0,0,0,0,0,0,0},
         {0,0,0,0,0,0,0,0},
         {0,0,0,1,1,0,0,0},
         {0,0,0,1,1,0,0,0},
         {0,0,0,0,0,0,0,0}},
        // P - index 11
        {{1,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,0,0},
         {1,1,0,0,0,0,0,0},
         {1,1,0,0,0,0,0,0},
         {1,1,0,0,0,0,0,0}},
        // A - index 12
        {{0,0,1,1,1,1,0,0},
         {0,1,1,0,0,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,1,1,1,1,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1}},
        // U - index 13
        {{1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,0}},
        // S - index 14
        {{0,1,1,1,1,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,0,0},
         {0,1,1,1,1,1,1,0},
         {0,0,0,0,0,0,1,1},
         {0,0,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {0,1,1,1,1,1,1,0}},
        // E - index 15
        {{1,1,1,1,1,1,1,1},
         {1,1,0,0,0,0,0,0},
         {1,1,0,0,0,0,0,0},
         {1,1,1,1,1,1,0,0},
         {1,1,0,0,0,0,0,0},
         {1,1,0,0,0,0,0,0},
         {1,1,0,0,0,0,0,0},
         {1,1,1,1,1,1,1,1}},
        // D - index 16
        {{1,1,1,1,1,1,0,0},
         {1,1,0,0,0,1,1,0},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,0,1,1},
         {1,1,0,0,0,1,1,0},
         {1,1,1,1,1,1,0,0}}
    };
    
    public UIRenderer() {
        createQuadGeometry();
    }
    
    private void createQuadGeometry() {
        float[] vertices = {
            0.0f, 0.0f, 0.0f,  0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f,  0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f,  0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f,  0.0f, 0.0f, 1.0f
        };
        
        int[] indices = {0, 1, 2, 2, 3, 0};
        
        quadVaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(quadVaoId);
        
        quadVboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        
        quadEboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, quadEboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
        
        GL30.glBindVertexArray(quadVaoId);
    }
    
    private void renderPixel(float x, float y, float size, Shader shader, Vector3f color) {
        Matrix4f modelMatrix = new Matrix4f().identity()
            .translate(x, y, 0.0f)
            .scale(size, size, 1.0f);
        
        Matrix4f mvpMatrix = new Matrix4f().identity().mul(modelMatrix);
        
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", color);
        shader.setUniform("isWireframe", false);
        
        GL30.glBindVertexArray(quadVaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }
    
    private void renderCharacter(int charIndex, float x, float y, float size, Shader shader, Vector3f color) {
        if (charIndex < 0 || charIndex >= fontBitmaps.length) return;
        
        int[][] bitmap = fontBitmaps[charIndex];
        float pixelSize = size / 8.0f;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (bitmap[row][col] == 1) {
                    float pixelX = x + col * pixelSize;
                    float pixelY = y + (7 - row) * pixelSize; // Flip Y coordinate
                    renderPixel(pixelX, pixelY, pixelSize, shader, color);
                }
            }
        }
    }
    
    public void renderTimer(float timeInSeconds, Shader shader, Matrix4f projectionMatrix) {
        // Convert time to minutes:seconds
        int totalSeconds = (int) timeInSeconds;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        
        // Setup 2D rendering
        Matrix4f orthoMatrix = new Matrix4f().ortho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        shader.setUniform("projectionMatrix", orthoMatrix);
        shader.setUniform("viewMatrix", new Matrix4f().identity());
        
        // Timer design - clean and minimal
        float startX = 0.55f;  // Moved more to the right
        float startY = 0.82f;  // Slightly lower
        float charSize = 0.06f; // Slightly bigger for better readability
        float spacing = 0.07f;  // Better spacing
        
        // Timer background panel
        float panelWidth = spacing * 4.5f + 0.04f;
        float panelHeight = charSize + 0.02f;
        float panelX = startX - 0.01f;
        float panelY = startY - 0.01f;
        
        // Render dark background
        Vector3f bgColor = new Vector3f(0.0f, 0.0f, 0.0f); // Black background
        renderPanel(panelX, panelY, panelWidth, panelHeight, shader, bgColor, false);
        
        // Render border
        Vector3f borderColor = new Vector3f(0.0f, 1.0f, 1.0f); // Cyan border
        renderPanelBorder(panelX, panelY, panelWidth, panelHeight, shader, borderColor);
        
        // Timer text color
        Vector3f timerColor = new Vector3f(1.0f, 1.0f, 1.0f); // White text
        
        // Render digits with better spacing
        renderCharacter(minutes / 10, startX, startY, charSize, shader, timerColor);
        renderCharacter(minutes % 10, startX + spacing, startY, charSize, shader, timerColor);
        renderCharacter(10, startX + spacing * 2, startY, charSize, shader, timerColor); // colon
        renderCharacter(seconds / 10, startX + spacing * 2.7f, startY, charSize, shader, timerColor);
        renderCharacter(seconds % 10, startX + spacing * 3.7f, startY, charSize, shader, timerColor);
        
        // Restore projection matrix
        shader.setUniform("projectionMatrix", projectionMatrix);
    }
    
    private void renderPanel(float x, float y, float width, float height, Shader shader, Vector3f color, boolean wireframe) {
        Matrix4f modelMatrix = new Matrix4f().identity()
            .translate(x + width/2, y + height/2, 0.0f)
            .scale(width/2, height/2, 1.0f);
        
        Matrix4f mvpMatrix = new Matrix4f().identity().mul(modelMatrix);
        
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", color);
        shader.setUniform("isWireframe", wireframe);
        
        GL30.glBindVertexArray(quadVaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }
    
    private void renderPanelBorder(float x, float y, float width, float height, Shader shader, Vector3f color) {
        float borderThickness = 0.001f; // Much thinner border
        
        // Top border
        renderPanel(x, y + height - borderThickness, width, borderThickness, shader, color, false);
        // Bottom border
        renderPanel(x, y, width, borderThickness, shader, color, false);
        // Left border
        renderPanel(x, y, borderThickness, height, shader, color, false);
        // Right border
        renderPanel(x + width - borderThickness, y, borderThickness, height, shader, color, false);
    }
    
    public void renderPauseOverlay(Shader shader, Matrix4f projectionMatrix) {
        // Setup 2D rendering
        Matrix4f orthoMatrix = new Matrix4f().ortho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        shader.setUniform("projectionMatrix", orthoMatrix);
        shader.setUniform("viewMatrix", new Matrix4f().identity());
        
        Vector3f pauseColor = new Vector3f(1.0f, 1.0f, 1.0f); // White
        
        // Render "PAUSED" text
        float charSize = 0.15f;
        float spacing = 0.18f;
        float startX = -0.5f;
        float startY = 0.0f;
        
        // P A U S E D
        renderCharacter(11, startX, startY, charSize, shader, pauseColor); // P
        renderCharacter(12, startX + spacing, startY, charSize, shader, pauseColor); // A
        renderCharacter(13, startX + spacing * 2, startY, charSize, shader, pauseColor); // U
        renderCharacter(14, startX + spacing * 3, startY, charSize, shader, pauseColor); // S
        renderCharacter(15, startX + spacing * 4, startY, charSize, shader, pauseColor); // E
        renderCharacter(16, startX + spacing * 5, startY, charSize, shader, pauseColor); // D
        
        // Restore projection matrix
        shader.setUniform("projectionMatrix", projectionMatrix);
    }
    
    public void cleanup() {
        GL15.glDeleteBuffers(quadVboId);
        GL15.glDeleteBuffers(quadEboId);
        GL30.glDeleteVertexArrays(quadVaoId);
    }
}