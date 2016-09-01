Feature: SmartMetering Management
  As a grid operator
  I want to be able to perform SmartMeteringManagement operations on a device
    
Background:
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "Test Organisation"
    
@SLIM-150
  Scenario: find event information from a meter
    When the find standard events request is received
    Then standard events should be returned

    When the find fraud events request is received
    Then fraud events should be returned

    When the find communication events request is received
    Then communication events should be returned

    When the find mbus events request is received
    Then mbus events should be returned        