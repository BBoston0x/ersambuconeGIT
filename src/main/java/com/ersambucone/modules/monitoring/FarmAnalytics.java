package com.ersambucone.modules.monitoring;

public class FarmAnalytics {
    private static long sessionStartTime;
    private static int itemsCollected;

    public static void startSession() {
        sessionStartTime = System.currentTimeMillis();
    }

    public static double getItemsPerHour() {
        double hoursElapsed = (System.currentTimeMillis() - sessionStartTime) / 3_600_000.0;
        return itemsCollected / hoursElapsed;
    }
}