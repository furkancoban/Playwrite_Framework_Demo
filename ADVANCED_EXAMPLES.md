# Advanced Framework Examples and Patterns

## Page Object Fluent API Example

```java
// Method chaining for readable, fluent code
loginPage
    .verifyLoginPageIsLoaded()
    .enterUsername("Admin")
    .enterPassword("admin123")
    .clickLoginButton()
    .verifyDashboardIsLoaded()
    .navigateToMenu("Admin")
    .logout();
```

## Error Handling Pattern

```java
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
```

## Test Data Management Example

```java
// Setting test data
testContext.setTestData("currentUser", "Admin");
testContext.setTestData("lastMenuNavigated", "PIM");
testContext.setTestData("loginAttempts", 1);

// Retrieving test data
String user = testContext.getTestDataAsString("currentUser");
int attempts = testContext.getTestDataAsInt("loginAttempts");

// Checking if data exists
if (testContext.hasTestData("lastMenuNavigated")) {
    String menu = testContext.getTestDataAsString("lastMenuNavigated");
}

// Print all test data
testContext.printTestData();
```

## Advanced BDD Scenario Patterns

### Multi-Step Workflow with Data Validation
```gherkin
Scenario: Complete user journey with data validation
    Given I am on the OrangeHRM login page
    When I login with username "Admin" and password "admin123"
    Then I should see the dashboard
    When I check dashboard welcome message
    And I navigate to "Admin" menu
    Then the URL should contain "admin"
    When I navigate to "Dashboard" menu
    Then I should see the dashboard
    When I logout
    Then I should be on the login page
```

### Outline-Based Scenario for Data-Driven Testing
```gherkin
Scenario Outline: Navigate to multiple modules with validation
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "<module>" menu
    And I wait for 1 seconds
    Then the URL should contain "<urlPart>"

    Examples:
      | module      | urlPart     |
      | Admin       | admin       |
      | PIM         | pim         |
      | Leave       | leave       |
```

## Configuration Management Examples

```java
// Default configuration initialization
String appUrl = ConfigManager.getAppUrl();  // Default from test.properties
String username = ConfigManager.getUsername();  // Default "Admin"
String password = ConfigManager.getPassword();  // Default "admin123"

// With fallback defaults
String customProperty = ConfigManager.getProperty("custom.key", "defaultValue");

// Type-specific getters
int timeout = ConfigManager.getElementWaitTimeout();  // Returns int
boolean headless = ConfigManager.isHeadless();  // Returns boolean
long delay = ConfigManager.getStepDelay();  // Returns long
```

## Logging Examples

```java
// Test step logging
TestLogger.testStep("Click login button");

// Assertion logging
TestLogger.assertion("Dashboard loaded successfully");

// Success logging
TestLogger.success("User logged in");

// Debug logging
TestLogger.debug("Current URL: " + currentUrl);

// Warning logging
TestLogger.warn("Unexpected element state");

// Error logging with exception
TestLogger.error("Failed to load page", exception);

// Scenario lifecycle
TestLogger.startScenario("User Login Workflow");
//... test execution ...
TestLogger.endScenario("User Login Workflow");
```

## Page Object Method Examples

```java
// Safe element operations
loginPage.fillField("input[name='username']", "Admin");
loginPage.clickElement("button[type='submit']");

// Element checks
if (loginPage.isElementVisible("input[name='username']")) {
    // Element is visible
}

if (loginPage.isElementPresent(".error-message")) {
    // Element exists on page
}

// Get element information
String pageTitle = loginPage.getPageTitle();
String elementText = loginPage.getText("h1");
int elementCount = loginPage.getElementCount("ul li");
String currentUrl = loginPage.getCurrentUrl();

// Wait operations
loginPage.wait(1000);  // Wait 1 second
loginPage.waitForPageLoad();  // Wait for page to load

// Navigation
loginPage.navigateTo("https://example.com");
```

## Step Definition Best Practices

```java
@Given("I am on the OrangeHRM login page")
public void i_am_on_orangehrm_login_page() {
    TestLogger.testStep("Verify OrangeHRM login page");
    assertNotNull(loginPage, "Login page should be initialized");
    loginPage.verifyLoginPageIsLoaded();
    TestLogger.assertion("Successfully navigated to OrangeHRM login page");
}

@When("I login with username {string} and password {string}")
public void i_login_with_credentials(String username, String password) {
    TestLogger.testStep("Login with username: " + username);
    assertNotNull(loginPage, "Login page should exist");
    dashboardPage = loginPage.loginWithCredentials(username, password);
    testContext.setTestData("lastUsername", username);
}

@Then("I should see the dashboard")
public void i_should_see_dashboard() {
    TestLogger.testStep("Verify dashboard is displayed");
    assertNotNull(dashboardPage, "Dashboard page should exist");
    dashboardPage.verifyDashboardIsLoaded();
    TestLogger.assertion("Dashboard is visible and loaded");
}
```

## Creating Custom Page Objects

```java
public class AdminPage extends BasePage {
    
    private static final String ADMIN_MENU = "span:has-text('Admin')";
    private static final String USER_MANAGEMENT = "a:has-text('User Management')";
    private static final String SYSTEM_USERS = "a:has-text('System Users')";
    private static final String ADD_USER_BUTTON = "button:has-text('Add')";
    
    public AdminPage(Page page) {
        super(page);
    }
    
    public AdminPage verifyAdminPageIsLoaded() {
        try {
            page.waitForSelector(ADMIN_MENU, 
                new Page.WaitForSelectorOptions().setTimeout(DEFAULT_TIMEOUT));
            TestLogger.assertion("Admin page loaded successfully");
            return this;
        } catch (Exception e) {
            TestLogger.error("Admin page failed to load", e);
            throw new RuntimeException("Admin page did not load", e);
        }
    }
    
    public AdminPage navigateToUserManagement() {
        TestLogger.testStep("Navigate to User Management");
        clickElement(USER_MANAGEMENT);
        waitForPageLoad();
        return this;
    }
    
    public AdminPage navigateToSystemUsers() {
        TestLogger.testStep("Navigate to System Users");
        clickElement(SYSTEM_USERS);
        waitForPageLoad();
        return this;
    }
    
    public AdminPage clickAddUserButton() {
        TestLogger.testStep("Click Add User button");
        clickElement(ADD_USER_BUTTON);
        return this;
    }
    
    public boolean isUserManagementVisible() {
        return isElementVisible(USER_MANAGEMENT);
    }
    
    public int getSystemUsersCount() {
        return getElementCount("table tbody tr");
    }
}
```

## Advanced Test Scenario Example

```gherkin
@advanced @complete_workflow @critical
Scenario: Complete admin workflow - Add and manage users
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "Admin" menu
    Then the URL should contain "admin"
    When I navigate to "User Management" submenu
    And I wait for 2 seconds
    Then the URL should contain "user"
    When I click on "Add" button
    Then I should see the "Add User" page header
    When I fill "Username" field with "testuser"
    And I fill "Password" field with "Test@123"
    And I select "Admin" as user role
    And I click "Save" button
    Then I should see success message "Successfully Added"
    When I search for "testuser"
    Then I should see the newly created user
    When I logout
    Then I should be on the login page
```

## Configuration Customization Example

```java
// In test.properties
app.url=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
app.username=Admin
app.password=admin123
page.load.timeout=10000
element.wait.timeout=5000

// Access custom properties
String customUrl = ConfigManager.getProperty("app.url");
String customTimeout = ConfigManager.getProperty("custom.timeout", "5000");
int timeoutMs = ConfigManager.getPropertyAsInt("element.wait.timeout", 5000);
boolean headless = ConfigManager.getPropertyAsBoolean("browser.headless", true);
```

## Running Tests with Various Configurations

```powershell
# Run with custom timeout
mvn test "-Dcucumber.filter.tags=@advanced" "-Dapp.url=http://custom-url"

# Run with multiple threads (requires config)
mvn test "-Dcucumber.filter.tags=@smoke" "-DforkCount=3"

# Run and skip clean
mvn test skipClean "-Dcucumber.filter.tags=@regression"

# Run with full debug output
mvn test "-Dcucumber.filter.tags=@smoke" -X
```

