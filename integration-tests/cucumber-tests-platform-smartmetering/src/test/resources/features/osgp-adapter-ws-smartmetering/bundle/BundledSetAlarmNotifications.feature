@SmartMetering @Platform
Feature: SmartMetering Bundle - SetAlarmNotifications
  As a grid operator 
  I want to be able to set alarm notifications on a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set alarm notifications on a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set alarm notifications action with parameters
      | AlarmNotificationCount |             2 |
      | AlarmType_1            | POWER_UP      |
      | AlarmTypeEnabled_1     | true          |
      | AlarmType_2            | FRAUD_ATTEMPT |
      | AlarmTypeEnabled_2     | true          |
    When the bundle request is received
    Then the bundle response should contain a set alarm notifications response with values
      | Result | OK |
