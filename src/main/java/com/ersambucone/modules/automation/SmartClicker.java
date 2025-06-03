package com.ersambucone.modules.automation;

import java.util.Random;

public class SmartClicker {
    private final Random rand = new Random();
    private int cps = 12;
    private boolean humanMode = true;

    public void simulateClick() {
        int delay = humanMode ? 1000 / (cps + rand.nextInt(3) - 1) : 1000 / cps;
        // Logica click...
    }
}