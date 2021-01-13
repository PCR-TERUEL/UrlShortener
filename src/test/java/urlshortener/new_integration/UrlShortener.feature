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
    When User input and submit a valid URL
    Then Gets a validated and shorted URL
    When User input and submit an invalid URL
    Then Gets an invalidated shorted URL
    Then Closes Chrome

  Scenario: User access shortedURL
    Given Open the Chrome and launch the application
    And Welcome page will be displayed
    When User visits login page
    And User input and submit login form
    Then Panel page will be displayed
    Then Is redirected to target URL successfully
    Then Closes Chrome

  Scenario: Number of clicks is incremented
    Given Open the Chrome and launch the application
    And Welcome page will be displayed
    When User visits login page
    And User input and submit login form
    Then Panel page will be displayed
    And Number of clicks is incremented by 1
    Then Closes Chrome

  Scenario: Shorted expired URL will not be accessible
    Given Open the Chrome and launch the application
    And Welcome page will be displayed
    When User visits login page
    And User input and submit login form
    Then Panel page will be displayed
    When Inputs a expired link
    And Visits the expired URL
    Then Closes Chrome


  Scenario: CSV Short
    Given Open the Chrome and launch the application
    And Welcome page will be displayed
    When User visits login page
    And User input and submit login form
    Then Panel page will be displayed
    When Uploads a CSV file
    Then All URL's from the CSV are properly loaded
    Then Closes Chrome

