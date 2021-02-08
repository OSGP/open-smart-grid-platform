@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Get Actual Power Quality
  As a grid operator
  I want to be able to get the actual power quality from a device
  So I can see them when investigating some issue

  Background:
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |

  Scenario: Get the actual power quality public from a device
    When the get actual power quality request is received
      | DeviceIdentification | TEST1024000000001 |
      | ProfileType          |            PUBLIC |
    Then the actual power quality result should be returned
      | NumberOfCaptureObjects |                15 |
      | NumberOfActualValues   |                15 |
      | DeviceIdentification   | TEST1024000000001 |
      | CaptureObject_ClassId_1        |              8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255  |
      | CaptureObject_AttributeIndex_1 |              2 |
      | CaptureObject_DataIndex_1      |              0 |
      | CaptureObject_ClassId_2        |              3 |
      | CaptureObject_LogicalName_2    | 1.0.32.7.0.255 |
      | CaptureObject_AttributeIndex_2 |              2 |
      | CaptureObject_DataIndex_2      |              0 |
      | CaptureObject_Unit_2           | V              |

  Scenario: Get the actual power quality private from a device
    When the get actual power quality request is received
      | DeviceIdentification | TEST1024000000001 |
      | ProfileType          |            PRIVATE |
    Then the actual power quality result should be returned
      | NumberOfCaptureObjects |                28 |
      | NumberOfActualValues   |                28 |
      | DeviceIdentification   | TEST1024000000001 |
      | CaptureObject_ClassId_1        |              8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255  |
      | CaptureObject_AttributeIndex_1 |              2 |
      | CaptureObject_DataIndex_1      |              0 |
      | CaptureObject_ClassId_2        |              3 |
      | CaptureObject_LogicalName_2    | 1.0.31.7.0.255 |
      | CaptureObject_AttributeIndex_2 |              2 |
      | CaptureObject_DataIndex_2      |              0 |
      | CaptureObject_Unit_2           | AMP            |

  Scenario: Do not refuse an operation with an inactive device
    Given a dlms device
      | DeviceIdentification  | E9998000014123414 |
      | DeviceType            | SMART_METER_E     |
      | DeviceLifecycleStatus | NEW_IN_INVENTORY  |
    When the get actual power quality request is received
      | DeviceIdentification   | E9998000014123414 |
      | ProfileType            |           PRIVATE |
    Then the actual power quality result should be returned
      | DeviceIdentification   | E9998000014123414 |
      | NumberOfCaptureObjects |                28 |
      | NumberOfActualValues   |                28 |
