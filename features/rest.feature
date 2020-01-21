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

  Scenario: A customer gets a refund
    Given a customer has a bank account with initial balance 100
    And is registered with dtuPay
    And he has valid tokens
    And there is a registered merchant that also has a bank account with initial balance 100
    When the customer is granted a refund of 50
    Then the api/dtupay/refund will return status code ok
    And the customer has a balance of 150 and the merchant has a balance of 50

  Scenario: A customer asks for a transcript
    Given a customer has a bank account with initial balance 1000
    And is registered with dtuPay
    And he has valid tokens
    And there is a registered merchant that also has a bank account with initial balance 200
    When the customer performs 4 purchases with amounts between 50 and 100
    And when he asks for reports between yesterday and tomorrow
    Then he gets 10 payment objects describing the purchases



