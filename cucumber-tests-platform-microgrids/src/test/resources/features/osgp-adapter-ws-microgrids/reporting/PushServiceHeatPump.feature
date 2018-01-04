@Microgrids @Platform @Iec61850MockServerMarkerWadden
Feature: Microgrids Receive reports for Heat Pump
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario: Receive a Heat Pump measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
      | Port                 |                   62103 |
      | EnableAllReports     | true                    |
    And OSGP is connected to the Marker Wadden RTU
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
    When the Marker Wadden RTU pushes a report
      | LogicalDevice | HEAT_PUMP1   |
      | ReportType    | Measurements |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-MARKER-WADDENREPORT  |
      | Result                 | OK                       |
      | ReportId               | HEAT_PUMP1_Measurements  |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | HEAT_PUMP                |
      | NumberOfMeasurements_1 |                       10 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | TotW                     |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | TotWh                    |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | MaxWPhs                  |
      | MeasurementId_1_4      |                        1 |
      | MeasurementNode_1_4    | MinWPhs                  |
      | MeasurementId_1_5      |                        1 |
      | MeasurementNode_1_5    | TotPF                    |
      | MeasurementId_1_6      |                        1 |
      | MeasurementNode_1_6    | TmpSv                    |
      | MeasurementId_1_7      |                        2 |
      | MeasurementNode_1_7    | TmpSv                    |
      | MeasurementId_1_8      |                        3 |
      | MeasurementNode_1_8    | TmpSv                    |
      | MeasurementId_1_9      |                        4 |
      | MeasurementNode_1_9    | TmpSv                    |
      | MeasurementId_1_10     |                        1 |
      | MeasurementNode_1_10   | FlwRte                   |

  Scenario: Receive a Heat Pump status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
      | Port                 |                   62103 |
      | EnableAllReports     | true                    |
    And OSGP is connected to the Marker Wadden RTU
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
    When the Marker Wadden RTU pushes a report
      | LogicalDevice | HEAT_PUMP1 |
      | ReportType    | Status     |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-MARKER-WADDENREPORT  |
      | Result                 | OK                       |
      | ReportId               | HEAT_PUMP1_Status        |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | HEAT_PUMP                |
      | NumberOfMeasurements_1 |                       19 |
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
      | MeasurementId_1_16     |                        1 |
      | MeasurementNode_1_16   | SchdId                   |
      | MeasurementId_1_17     |                        2 |
      | MeasurementNode_1_17   | SchdId                   |
      | MeasurementId_1_18     |                        3 |
      | MeasurementNode_1_18   | SchdId                   |
      | MeasurementId_1_19     |                        4 |
      | MeasurementNode_1_19   | SchdId                   |
