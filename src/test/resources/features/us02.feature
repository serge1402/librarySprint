Feature: As a user, I want to search for a specific user by their name or email address using
  the get_user_by_id/{id} endpoint so that I can quickly find the information I need.


  Scenario: Retrieve single user
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Path param is "1"
    When I send GET request to "/get_user_by_id/{id}" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And "id" field should be same with path param
    And "full_name" field should not be null
    And "email" field should not be null
    And "password" field should not be null