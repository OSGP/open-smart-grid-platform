@SmartMetering @Platform @SmartMeteringManagement
Feature: SmartMetering Management - Get debug information
  As a grid operator
  I want to have a webmethod to get the debug information of a single meter
  So I can see extra information of a meter under investigation
  @NightlyBuildOnly
  Scenario: Get debug information from a single meter
    Given a dlms device
      | DeviceIdentification        | TEST1024000000001 |
      | DeviceType                  | SMART_METER_E     |
    And there is debug information logged for the device
    When the get debug information request is received
      | DeviceIdentification        | TEST1024000000001 |
    Then the device debug information should be in the response message
    And the response data record should not be deleted
