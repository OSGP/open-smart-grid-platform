# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Microgrids @Platform @Iec61850MockServerSchoteroog
Feature: Microgrids Receive reports for Heat Buffer
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario Outline: Receive a Heat Buffer measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
      | Port                 |                62104 |
      | EnableAllReports     | true                 |
    And OSGP is connected to the Schoteroog RTU
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
    When the Schoteroog RTU pushes a report
      | LogicalDevice | HEAT_BUFFER1          |
      | Node          | <Report_Trigger_Node> |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-SCHOTEROOGREPORT      |
      | Result                 | OK                        |
      | ReportId               | HEAT_BUFFER1_Measurements |
      | NumberOfSystems        |                         1 |
      | ReportSequenceNumber   |                         1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z  |
      | SystemId_1             |                         1 |
      | SystemType_1           | HEAT_BUFFER               |
      | NumberOfMeasurements_1 |                         1 |
      | MeasurementId_1_1      | <ID>                      |
      | MeasurementNode_1_1    | <Measurement_Node>        |

    Examples: 
      | Measurement_Node | ID | Report_Trigger_Node |
      | TmpSv            |  1 | TTMP1.TmpSv.q       |
      | TmpSv            |  2 | TTMP2.TmpSv.q       |
      | TmpSv            |  3 | TTMP3.TmpSv.q       |

  Scenario Outline: Receive a Heat Buffer status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
      | Port                 |                62104 |
      | EnableAllReports     | true                 |
    And OSGP is connected to the Schoteroog RTU
      | DeviceIdentification | RTU-SCHOTEROOGREPORT |
    When the Schoteroog RTU pushes a report
      | LogicalDevice | HEAT_BUFFER1          |
      | Node          | <Report_Trigger_Node> |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-SCHOTEROOGREPORT     |
      | Result                 | OK                       |
      | ReportId               | HEAT_BUFFER1_Status      |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | HEAT_BUFFER              |
      | NumberOfMeasurements_1 |                        1 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | <Measurement_Node>       |

    Examples: 
      | Measurement_Node |  | Report_Trigger_Node |
      | Beh              |  | LLN0.Beh.q          |
      | Health           |  | LLN0.Health.q       |
      | IntIn1           |  | GGIO1.IntIn1.q      |
      | IntIn2           |  | GGIO1.IntIn2.q      |
      | Alm1             |  | GGIO1.Alm1.q        |
      | Alm2             |  | GGIO1.Alm2.q        |
      | Alm3             |  | GGIO1.Alm3.q        |
      | Alm4             |  | GGIO1.Alm4.q        |
      | Wrn1             |  | GGIO1.Wrn1.q        |
      | Wrn2             |  | GGIO1.Wrn2.q        |
      | Wrn3             |  | GGIO1.Wrn3.q        |
      | Wrn4             |  | GGIO1.Wrn4.q        |
