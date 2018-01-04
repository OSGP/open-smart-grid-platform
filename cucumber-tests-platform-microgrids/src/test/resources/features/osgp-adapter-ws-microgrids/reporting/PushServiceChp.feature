@Microgrids @Platform @Iec61850MockServerSchoteroog
Feature: Microgrids Receive reports for Chp
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario: Receive a Chp measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
      | Port                 |                62104 |
      | EnableAllReports     | true                 |
    And OSGP is connected to the Schoteroog RTU
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
    When the Schoteroog RTU pushes a report
      | LogicalDevice | CHP1         |
      | ReportType    | Measurements |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-SCHOTEROOGREPORT     |
      | Result                 | OK                       |
      | ReportId               | CHP1_Measurements        |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | CHP                      |
      | NumberOfMeasurements_1 |                        8 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | TotW                     |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | MaxWPhs                  |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | MinWPhs                  |
      | MeasurementId_1_4      |                        1 |
      | MeasurementNode_1_4    | TotWh                    |
      | MeasurementId_1_5      |                        1 |
      | MeasurementNode_1_5    | TmpSv                    |
      | MeasurementId_1_6      |                        2 |
      | MeasurementNode_1_6    | TmpSv                    |
      | MeasurementId_1_7      |                        1 |
      | MeasurementNode_1_7    | FlwRte                   |
      | MeasurementId_1_8      |                        2 |
      | MeasurementNode_1_8    | FlwRte                   |

  Scenario: Receive a Chp status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
      | Port                 |                62104 |
      | EnableAllReports     | true                 |
    And OSGP is connected to the Schoteroog RTU
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
    When the Schoteroog RTU pushes a report
      | LogicalDevice | CHP1   |
      | ReportType    | Status |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-SCHOTEROOGREPORT     |
      | Result                 | OK                       |
      | ReportId               | CHP1_Status              |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | CHP                      |
      | NumberOfMeasurements_1 |                       16 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | Beh                      |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | Health                   |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | GnOpSt                   |
      | MeasurementId_1_4      |                        1 |
      | MeasurementNode_1_4    | OpTmsRs                  |
      | MeasurementId_1_5      |                        1 |
      | MeasurementNode_1_5    | Beh                      |
      | MeasurementId_1_6      |                        1 |
      | MeasurementNode_1_6    | Beh                      |
      | MeasurementId_1_7      |                        1 |
      | MeasurementNode_1_7    | IntIn1                   |
      | MeasurementId_1_8      |                        1 |
      | MeasurementNode_1_8    | IntIn2                   |
      | MeasurementId_1_9      |                        1 |
      | MeasurementNode_1_9    | Alm1                     |
      | MeasurementId_1_10     |                        1 |
      | MeasurementNode_1_10   | Alm2                     |
      | MeasurementId_1_11     |                        1 |
      | MeasurementNode_1_11   | Alm3                     |
      | MeasurementId_1_12     |                        1 |
      | MeasurementNode_1_12   | Alm4                     |
      | MeasurementId_1_13     |                        1 |
      | MeasurementNode_1_13   | Wrn1                     |
      | MeasurementId_1_14     |                        1 |
      | MeasurementNode_1_14   | Wrn2                     |
      | MeasurementId_1_15     |                        1 |
      | MeasurementNode_1_15   | Wrn3                     |
      | MeasurementId_1_16     |                        1 |
      | MeasurementNode_1_16   | Wrn4                     |
