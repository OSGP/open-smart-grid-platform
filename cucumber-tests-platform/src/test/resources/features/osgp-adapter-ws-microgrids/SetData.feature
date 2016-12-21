Feature: Set Data Service
  As scrum team I want to have cucumber tests for Set Data Services so that I am able to guarantee the quality of Microgrid Platform.

  @Iec61850MockServer @test
  Scenario: SetData Service
    Given an rtu device
      | DeviceIdentification | RTU10001 |
    When a set data request is received
      | DeviceIdentification | RTU10001            |
      | NumberOfSystems      |                   1 |
      | SystemId_1           |                   1 |
      | SystemType_1         | RTU                 |
      # | SetPointId_1         |                   1 |
      | SetPointNode_1       | SchdId              |
      | SetPointValue_1      |                   1 |
      # | SetPointId_1         |                   1 |
      | SetPointNode_1       | SchdTyp             |
      | SetPointValue_1      |                   1 |
      #| SetPointId_1         |                   1 |
      | SetPointNode_1       | SchdCat             |
      | SetPointValue_1      |                   1 |
      | ProfileId_1          |                   1 |
      | ProfileNode_1        | SchdAbsTm           |
      | ProfileEntryTime_1   | 2016-11-21T01:45:00 |
      | ProfileEntryValue_1  |                  74 |
      | ProfileEntryTime_2   | 2016-11-21T12:45:00 |
      | ProfileEntryValue_2  |                  45 |
      | ProfileEntryTime_3   | 2016-11-22T00:00:00 |
      | ProfileEntryValue_3  |                  15 |
      | ProfileEntryTime_4   | 2016-11-22T01:30:00 |
      | ProfileEntryValue_4  |                  21 |
    Then the set data response should be returned
      | DeviceIdentification | RTU10001 |
      | Result               | OK       |
    And the rtu simulator should contain
      | RTU1 | DSCH1.SchdId.setVal     |                   1 |
      | RTU1 | DSCH1.SchdTyp.setVal    |                   1 |
      | RTU1 | DSCH1.SchdCat.setVal    |                   1 |
      | RTU1 | DSCH1.SchdAbsTm.val.0   |                  74 |
      | RTU1 | DSCH1.SchdAbsTm.time.0  | 2016-11-21T01:45:00 |
      | RTU1 | DSCH1.SchdAbsTm.val.1   |                  45 |
      | RTU1 | DSCH1.SchdAbsTm.time.1  | 2016-11-21T12:45:00 |
      | RTU1 | DSCH1.SchdAbsTm.val.2   |                  15 |
      | RTU1 | DSCH1.SchdAbsTm.time.2  | 2016-11-22T00:00:00 |
      | RTU1 | DSCH1.SchdAbsTm.val.3   |                  21 |
      | RTU1 | DSCH1.SchdAbsTm.time.03 | 2016-11-22T01:30:00 |
