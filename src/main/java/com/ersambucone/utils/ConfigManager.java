package com.ersambucone.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages configuration files
 */
public class ConfigManager {
    private static final String CONFIG_DIR = "config/ersambucone";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Loads a configuration file
     * 
     * @param name The name of the configuration file
     * @return The configuration as a JsonObject
     */
    public static JsonObject loadConfig(String name) {
        try {
            File configFile = getConfigFile(name);
            if (!configFile.exists()) {
                return new JsonObject();
            }
            
            try (FileReader reader = new FileReader(configFile)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        } catch (Exception e) {
            Logger.error("Failed to load config " + name + ": " + e.getMessage());
            return new JsonObject();
        }
    }
    
    /**
     * Saves a configuration file
     * 
     * @param name The name of the configuration file
     * @param config The configuration as a JsonObject
     */
    public static void saveConfig(String name, JsonObject config) {
        try {
            File configFile = getConfigFile(name);
            ensureConfigDir();
            
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception e) {
            Logger.error("Failed to save config " + name + ": " + e.getMessage());
        }
    }
    
    /**
     * Gets a configuration file
     * 
     * @param name The name of the configuration file
     * @return The configuration file
     */
    private static File getConfigFile(String name) {
        return new File(CONFIG_DIR, name + ".json");
    }
    
    /**
     * Ensures the configuration directory exists
     */
    private static void ensureConfigDir() throws IOException {
        Path configDir = Paths.get(CONFIG_DIR);
        if (!Files.exists(configDir)) {
            Files.createDirectories(configDir);
        }
    }
}