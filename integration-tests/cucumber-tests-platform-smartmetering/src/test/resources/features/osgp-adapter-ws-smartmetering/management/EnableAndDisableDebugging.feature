@SmartMetering @Platform @SmartMeteringManagement @NightlyBuildOnly
Feature: SmartMetering Management - Enable and disable debugging
  As a grid operator
  I want to turn debugging of a device on or off
  So I can see extra information of a meter under investigation

  Scenario: Enable debug information from a single meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the enable Debug request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the enable debug response should be "OK"
    And the device debug information should be enabled

  Scenario: Disable debug information from a single meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the disable Debug request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the disable debug response should be "OK"
    And the device debug information should be disabled
