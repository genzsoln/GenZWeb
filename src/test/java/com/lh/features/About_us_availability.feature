@test
Feature: Testing the availability of About us link

  Scenario: Verify About Us Page Availability
    Given User is on the Genz homepage
    When User clicks on the About Us link
    Then About Us page should be accessible