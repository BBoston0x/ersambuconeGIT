package com.ersambucone.modules.automation;

import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class BuildHelper {
    private MinecraftClient client;

    public BuildHelper(MinecraftClient client) {
        this.client = client;
    }

    public void placeBlock(BlockPos pos) {
        if (client.world == null || client.player == null) return;

        BlockHitResult hitResult = new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, false);
        client.interactionManager.interactBlock(client.player, client.world, client.player.getActiveHand(), hitResult);
    }

    // Add more building helper methods as needed
}