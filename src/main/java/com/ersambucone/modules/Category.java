package com.ersambucone.modules;

/**
 * Enum representing the different categories of modules
 */
public enum Category {
    REWARDS("Rewards"),
    UTILITY("Utility"),
    COSMETICS("Cosmetics"),
    AUTOMATION("Automation"),
    MONITORING("Monitoring");
    
    private final String name;
    
    Category(String name) {
        this.name = name;
    }
    
    /**
     * Gets the display name of the category
     * 
     * @return The category display name
     */
    public String getName() {
        return name;
    }
}