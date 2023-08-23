@test
Feature: Testing the availability of the Genz Website

  Scenario: Verify Homepage Availability
    Given User is on the Genz homepage
    Then Homepage should be accessible