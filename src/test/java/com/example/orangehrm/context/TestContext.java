package com.example.orangehrm.context;

import com.example.orangehrm.pages.LoginPage;
import com.example.orangehrm.pages.DashboardPage;
import com.microsoft.playwright.Page;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages runtime state for test execution. This class holds references to page objects
 * and test-specific data that needs to be shared across different step definitions.
 * 
 * Note: Using a context object instead of static fields makes tests much cleaner
 * and prevents state leakage between scenarios. Learned this the hard way with
 * parallelization issues.
 */
public class TestContext {

    private Page page;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private Map<String, Object> testData = new HashMap<>();

    public TestContext(Page page) {
        this.page = page;
        this.loginPage = new LoginPage(page);
        this.dashboardPage = new DashboardPage(page);
    }

    // Accessor methods for page objects
    // Each page object is lazy-initialized with the current page instance
    public Page getPage() {
        return page;
    }

    public LoginPage getLoginPage() {
        return loginPage;
    }

    public DashboardPage getDashboardPage() {
        return dashboardPage;
    }

    // Store test-specific runtime data that needs to be accessible across multiple steps
    // This helps reduce coupling between step definitions
    public void setTestData(String key, Object value) {
        testData.put(key, value);
    }

    public Object getTestData(String key) {
        return testData.get(key);
    }

    public String getTestDataAsString(String key) {
        Object value = testData.get(key);
        return value != null ? value.toString() : null;
    }

    public int getTestDataAsInt(String key) {
        Object value = testData.get(key);
        return value != null ? Integer.parseInt(value.toString()) : 0;
    }

    public boolean getTestDataAsBoolean(String key) {
        Object value = testData.get(key);
        return value != null ? Boolean.parseBoolean(value.toString()) : false;
    }

    // Clears all stored test data. Useful for cleanup between test runs.
    public void clearTestData() {
        testData.clear();
    }

    // Check if data exists before retrieval to avoid NPE
    public boolean hasTestData(String key) {
        return testData.containsKey(key);
    }

    // Debug helper - useful during test development/debugging
    public void printTestData() {
        testData.forEach((key, value) -> 
            System.out.println("TestData - " + key + ": " + value)
        );
    }
}
