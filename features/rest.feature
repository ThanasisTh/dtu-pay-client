Feature: Test the rest service

  Scenario: A customer tries to do a payment
    Given a customer has a bank account with initial balance 100
    And is registered with dtuPay
    And he has valid tokens
    And there is a registered merchant that also has a bank account with initial balance 100
    When the customer tries to perform a payment with the amount of 50
    Then the endpoint api/pay will return true
    And the customer has a balance of 50 and the merchant has a balance of 150


  Scenario: A customer tries to do a payment but doesnt have tokens
    Given a customer has a bank account with initial balance 100
    And is registered with dtuPay
    And he doesn't have tokens
    And there is a registered merchant that also has a bank account with initial balance 100
    When the customer tries to perform a payment with the amount of 50
    Then the endpoint api/pay will return false
    And the customer has a balance of 100 and the merchant has a balance of 100

  Scenario: A customer tries to do a payment but doesnt have enough money
    Given a customer has a bank account with initial balance 40
    And is registered with dtuPay
    And he has valid tokens
    And there is a registered merchant that also has a bank account with initial balance 100
    When the customer tries to perform a payment with the amount of 50
    Then the endpoint api/pay will return false
    And the customer has a balance of 40 and the merchant has a balance of 100







