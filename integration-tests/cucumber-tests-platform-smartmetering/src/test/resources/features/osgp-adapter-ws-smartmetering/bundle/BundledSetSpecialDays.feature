@SmartMetering @Platform
Feature: SmartMetering Bundle - SetSpecialDays
  As a grid operator 
  I want to be able to set special days on a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set special days on a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set special days action with parameters
      | SpecialDayCount  |          1 |
      | SpecialDayId_1   |          3 |
      | SpecialDayDate_1 | FFFFFFFFFF |
    When the bundle request is received
    Then the bundle response should contain a set special days response with values
      | Result | OK |
    And the response data record should not be deleted
