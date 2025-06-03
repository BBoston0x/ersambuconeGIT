package com.ersambucone.ui.themes;

public class ThemeManager {
    public enum Theme { DARK, LIGHT, TAUNASHI }

    public static void loadTheme(Theme theme) {
        switch (theme) {
            case TAUNASHI -> TaunahiTheme.apply();
            // Altri temi...
        }
    }
}