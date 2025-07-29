@Login
Feature: TTA App - Android Login


  Background:
    Given Validate the login screen or if on home screen then navigate to login screen

  @Login_Promo_001
  Scenario: Successful login with valid credentials
    When User enters mobile number "9289656187"
    And User clicks login button
    Then User enters OTP "123456"
    And User clicks outside of you can change the app language from here popup
    And User clicks on close button on Don't miss out on offers! popup
    And User clicks on close button on want to continue your consultation popup
    Then User clicks on close button on popular popup


  @Login_Repeat_001
  Scenario: Successful login with valid credentials
    When User enters mobile number "9289656187"
    And User clicks login button
    Then User enters OTP "123456"
    And User clicks outside of you can change the app language from here popup
    And User clicks on close button on Don't miss out on offers! popup
