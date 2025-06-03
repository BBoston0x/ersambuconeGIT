package com.ersambucone.modules.impl.automation;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;
import net.minecraft.client.MinecraftClient;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Advanced autoclicker with customizable settings and patterns
 */
public class SmartClicker extends Module {
    private int cps = 8;
    private boolean randomizeCps = true;
    private ClickPattern pattern = ClickPattern.NORMAL;
    private ClickButton button = ClickButton.LEFT;
    private boolean breakBlocks = true;
    private boolean attackEntities = true;
    private int minCps = 6;
    private int maxCps = 10;
    
    private final Random random = new Random();
    private ScheduledExecutorService executor;
    private long lastClickTime = 0;
    private int clickCounter = 0;
    private final int MAX_BURST_CLICKS = 3;
    
    public SmartClicker() {
        super("SmartClicker", "Advanced autoclicker with customizable settings", Category.AUTOMATION);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("SmartClicker enabled");
        Logger.log("CPS: " + cps + " (" + minCps + "-" + maxCps + ")");
        Logger.log("Randomize CPS: " + (randomizeCps ? "enabled" : "disabled"));
        Logger.log("Pattern: " + pattern);
        Logger.log("Button: " + button);
        
        // Start the clicker thread
        startClicker();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        // Stop the clicker thread
        stopClicker();
        Logger.log("SmartClicker disabled");
    }
    
    /**
     * Starts the clicker thread
     */
    private void startClicker() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        
        executor = Executors.newSingleThreadScheduledExecutor();
        
        // Schedule the clicking task
        executor.scheduleAtFixedRate(() -> {
            try {
                if (!isEnabled()) {
                    stopClicker();
                    return;
                }
                
                // Check if the game is in focus
                if (MinecraftClient.getInstance().isWindowFocused()) {
                    performClick();
                }
            } catch (Exception e) {
                Logger.error("Error in SmartClicker: " + e.getMessage());
            }
        }, 0, 50, TimeUnit.MILLISECONDS); // Check every 50ms
    }
    
    /**
     * Stops the clicker thread
     */
    private void stopClicker() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Performs a click based on the current settings
     */
    private void performClick() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastClick = currentTime - lastClickTime;
        
        // Calculate the delay between clicks
        int currentCps = cps;
        if (randomizeCps) {
            currentCps = minCps + random.nextInt(maxCps - minCps + 1);
        }
        
        long delay = 1000 / currentCps;
        
        // Apply pattern modifications
        switch (pattern) {
            case JITTER:
                // Add some jitter to the delay
                delay += random.nextInt(50) - 25;
                break;
            case BURST:
                // Burst clicks (multiple clicks in quick succession, then pause)
                if (clickCounter < MAX_BURST_CLICKS) {
                    delay = 50 + random.nextInt(30);
                } else {
                    delay = 1000 + random.nextInt(500);
                    clickCounter = 0;
                }
                break;
            case BUTTERFLY:
                // Butterfly pattern (alternating fast and slow clicks)
                if (clickCounter % 2 == 0) {
                    delay = 1000 / (currentCps + 2);
                } else {
                    delay = 1000 / (currentCps - 1);
                }
                break;
            case NORMAL:
            default:
                // Add slight randomization to normal pattern
                delay += random.nextInt(20) - 10;
                break;
        }
        
        // Ensure delay is not negative
        delay = Math.max(50, delay);
        
        // Check if it's time to click
        if (timeSinceLastClick >= delay) {
            // Perform the click
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.world != null) {
                // Check conditions for clicking
                boolean shouldClick = true;
                
                if (button == ClickButton.LEFT) {
                    // Left click logic
                    if (!breakBlocks && client.crosshairTarget != null && 
                            client.crosshairTarget.getType().toString().equals("BLOCK")) {
                        shouldClick = false;
                    }
                    
                    if (!attackEntities && client.crosshairTarget != null && 
                            client.crosshairTarget.getType().toString().equals("ENTITY")) {
                        shouldClick = false;
                    }
                    
                    if (shouldClick) {
                        // Simulate left click
                        // In a real implementation, this would use the Minecraft input system
                        Logger.debug("Left click");
                    }
                } else {
                    // Right click logic
                    // Simulate right click
                    Logger.debug("Right click");
                }
                
                lastClickTime = currentTime;
                clickCounter++;
            }
        }
    }
    
    /**
     * Sets the clicks per second
     * 
     * @param cps The clicks per second (1-20)
     */
    public void setCps(int cps) {
        this.cps = Math.max(1, Math.min(20, cps));
        if (isEnabled()) {
            Logger.log("CPS set to: " + this.cps);
        }
    }
    
    /**
     * Sets the minimum CPS for randomization
     * 
     * @param minCps The minimum CPS (1-19)
     */
    public void setMinCps(int minCps) {
        this.minCps = Math.max(1, Math.min(19, minCps));
        if (this.minCps >= this.maxCps) {
            this.maxCps = this.minCps + 1;
        }
        if (isEnabled()) {
            Logger.log("Min CPS set to: " + this.minCps);
            Logger.log("CPS range: " + this.minCps + "-" + this.maxCps);
        }
    }
    
    /**
     * Sets the maximum CPS for randomization
     * 
     * @param maxCps The maximum CPS (2-20)
     */
    public void setMaxCps(int maxCps) {
        this.maxCps = Math.max(2, Math.min(20, maxCps));
        if (this.maxCps <= this.minCps) {
            this.minCps = this.maxCps - 1;
        }
        if (isEnabled()) {
            Logger.log("Max CPS set to: " + this.maxCps);
            Logger.log("CPS range: " + this.minCps + "-" + this.maxCps);
        }
    }
    
    /**
     * Sets whether to randomize the CPS
     * 
     * @param randomizeCps True to randomize, false otherwise
     */
    public void setRandomizeCps(boolean randomizeCps) {
        this.randomizeCps = randomizeCps;
        if (isEnabled()) {
            Logger.log("Randomize CPS: " + (randomizeCps ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Sets the click pattern
     * 
     * @param pattern The click pattern
     */
    public void setPattern(ClickPattern pattern) {
        this.pattern = pattern;
        if (isEnabled()) {
            Logger.log("Pattern set to: " + pattern);
        }
    }
    
    /**
     * Sets the click button
     * 
     * @param button The click button
     */
    public void setButton(ClickButton button) {
        this.button = button;
        if (isEnabled()) {
            Logger.log("Button set to: " + button);
        }
    }
    
    /**
     * Sets whether to break blocks
     * 
     * @param breakBlocks True to break blocks, false otherwise
     */
    public void setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
        if (isEnabled()) {
            Logger.log("Break blocks: " + (breakBlocks ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Sets whether to attack entities
     * 
     * @param attackEntities True to attack entities, false otherwise
     */
    public void setAttackEntities(boolean attackEntities) {
        this.attackEntities = attackEntities;
        if (isEnabled()) {
            Logger.log("Attack entities: " + (attackEntities ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Gets the clicks per second
     * 
     * @return The clicks per second
     */
    public int getCps() {
        return cps;
    }
    
    /**
     * Gets the minimum CPS for randomization
     * 
     * @return The minimum CPS
     */
    public int getMinCps() {
        return minCps;
    }
    
    /**
     * Gets the maximum CPS for randomization
     * 
     * @return The maximum CPS
     */
    public int getMaxCps() {
        return maxCps;
    }
    
    /**
     * Checks if CPS randomization is enabled
     * 
     * @return True if CPS randomization is enabled, false otherwise
     */
    public boolean isRandomizeCpsEnabled() {
        return randomizeCps;
    }
    
    /**
     * Gets the click pattern
     * 
     * @return The click pattern
     */
    public ClickPattern getPattern() {
        return pattern;
    }
    
    /**
     * Gets the click button
     * 
     * @return The click button
     */
    public ClickButton getButton() {
        return button;
    }
    
    /**
     * Checks if breaking blocks is enabled
     * 
     * @return True if breaking blocks is enabled, false otherwise
     */
    public boolean isBreakBlocksEnabled() {
        return breakBlocks;
    }
    
    /**
     * Checks if attacking entities is enabled
     * 
     * @return True if attacking entities is enabled, false otherwise
     */
    public boolean isAttackEntitiesEnabled() {
        return attackEntities;
    }
    
    /**
     * Click patterns
     */
    public enum ClickPattern {
        NORMAL,
        JITTER,
        BURST,
        BUTTERFLY
    }
    
    /**
     * Click buttons
     */
    public enum ClickButton {
        LEFT,
        RIGHT
    }
}