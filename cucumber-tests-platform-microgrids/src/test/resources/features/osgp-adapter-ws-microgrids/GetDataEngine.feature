@Microgrids @Platform
Feature: Microgrids Get Engine Data
  As an OSGP client
  I want to get Engine data from an RTU
  So this data can be used by other processes

  @Iec61850MockServerMarkerWadden
  Scenario: Request ENGINE1
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-MARKER-WADDEN |
      | Port                 |             62103 |
    And the Marker Wadden RTU returning
      | ENGINE1 | LLN0.Mod.stVal         |                   1 |
      | ENGINE1 | LLN0.Mod.q             | VALIDITY_GOOD       |
      | ENGINE1 | LLN0.Mod.t             | 2017-02-01T12:01:00 |
      | ENGINE1 | LLN0.Beh.stVal         |                   2 |
      | ENGINE1 | LLN0.Beh.q             | VALIDITY_GOOD       |
      | ENGINE1 | LLN0.Beh.t             | 2017-02-01T12:01:00 |
      | ENGINE1 | LLN0.Health.stVal      |                   3 |
      | ENGINE1 | LLN0.Health.q          | VALIDITY_GOOD       |
      | ENGINE1 | LLN0.Health.t          | 2017-02-01T12:01:00 |
      #...........................................................
      | ENGINE1 | GGIO1.Alm1.stVal       | false               |
      | ENGINE1 | GGIO1.Alm1.q           | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.Alm1.t           | 2017-02-01T12:01:00 |
      | ENGINE1 | GGIO1.Alm2.stVal       | true                |
      | ENGINE1 | GGIO1.Alm2.q           | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.Alm2.t           | 2017-02-01T12:01:00 |
      | ENGINE1 | GGIO1.Alm3.stVal       | false               |
      | ENGINE1 | GGIO1.Alm3.q           | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.Alm3.t           | 2017-02-01T12:01:00 |
      | ENGINE1 | GGIO1.Alm4.stVal       | true                |
      | ENGINE1 | GGIO1.Alm4.q           | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.Alm4.t           | 2017-02-01T12:01:00 |
      | ENGINE1 | GGIO1.IntIn1.stVal     |                   4 |
      | ENGINE1 | GGIO1.IntIn1.q         | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.IntIn1.t         | 2017-02-01T12:01:00 |
      #...........................................................
      | ENGINE1 | GGIO1.Wrn1.stVal       | false               |
      | ENGINE1 | GGIO1.Wrn1.q           | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.Wrn1.t           | 2017-02-01T12:01:00 |
      | ENGINE1 | GGIO1.Wrn2.stVal       | true                |
      | ENGINE1 | GGIO1.Wrn2.q           | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.Wrn2.t           | 2017-02-01T12:01:00 |
      | ENGINE1 | GGIO1.Wrn3.stVal       | false               |
      | ENGINE1 | GGIO1.Wrn3.q           | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.Wrn3.t           | 2017-02-01T12:01:00 |
      | ENGINE1 | GGIO1.Wrn4.stVal       | true                |
      | ENGINE1 | GGIO1.Wrn4.q           | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.Wrn4.t           | 2017-02-01T12:01:00 |
      | ENGINE1 | GGIO1.IntIn2.stVal     |                   5 |
      | ENGINE1 | GGIO1.IntIn2.q         | VALIDITY_GOOD       |
      | ENGINE1 | GGIO1.IntIn2.t         | 2017-02-01T12:01:00 |
      #...........................................................
      | ENGINE1 | DSCH1.SchdId.setVal    |                   6 |
      | ENGINE1 | DSCH1.SchdTyp.setVal   |                   6 |
      | ENGINE1 | DSCH1.SchCat.setVal    |                   1 |
      | ENGINE1 | DSCH2.SchdId.setVal    |                   6 |
      | ENGINE1 | DSCH2.SchdTyp.setVal   |                   6 |
      | ENGINE1 | DSCH2.SchCat.setVal    |                   2 |
      | ENGINE1 | DSCH3.SchdId.setVal    |                   6 |
      | ENGINE1 | DSCH3.SchdTyp.setVal   |                   6 |
      | ENGINE1 | DSCH3.SchCat.setVal    |                   1 |
      | ENGINE1 | DSCH4.SchdId.setVal    |                   6 |
      | ENGINE1 | DSCH4.SchdTyp.setVal   |                   6 |
      | ENGINE1 | DSCH4.SchCat.setVal    |                   2 |
      #...........................................................
      | ENGINE1 | MMXU1.TotW.mag.f       |                  10 |
      | ENGINE1 | MMXU1.TotW.q           | VALIDITY_GOOD       |
      | ENGINE1 | MMXU1.TotW.t           | 2017-02-01T12:02:00 |
      | ENGINE1 | MMXU1.MinWPhs.mag.f    |                  11 |
      | ENGINE1 | MMXU1.MinWPhs.q        | VALIDITY_GOOD       |
      | ENGINE1 | MMXU1.MinWPhs.t        | 2017-02-01T12:02:00 |
      | ENGINE1 | MMXU1.MaxWPhs.mag.f    |                  12 |
      | ENGINE1 | MMXU1.MaxWPhs.q        | VALIDITY_GOOD       |
      | ENGINE1 | MMXU1.MaxWPhs.t        | 2017-02-01T12:02:00 |
      #...........................................................
      | ENGINE1 | DRCC1.OutWSet.subVal.f |                  14 |
      | ENGINE1 | DRCC1.OutWSet.subQ     | VALIDITY_GOOD       |
      #...........................................................
      | ENGINE1 | DGEN1.TotWh.mag.f      |                  15 |
      | ENGINE1 | DGEN1.TotWh.q          | VALIDITY_GOOD       |
      | ENGINE1 | DGEN1.TotWh.t          | 2017-02-01T12:02:00 |
      | ENGINE1 | DGEN1.GnOpSt.stVal     |                  15 |
      | ENGINE1 | DGEN1.GnOpSt.q         | VALIDITY_GOOD       |
      | ENGINE1 | DGEN1.GnOpSt.t         | 2017-02-01T12:02:00 |
      | ENGINE1 | DGEN1.OpTmsRs.stVal    |                  15 |
      | ENGINE1 | DGEN1.OpTmsRs.q        | VALIDITY_GOOD       |
      | ENGINE1 | DGEN1.OpTmsRs.t        | 2017-02-01T12:02:00 |
    #...........................................................
    When a get data request is received
      | DeviceIdentification       | RTU-MARKER-WADDEN |
      | NumberOfSystems            |                 1 |
      | SystemId_1                 |                 1 |
      | SystemType_1               | ENGINE            |
      | NumberOfMeasurements_1     |                32 |
      | MeasurementFilterNode_1_1  | Mod               |
      | MeasurementFilterNode_1_2  | Beh               |
      | MeasurementFilterNode_1_3  | Health            |
      | MeasurementFilterNode_1_4  | Alm1              |
      | MeasurementFilterNode_1_5  | Alm2              |
      | MeasurementFilterNode_1_6  | Alm3              |
      | MeasurementFilterNode_1_7  | Alm4              |
      | MeasurementFilterNode_1_8  | IntIn1            |
      | MeasurementFilterNode_1_9  | Wrn1              |
      | MeasurementFilterNode_1_10 | Wrn2              |
      | MeasurementFilterNode_1_11 | Wrn3              |
      | MeasurementFilterNode_1_12 | Wrn4              |
      | MeasurementFilterNode_1_13 | IntIn2            |
      | MeasurementFilterNode_1_14 | SchdId            |
      | MeasurementFilterId_1_14   |                 1 |
      | MeasurementFilterNode_1_15 | SchdId            |
      | MeasurementFilterId_1_15   |                 2 |
      | MeasurementFilterNode_1_16 | SchdId            |
      | MeasurementFilterId_1_16   |                 3 |
      | MeasurementFilterNode_1_17 | SchdId            |
      | MeasurementFilterId_1_17   |                 4 |
      | MeasurementFilterNode_1_18 | SchdTyp           |
      | MeasurementFilterId_1_18   |                 1 |
      | MeasurementFilterNode_1_19 | SchdTyp           |
      | MeasurementFilterId_1_19   |                 2 |
      | MeasurementFilterNode_1_20 | SchdTyp           |
      | MeasurementFilterId_1_20   |                 3 |
      | MeasurementFilterNode_1_21 | SchdTyp           |
      | MeasurementFilterId_1_21   |                 4 |
      | MeasurementFilterNode_1_22 | SchCat            |
      | MeasurementFilterId_1_22   |                 1 |
      | MeasurementFilterNode_1_23 | SchCat            |
      | MeasurementFilterId_1_23   |                 2 |
      | MeasurementFilterNode_1_24 | SchCat            |
      | MeasurementFilterId_1_24   |                 3 |
      | MeasurementFilterNode_1_25 | SchCat            |
      | MeasurementFilterId_1_25   |                 4 |
      | MeasurementFilterNode_1_26 | TotW              |
      | MeasurementFilterNode_1_27 | MinWPhs           |
      | MeasurementFilterNode_1_28 | MaxWPhs           |
      | MeasurementFilterNode_1_29 | OutWSet           |
      | MeasurementFilterNode_1_30 | TotWh             |
      | MeasurementFilterNode_1_31 | GnOpSt            |
      | MeasurementFilterNode_1_32 | OpTmsRs           |
    Then the get data response should be returned
      | DeviceIdentification      | RTU-MARKER-WADDEN   |
      | Result                    | OK                  |
      | NumberOfSystems           |                   1 |
      | SystemId_1                |                   1 |
      | SystemType_1              | ENGINE              |
      | NumberOfMeasurements_1    |                  32 |
      | MeasurementId_1_1         |                   1 |
      | MeasurementNode_1_1       | Mod                 |
      | MeasurementQualifier_1_1  |                   0 |
      | MeasurementValue_1_1      |                 1.0 |
      | MeasurementTime_1_1       | 2017-02-01T12:01:00 |
      | MeasurementId_1_2         |                   1 |
      | MeasurementNode_1_2       | Beh                 |
      | MeasurementQualifier_1_2  |                   0 |
      | MeasurementValue_1_2      |                 2.0 |
      | MeasurementTime_1_2       | 2017-02-01T12:01:00 |
      | MeasurementId_1_3         |                   1 |
      | MeasurementNode_1_3       | Health              |
      | MeasurementQualifier_1_3  |                   0 |
      | MeasurementValue_1_3      |                 3.0 |
      | MeasurementTime_1_3       | 2017-02-01T12:01:00 |
      #......................................................
      | MeasurementId_1_4         |                   1 |
      | MeasurementNode_1_4       | Alm1                |
      | MeasurementQualifier_1_4  |                   0 |
      | MeasurementValue_1_4      |                 0.0 |
      | MeasurementTime_1_4       | 2017-02-01T12:01:00 |
      | MeasurementId_1_5         |                   1 |
      | MeasurementNode_1_5       | Alm2                |
      | MeasurementQualifier_1_5  |                   0 |
      | MeasurementValue_1_5      |                 1.0 |
      | MeasurementTime_1_5       | 2017-02-01T12:01:00 |
      | MeasurementId_1_6         |                   1 |
      | MeasurementNode_1_6       | Alm3                |
      | MeasurementQualifier_1_6  |                   0 |
      | MeasurementValue_1_6      |                 0.0 |
      | MeasurementTime_1_6       | 2017-02-01T12:01:00 |
      | MeasurementId_1_7         |                   1 |
      | MeasurementNode_1_7       | Alm4                |
      | MeasurementQualifier_1_7  |                   0 |
      | MeasurementValue_1_7      |                 1.0 |
      | MeasurementTime_1_7       | 2017-02-01T12:01:00 |
      | MeasurementId_1_8         |                   1 |
      | MeasurementNode_1_8       | IntIn1              |
      | MeasurementQualifier_1_8  |                   0 |
      | MeasurementValue_1_8      |                 4.0 |
      | MeasurementTime_1_8       | 2017-02-01T12:01:00 |
      #......................................................
      | MeasurementId_1_9         |                   1 |
      | MeasurementNode_1_9       | Wrn1                |
      | MeasurementQualifier_1_9  |                   0 |
      | MeasurementValue_1_9      |                 0.0 |
      | MeasurementTime_1_9       | 2017-02-01T12:01:00 |
      | MeasurementId_1_10        |                   1 |
      | MeasurementNode_1_10      | Wrn2                |
      | MeasurementQualifier_1_10 |                   0 |
      | MeasurementValue_1_10     |                 1.0 |
      | MeasurementTime_1_10      | 2017-02-01T12:01:00 |
      | MeasurementId_1_11        |                   1 |
      | MeasurementNode_1_11      | Wrn3                |
      | MeasurementQualifier_1_11 |                   0 |
      | MeasurementValue_1_11     |                 0.0 |
      | MeasurementTime_1_11      | 2017-02-01T12:01:00 |
      | MeasurementId_1_12        |                   1 |
      | MeasurementNode_1_12      | Wrn4                |
      | MeasurementQualifier_1_12 |                   0 |
      | MeasurementValue_1_12     |                 1.0 |
      | MeasurementTime_1_12      | 2017-02-01T12:01:00 |
      | MeasurementId_1_13        |                   1 |
      | MeasurementNode_1_13      | IntIn2              |
      | MeasurementQualifier_1_13 |                   0 |
      | MeasurementValue_1_13     |                 5.0 |
      | MeasurementTime_1_13      | 2017-02-01T12:01:00 |
      #......................................................
      | MeasurementId_1_14        |                   1 |
      | MeasurementNode_1_14      | SchdId              |
      | MeasurementQualifier_1_14 |                   0 |
      | MeasurementValue_1_14     |                 6.0 |
      | MeasurementId_1_15        |                   2 |
      | MeasurementNode_1_15      | SchdId              |
      | MeasurementQualifier_1_15 |                   0 |
      | MeasurementValue_1_15     |                 6.0 |
      | MeasurementId_1_16        |                   3 |
      | MeasurementNode_1_16      | SchdId              |
      | MeasurementQualifier_1_16 |                   0 |
      | MeasurementValue_1_16     |                 6.0 |
      | MeasurementId_1_17        |                   4 |
      | MeasurementNode_1_17      | SchdId              |
      | MeasurementQualifier_1_17 |                   0 |
      | MeasurementValue_1_17     |                 6.0 |
      | MeasurementId_1_18        |                   1 |
      | MeasurementNode_1_18      | SchdTyp             |
      | MeasurementQualifier_1_18 |                   0 |
      | MeasurementValue_1_18     |                 6.0 |
      | MeasurementId_1_19        |                   2 |
      | MeasurementNode_1_19      | SchdTyp             |
      | MeasurementQualifier_1_19 |                   0 |
      | MeasurementValue_1_19     |                 6.0 |
      | MeasurementId_1_20        |                   3 |
      | MeasurementNode_1_20      | SchdTyp             |
      | MeasurementQualifier_1_20 |                   0 |
      | MeasurementValue_1_20     |                 6.0 |
      | MeasurementId_1_21        |                   4 |
      | MeasurementNode_1_21      | SchdTyp             |
      | MeasurementQualifier_1_21 |                   0 |
      | MeasurementValue_1_21     |                 6.0 |
      | MeasurementId_1_22        |                   1 |
      | MeasurementNode_1_22      | SchdCat             |
      | MeasurementQualifier_1_22 |                   0 |
      | MeasurementValue_1_22     |                 1.0 |
      | MeasurementId_1_23        |                   2 |
      | MeasurementNode_1_23      | SchdCat             |
      | MeasurementQualifier_1_23 |                   0 |
      | MeasurementValue_1_23     |                 2.0 |
      | MeasurementId_1_24        |                   3 |
      | MeasurementNode_1_24      | SchdCat             |
      | MeasurementQualifier_1_24 |                   0 |
      | MeasurementValue_1_24     |                 1.0 |
      | MeasurementId_1_25        |                   4 |
      | MeasurementNode_1_25      | SchdCat             |
      | MeasurementQualifier_1_25 |                   0 |
      | MeasurementValue_1_25     |                 2.0 |
      #......................................................
      | MeasurementId_1_26        |                   1 |
      | MeasurementNode_1_26      | TotW                |
      | MeasurementQualifier_1_26 |                   0 |
      | MeasurementValue_1_26     |                10.0 |
      | MeasurementTime_1_26      | 2017-02-01T12:02:00 |
      | MeasurementId_1_27        |                   1 |
      | MeasurementNode_1_27      | MinWPhs             |
      | MeasurementQualifier_1_27 |                   0 |
      | MeasurementValue_1_27     |                11.0 |
      | MeasurementTime_1_27      | 2017-02-01T12:02:00 |
      | MeasurementId_1_28        |                   1 |
      | MeasurementNode_1_28      | MaxWPhs             |
      | MeasurementQualifier_1_28 |                   0 |
      | MeasurementValue_1_28     |                12.0 |
      | MeasurementTime_1_28      | 2017-02-01T12:02:00 |
      #......................................................
      | MeasurementId_1_29        |                   1 |
      | MeasurementNode_1_29      | OutWSet             |
      | MeasurementQualifier_1_29 |                   0 |
      | MeasurementValue_1_29     |                14.0 |
      | MeasurementTime_1_29      | 2017-02-01T12:02:00 |
      #......................................................
      | MeasurementId_1_30        |                   1 |
      | MeasurementNode_1_30      | TotWh               |
      | MeasurementQualifier_1_30 |                   0 |
      | MeasurementValue_1_30     |                15.0 |
      | MeasurementTime_1_30      | 2017-02-01T12:02:00 |
      | MeasurementId_1_31        |                   1 |
      | MeasurementNode_1_31      | GnOpSt              |
      | MeasurementQualifier_1_31 |                   0 |
      | MeasurementValue_1_31     |                15.0 |
      | MeasurementTime_1_31      | 2017-02-01T12:02:00 |
      | MeasurementId_1_32        |                   1 |
      | MeasurementNode_1_32      | OpTmsRs             |
      | MeasurementQualifier_1_32 |                   0 |
      | MeasurementValue_1_32     |                15.0 |
      | MeasurementTime_1_32      | 2017-02-01T12:02:00 |
