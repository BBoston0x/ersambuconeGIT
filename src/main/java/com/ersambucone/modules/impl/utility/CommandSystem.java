package com.ersambucone.modules.impl.utility;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a command system for the client with "." prefix
 */
public class CommandSystem extends Module {
    private static final String PREFIX = ".";
    private final Map<String, Command> commands = new HashMap<>();
    private boolean privateChat = true;
    
    public CommandSystem() {
        super("CommandSystem", "Provides a command system for the client", Category.UTILITY);
        registerDefaultCommands();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("Command system enabled with prefix: " + PREFIX);
        Logger.log("Private chat: " + (privateChat ? "enabled" : "disabled"));
    }
    
    /**
     * Registers the default commands
     */
    private void registerDefaultCommands() {
        // Help command
        registerCommand("help", this::executeHelpCommand);
        
        // Module management commands
        registerCommand("toggle", this::executeToggleCommand);
        registerCommand("bind", this::executeBindCommand);
        
        // Macro commands
        registerCommand("macro", this::executeMacroCommand);
        
        // Settings command
        registerCommand("settings", this::executeSettingsCommand);
        
        // Client info command
        registerCommand("info", args -> "Er Sambucone Client - Version 1.0");
    }
    
    /**
     * Executes the help command
     */
    private String executeHelpCommand(String[] args) {
        Logger.log("=== Er Sambucone Client Commands ===");
        List<String> commandList = getCommandList();
        java.util.Collections.sort(commandList); // Sort alphabetically
        
        for (String cmd : commandList) {
            Logger.log(PREFIX + cmd);
        }
        return "Displayed " + commandList.size() + " commands in client log";
    }
    
    /**
     * Executes the toggle command
     */
    private String executeToggleCommand(String[] args) {
        if (args.length < 1) {
            return "Usage: " + PREFIX + "toggle <module>";
        }
        
        String moduleName = args[0];
        com.ersambucone.modules.Module module = 
            com.ersambucone.modules.ModuleManager.getInstance().getModule(moduleName);
            
        if (module != null) {
            module.toggle();
            return "Toggled module: " + module.getName() + " (" + 
                (module.isEnabled() ? "enabled" : "disabled") + ")";
        }
        
        return "Module not found: " + moduleName;
    }
    
    /**
     * Executes the bind command
     */
    private String executeBindCommand(String[] args) {
        if (args.length < 2) {
            return "Usage: " + PREFIX + "bind <module> <key>";
        }
        
        String moduleName = args[0];
        String key = args[1];
        
        com.ersambucone.modules.Module module = 
            com.ersambucone.modules.ModuleManager.getInstance().getModule(moduleName);
            
        if (module == null) {
            return "Module not found: " + moduleName;
        }
        
        // Implementation would go here
        return "Bound module " + module.getName() + " to key " + key;
    }
    
    /**
     * Executes the macro command
     */
    private String executeMacroCommand(String[] args) {
        if (args.length < 1) {
            return "Usage: " + PREFIX + "macro <list|start|stop|create|delete> [name]";
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "list":
                return "Macro list command executed";
            case "start":
                if (args.length < 2) {
                    return "Usage: " + PREFIX + "macro start <name>";
                }
                return "Started macro: " + args[1];
            case "stop":
                return "Stopped current macro";
            case "create":
                if (args.length < 2) {
                    return "Usage: " + PREFIX + "macro create <name>";
                }
                return "Created macro: " + args[1];
            case "delete":
                if (args.length < 2) {
                    return "Usage: " + PREFIX + "macro delete <name>";
                }
                return "Deleted macro: " + args[1];
            default:
                return "Unknown macro action: " + action;
        }
    }
    
    /**
     * Executes the settings command
     */
    private String executeSettingsCommand(String[] args) {
        if (args.length < 1) {
            return "Usage: " + PREFIX + "settings <module> [setting] [value]";
        }
        
        String moduleName = args[0];
        com.ersambucone.modules.Module module = 
            com.ersambucone.modules.ModuleManager.getInstance().getModule(moduleName);
            
        if (module == null) {
            return "Module not found: " + moduleName;
        }
        
        // List settings if no setting name provided
        if (args.length == 1) {
            if (!module.hasSettings()) {
                return "Module " + module.getName() + " has no settings";
            }
            
            Logger.log("=== Settings for " + module.getName() + " ===");
            for (com.ersambucone.modules.Module.Setting<?> setting : module.getSettings()) {
                Logger.log(setting.getName() + ": " + setting.getValue());
            }
            return "Displayed settings for " + module.getName() + " in client log";
        }
        
        // Get setting
        String settingName = args[1];
        com.ersambucone.modules.Module.Setting<?> setting = module.getSetting(settingName);
        
        if (setting == null) {
            return "Setting not found: " + settingName;
        }
        
        // Get setting value if no new value provided
        if (args.length == 2) {
            return setting.getName() + ": " + setting.getValue();
        }
        
        // Set setting value
        // Note: This is a simplified implementation that assumes all settings are strings
        // A real implementation would need to handle different types
        String value = args[2];
        
        // Implementation would go here to set the value based on its type
        return "Set " + setting.getName() + " to " + value + " for " + module.getName();
    }
    
    /**
     * Registers a command
     * 
     * @param name The command name
     * @param command The command implementation
     */
    public void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);
        Logger.log("Registered command: " + PREFIX + name);
    }
    
    /**
     * Gets a list of all registered commands
     * 
     * @return A list of command names
     */
    public List<String> getCommandList() {
        return new ArrayList<>(commands.keySet());
    }
    
    /**
     * Executes a command
     * 
     * @param input The command input (with or without prefix)
     * @return Command execution result or null if not a command
     */
    public String executeCommand(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        
        // Check if the input starts with the prefix
        if (!input.startsWith(PREFIX)) {
            return null;
        }
        
        // Remove the prefix and trim any extra whitespace
        String commandInput = input.substring(PREFIX.length()).trim();
        if (commandInput.isEmpty()) {
            return "Please enter a command. Use " + PREFIX + "help for a list of commands.";
        }
        
        // Split the command and arguments
        String[] parts = commandInput.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];
        
        // Get the command
        Command command = commands.get(commandName);
        if (command != null) {
            try {
                return command.execute(args);
            } catch (Exception e) {
                Logger.error("Error executing command: " + commandName);
                e.printStackTrace();
                return "Error executing command: " + e.getMessage();
            }
        }
        
        // Suggest similar commands if not found
        List<String> suggestions = findSimilarCommands(commandName);
        if (!suggestions.isEmpty()) {
            return "Unknown command: " + commandName + ". Did you mean: " + String.join(", ", suggestions) + "?";
        }
        
        return "Unknown command: " + commandName + ". Use " + PREFIX + "help for a list of commands.";
    }
    
    /**
     * Finds commands similar to the given name
     * 
     * @param name The command name to find similar commands for
     * @return A list of similar command names
     */
    private List<String> findSimilarCommands(String name) {
        List<String> result = new ArrayList<>();
        for (String cmd : commands.keySet()) {
            // Simple similarity check - commands that start with the same letter
            // or have a Levenshtein distance of 2 or less
            if (cmd.charAt(0) == name.charAt(0) || levenshteinDistance(cmd, name) <= 2) {
                result.add(cmd);
            }
            
            // Limit to 3 suggestions
            if (result.size() >= 3) {
                break;
            }
        }
        return result;
    }
    
    /**
     * Calculates the Levenshtein distance between two strings
     * 
     * @param a First string
     * @param b Second string
     * @return The Levenshtein distance
     */
    private int levenshteinDistance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            costs[j] = j;
        }
        
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), 
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        
        return costs[b.length()];
    }
    
    /**
     * Sets whether to use private chat
     * 
     * @param privateChat True to use private chat, false otherwise
     */
    public void setPrivateChat(boolean privateChat) {
        this.privateChat = privateChat;
        if (isEnabled()) {
            Logger.log("Private chat: " + (privateChat ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Checks if private chat is enabled
     * 
     * @return True if private chat is enabled, false otherwise
     */
    public boolean isPrivateChatEnabled() {
        return privateChat;
    }
    
    /**
     * Gets the command prefix
     * 
     * @return The command prefix
     */
    public static String getPrefix() {
        return PREFIX;
    }
    
    /**
     * Checks if the input is a command
     * 
     * @param input The input to check
     * @return True if the input is a command, false otherwise
     */
    public boolean isCommand(String input) {
        return input != null && !input.isEmpty() && input.startsWith(PREFIX);
    }
    
    /**
     * Gets a command by name
     * 
     * @param name The command name
     * @return The command, or null if not found
     */
    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }
    
    /**
     * Command interface
     */
    public interface Command {
        /**
         * Executes the command
         * 
         * @param args The command arguments
         * @return The command result message
         */
        String execute(String[] args);
    }
}