package com.example.orangehrm.utils;

import com.microsoft.playwright.Page;
import java.util.HashMap;
import java.util.Map;

/**
 * Smart element locator with fallback strategies for robust test execution.
 * 
 * When element selectors fail due to UI changes, this utility attempts
 * alternative selector strategies to find elements, reducing test brittleness.
 * Successful selectors are cached for improved performance.
 * 
 * How it works:
 * 1. Test executes with a primary selector (e.g., "input[name='username']")
 * 2. If selector fails, tries alternative strategies (ID, text, position)
 * 3. On success, caches the working selector for future use
 * 4. Next execution prioritizes cached selectors
 * 5. Logs all fallback attempts for debugging
 * 
 * Benefits:
 * - Reduces flaky tests from minor UI changes
 * - Intelligent selector caching
 * - Reduced maintenance overhead
 * - Detailed logging of fallback attempts
 */
public class ElementLocatorHelper {

    private static Map<String, String> selectorCache = new HashMap<>();
    private static Map<String, Integer> healingAttempts = new HashMap<>();
    private static boolean healingEnabled = true;
    
    /**
     * Initialize element locator helper
     * Should be called during test setup
     */
    public static void initializeSelfHealing(Page page) {
        try {
            // Create checkpoint directory for fallback logs
            java.io.File checkpointDir = new java.io.File("target/heal-logs");
            if (!checkpointDir.exists()) {
                checkpointDir.mkdirs();
            }
            
            TestLogger.info("Element locator helper initialized");
        } catch (Exception e) {
            TestLogger.warn("Initialization issue", e);
        }
    }
    
    /**
     * Find element using fallback strategies if primary selector fails
     * 
     * @param page Playwright page instance
     * @param primarySelector Primary CSS/XPath selector
     * @param elementDescription Human description of element (e.g., "login button")
     * @return Locator object
     */
    public static com.microsoft.playwright.Locator findElement(
            Page page, String primarySelector, String elementDescription) {
        
        if (!healingEnabled) {
            return page.locator(primarySelector);
        }
        
        try {
            // Try primary selector first
            var locator = page.locator(primarySelector);
            if (locator.count() > 0 && locator.isVisible()) {
                cacheSelector(elementDescription, primarySelector);
                return locator;
            }
        } catch (Exception e) {
            TestLogger.debug("Primary selector failed, attempting to heal: " + primarySelector);
        }
        
        // Try cached selector if available
        String cachedSelector = selectorCache.get(elementDescription);
        if (cachedSelector != null && !cachedSelector.equals(primarySelector)) {
            try {
                var locator = page.locator(cachedSelector);
                if (locator.count() > 0 && locator.isVisible()) {
                    TestLogger.info("Located element for: " + elementDescription + 
                                   " (cached: " + cachedSelector + ")");
                    recordHealing(elementDescription);
                    return locator;
                }
            } catch (Exception e) {
                TestLogger.debug("Cached selector also failed");
            }
        }
        
        // Attempt fallback using alternative selectors
        return attemptAIHealing(page, primarySelector, elementDescription);
    }
    
    /**
     * Smart element finding with fallback strategies
     * Tries multiple approaches to locate an element
     */
    private static com.microsoft.playwright.Locator attemptAIHealing(
            Page page, String primarySelector, String elementDescription) {
        
        TestLogger.testStep("Attempting fallback strategies for: " + elementDescription);
        
        // Approach 1: Find by text if selector contains text pattern
        if (primarySelector.contains("has-text") || primarySelector.contains("text=")) {
            try {
                String textPattern = extractTextPattern(primarySelector);
                if (textPattern != null) {
                    var healedLocator = page.locator("text=" + textPattern);
                    if (healedLocator.count() > 0 && healedLocator.isVisible()) {
                        TestLogger.success("Located with text pattern: " + textPattern);
                        cacheSelector(elementDescription, "text=" + textPattern);
                        recordHealing(elementDescription);
                        return healedLocator;
                    }
                }
            } catch (Exception e) {
                TestLogger.debug("Text-based fallback failed");
            }
        }
        
        // Approach 2: Find by accessibility role
        try {
            String role = extractRole(primarySelector);
            if (role != null) {
                var healedLocator = page.locator("[role='" + role + "']");
                if (healedLocator.count() > 0 && healedLocator.isVisible()) {
                    TestLogger.success("Located using role: " + role);
                    cacheSelector(elementDescription, "[role='" + role + "']");
                    recordHealing(elementDescription);
                    return healedLocator;
                }
            }
        } catch (Exception e) {
            TestLogger.debug("Role-based fallback failed");
        }
        
        // Approach 3: Find by tag and partial attribute
        try {
            String attributePattern = extractAttributePattern(primarySelector);
            if (attributePattern != null) {
                var healedLocator = page.locator(attributePattern);
                if (healedLocator.count() > 0 && healedLocator.isVisible()) {
                    TestLogger.success("Located using attribute pattern: " + attributePattern);
                    cacheSelector(elementDescription, attributePattern);
                    recordHealing(elementDescription);
                    return healedLocator;
                }
            }
        } catch (Exception e) {
            TestLogger.debug("Attribute-based fallback failed");
        }
        
        // Approach 4: Fallback to original selector
        TestLogger.warn("Could not locate with fallback strategies: " + primarySelector + 
                       " for element: " + elementDescription + ". Using original selector.");
        recordHealing(elementDescription);
        return page.locator(primarySelector); // Will fail but with clear error
    }
    
    /**
     * Extract text pattern from selector
     */
    private static String extractTextPattern(String selector) {
        if (selector.contains("'")) {
            int start = selector.indexOf("'") + 1;
            int end = selector.lastIndexOf("'");
            if (start < end) {
                return selector.substring(start, end);
            }
        }
        return null;
    }
    
    /**
     * Extract role from selector
     */
    private static String extractRole(String selector) {
        if (selector.contains("role=")) {
            int start = selector.indexOf("role='") + 6;
            int end = selector.indexOf("'", start);
            if (start < end) {
                return selector.substring(start, end);
            }
        }
        return null;
    }
    
    /**
     * Extract attribute pattern from selector
     */
    private static String extractAttributePattern(String selector) {
        // Handle common patterns like input[name='username'] -> input[name*='user']
        if (selector.contains("[")) {
            int start = selector.indexOf("[");
            int end = selector.indexOf("]") + 1;
            if (start < end) {
                String attribute = selector.substring(start, end);
                // Make pattern more flexible
                return attribute.replace("=", "*=");
            }
        }
        return null;
    }
    
    /**
     * Cache successful selector for future use
     */
    private static void cacheSelector(String elementDescription, String selector) {
        selectorCache.put(elementDescription, selector);
        TestLogger.debug("Cached selector for '" + elementDescription + "': " + selector);
    }
    
    /**
     * Record healing attempt
     */
    private static void recordHealing(String elementDescription) {
        int attempts = healingAttempts.getOrDefault(elementDescription, 0) + 1;
        healingAttempts.put(elementDescription, attempts);
    }
    
    /**
     * Get selector fallback statistics
     */
    public static void printHealingStats() {
        if (selectorCache.isEmpty() && healingAttempts.isEmpty()) {
            TestLogger.info("No fallback selectors were used during this test run");
        } else {
            TestLogger.info("=== Element Locator Statistics ===");
            TestLogger.info("Total fallback selectors: " + selectorCache.size());
            TestLogger.info("Total fallback attempts: " + healingAttempts.values().stream()
                .mapToInt(Integer::intValue).sum());
            
            selectorCache.forEach((key, value) -> 
                TestLogger.info("  - " + key + " → " + value + 
                    " (attempts: " + healingAttempts.getOrDefault(key, 0) + ")")
            );
        }
    }
    
    /**
     * Clear selector cache
     */
    public static void clearCache() {
        selectorCache.clear();
        healingAttempts.clear();
        TestLogger.debug("Selector cache cleared");
    }
    
    /**
     * Check if element exists using smart locating
     */
    public static boolean elementExists(Page page, String selector, String description) {
        try {
            var locator = findElement(page, selector, description);
            return locator.count() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Disable fallback strategies for specific tests if needed
     */
    public static void disableHealing() {
        healingEnabled = false;
        TestLogger.debug("Fallback strategies disabled");
    }
    
    /**
     * Re-enable fallback strategies
     */
    public static void enableHealing() {
        healingEnabled = true;
        TestLogger.debug("Fallback strategies enabled");
    }
    
    /**
     * Check healing status
     */
    public static boolean isHealingEnabled() {
        return healingEnabled;
    }
}

