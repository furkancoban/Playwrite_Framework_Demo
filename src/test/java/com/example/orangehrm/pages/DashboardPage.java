package com.example.orangehrm.pages;

import com.example.orangehrm.config.ConfigManager;
import com.example.orangehrm.utils.TestLogger;
import com.microsoft.playwright.Page;

/**
 * Page object for the main dashboard. Has methods for navigation and user menu stuff.
 */
public class DashboardPage extends BasePage {

    private static final String DASHBOARD_HEADER = "h6:has-text('Dashboard')";
    private static final String USER_DROPDOWN = "span.oxd-userdropdown-tab";
    private static final String LOGOUT_BUTTON = "a:has-text('Logout')";
    private static final String MAIN_MENU_ITEMS = "ul.oxd-main-menu li";
    private static final String MENU_ITEM_TEMPLATE = "span:has-text('{}')";

    public DashboardPage(Page page) {
        super(page);
    }

    /**
     * Make sure we're actually on the dashboard
     */
    public DashboardPage verifyDashboardIsLoaded() {
        int dashboardTimeout = Math.max(ConfigManager.getPageLoadTimeout(), 15000);

        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                page.waitForSelector(
                    DASHBOARD_HEADER,
                    new Page.WaitForSelectorOptions().setTimeout((double) dashboardTimeout)
                );
                TestLogger.assertion("Dashboard loaded successfully");
                TestLogger.assertion("Dashboard URL contains 'dashboard': " + getCurrentUrl().contains("dashboard"));
                return this;
            } catch (Exception e) {
                TestLogger.error("Dashboard load attempt " + attempt + " failed", e);
                if (attempt < 2) {
                    wait(1000);
                } else {
                    throw new RuntimeException("Dashboard did not load", e);
                }
            }
        }

        throw new RuntimeException("Dashboard did not load");
    }

    /**
     * Click on a menu item to go to that module
     */
    public DashboardPage navigateToMenu(String menuName) {
        TestLogger.testStep("Navigate to menu: " + menuName);
        String selector = MENU_ITEM_TEMPLATE.replace("{}", menuName);
        
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT));
            page.locator(selector).first().click();
            waitForPageLoad();
            wait(500); // Brief wait for page transition
            TestLogger.success("Navigated to: " + menuName);
        } catch (Exception e) {
            TestLogger.error("Failed to navigate to menu: " + menuName, e);
            throw new RuntimeException("Unable to navigate to: " + menuName, e);
        }
        
        return this;
    }

    /**
     * Click the user profile icon in the top right
     */
    public DashboardPage clickUserProfileDropdown() {
        TestLogger.testStep("Click user profile dropdown");
        clickElement(USER_DROPDOWN);
        wait(300);
        return this;
    }

    /**
     * Log out and go back to the login page
     */
    public LoginPage logout() {
        TestLogger.testStep("Logout from application");
        clickUserProfileDropdown();
        clickElement(LOGOUT_BUTTON);
        waitForPageLoad();
        TestLogger.success("Logged out successfully");
        return new LoginPage(page);
    }

    /**
     * Count how many menu items are in the sidebar
     */
    public int getMainMenuItemsCount() {
        int menuTimeout = Math.max(ConfigManager.getElementWaitTimeout(), 8000);

        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                page.waitForSelector(MAIN_MENU_ITEMS, new Page.WaitForSelectorOptions().setTimeout((double) menuTimeout));
                int count = getElementCount(MAIN_MENU_ITEMS);

                if (count > 0) {
                    TestLogger.assertion("Main menu items count: " + count);
                    return count;
                }

                wait(500);
            } catch (Exception e) {
                TestLogger.error("Menu count attempt " + attempt + " failed", e);
                if (attempt < 2) {
                    wait(500);
                }
            }
        }

        TestLogger.assertion("Main menu items count: 0");
        return 0;
    }

    /**
     * Verify user menu is visible
     */
    public boolean isUserMenuVisible() {
        return isElementVisible("ul.oxd-dropdown-menu");
    }

    /**
     * Check if specific menu item exists
     */
    public boolean isMenuItemPresent(String menuName) {
        String selector = MENU_ITEM_TEMPLATE.replace("{}", menuName);
        return isElementPresent(selector);
    }
    
    /**
     * Check if specific dropdown menu item exists in user dropdown
     */
    public boolean isDropdownMenuItemPresent(String menuName) {
        try {
            page.waitForSelector("ul.oxd-dropdown-menu", new Page.WaitForSelectorOptions().setTimeout(5000));
            String selector = "ul.oxd-dropdown-menu a:has-text('" + menuName + "')";
            return isElementVisible(selector);
        } catch (Exception e) {
            TestLogger.error("Dropdown menu item check failed for: " + menuName, e);
            return false;
        }
    }

    /**
     * Get dashboard title/welcome message
     */
    public String getDashboardWelcomeMessage() {
        return getText(DASHBOARD_HEADER);
    }
}


