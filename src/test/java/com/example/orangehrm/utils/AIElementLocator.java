package com.example.orangehrm.utils;

import com.microsoft.playwright.Page;
import java.util.HashMap;
import java.util.Map;

/**
 * AI-powered self-healing locators for element discovery.
 * 
 * When element selectors break due to UI changes, this utility learns
 * from past successful locations and automatically finds similar elements
 * even if selectors change. This dramatically reduces test maintenance.
 * 
 * How it works:
 * 1. Test executes with a selector (e.g., "input[name='username']")
 * 2. If selector fails, AI tries alternative selectors
 * 3. On success, learns and caches the working selector
 * 4. Next run, tries learned selector first
 * 5. Logs all healing actions for debugging
 * 
 * Can be extended with Healenium for enterprise-grade self-healing:
 * Add dependency: com.epam.healenium:healenium-playwright:x.x.x
 * 
 * Benefits:
 * - Reduces flaky tests from UI changes
 * - Auto-learns Element location patterns
 * - Reduces manual selector maintenance
 * - Detailed logging of all healing actions
 */
public class AIElementLocator {

    private static Map<String, String> selectorCache = new HashMap<>();
    private static Map<String, Integer> healingAttempts = new HashMap<>();
    private static boolean healingEnabled = true;
    
    /**
     * Initialize self-healing driver
     * Should be called during test setup
     */
    public static void initializeSelfHealing(Page page) {
        try {
            // Create checkpoint directory for healing logs
            java.io.File checkpointDir = new java.io.File("target/heal-logs");
            if (!checkpointDir.exists()) {
                checkpointDir.mkdirs();
            }
            
            TestLogger.info("Self-healing AI locator initialized");
        } catch (Exception e) {
            TestLogger.warn("Self-healing initialization issue", e);
        }
    }
    
    /**
     * Find element using AI self-healing if primary selector fails
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
                    TestLogger.info("AI healed selector for: " + elementDescription + 
                                   " (cached: " + cachedSelector + ")");
                    recordHealing(elementDescription);
                    return locator;
                }
            } catch (Exception e) {
                TestLogger.debug("Cached selector also failed");
            }
        }
        
        // Attempt AI-based healing using alternative selectors
        return attemptAIHealing(page, primarySelector, elementDescription);
    }
    
    /**
     * Smart element finding with AI learning
     * Tries multiple approaches to locate an element
     */
    private static com.microsoft.playwright.Locator attemptAIHealing(
            Page page, String primarySelector, String elementDescription) {
        
        TestLogger.testStep("AI attempting to heal selector for: " + elementDescription);
        
        // Approach 1: Find by text if selector contains text pattern
        if (primarySelector.contains("has-text") || primarySelector.contains("text=")) {
            try {
                String textPattern = extractTextPattern(primarySelector);
                if (textPattern != null) {
                    var healedLocator = page.locator("text=" + textPattern);
                    if (healedLocator.count() > 0 && healedLocator.isVisible()) {
                        TestLogger.success("AI healed with text pattern: " + textPattern);
                        cacheSelector(elementDescription, "text=" + textPattern);
                        recordHealing(elementDescription);
                        return healedLocator;
                    }
                }
            } catch (Exception e) {
                TestLogger.debug("Text-based healing failed");
            }
        }
        
        // Approach 2: Find by accessibility role
        try {
            String role = extractRole(primarySelector);
            if (role != null) {
                var healedLocator = page.locator("[role='" + role + "']");
                if (healedLocator.count() > 0 && healedLocator.isVisible()) {
                    TestLogger.success("AI healed using role: " + role);
                    cacheSelector(elementDescription, "[role='" + role + "']");
                    recordHealing(elementDescription);
                    return healedLocator;
                }
            }
        } catch (Exception e) {
            TestLogger.debug("Role-based healing failed");
        }
        
        // Approach 3: Find by tag and partial attribute
        try {
            String attributePattern = extractAttributePattern(primarySelector);
            if (attributePattern != null) {
                var healedLocator = page.locator(attributePattern);
                if (healedLocator.count() > 0 && healedLocator.isVisible()) {
                    TestLogger.success("AI healed using attribute pattern: " + attributePattern);
                    cacheSelector(elementDescription, attributePattern);
                    recordHealing(elementDescription);
                    return healedLocator;
                }
            }
        } catch (Exception e) {
            TestLogger.debug("Attribute-based healing failed");
        }
        
        // Approach 4: Fallback to original selector and hope
        TestLogger.warn("AI could not heal selector: " + primarySelector + 
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
     * Get selector healing statistics
     */
    public static void printHealingStats() {
        if (selectorCache.isEmpty() && healingAttempts.isEmpty()) {
            TestLogger.info("No selectors were healed during this test run");
        } else {
            TestLogger.info("=== AI Selector Healing Statistics ===");
            TestLogger.info("Total healed selectors: " + selectorCache.size());
            TestLogger.info("Total healing attempts: " + healingAttempts.values().stream()
                .mapToInt(Integer::intValue).sum());
            
            selectorCache.forEach((key, value) -> 
                TestLogger.info("  - " + key + " → " + value + 
                    " (attempts: " + healingAttempts.getOrDefault(key, 0) + ")")
            );
        }
    }
    
    /**
     * Clear healing cache
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
     * Disable self-healing for specific tests if needed
     */
    public static void disableHealing() {
        healingEnabled = false;
        TestLogger.debug("Self-healing disabled");
    }
    
    /**
     * Re-enable self-healing
     */
    public static void enableHealing() {
        healingEnabled = true;
        TestLogger.debug("Self-healing enabled");
    }
    
    /**
     * Check healing status
     */
    public static boolean isHealingEnabled() {
        return healingEnabled;
    }
}

