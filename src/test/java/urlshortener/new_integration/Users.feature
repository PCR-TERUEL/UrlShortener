Feature: Manage user
  Scenario: Access index
    Given Open the Chrome and launch the application
    And Welcome page will be displayed
    Then Closes Chrome

  Scenario: User sing up
    Given Open the Chrome and launch the application
    And Welcome page will be displayed
    When User visits login page
    And User input and submit signup form
    Then User is registered
    Then Closes Chrome

  Scenario: User login
  Given Open the Chrome and launch the application
    And Welcome page will be displayed
    When User visits login page
    And User input and submit login form
    Then Panel page will be displayed
    Then Closes Chrome

  Scenario: User shorts a URL
    Given Open the Chrome and launch the application
    And Welcome page will be displayed
    When User visits login page
    And User input and submit login form
    Then Panel page will be displayed
    When User input and submit a URL
    Then Gets a validated and shorted URL
    Then Closes Chrome
