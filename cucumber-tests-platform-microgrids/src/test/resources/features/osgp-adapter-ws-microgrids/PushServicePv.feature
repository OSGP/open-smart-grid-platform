@MicroGrids @Platform @Iec61850MockServerPampus
Feature: As OSGP client
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario: Receive a PV measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
    And OSGP is connected to the RTU device
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the RTU pushes a report
      | LogicalDevice     | PV1          |
      | LogicalDeviceNode | MMXU1.TotW.q |
      | Value             | OLD_DATA     |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification     | RTU-PAMPUSREPORT         |
      | Result                   | OK                       |
      | ReportId                 | PV1_Measurements         |
      | NumberOfSystems          |                        1 |
      | ReportSequenceNumber     |                        1 |
      | ReportTimeOfEntry        | 2017-05-01T00:00:00.000Z |
      | SystemId_1               |                        1 |
      | SystemType_1             | PV                       |
      | NumberOfMeasurements_1   |                        4 |
      | MeasurementId_1_1        |                        1 |
      | MeasurementNode_1_1      | TotW                     |
      | MeasurementQualifier_1_1 |                     1024 |
      | MeasurementValue_1_1     |                      0.0 |
      | MeasurementId_1_2        |                        1 |
      | MeasurementNode_1_2      | TotWh                    |
      | MeasurementQualifier_1_2 |                        0 |
      | MeasurementValue_1_2     |                      0.0 |
      | MeasurementId_1_3        |                        1 |
      | MeasurementNode_1_3      | MaxWPhs                  |
      | MeasurementQualifier_1_3 |                        0 |
      | MeasurementValue_1_3     |                      0.0 |
      | MeasurementId_1_4        |                        1 |
      | MeasurementNode_1_4      | MinWPhs                  |
      | MeasurementQualifier_1_4 |                        0 |
      | MeasurementValue_1_4     |                      0.0 |

  Scenario: Receive a PV status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
    And OSGP is connected to the RTU device
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the RTU pushes a report
      | LogicalDevice     | PV1           |
      | LogicalDeviceNode | LLN0.Health.q |
      | Value             | OLD_DATA      |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification      | RTU-PAMPUSREPORT         |
      | Result                    | OK                       |
      | ReportId                  | PV1_Status               |
      | NumberOfSystems           |                        1 |
      | ReportSequenceNumber      |                        1 |
      | ReportTimeOfEntry         | 2017-05-01T00:00:00.000Z |
      | SystemId_1                |                        1 |
      | SystemType_1              | PV                       |
      | NumberOfMeasurements_1    |                       15 |
      | MeasurementId_1_1         |                        1 |
      | MeasurementNode_1_1       | Beh                      |
      | MeasurementQualifier_1_1  |                        0 |
      | MeasurementValue_1_1      |                      0.0 |
      | MeasurementId_1_2         |                        1 |
      | MeasurementNode_1_2       | Health                   |
      | MeasurementQualifier_1_2  |                     1024 |
      | MeasurementValue_1_2      |                      3.0 |
      | MeasurementId_1_3         |                        1 |
      | MeasurementNode_1_3       | OutWSet                  |
      | MeasurementQualifier_1_3  |                        0 |
      | MeasurementValue_1_3      |                      0.0 |
      | MeasurementId_1_4         |                        1 |
      | MeasurementNode_1_4       | GnOpSt                   |
      | MeasurementQualifier_1_4  |                        0 |
      | MeasurementValue_1_4      |                      0.0 |
      | MeasurementId_1_5         |                        1 |
      | MeasurementNode_1_5       | OpTmsRs                  |
      | MeasurementQualifier_1_5  |                        0 |
      | MeasurementValue_1_5      |                      0.0 |
      | MeasurementId_1_6         |                        1 |
      | MeasurementNode_1_6       | IntIn1                   |
      | MeasurementQualifier_1_6  |                        0 |
      | MeasurementValue_1_6      |                      0.0 |
      | MeasurementId_1_7         |                        1 |
      | MeasurementNode_1_7       | IntIn2                   |
      | MeasurementQualifier_1_7  |                        0 |
      | MeasurementValue_1_7      |                      0.0 |
      | MeasurementId_1_8         |                        1 |
      | MeasurementNode_1_8       | Alm1                     |
      | MeasurementQualifier_1_8  |                        0 |
      | MeasurementValue_1_8      |                      0.0 |
      | MeasurementId_1_9         |                        1 |
      | MeasurementNode_1_9       | Alm2                     |
      | MeasurementQualifier_1_9  |                        0 |
      | MeasurementValue_1_9      |                      0.0 |
      | MeasurementId_1_10        |                        1 |
      | MeasurementNode_1_10      | Alm3                     |
      | MeasurementQualifier_1_10 |                        0 |
      | MeasurementValue_1_10     |                      0.0 |
      | MeasurementId_1_11        |                        1 |
      | MeasurementNode_1_11      | Alm4                     |
      | MeasurementQualifier_1_11 |                        0 |
      | MeasurementValue_1_11     |                      0.0 |
      | MeasurementId_1_12        |                        1 |
      | MeasurementNode_1_12      | Wrn1                     |
      | MeasurementQualifier_1_12 |                        0 |
      | MeasurementValue_1_12     |                      0.0 |
      | MeasurementId_1_13        |                        1 |
      | MeasurementNode_1_13      | Wrn2                     |
      | MeasurementQualifier_1_13 |                        0 |
      | MeasurementValue_1_13     |                      0.0 |
      | MeasurementId_1_14        |                        1 |
      | MeasurementNode_1_14      | Wrn3                     |
      | MeasurementQualifier_1_14 |                        0 |
      | MeasurementValue_1_14     |                      0.0 |
      | MeasurementId_1_15        |                        1 |
      | MeasurementNode_1_15      | Wrn4                     |
      | MeasurementQualifier_1_15 |                        0 |
      | MeasurementValue_1_15     |                      0.0 |
