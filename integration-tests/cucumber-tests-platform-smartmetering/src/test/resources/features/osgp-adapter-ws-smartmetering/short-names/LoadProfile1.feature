# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

# Needs a DlmsDevice simulator with e650 profile on port 1026

# This test is broken, needs fixing later when support for L+G E650 devices is needed:
@SmartMetering @Platform @SN @Skip
Feature: SmartMetering short names - Load profile 1

  Scenario: Get load profile1 capture objects from L+G E650
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
      | ObisCodeD            |                 1 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 3 |
    Then a get specific attribute value response should be returned
      | DeviceIdentification | TEST1024000000005 |
      | Result               | OK                |
      | ResponsePart         | 0-0:96.240.12.255 |

  Scenario: Get load profile1 buffer from L+G E650
    Given a dlms device
      | DeviceIdentification | TEST1024000000005 |
      | DeviceType           | SMART_METER_E     |
      | UseSn                | true              |
      | UseHdlc              | true              |
      | Port                 |              1026 |
      | Hls5active           | false             |
      | Lls1active           | true              |
    When the get power quality profile request data is received
      | DeviceIdentification | TEST1024000000005 |
      | ClassId              |                 7 |
      | ObisCodeA            |                 1 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                99 |
      | ObisCodeD            |                 1 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
      | BeginDate            | 2015-09-06T23:59  |
      | EndDate              | 2015-09-08T00:01  |
    Then the power quality profile response data should be returned
      | DeviceIdentification           | TEST1024000000005 |
      | Result                         | OK                |
      | NumberOfProfileEntries         |               289 |
      | NumberOfCaptureObjects         |                 9 |
      | CaptureObject_ClassId_1        |                 8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255     |
      | CaptureObject_AttributeIndex_1 |                 2 |
      | CaptureObject_DataIndex_1      |                 0 |
      | CaptureObject_Unit_1           | UNDEFINED         |
      | CaptureObject_ClassId_2        |                 3 |
      | CaptureObject_LogicalName_2    | 0.0.96.240.12.255 |
      | CaptureObject_AttributeIndex_2 |                18 |
      | CaptureObject_DataIndex_2      |                 0 |
      | CaptureObject_Unit_2           | UNDEFINED         |
      | CaptureObject_ClassId_3        |                 5 |
      | CaptureObject_LogicalName_3    | 1.1.1.4.0.255     |
      | CaptureObject_AttributeIndex_3 |                 3 |
      | CaptureObject_DataIndex_3      |                 0 |
      | CaptureObject_Unit_3           | W                 |
      | CaptureObject_ClassId_4        |                 5 |
      | CaptureObject_LogicalName_4    | 1.1.2.4.0.255     |
      | CaptureObject_AttributeIndex_4 |                 3 |
      | CaptureObject_DataIndex_4      |                 0 |
      | CaptureObject_Unit_4           | W                 |
      | CaptureObject_ClassId_5        |                 5 |
      | CaptureObject_LogicalName_5    | 1.1.3.4.0.255     |
      | CaptureObject_AttributeIndex_5 |                 3 |
      | CaptureObject_DataIndex_5      |                 0 |
      | CaptureObject_Unit_5           | VAR               |
      | CaptureObject_ClassId_6        |                 5 |
      | CaptureObject_LogicalName_6    | 1.1.4.4.0.255     |
      | CaptureObject_AttributeIndex_6 |                 3 |
      | CaptureObject_DataIndex_6      |                 0 |
      | CaptureObject_Unit_6           | VAR               |
      | CaptureObject_ClassId_7        |                 3 |
      | CaptureObject_LogicalName_7    | 1.1.33.7.0.255    |
      | CaptureObject_AttributeIndex_7 |                11 |
      | CaptureObject_DataIndex_7      |                 0 |
      | CaptureObject_Unit_7           | COUNT             |
      | CaptureObject_ClassId_8        |                 3 |
      | CaptureObject_LogicalName_8    | 1.1.53.7.0.255    |
      | CaptureObject_AttributeIndex_8 |                11 |
      | CaptureObject_DataIndex_8      |                 0 |
      | CaptureObject_Unit_8           | COUNT             |
      | CaptureObject_ClassId_9        |                 3 |
      | CaptureObject_LogicalName_9    | 1.1.73.7.0.255    |
      | CaptureObject_AttributeIndex_9 |                11 |
      | CaptureObject_DataIndex_9      |                 0 |
      | CaptureObject_Unit_9           | COUNT             |
