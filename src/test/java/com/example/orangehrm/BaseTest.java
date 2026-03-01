package com.example.orangehrm;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.example.orangehrm.config.ConfigManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;

    @BeforeEach
    public void setUp() {
        // Create a fresh browser stack for each test method to avoid state leakage.
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(java.util.Arrays.asList("--start-maximized")));
        page = browser.newPage();
        // Start each test from the login page for deterministic test setup.
        page.navigate(ConfigManager.getAppUrl());
    }

    @AfterEach
    public void tearDown() {
        // Close browser first (child resource), then Playwright engine.
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}