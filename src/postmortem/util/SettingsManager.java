package postmortem.util;

import java.io.*;
import java.util.Properties;

public class SettingsManager {
    private static final String SETTINGS_FILE = "postmortem_settings.properties";
    private static Properties properties = new Properties();

    static {
        loadSettings();
    }

    private static void loadSettings() {
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // File doesn't exist yet, will be created on first save
            setDefaults();
        }
    }

    private static void setDefaults() {
        properties.setProperty("theme.darkMode", "false");
        properties.setProperty("ui.autoRefresh", "true");
        properties.setProperty("ui.refreshInterval", "30000");
        properties.setProperty("ui.itemsPerPage", "50");
    }

    public static void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(fos, "Postmortem Application Settings");
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    public static String getSetting(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static void setSetting(String key, String value) {
        properties.setProperty(key, value);
        saveSettings();
    }

    public static boolean getBooleanSetting(String key, boolean defaultValue) {
        String value = properties.getProperty(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    public static void setBooleanSetting(String key, boolean value) {
        setSetting(key, String.valueOf(value));
    }

    public static int getIntSetting(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key, String.valueOf(defaultValue));
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void setIntSetting(String key, int value) {
        setSetting(key, String.valueOf(value));
    }
}
