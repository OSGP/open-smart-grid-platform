@Microgrids @Platform @Iec61850MockServerMarkerWadden
Feature: Microgrids Get Load Data
  As an OSGP client
  I want to get Load data from an RTU
  So this data can be used by other processes

  Scenario: Request data for combined Load device
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKERWADDEN-LOAD |
      | Port                 |                 62103 |
      | UseCombinedLoad      | true                  |
    And the Marker Wadden RTU returning
      #...........................................................
      | LOAD1 | LLN0.Mod.stVal      |                   1 |
      | LOAD1 | LLN0.Mod.q          | VALIDITY_GOOD       |
      | LOAD1 | LLN0.Mod.t          | 2017-02-01T12:01:00 |
      | LOAD1 | LLN0.Beh.stVal      |                   2 |
      | LOAD1 | LLN0.Beh.q          | VALIDITY_GOOD       |
      | LOAD1 | LLN0.Beh.t          | 2017-02-01T12:01:00 |
      | LOAD1 | LLN0.Health.stVal   |                   3 |
      | LOAD1 | LLN0.Health.q       | VALIDITY_GOOD       |
      | LOAD1 | LLN0.Health.t       | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD1 | GGIO1.Alm1.stVal    | false               |
      | LOAD1 | GGIO1.Alm1.q        | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.Alm1.t        | 2017-02-01T12:01:00 |
      | LOAD1 | GGIO1.Alm2.stVal    | true                |
      | LOAD1 | GGIO1.Alm2.q        | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.Alm2.t        | 2017-02-01T12:01:00 |
      | LOAD1 | GGIO1.Alm3.stVal    | false               |
      | LOAD1 | GGIO1.Alm3.q        | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.Alm3.t        | 2017-02-01T12:01:00 |
      | LOAD1 | GGIO1.Alm4.stVal    | true                |
      | LOAD1 | GGIO1.Alm4.q        | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.Alm4.t        | 2017-02-01T12:01:00 |
      | LOAD1 | GGIO1.IntIn1.stVal  |                   4 |
      | LOAD1 | GGIO1.IntIn1.q      | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.IntIn1.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD1 | GGIO1.Wrn1.stVal    | false               |
      | LOAD1 | GGIO1.Wrn1.q        | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.Wrn1.t        | 2017-02-01T12:01:00 |
      | LOAD1 | GGIO1.Wrn2.stVal    | true                |
      | LOAD1 | GGIO1.Wrn2.q        | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.Wrn2.t        | 2017-02-01T12:01:00 |
      | LOAD1 | GGIO1.Wrn3.stVal    | false               |
      | LOAD1 | GGIO1.Wrn3.q        | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.Wrn3.t        | 2017-02-01T12:01:00 |
      | LOAD1 | GGIO1.Wrn4.stVal    | true                |
      | LOAD1 | GGIO1.Wrn4.q        | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.Wrn4.t        | 2017-02-01T12:01:00 |
      | LOAD1 | GGIO1.IntIn2.stVal  |                   5 |
      | LOAD1 | GGIO1.IntIn2.q      | VALIDITY_GOOD       |
      | LOAD1 | GGIO1.IntIn2.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD1 | MMXU1.TotW.mag.f    |                  10 |
      | LOAD1 | MMXU1.TotW.q        | VALIDITY_GOOD       |
      | LOAD1 | MMXU1.TotW.t        | 2017-02-01T12:02:00 |
      | LOAD1 | MMXU1.MinWPhs.mag.f |                  11 |
      | LOAD1 | MMXU1.MinWPhs.q     | VALIDITY_GOOD       |
      | LOAD1 | MMXU1.MinWPhs.t     | 2017-02-01T12:02:00 |
      | LOAD1 | MMXU1.MaxWPhs.mag.f |                  12 |
      | LOAD1 | MMXU1.MaxWPhs.q     | VALIDITY_GOOD       |
      | LOAD1 | MMXU1.MaxWPhs.t     | 2017-02-01T12:02:00 |
      #...........................................................
      | LOAD1 | MMXU2.TotW.mag.f    |                  20 |
      | LOAD1 | MMXU2.TotW.q        | VALIDITY_GOOD       |
      | LOAD1 | MMXU2.TotW.t        | 2017-02-01T12:02:00 |
      | LOAD1 | MMXU2.MinWPhs.mag.f |                  21 |
      | LOAD1 | MMXU2.MinWPhs.q     | VALIDITY_GOOD       |
      | LOAD1 | MMXU2.MinWPhs.t     | 2017-02-01T12:02:00 |
      | LOAD1 | MMXU2.MaxWPhs.mag.f |                  22 |
      | LOAD1 | MMXU2.MaxWPhs.q     | VALIDITY_GOOD       |
      | LOAD1 | MMXU2.MaxWPhs.t     | 2017-02-01T12:02:00 |
    When a get data request is received
      #...........................................................
      | DeviceIdentification       | RTU-MARKERWADDEN-LOAD |
      | NumberOfSystems            |                     1 |
      #...........................................................
      | SystemId_1                 |                     1 |
      | SystemType_1               | LOAD                  |
      | NumberOfMeasurements_1     |                    19 |
      #...........................................................
      | MeasurementFilterNode_1_1  | Mod                   |
      | MeasurementFilterNode_1_2  | Beh                   |
      | MeasurementFilterNode_1_3  | Health                |
      | MeasurementFilterNode_1_4  | Alm1                  |
      | MeasurementFilterNode_1_5  | Alm2                  |
      | MeasurementFilterNode_1_6  | Alm3                  |
      | MeasurementFilterNode_1_7  | Alm4                  |
      | MeasurementFilterNode_1_8  | IntIn1                |
      | MeasurementFilterNode_1_9  | Wrn1                  |
      | MeasurementFilterNode_1_10 | Wrn2                  |
      | MeasurementFilterNode_1_11 | Wrn3                  |
      | MeasurementFilterNode_1_12 | Wrn4                  |
      | MeasurementFilterNode_1_13 | IntIn2                |
      | MeasurementFilterId_1_14   |                     1 |
      | MeasurementFilterNode_1_14 | TotW                  |
      | MeasurementFilterId_1_15   |                     1 |
      | MeasurementFilterNode_1_15 | MinWPhs               |
      | MeasurementFilterId_1_16   |                     1 |
      | MeasurementFilterNode_1_16 | MaxWPhs               |
      #...........................................................
      | MeasurementFilterId_1_17   |                     2 |
      | MeasurementFilterNode_1_17 | TotW                  |
      | MeasurementFilterId_1_18   |                     2 |
      | MeasurementFilterNode_1_18 | MinWPhs               |
      | MeasurementFilterId_1_19   |                     2 |
      | MeasurementFilterNode_1_19 | MaxWPhs               |
    Then the get data response should be returned
      #...........................................................
      | DeviceIdentification      | RTU-MARKERWADDEN-LOAD    |
      | Result                    | OK                       |
      | NumberOfSystems           |                        1 |
      #...........................................................
      | SystemId_1                |                        1 |
      | SystemType_1              | LOAD                     |
      | NumberOfMeasurements_1    |                       19 |
      #...........................................................
      | MeasurementId_1_1         |                        1 |
      | MeasurementNode_1_1       | Mod                      |
      | MeasurementValue_1_1      |                        1 |
      | MeasurementQualifier_1_1  |                        0 |
      | MeasurementTime_1_1       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_2         |                        1 |
      | MeasurementNode_1_2       | Beh                      |
      | MeasurementValue_1_2      |                        2 |
      | MeasurementQualifier_1_2  |                        0 |
      | MeasurementTime_1_2       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_3         |                        1 |
      | MeasurementNode_1_3       | Health                   |
      | MeasurementValue_1_3      |                        3 |
      | MeasurementQualifier_1_3  |                        0 |
      | MeasurementTime_1_3       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_4         |                        1 |
      | MeasurementNode_1_4       | Alm1                     |
      | MeasurementValue_1_4      |                        0 |
      | MeasurementQualifier_1_4  |                        0 |
      | MeasurementTime_1_4       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_5         |                        1 |
      | MeasurementNode_1_5       | Alm2                     |
      | MeasurementValue_1_5      |                        1 |
      | MeasurementQualifier_1_5  |                        0 |
      | MeasurementTime_1_5       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_6         |                        1 |
      | MeasurementNode_1_6       | Alm3                     |
      | MeasurementValue_1_6      |                        0 |
      | MeasurementQualifier_1_6  |                        0 |
      | MeasurementTime_1_6       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_7         |                        1 |
      | MeasurementNode_1_7       | Alm4                     |
      | MeasurementValue_1_7      |                        1 |
      | MeasurementQualifier_1_7  |                        0 |
      | MeasurementTime_1_7       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_8         |                        1 |
      | MeasurementNode_1_8       | IntIn1                   |
      | MeasurementValue_1_8      |                        4 |
      | MeasurementQualifier_1_8  |                        0 |
      | MeasurementTime_1_8       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_9         |                        1 |
      | MeasurementNode_1_9       | Wrn1                     |
      | MeasurementValue_1_9      |                        0 |
      | MeasurementQualifier_1_9  |                        0 |
      | MeasurementTime_1_9       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_10        |                        1 |
      | MeasurementNode_1_10      | Wrn2                     |
      | MeasurementValue_1_10     |                        1 |
      | MeasurementQualifier_1_10 |                        0 |
      | MeasurementTime_1_10      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_11        |                        1 |
      | MeasurementNode_1_11      | Wrn3                     |
      | MeasurementValue_1_11     |                        0 |
      | MeasurementQualifier_1_11 |                        0 |
      | MeasurementTime_1_11      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_12        |                        1 |
      | MeasurementNode_1_12      | Wrn4                     |
      | MeasurementValue_1_12     |                        1 |
      | MeasurementQualifier_1_12 |                        0 |
      | MeasurementTime_1_12      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_1_13        |                        1 |
      | MeasurementNode_1_13      | IntIn2                   |
      | MeasurementValue_1_13     |                        5 |
      | MeasurementQualifier_1_13 |                        0 |
      | MeasurementTime_1_13      | 2017-02-01T12:01:00.000Z |
      #...........................................................
      | MeasurementId_1_14        |                        1 |
      | MeasurementNode_1_14      | TotW                     |
      | MeasurementValue_1_14     |                       10 |
      | MeasurementQualifier_1_14 |                        0 |
      | MeasurementTime_1_14      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_1_15        |                        1 |
      | MeasurementNode_1_15      | MinWPhs                  |
      | MeasurementValue_1_15     |                       11 |
      | MeasurementQualifier_1_15 |                        0 |
      | MeasurementTime_1_15      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_1_16        |                        1 |
      | MeasurementNode_1_16      | MaxWPhs                  |
      | MeasurementValue_1_16     |                       12 |
      | MeasurementQualifier_1_16 |                        0 |
      | MeasurementTime_1_16      | 2017-02-01T12:02:00.000Z |
      #...........................................................
      | MeasurementId_1_17        |                        2 |
      | MeasurementNode_1_17      | TotW                     |
      | MeasurementValue_1_17     |                       20 |
      | MeasurementQualifier_1_17 |                        0 |
      | MeasurementTime_1_17      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_1_18        |                        2 |
      | MeasurementNode_1_18      | MinWPhs                  |
      | MeasurementValue_1_18     |                       21 |
      | MeasurementQualifier_1_18 |                        0 |
      | MeasurementTime_1_18      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_1_19        |                        2 |
      | MeasurementNode_1_19      | MaxWPhs                  |
      | MeasurementValue_1_19     |                       22 |
      | MeasurementQualifier_1_19 |                        0 |
      | MeasurementTime_1_19      | 2017-02-01T12:02:00.000Z |
