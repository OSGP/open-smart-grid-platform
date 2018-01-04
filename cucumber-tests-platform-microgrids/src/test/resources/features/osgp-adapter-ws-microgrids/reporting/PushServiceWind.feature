@Microgrids @Platform @Iec61850MockServerPampus
Feature: Microgrids Receive reports for Wind
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario: Receive a Wind measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | WIND1        |
      | ReportType    | Measurements |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-PAMPUSREPORT         |
      | Result                 | OK                       |
      | ReportId               | WIND1_Measurements       |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | WIND                     |
      | NumberOfMeasurements_1 |                       13 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | TotW                     |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | TotPF                    |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | W.phsA                   |
      | MeasurementId_1_4      |                        1 |
      | MeasurementNode_1_4    | W.phsB                   |
      | MeasurementId_1_5      |                        1 |
      | MeasurementNode_1_5    | W.phsC                   |
      | MeasurementId_1_6      |                        1 |
      | MeasurementNode_1_6    | MaxWPhs                  |
      | MeasurementId_1_7      |                        1 |
      | MeasurementNode_1_7    | MinWPhs                  |
      | MeasurementId_1_8      |                        2 |
      | MeasurementNode_1_8    | W.phsA                   |
      | MeasurementId_1_9      |                        2 |
      | MeasurementNode_1_9    | W.phsB                   |
      | MeasurementId_1_10     |                        2 |
      | MeasurementNode_1_10   | W.phsC                   |
      | MeasurementId_1_11     |                        3 |
      | MeasurementNode_1_11   | W.phsA                   |
      | MeasurementId_1_12     |                        3 |
      | MeasurementNode_1_12   | W.phsB                   |
      | MeasurementId_1_13     |                        3 |
      | MeasurementNode_1_13   | W.phsC                   |

  Scenario: Receive a Wind status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | WIND1  |
      | ReportType    | Status |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-PAMPUSREPORT         |
      | Result                 | OK                       |
      | ReportId               | WIND1_Status             |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | WIND                     |
      | NumberOfMeasurements_1 |                       15 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | Beh                      |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | Health                   |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | OutWSet                  |
      | MeasurementId_1_4      |                        1 |
      | MeasurementNode_1_4    | GnOpSt                   |
      | MeasurementId_1_5      |                        1 |
      | MeasurementNode_1_5    | OpTmsRs                  |
      | MeasurementId_1_6      |                        1 |
      | MeasurementNode_1_6    | IntIn1                   |
      | MeasurementId_1_7      |                        1 |
      | MeasurementNode_1_7    | IntIn2                   |
      | MeasurementId_1_8      |                        1 |
      | MeasurementNode_1_8    | Alm1                     |
      | MeasurementId_1_9      |                        1 |
      | MeasurementNode_1_9    | Alm2                     |
      | MeasurementId_1_10     |                        1 |
      | MeasurementNode_1_10   | Alm3                     |
      | MeasurementId_1_11     |                        1 |
      | MeasurementNode_1_11   | Alm4                     |
      | MeasurementId_1_12     |                        1 |
      | MeasurementNode_1_12   | Wrn1                     |
      | MeasurementId_1_13     |                        1 |
      | MeasurementNode_1_13   | Wrn2                     |
      | MeasurementId_1_14     |                        1 |
      | MeasurementNode_1_14   | Wrn3                     |
      | MeasurementId_1_15     |                        1 |
      | MeasurementNode_1_15   | Wrn4                     |
