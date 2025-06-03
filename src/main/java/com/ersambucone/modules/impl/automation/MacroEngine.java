package com.ersambucone.modules.impl.automation;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Advanced macro system for farming automation with path recording and profit tracking
 */
public class MacroEngine extends Module {
    private int repeatCount = 10;
    private int delay = 200;
    private boolean recording = false;
    private boolean playing = false;
    private String currentMacro = "";
    private final Map<String, List<MacroAction>> macros = new HashMap<>();
    private final Map<String, Integer> profits = new HashMap<>();
    private long startTime = 0;
    private long totalRuntime = 0;
    
    // Path recording
    private final List<Vec3d> recordedPath = new ArrayList<>();
    private boolean pathRecording = false;
    private int pathIndex = 0;
    
    public MacroEngine() {
        super("MacroEngine", "Advanced macro system for farming automation", Category.AUTOMATION);
        loadDefaultMacros();
    }
    
    /**
     * Loads default macros from configuration
     */
    private void loadDefaultMacros() {
        // Example macro for demonstration
        List<MacroAction> farmingActions = new ArrayList<>();
        farmingActions.add(new MacroAction(MacroActionType.MOVE_FORWARD, 1000));
        farmingActions.add(new MacroAction(MacroActionType.CLICK, 200));
        farmingActions.add(new MacroAction(MacroActionType.MOVE_RIGHT, 500));
        farmingActions.add(new MacroAction(MacroActionType.CLICK, 200));
        farmingActions.add(new MacroAction(MacroActionType.MOVE_BACKWARD, 1000));
        farmingActions.add(new MacroAction(MacroActionType.CLICK, 200));
        farmingActions.add(new MacroAction(MacroActionType.MOVE_LEFT, 500));
        farmingActions.add(new MacroAction(MacroActionType.CLICK, 200));
        macros.put("farming", farmingActions);
        
        Logger.log("Loaded default macros");
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("MacroEngine enabled");
        Logger.log("Repeat count: " + repeatCount);
        Logger.log("Delay: " + delay + "ms");
        Logger.log("Available macros: " + String.join(", ", macros.keySet()));
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        stopRecording();
        stopPlaying();
        Logger.log("MacroEngine disabled");
    }
    
    /**
     * Starts recording a new macro
     * 
     * @param name The macro name
     * @return True if recording started, false if already recording
     */
    public boolean startRecording(String name) {
        if (recording) {
            return false;
        }
        
        recording = true;
        currentMacro = name;
        macros.put(name, new ArrayList<>());
        startTime = System.currentTimeMillis();
        Logger.log("Started recording macro: " + name);
        return true;
    }
    
    /**
     * Stops recording the current macro
     * 
     * @return True if recording stopped, false if not recording
     */
    public boolean stopRecording() {
        if (!recording) {
            return false;
        }
        
        recording = false;
        totalRuntime = System.currentTimeMillis() - startTime;
        Logger.log("Stopped recording macro: " + currentMacro);
        Logger.log("Recorded " + macros.get(currentMacro).size() + " actions");
        Logger.log("Total runtime: " + (totalRuntime / 1000) + " seconds");
        currentMacro = "";
        return true;
    }
    
    /**
     * Starts playing a macro
     * 
     * @param name The macro name
     * @return True if playing started, false if already playing or macro not found
     */
    public boolean startPlaying(String name) {
        if (playing || !macros.containsKey(name)) {
            return false;
        }
        
        playing = true;
        currentMacro = name;
        startTime = System.currentTimeMillis();
        Logger.log("Started playing macro: " + name);
        return true;
    }
    
    /**
     * Stops playing the current macro
     * 
     * @return True if playing stopped, false if not playing
     */
    public boolean stopPlaying() {
        if (!playing) {
            return false;
        }
        
        playing = false;
        totalRuntime = System.currentTimeMillis() - startTime;
        Logger.log("Stopped playing macro: " + currentMacro);
        Logger.log("Total runtime: " + (totalRuntime / 1000) + " seconds");
        currentMacro = "";
        return true;
    }
    
    /**
     * Starts recording a path
     * 
     * @return True if path recording started, false if already recording
     */
    public boolean startPathRecording() {
        if (pathRecording) {
            return false;
        }
        
        pathRecording = true;
        recordedPath.clear();
        Logger.log("Started recording path");
        return true;
    }
    
    /**
     * Stops recording a path
     * 
     * @return True if path recording stopped, false if not recording
     */
    public boolean stopPathRecording() {
        if (!pathRecording) {
            return false;
        }
        
        pathRecording = false;
        Logger.log("Stopped recording path");
        Logger.log("Recorded " + recordedPath.size() + " path points");
        return true;
    }
    
    /**
     * Records the current position in the path
     */
    public void recordPathPoint() {
        if (!pathRecording || MinecraftClient.getInstance().player == null) {
            return;
        }
        
        Vec3d pos = MinecraftClient.getInstance().player.getPos();
        recordedPath.add(pos);
        Logger.log("Recorded path point: " + pos.x + ", " + pos.y + ", " + pos.z);
    }
    
    /**
     * Adds a profit entry
     * 
     * @param item The item name
     * @param amount The amount
     */
    public void addProfit(String item, int amount) {
        profits.put(item, profits.getOrDefault(item, 0) + amount);
        Logger.log("Added profit: " + amount + " " + item);
        Logger.log("Total " + item + ": " + profits.get(item));
    }
    
    /**
     * Gets the total profit for an item
     * 
     * @param item The item name
     * @return The total profit
     */
    public int getProfit(String item) {
        return profits.getOrDefault(item, 0);
    }
    
    /**
     * Gets all profits
     * 
     * @return A map of item names to profits
     */
    public Map<String, Integer> getProfits() {
        return new HashMap<>(profits);
    }
    
    /**
     * Clears all profits
     */
    public void clearProfits() {
        profits.clear();
        Logger.log("Cleared all profits");
    }
    
    /**
     * Sets the repeat count
     * 
     * @param repeatCount The repeat count (1-100)
     */
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = Math.max(1, Math.min(100, repeatCount));
        if (isEnabled()) {
            Logger.log("Repeat count set to: " + this.repeatCount);
        }
    }
    
    /**
     * Sets the delay
     * 
     * @param delay The delay in milliseconds (50-1000)
     */
    public void setDelay(int delay) {
        this.delay = Math.max(50, Math.min(1000, delay));
        if (isEnabled()) {
            Logger.log("Delay set to: " + this.delay + "ms");
        }
    }
    
    /**
     * Gets the repeat count
     * 
     * @return The repeat count
     */
    public int getRepeatCount() {
        return repeatCount;
    }
    
    /**
     * Gets the delay
     * 
     * @return The delay in milliseconds
     */
    public int getDelay() {
        return delay;
    }
    
    /**
     * Checks if recording is in progress
     * 
     * @return True if recording, false otherwise
     */
    public boolean isRecording() {
        return recording;
    }
    
    /**
     * Checks if playing is in progress
     * 
     * @return True if playing, false otherwise
     */
    public boolean isPlaying() {
        return playing;
    }
    
    /**
     * Checks if path recording is in progress
     * 
     * @return True if path recording, false otherwise
     */
    public boolean isPathRecording() {
        return pathRecording;
    }
    
    /**
     * Gets the current macro name
     * 
     * @return The current macro name
     */
    public String getCurrentMacro() {
        return currentMacro;
    }
    
    /**
     * Gets all macro names
     * 
     * @return A list of macro names
     */
    public List<String> getMacroNames() {
        return new ArrayList<>(macros.keySet());
    }
    
    /**
     * Gets the total runtime
     * 
     * @return The total runtime in milliseconds
     */
    public long getTotalRuntime() {
        if (recording || playing) {
            return System.currentTimeMillis() - startTime;
        }
        return totalRuntime;
    }
    
    /**
     * Gets the recorded path
     * 
     * @return The recorded path
     */
    public List<Vec3d> getRecordedPath() {
        return new ArrayList<>(recordedPath);
    }
    
    /**
     * Checks if the module is active (recording or playing)
     * 
     * @return True if active, false otherwise
     */
    public boolean isActive() {
        return recording || playing;
    }
    
    /**
     * Macro action types
     */
    public enum MacroActionType {
        CLICK,
        RIGHT_CLICK,
        MOVE_FORWARD,
        MOVE_BACKWARD,
        MOVE_LEFT,
        MOVE_RIGHT,
        JUMP,
        SNEAK,
        WAIT
    }
    
    /**
     * Macro action
     */
    public static class MacroAction {
        private final MacroActionType type;
        private final long duration;
        
        public MacroAction(MacroActionType type, long duration) {
            this.type = type;
            this.duration = duration;
        }
        
        public MacroActionType getType() {
            return type;
        }
        
        public long getDuration() {
            return duration;
        }
    }
}