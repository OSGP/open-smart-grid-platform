@SmartMetering @Platform
Feature: SmartMetering - Receive Alarm Notifications
  As a grid operator
  I want to be able to receive alarm notifications from a device
  So I get notified about certain events soon after they occur

  Background:
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TEST102600000001  |
      | DeviceType           | SMART_METER_E     |

  Scenario: Handle a received alarm notification from a known device
    When an alarm notification is received from a known device
      | DeviceIdentification | TEST1024000000001 |
    Then 2 alarm should be pushed to the osgp_logging database device_log_item table
      | DeviceIdentification | TEST1024000000001 |
    And a push notification alarm should be received
    
  Scenario: Handle a received forwarded Mx382 alarm notification from a known device
    When a forwarded mx382 alarm notification is received from a known device
      | DeviceIdentification | TEST102600000001 |
    Then 1 alarm should be pushed to the osgp_logging database device_log_item table
      | DeviceIdentification | TEST102600000001 |
    And a push notification alarm should be received

  @NightlyBuildOnly
  Scenario: Handle a received alarm notification from an unknown device
    When an alarm notification is received from an unknown device
      | DeviceIdentification | UNKNOWN0000000001 |
    Then 2 alarm should be pushed to the osgp_logging database device_log_item table
      | DeviceIdentification | UNKNOWN0000000001 |
    And a push notification alarm should be received
