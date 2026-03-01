package com.example.orangehrm;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Cucumber test runner for the OrangeHRM Playwright test framework.
 * This runner discovers and executes all feature files in the features directory.
 */
@Suite
// Discover all feature files under src/test/resources/features
@SelectClasspathResource("features")
// Bind step definitions/hooks package
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.orangehrm.steps")
// Emit both human-readable HTML and machine-readable JSON outputs
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-report.html,json:target/cucumber.json")
public class CucumberTest {
    // Runner class - no implementation needed
}
