@Microgrids @Platform @Iec61850MockServerPampus
Feature: Microgrids Receive reports for PQ
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario: Receive a PQ measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | PQ1          |
      | ReportType    | Measurements |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-PAMPUSREPORT         |
      | Result                 | OK                       |
      | ReportId               | PQ1_Measurements         |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | PQ                       |
      | NumberOfMeasurements_1 |                       21 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | Hz                       |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | PNV.phsA                 |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | PNV.phsB                 |
      | MeasurementId_1_4      |                        1 |
      | MeasurementNode_1_4    | PNV.phsC                 |
      | MeasurementId_1_5      |                        1 |
      | MeasurementNode_1_5    | PF.phsA                  |
      | MeasurementId_1_6      |                        1 |
      | MeasurementNode_1_6    | PF.phsB                  |
      | MeasurementId_1_7      |                        1 |
      | MeasurementNode_1_7    | PF.phsC                  |
      | MeasurementId_1_8      |                        1 |
      | MeasurementNode_1_8    | Z.phsA                   |
      | MeasurementId_1_9      |                        1 |
      | MeasurementNode_1_9    | Z.phsB                   |
      | MeasurementId_1_10     |                        1 |
      | MeasurementNode_1_10   | Z.phsC                   |
      | MeasurementId_1_11     |                        2 |
      | MeasurementNode_1_11   | Hz                       |
      | MeasurementId_1_12     |                        2 |
      | MeasurementNode_1_12   | PNV.phsA                 |
      | MeasurementId_1_13     |                        2 |
      | MeasurementNode_1_13   | PNV.phsB                 |
      | MeasurementId_1_14     |                        2 |
      | MeasurementNode_1_14   | PNV.phsC                 |
      | MeasurementId_1_15     |                        2 |
      | MeasurementNode_1_15   | PF.phsA                  |
      | MeasurementId_1_16     |                        2 |
      | MeasurementNode_1_16   | PF.phsB                  |
      | MeasurementId_1_17     |                        2 |
      | MeasurementNode_1_17   | PF.phsC                  |
      | MeasurementId_1_18     |                        3 |
      | MeasurementNode_1_18   | Hz                       |
      | MeasurementId_1_19     |                        3 |
      | MeasurementNode_1_19   | PNV.phsA                 |
      | MeasurementId_1_20     |                        3 |
      | MeasurementNode_1_20   | PNV.phsB                 |
      | MeasurementId_1_21     |                        3 |
      | MeasurementNode_1_21   | PNV.phsC                 |

  Scenario: Receive a PQ status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | PQ1    |
      | ReportType    | Status |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-PAMPUSREPORT         |
      | Result                 | OK                       |
      | ReportId               | PQ1_Status               |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | PQ                       |
      | NumberOfMeasurements_1 |                       13 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | Beh                      |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | Health                   |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | IntIn1                   |
      | MeasurementId_1_4      |                        1 |
      | MeasurementNode_1_4    | IntIn2                   |
      | MeasurementId_1_5      |                        1 |
      | MeasurementNode_1_5    | Alm1                     |
      | MeasurementId_1_6      |                        1 |
      | MeasurementNode_1_6    | Alm2                     |
      | MeasurementId_1_7      |                        1 |
      | MeasurementNode_1_7    | Alm3                     |
      | MeasurementId_1_8      |                        1 |
      | MeasurementNode_1_8    | Alm4                     |
      | MeasurementId_1_9      |                        1 |
      | MeasurementNode_1_9    | Wrn1                     |
      | MeasurementId_1_10     |                        1 |
      | MeasurementNode_1_10   | Wrn2                     |
      | MeasurementId_1_11     |                        1 |
      | MeasurementNode_1_11   | Wrn3                     |
      | MeasurementId_1_12     |                        1 |
      | MeasurementNode_1_12   | Wrn4                     |
      | MeasurementId_1_13     |                        1 |
      | MeasurementNode_1_13   | OpCntRs                  |
