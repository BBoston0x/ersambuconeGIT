package com.ersambucone.modules.rewards;

import com.ersambucone.config.ConfigManager;
import java.io.IOException;
import java.nio.file.Path;

public class GiftAutomation {
    private GiftConfig giftConfig;

    public GiftAutomation() {
        loadConfig();
    }

    private void loadConfig() {
        Path path = Path.of("config/gift_automation.json");
        try {
            giftConfig = ConfigManager.loadConfig(path, GiftConfig.class);
            if (giftConfig == null) {
                giftConfig = new GiftConfig(); // Create default if not exists
                ConfigManager.saveConfig(path, giftConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Handle the exception properly, maybe log it or show an error message
        }
    }

    // Add methods for gift automation logic

    public static void claimRewards() {
        RewardConfig config = ConfigManager.loadConfig(
                Path.of("config/rewards/commands.json"),
                RewardConfig.class
        );
        config.reward_commands.forEach(cmd -> MinecraftClient.getInstance().player.sendChatMessage(cmd));
    }

    public static class RewardConfig {
        public String[] reward_commands;
        public int cooldown_seconds;
    }

    public static class GiftConfig {
        // Define GiftConfig fields here
    }
}