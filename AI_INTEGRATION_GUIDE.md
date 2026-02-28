# AI Integration Guide for OrangeHRM Test Framework

## Overview

This framework now includes two AI-powered testing features:

### 1. **Visual AI Testing with Screenshot Capture**
- Automatically captures screenshots at key test points
- Enables integration with AI visual testing services (Applitools Eyes, Percy, AWS Lookout)
- Detects visual regressions that normal assertions miss
- Saves checkpoints to `target/visual-checkpoints/` for analysis

### 2. **Self-Healing Locators with AI Learning**
- Automatically finds elements even if CSS selectors break
- Learns from successful selections and caches them
- Tries multiple fallback strategies (text patterns, roles, attributes)
- Dramatically reduces test maintenance when UI changes
- Enables integration with Healenium for enterprise-grade element recovery

---

## Visual AI Testing Setup

### Basic Usage (Built-in)
The framework automatically captures visual checkpoints:
```
✓ On page loads
✓ After form fills
✓ After button clicks
✓ After navigation
```

Checkpoints are saved to: `target/visual-checkpoints/`

### Advanced Usage - Applitools Eyes Integration

#### Step 1: Sign Up
Visit [Applitools](https://applitools.com) and create a free account.

#### Step 2: Get API Key
1. Log in to Applitools dashboard
2. Go to Account Settings
3. Copy your API Key

#### Step 4: Set Environment Variable
```bash
# Windows PowerShell
$env:APPLITOOLS_API_KEY="your-api-key-here"

# Windows Command Prompt
set APPLITOOLS_API_KEY=your-api-key-here

# Linux/Mac
export APPLITOOLS_API_KEY=your-api-key-here
```

#### Step 4: Add Dependency (Optional)
Add to `pom.xml`:
```xml
<dependency>
    <groupId>com.applitools</groupId>
    <artifactId>eyes-playwright-java5</artifactId>
    <version>1.10.0</version>
    <scope>test</scope>
</dependency>
```

#### Step 5: Update AIVisualTestingHelper.java
Uncomment the Applitools integration code:
```java
public static void initializeVisualTesting(Page page, String scenarioName) {
    try {
        Eyes eyes = new Eyes();
        eyes.open(page, "OrangeHRM", scenarioName);
        // ... rest of code
    }
}
```

#### Step 6: Run Tests
```bash
mvn test "-Dcucumber.filter.tags=@smoke"
```

The framework will now:
1. Take visual snapshots during test execution
2. Compare them with baseline images
3. Report visual differences in Applitools dashboard
4. Flag any visual regressions automatically

---

## Self-Healing Locators Setup

### Built-in Self-Healing (No Additional Setup Required)

The framework automatically attempts to heal broken selectors:

#### How It Works
1. **First Attempt**: Try primary selector
   ```java
   page.locator("input[name='username']")
   ```

2. **On Failure**: Try cached learned selector
   ```java
   page.locator("input[name*='user']") // More flexible pattern
   ```

3. **Still Failing**: Try alternative strategies:
   - Text pattern matching: `page.locator("text=Login")`
   - Role-based: `page.locator("[role='button']")`
   - Partial attribute: `page.locator("input[name*='user']")`

4. **Healing Complete**: Cache learned selector for next run

#### Example Log Output
```
📍 STEP: Click login button
AI attempting to heal selector for: login button
AI healed with text pattern: Login
🔧 Cached selector for 'login button': text=Login
✅ Healed selectors - count: 1
```

### View Healing Statistics
Every test run prints healing statistics:
```
=== AI Selector Healing Statistics ===
Total healed selectors: 3
Total healing attempts: 5
  - login button → text=Login (attempts: 2)
  - submit button → button[type='submit'] (attempts: 1)
  - username field → input[name*='user'] (attempts: 2)
```

### Advanced Usage - Healenium Integration

#### Step 1: Add Dependency
```xml
<dependency>
    <groupId>com.epam.healenium</groupId>
    <artifactId>healenium-playwright</artifactId>
    <version>0.3.0</version>
    <scope>test</scope>
</dependency>
```

#### Step 2: Update AIElementLocator.java
Replace the standalone implementation with:
```java
import com.epam.healenium.SelfHealingDriver;

public class AIElementLocator {
    private static SelfHealingDriver healingDriver;
    
    public static void initializeSelfHealing(Page page) {
        healingDriver = new SelfHealingDriver(page);
        TestLogger.info("Healenium self-healing initialized");
    }
    
    public static Locator findElement(Page page, String selector, String description) {
        return healingDriver.findElement(selector);
    }
}
```

#### Step 3: Run Tests
```bash
mvn test "-Dcucumber.filter.tags=@smoke"
```

Healenium will now:
- Learn element locations automatically
- Detect and adapt to UI changes
- Maintain healing history
- Provide detailed healing reports

---

## Using AI Features in Tests

### Capturing Visual Checkpoints Manually

```java
// In step definitions or page objects:
AIVisualTestingHelper.checkVisualAppearance(page, "Login form filled");
AIVisualTestingHelper.checkFullPage(page, "Dashboard loaded");
AIVisualTestingHelper.checkRegion(page, "tr:first-child", "First table row");
```

### Disabling AI Features for Specific Tests

```java
@Before
public void setup() {
    AIElementLocator.disableHealing(); // Disable self-healing for legacy tests
    AIVisualTestingHelper.disableVisualTesting();
}

@After
public void cleanup() {
    AIElementLocator.enableHealing(); // Re-enable for other tests
    AIVisualTestingHelper.enableVisualTesting();
}
```

### Clearing Healing Cache

If you update selectors, clear the cache:
```java
AIElementLocator.clearCache();
```

---

## Running Tests with AI Features

### Basic Smoke Tests
```bash
mvn test "-Dcucumber.filter.tags=@smoke"
```

### With Visible Browser + AI Features
```bash
mvn test "-Dcucumber.filter.tags=@smoke" "-Dtest.headless=false"
```

### With Step Delays for Observation
```bash
mvn test "-Dcucumber.filter.tags=@smoke" "-Dtest.headless=false" "-Dtest.step.delay=1000"
```

---

## Output Locations

| Feature | Location | Description |
|---------|----------|-------------|
| Visual Checkpoints | `target/visual-checkpoints/` | Captured screenshot images |
| Healing Logs | `target/heal-logs/` | Self-healing attempt logs |
| Test Logs | `target/logs/test-execution.log` | Full test execution details |
| HTML Report | `target/cucumber-report.html` | Cucumber test report |
| JSON Report | `target/cucumber.json` | JSON test results |

---

## Troubleshooting

### Visual Testing Not Working?
1. Check `AIVisualTestingHelper.isVisualTestingActive()`
2. Verify `target/visual-checkpoints/` is created
3. For Applitools: verify `APPLITOOLS_API_KEY` environment variable is set

### Self-Healing Not Working?
1. Check `AIElementLocator.isHealingEnabled()`
2. Review `=== AI Selector Healing Statistics ===` in logs
3. Verify selectors are unique enough for healing to succeed

### Tests Failing Even With AI?
1. AI healing has limits - some broken selectors can't be recovered
2. Update the original selector as fallback
3. Add more specific element descriptions for better healing
4. Check if element exists before attempting click/fill

---

## Best Practices

1. **Always provide element descriptions**
   ```java
   AIElementLocator.findElement(page, selector, "login submit button")
   // Better healing with human-readable names
   ```

2. **Use visual checkpoints at key points**
   ```java
   AIVisualTestingHelper.checkVisualAppearance(page, "After form submit");
   ```

3. **Monitor healing statistics**
   - Review which selectors were healed
   - Update unstable selectors proactively
   - Cache learned selectors in comments

4. **Test with and without AI**
   - Ensure tests pass without healing
   - Use healing as safety net, not primary solution
   - Fix root cause selector issues

5. **Keep selectors maintainable**
   - Use semantic selectors (role, aria-label)
   - Avoid brittle positional selectors
   - Test selector stability before committing

---

## Performance Impact

- **Visual Testing**: +500ms per checkpoint (screenshot)
- **Self-Healing**: +100-300ms per healed selector (alternative attempts)
- **Unaffected**: Normal test execution with working selectors

Disable AI features if performance is critical:
```bash
mvn test "-Dtest.ai.features=disabled"
```

---

## Integrating with CI/CD

### GitHub Actions Example
```yaml
env:
  APPLITOOLS_API_KEY: ${{ secrets.APPLITOOLS_API_KEY }}

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '11'
      - run: mvn test "-Dcucumber.filter.tags=@smoke"
```

### Jenkins Example
```groovy
pipeline {
    environment {
        APPLITOOLS_API_KEY = credentials('applitools-key')
    }
    stages {
        stage('Test') {
            steps {
                sh 'mvn test "-Dcucumber.filter.tags=@smoke"'
            }
        }
    }
}
```

---

## Additional Resources

- [Applitools Documentation](https://applitools.com/docs)
- [Healenium GitHub](https://github.com/healenium/healenium-web)
- [Playwright Documentation](https://playwright.dev/java/)
- [Visual Testing Best Practices](https://applitools.com/blog/visual-testing/)
