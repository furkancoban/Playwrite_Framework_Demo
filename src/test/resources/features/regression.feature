@regression
Feature: Comprehensive regression tests for OrangeHRM

  These scenarios exercise a wider variety of OrangeHRM modules, navigation,
  and workflows to catch potential regressions after updates or deployments.

  Scenario Outline: Navigate to different modules
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "<module>" menu
    Then the URL should contain "<urlPart>"

    Examples:
      | module      | urlPart     |
      | Admin       | admin       |
      | PIM         | pim         |
      | Leave       | leave       |
      | Time        | time        |
      | Recruitment | recruitment |
      | My Info     | myinfo      |
      | Performance | performance |
      | Dashboard   | dashboard   |

  @regression @login_workflow
  Scenario: Complete login and logout workflow
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And the URL should contain "dashboard"
    When I logout
    Then I should be on the login page

  @regression @navigation
  Scenario: Verify all main menu items are accessible
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    And I should see at least 8 menu items

  @regression @user_profile
  Scenario: Access user profile menu
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I click on my profile
    Then I should see user menu options

  @regression @detailed
  Scenario: Navigate through multiple modules in sequence
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "Admin" menu
    Then the URL should contain "admin"
    When I navigate to "PIM" menu
    Then the URL should contain "pim"
    When I navigate to "Leave" menu
    Then the URL should contain "leave"
    When I navigate to "Dashboard" menu
    Then the URL should contain "dashboard"

  @regression @page_load
  Scenario Outline: Verify page load times for different modules
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I navigate to "<module>" menu
    And I wait for 2 seconds
    Then the URL should contain "<urlPart>"

    Examples:
      | module      | urlPart     |
      | Admin       | admin       |
      | PIM         | pim         |
      | Leave       | leave       |
      | Time        | time        |
      | Recruitment | recruitment |

  @regression @stability
  Scenario: Dashboard stability check
    Given I am on the OrangeHRM login page
    When I login with valid credentials
    Then I should see the dashboard
    When I wait for 3 seconds
    Then the URL should contain "dashboard"
    And the page title should contain "OrangeHRM"

  @regression @critical
  Scenario: Verify login page elements
    Given I am on the OrangeHRM login page
    Then the page title should contain "OrangeHRM"
    And the URL should contain "auth/login"

