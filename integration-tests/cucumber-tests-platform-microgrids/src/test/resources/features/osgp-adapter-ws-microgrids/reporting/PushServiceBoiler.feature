@Microgrids @Platform @Iec61850MockServerMarkerWadden
Feature: Microgrids Receive reports for Boiler
  I want to receive reports from the RTU
  So that I can monitor the microgrid

  Scenario Outline: Receive a Boiler measurements report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
      | Port                 |                   62103 |
      | EnableAllReports     | true                    |
    And OSGP is connected to the Marker Wadden RTU
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
    When the Marker Wadden RTU pushes a report
      | LogicalDevice | BOILER1               |
      | Node          | <Report_Trigger_Node> |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-MARKER-WADDENREPORT  |
      | Result                 | OK                       |
      | ReportId               | BOILER1_Measurements     |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | BOILER                   |
      | NumberOfMeasurements_1 |                        1 |
      | MeasurementId_1_1      | <ID>                     |
      | MeasurementNode_1_1    | <Measurement_Node>       |

    Examples: 
      | Measurement_Node | ID | Report_Trigger_Node |
      | TotW             |  1 | MMXU1.TotW.q        |
      | TotWh            |  1 | DGEN1.TotWh.q       |
      | MaxWPhs          |  1 | MMXU1.MaxWPhs.q     |
      | MinWPhs          |  1 | MMXU1.MinWPhs.q     |
      | TmpSv            |  1 | TTMP1.TmpSv.q       |
      | TmpSv            |  2 | TTMP2.TmpSv.q       |
      | TmpSv            |  3 | TTMP3.TmpSv.q       |
      | TmpSv            |  4 | TTMP4.TmpSv.q       |
      | FlwRte           |  1 | MFLW1.FlwRte.q      |

  Scenario Outline: Receive a Boiler status report
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
      | Port                 |                   62103 |
      | EnableAllReports     | true                    |
    And OSGP is connected to the Marker Wadden RTU
      | DeviceIdentification | RTU-MARKER-WADDENREPORT |
    When the Marker Wadden RTU pushes a report
      | LogicalDevice | BOILER1               |
      | Node          | <Report_Trigger_Node> |
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification   | RTU-MARKER-WADDENREPORT  |
      | Result                 | OK                       |
      | ReportId               | BOILER1_Status           |
      | NumberOfSystems        |                        1 |
      | ReportSequenceNumber   |                        1 |
      | ReportTimeOfEntry      | 2017-05-01T00:00:00.000Z |
      | SystemId_1             |                        1 |
      | SystemType_1           | BOILER                   |
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
