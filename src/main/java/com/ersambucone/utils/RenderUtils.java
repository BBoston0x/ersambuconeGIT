package com.ersambucone.utils;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;

public class RenderUtils {
    public static void drawRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        // Implementazione semplificata
    }

    public static void drawLine(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, Vec3d start, Vec3d end, float red, float green, float blue, float alpha) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        
        matrixStack.push();
        matrixStack.translate(start.x, start.y, start.z);
        
        Vec3d diff = end.subtract(start);
        
        vertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), 0, 0, 0)
                .color(red, green, blue, alpha)
                .normal(1, 0, 0)
                .next();
        
        vertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), (float)diff.x, (float)diff.y, (float)diff.z)
                .color(red, green, blue, alpha)
                .normal(1, 0, 0)
                .next();
        
        matrixStack.pop();
    }

    // Add more rendering utility methods as needed
}