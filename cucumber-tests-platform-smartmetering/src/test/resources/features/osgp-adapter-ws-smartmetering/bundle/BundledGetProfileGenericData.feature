@SmartMetering @Platform
Feature: SmartMetering Bundle - GetProfileGenericData
  As a grid operator
  I want to be able to retrieve profile generic data from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Retrieve profile generic data as part of a bundled request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001   |
    And the bundle request contains a get profile generic data action with parameters
      | ObisCodeA            |                   1 |
      | ObisCodeB            |                   0 |
      | ObisCodeC            |                  99 |
      | ObisCodeD            |                   1 |
      | ObisCodeE            |                   0 |
      | ObisCodeF            |                 255 |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a profile generic data response with values
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
