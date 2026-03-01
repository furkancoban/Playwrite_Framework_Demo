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
        // Context string is appended to log/error messages for easier triage.
        this.currentContext = " [" + context + "]";
        return this;
    }
    
    // Element visibility assertions
    public UIAssertions elementIsVisible(String selector) {
        return elementIsVisible(selector, 5000);
    }
    
    public UIAssertions elementIsVisible(String selector, int timeoutMs) {
        try {
            // Wait for visibility instead of immediate check to reduce flaky timing failures.
            Locator element = page.locator(selector);
            element.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            String msg = "[OK] Element is visible: " + selector + currentContext;
            TestLogger.success(msg);
        } catch (Exception e) {
            String msg = "[FAIL] Element NOT visible: " + selector + currentContext;
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
                String msg = "[OK] Element contains text \"" + expectedText + "\": " + selector + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "[FAIL] Element text mismatch. Expected: \"" + expectedText + "\", Got: \"" + actualText + "\"" + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "[FAIL] Failed to verify element text: " + selector + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    public UIAssertions pageBodyContainsText(String expectedText) {
        try {
            String bodyText = page.locator("body").textContent();
            if (bodyText!= null && bodyText.contains(expectedText)) {
                String msg = "[OK] Page contains text '" + expectedText + "'" + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "[FAIL] Page text not found: '" + expectedText + "'" + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "[FAIL] Failed to verify page text" + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    // URL assertions
    public UIAssertions urlContains(String expectedUrlPart) {
        try {
            // URL checks are useful after navigation actions where DOM may still be stable.
            String currentUrl = page.url();
            if (currentUrl.contains(expectedUrlPart)) {
                String msg = "[OK] URL contains '" + expectedUrlPart + "'";
                TestLogger.success(msg);
            } else {
                String msg = "[FAIL] URL does not contain '" + expectedUrlPart + "'. Current URL: " + currentUrl;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "[FAIL] Failed to verify URL" + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    public UIAssertions urlEquals(String expectedUrl) {
        try {
            String currentUrl = page.url();
            if (currentUrl.equals(expectedUrl)) {
                String msg = "[OK] URL matches: " + expectedUrl;
                TestLogger.success(msg);
            } else {
                String msg = "[FAIL] URL mismatch. Expected: '" + expectedUrl + "', Got: '" + currentUrl + "'";
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "[FAIL] Failed to verify URL" + currentContext;
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
                String msg = "[OK] Element count matches: " + expectedCount + " for selector: " + selector + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "[FAIL] Element count mismatch. Expected: " + expectedCount + ", Got: " + actualCount + " for selector: " + selector + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "[FAIL] Failed to verify element count: " + selector + currentContext;
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
    
    public UIAssertions elementCountGreaterThan(String selector, int minCount) {
        try {
            int actualCount = page.locator(selector).count();
            if (actualCount > minCount) {
                String msg = "[OK] Element count (" + actualCount + ") is greater than " + minCount + " for selector: " + selector + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "[FAIL] Element count (" + actualCount + ") is NOT greater than " + minCount + " for selector: " + selector + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "[FAIL] Failed to verify element count: " + selector + currentContext;
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
                String msg = "[OK] Element attribute " + attributeName + " = '" + expectedValue + "' for selector: " + selector + currentContext;
                TestLogger.success(msg);
            } else {
                String msg = "[FAIL] Attribute mismatch. Expected: '" + expectedValue + "', Got: '" + actualValue + "' for " + attributeName + " on: " + selector + currentContext;
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "[FAIL] Failed to verify element attribute: " + selector + currentContext;
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
                String msg = "[OK] Page title contains '" + expectedTitle + "'";
                TestLogger.success(msg);
            } else {
                String msg = "[FAIL] Title mismatch. Expected to contain: '" + expectedTitle + "', Got: '" + actualTitle + "'";
                TestLogger.error(msg);
                throw new AssertionError(msg);
            }
        } catch (Exception e) {
            String msg = "[FAIL] Failed to verify page title";
            TestLogger.error(msg);
            throw new AssertionError(msg, e);
        }
        return this;
    }
}
