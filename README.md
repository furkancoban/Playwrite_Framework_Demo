# OrangeHRM Playwright Test Framework

This is a test automation framework for OrangeHRM built with Playwright, Cucumber, and Java. It's designed to be maintainable, scalable, and easy to understand.

## What's Inside

The framework follows the Page Object Model pattern with:
- **Page objects** for each page (LoginPage, DashboardPage, etc.)
- **Cucumber feature files** with BDD scenarios 
- **Centralized configuration** for credentials and settings
- **Structured logging** for debugging and monitoring
- **Example test data** with 30+ test scenarios (smoke, regression, advanced)

## Quick Start

### Prerequisites
- Java 11 or newer
- Maven 3.6 or newer

### Running Tests

```bash
# Run all tests
mvn test

# Run only smoke tests (fast sanity check ~5 minutes)
mvn test -Dcucumber.filter.tags="@smoke"

# Run advanced tests
mvn test -Dcucumber.filter.tags="@advanced"

# Run with visible browser (instead of headless)
mvn test -Dtest.headless=false

# Run with step delays for observation
mvn test -Dtest.headless=false -Dtest.step.delay=1000
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

Update credentials in `src/test/resources/test.properties`:

```properties
app.url=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
app.username=Admin
app.password=admin123
```

## Test Scenarios

- **Smoke tests** (10 scenarios): Basic login, navigation, dashboard access
- **Regression tests** (8 scenarios): Module navigation, menu verification
- **Advanced tests** (12+ scenarios): Complex workflows, user journeys, error recovery

Run specific scenarios by tag:
```bash
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@regression"
mvn test -Dcucumber.filter.tags="@advanced"
```

## Adding New Tests

1. Add a scenario to one of the feature files in `src/test/resources/features/`
2. Map any new Gherkin steps to Java methods in `StepDefinitions.java`
3. Create page objects as needed (extend `BasePage`)
4. Tag with `@smoke`, `@regression`, or `@advanced`
5. Run with: `mvn test -Dcucumber.filter.tags="@yourTag"`

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

## Logs

Test execution logs are written to `target/logs/test-execution.log`. Console output is abbreviated; check the log file for full details.

## Documentation

For detailed architecture, patterns, and advanced examples, see:
- [FRAMEWORK_GUIDE.md](FRAMEWORK_GUIDE.md) - Framework architecture and design
- [ADVANCED_EXAMPLES.md](ADVANCED_EXAMPLES.md) - Code patterns and examples

## Troubleshooting

- **Elements not found**: Check selectors in page objects, or run with `-Dtest.headless=false` to watch the browser
- **Login failures**: Verify credentials in test.properties and check that OrangeHRM is accessible
- **Slow tests**: Use headless mode (it's the default) or reduce step delays
- **Timing issues**: Increase `element.wait.timeout` in test.properties if tests are flaky

## Browser/Environment Support

- Default: Chromium headless mode 
- Override with `-Dtest.browser=firefox` or `-Dtest.browser=webkit`
- Show browser with `-Dtest.headless=false`
- Add step delays with `-Dtest.step.delay=500` (milliseconds)
