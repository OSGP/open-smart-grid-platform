# Needs a DlmsDevice simulator with e650 profile on port 1026
@SmartMetering @Platform @SN
Feature: SmartMetering short names - Event Log

  Scenario: Get event log capture objects from L+G E650
    Given a dlms device
      | DeviceIdentification | TEST1024000000005 |
      | DeviceType           | SMART_METER_E     |
      | UseSn                | true              |
      | UseHdlc              | true              |
      | Port                 |              1026 |
      | Hls5active           | false             |
      | Lls1active           | true              |
    When the get specific attribute value request is received
      | DeviceIdentification | TEST1024000000005 |
      | ClassId              |                 7 |
      | ObisCodeA            |                 1 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                99 |
      | ObisCodeD            |                98 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 3 |
    Then a get specific attribute value response should be returned
      | DeviceIdentification | TEST1024000000005 |
      | Result               | OK                |
      | ResponsePart         | 0-0:1.0.0.255     |

  Scenario: Get event log buffer from L+G E650
    Given a dlms device
      | DeviceIdentification | TEST1024000000005 |
      | DeviceType           | SMART_METER_E     |
      | UseSn                | true              |
      | UseHdlc              | true              |
      | Port                 |              1026 |
      | Hls5active           | false             |
      | Lls1active           | true              |
    When the get profile generic data request is received
      | DeviceIdentification | TEST1024000000005 |
      | ClassId              |                 7 |
      | ObisCodeA            |                 1 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                99 |
      | ObisCodeD            |                98 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
      | BeginDate            | 2015-09-06T23:59  |
      | EndDate              | 2015-09-08T00:01  |
    Then the profile generic data result should be returned
      | DeviceIdentification           | TEST1024000000005 |
      | Result                         | OK                |
      | NumberOfCaptureObjects         |                 7 |
      | CaptureObject_ClassId_1        |                 8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255     |
      | CaptureObject_AttributeIndex_1 |                 2 |
      | CaptureObject_DataIndex_1      |                 0 |
      | CaptureObject_Unit_1           | UNDEFINED         |
      | CaptureObject_ClassId_2        |                 3 |
      | CaptureObject_LogicalName_2    | 0.0.96.240.12.255 |
      | CaptureObject_AttributeIndex_2 |                19 |
      | CaptureObject_DataIndex_2      |                 0 |
      | CaptureObject_Unit_2           | UNDEFINED         |
      | CaptureObject_ClassId_3        |                 3 |
      | CaptureObject_LogicalName_3    | 0.0.96.240.12.255 |
      | CaptureObject_AttributeIndex_3 |                 2 |
      | CaptureObject_DataIndex_3      |                 0 |
      | CaptureObject_Unit_3           | UNDEFINED         |
      | CaptureObject_ClassId_4        |                 3 |
      | CaptureObject_LogicalName_4    | 0.0.96.240.12.255 |
      | CaptureObject_AttributeIndex_4 |                12 |
      | CaptureObject_DataIndex_4      |                 0 |
      | CaptureObject_Unit_4           | UNDEFINED         |
      | CaptureObject_ClassId_5        |                 3 |
      | CaptureObject_LogicalName_5    | 0.0.97.97.0.255   |
      | CaptureObject_AttributeIndex_5 |                 2 |
      | CaptureObject_DataIndex_5      |                 0 |
      | CaptureObject_Unit_5           | M3                |
      | CaptureObject_ClassId_6        |                 3 |
      | CaptureObject_LogicalName_6    | 1.1.1.8.0.255     |
      | CaptureObject_AttributeIndex_6 |                 2 |
      | CaptureObject_DataIndex_6      |                 0 |
      | CaptureObject_Unit_6           | W                 |
      | CaptureObject_ClassId_7        |                 3 |
      | CaptureObject_LogicalName_7    | 1.1.2.8.0.255     |
      | CaptureObject_AttributeIndex_7 |                 2 |
      | CaptureObject_DataIndex_7      |                 0 |
      | CaptureObject_Unit_7           | W                 |
