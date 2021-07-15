@SmartMetering @Platform 
Feature: SmartMetering Bundle - SetAdministrativeStatus
  As a grid operator 
  I want to be able to set administrative status on a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set administrative status on a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set administrative status action with parameters
      | AdministrativeStatusType  |          ON |
    When the bundle request is received
    Then the bundle response should contain a set administrative status response with values
      | Result               | OK                |
    And the response data record should not be deleted
