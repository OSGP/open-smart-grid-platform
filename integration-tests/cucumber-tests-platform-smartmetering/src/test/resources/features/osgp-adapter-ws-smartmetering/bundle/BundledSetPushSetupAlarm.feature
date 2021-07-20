@SmartMetering @Platform
Feature: SmartMetering Bundle - SetPushSetupAlarm
  As a grid operator 
  I want to be able to set push setup alarm on a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set push setup alarm on a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set push setup alarm action with parameters
      | Host | localhost |
      | Port |      9598 |
    When the bundle request is received
    Then the bundle response should contain a set push setup alarm response with values
      | Result | OK |
