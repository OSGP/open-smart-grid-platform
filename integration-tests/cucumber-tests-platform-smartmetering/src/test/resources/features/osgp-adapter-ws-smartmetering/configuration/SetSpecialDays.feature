@blablabla
Feature: SmartMetering Configuration - Set Special Days
  As a grid operator
  I want to be able to set special days on a device
  So correct tarrifs are used for billing

  Scenario: Set special days on a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the set special days request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the special days should be set on the device
      | DeviceIdentification | TEST1024000000001 |
