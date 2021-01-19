@SmartMetering @Platform @NightlyBuildOnly
Feature: Core operations
  As a grid operator
  I want to log each retry in the audit trail
  So I get better information the number of retries necessary
  Scenario: Try to connect to an unknown ip address
    Given a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
      | Port                 |              9999 |
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1024000000002 |
    Then the audit trail contains multiple retry log records
      | DeviceIdentification | TEST1024000000002 |
