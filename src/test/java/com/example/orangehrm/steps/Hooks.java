package com.example.orangehrm.steps;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import com.example.orangehrm.config.ConfigManager;
import com.example.orangehrm.context.TestContext;
import com.example.orangehrm.utils.TestLogger;
import com.example.orangehrm.utils.ScreenshotHelper;
import com.example.orangehrm.utils.VisualCheckpointHelper;
import com.example.orangehrm.utils.ElementLocatorHelper;
import com.example.orangehrm.utils.SimpleReportTracker;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.util.Properties;

/**
 * Cucumber hooks for test lifecycle management.
 * 
 * @Before: Runs before each scenario - initializes browser and navigates to app
 * @After: Runs after each scenario - cleans up resources
 * @AfterStep: Allows adding delays between steps for observation (useful for debugging)
 * 
 * Static fields here are injected into StepDefinitions. While static state can be
 * problematic for parallel execution, it works fine for sequential test runs
 * and keeps the code simple. If parallelization is needed later, could switch to
 * ThreadLocal or Scenario context.
 */
public class Hooks {

    public static Playwright playwright;
    public static Browser browser;
    public static Page page;
    public static TestContext testContext;
    public static Properties testProperties;
    public static Scenario currentScenario;

    private static final int MIN_NAVIGATION_TIMEOUT_MS = 30000;  // 30 seconds minimum for stable initial load
    @SuppressWarnings("unused")
	private static final int NAVIGATION_RETRY_COUNT = 2;  // Reduce retries for faster failure
    private static volatile int scenariosExecuted = 0;
    private static volatile int scenariosPassed = 0;
    private static volatile int scenariosFailed = 0;
    private static ThreadLocal<java.time.LocalDateTime> scenarioStartTime = new ThreadLocal<>();

    static {
        // Register shutdown hook to ensure report is generated even if tests are interrupted
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            TestLogger.info("\n=== TEST EXECUTION STOPPED ===");
            TestLogger.info("Scenarios Executed: " + scenariosExecuted);
            TestLogger.info("Scenarios Passed: " + scenariosPassed);
            TestLogger.info("Scenarios Failed: " + scenariosFailed);
            TestLogger.info("\nGenerating reports from captured data...");
            
            // Ensure final report is generated
            SimpleReportTracker.generateHtmlReport();
            
            // Try generating from Cucumber JSON if available
            com.example.orangehrm.utils.ReportGenerator.generatePartialReport();
            
            TestLogger.info("\n📊 AVAILABLE REPORTS:");
            TestLogger.info("  ✅ Live Report: target/test-execution-report.html (ALWAYS AVAILABLE)");
            TestLogger.info("  📄 Report Data: target/test-execution-data.txt");
            TestLogger.info("  📋 Cucumber JSON: target/cucumber.json (if available)");
            TestLogger.info("  📈 Partial Report: target/cucumber-report-partial.html (if JSON available)");
            TestLogger.info("==============================\n");
        }));
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        currentScenario = scenario;
        scenarioStartTime.set(java.time.LocalDateTime.now()); // Track start time
        String browserName = ConfigManager.getBrowser();
        boolean headless = ConfigManager.isHeadless();
        
        TestLogger.startScenario(scenario.getName());
        TestLogger.info("Starting test scenario... browser=" + browserName + " headless=" + headless);
        
        try {
            // Initialize Playwright browser. Using channel="chrome" because it's more stable
            // than launching from scratch - assumes Chrome is installed on the system.
            // For CI/CD, ensure chromium is installed via container or runner setup.
            
            playwright = Playwright.create();
            if ("chrome".equals(browserName)) {
                browser = playwright.chromium()
                    .launch(new BrowserType.LaunchOptions()
                        .setChannel("chrome")
                        .setHeadless(headless));
            } else if ("chromium".equals(browserName)) {
                browser = playwright.chromium()
                    .launch(new BrowserType.LaunchOptions()
                        .setHeadless(headless));
            } else if ("firefox".equals(browserName)) {
                browser = playwright.firefox()
                    .launch(new BrowserType.LaunchOptions()
                        .setHeadless(headless));
            } else if ("webkit".equals(browserName)) {
                browser = playwright.webkit()
                    .launch(new BrowserType.LaunchOptions().setHeadless(headless));
            } else {
                browser = playwright.chromium()
                    .launch(new BrowserType.LaunchOptions()
                        .setHeadless(headless));
            }
            // Create page with a reasonable viewport size
            page = browser.newPage();
            // Set viewport size after page creation for better compatibility
            try {
                page.setViewportSize(1920, 1080);
            } catch (Exception e) {
                TestLogger.warn("Could not set viewport size: " + e.getMessage());
                // Continue anyway, viewport is not critical for functionality
            }
            testContext = new TestContext(page);
            
            // Initialize helper utilities
            VisualCheckpointHelper.initializeVisualTesting(page, scenario.getName());
            ElementLocatorHelper.initializeSelfHealing(page);
            
            String appUrl = ConfigManager.getAppUrl();
            // Use configured timeout with a safe minimum for external site variability
            int navigationTimeout = Math.max(ConfigManager.getPageLoadTimeout(), MIN_NAVIGATION_TIMEOUT_MS);

            page.setDefaultTimeout(navigationTimeout);
            page.setDefaultNavigationTimeout(navigationTimeout);

            navigateWithRetry(appUrl, navigationTimeout);
            TestLogger.success("Browser launched and navigated to: " + appUrl);
        } catch (Exception e) {
            TestLogger.error("Playwright initialization failed", e);
            playwright = null;
            browser = null;
            page = null;
            testContext = null;
            throw new RuntimeException("Failed to initialize browser", e);
        }
    }

    private void navigateWithRetry(String appUrl, int navigationTimeout) {
        // Simple navigation - let Playwright handle timing
        try {
            TestLogger.info("Navigating to: " + appUrl);

            long startTime = System.currentTimeMillis();
            page.navigate(appUrl);
            long navigationTime = System.currentTimeMillis() - startTime;
            TestLogger.info("Page navigation completed in " + navigationTime + "ms");

            TestLogger.info("Navigation successful, login page ready for interaction");
            return;
        } catch (Exception e) {
            TestLogger.warn("Navigation failed: " + e.getMessage());
            throw new RuntimeException("Cannot load login page: " + appUrl, e);
        }
    }

    @io.cucumber.java.AfterStep
    public void afterStep(Scenario scenario) {
        // Capture screenshot after each step and embed in report
        if (page != null && scenario != null) {
            try {
                byte[] screenshot = page.screenshot();
                scenario.attach(screenshot, "image/png", "Step Screenshot");
                TestLogger.debug("Screenshot captured and embedded after step");
            } catch (Exception e) {
                TestLogger.error("Failed to capture step screenshot", e);
            }
        }
        
        // Add step delay if configured (for observation during manual test runs)
        long stepDelay = ConfigManager.getStepDelay();
        if (stepDelay > 0) {
            try {
                Thread.sleep(stepDelay);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        TestLogger.endScenario(scenario.getName());
        
        // Calculate scenario duration
        java.time.LocalDateTime startTime = scenarioStartTime.get();
        java.time.LocalDateTime endTime = java.time.LocalDateTime.now();
        long durationMs = startTime != null ? 
            java.time.Duration.between(startTime, endTime).toMillis() : 0;
        
        // Track execution statistics for partial report generation
        scenariosExecuted++;
        String status;
        if (scenario.isFailed()) {
            scenariosFailed++;
            status = "FAILED";
        } else {
            scenariosPassed++;
            status = "PASSED";
        }
        
        // Record to real-time tracker (writes immediately to disk)
        SimpleReportTracker.recordScenario(scenario.getName(), status, 
            startTime != null ? startTime : endTime, endTime, durationMs);
        
        TestLogger.info("Current Status: " + SimpleReportTracker.getStats());
        
        try {
            // Close visual checkpoint tracking
            if (VisualCheckpointHelper.isVisualTestingActive()) {
                VisualCheckpointHelper.closeVisualTesting();
            }
            
            // Print element locator statistics
            ElementLocatorHelper.printHealingStats();
            
            // Capture screenshot on test failure for troubleshooting
            if (scenario.isFailed() && page != null) {
                String screenshotPath = ScreenshotHelper.captureScreenshotOnFailure(
                    page, 
                    scenario.getName(),
                    null
                );
                TestLogger.error("Test failed. Screenshot saved: " + screenshotPath);
            }
            
            // Flush logs and ensure report data is written
            TestLogger.flushLogs();
            
            // Close browser and cleanup
            if (page != null) page.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
            TestLogger.success("Browser closed and cleanup completed");
        } catch (Exception e) {
            TestLogger.error("Error during cleanup", e);
        }
    }
}
