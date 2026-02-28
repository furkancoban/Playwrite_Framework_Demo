package com.example.orangehrm.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Handles all our configuration stuff. Loads properties from test.properties when the class loads.
 * 
 * Why this approach:
 * - One place to change test settings
 * - Easy to override with system properties for CI/CD
 * - Can support different environments if we need it
 * - No messing with a bunch of config files
 * 
 * I initially tried YAML but Properties is more standard for Java projects
 * and way easier to work with in CI/CD tools like Jenkins or GitHub Actions.
 */
public class ConfigManager {

    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "test.properties";

    static {
        loadProperties();
    }

    /**
     * Load properties from our config file
     */
    private static void loadProperties() {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Configuration file not found: " + CONFIG_FILE);
            }
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }

    /**
     * Get a property value with a default fallback.
     * Could use Optional here but this is cleaner for our needs.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get property value (returns null if not found)
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get property as an integer. Returns the default if parsing fails.
     */
    public static int getPropertyAsInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get property as boolean
     */
    public static boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    // Configuration getters
    public static String getAppUrl() {
        return getProperty("app.url", "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
    }

    public static String getUsername() {
        return getProperty("app.username", "Admin");
    }

    public static String getPassword() {
        return getProperty("app.password", "admin123");
    }

    public static String getBrowser() {
        return System.getProperty("test.browser", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("test.headless", "false"));
    }

    public static int getPageLoadTimeout() {
        return getPropertyAsInt("page.load.timeout", 10000);
    }

    public static int getElementWaitTimeout() {
        return getPropertyAsInt("element.wait.timeout", 5000);
    }

    public static long getStepDelay() {
        return Long.parseLong(System.getProperty("test.step.delay", "0"));
    }
}
