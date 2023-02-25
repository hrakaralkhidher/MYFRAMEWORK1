Feature: Get Books Endpoint

  Scenario: Given baseURI when we make a get call to /books endpoint verify response and status code
    Given baseURI
    When we want to make a get call to "/books" endpoint
    Then Verify that response has all books
    And Verify sttus code is 200