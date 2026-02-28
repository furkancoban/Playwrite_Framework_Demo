package com.example.orangehrm.pages;

import com.example.orangehrm.utils.TestLogger;
import com.example.orangehrm.utils.ElementLocatorHelper;
import com.example.orangehrm.utils.VisualCheckpointHelper;
import com.example.orangehrm.config.ConfigManager;
import com.microsoft.playwright.Page;

/**\n * Encapsulates common page object patterns used across all page classes.\n * This reduces duplication and makes maintenance easier when we need to tweak\n * retry logic, timeouts, or error handling in one place.\n * \n * Note: Initially had too many custom retry mechanisms here, but found that\n * Playwright's built-in waits are solid. Removed that complexity.\n */
@SuppressWarnings("unused")
public class BasePage {

    protected Page page;
    protected static final int DEFAULT_TIMEOUT = 15000;  // 15 seconds for element operations

    public BasePage(Page page) {
        this.page = page;
    }

    /**
     * Click an element with proper wait and error handling.
     * Uses optimized timeout configuration for better performance.
     */
    @SuppressWarnings("unused")
    public void clickElement(String selector) {
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT));
            page.click(selector);
            TestLogger.success("Clicked element: " + selector);
        } catch (Exception e) {
            TestLogger.error("Failed to click element: " + selector, e);
            throw new RuntimeException("Unable to click element: " + selector, e);
        }
    }

    /**
     * Safe element fill with error handling
     */
    @SuppressWarnings("unused")
    public void fillField(String selector, String value) {
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT));
            page.fill(selector, value);
            TestLogger.success("Filled field: " + selector + " with value: " + value);
        } catch (Exception e) {
            TestLogger.error("Failed to fill field: " + selector, e);
            throw new RuntimeException("Unable to fill field: " + selector, e);
        }
    }

    /**
     * Safe element visibility check
     */
    public boolean isElementVisible(String selector) {
        try {
            return page.locator(selector).isVisible();
        } catch (Exception e) {
            TestLogger.debug("Element not visible: " + selector);
            return false;
        }
    }

    /**
     * Safe element existence check
     */
    public boolean isElementPresent(String selector) {
        try {
            return page.locator(selector).count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get text from element
     */
    public String getText(String selector) {
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT));
            String text = page.textContent(selector);
            TestLogger.debug("Retrieved text from: " + selector + " = " + text);
            return text;
        } catch (Exception e) {
            TestLogger.error("Failed to get text from element: " + selector, e);
            return "";
        }
    }

    /**
     * Wait for page load state
     */
    public void waitForPageLoad() {
        page.waitForLoadState();
        TestLogger.info("Page loaded");
    }

    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return page.url();
    }

    /**
     * Get page title
     */
    public String getPageTitle() {
        return page.title();
    }

    /**
     * Navigate to URL
     */
    public void navigateTo(String url) {
        page.navigate(url);
        waitForPageLoad();
        TestLogger.success("Navigated to: " + url);
    }

    /**
     * Wait for specific time
     */
    public void wait(int milliseconds) {
        page.waitForTimeout(milliseconds);
    }

    /**
     * Get element count
     */
    public int getElementCount(String selector) {
        return page.locator(selector).count();
    }
}
