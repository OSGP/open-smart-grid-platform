@SmartMetering @Platform @SmartMeteringAdHoc @NightlyBuildOnly
Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to synchronize time on a device
  So time related data on the device will have reliable timestamps

  Scenario: Retrieve SynchronizeTime result from a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When receiving a get synchronize time request
      | DeviceIdentification | TEST1024000000001 |
    Then the date and time is synchronized on the device
      | DeviceIdentification | TEST1024000000001 |
    And the response data record should not be deleted
