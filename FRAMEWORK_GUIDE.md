# OrangeHRM Playwright Test Framework

## 🏗️ Framework Architecture Overview

This test automation framework started as a simple Playwright + Cucumber project and evolved into a more structured codebase after realizing we needed better organization for maintainability. The main patterns we use (Page Objects, Config Management, Logging) are industry standards for a reason - they keep test code clean and make debugging easier.

**What we learned along the way:**
- Avoid too much abstraction - it makes code harder to understand
- Page Objects work great for reusable element operations  
- Centralized logging beats scattered println statements
- Configuration should be external, not hardcoded
- TestContext is cleaner than static fields for storing runtime data

---

## 📁 Project Structure (Enhanced)

```
src/test/java/com/example/orangehrm/
├── config/
│   └── ConfigManager.java          # Centralized configuration management
├── context/
│   └── TestContext.java            # Shared test data and page objects
├── pages/
│   ├── BasePage.java               # Base page object with common methods
│   ├── LoginPage.java              # Login page object
│   └── DashboardPage.java          # Dashboard page object
├── steps/
│   ├── Hooks.java                  # Cucumber hooks (setup/teardown)
│   └── StepDefinitions.java        # BDD step definitions
└── utils/
    └── TestLogger.java             # Centralized logging utility

src/test/resources/
├── features/
│   ├── smoke.feature               # 10 smoke test scenarios
│   ├── regression.feature          # Regression test scenarios
│   └── advanced.feature            # 10+ complex workflow scenarios
├── test.properties                 # Configuration properties
└── log4j2.xml                      # Logging configuration
```

---

### 1. **Page Object Model (POM) Pattern**
- `BasePage`: Abstract base class with common functionality
- `LoginPage`: Login-specific page operations
- `DashboardPage`: Dashboard-specific page operations
- Provides reusability, maintainability, and reduced duplication

**Example:**
```java
public class LoginPage extends BasePage {
    public DashboardPage loginWithValidCredentials() {
        return enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();
    }
}
```

### 2. **Centralized Configuration Management**
- `ConfigManager`: Single source of truth for all configurations
- Property-based approach for easy maintenance
- Methods for different data types (String, int, boolean)
- Fallback default values

**Example:**
```java
String appUrl = ConfigManager.getAppUrl();
boolean isHeadless = ConfigManager.isHeadless();
int timeout = ConfigManager.getElementWaitTimeout();
```

### 3. **Structured Logging**
- `TestLogger`: Centralized logging utility
- Color-coded log messages with emojis for quick visual parsing
- Different log levels (info, debug, warn, error, success)
- Structured test flow logging (step, assertion, scenario)

**Example:**
```java
TestLogger.testStep("Click login button");
TestLogger.assertion("Login successful");
TestLogger.success("Dashboard loaded");
TestLogger.error("Element not found", exception);
```

### 4. **Test Context Management**
- `TestContext`: Manages shared test data between steps
- Page object instances stored centrally
- Key-value data storage for test execution context
- Type-safe data retrieval methods

**Example:**
```java
testContext.setTestData("lastUsername", "Admin");
String username = testContext.getTestDataAsString("lastUsername");
```

### 5. **Error Handling**

When tests fail, we want to know exactly why. Rather than letting Playwright throw cryptic exceptions, we catch them and provide context.

```java
try {
    page.waitForSelector(selector);
    page.click(selector);
    TestLogger.success("Clicked element");
} catch (Exception e) {
    TestLogger.error("Failed to click", e);
    throw new RuntimeException("Element click failed", e);
}
```

This pattern helps with debugging - when something breaks in CI/CD, the error message tells you what failed and where.**

### 6. **Page Object Methods**

The page objects provide a fluent interface - you can chain methods together to express test logic in a natural way:

```java
loginPage
    .enterUsername("Admin")
    .enterPassword("admin123")
    .clickLoginButton();
```

Common methods in BasePage include:
- `clickElement()` - click with error handling
- `fillField()` - enter text
- `isElementVisible()` / `isElementPresent()` - check visibility
- `getText()` - extract text content
- `navigateTo()` - go to URL
- `waitForPageLoad()` - smart waiting

This keeps step definitions short and readable.

### 7. **Complex Test Scenarios**
- Multi-step workflows (12+ advanced scenarios)
- Error recovery workflows
- Rapid navigation tests
- Sequential module navigation
- Complete user journey testing

---

## 🧪 Test Coverage

We have a mix of test types covering different aspects:

**Smoke Tests (10 scenarios)** - Quick sanity checks
- Can you log in? Does the dashboard load? Can you navigate menus? 
- These run fast and catch major issues early
- Great for CI/CD pipelines

**Regression Tests (8 scenarios)** - Ensure nothing broke
- Module navigation flows
- Login/logout workflows  
- Menu accessibility
- These validate that existing functionality still works

**Advanced Tests (12+ scenarios)** - Complex workflows
- Invalid login attempts and recovery
- Multi-module navigation (clicking through 5+ modules in sequence)
- Complete user journeys from login to logout
- Profile and menu interactions
- Data persistence across navigation

The advanced tests are useful for catching subtle issues that simple login tests miss.

---

## 📊 Dependencies Added

```xml
<!-- Logging -->
SLF4J 2.0.9
Log4j2 2.20.0

<!-- Utilities -->
Apache Commons Lang3 3.13.0
Apache Commons IO 2.13.0

<!-- JSON Processing -->
Jackson Databind 2.15.2
```

---

## 🚀 Running Tests

### Run all smoke tests:
```powershell
mvn test "-Dcucumber.filter.tags=@smoke"
```

### Run all advanced tests:
```powershell
mvn test "-Dcucumber.filter.tags=@advanced"
```

### Run all regression tests:
```powershell
mvn test "-Dcucumber.filter.tags=@regression"
```

### Run with visible browser:
```powershell
mvn test "-Dcucumber.filter.tags=@smoke" "-Dtest.headless=false"
```

### Run with step delay for observation:
```powershell
mvn test "-Dcucumber.filter.tags=@advanced" "-Dtest.headless=false" "-Dtest.step.delay=1000"
```

### Run specific test scenario:
```powershell
mvn test "-Dcucumber.filter.tags=@login_validation"
```

### Run with different browser:
```powershell
mvn test "-Dcucumber.filter.tags=@smoke" "-Dtest.browser=firefox"
```

---

## 📝 Configuration File (test.properties)

```properties
# OrangeHRM Application Configuration
app.url=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
app.username=Admin
app.password=admin123

# Timeout Configuration
page.load.timeout=10000
element.wait.timeout=5000
```

---

## 🔍 Logging Output

Tests generate structured logs with clear flow visibility:

```
========== SCENARIO START: Test Scenario ==========
ℹ️  Starting test scenario... browser=chrome headless=true
✅ Browser launched and navigated to: https://...
📍 STEP: Verify OrangeHRM login page
🧪 ASSERT: Login page loaded successfully
📍 STEP: Login with valid credentials from config
✅ Filled field: input[name="username"] with value: Admin
========== SCENARIO END: Test Scenario ==========
```

---

## 🎓 Design Decisions

**Single Responsibility**: Each class does one thing. Page objects handle page operations, step definitions handle step mappings, ConfigManager handles configuration. Keeps maintenance straightforward.

**DRY (Don't Repeat Yourself)**: Common Playwright operations (click, fill, wait) live in BasePage so every page object doesn't replicate them.

**Fluent API**: You can chain method calls - `loginPage.enterUsername().enterPassword().clickLoginButton()` reads much better than separate statements scattered across step definitions.

**Fail-Fast**: When something goes wrong, we stop immediately with a clear error message. This saves debugging time later.

**Test Independence**: Each test scenario can run standalone without depending on previous tests. This is critical for parallel execution and reliability.

**Clear Logging**: Instead of leaving debugging to System.out.println, we use structured logging with timestamps and log levels. Helpful in CI/CD environments.

**Configuration Over Hardcoding**: Credentials, URLs, timeouts all come from test.properties. You can change behavior without touching code - essential for different environments (dev/staging/prod).

---

## 🔧 Extending the Framework

If you need to test a new page, the pattern is pretty straightforward:

1. Create a new page class extending BasePage
2. Add selectors for the unique elements on that page
3. Add methods for the workflows on that page
4. Add step definitions that use your new page object

**Example:**
```java
public class AdminPage extends BasePage {
    private static final String ADMIN_MENU = "span:has-text('Admin')";
    
    public AdminPage(Page page) {
        super(page);
    }
    
    public void verifyAdminPageLoaded() {
        page.waitForSelector(ADMIN_MENU);
        TestLogger.success("Admin page loaded");
    }
}
```

Then in your step definitions, just instantiate it and use it like you do with LoginPage.
public void i_verify_admin_panel_accessible() {
    AdminPage adminPage = new AdminPage(testContext.getPage());
    adminPage.verifyAdminPageLoaded();
    TestLogger.assertion("Admin panel verified");
}
```

For new test scenarios, just add them to the relevant .feature file. If you need a new tag (like @admin_panel), grep for existing tags to understand the naming convention and add yours.

---

## 📈 Performance Considerations

- **Headless Mode**: Default for CI/CD (faster execution)
- **Visible Mode**: For debugging and development
- **Step Delays**: Configurable wait time between steps
- **Page Load Timeouts**: Configurable timeouts for different scenarios
- **Element Waits**: Smart waiting instead of hard delays

---

## 🐛 Troubleshooting

**Elements not found / selectors breaking?**  
Check the selectors in page objects first. They might have changed with a UI update. You can also run with a visible browser (`-Dtest.headless=false`) to watch what's happening and adjust selectors as needed.

**Login keeps failing?**  
Make sure test.properties has the right credentials and the OrangeHRM demo site is accessible. Also check that the app.url is correct - if it's redirecting you'll get stuck on the login page.

**Tests are slow?**  
- Use headless mode (it's the default for a reason - no rendering = faster)
- Check if you have a huge step delay set (look in test.properties)
- If you're running advanced tests locally, they take longer (intentionally). Use @smoke tag to speed things up

**Weird timing issues?**  
Playwright does "wait for element to be clickable" automatically, but sometimes a page update is slower. If you're getting flaky tests, increase element.wait.timeout in test.properties. We default to 5 seconds which is usually enough.

**Log output is confusing?**  
Check target/logs/ for the full log files. The console output is abbreviated. The log files have everything with timestamps.

---

## 📚 Resources

- [Playwright Java Documentation](https://playwright.dev/java/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Page Object Model Pattern](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)
- [Log4j2 Configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html)

