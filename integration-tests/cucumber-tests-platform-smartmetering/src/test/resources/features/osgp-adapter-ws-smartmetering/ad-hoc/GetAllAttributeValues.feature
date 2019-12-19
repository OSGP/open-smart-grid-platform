@SmartMetering @Platform @SmartMeteringAdHoc @NightlyBuildOnly
Feature: SmartMetering AdHoc - Get All Attribute Values
  As a grid operator
  I want to be able to get all attribute values from a device
  So I can examine what is on the device in detail in case of issues

  Scenario: Get All Attribute Values Request
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the get all attribute values request is received
      | DeviceIdentification | TEST1024000000001 |
    Then a get all attribute values response should be returned
      | Result | OK |
