@regression
Feature: Advanced Workflow Tests - OrangeHRM Complex Scenarios

  CONSOLIDATED: All advanced test scenarios have been moved to regression.feature
  for better organized test suite structure. This file is maintained for backward
  compatibility reference only.

  Test Suite Organization (Final Structure):
  ============================================
  
  smoke.feature:
  - Contains exactly 10 critical smoke tests
  - Tag: @smoke
  - Purpose: Quick verification of core functionality after deployment
  - Execution: Fast, covers essential workflows
  - Tests: Test 1-10 (login, navigation, menus, logout)
  
  regression.feature:
  - Contains 40+ regression and advanced workflow tests
  - Tags: @regression (with secondary tags like @complex, @detailed, @resilience)
  - Purpose: Comprehensive coverage of all modules and edge cases
  - Execution: Full validation suite
  - Includes: Original regression tests + relocated advanced tests
  
  How to run:
  - Smoke tests: mvn test -Dcucumber.filter.tags=@smoke
  - Regression tests: mvn test -Dcucumber.filter.tags=@regression
  - All tests: mvn test

  See regression.feature for complete test definitions.

