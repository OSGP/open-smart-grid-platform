@MicroGrids @Platform @Iec61850MockServerSchoteroog
Feature: Microgrids Get CHP Data
  As an OSGP client
  I want to get CHP data from an RTU
  So this data can be used by other processes

  Scenario: Request CHP
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOG |
      | Port                 |          62104 |
    And the Schoteroog RTU returning
      | CHP1 | TTMP1.TmpSv.instMag.f |                  10 |
      | CHP1 | TTMP1.TmpSv.q         | VALIDITY_GOOD       |
      | CHP1 | TTMP1.TmpSv.t         | 2017-02-01T12:01:00 |
      | CHP1 | TTMP2.TmpSv.instMag.f |                  20 |
      | CHP1 | TTMP2.TmpSv.q         | VALIDITY_GOOD       |
      | CHP1 | TTMP2.TmpSv.t         | 2017-02-01T12:02:00 |
    When a get data request is received
      | DeviceIdentification      | RTU-SCHOTEROOG |
      | NumberOfSystems           |              1 |
      | SystemId_1                |              1 |
      | SystemType_1              | CHP            |
      | NumberOfMeasurements_1    |              2 |
      | MeasurementFilterNode_1_1 | TmpSv          |
      | MeasurementFilterId_1_1   |              1 |
      | MeasurementFilterNode_1_2 | TmpSv          |
      | MeasurementFilterId_1_2   |              2 |
    Then the get data response should be returned
      | DeviceIdentification     | RTU-SCHOTEROOG           |
      | Result                   | OK                       |
      | NumberOfSystems          |                        1 |
      | SystemId_1               |                        1 |
      | SystemType_1             | CHP                      |
      | NumberOfMeasurements_1   |                        2 |
      | MeasurementId_1_1        |                        1 |
      | MeasurementNode_1_1      | TmpSv                    |
      | MeasurementValue_1_1     |                       10 |
      | MeasurementQualifier_1_1 |                        0 |
      | MeasurementTime_1_1      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_2        |                        2 |
      | MeasurementNode_1_2      | TmpSv                    |
      | MeasurementValue_1_2     |                       20 |
      | MeasurementQualifier_1_2 |                        0 |
      | MeasurementTime_1_2      | 2017-02-01T12:02:00.000Z |
