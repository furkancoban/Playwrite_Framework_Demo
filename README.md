# OrangeHRM Playwright Test Framework

Hey! This is our test automation framework for OrangeHRM. It uses Playwright for browser automation, Cucumber for writing tests in plain English, and Java as the backbone. I've tried to keep everything clean and organized so it's easy to jump in and add new tests.

## What's in Here

The framework uses the Page Object Model pattern, which basically means:
- **Page objects** - One class per page (LoginPage, DashboardPage, etc.) to keep selectors organized
- **Cucumber feature files** - Tests written in plain English that anyone can read
- **Config files** - All your credentials and settings in one place
- **Logging** - Everything gets logged so you can debug when things break
- **Test suites** - 10 smoke tests for quick checks, plus 40+ regression tests for thorough coverage

## Getting Started

### What You'll Need
- Java 11 or higher
- Maven 3.6 or higher

### Running the Tests

Note: In PowerShell, you need to quote the properties like this:

```bash
# Run everything
mvn test

# Just the smoke tests (takes about 5 minutes - good for quick sanity checks)
mvn "-Dcucumber.filter.tags=@smoke" test

# Regression suite (the full test battery)
mvn "-Dcucumber.filter.tags=@regression" test

# Want to see what's happening? Run with the browser visible
mvn "-Dtest.headless=false" test

# Slow things down if you need to watch each step
mvn "-Dtest.headless=false" "-Dtest.step.delay=1000" test
```

## Project Structure

```
src/test/
├── java/com/example/orangehrm/
│   ├── config/ConfigManager.java       # Configuration management
│   ├── context/TestContext.java        # Shared test data
│   ├── pages/                          # Page objects (LoginPage, DashboardPage, etc.)
│   ├── steps/
│   │   ├── Hooks.java                  # Test setup/teardown
│   │   └── StepDefinitions.java        # BDD step implementations
│   └── utils/TestLogger.java           # Logging utility
└── resources/
    ├── features/                       # Cucumber feature files
    ├── test.properties                 # Test configuration
    └── log4j2.xml                      # Logging configuration
```

## Configuration

All your settings live in `src/test/resources/test.properties`. You can change the URL, credentials, timeouts, whatever you need:

```properties
app.url=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
app.username=Admin
app.password=admin123
page.load.timeout=15000
element.wait.timeout=8000
```

## Test Suites

- **Smoke tests** (10 scenarios) - The critical stuff: login, basic navigation, making sure nothing's completely broken
- **Regression tests** (40+ scenarios) - Everything else: module navigation, workflows, edge cases, the whole nine yards

Run them by tag:
```bash
mvn "-Dcucumber.filter.tags=@smoke" test
mvn "-Dcucumber.filter.tags=@regression" test
```

## Adding New Tests

1. Write your scenario in one of the feature files (`src/test/resources/features/`)
2. If you use new steps, add them to `StepDefinitions.java`
3. Need a new page? Create a class that extends `BasePage`
4. Tag it with `@smoke` (for critical stuff) or `@regression` (for everything else)
5. Run it: `mvn "-Dcucumber.filter.tags=@yourTag" test`

## Extending the Framework

### Adding a New Page Object

```java
public class YourPage extends BasePage {
    private static final String YOUR_ELEMENT = "your selector";
    
    public YourPage(Page page) {
        super(page);
    }
    
    public void yourMethod() {
        clickElement(YOUR_ELEMENT);
    }
}
```

### Adding a New Step

```java
@When("I do something")
public void i_do_something() {
    TestLogger.testStep("Doing something");
    // implementation
    TestLogger.assertion("Something done");
}
```

## Logs and Reports

Full test logs go to `target/logs/test-execution.log`. The console shows abbreviated output, but if you need details, that's where to look.

After a test run, you'll find reports in:
- `target/cucumber-report.html` - The main Cucumber report
- `target/test-execution-report.html` - Alternative HTML report
- `target/screenshots/` - Screenshots from test steps

## Troubleshooting

- **Can't find elements?** - Check the selectors in your page objects, or run with `-Dtest.headless=false` to actually see what the browser is doing
- **Login not working?** - Double-check the credentials in test.properties. Also make sure OrangeHRM is actually up and running
- **Tests are slow?** - Headless mode (the default) is faster. Also check your internet connection since we're hitting a live site
- **Random failures?** - Could be timing issues. Try bumping up `element.wait.timeout` in test.properties. External demo sites can be flaky sometimes.

## Browser Options

- By default, tests run in Chromium headless mode (fastest)
- Want Firefox or Safari? Use `-Dtest.browser=firefox` or `-Dtest.browser=webkit`
- Need to see what's happening? Add `-Dtest.headless=false`
- Want to slow things down? Use `-Dtest.step.delay=500` (in milliseconds)
