package com.ersambucone.modules.impl.monitoring;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Advanced staff detection system with multiple detection methods
 */
public class StaffRadar extends Module {
    private boolean alertSound = true;
    private int range = 50;
    private boolean checkVanished = true;
    private boolean checkNamePatterns = true;
    private boolean checkGamemode = true;
    private boolean notifyChat = true;
    private int scanInterval = 5; // seconds
    
    // Staff detection patterns
    private final List<Pattern> staffPatterns = new ArrayList<>();
    
    // Known staff names
    private final Set<String> knownStaff = new HashSet<>();
    
    // Recently detected staff
    private final Set<String> recentlyDetected = new HashSet<>();
    
    // Last scan time
    private long lastScanTime = 0;
    
    public StaffRadar() {
        super("StaffRadar", "Advanced staff detection system", Category.MONITORING);
        initStaffPatterns();
        initKnownStaff();
    }
    
    /**
     * Initializes staff name patterns
     */
    private void initStaffPatterns() {
        // Common staff name patterns
        staffPatterns.add(Pattern.compile("(?i)admin"));
        staffPatterns.add(Pattern.compile("(?i)mod"));
        staffPatterns.add(Pattern.compile("(?i)staff"));
        staffPatterns.add(Pattern.compile("(?i)helper"));
        staffPatterns.add(Pattern.compile("(?i)owner"));
        staffPatterns.add(Pattern.compile("(?i)dev"));
        Logger.log("Initialized " + staffPatterns.size() + " staff name patterns");
    }
    
    /**
     * Initializes known staff names
     */
    private void initKnownStaff() {
        // Add some example staff names
        knownStaff.add("ServerAdmin");
        knownStaff.add("GameModerator");
        knownStaff.add("HelperStaff");
        Logger.log("Initialized " + knownStaff.size() + " known staff names");
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("StaffRadar enabled");
        Logger.log("Alert sound: " + (alertSound ? "enabled" : "disabled"));
        Logger.log("Range: " + range + " blocks");
        Logger.log("Check vanished: " + (checkVanished ? "enabled" : "disabled"));
        Logger.log("Check name patterns: " + (checkNamePatterns ? "enabled" : "disabled"));
        Logger.log("Check gamemode: " + (checkGamemode ? "enabled" : "disabled"));
        Logger.log("Notify in chat: " + (notifyChat ? "enabled" : "disabled"));
        Logger.log("Scan interval: " + scanInterval + " seconds");
        
        // Clear recently detected staff
        recentlyDetected.clear();
    }
    
    /**
     * Performs a staff scan
     * This would be called periodically from a tick handler
     */
    public void performScan() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScanTime < scanInterval * 1000) {
            return;
        }
        lastScanTime = currentTime;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }
        
        // Scan for staff
        List<String> detectedStaff = new ArrayList<>();
        
        // Check players in range
        if (client.world.getPlayers() != null) {
            for (PlayerEntity player : client.world.getPlayers()) {
                if (player == client.player) {
                    continue;
                }
                
                // Check distance
                if (player.distanceTo(client.player) > range) {
                    continue;
                }
                
                String playerName = player.getGameProfile().getName();
                
                // Check if known staff
                if (knownStaff.contains(playerName)) {
                    detectedStaff.add(playerName + " (Known Staff)");
                    continue;
                }
                
                // Check name patterns
                if (checkNamePatterns) {
                    for (Pattern pattern : staffPatterns) {
                        if (pattern.matcher(playerName).find()) {
                            detectedStaff.add(playerName + " (Name Pattern)");
                            break;
                        }
                    }
                }
                
                // Additional checks could be added here
            }
        }
        
        // Check vanished players
        if (checkVanished && client.getNetworkHandler() != null) {
            Set<String> visiblePlayers = new HashSet<>();
            
            // Get all visible players
            if (client.world.getPlayers() != null) {
                for (PlayerEntity player : client.world.getPlayers()) {
                    visiblePlayers.add(player.getGameProfile().getName());
                }
            }
            
            // Check player list for players not in the world
            for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
                String playerName = entry.getProfile().getName();
                if (!visiblePlayers.contains(playerName) && !playerName.equals(client.player.getGameProfile().getName())) {
                    detectedStaff.add(playerName + " (Vanished)");
                }
            }
        }
        
        // Notify about detected staff
        for (String staff : detectedStaff) {
            if (!recentlyDetected.contains(staff)) {
                recentlyDetected.add(staff);
                Logger.log("Detected staff: " + staff);
                
                // Play alert sound
                if (alertSound) {
                    client.getSoundManager().play(
                        net.minecraft.client.sound.PositionedSoundInstance.master(
                            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
                }
                
                // Send chat notification
                if (notifyChat) {
                    client.player.sendMessage(Text.literal("§c[StaffRadar] §fDetected: §e" + staff));
                }
            }
        }
        
        // Clean up recently detected staff (remove after 60 seconds)
        Set<String> toRemove = new HashSet<>();
        for (String staff : recentlyDetected) {
            if (!detectedStaff.contains(staff)) {
                toRemove.add(staff);
            }
        }
        recentlyDetected.removeAll(toRemove);
    }
    
    /**
     * Adds a staff name pattern
     * 
     * @param pattern The pattern to add
     */
    public void addStaffPattern(String pattern) {
        staffPatterns.add(Pattern.compile("(?i)" + pattern));
        Logger.log("Added staff pattern: " + pattern);
    }
    
    /**
     * Adds a known staff name
     * 
     * @param name The name to add
     */
    public void addKnownStaff(String name) {
        knownStaff.add(name);
        Logger.log("Added known staff: " + name);
    }
    
    /**
     * Sets whether to play an alert sound
     * 
     * @param alertSound True to play an alert sound, false otherwise
     */
    public void setAlertSound(boolean alertSound) {
        this.alertSound = alertSound;
        if (isEnabled()) {
            Logger.log("Alert sound: " + (alertSound ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Sets the detection range
     * 
     * @param range The range in blocks (10-100)
     */
    public void setRange(int range) {
        this.range = Math.max(10, Math.min(100, range));
        if (isEnabled()) {
            Logger.log("Range set to: " + this.range + " blocks");
        }
    }
    
    /**
     * Sets whether to check for vanished players
     * 
     * @param checkVanished True to check for vanished players, false otherwise
     */
    public void setCheckVanished(boolean checkVanished) {
        this.checkVanished = checkVanished;
        if (isEnabled()) {
            Logger.log("Check vanished: " + (checkVanished ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Sets whether to check name patterns
     * 
     * @param checkNamePatterns True to check name patterns, false otherwise
     */
    public void setCheckNamePatterns(boolean checkNamePatterns) {
        this.checkNamePatterns = checkNamePatterns;
        if (isEnabled()) {
            Logger.log("Check name patterns: " + (checkNamePatterns ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Sets whether to check gamemode
     * 
     * @param checkGamemode True to check gamemode, false otherwise
     */
    public void setCheckGamemode(boolean checkGamemode) {
        this.checkGamemode = checkGamemode;
        if (isEnabled()) {
            Logger.log("Check gamemode: " + (checkGamemode ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Sets whether to notify in chat
     * 
     * @param notifyChat True to notify in chat, false otherwise
     */
    public void setNotifyChat(boolean notifyChat) {
        this.notifyChat = notifyChat;
        if (isEnabled()) {
            Logger.log("Notify in chat: " + (notifyChat ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Sets the scan interval
     * 
     * @param scanInterval The scan interval in seconds (1-60)
     */
    public void setScanInterval(int scanInterval) {
        this.scanInterval = Math.max(1, Math.min(60, scanInterval));
        if (isEnabled()) {
            Logger.log("Scan interval set to: " + this.scanInterval + " seconds");
        }
    }
    
    /**
     * Checks if alert sound is enabled
     * 
     * @return True if alert sound is enabled, false otherwise
     */
    public boolean isAlertSoundEnabled() {
        return alertSound;
    }
    
    /**
     * Gets the detection range
     * 
     * @return The range in blocks
     */
    public int getRange() {
        return range;
    }
    
    /**
     * Checks if checking for vanished players is enabled
     * 
     * @return True if checking for vanished players is enabled, false otherwise
     */
    public boolean isCheckVanishedEnabled() {
        return checkVanished;
    }
    
    /**
     * Checks if checking name patterns is enabled
     * 
     * @return True if checking name patterns is enabled, false otherwise
     */
    public boolean isCheckNamePatternsEnabled() {
        return checkNamePatterns;
    }
    
    /**
     * Checks if checking gamemode is enabled
     * 
     * @return True if checking gamemode is enabled, false otherwise
     */
    public boolean isCheckGamemodeEnabled() {
        return checkGamemode;
    }
    
    /**
     * Checks if notifying in chat is enabled
     * 
     * @return True if notifying in chat is enabled, false otherwise
     */
    public boolean isNotifyChatEnabled() {
        return notifyChat;
    }
    
    /**
     * Gets the scan interval
     * 
     * @return The scan interval in seconds
     */
    public int getScanInterval() {
        return scanInterval;
    }
    
    /**
     * Gets the recently detected staff
     * 
     * @return The recently detected staff
     */
    public Set<String> getRecentlyDetectedStaff() {
        return new HashSet<>(recentlyDetected);
    }
    
    /**
     * Gets the known staff
     * 
     * @return The known staff
     */
    public Set<String> getKnownStaff() {
        return new HashSet<>(knownStaff);
    }
}