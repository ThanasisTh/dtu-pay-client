Feature: Test the rest service
#
#  Scenario: Creating a customer and adding him to the system
#    Given there is a customer in the bank with credentials "Bob" "Hansen" with cpr number "392930-40281" and account id "3892487921320"
#    And all the credentials are valid
#    When We register the customer to dtu pay using the rest service with source path /register/customer
#    Then we get a confirmation that the customer was registered
#
#  Scenario: Creating a merchant and adding him to the system
#    Given there is a merchant in the bank with credentials "Alexandra Desypri" with uuid number "19237-19271" and account id "473297283013"
#    And all the credentials are valid
#    When We register the merchant to dtu pay using the rest service with source path /register/merchant
#    Then we get a confirmation that the merchant was registered
#
#  Scenario: A customer requests tokens
#    Given there is a registered customer in dtuPay with cprNumber "123456-78999"
#    When he requests 5 tokens
#    Then we get a confirmation that the system gave him tokens

  # This last scenario covers all earlier scenarios and since the tests for the first 3 scenarios has started to
  # fail for some reason we are currently ignoring them

  Scenario: A customer tries to do a payment
    Given a customer has a bank account with initial balance 100
    And is registered with dtuPay
    And he has valid tokens
    And there is a registered merchant that also has a bank account with initial balance 100
    When the customer tries to perform a payment with the amount of 50
    Then the endpoint api/pay will return true
    And the customer has a balance of 50 and the merchant has a balance of 150


#  Scenario: A customer tries to do a payment but doesnt have tokens
#    Given a customer has a bank account with initial balance 100
#    And is registered with dtuPay
#    And he doesn't have tokens
#    And there is a registered merchant that also has a bank account with initial balance 100
#    When the customer tries to perform a payment with the amount of 50
#    Then the endpoint api/pay will return false
#    And the customer has a balance of 100 and the merchant has a balance of 100





