package com.game;

public class LevelSystem {
    private int currentLevel;
    private int currentExp;
    private int expToNextLevel;
    private int totalExpEarned;
    
    public LevelSystem() {
        this.currentLevel = 1;
        this.currentExp = 0;
        this.expToNextLevel = calculateExpForLevel(2); // Exp needed for level 2
        this.totalExpEarned = 0;
    }
    
    // Calculate total exp needed to reach a specific level
    private int calculateExpForLevel(int level) {
        if (level <= 1) return 0;
        
        // Progressive exp requirement: level 2 = 10, level 3 = 25, level 4 = 45, etc.
        // Formula: sum of (level * 5 + 5) for each level
        int totalExp = 0;
        for (int i = 2; i <= level; i++) {
            totalExp += (i * 5 + 5); // Level 2: 15, Level 3: 20, Level 4: 25, etc.
        }
        return totalExp;
    }
    
    // Add experience points (1 per coin)
    public boolean addExp(int exp) {
        currentExp += exp;
        totalExpEarned += exp;
        
        boolean leveledUp = false;
        
        // Check for level up
        while (currentExp >= expToNextLevel) {
            currentExp -= expToNextLevel;
            currentLevel++;
            leveledUp = true;
            
            // Calculate exp needed for next level
            int nextLevelTotalExp = calculateExpForLevel(currentLevel + 1);
            int currentLevelTotalExp = calculateExpForLevel(currentLevel);
            expToNextLevel = nextLevelTotalExp - currentLevelTotalExp;
            
            System.out.println("🎉 LEVEL UP! 🎉 Now level " + currentLevel);
        }
        
        return leveledUp;
    }
    
    // Get current level
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    // Get current exp in this level
    public int getCurrentExp() {
        return currentExp;
    }
    
    // Get exp needed for next level
    public int getExpToNextLevel() {
        return expToNextLevel;
    }
    
    // Get progress as percentage (0.0 to 1.0)
    public float getLevelProgress() {
        return (float) currentExp / (float) expToNextLevel;
    }
    
    // Get total exp earned
    public int getTotalExpEarned() {
        return totalExpEarned;
    }
    
    // Get exp remaining to next level
    public int getExpRemaining() {
        return expToNextLevel - currentExp;
    }
    
    // Debug info
    public String getDebugInfo() {
        return String.format("Level %d | %d/%d EXP (%.1f%%) | Total: %d", 
            currentLevel, currentExp, expToNextLevel, getLevelProgress() * 100, totalExpEarned);
    }
}