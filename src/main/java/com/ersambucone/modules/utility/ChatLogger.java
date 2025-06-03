package com.ersambucone.modules.utility;

import java.io.FileWriter;

public class ChatLogger {
    public static void logMessage(String message) {
        try (FileWriter writer = new FileWriter("chat.log", true)) {
            writer.write(message + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}