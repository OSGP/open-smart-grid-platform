@SmartMetering @Platform
Feature: SmartMetering Bundle - GetActualPowerQuality
  As a grid operator
  I want to be able to retrieve actual power quality data from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification      | TEST1024000000001 |
      | DeviceType                | SMART_METER_E     |
      | Protocol                  | DSMR              |
      | ProtocolVersion           | 4.2.2             |
      | Port                      | 1024              |
      | FirmwareModuleVersionComm | V 1.1             |
      | FirmwareModuleVersionMa   | V 1.2             |
      | FirmwareModuleVersionFunc | V 1.3             |

  Scenario: Retrieve actual power quality data as part of a bundled public request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001   |
    And the bundle request contains a actual power quality request with parameters
      | ProfileType          |              PUBLIC |
    When the bundle request is received
    Then the bundle response should contain a actual power quality response with values
      | DeviceIdentification           | TEST1024000000001 |
      | NumberOfCaptureObjects         |                15 |
      | NumberOfActualValues           |                15 |
      | CaptureObject_ClassId_1        |                 8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255     |
      | CaptureObject_AttributeIndex_1 |                 2 |
      | CaptureObject_DataIndex_1      |                 0 |
      | CaptureObject_ClassId_2        |                 3 |
      | CaptureObject_LogicalName_2    | 1.0.32.7.0.255    |
      | CaptureObject_AttributeIndex_2 |                 2 |
      | CaptureObject_DataIndex_2      |                 0 |
      | CaptureObject_Unit_2           | V                 |

  Scenario: Retrieve actual power quality data as part of a bundled private request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001   |
    And the bundle request contains a actual power quality request with parameters
      | ProfileType          |             PRIVATE |
    When the bundle request is received
    Then the bundle response should contain a actual power quality response with values
      | DeviceIdentification           | TEST1024000000001 |
      | NumberOfCaptureObjects         |                28 |
      | NumberOfActualValues           |                28 |
      | CaptureObject_ClassId_1        |                 8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255     |
      | CaptureObject_AttributeIndex_1 |                 2 |
      | CaptureObject_DataIndex_1      |                 0 |
      | CaptureObject_ClassId_2        |                 3 |
      | CaptureObject_LogicalName_2    | 1.0.31.7.0.255    |
      | CaptureObject_AttributeIndex_2 |                 2 |
      | CaptureObject_DataIndex_2      |                 0 |
      | CaptureObject_Unit_2           | AMP               |
