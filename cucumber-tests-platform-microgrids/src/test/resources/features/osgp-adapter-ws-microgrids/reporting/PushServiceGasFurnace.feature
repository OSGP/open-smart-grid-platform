@Microgrids @Platform @Iec61850MockServerSchoteroog
Feature: Microgrids Receive reports for Gas Furnace
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario: Receive a Gas Furnace measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
      | Port                 |                62104 |
      | EnableAllReports     | true                 |
    And OSGP is connected to the Schoteroog RTU
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
    When the Schoteroog RTU pushes a report
      | LogicalDevice | GAS_FURNACE1 |
      | ReportType    | Measurements |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-SCHOTEROOGREPORT      |
      | Result                 | OK                        |
      | ReportId               | GAS_FURNACE1_Measurements |
      | NumberOfSystems        |                         1 |
      | ReportSequenceNumber   |                         1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z  |
      | SystemId_1             |                         1 |
      | SystemType_1           | GAS_FURNACE               |
      | NumberOfMeasurements_1 |                         4 |
      | MeasurementId_1_1      |                         1 |
      | MeasurementNode_1_1    | TmpSv                     |
      | MeasurementId_1_2      |                         2 |
      | MeasurementNode_1_2    | TmpSv                     |
      | MeasurementId_1_3      |                         1 |
      | MeasurementNode_1_3    | FlwRte                    |
      | MeasurementId_1_4      |                         2 |
      | MeasurementNode_1_4    | FlwRte                    |

  Scenario: Receive a Gas Furnace status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
      | Port                 |                62104 |
      | EnableAllReports     | true                 |
    And OSGP is connected to the Schoteroog RTU
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
    When the Schoteroog RTU pushes a report
      | LogicalDevice | GAS_FURNACE1 |
      | ReportType    | Status       |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-SCHOTEROOGREPORT     |
      | Result                 | OK                       |
      | ReportId               | GAS_FURNACE1_Status      |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | GAS_FURNACE              |
      | NumberOfMeasurements_1 |                       14 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | Beh                      |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | Health                   |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | Beh                      |
      | MeasurementId_1_4      |                        1 |
      | MeasurementNode_1_4    | Beh                      |
      | MeasurementId_1_5      |                        1 |
      | MeasurementNode_1_5    | IntIn1                   |
      | MeasurementId_1_6      |                        1 |
      | MeasurementNode_1_6    | IntIn2                   |
      | MeasurementId_1_7      |                        1 |
      | MeasurementNode_1_7    | Alm1                     |
      | MeasurementId_1_8      |                        1 |
      | MeasurementNode_1_8    | Alm2                     |
      | MeasurementId_1_9      |                        1 |
      | MeasurementNode_1_9    | Alm3                     |
      | MeasurementId_1_10     |                        1 |
      | MeasurementNode_1_10   | Alm4                     |
      | MeasurementId_1_11     |                        1 |
      | MeasurementNode_1_11   | Wrn1                     |
      | MeasurementId_1_12     |                        1 |
      | MeasurementNode_1_12   | Wrn2                     |
      | MeasurementId_1_13     |                        1 |
      | MeasurementNode_1_13   | Wrn3                     |
      | MeasurementId_1_14     |                        1 |
      | MeasurementNode_1_14   | Wrn4                     |
