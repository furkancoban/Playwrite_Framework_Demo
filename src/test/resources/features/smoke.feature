@smoke
Feature: Smoke Test Suite - OrangeHRM Critical Functionality

  This is our smoke test suite - just 10 critical tests to make sure nothing's
  completely broken after a deployment. We're checking login, basic navigation,
  and that the main modules load. If these pass, we're good to go deeper.
  Each test grabs screenshots so you can see what happened in the reports.

  @smoke @test1 @login_verification
  Scenario: Test 1 - Make sure the login page loads properly
    Given I am on the OrangeHRM login page
    Then the page title should contain "OrangeHRM"
    And I should see the username field
    And I should see the password field
    And I should see the login button

  @smoke @test2 @login_workflow
  Scenario: Test 2 - Full login flow with valid credentials
    Given I am on the OrangeHRM login page
    When I verify login page elements are visible
    And I login with valid credentials
    Then I should see the dashboard
    And the URL should contain "dashboard"
    And I should see the user welcome message

  @smoke @test3 @navigation @admin
  Scenario: Test 3 - Check that Admin module loads
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I should see the main menu
    When I navigate to "Admin" menu
    Then the URL should contain "admin"
    And the Admin page should load successfully

  @smoke @test4 @navigation @pim
  Scenario: Test 4 - Check PIM module works
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "PIM" menu
    Then the URL should contain "pim"
    And I should see PIM module elements

  @smoke @test5 @navigation @leave
  Scenario: Test 5 - Navigate to Leave module
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I verify dashboard menu is visible
    When I navigate to "Leave" menu
    Then the URL should contain "leave"
    And I should see leave module content

  @smoke @test6 @navigation @time
  Scenario: Test 6 - Make sure Time module loads
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
  Scenario: Test 8 - Check user profile dropdown
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I click on my profile
    Then I should see user menu options
    And I should see the profile option
    And I should see the logout option

  @smoke @test9 @menu_verification
  Scenario: Test 9 - Make sure all menu items are there
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I verify all main menu items are present
    And I should see at least 8 menu items
    And each menu item should be clickable

  @smoke @test10 @logout_workflow
  Scenario: Test 10 - Login and logout flow
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And wait for "2" seconds
    When I click on my profile
    Then I should see user menu options
    When I logout
    Then I should be on the login page
    And the login page should display properly
