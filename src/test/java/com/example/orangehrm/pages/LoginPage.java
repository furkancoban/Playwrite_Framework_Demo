package com.example.orangehrm.pages;

import com.example.orangehrm.utils.TestLogger;
import com.example.orangehrm.config.ConfigManager;
import com.microsoft.playwright.Page;

/**
 * Page object for the OrangeHRM login page. Has all the selectors and actions for logging in.
 */
public class LoginPage extends BasePage {

    private static final String USERNAME_INPUT = "input[name=\"username\"]";
    private static final String PASSWORD_INPUT = "input[name=\"password\"]";
    private static final String LOGIN_BUTTON = "button[type=\"submit\"]";
    private static final String ERROR_MESSAGE = ".oxd-text--toast-message";
    private static final String LOGIN_PAGE_TITLE = "h5:has-text('Login')";

    public LoginPage(Page page) {
        super(page);
    }

    /**
     * Make sure the login page actually loaded
     */
    public LoginPage verifyLoginPageIsLoaded() {
        try {
            page.waitForSelector(USERNAME_INPUT, new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT));
            TestLogger.assertion("Login page loaded successfully");
            return this;
        } catch (Exception e) {
            TestLogger.error("Login page failed to load", e);
            throw new RuntimeException("Login page did not load", e);
        }
    }

    /**
     * Type in the username
     */
    public LoginPage enterUsername(String username) {
        TestLogger.testStep("Enter username: " + username);
        fillField(USERNAME_INPUT, username);
        return this;
    }

    /**
     * Type in the password
     */
    public LoginPage enterPassword(String password) {
        TestLogger.testStep("Enter password");
        fillField(PASSWORD_INPUT, password);
        return this;
    }

    /**
     * Click the login button and return the dashboard page
     */
    public DashboardPage clickLoginButton() {
        TestLogger.testStep("Click login button");
        clickElement(LOGIN_BUTTON);
        waitForPageLoad();
        return new DashboardPage(page);
    }

    /**
     * Do the whole login flow using credentials from the config file
     */
    public DashboardPage loginWithValidCredentials() {
        TestLogger.testStep("Login with valid credentials");
        String username = ConfigManager.getUsername();
        String password = ConfigManager.getPassword();
        
        return enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();
    }

    /**
     * Login with specific credentials (not from config)
     */
    public DashboardPage loginWithCredentials(String username, String password) {
        TestLogger.testStep("Login with custom credentials");
        return enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();
    }

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        return isElementVisible(ERROR_MESSAGE);
    }

    /**
     * Get error message text
     */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    /**
     * Verify login page title
     */
    public boolean verifyLoginPageTitle() {
        return isElementPresent(LOGIN_PAGE_TITLE);
    }
}
