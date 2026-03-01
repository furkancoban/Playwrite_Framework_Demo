# Professional Playwright Testing Framework Guide

## 📚 Framework Architecture Overview

This is an **enterprise-grade test automation framework** supporting UI testing with built-in support for API and database validation. The framework is designed for scalability and maintainability.

### Framework Layers

```
┌─────────────────────────────────────────────┐
│     Test Scenarios (Feature Files)          │
├─────────────────────────────────────────────┤
│  Step Definitions ← UIAssertions           │
│  (Gherkin → Code mapping)                   │
├─────────────────────────────────────────────┤
│  Page Objects (UI Interactions)            │
│  │├─ LoginPage                              │
│  │├─ DashboardPage                          │
│  └─ BasePage                                │
├─────────────────────────────────────────────┤
│  Professional Assertion & Test Helpers     │
│  │├─ UIAssertions (Element & Page checks)  │
│  │├─ APITester (REST API validation)       │
│  │├─ DatabaseTester (Data verification)    │
│  └─ TestLogger (Humanized output)           │
├─────────────────────────────────────────────┤
│  Browser & Context Management              │
│  │├─ Hooks (Lifecycle)                      │
│  │├─ TestContext (State)                    │
│  └─ EnhancedConfigManager (Settings)        │
├─────────────────────────────────────────────┤
│  Playwright Browser Automation              │
│  (Chrome, Firefox, Safari support)          │
├─────────────────────────────────────────────┤
│  Report Generation                          │
│  └─ EnhancedReportGenerator (Charts, UI)    │
└─────────────────────────────────────────────┘
```

---

## 🎯 Core Components

### 1. **UIAssertions** - Professional Element & Page Assertions

Located: `src/test/java/com/example/orangehrm/assertions/UIAssertions.java`

Professional, fluent API for element and page validations with detailed error messages.

#### Key Methods:

```java
// Element Visibility
assertions().elementIsVisible("input[name='username']")
            .withContext("Login form");

// Text Content Validation
assertions().elementContainsText("h1", "Welcome")
            .withContext("Page header");

// Page Body Search
assertions().pageBodyContainsText("User profile updated")
            .withContext("Success message");

// URL Validation
assertions().urlContains("dashboard")
assertions().urlEquals("https://app.local/dashboard");

// Element Count Assertions
assertions().elementCountEquals("button", 5)
assertions().elementCountGreaterThan("table tr", 10);

// Attribute Validation
assertions().elementHasAttribute("input", "disabled", "true");

// Page Title
assertions().titleContains("OrangeHRM");
```

**Features:**
- ✅/✗ indicators in output
- Detailed error messages (actual vs expected)
- Timeout handling (default 5 seconds)
- Fluent chaining for readable assertions
- Context tracking for better debugging

**Usage in Step Definitions:**
```java
@Then("I should see username field")
public void verify_username_field() {
    assertions().elementIsVisible("input[name='username']")
               .withContext("Login form validation");
}
```

---

### 2. **APITester** - REST API Testing

Located: `src/test/java/com/example/orangehrm/api/APITester.java`

Complete REST API testing capability integrated with the same reporting system.

#### Key Methods:

```java
// Initialize
APITester api = new APITester("https://api.example.com");

// Authentication
api.setBearerToken("your-jwt-token")
   .addHeader("X-Custom-Header", "value");

// HTTP Requests
APIResponse response = api.get("/users/123");
APIResponse response = api.post("/users", jsonBody);
APIResponse response = api.put("/users/123", jsonBody);
APIResponse response = api.delete("/users/123");

// Assertions
response.assertStatusSuccess()
        .assertStatusCode(200)
        .assertBodyContains("user_id")
        .assertJsonField("status", "active");
```

**Features:**
- GET, POST, PUT, DELETE support
- Bearer token authentication
- Custom header management
- Query parameter handling
- JSON response parsing
- Status code assertions
- Body content assertions
- JSON field validation
- Integrated logging

**Example Test Scenario:**
```java
@Given("I have API authentication")
public void setup_api_auth() {
    api = new APITester("https://api.orangehrm.local");
    api.setBearerToken(ConfigManager.getInstance().getApiKey());
}

@When("I fetch employee list")
public void fetch_employees() {
    response = api.get("/api/v1/employees", Map.of("limit", "10"));
}

@Then("API returns success with employee data")
public void verify_employee_response() {
    response.assertStatusSuccess()
            .assertJsonField("recordFound", "10");
}
```

---

### 3. **DatabaseTester** - Data Validation

Located: `src/test/java/com/example/orangehrm/database/DatabaseTester.java`

Verify data changes made through UI or API.

#### Key Methods:

```java
// Initialize (JDBC)
DatabaseTester db = new DatabaseTester(
    "jdbc:mysql://localhost:3306/orangehrm",
    "root",
    "password"
);

// Query Execution
List<Map<String, Object>> results = db.executeQuery(
    "SELECT * FROM ohrm_employee WHERE emp_number = 1"
);

// Single Value Retrieval
String employeeName = db.getSingleValue(
    "SELECT emp_firstname FROM ohrm_employee WHERE emp_number = 1"
);

// Data Assertions
db.recordExists("ohrm_employee", Map.of("emp_number", "1"))
db.assertRowCount("ohrm_employee", 50)
db.assertColumnValue("ohrm_employee", "emp_status", "Active", 
                     "emp_number = 1")

// Cleanup
db.disconnect();
```

**Features:**
- JDBC support for any database
- Query execution and result retrieval
- Record existence checks
- Row count assertions
- Column value verification
- Automatic result mapping
- Clean connection management

**Example Usage:**
```java
@Then("employee record should be updated in database")
public void verify_db_update() {
    DatabaseTester db = new DatabaseTester(
        System.getenv("DB_URL"),
        System.getenv("DB_USER"),
        System.getenv("DB_PASS")
    );
    
    db.recordExists("ohrm_employee", 
                    Map.of("emp_firstname", "John"))
      .assertColumnValue("ohrm_employee", "emp_status", "Active", 
                         "emp_firstname = 'John'");
    
    db.disconnect();
}
```

---

### 4. **EnhancedConfigManager** - Configuration Management

Located: `src/test/java/com/example/orangehrm/config/EnhancedConfigManager.java`

Centralized configuration for multiple test environments (dev, staging, production).

#### Key Methods:

```java
// Singleton access
EnhancedConfigManager config = EnhancedConfigManager.getInstance();

// Get configuration values
String appUrl = config.getApplicationUrl();
int timeout = config.getNavigationTimeout();
boolean headless = config.isHeadlessBrowser();

// Environment-specific configuration
String environment = config.getActiveEnvironment();  // dev, staging, prod
config.setActiveEnvironment("prod");

// Runtime overrides
config.setOverride("browser", "firefox");
config.clearOverride("browser");

// Predefined properties
config.getApplicationUrl()              // application.url
config.getBrowserType()                 // browser
config.isHeadlessBrowser()              // browser.headless
config.getNavigationTimeout()           // navigation.timeout.ms
config.getElementTimeout()              // element.timeout.ms
config.getApiBaseUrl()                  // api.base.url
config.getDatabaseUrl()                 // database.url
config.getReportOutputPath()            // report.output.path
```

**Configuration Priority (Highest → Lowest):**
1. Runtime overrides `setOverride("key", "value")`
2. Environment-specific keys `key.environment`
3. Base property file keys
4. Default values in method calls

**Example in test.properties:**
```properties
# Base configuration
application.url=http://localhost:8080
browser=chrome
browser.headless=false
navigation.timeout.ms=60000
report.output.path=target/enhanced-test-report.html

# Environment-specific
application.url.prod=https://orangehrm.prod.com
browser.headless.prod=true
navigation.timeout.ms.prod=30000

# API Configuration
api.base.url=http://localhost:8080/api/v1
api.key=${API_KEY}

# Database Configuration
database.url=jdbc:mysql://localhost:3306/orangehrm
database.username=root
database.password=password
```

**Usage in Tests:**
```java
@Given("I navigate to application")
public void navigate_to_app() {
    String appUrl = EnhancedConfigManager.getInstance().getApplicationUrl();
    Hooks.page.navigate(appUrl);
}
```

---

### 5. **EnhancedReportGenerator** - Professional Reporting

Located: `src/test/java/com/example/orangehrm/utils/EnhancedReportGenerator.java`

Interactive HTML report with charts, timeline, and detailed metrics.

**Report Features:**

📊 **Statistics Dashboard:**
- Total tests executed
- Passed/Failed counts
- Pass rate percentage
- Average/Min/Max execution time

📈 **Visual Charts:**
- Pie chart (Pass/Fail distribution)
- Bar chart (Test comparison)
- Status timeline

📝 **Detailed Scenarios:**
- Expandable scenario cards
- Step-by-step execution details
- Status indicators
- Screenshots embedded for each step
- Failure reasons prominently displayed

🎨 **Professional UI:**
- Modern gradient design (purple/blue)
- Responsive CSS Grid
- Font Awesome icons
- Color-coded status markers
- Hover effects and smooth transitions

**Generated Report:**
```
target/enhanced-test-report.html
```

---

## 🧪 Step Definition Example with Professional Framework

```java
@Given("I am on the OrangeHRM login page")
public void navigate_to_login() {
    TestLogger.testStep("Navigate to login page");
    
    // Get app URL from config
    String appUrl = EnhancedConfigManager.getInstance()
                                        .getApplicationUrl();
    Hooks.page.navigate(appUrl);
    
    // Professional assertion
    assertions().elementIsVisible("input[name='username']")
               .withContext("Login form visibility");
}

@When("I login with API credentials from database")
public void login_with_db_credentials() {
    TestLogger.testStep("Fetch credentials from database");
    
    // Query database for test credentials
    DatabaseTester db = new DatabaseTester(
        config.getDatabaseUrl(),
        config.getDatabaseUsername(),
        config.getDatabasePassword()
    );
    
    String username = db.getSingleValue(
        "SELECT emp_email FROM ohrm_employee LIMIT 1"
    );
    
    // Login
    loginPage.login(username, "password");
    
    db.disconnect();
}

@Then("API should return updated user data")
public void verify_api_data() {
    TestLogger.testStep("Verify API returns updated data");
    
    // Make API call
    APITester api = new APITester(
        config.getApiBaseUrl()
    );
    api.setBearerToken(config.getApiKey());
    
    // Validate response
    api.get("/users/current")
       .assertStatusSuccess()
       .assertJsonField("status", "active")
       .assertBodyContains("user_id");
}
```

---

## 🚀 Running Tests with Professional Framework

### Run All Tests
```bash
mvn clean test
```

### Run Specific Tags
```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@regression"
```

### Run with Specific Environment
```bash
export TEST_ENV=prod
mvn clean test
```

### Run with Custom Configuration
```bash
mvn clean test \
  -DargLine="-Dapplication.url=https://custom-app.local" \
  -Dcucumber.filter.tags="@api"
```

---

## 📊 Report Viewing

### After Test Execution
```bash
# Windows
start target/enhanced-test-report.html

# macOS
open target/enhanced-test-report.html

# Linux
xdg-open target/enhanced-test-report.html
```

**Report includes:**
- ✅ Summary statistics
- 📈 Interactive charts
- 💾 Screenshots for every step
- 🔴 Failure details and error messages
- ⏱️ Execution timeline
- 📋 Detailed scenario information

---

## 🏗️ Framework Capabilities Summary

| Feature | Status | Location |
|---------|--------|----------|
| 🎯 UI Testing | ✅ | UIAssertions, Page Objects |
| 🌐 API Testing | ✅ | APITester |
| 💾 Database Testing | ✅ | DatabaseTester |
| ⚙️ Configuration | ✅ | EnhancedConfigManager |
| 📊 Reporting | ✅ | EnhancedReportGenerator |
| 📸 Screenshots | ✅ | ScreenshotHelper, Hooks |
| 🔍 Visual Testing | ✅ | AIVisualTestingHelper |
| 📝 Logging | ✅ | TestLogger |
| 🌍 Multi-Browser | ✅ | Hooks (Chrome support, others) |
| 🔄 Parallel Execution | ✅ | Maven configuration |
| 🎨 Modern UI | ✅ | EnhancedReportGenerator |

---

## 🔮 Future Enhancements

The framework is architected to support:

1. **Additional API Assertion Classes**
   - Response header validation
   - Schema validation
   - Performance assertions

2. **Performance Testing**
   - Response time assertions
   - Load testing integration
   - Metrics collection

3. **Mobile Testing**
   - Device emulation
   - Touch event handling
   - Mobile-specific assertions

4. **CI/CD Integration**
   - GitHub Actions workflows
   - Test artifacts archiving
   - Automated report publishing

5. **Advanced Reporting**
   - Historical trend analysis
   - Flaky test detection
   - Custom metrics dashboard

---

## 📖 Best Practices

1. **Use Configuration Manager** for all environment-specific values
2. **Use Professional Assertions** for readable, maintainable assertions
3. **Leverage Page Objects** for UI interaction abstraction
4. **Structure Tests as Scenarios** following Gherkin conventions
5. **Integrate API Tests** alongside UI tests for comprehensive validation
6. **Verify Data** using DatabaseTester after API/UI operations
7. **Review Reports** after each test run for metrics and trends
8. **Keep Tests Independent** - no data dependencies between scenarios
9. **Use Meaningful Logging** with TestLogger for debugging
10. **Document Test Scenarios** in feature files with clear descriptions

---

## 🆘 Troubleshooting

### Issue: Screenshots not appearing in report
**Solution:** Ensure `ScreenshotHelper.captureAndEmbedScreenshot()` is called after each step. Logs show file size verification.

### Issue: API tests timing out
**Solution:** Check `navigation.timeout.ms` and `element.timeout.ms` in configuration. Increase if necessary.

### Issue: Database connection failure
**Solution:** Verify database URL, username, password in `test.properties` and ensure connection string is correct for your JDBC driver.

### Issue: Configuration not loading
**Solution:** Ensure `test.properties` exists in `src/test/resources/` and file is readable.

---

## 📚 References

- [Playwright Documentation](https://playwright.dev/java/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Maven Testing Guide](https://maven.apache.org/plugins/maven-surefire-plugin/)
- [JDBC Database Connectivity](https://docs.oracle.com/javase/tutorial/jdbc/)

---

**Framework Version:** 2.0 (Professional Enterprise Edition)
**Last Updated:** 2025
**Status:** ✅ Production Ready
