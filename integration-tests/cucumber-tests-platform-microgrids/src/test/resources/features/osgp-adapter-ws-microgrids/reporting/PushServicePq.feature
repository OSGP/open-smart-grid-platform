# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Microgrids @Platform @Iec61850MockServerPampus
Feature: Microgrids Receive reports for PQ
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario Outline: Receive a PQ measurements report with PNV measurements
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | PQ1                   |
      | Node          | <Report_Trigger_Node> |
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
      | NumberOfMeasurements_1 |                        3 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | PNV.phsA                 |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | PNV.phsB                 |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | PNV.phsC                 |

    Examples: 
      | Report_Trigger_Node |
      | MMXU1.PNV.phsA.q    |
      | MMXU1.PNV.phsB.q    |
      | MMXU1.PNV.phsC.q    |

  Scenario Outline: Receive a PQ measurements report with PF measurements
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | PQ1                   |
      | Node          | <Report_Trigger_Node> |
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
      | NumberOfMeasurements_1 |                        3 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | PF.phsA                  |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | PF.phsB                  |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | PF.phsC                  |

    Examples: 
      | Report_Trigger_Node |
      | MMXU1.PF.phsA.q     |
      | MMXU1.PF.phsB.q     |
      | MMXU1.PF.phsC.q     |

  Scenario Outline: Receive a PQ measurements report with Z measurements
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | PQ1                   |
      | Node          | <Report_Trigger_Node> |
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
      | NumberOfMeasurements_1 |                        3 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | Z.phsA                   |
      | MeasurementId_1_2      |                        1 |
      | MeasurementNode_1_2    | Z.phsB                   |
      | MeasurementId_1_3      |                        1 |
      | MeasurementNode_1_3    | Z.phsC                   |

    Examples: 
      | Report_Trigger_Node |
      | MMXU1.Z.phsA.q      |
      | MMXU1.Z.phsB.q      |
      | MMXU1.Z.phsC.q      |

  Scenario: Receive a PQ measurements report with Hz measurements
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | PQ1        |
      | Node          | MMXU1.Hz.q |
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
      | NumberOfMeasurements_1 |                        1 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | Hz                       |

  Scenario Outline: Receive a PQ status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUSREPORT |
      | Port                 |            62102 |
      | EnableAllReports     | true             |
    And OSGP is connected to the Pampus RTU
      | DeviceIdentification | RTU-PAMPUSREPORT |
    When the Pampus RTU pushes a report
      | LogicalDevice | PQ1                   |
      | Node          | <Report_Trigger_Node> |
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
      | NumberOfMeasurements_1 |                        1 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | <Measurement_Node>       |

    Examples: 
      | Measurement_Node | Report_Trigger_Node |
      | Beh              | LLN0.Beh.q          |
      | Health           | LLN0.Health.q       |
      | IntIn1           | GGIO1.IntIn1.q      |
      | IntIn2           | GGIO1.IntIn2.q      |
      | Alm1             | GGIO1.Alm1.q        |
      | Alm2             | GGIO1.Alm2.q        |
      | Alm3             | GGIO1.Alm3.q        |
      | Alm4             | GGIO1.Alm4.q        |
      | Wrn1             | GGIO1.Wrn1.q        |
      | Wrn2             | GGIO1.Wrn2.q        |
      | Wrn3             | GGIO1.Wrn3.q        |
      | Wrn4             | GGIO1.Wrn4.q        |
