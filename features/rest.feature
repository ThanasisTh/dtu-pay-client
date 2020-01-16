Feature: Test the rest service

  Scenario: Creating a customer and adding him to the system
    Given there is a customer in the bank with credentials "Bob" "Hansen" with cpr number "392930-40281" and account id "3892487921320"
    And all the credentials are valid
    When We register the customer to dtu pay using the rest service with source path /register/customer
    Then we get a confirmation that the customer was registered



