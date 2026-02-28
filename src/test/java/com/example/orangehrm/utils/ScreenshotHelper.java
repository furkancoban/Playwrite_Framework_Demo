package com.example.orangehrm.utils;

import com.microsoft.playwright.Page;
import io.cucumber.java.Scenario;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;

/**
 * Utility class for capturing and managing screenshots during test execution.
 * Screenshots are saved to target/screenshots/ with timestamp for easy tracking
 * and included in test reports for visual verification.
 */
public class ScreenshotHelper {
    
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    
    /**
     * Capture a screenshot with timestamp
     */
    public static String captureScreenshot(Page page, String scenarioName) {
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String sanitizedScenario = scenarioName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String filename = String.format("%s_%s.png", sanitizedScenario, timestamp);
            String filepath = Paths.get(SCREENSHOT_DIR, filename).toString();
            
            // Capture screenshot
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filepath)));
            
            TestLogger.debug("Screenshot captured: " + filepath);
            return filepath;
            
        } catch (Exception e) {
            TestLogger.error("Failed to capture screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot on test failure
     */
    public static String captureScreenshotOnFailure(Page page, String scenarioName, Throwable error) {
        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String sanitizedScenario = scenarioName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String filename = String.format("%s_FAILED_%s.png", sanitizedScenario, timestamp);
            String filepath = Paths.get(SCREENSHOT_DIR, filename).toString();
            
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filepath)));
            
            TestLogger.error("Screenshot on failure: " + filepath);
            return filepath;
            
        } catch (Exception e) {
            TestLogger.error("Failed to capture failure screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot with custom name (useful for key test steps)
     */
    public static String captureScreenshot(Page page, String scenarioName, String stepName) {
        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String sanitizedScenario = scenarioName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String sanitizedStep = stepName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String filename = String.format("%s_%s_%s.png", sanitizedScenario, sanitizedStep, timestamp);
            String filepath = Paths.get(SCREENSHOT_DIR, filename).toString();
            
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filepath)));
            
            TestLogger.debug("Screenshot captured: " + filepath);
            return filepath;
            
        } catch (Exception e) {
            TestLogger.error("Failed to capture screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot and embed it in Cucumber report
     * This method both saves the screenshot to disk and attaches it to the report
     */
    public static void captureAndEmbedScreenshot(Page page, Scenario scenario, String stepName, String status) {
        try {
            // Capture screenshot as bytes
            byte[] screenshot = page.screenshot();
            
            // Embed in Cucumber report with descriptive name
            String screenshotName = String.format("%s [%s]", stepName, status);
            scenario.attach(screenshot, "image/png", screenshotName);
            
            // Also save to disk for reference
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String sanitizedScenario = scenario.getName().replaceAll("[^a-zA-Z0-9_-]", "_");
            String sanitizedStep = stepName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String filename = String.format("%s_%s_%s_%s.png", sanitizedScenario, sanitizedStep, status, timestamp);
            String filepath = Paths.get(SCREENSHOT_DIR, filename).toString();
            
            Files.write(Paths.get(filepath), screenshot);
            
            TestLogger.debug(String.format("Screenshot [%s] embedded in report: %s", status, stepName));
            
        } catch (IOException e) {
            TestLogger.error("Failed to capture and embed screenshot", e);
        }
    }
}
