Feature: 
  As a Grid Operator
  I want to retrieve the association LN objectlist of a meter
  So I can store that the association LN objectlist
@SLIM-517 @SLIM-505
  Scenario: Store the association LN objectlist from a device in the integration layer database
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the retrieve association LN objectlist request is received
    Then the objectlist should be returned
    And the objeclist should be stored in the integration layer database