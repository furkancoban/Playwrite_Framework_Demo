package com.example.orangehrm.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Enhanced Configuration Manager - Centralized management of test environments.
 * Supports multiple environment configurations (dev, staging, production).
 * Provides type-safe access to configuration values with defaults.
 */
public class EnhancedConfigManager {
    
    private static EnhancedConfigManager instance;
    private Properties properties;
    private Map<String, String> environmentOverrides;
    private String activeEnvironment;
    
    private EnhancedConfigManager() {
        this.properties = new Properties();
        this.environmentOverrides = new HashMap<>();
        this.activeEnvironment = System.getenv("TEST_ENV") != null ? 
            System.getenv("TEST_ENV") : "dev";
        
        loadProperties();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized EnhancedConfigManager getInstance() {
        if (instance == null) {
            instance = new EnhancedConfigManager();
        }
        return instance;
    }
    
    private void loadProperties() {
        try {
            String propFile = "src/test/resources/test.properties";
            FileInputStream fis = new FileInputStream(propFile);
            properties.load(fis);
            fis.close();
            
            System.out.println("✓ Configuration loaded from: " + propFile);
            System.out.println("✓ Active environment: " + activeEnvironment);
        } catch (IOException e) {
            System.out.println("✗ Failed to load configuration: " + e.getMessage());
            System.out.println("Using defaults only");
        }
    }
    
    /**
     * Get String value
     */
    public String getString(String key) {
        return getString(key, null);
    }
    
    public String getString(String key, String defaultValue) {
        // Check environment overrides first
        if (environmentOverrides.containsKey(key)) {
            return environmentOverrides.get(key);
        }
        
        // Check environment-specific key
        String envSpecificKey = key + "." + activeEnvironment;
        if (properties.containsKey(envSpecificKey)) {
            return properties.getProperty(envSpecificKey);
        }
        
        // Check base key
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        
        // Return default
        return defaultValue;
    }
    
    /**
     * Get Integer value
     */
    public int getInt(String key, int defaultValue) {
        String value = getString(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid integer value for key: " + key);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get Boolean value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);
        if (value != null) {
            return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes");
        }
        return defaultValue;
    }
    
    /**
     * Set override value (useful for test parameterization)
     */
    public void setOverride(String key, String value) {
        environmentOverrides.put(key, value);
    }
    
    /**
     * Clear override
     */
    public void clearOverride(String key) {
        environmentOverrides.remove(key);
    }
    
    /**
     * Get active environment
     */
    public String getActiveEnvironment() {
        return activeEnvironment;
    }
    
    /**
     * Set active environment
     */
    public void setActiveEnvironment(String environment) {
        this.activeEnvironment = environment;
        System.out.println("✓ Active environment changed to: " + environment);
    }
    
    // ===== Commonly used properties =====
    
    public String getApplicationUrl() {
        return getString("application.url", "http://localhost:8080");
    }
    
    public String getBrowserType() {
        return getString("browser", "chrome");
    }
    
    public boolean isHeadlessBrowser() {
        return getBoolean("browser.headless", false);
    }
    
    public int getNavigationTimeout() {
        return getInt("navigation.timeout.ms", 30000);
    }
    
    public int getElementTimeout() {
        return getInt("element.timeout.ms", 10000);
    }
    
    public boolean isVisualTestingEnabled() {
        return getBoolean("visual.testing.enabled", false);
    }
    
    public String getScreenshotPath() {
        return getString("screenshot.path", "target/screenshots");
    }
    
    public boolean isParallelExecutionEnabled() {
        return getBoolean("parallel.execution", false);
    }
    
    public int getParallelThreadCount() {
        return getInt("parallel.thread.count", 3);
    }
    
    public String getReportOutputPath() {
        return getString("report.output.path", "target/enhanced-test-report.html");
    }
    
    public String getApiBaseUrl() {
        return getString("api.base.url");
    }
    
    public String getApiKey() {
        return getString("api.key");
    }
    
    public String getDatabaseUrl() {
        return getString("database.url");
    }
    
    public String getDatabaseUsername() {
        return getString("database.username");
    }
    
    public String getDatabasePassword() {
        return getString("database.password");
    }
    
    /**
     * Print all current configuration
     */
    public void printActiveConfiguration() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         ACTIVE TEST CONFIGURATION - " + activeEnvironment.toUpperCase());
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Application URL    : " + getApplicationUrl());
        System.out.println("Browser Type       : " + getBrowserType() + " (Headless: " + isHeadlessBrowser() + ")");
        System.out.println("Navigation Timeout : " + getNavigationTimeout() + " ms");
        System.out.println("Element Timeout    : " + getElementTimeout() + " ms");
        System.out.println("Visual Testing     : " + isVisualTestingEnabled());
        System.out.println("Parallel Execution : " + isParallelExecutionEnabled());
        if (isParallelExecutionEnabled()) {
            System.out.println("Thread Count       : " + getParallelThreadCount());
        }
        System.out.println("Report Output      : " + getReportOutputPath());
        System.out.println("─────────────────────────────────────────────────────────────\n");
    }
}
