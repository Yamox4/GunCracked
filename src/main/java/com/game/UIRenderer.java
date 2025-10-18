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
        
        // Timer outline panel (no background, just border)
        float panelWidth = spacing * 4.5f + 0.04f;
        float panelHeight = charSize + 0.02f;
        float panelX = startX - 0.01f;
        float panelY = startY - 0.01f;
        
        // Render only white outline border (no background)
        Vector3f borderColor = new Vector3f(1.0f, 1.0f, 1.0f); // Pure white border
        renderPanelBorder(panelX, panelY, panelWidth, panelHeight, shader, borderColor);
        
        // Timer text color - pure white
        Vector3f timerColor = new Vector3f(1.0f, 1.0f, 1.0f); // Pure white text
        
        // Render digits with better spacing
        renderCharacter(minutes / 10, startX, startY, charSize, shader, timerColor);
        renderCharacter(minutes % 10, startX + spacing, startY, charSize, shader, timerColor);
        renderCharacter(10, startX + spacing * 2, startY, charSize, shader, timerColor); // colon
        renderCharacter(seconds / 10, startX + spacing * 2.7f, startY, charSize, shader, timerColor);
        renderCharacter(seconds % 10, startX + spacing * 3.7f, startY, charSize, shader, timerColor);
        
        // Restore projection matrix
        shader.setUniform("projectionMatrix", projectionMatrix);
    }
    
    public void renderLevelBar(LevelSystem levelSystem, Shader shader, Matrix4f projectionMatrix) {
        // Setup 2D rendering
        Matrix4f orthoMatrix = new Matrix4f().ortho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        shader.setUniform("projectionMatrix", orthoMatrix);
        shader.setUniform("viewMatrix", new Matrix4f().identity());
        
        // Level bar at bottom of screen
        float barWidth = 1.6f; // 80% of screen width
        float barHeight = 0.08f;
        float barX = -0.8f; // Center horizontally
        float barY = -0.9f; // Bottom of screen
        
        Vector3f borderColor = new Vector3f(1.0f, 1.0f, 1.0f); // White border
        Vector3f bgColor = new Vector3f(0.2f, 0.2f, 0.2f); // Dark background
        Vector3f expColor = new Vector3f(1.0f, 0.8f, 0.0f); // Gold exp bar
        Vector3f textColor = new Vector3f(1.0f, 1.0f, 1.0f); // White text
        
        // Render background bar
        renderRect(barX, barY, barWidth, barHeight, bgColor, shader);
        
        // Render exp progress bar
        float progress = levelSystem.getLevelProgress();
        float progressWidth = barWidth * progress;
        renderRect(barX, barY, progressWidth, barHeight, expColor, shader);
        
        // Render border
        renderRectBorder(barX, barY, barWidth, barHeight, borderColor, shader);
        
        // Render level text (left side)
        float textSize = 0.04f;
        float textY = barY + (barHeight - textSize) / 2;
        renderText("LV " + levelSystem.getCurrentLevel(), barX + 0.05f, textY, textSize, textColor, shader);
        
        // Render exp text (center)
        String expText = levelSystem.getCurrentExp() + "/" + levelSystem.getExpToNextLevel();
        float expTextWidth = expText.length() * textSize * 0.6f;
        renderText(expText, -expTextWidth / 2, textY, textSize, textColor, shader);
        
        // Render exp remaining (right side)
        String remainingText = levelSystem.getExpRemaining() + " EXP";
        float remainingWidth = remainingText.length() * textSize * 0.6f;
        renderText(remainingText, barX + barWidth - remainingWidth - 0.05f, textY, textSize, textColor, shader);
        
        // Restore projection matrix
        shader.setUniform("projectionMatrix", projectionMatrix);
    }
    
    private void renderRect(float x, float y, float width, float height, Vector3f color, Shader shader) {
        // Simple rectangle rendering using lines (since we don't have filled quad rendering)
        for (float i = 0; i < height; i += 0.01f) {
            renderLine(x, y + i, x + width, y + i, color, shader);
        }
    }
    
    private void renderRectBorder(float x, float y, float width, float height, Vector3f color, Shader shader) {
        // Render rectangle border
        renderLine(x, y, x + width, y, color, shader); // Bottom
        renderLine(x, y + height, x + width, y + height, color, shader); // Top
        renderLine(x, y, x, y + height, color, shader); // Left
        renderLine(x + width, y, x + width, y + height, color, shader); // Right
    }
    
    private void renderLine(float x1, float y1, float x2, float y2, Vector3f color, Shader shader) {
        // Simple line rendering using pixel rendering
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        int steps = (int) (length * 1000); // Number of pixels to draw
        
        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;
            float x = x1 + dx * t;
            float y = y1 + dy * t;
            renderPixel(x, y, 0.002f, shader, color);
        }
    }
    
    private void renderText(String text, float x, float y, float size, Vector3f color, Shader shader) {
        float currentX = x;
        float spacing = size * 0.8f;
        
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                currentX += spacing;
                continue;
            }
            
            int charIndex = getCharIndex(c);
            if (charIndex >= 0) {
                renderCharacter(charIndex, currentX, y, size, shader, color);
            }
            currentX += spacing;
        }
    }

    public void renderDebugConsole(int fps, int enemyCount, float playerHealth, int enemiesKilled, int coinsCollected, Shader shader, Matrix4f projectionMatrix) {
        // Setup 2D rendering
        Matrix4f orthoMatrix = new Matrix4f().ortho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        shader.setUniform("projectionMatrix", orthoMatrix);
        shader.setUniform("viewMatrix", new Matrix4f().identity());
        
        Vector3f textColor = new Vector3f(0.0f, 1.0f, 1.0f); // Cyan text
        Vector3f coinColor = new Vector3f(1.0f, 0.9f, 0.3f); // Yellow/gold color for coins
        Vector3f borderColor = new Vector3f(1.0f, 1.0f, 1.0f); // White border
        
        float charSize = 0.04f;
        float lineHeight = 0.06f;
        float startX = -0.98f; // Top left corner
        float startY = 0.9f;
        
        // Debug panel border (made taller for coins)
        float panelWidth = 0.3f;
        float panelHeight = 0.31f;
        renderPanelBorder(startX - 0.01f, startY - panelHeight + 0.01f, panelWidth, panelHeight, shader, borderColor);
        
        // Render debug info
        renderDebugLine("FPS", fps, startX, startY, charSize, shader, textColor);
        renderDebugLine("ENEMIES", enemyCount, startX, startY - lineHeight, charSize, shader, textColor);
        renderDebugLine("HEALTH", (int)playerHealth, startX, startY - lineHeight * 2, charSize, shader, textColor);
        renderDebugLine("KILLED", enemiesKilled, startX, startY - lineHeight * 3, charSize, shader, textColor);
        renderDebugLine("COINS", coinsCollected, startX, startY - lineHeight * 4, charSize, shader, coinColor);
        
        // Restore projection matrix
        shader.setUniform("projectionMatrix", projectionMatrix);
    }
    
    private void renderDebugLine(String label, int value, float x, float y, float charSize, Shader shader, Vector3f color) {
        float spacing = charSize * 0.8f;
        float currentX = x;
        
        // Render label
        for (char c : label.toCharArray()) {
            int charIndex = getCharIndex(c);
            if (charIndex >= 0) {
                renderCharacter(charIndex, currentX, y, charSize, shader, color);
            }
            currentX += spacing;
        }
        
        // Render colon
        renderCharacter(10, currentX, y, charSize, shader, color); // colon
        currentX += spacing;
        
        // Render value
        String valueStr = String.valueOf(value);
        for (char c : valueStr.toCharArray()) {
            int charIndex = getCharIndex(c);
            if (charIndex >= 0) {
                renderCharacter(charIndex, currentX, y, charSize, shader, color);
            }
            currentX += spacing;
        }
    }
    
    private int getCharIndex(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0'; // 0-9
        }
        switch (c) {
            case ':': return 10;
            case 'P': return 11;
            case 'A': return 12;
            case 'U': return 13;
            case 'S': return 14;
            case 'E': return 15;
            case 'D': return 16;
            case 'F': return 11; // Use P for F
            case 'R': return 11; // Use P for R
            case 'N': return 11; // Use P for N
            case 'M': return 11; // Use P for M
            case 'I': return 1;  // Use 1 for I
            case 'L': return 1;  // Use 1 for L
            case 'T': return 7;  // Use 7 for T
            case 'H': return 11; // Use P for H
            case 'K': return 11; // Use P for K
            default: return -1;
        }
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