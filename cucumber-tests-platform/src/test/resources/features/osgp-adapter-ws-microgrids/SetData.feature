@Iec61850MockServerMarkerWadden
Feature: MicroGrids Set Data Service
  As scrum team I want to have cucumber tests for Set Data Services
  so that I am able to guarantee the quality of Microgrid Platform.

  Scenario Outline: SetData Service
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10003                              |
#      | IcdFilename          | MarkerWadden_0_1_1_reporting_hack.icd |
#      | Port                 |                                 63102 |
#    And an rtu simulator started with
#      | ServerName  | WAGO61850Server                       |
#      | IcdFilename | MarkerWadden_0_1_1_reporting_hack.icd |
#      | Port        |                                 63102 |
    When a set data request is received    
      | DeviceIdentification       | RTU10003            |
      | NumberOfSystems            |                   1 |
      | SystemId_1                 |                   1 |
      | SystemType_1               | <SystemType_1>      |
      | NumberOfSetPoints_1        |                   3 |
      | SetPointId_1_1             |                   1 |
      | SetPointNode_1_1           | SchdId              |
      | SetPointValue_1_1          |                   1 |
      | SetPointId_1_2             |                   1 |
      | SetPointNode_1_2           | SchdTyp             |
      | SetPointValue_1_2          |                   1 |
      | SetPointId_1_3             |                   1 |
      | SetPointNode_1_3           | SchdCat             |
      | SetPointValue_1_3          |                   1 |
      | NumberOfProfiles_1         |                   1 |
      | ProfileId_1_1              |                   1 |
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
      | DeviceIdentification | RTU10001 |
      | Result               | OK       |
    And the rtu simulator should contain
      | <Logical_node> | DSCH1.SchdId.setVal    |                   1 |
      | <Logical_node> | DSCH1.SchdTyp.setVal   |                   1 |
      | <Logical_node> | DSCH1.SchCat.setVal    |                   1 |
      | <Logical_node> | DSCH1.SchdAbsTm.val.0  |                  74 |
      | <Logical_node> | DSCH1.SchdAbsTm.time.0 | 2016-11-21T01:45:00 |
      | <Logical_node> | DSCH1.SchdAbsTm.val.1  |                  45 |
      | <Logical_node> | DSCH1.SchdAbsTm.time.1 | 2016-11-21T12:45:00 |
      | <Logical_node> | DSCH1.SchdAbsTm.val.2  |                  15 |
      | <Logical_node> | DSCH1.SchdAbsTm.time.2 | 2016-11-22T00:00:00 |
      | <Logical_node> | DSCH1.SchdAbsTm.val.3  |                  21 |
      | <Logical_node> | DSCH1.SchdAbsTm.time.3 | 2016-11-22T01:30:00 |

    Examples:
      | SystemType_1  | Logical_node | 
      | RTU           | RTU1         |
      | BATTERY       | BATTERY1     |
      | HEAT_PUMP     | HEAT_PUMP1   |
      | BOILER        | BOILER1      | 
