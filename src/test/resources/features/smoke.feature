@smoke
Feature: Smoke Test Suite - OrangeHRM Critical Functionality

  This smoke test suite contains 10 critical test cases to verify core
  OrangeHRM functionality after deployment. These tests cover login, navigation,
  dashboard access, and basic HR operations. Each scenario has detailed steps
  to validate specific functionality and capture screenshots for test reports.

  @smoke @test1 @login_verification
  Scenario: Test 1 - Verify OrangeHRM login page loads with all required elements
    Given I am on the OrangeHRM login page
    Then the page title should contain "OrangeHRM"
    And I should see the username field
    And I should see the password field
    And I should see the login button

  @smoke @test2 @login_workflow
  Scenario: Test 2 - Complete login workflow with valid credentials
    Given I am on the OrangeHRM login page
    When I verify login page elements are visible
    And I login with valid credentials
    Then I should see the dashboard
    And the URL should contain "dashboard"
    And I should see the user welcome message

  @smoke @test3 @navigation @admin
  Scenario: Test 3 - Navigate to Admin module and verify page load
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I should see the main menu
    When I navigate to "Admin" menu
    Then the URL should contain "admin"
    And the Admin page should load successfully

  @smoke @test4 @navigation @pim
  Scenario: Test 4 - Navigate to PIM module and verify functionality
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "PIM" menu
    Then the URL should contain "pim"
    And I should see PIM module elements

  @smoke @test5 @navigation @leave
  Scenario: Test 5 - Navigate to Leave module and verify menu
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I verify dashboard menu is visible
    When I navigate to "Leave" menu
    Then the URL should contain "leave"
    And I should see leave module content

  @smoke @test6 @navigation @time
  Scenario: Test 6 - Navigate to Time module with verification
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "Time" menu
    Then the URL should contain "time"
    And the page should load successfully
    And I should see time module elements

  @smoke @test7 @navigation @recruitment
  Scenario: Test 7 - Navigate to Recruitment module
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "Recruitment" menu
    Then the URL should contain "recruitment"
    And I should see recruitment page content

  @smoke @test8 @profile_menu
  Scenario: Test 8 - Verify user profile dropdown menu functionality
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I click on my profile
    Then I should see user menu options
    And I should see the profile option
    And I should see the logout option

  @smoke @test9 @menu_verification
  Scenario: Test 9 - Verify all main menu items are accessible
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I verify all main menu items are present
    And I should see at least 8 menu items
    And each menu item should be clickable

  @smoke @test10 @logout_workflow
  Scenario: Test 10 - Complete login and logout workflow
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And wait for "2" seconds
    When I click on my profile
    Then I should see user menu options
    When I logout
    Then I should be on the login page
    And the login page should display properly

  @smoke @test11 @auth_resilience @senior
  Scenario: Test 11 - Validate authentication resilience with negative then positive login
    Given I am on the OrangeHRM login page
    Then I should see the username field
    And I should see the password field
    And I should see the login button
    When I login with username "InvalidUser" and password "InvalidPass123"
    Then I should see error message
    And the URL should contain "auth/login"
    And I should see the username field
    And I should see the password field
    When I login with valid credentials
    Then I should see the dashboard
    And the URL should contain "dashboard"
    And I should see the user welcome message

  @smoke @test12 @cross_module @senior
  Scenario: Test 12 - Verify session continuity during cross-module traversal
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I should see the main menu
    When I navigate to "Admin" menu
    Then the URL should contain "admin"
    And the Admin page should load successfully
    When I navigate to "PIM" menu
    Then the URL should contain "pim"
    And I should see PIM module elements
    When I navigate to "Leave" menu
    Then the URL should contain "leave"
    And I should see leave module content
    When I navigate to "Dashboard" menu
    Then the URL should contain "dashboard"
    And I should see the user welcome message

  @smoke @test13 @profile_controls @senior
  Scenario: Test 13 - Validate profile controls remain stable after module navigation
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I should see the main menu
    When I navigate to "Recruitment" menu
    Then the URL should contain "recruitment"
    And I should see recruitment page content
    When I click on my profile
    Then I should see user menu options
    And I should see the profile option
    And I should see the logout option
    When I navigate to "Time" menu
    Then the URL should contain "time"
    And I should see time module elements
    When I click on my profile
    Then I should see user menu options
    And I should see the logout option

  @smoke @test14 @navigation_hardening @senior
  Scenario: Test 14 - Validate critical modules are reachable in repeated navigation sequence
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I verify all main menu items are present
    And each menu item should be clickable
    When I navigate to "Time" menu
    Then the URL should contain "time"
    And the page should load successfully
    And I should see time module elements
    When I navigate to "Admin" menu
    Then the URL should contain "admin"
    And the Admin page should load successfully
    When I navigate to "PIM" menu
    Then the URL should contain "pim"
    And I should see PIM module elements
    And I should see at least 8 menu items

  @smoke @test15 @end_to_end_logout @senior
  Scenario: Test 15 - End-to-end login, module verification, and secure logout
    Given I am on the OrangeHRM login page
    When I verify login page elements are visible
    And I login with valid credentials
    Then I should see the dashboard
    And I should see the user welcome message
    When I navigate to "Leave" menu
    Then the URL should contain "leave"
    And I should see leave module content
    When I navigate to "Dashboard" menu
    Then the URL should contain "dashboard"
    And I should see the main menu
    When I click on my profile
    Then I should see user menu options
    And I should see the logout option
    When I logout
    Then I should be on the login page
    And the login page should display properly
