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

  Scenario: Retrieve actual power quality data as part of a bundled request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001   |
    And the bundle request contains a actual power quality request with parameters
      | ProfileType          |              PUBLIC |
    When the bundle request is received
    Then the bundle response should contain a actual power quality response with values
      | DeviceIdentification           | TEST1024000000001 |
      | NumberOfCaptureObjects         |                 4 |
      | CaptureObject_ClassId_1        |                 8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255     |
      | CaptureObject_AttributeIndex_1 |                 2 |
      | CaptureObject_DataIndex_1      |                 0 |
      | CaptureObject_Unit_1           | UNDEFINED         |
      | CaptureObject_ClassId_2        |                 1 |
      | CaptureObject_LogicalName_2    | 0.0.96.10.2.255   |
      | CaptureObject_AttributeIndex_2 |                 2 |
      | CaptureObject_DataIndex_2      |                 0 |
      | CaptureObject_Unit_2           | UNDEFINED         |
      | CaptureObject_ClassId_3        |                 3 |
      | CaptureObject_LogicalName_3    | 1.0.1.8.0.255     |
      | CaptureObject_AttributeIndex_3 |                 2 |
      | CaptureObject_DataIndex_3      |                 0 |
      | CaptureObject_Unit_3           | KWH               |
      | CaptureObject_ClassId_4        |                 3 |
      | CaptureObject_LogicalName_4    | 1.0.2.8.0.255     |
      | CaptureObject_AttributeIndex_4 |                 2 |
      | CaptureObject_DataIndex_4      |                 0 |
      | CaptureObject_Unit_4           | KWH               |
      | NumberOfProfileEntries         |               960 |
