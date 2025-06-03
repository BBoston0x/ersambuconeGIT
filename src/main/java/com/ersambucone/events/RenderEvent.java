package com.ersambucone.events;

import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent {
    private final MatrixStack matrixStack;
    
    public RenderEvent(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }
    
    public MatrixStack getMatrixStack() {
        return matrixStack;
    }
}