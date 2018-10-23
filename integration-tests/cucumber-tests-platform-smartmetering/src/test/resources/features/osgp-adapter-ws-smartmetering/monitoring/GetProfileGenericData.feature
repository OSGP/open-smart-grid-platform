@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Profile Generic Data
  As a grid operator
  I want to be able to get profile generic data from a device
  So I am able to retrieve profile data regardless if a web service exists that
  offers more specific support or not

  Background:
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |

  Scenario: Get the profile generic data from a device
    When the get profile generic data request is received
      | DeviceIdentification | TEST1024000000001 |
      | ObisCodeA            |                 1 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                99 |
      | ObisCodeD            |                 1 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | BeginDate            | 2015-01-01        |
      | EndDate              | 2017-01-10        |
    Then the profile generic data result should be returned
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

  Scenario: Get the profile generic data from a device for a single selected value
    When the get profile generic data request is received
      | DeviceIdentification           | TEST1024000000001 |
      | ObisCodeA                      |                 1 |
      | ObisCodeB                      |                 0 |
      | ObisCodeC                      |                99 |
      | ObisCodeD                      |                 1 |
      | ObisCodeE                      |                 0 |
      | ObisCodeF                      |               255 |
      | BeginDate                      | 2015-09-06T23:59  |
      | EndDate                        | 2015-09-08T00:01  |
      | NumberOfSelectedValues         |                 1 |
      | SelectedValue_ClassId_1        |                 3 |
      | SelectedValue_LogicalName_1    | 1.0.2.8.0.255     |
      | SelectedValue_AttributeIndex_1 |                 2 |
      | SelectedValue_DataIndex_1      |                 0 |
    Then the profile generic data result should be returned
      | DeviceIdentification           | TEST1024000000001 |
      | NumberOfCaptureObjects         |                 2 |
      | CaptureObject_ClassId_1        |                 8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255     |
      | CaptureObject_AttributeIndex_1 |                 2 |
      | CaptureObject_DataIndex_1      |                 0 |
      | CaptureObject_Unit_1           | UNDEFINED         |
      | CaptureObject_ClassId_2        |                 3 |
      | CaptureObject_LogicalName_2    | 1.0.2.8.0.255     |
      | CaptureObject_AttributeIndex_2 |                 2 |
      | CaptureObject_DataIndex_2      |                 0 |
      | CaptureObject_Unit_2           | KWH               |
      | NumberOfProfileEntries         |                97 |

  Scenario: Get the profile generic data from a device for two selected values
    When the get profile generic data request is received
      | DeviceIdentification           | TEST1024000000001 |
      | ObisCodeA                      |                 1 |
      | ObisCodeB                      |                 0 |
      | ObisCodeC                      |                99 |
      | ObisCodeD                      |                 1 |
      | ObisCodeE                      |                 0 |
      | ObisCodeF                      |               255 |
      | BeginDate                      | 2015-09-06T20:15  |
      | EndDate                        | 2015-09-06T22:00  |
      | NumberOfSelectedValues         |                 2 |
      | SelectedValue_ClassId_1        |                 1 |
      | SelectedValue_LogicalName_1    | 0.0.96.10.2.255   |
      | SelectedValue_AttributeIndex_1 |                 2 |
      | SelectedValue_DataIndex_1      |                 0 |
      | SelectedValue_ClassId_2        |                 3 |
      | SelectedValue_LogicalName_2    | 1.0.1.8.0.255     |
      | SelectedValue_AttributeIndex_2 |                 2 |
      | SelectedValue_DataIndex_2      |                 0 |
    Then the profile generic data result should be returned
      | DeviceIdentification           | TEST1024000000001 |
      | NumberOfCaptureObjects         |                 3 |
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
      | NumberOfProfileEntries         |                 7 |
