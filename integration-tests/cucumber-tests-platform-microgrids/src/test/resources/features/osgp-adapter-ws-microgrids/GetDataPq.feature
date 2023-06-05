# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Microgrids @Platform @Iec61850MockServerPampus
Feature: Microgrids Get PQ Data
  As an OSGP client
  I want to get PQ data from an RTU
  So this data can be used by other processes

  Scenario: Request PQ
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS |
      | Port                 |      62102 |
    And the Pampus RTU returning
      | PQ1 | LLN0.Mod.stVal            |                   1 |
      | PQ1 | LLN0.Mod.q                | VALIDITY_GOOD       |
      | PQ1 | LLN0.Beh.stVal            |                   2 |
      | PQ1 | LLN0.Beh.q                | VALIDITY_GOOD       |
      | PQ1 | LLN0.Health.stVal         |                   3 |
      | PQ1 | LLN0.Health.q             | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Alm1.stVal          | false               |
      | PQ1 | GGIO1.Alm1.q              | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Alm1.t              | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.Alm2.stVal          | true                |
      | PQ1 | GGIO1.Alm2.q              | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Alm2.t              | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.Alm3.stVal          | false               |
      | PQ1 | GGIO1.Alm3.q              | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Alm3.t              | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.Alm4.stVal          | true                |
      | PQ1 | GGIO1.Alm4.q              | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Alm4.t              | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.IntIn1.stVal        |                   4 |
      | PQ1 | GGIO1.IntIn1.q            | VALIDITY_GOOD       |
      | PQ1 | GGIO1.IntIn1.t            | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.Wrn1.stVal          | false               |
      | PQ1 | GGIO1.Wrn1.q              | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Wrn1.t              | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.Wrn2.stVal          | true                |
      | PQ1 | GGIO1.Wrn2.q              | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Wrn2.t              | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.Wrn3.stVal          | false               |
      | PQ1 | GGIO1.Wrn3.q              | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Wrn3.t              | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.Wrn4.stVal          | true                |
      | PQ1 | GGIO1.Wrn4.q              | VALIDITY_GOOD       |
      | PQ1 | GGIO1.Wrn4.t              | 2017-02-01T12:01:00 |
      | PQ1 | GGIO1.IntIn2.stVal        |                   5 |
      | PQ1 | GGIO1.IntIn2.q            | VALIDITY_GOOD       |
      | PQ1 | GGIO1.IntIn2.t            | 2017-02-01T12:01:00 |
      #...........................................................
      | PQ1 | MMXU1.Hz.mag.f            |                  10 |
      | PQ1 | MMXU1.Hz.q                | VALIDITY_GOOD       |
      | PQ1 | MMXU1.Hz.t                | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.PNV.phsA.cVal.mag.f |                  10 |
      | PQ1 | MMXU1.PNV.phsA.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU1.PNV.phsA.t          | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.PNV.phsB.cVal.mag.f |                  10 |
      | PQ1 | MMXU1.PNV.phsB.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU1.PNV.phsB.t          | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.PNV.phsC.cVal.mag.f |                  10 |
      | PQ1 | MMXU1.PNV.phsC.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU1.PNV.phsC.t          | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.PF.phsA.cVal.mag.f  |                  10 |
      | PQ1 | MMXU1.PF.phsA.q           | VALIDITY_GOOD       |
      | PQ1 | MMXU1.PF.phsA.t           | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.PF.phsB.cVal.mag.f  |                  10 |
      | PQ1 | MMXU1.PF.phsB.q           | VALIDITY_GOOD       |
      | PQ1 | MMXU1.PF.phsB.t           | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.PF.phsC.cVal.mag.f  |                  10 |
      | PQ1 | MMXU1.PF.phsC.q           | VALIDITY_GOOD       |
      | PQ1 | MMXU1.PF.phsC.t           | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.Z.phsA.cVal.mag.f   |                  10 |
      | PQ1 | MMXU1.Z.phsA.q            | VALIDITY_GOOD       |
      | PQ1 | MMXU1.Z.phsA.t            | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.Z.phsB.cVal.mag.f   |                  10 |
      | PQ1 | MMXU1.Z.phsB.q            | VALIDITY_GOOD       |
      | PQ1 | MMXU1.Z.phsB.t            | 2017-02-01T12:02:00 |
      | PQ1 | MMXU1.Z.phsC.cVal.mag.f   |                  10 |
      | PQ1 | MMXU1.Z.phsC.q            | VALIDITY_GOOD       |
      | PQ1 | MMXU1.Z.phsC.t            | 2017-02-01T12:02:00 |
      #...........................................................
      | PQ1 | MMXU2.Hz.mag.f            |                  10 |
      | PQ1 | MMXU2.Hz.q                | VALIDITY_GOOD       |
      | PQ1 | MMXU2.Hz.t                | 2017-02-01T12:02:00 |
      | PQ1 | MMXU2.PNV.phsA.cVal.mag.f |                  10 |
      | PQ1 | MMXU2.PNV.phsA.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU2.PNV.phsA.t          | 2017-02-01T12:02:00 |
      | PQ1 | MMXU2.PNV.phsB.cVal.mag.f |                  10 |
      | PQ1 | MMXU2.PNV.phsB.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU2.PNV.phsB.t          | 2017-02-01T12:02:00 |
      | PQ1 | MMXU2.PNV.phsC.cVal.mag.f |                  10 |
      | PQ1 | MMXU2.PNV.phsC.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU2.PNV.phsC.t          | 2017-02-01T12:02:00 |
      | PQ1 | MMXU2.PF.phsA.cVal.mag.f  |                  10 |
      | PQ1 | MMXU2.PF.phsA.q           | VALIDITY_GOOD       |
      | PQ1 | MMXU2.PF.phsA.t           | 2017-02-01T12:02:00 |
      | PQ1 | MMXU2.PF.phsB.cVal.mag.f  |                  10 |
      | PQ1 | MMXU2.PF.phsB.q           | VALIDITY_GOOD       |
      | PQ1 | MMXU2.PF.phsB.t           | 2017-02-01T12:02:00 |
      | PQ1 | MMXU2.PF.phsC.cVal.mag.f  |                  10 |
      | PQ1 | MMXU2.PF.phsC.q           | VALIDITY_GOOD       |
      | PQ1 | MMXU2.PF.phsC.t           | 2017-02-01T12:02:00 |
      #........................................................
      | PQ1 | MMXU3.Hz.mag.f            |                  10 |
      | PQ1 | MMXU3.Hz.q                | VALIDITY_GOOD       |
      | PQ1 | MMXU3.Hz.t                | 2017-02-01T12:02:00 |
      | PQ1 | MMXU3.PNV.phsA.cVal.mag.f |                  10 |
      | PQ1 | MMXU3.PNV.phsA.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU3.PNV.phsA.t          | 2017-02-01T12:02:00 |
      | PQ1 | MMXU3.PNV.phsB.cVal.mag.f |                  10 |
      | PQ1 | MMXU3.PNV.phsB.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU3.PNV.phsB.t          | 2017-02-01T12:02:00 |
      | PQ1 | MMXU3.PNV.phsC.cVal.mag.f |                  10 |
      | PQ1 | MMXU3.PNV.phsC.q          | VALIDITY_GOOD       |
      | PQ1 | MMXU3.PNV.phsC.t          | 2017-02-01T12:02:00 |
      #........................................................
      | PQ1 | QVVR1.OpCntRs.ctlModel    |                  10 |
    When a get data request is received
      | DeviceIdentification       | RTU-PAMPUS |
      | NumberOfSystems            |          1 |
      | SystemId_1                 |          1 |
      | SystemType_1               | PQ         |
      | NumberOfMeasurements_1     |         35 |
      | MeasurementFilterNode_1_1  | Mod        |
      | MeasurementFilterNode_1_2  | Beh        |
      | MeasurementFilterNode_1_3  | Health     |
      | MeasurementFilterNode_1_4  | Alm1       |
      | MeasurementFilterNode_1_5  | Alm2       |
      | MeasurementFilterNode_1_6  | Alm3       |
      | MeasurementFilterNode_1_7  | Alm4       |
      | MeasurementFilterNode_1_8  | IntIn1     |
      | MeasurementFilterNode_1_9  | Wrn1       |
      | MeasurementFilterNode_1_10 | Wrn2       |
      | MeasurementFilterNode_1_11 | Wrn3       |
      | MeasurementFilterNode_1_12 | Wrn4       |
      | MeasurementFilterNode_1_13 | IntIn2     |
      | MeasurementFilterNode_1_14 | Hz         |
      | MeasurementFilterId_1_14   |          1 |
      | MeasurementFilterNode_1_15 | Hz         |
      | MeasurementFilterId_1_15   |          2 |
      #..........................................
      | MeasurementFilterNode_1_16 | PNV.phsA   |
      | MeasurementFilterId_1_16   |          1 |
      | MeasurementFilterNode_1_17 | PNV.phsB   |
      | MeasurementFilterId_1_17   |          1 |
      | MeasurementFilterNode_1_18 | PNV.phsC   |
      | MeasurementFilterId_1_18   |          1 |
      | MeasurementFilterNode_1_19 | PNV.phsA   |
      | MeasurementFilterId_1_19   |          2 |
      | MeasurementFilterNode_1_20 | PNV.phsB   |
      | MeasurementFilterId_1_20   |          2 |
      | MeasurementFilterNode_1_21 | PNV.phsC   |
      | MeasurementFilterId_1_21   |          2 |
      #..........................................
      | MeasurementFilterNode_1_22 | PF.phsA    |
      | MeasurementFilterId_1_22   |          1 |
      | MeasurementFilterNode_1_23 | PF.phsB    |
      | MeasurementFilterId_1_23   |          1 |
      | MeasurementFilterNode_1_24 | PF.phsC    |
      | MeasurementFilterId_1_24   |          1 |
      | MeasurementFilterNode_1_25 | PF.phsA    |
      | MeasurementFilterId_1_25   |          2 |
      | MeasurementFilterNode_1_26 | PF.phsB    |
      | MeasurementFilterId_1_26   |          2 |
      | MeasurementFilterNode_1_27 | PF.phsC    |
      | MeasurementFilterId_1_27   |          2 |
      | MeasurementFilterNode_1_28 | Z.phsA     |
      | MeasurementFilterId_1_28   |          1 |
      | MeasurementFilterNode_1_29 | Z.phsB     |
      | MeasurementFilterId_1_29   |          1 |
      | MeasurementFilterNode_1_30 | Z.phsC     |
      | MeasurementFilterId_1_30   |          1 |
      #..........................................
      | MeasurementFilterNode_1_31 | Hz         |
      | MeasurementFilterId_1_31   |          3 |
      | MeasurementFilterNode_1_32 | PNV.phsA   |
      | MeasurementFilterId_1_32   |          3 |
      | MeasurementFilterNode_1_33 | PNV.phsB   |
      | MeasurementFilterId_1_33   |          3 |
      | MeasurementFilterNode_1_34 | PNV.phsC   |
      | MeasurementFilterId_1_34   |          3 |
      #..........................................
      | MeasurementFilterNode_1_35 | OpCntRs    |
      | MeasurementFilterId_1_35   |          1 |
    Then the get data response should be returned
      | DeviceIdentification      | RTU-PAMPUS |
      | Result                    | OK         |
      | NumberOfSystems           |          1 |
      | SystemId_1                |          1 |
      | SystemType_1              | PQ         |
      | NumberOfMeasurements_1    |         35 |
      | MeasurementId_1_1         |          1 |
      | MeasurementNode_1_1       | Mod        |
      | MeasurementQualifier_1_1  |          0 |
      | MeasurementValue_1_1      |        1.0 |
      | MeasurementId_1_2         |          1 |
      | MeasurementNode_1_2       | Beh        |
      | MeasurementQualifier_1_2  |          0 |
      | MeasurementValue_1_2      |        2.0 |
      | MeasurementId_1_3         |          1 |
      | MeasurementNode_1_3       | Health     |
      | MeasurementQualifier_1_3  |          0 |
      | MeasurementValue_1_3      |        3.0 |
      #.......................................................
      | MeasurementId_1_4         |          1 |
      | MeasurementNode_1_4       | Alm1       |
      | MeasurementQualifier_1_4  |          0 |
      | MeasurementValue_1_4      |        0.0 |
      | MeasurementId_1_5         |          1 |
      | MeasurementNode_1_5       | Alm2       |
      | MeasurementQualifier_1_5  |          0 |
      | MeasurementValue_1_5      |        1.0 |
      | MeasurementId_1_6         |          1 |
      | MeasurementNode_1_6       | Alm3       |
      | MeasurementQualifier_1_6  |          0 |
      | MeasurementValue_1_6      |        0.0 |
      | MeasurementId_1_7         |          1 |
      | MeasurementNode_1_7       | Alm4       |
      | MeasurementQualifier_1_7  |          0 |
      | MeasurementValue_1_7      |        1.0 |
      | MeasurementId_1_8         |          1 |
      | MeasurementNode_1_8       | IntIn1     |
      | MeasurementQualifier_1_8  |          0 |
      | MeasurementValue_1_8      |        4.0 |
      #.......................................................
      | MeasurementId_1_9         |          1 |
      | MeasurementNode_1_9       | Wrn1       |
      | MeasurementQualifier_1_9  |          0 |
      | MeasurementValue_1_9      |        0.0 |
      | MeasurementId_1_10        |          1 |
      | MeasurementNode_1_10      | Wrn2       |
      | MeasurementQualifier_1_10 |          0 |
      | MeasurementValue_1_10     |        1.0 |
      | MeasurementId_1_11        |          1 |
      | MeasurementNode_1_11      | Wrn3       |
      | MeasurementQualifier_1_11 |          0 |
      | MeasurementValue_1_11     |        0.0 |
      | MeasurementId_1_12        |          1 |
      | MeasurementNode_1_12      | Wrn4       |
      | MeasurementQualifier_1_12 |          0 |
      | MeasurementValue_1_12     |        1.0 |
      | MeasurementId_1_13        |          1 |
      | MeasurementNode_1_13      | IntIn2     |
      | MeasurementQualifier_1_13 |          0 |
      | MeasurementValue_1_13     |        5.0 |
      #.......................................................
      | MeasurementId_1_14        |          1 |
      | MeasurementNode_1_14      | Hz         |
      | MeasurementQualifier_1_14 |          0 |
      | MeasurementValue_1_14     |       10.0 |
      #.......................................................
      | MeasurementId_1_15        |          2 |
      | MeasurementNode_1_15      | Hz         |
      | MeasurementQualifier_1_15 |          0 |
      | MeasurementValue_1_15     |       10.0 |
      #.......................................................
      | MeasurementId_1_16        |          1 |
      | MeasurementNode_1_16      | PNV.phsA   |
      | MeasurementQualifier_1_16 |          0 |
      | MeasurementValue_1_16     |       10.0 |
      | MeasurementId_1_17        |          1 |
      | MeasurementNode_1_17      | PNV.phsB   |
      | MeasurementQualifier_1_17 |          0 |
      | MeasurementValue_1_17     |       10.0 |
      | MeasurementId_1_18        |          1 |
      | MeasurementNode_1_18      | PNV.phsC   |
      | MeasurementQualifier_1_18 |          0 |
      | MeasurementValue_1_18     |       10.0 |
      #.......................................................
      | MeasurementId_1_19        |          2 |
      | MeasurementNode_1_19      | PNV.phsA   |
      | MeasurementQualifier_1_19 |          0 |
      | MeasurementValue_1_19     |       10.0 |
      | MeasurementId_1_20        |          2 |
      | MeasurementNode_1_20      | PNV.phsB   |
      | MeasurementQualifier_1_20 |          0 |
      | MeasurementValue_1_20     |       10.0 |
      | MeasurementId_1_21        |          2 |
      | MeasurementNode_1_21      | PNV.phsC   |
      | MeasurementQualifier_1_21 |          0 |
      | MeasurementValue_1_21     |       10.0 |
      #.......................................................
      | MeasurementId_1_22        |          1 |
      | MeasurementNode_1_22      | PF.phsA    |
      | MeasurementQualifier_1_22 |          0 |
      | MeasurementValue_1_22     |       10.0 |
      | MeasurementId_1_23        |          1 |
      | MeasurementNode_1_23      | PF.phsB    |
      | MeasurementQualifier_1_23 |          0 |
      | MeasurementValue_1_23     |       10.0 |
      | MeasurementId_1_24        |          1 |
      | MeasurementNode_1_24      | PF.phsC    |
      | MeasurementQualifier_1_24 |          0 |
      | MeasurementValue_1_24     |       10.0 |
      #.......................................................
      | MeasurementId_1_25        |          2 |
      | MeasurementNode_1_25      | PF.phsA    |
      | MeasurementQualifier_1_25 |          0 |
      | MeasurementValue_1_25     |       10.0 |
      | MeasurementId_1_26        |          2 |
      | MeasurementNode_1_26      | PF.phsB    |
      | MeasurementQualifier_1_26 |          0 |
      | MeasurementValue_1_26     |       10.0 |
      | MeasurementId_1_27        |          2 |
      | MeasurementNode_1_27      | PF.phsC    |
      | MeasurementQualifier_1_27 |          0 |
      | MeasurementValue_1_27     |       10.0 |
      #.......................................................
      | MeasurementId_1_28        |          1 |
      | MeasurementNode_1_28      | Z.phsA     |
      | MeasurementQualifier_1_28 |          0 |
      | MeasurementValue_1_28     |       10.0 |
      | MeasurementId_1_29        |          1 |
      | MeasurementNode_1_29      | Z.phsB     |
      | MeasurementQualifier_1_29 |          0 |
      | MeasurementValue_1_29     |       10.0 |
      | MeasurementId_1_30        |          1 |
      | MeasurementNode_1_30      | Z.phsC     |
      | MeasurementQualifier_1_30 |          0 |
      | MeasurementValue_1_30     |       10.0 |
      #.......................................................
      | MeasurementId_1_31        |          3 |
      | MeasurementNode_1_31      | Hz         |
      | MeasurementQualifier_1_31 |          0 |
      | MeasurementValue_1_31     |       10.0 |
      #.......................................................
      | MeasurementId_1_32        |          3 |
      | MeasurementNode_1_32      | PNV.phsA   |
      | MeasurementQualifier_1_32 |          0 |
      | MeasurementValue_1_32     |       10.0 |
      | MeasurementId_1_33        |          3 |
      | MeasurementNode_1_33      | PNV.phsB   |
      | MeasurementQualifier_1_33 |          0 |
      | MeasurementValue_1_33     |       10.0 |
      | MeasurementId_1_34        |          3 |
      | MeasurementNode_1_34      | PNV.phsC   |
      | MeasurementQualifier_1_34 |          0 |
      | MeasurementValue_1_34     |       10.0 |
      #.........................................
      | MeasurementId_1_35        |          1 |
      | MeasurementNode_1_35      | OpCntRs    |
      | MeasurementQualifier_1_35 |          0 |
      | MeasurementValue_1_35     |       10.0 |
