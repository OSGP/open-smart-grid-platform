@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering Configuration - Configure Definable Load Profile
  As a grid operator
  I want to be able to change the definable load profile
  So I can define the values to be monitored

  Scenario: Set capture objects clock and Instantaneous voltage for phase 1 in definable load profile
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When a Configure Definable Load Profile request is received
      | DeviceIdentification           | TEST1024000000001 |
      | NumberOfCaptureObjects         |                 1 |
      | CaptureObject_ClassId_1        |                 3 |
      | CaptureObject_LogicalName_1    | 1.0.32.7.0.255    |
      | CaptureObject_AttributeIndex_1 |                 2 |
      | CaptureObject_DataIndex_1      |                 0 |
    Then the Configure Definable Load Profile response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the Definable Load Profile of "TEST1024000000001" contains
      | NumberOfCaptureObjects         |              2 |
      | CaptureObject_ClassId_1        |              8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255  |
      | CaptureObject_AttributeIndex_1 |              2 |
      | CaptureObject_DataIndex_1      |              0 |
      | CaptureObject_ClassId_2        |              3 |
      | CaptureObject_LogicalName_2    | 1.0.32.7.0.255 |
      | CaptureObject_AttributeIndex_2 |              2 |
      | CaptureObject_DataIndex_2      |              0 |
    And the response data record should not be deleted

  Scenario: Set capture period to 1 hour in definable load profile
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When a Configure Definable Load Profile request is received
      | DeviceIdentification | TEST1024000000001 |
      | CapturePeriod        |              3600 |
    Then the Configure Definable Load Profile response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And the Definable Load Profile of "TEST1024000000001" contains
      | CapturePeriod | 3600 |
