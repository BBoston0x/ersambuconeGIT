package com.ersambucone.modules.monitoring;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;

public class StaffRadar {
    public static void checkForStaff() {
        Scoreboard scoreboard = MinecraftClient.getInstance().world.getScoreboard();
        // 1. Controllo vanish tramite entity
        // 2. Analisi scoreboard
        // 3. Verifica pacchetti custom
    }
}