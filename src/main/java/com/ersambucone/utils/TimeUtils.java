// # Gestione timer
package com.ersambucone.utils;

public class TimeUtils {
    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}