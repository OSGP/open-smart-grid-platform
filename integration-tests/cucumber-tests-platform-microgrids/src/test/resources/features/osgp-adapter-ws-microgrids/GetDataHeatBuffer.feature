@Microgrids @Platform @Iec61850MockServerSchoteroog
Feature: Microgrids Get Heat Buffer Data
  As an OSGP client
  I want to get Heat Buffer data from an RTU
  So this data can be used by other processes

  Scenario: Request Heat Buffer
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOG |
      | Port                 |          62104 |
    And the Schoteroog RTU returning
      | HEAT_BUFFER1 | TTMP1.TmpSv.instMag.f |                  20 |
      | HEAT_BUFFER1 | TTMP1.TmpSv.t         | 2017-02-01T12:01:00 |
      | HEAT_BUFFER1 | TTMP2.TmpSv.instMag.f |                  25 |
      | HEAT_BUFFER1 | TTMP1.TmpSv.t         | 2017-02-01T12:02:00 |
      | HEAT_BUFFER1 | TTMP3.TmpSv.instMag.f |                  30 |
      | HEAT_BUFFER1 | TTMP1.TmpSv.t         | 2017-02-01T12:03:00 |
      | HEAT_BUFFER1 | KTNK1.VlmCap.setMag.f |                1313 |
    When a get data request is received
      | DeviceIdentification      | RTU-SCHOTEROOG |
      | NumberOfSystems           |              1 |
      | SystemId_1                |              1 |
      | SystemType_1              | HEAT_BUFFER    |
      | NumberOfMeasurements_1    |              4 |
      | MeasurementFilterNode_1_1 | TmpSv          |
      | MeasurementFilterId_1_1   |              1 |
      | MeasurementFilterNode_1_2 | TmpSv          |
      | MeasurementFilterId_1_2   |              2 |
      | MeasurementFilterNode_1_3 | TmpSv          |
      | MeasurementFilterId_1_3   |              3 |
      | MeasurementFilterNode_1_4 | VlmCap         |
      | MeasurementFilterId_1_4   |              1 |
    Then the get data response should be returned
      | DeviceIdentification     | RTU-SCHOTEROOG           |
      | Result                   | OK                       |
      | NumberOfSystems          |                        1 |
      | SystemId_1               |                        1 |
      | SystemType_1             | HEAT_BUFFER              |
      | NumberOfMeasurements_1   |                        4 |
      | MeasurementId_1_1        |                        1 |
      | MeasurementNode_1_1      | TmpSv                    |
      | MeasurementValue_1_1     |                       20 |
      | MeasurementQualifier_1_1 |                        0 |
      | MeasurementTime_1_1      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_2        |                        2 |
      | MeasurementNode_1_2      | TmpSv                    |
      | MeasurementValue_1_2     |                       25 |
      | MeasurementQualifier_1_2 |                        0 |
      | MeasurementTime_1_2      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_1_3        |                        3 |
      | MeasurementNode_1_3      | TmpSv                    |
      | MeasurementValue_1_3     |                       30 |
      | MeasurementQualifier_1_3 |                        0 |
      | MeasurementTime_1_3      | 2017-02-01T12:03:00.000Z |
      | MeasurementId_1_4        |                        1 |
      | MeasurementNode_1_4      | VlmCap                   |
      | MeasurementValue_1_4     |                     1313 |
      | MeasurementQualifier_1_4 |                        0 |
