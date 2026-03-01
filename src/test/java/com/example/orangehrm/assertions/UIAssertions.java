package com.example.orangehrm.assertions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.example.orangehrm.utils.TestLogger;

/**
 * Professional assertion helpers for UI testing.
 * Provides fluent, readable assertions with detailed error messages.
 */
public class UIAssertions {
    
    private Page page;
    private String currentContext = "";
    
    public UIAssertions(Page page) {
        this.page = page;
    }
    
    public UIAssertions withContext(String context) {
        this.currentContext = " [" + context + "]";
        return this;
    }
    
    // Element visibility assertions
    public UIAssertions elementIsVisible(String selector) {
        return elementIsVisible(selector, 5000);
    }
    
    public UIAssertions elementIsVisible(String selector, int timeoutMs) {
        try {
            Locator element = page.locator(selector);
            element.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            String msg = "✓ Element is visible: " + selector + currentContext;
            TestLogger.success(msg);
        } catch (Exception e) {
            String msg = "✗ Element NOT visible: " + selector + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    // Text content assertions
    public UIAssertions elementContainsText(String selector, String expectedText) {
        try {
            Locator element = page.locator(selector);
            String actualText = element.textContent();
            if (actualText != null && actualText.contains(expectedText)) {
                String msg = "✓ Element contains text '" + expectedText + "': " + selector + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "✗ Element text mismatch. Expected: '" + expectedText + "', Got: '" + actualText + "'" + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "✗ Failed to verify element text: " + selector + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    public UIAssertions pageBodyContainsText(String expectedText) {
        try {
            String bodyText = page.locator("body").textContent();
            if (bodyText!= null && bodyText.contains(expectedText)) {
                String msg = "✓ Page contains text '" + expectedText + "'" + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "✗ Page text not found: '" + expectedText + "'" + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "✗ Failed to verify page text" + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    // URL assertions
    public UIAssertions urlContains(String expectedUrlPart) {
        try {
            String currentUrl = page.url();
            if (currentUrl.contains(expectedUrlPart)) {
                String msg = "✓ URL contains '" + expectedUrlPart + "'";
                TestLogger.success(msg);
            } else {
                String msg = "✗ URL does not contain '" + expectedUrlPart + "'. Current URL: " + currentUrl;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "✗ Failed to verify URL" + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    public UIAssertions urlEquals(String expectedUrl) {
        try {
            String currentUrl = page.url();
            if (currentUrl.equals(expectedUrl)) {
                String msg = "✓ URL matches: " + expectedUrl;
                TestLogger.success(msg);
            } else {
                String msg = "✗ URL mismatch. Expected: '" + expectedUrl + "', Got: '" + currentUrl + "'";
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "✗ Failed to verify URL" + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    // Count assertions
    public UIAssertions elementCountEquals(String selector, int expectedCount) {
        try {
            int actualCount = page.locator(selector).count();
            if (actualCount == expectedCount) {
                String msg = "✓ Element count matches: " + expectedCount + " for selector: " + selector + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "✗ Element count mismatch. Expected: " + expectedCount + ", Got: " + actualCount + " for selector: " + selector + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "✗ Failed to verify element count: " + selector + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    public UIAssertions elementCountGreaterThan(String selector, int minCount) {
        try {
            int actualCount = page.locator(selector).count();
            if (actualCount > minCount) {
                String msg = "✓ Element count (" + actualCount + ") is greater than " + minCount + " for selector: " + selector + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "✗ Element count (" + actualCount + ") is NOT greater than " + minCount + " for selector: " + selector + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "✗ Failed to verify element count: " + selector + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    // Attribute assertions
    public UIAssertions elementHasAttribute(String selector, String attributeName, String expectedValue) {
        try {
            String actualValue = page.locator(selector).getAttribute(attributeName);
            if (actualValue != null && actualValue.equals(expectedValue)) {
                String msg = "✓ Element attribute " + attributeName + " = '" + expectedValue + "' for selector: " + selector + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "✗ Attribute mismatch. Expected: '" + expectedValue + "', Got: '" + actualValue + "' for " + attributeName + " on: " + selector + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "✗ Failed to verify element attribute: " + selector + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    // Title assertion
    public UIAssertions titleContains(String expectedTitle) {
        try {
            String actualTitle = page.title();
            if (actualTitle.contains(expectedTitle)) {
                String msg = "✓ Page title contains '" + expectedTitle + "'";
                TestLogger.success(msg);
            } else {
                String msg = "✗ Title mismatch. Expected to contain: '" + expectedTitle + "', Got: '" + actualTitle + "'";
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "✗ Failed to verify page title";
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
}
