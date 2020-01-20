Feature: Test the token requests

  Scenario: A customer without tokens asks for tokens
    Given a customer registered in dtuPay
    And doesnt have any valid tokens
    When the customer asks for 5 tokens
    Then he will recieve 5 tokens

  Scenario: A customer without tokens asks for too many tokens
    Given a customer registered in dtuPay
    And doesnt have any valid tokens
    When the customer asks for 6 tokens
    Then he will not recieve any tokens

  Scenario: A customer asks for tokens, but already has tokens
    Given a customer registered in dtuPay
    And already have 3 tokens
    When the customer asks for 5 tokens
    Then he will not recieve any tokens
    And he will have 3 tokens