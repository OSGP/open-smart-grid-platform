@Microgrids @Platform @Iec61850MockServerMarkerWadden
Feature: Microgrids Receive reports for Engine
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario Outline: Receive an Engine measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
      | Port                 |                   62103 |
      | EnableAllReports     | true                    |
    And OSGP is connected to the Marker Wadden RTU
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
    When the Marker Wadden RTU pushes a report
      | LogicalDevice | ENGINE1               |
      | Node          | <Report_Trigger_Node> |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-MARKER-WADDENREPORT  |
      | Result                 | OK                       |
      | ReportId               | ENGINE1_Measurements     |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | ENGINE                   |
      | NumberOfMeasurements_1 |                        1 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | <Measurement_Node>       |

    Examples: 
      | Measurement_Node | Report_Trigger_Node |
      | TotW             | MMXU1.TotW.q        |
      | TotWh            | DGEN1.TotWh.q       |
      | MaxWPhs          | MMXU1.MaxWPhs.q     |
      | MinWPhs          | MMXU1.MinWPhs.q     |

  Scenario Outline: Receive an Engine status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
      | Port                 |                   62103 |
      | EnableAllReports     | true                    |
    And OSGP is connected to the Marker Wadden RTU
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
    When the Marker Wadden RTU pushes a report
      | LogicalDevice | ENGINE1               |
      | Node          | <Report_Trigger_Node> |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-MARKER-WADDENREPORT  |
      | Result                 | OK                       |
      | ReportId               | ENGINE1_Status           |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | ENGINE                   |
      | NumberOfMeasurements_1 |                        1 |
      | MeasurementId_1_1      |                        1 |
      | MeasurementNode_1_1    | <Measurement_Node>       |

    Examples: 
      | Measurement_Node | Report_Trigger_Node |
      | Beh              | LLN0.Beh.q          |
      | Health           | LLN0.Health.q       |
      | GnOpSt           | DGEN1.GnOpSt.q      |
      | OpTmsRs          | DGEN1.OpTmsRs.q     |
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
