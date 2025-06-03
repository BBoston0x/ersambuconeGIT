package com.ersambucone.modules.utility;

import java.util.HashMap;
import java.util.Map;

public class CommandSystem {
    private Map<String, Command> commands;

    public CommandSystem() {
        commands = new HashMap<>();
    }

    public void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);
    }

    public boolean executeCommand(String input) {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(args);
            return true;
        }
        return false;
    }

    public interface Command {
        void execute(String args);
    }
}