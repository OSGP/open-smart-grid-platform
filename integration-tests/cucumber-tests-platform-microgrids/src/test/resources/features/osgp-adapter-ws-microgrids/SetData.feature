# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Microgrids @Platform @Iec61850MockServerMarkerWadden
Feature: Microgrids Set Data Service
  As scrum team I want to have cucumber tests for Set Data Services
  so that I am able to guarantee the quality of Microgrid Platform.

  Scenario Outline: SetData Service
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKER-WADDEN |
      | Port                 |             62103 |
    When a set data request is received
      | DeviceIdentification       | RTU-MARKER-WADDEN   |
      | NumberOfSystems            |                   1 |
      | SystemId_1                 |                   1 |
      | SystemType_1               | <SystemType_1>      |
      | NumberOfSetPoints_1        |                   3 |
      | SetPointId_1_1             | <NodeId>            |
      | SetPointNode_1_1           | SchdId              |
      | SetPointValue_1_1          |                   1 |
      | SetPointId_1_2             | <NodeId>            |
      | SetPointNode_1_2           | SchdTyp             |
      | SetPointValue_1_2          |                   1 |
      | SetPointId_1_3             | <NodeId>            |
      | SetPointNode_1_3           | SchdCat             |
      | SetPointValue_1_3          |                   1 |
      | NumberOfProfiles_1         |                   1 |
      | ProfileId_1_1              | <NodeId>            |
      | ProfileNode_1_1            | SchdAbsTm           |
      | NumberOfProfileEntries_1_1 |                   4 |
      | ProfileEntryId_1_1_1       |                   1 |
      | ProfileEntryTime_1_1_1     | 2016-11-21T01:45:00 |
      | ProfileEntryValue_1_1_1    |                  74 |
      | ProfileEntryId_1_1_2       |                   2 |
      | ProfileEntryTime_1_1_2     | 2016-11-21T12:45:00 |
      | ProfileEntryValue_1_1_2    |                  45 |
      | ProfileEntryId_1_1_3       |                   3 |
      | ProfileEntryTime_1_1_3     | 2016-11-22T00:00:00 |
      | ProfileEntryValue_1_1_3    |                  15 |
      | ProfileEntryId_1_1_4       |                   4 |
      | ProfileEntryTime_1_1_4     | 2016-11-22T01:30:00 |
      | ProfileEntryValue_1_1_4    |                  21 |
    Then the set data response should be returned
      | DeviceIdentification | RTU-MARKER-WADDEN |
      | Result               | OK                |
    And the Marker Wadden RTU should contain
      | <LogicalDevice> | DSCH<NodeId>.SchdId.setVal    |                   1 |
      | <LogicalDevice> | DSCH<NodeId>.SchdTyp.setVal   |                   1 |
      | <LogicalDevice> | DSCH<NodeId>.SchCat.setVal    |                   1 |
      | <LogicalDevice> | DSCH<NodeId>.SchdAbsTm.val.0  |                  74 |
      | <LogicalDevice> | DSCH<NodeId>.SchdAbsTm.time.0 | 2016-11-21T01:45:00 |
      | <LogicalDevice> | DSCH<NodeId>.SchdAbsTm.val.1  |                  45 |
      | <LogicalDevice> | DSCH<NodeId>.SchdAbsTm.time.1 | 2016-11-21T12:45:00 |
      | <LogicalDevice> | DSCH<NodeId>.SchdAbsTm.val.2  |                  15 |
      | <LogicalDevice> | DSCH<NodeId>.SchdAbsTm.time.2 | 2016-11-22T00:00:00 |
      | <LogicalDevice> | DSCH<NodeId>.SchdAbsTm.val.3  |                  21 |
      | <LogicalDevice> | DSCH<NodeId>.SchdAbsTm.time.3 | 2016-11-22T01:30:00 |

    Examples: 
      | SystemType_1 | LogicalDevice | NodeId |
      | RTU          | RTU1          |      1 |
      | BATTERY      | BATTERY1      |      1 |
      | HEAT_PUMP    | HEAT_PUMP1    |      1 |
      | BOILER       | BOILER1       |      1 |
      | RTU          | RTU1          |      2 |
      | BATTERY      | BATTERY1      |      2 |
      | HEAT_PUMP    | HEAT_PUMP1    |      2 |
      | BOILER       | BOILER1       |      2 |
      | RTU          | RTU1          |      3 |
      | BATTERY      | BATTERY1      |      3 |
      | HEAT_PUMP    | HEAT_PUMP1    |      3 |
      | BOILER       | BOILER1       |      3 |
      | RTU          | RTU1          |      4 |
      | BATTERY      | BATTERY1      |      4 |
      | HEAT_PUMP    | HEAT_PUMP1    |      4 |
      | BOILER       | BOILER1       |      4 |
      | ENGINE       | ENGINE1       |      1 |
      | ENGINE       | ENGINE1       |      2 |
      | ENGINE       | ENGINE1       |      3 |
      | ENGINE       | ENGINE1       |      4 |
