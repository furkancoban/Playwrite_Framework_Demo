package com.example.orangehrm.context;

import com.example.orangehrm.pages.LoginPage;
import com.example.orangehrm.pages.DashboardPage;
import com.microsoft.playwright.Page;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of stuff during test execution. Holds our page objects and any data
 * that needs to be shared between different steps.
 * 
 * Note: Using a context object instead of static fields is way cleaner and prevents
 * weird state issues between scenarios. Learned that lesson the hard way!
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

    // Get page objects - each one is created once with the current page
    public Page getPage() {
        return page;
    }

    public LoginPage getLoginPage() {
        return loginPage;
    }

    public DashboardPage getDashboardPage() {
        return dashboardPage;
    }

    // Store test data that needs to be shared across multiple steps.
    // Helps keep step definitions loosely coupled.
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

    // Clear all test data. Good for cleanup between runs.
    public void clearTestData() {
        testData.clear();
    }

    // Check if we have a specific piece of data
    public boolean hasTestData(String key) {
        return testData.containsKey(key);
    }

    // Debug helper - prints everything we've stored
    public void printTestData() {
        testData.forEach((key, value) -> 
            System.out.println("TestData - " + key + ": " + value)
        );
    }
}
