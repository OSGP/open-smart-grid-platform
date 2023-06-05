# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Microgrids @Platform @Iec61850MockServer @Iec61850MockServerPampus
Feature: Microgrids Get Wind Data
  As an OSGP client
  I want to get Wind data from an RTU
  So this data can be used by other processes

  Scenario: Request Wind
    Given an rtu iec61850 device
      | DeviceIdentification | RTU_PAMPUS_WIND |
      | Port                 |           62102 |
    And the Pampus RTU returning
      | WIND1 | LLN0.Mod.stVal          |                   1 |
      | WIND1 | LLN0.Mod.q              | VALIDITY_GOOD       |
      | WIND1 | LLN0.Beh.stVal          |                   2 |
      | WIND1 | LLN0.Beh.q              | VALIDITY_GOOD       |
      | WIND1 | LLN0.Health.stVal       |                   3 |
      | WIND1 | LLN0.Health.q           | VALIDITY_GOOD       |
      #.......................................................
      | WIND1 | GGIO1.Alm1.stVal        | false               |
      | WIND1 | GGIO1.Alm1.q            | VALIDITY_GOOD       |
      | WIND1 | GGIO1.Alm1.t            | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.Alm2.stVal        | true                |
      | WIND1 | GGIO1.Alm2.q            | VALIDITY_GOOD       |
      | WIND1 | GGIO1.Alm2.t            | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.Alm3.stVal        | false               |
      | WIND1 | GGIO1.Alm3.q            | VALIDITY_GOOD       |
      | WIND1 | GGIO1.Alm3.t            | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.Alm4.stVal        | true                |
      | WIND1 | GGIO1.Alm4.q            | VALIDITY_GOOD       |
      | WIND1 | GGIO1.Alm4.t            | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.IntIn1.stVal      |                   4 |
      | WIND1 | GGIO1.IntIn1.q          | VALIDITY_GOOD       |
      | WIND1 | GGIO1.IntIn1.t          | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.IntIn2.stVal      |                   5 |
      | WIND1 | GGIO1.IntIn2.q          | VALIDITY_GOOD       |
      | WIND1 | GGIO1.IntIn2.t          | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.Wrn1.stVal        | false               |
      | WIND1 | GGIO1.Wrn1.q            | VALIDITY_GOOD       |
      | WIND1 | GGIO1.Wrn1.t            | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.Wrn2.stVal        | true                |
      | WIND1 | GGIO1.Wrn2.q            | VALIDITY_GOOD       |
      | WIND1 | GGIO1.Wrn2.t            | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.Wrn3.stVal        | false               |
      | WIND1 | GGIO1.Wrn3.q            | VALIDITY_GOOD       |
      | WIND1 | GGIO1.Wrn3.t            | 2017-02-01T12:01:00 |
      | WIND1 | GGIO1.Wrn4.stVal        | true                |
      | WIND1 | GGIO1.Wrn4.q            | VALIDITY_GOOD       |
      | WIND1 | GGIO1.Wrn4.t            | 2017-02-01T12:01:00 |
      #.......................................................
      | WIND1 | MMXU1.TotW.mag.f        |                  10 |
      | WIND1 | MMXU1.TotW.q            | VALIDITY_GOOD       |
      | WIND1 | MMXU1.TotW.t            | 2017-02-01T12:02:00 |
      | WIND1 | MMXU1.TotPF.mag.f       |                  11 |
      | WIND1 | MMXU1.TotPF.q           | VALIDITY_GOOD       |
      | WIND1 | MMXU1.TotPF.t           | 2017-02-01T12:02:00 |
      | WIND1 | MMXU1.MinWPhs.mag.f     |                  12 |
      | WIND1 | MMXU1.MinWPhs.q         | VALIDITY_GOOD       |
      | WIND1 | MMXU1.MinWPhs.t         | 2017-02-01T12:02:00 |
      | WIND1 | MMXU1.MaxWPhs.mag.f     |                  13 |
      | WIND1 | MMXU1.MaxWPhs.q         | VALIDITY_GOOD       |
      | WIND1 | MMXU1.MaxWPhs.t         | 2017-02-01T12:02:00 |
      #.......................................................
      | WIND1 | MMXU1.W.phsA.cVal.mag.f |                  11 |
      | WIND1 | MMXU1.W.phsA.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU1.W.phsA.t          | 2017-02-01T12:02:00 |
      | WIND1 | MMXU1.W.phsB.cVal.mag.f |                  12 |
      | WIND1 | MMXU1.W.phsB.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU1.W.phsB.t          | 2017-02-01T12:02:00 |
      | WIND1 | MMXU1.W.phsC.cVal.mag.f |                  13 |
      | WIND1 | MMXU1.W.phsC.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU1.W.phsC.t          | 2017-02-01T12:02:00 |
      | WIND1 | MMXU2.W.phsA.cVal.mag.f |                  21 |
      | WIND1 | MMXU2.W.phsA.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU2.W.phsA.t          | 2017-02-01T12:02:00 |
      | WIND1 | MMXU2.W.phsB.cVal.mag.f |                  22 |
      | WIND1 | MMXU2.W.phsB.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU2.W.phsB.t          | 2017-02-01T12:02:00 |
      | WIND1 | MMXU2.W.phsC.cVal.mag.f |                  23 |
      | WIND1 | MMXU2.W.phsC.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU2.W.phsC.t          | 2017-02-01T12:02:00 |
      | WIND1 | MMXU3.W.phsA.cVal.mag.f |                  31 |
      | WIND1 | MMXU3.W.phsA.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU3.W.phsA.t          | 2017-02-01T12:02:00 |
      | WIND1 | MMXU3.W.phsB.cVal.mag.f |                  32 |
      | WIND1 | MMXU3.W.phsB.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU3.W.phsB.t          | 2017-02-01T12:02:00 |
      | WIND1 | MMXU3.W.phsC.cVal.mag.f |                  33 |
      | WIND1 | MMXU3.W.phsC.q          | VALIDITY_GOOD       |
      | WIND1 | MMXU3.W.phsC.t          | 2017-02-01T12:02:00 |
      #.......................................................
      | WIND1 | DRCC1.OutWSet.subVal.f  |                  14 |
      | WIND1 | DRCC1.OutWSet.subQ      | VALIDITY_GOOD       |
      #.......................................................
      | WIND1 | DGEN1.TotWh.mag.f       |                  15 |
      | WIND1 | DGEN1.TotWh.q           | VALIDITY_GOOD       |
      | WIND1 | DGEN1.TotWh.t           | 2017-02-01T12:02:00 |
      | WIND1 | DGEN1.GnOpSt.stVal      |                  15 |
      | WIND1 | DGEN1.GnOpSt.q          | VALIDITY_GOOD       |
      | WIND1 | DGEN1.GnOpSt.t          | 2017-02-01T12:02:00 |
      | WIND1 | DGEN1.OpTmsRs.stVal     |                  15 |
      | WIND1 | DGEN1.OpTmsRs.q         | VALIDITY_GOOD       |
      | WIND1 | DGEN1.OpTmsRs.t         | 2017-02-01T12:02:00 |
    When a get data request is received
      | DeviceIdentification       | RTU_PAMPUS_WIND |
      | NumberOfSystems            |               1 |
      | SystemId_1                 |               1 |
      | SystemType_1               | WIND            |
      | NumberOfMeasurements_1     |              29 |
      | MeasurementFilterNode_1_1  | Mod             |
      | MeasurementFilterNode_1_2  | Beh             |
      | MeasurementFilterNode_1_3  | Health          |
      | MeasurementFilterNode_1_4  | Alm1            |
      | MeasurementFilterNode_1_5  | Alm2            |
      | MeasurementFilterNode_1_6  | Alm3            |
      | MeasurementFilterNode_1_7  | Alm4            |
      | MeasurementFilterNode_1_8  | IntIn1          |
      | MeasurementFilterNode_1_9  | Wrn1            |
      | MeasurementFilterNode_1_10 | Wrn2            |
      | MeasurementFilterNode_1_11 | Wrn3            |
      | MeasurementFilterNode_1_12 | Wrn4            |
      | MeasurementFilterNode_1_13 | IntIn2          |
      | MeasurementFilterNode_1_14 | TotW            |
      | MeasurementFilterNode_1_15 | MinWPhs         |
      | MeasurementFilterNode_1_16 | MaxWPhs         |
      | MeasurementFilterId_1_17   |               1 |
      | MeasurementFilterNode_1_17 | W.phsA          |
      | MeasurementFilterId_1_18   |               1 |
      | MeasurementFilterNode_1_18 | W.phsB          |
      | MeasurementFilterId_1_19   |               1 |
      | MeasurementFilterNode_1_19 | W.phsC          |
      | MeasurementFilterId_1_20   |               2 |
      | MeasurementFilterNode_1_20 | W.phsA          |
      | MeasurementFilterId_1_21   |               2 |
      | MeasurementFilterNode_1_21 | W.phsB          |
      | MeasurementFilterId_1_22   |               2 |
      | MeasurementFilterNode_1_22 | W.phsC          |
      | MeasurementFilterId_1_23   |               3 |
      | MeasurementFilterNode_1_23 | W.phsA          |
      | MeasurementFilterId_1_24   |               3 |
      | MeasurementFilterNode_1_24 | W.phsB          |
      | MeasurementFilterId_1_25   |               3 |
      | MeasurementFilterNode_1_25 | W.phsC          |
      | MeasurementFilterNode_1_26 | OutWSet         |
      | MeasurementFilterNode_1_27 | TotWh           |
      | MeasurementFilterNode_1_28 | GnOpSt          |
      | MeasurementFilterNode_1_29 | OpTmsRs         |
    Then the get data response should be returned
      | DeviceIdentification      | RTU_PAMPUS_WIND |
      | Result                    | OK              |
      | NumberOfSystems           |               1 |
      | SystemId_1                |               1 |
      | SystemType_1              | WIND            |
      | NumberOfMeasurements_1    |              29 |
      | MeasurementId_1_1         |               1 |
      | MeasurementNode_1_1       | Mod             |
      | MeasurementQualifier_1_1  |               0 |
      | MeasurementValue_1_1      |             1.0 |
      | MeasurementId_1_2         |               1 |
      | MeasurementNode_1_2       | Beh             |
      | MeasurementQualifier_1_2  |               0 |
      | MeasurementValue_1_2      |             2.0 |
      | MeasurementId_1_3         |               1 |
      | MeasurementNode_1_3       | Health          |
      | MeasurementQualifier_1_3  |               0 |
      | MeasurementValue_1_3      |             3.0 |
      #.........................................................
      | MeasurementId_1_4         |               1 |
      | MeasurementNode_1_4       | Alm1            |
      | MeasurementQualifier_1_4  |               0 |
      | MeasurementValue_1_4      |             0.0 |
      | MeasurementId_1_5         |               1 |
      | MeasurementNode_1_5       | Alm2            |
      | MeasurementQualifier_1_5  |               0 |
      | MeasurementValue_1_5      |             1.0 |
      | MeasurementId_1_6         |               1 |
      | MeasurementNode_1_6       | Alm3            |
      | MeasurementQualifier_1_6  |               0 |
      | MeasurementValue_1_6      |             0.0 |
      | MeasurementId_1_7         |               1 |
      | MeasurementNode_1_7       | Alm4            |
      | MeasurementQualifier_1_7  |               0 |
      | MeasurementValue_1_7      |             1.0 |
      | MeasurementId_1_8         |               1 |
      | MeasurementNode_1_8       | IntIn1          |
      | MeasurementQualifier_1_8  |               0 |
      | MeasurementValue_1_8      |             4.0 |
      #.........................................................
      | MeasurementId_1_9         |               1 |
      | MeasurementNode_1_9       | Wrn1            |
      | MeasurementQualifier_1_9  |               0 |
      | MeasurementValue_1_9      |             0.0 |
      | MeasurementId_1_10        |               1 |
      | MeasurementNode_1_10      | Wrn2            |
      | MeasurementQualifier_1_10 |               0 |
      | MeasurementValue_1_10     |             1.0 |
      | MeasurementId_1_11        |               1 |
      | MeasurementNode_1_11      | Wrn3            |
      | MeasurementQualifier_1_11 |               0 |
      | MeasurementValue_1_11     |             0.0 |
      | MeasurementId_1_12        |               1 |
      | MeasurementNode_1_12      | Wrn4            |
      | MeasurementQualifier_1_12 |               0 |
      | MeasurementValue_1_12     |             1.0 |
      | MeasurementId_1_13        |               1 |
      | MeasurementNode_1_13      | IntIn2          |
      | MeasurementQualifier_1_13 |               0 |
      | MeasurementValue_1_13     |             5.0 |
      #.........................................................
      | MeasurementId_1_14        |               1 |
      | MeasurementNode_1_14      | TotW            |
      | MeasurementQualifier_1_14 |               0 |
      | MeasurementValue_1_14     |            10.0 |
      | MeasurementId_1_15        |               1 |
      | MeasurementNode_1_15      | MinWPhs         |
      | MeasurementQualifier_1_15 |               0 |
      | MeasurementValue_1_15     |            12.0 |
      | MeasurementId_1_16        |               1 |
      | MeasurementNode_1_16      | MaxWPhs         |
      | MeasurementQualifier_1_16 |               0 |
      | MeasurementValue_1_16     |            13.0 |
      #.........................................................
      | MeasurementId_1_17        |               1 |
      | MeasurementNode_1_17      | W.phsA          |
      | MeasurementQualifier_1_17 |               0 |
      | MeasurementValue_1_17     |            11.0 |
      | MeasurementId_1_18        |               1 |
      | MeasurementNode_1_18      | W.phsB          |
      | MeasurementQualifier_1_18 |               0 |
      | MeasurementValue_1_18     |            12.0 |
      | MeasurementId_1_19        |               1 |
      | MeasurementNode_1_19      | W.phsC          |
      | MeasurementQualifier_1_19 |               0 |
      | MeasurementValue_1_19     |            13.0 |
      | MeasurementId_1_20        |               2 |
      | MeasurementNode_1_20      | W.phsA          |
      | MeasurementQualifier_1_20 |               0 |
      | MeasurementValue_1_20     |            21.0 |
      | MeasurementId_1_21        |               2 |
      | MeasurementNode_1_21      | W.phsB          |
      | MeasurementQualifier_1_21 |               0 |
      | MeasurementValue_1_21     |            22.0 |
      | MeasurementId_1_22        |               2 |
      | MeasurementNode_1_22      | W.phsC          |
      | MeasurementQualifier_1_22 |               0 |
      | MeasurementValue_1_22     |            23.0 |
      | MeasurementId_1_23        |               3 |
      | MeasurementNode_1_23      | W.phsA          |
      | MeasurementQualifier_1_23 |               0 |
      | MeasurementValue_1_23     |            31.0 |
      | MeasurementId_1_24        |               3 |
      | MeasurementNode_1_24      | W.phsB          |
      | MeasurementQualifier_1_24 |               0 |
      | MeasurementValue_1_24     |            32.0 |
      | MeasurementId_1_25        |               3 |
      | MeasurementNode_1_25      | W.phsC          |
      | MeasurementQualifier_1_25 |               0 |
      | MeasurementValue_1_25     |            33.0 |
      #.........................................................
      | MeasurementId_1_26        |               1 |
      | MeasurementNode_1_26      | OutWSet         |
      | MeasurementQualifier_1_26 |               0 |
      | MeasurementValue_1_26     |            14.0 |
      #.........................................................
      | MeasurementId_1_27        |               1 |
      | MeasurementNode_1_27      | TotWh           |
      | MeasurementQualifier_1_27 |               0 |
      | MeasurementValue_1_27     |            15.0 |
      | MeasurementId_1_28        |               1 |
      | MeasurementNode_1_28      | GnOpSt          |
      | MeasurementQualifier_1_28 |               0 |
      | MeasurementValue_1_28     |            15.0 |
      | MeasurementId_1_29        |               1 |
      | MeasurementNode_1_29      | OpTmsRs         |
      | MeasurementQualifier_1_29 |               0 |
      | MeasurementValue_1_29     |            15.0 |
