package com.ersambucone.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T loadConfig(Path path, Class<T> configClass) throws IOException {
        if (Files.exists(path)) {
            String json = Files.readString(path);
            return GSON.fromJson(json, configClass);
        }
        return null;
    }

    public static void saveConfig(Path path, Object config) throws IOException {
        String json = GSON.toJson(config);
        Files.writeString(path, json);
    }
}