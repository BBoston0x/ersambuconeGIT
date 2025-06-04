package com.ersambucone.modules.impl.monitoring;

import com.ersambucone.events.EventBus;
import com.ersambucone.events.EventListener;
import com.ersambucone.events.farming.FarmAnalysisEvent;
import com.ersambucone.events.farming.FarmOptimizeEvent;
import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

import java.util.*;

/**
 * Module for optimizing farming efficiency
 */
public class FarmOptimizer extends Module {
    
    private final Map<String, Double> farmEfficiencyData = new HashMap<>();
    private final Map<String, Integer> cropCountData = new HashMap<>();
    private long lastAnalysisTime = 0;
    private final EventListener eventListener = new EventListener() {
        @Override
        public void onEvent(Object event) {
            if (event instanceof FarmAnalysisEvent) {
                analyzeFarm((FarmAnalysisEvent) event);
            }
        }
    };
    
    /**
     * Creates a new FarmOptimizer
     */
    public FarmOptimizer() {
        super("Farm Optimizer", "Analyzes and optimizes farming efficiency", Category.MONITORING);
    }

    @Override
    public void onEnable() {
        EventBus.register(eventListener);
        Logger.log("Farm Optimizer enabled");
    }

    /**
     * Analyzes farm data from an event
     * 
     * @param event The event
     */
    private void analyzeFarm(FarmAnalysisEvent event) {
        try {
            Logger.debug("Analyzing farm: " + event.getFarmType() + " with efficiency " + event.getEfficiency());
            
            // Collect data
            farmEfficiencyData.merge(event.getFarmType(), event.getEfficiency(), (old, newVal) -> (old * 0.7 + newVal * 0.3));
            cropCountData.merge(event.getFarmType(), event.getCropCount(), (old, newVal) -> Math.max(old, newVal));
            
            // Analyze every 10 minutes
            if (System.currentTimeMillis() - lastAnalysisTime > 600_000) {
                optimizeFarming();
                lastAnalysisTime = System.currentTimeMillis();
            }
        } catch (Exception e) {
            Logger.error("Error analyzing farm: " + e.getMessage());
        }
    }

    /**
     * Optimizes farming based on collected data
     */
    private void optimizeFarming() {
        try {
            Logger.debug("Optimizing farming...");
            
            // Find the most efficient farm type
            String bestFarmType = farmEfficiencyData.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Wheat");
            
            // Calculate efficiency improvement
            double currentEfficiency = farmEfficiencyData.getOrDefault(bestFarmType, 1.0);
            double baselineEfficiency = farmEfficiencyData.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(1.0);
            double improvement = currentEfficiency / baselineEfficiency - 1.0;
            
            // Generate optimization suggestions
            List<String> optimizations = new ArrayList<>();
            optimizations.add("Use " + bestFarmType + " for best results");
            
            if (cropCountData.getOrDefault(bestFarmType, 0) < 100) {
                optimizations.add("Expand farm size for better yield");
            }
            
            if (currentEfficiency < 0.8) {
                optimizations.add("Optimize farm layout for faster harvesting");
            }
            
            // Fire optimization event
            EventBus.fire(new FarmOptimizeEvent(bestFarmType, optimizations, improvement));
            
            Logger.log("Farm optimization complete: " + bestFarmType + " is best with " + 
                    String.format("%.1f%%", improvement * 100) + " improvement");
            
            // Reset data for next analysis cycle
            if (farmEfficiencyData.size() > 10) {
                // Keep only the most recent data to prevent memory bloat
                farmEfficiencyData.clear();
                cropCountData.clear();
            }
        } catch (Exception e) {
            Logger.error("Error optimizing farming: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        EventBus.unregister(eventListener);
        Logger.log("Farm Optimizer disabled");
    }
}