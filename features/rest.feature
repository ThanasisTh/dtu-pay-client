Feature: Test the rest service

  Scenario: Creating a customer and adding him to the system
    Given there is a customer in the bank with credentials "Bob" "Hansen" with cpr number "392930-40281" and account id "3892487921320"
    And all the credentials are valid
    When We register the customer to dtu pay using the rest service with source path /register/customer
    Then we get a confirmation that the customer was registered

  Scenario: Creating a merchant and adding him to the system
    Given there is a merchant in the bank with credentials "Alexandra Desypri" with uuid number "19237-19271" and account id "473297283013"
    And all the credentials are valid
    When We register the merchant to dtu pay using the rest service with source path /register/merchant
    Then we get a confirmation that the merchant was registered

   Scenario: A customer requests tokens
     Given there is a registered customer in dtuPay with cprNumber "123456-78999"
     When he requests 5 tokens
     Then we get a confirmation that the system gave him tokens



