@advanced
Feature: Advanced Workflow Tests - OrangeHRM Complex Scenarios

  These scenarios test complex, multi-step workflows and edge cases that
  require advanced validation and error handling

  @advanced @login_validation @critical
  Scenario: Login validation with invalid credentials
    Given I am on the OrangeHRM login page
    When I login with username "InvalidUser" and password "InvalidPassword"
    Then I should see error message

  @advanced @multi_module_workflow @complex
  Scenario: User navigates through multiple modules sequentially
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "Admin" menu
    Then the URL should contain "admin"
    When I wait for 1 seconds
    When I navigate to "PIM" menu
    Then the URL should contain "pim"
    When I wait for 1 seconds
    When I navigate to "Leave" menu
    Then the URL should contain "leave"
    When I wait for 1 seconds
    When I navigate to "Time" menu
    Then the URL should contain "time"
    When I wait for 1 seconds
    When I navigate to "Dashboard" menu
    Then the URL should contain "dashboard"

  @advanced @profile_menu_workflow @detailed
  Scenario: User profile menu interactions
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I click on my profile
    Then I should see user menu options
    When I logout
    Then I should be on the login page

  @advanced @menu_verification @critical
  Scenario: Verify all OrangeHRM main menu items are accessible
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I should see at least 8 menu items
    When I verify menu item "Admin" exists
    When I verify menu item "PIM" exists
    When I verify menu item "Leave" exists
    When I verify menu item "Time" exists
    When I verify menu item "Recruitment" exists

  @advanced @dashboard_content_validation @detailed
  Scenario: Dashboard content validation after login
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I check dashboard welcome message
    And the page title should contain "OrangeHRM"
    And the URL should contain "dashboard"

  @advanced @page_stability @performance
  Scenario: Page stability and load time verification
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I wait for 2 seconds
    And the page title should contain "OrangeHRM"
    And the URL should contain "dashboard"
    When I navigate to "Admin" menu
    When I wait for 2 seconds
    Then the URL should contain "admin"

  @advanced @sequential_navigation @workflow
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
      | Time        | time        |
      | Recruitment | recruitment |
      | Maintenance | maintenance |

  @advanced @error_recovery @resilience
  Scenario: Error recovery workflow
    Given I am on the OrangeHRM login page
    When I login with username "AdminWrong" and password "admin123"
    Then I should see error message
    When I login with valid credentials
    Then I should see the dashboard

  @advanced @complete_user_journey @critical
  Scenario: Complete user journey - Full workflow from login to logout
    Given I am on the OrangeHRM login page
    And the page title should contain "OrangeHRM"
    When I login with valid credentials
    Then I should see the dashboard
    And I should see at least 8 menu items
    When I navigate to "Admin" menu
    Then the URL should contain "admin"
    When I navigate to "Dashboard" menu
    Then the URL should contain "dashboard"
    When I click on my profile
    Then I should see user menu options
    When I logout
    Then I should be on the login page
    And the URL should contain "auth/login"

  @advanced @concurrent_menu_switches @complex
  Scenario: Rapid menu navigation without delays
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "Admin" menu
    When I navigate to "PIM" menu
    When I navigate to "Leave" menu
    When I navigate to "Time" menu
    When I navigate to "Recruitment" menu
    When I navigate to "Dashboard" menu
    Then the URL should contain "dashboard"

  @advanced @data_persistence @workflow
  Scenario: Verify data persistence across navigation
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I check dashboard welcome message
    When I navigate to "Admin" menu
    When I navigate to "Dashboard" menu
    Then I should see the dashboard

  @advanced @page_element_validation @detailed
  Scenario: Comprehensive page element validation
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I should see the "Dashboard" page header
    And I should see at least 8 menu items
    Then the page title should contain "OrangeHRM"
