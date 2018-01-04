@Microgrids @Platform @Iec61850MockServerPampus
Feature: Microgrids Get Load Data
  As an OSGP client
  I want to get Load data from an RTU
  So this data can be used by other processes

  Scenario: Request data for 5 Load devices
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS-LOAD |
      | Port                 |           62102 |
    And the Pampus RTU returning
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
      #...........................................................
      | LOAD2 | LLN0.Mod.stVal      |                   2 |
      | LOAD2 | LLN0.Mod.q          | VALIDITY_GOOD       |
      | LOAD2 | LLN0.Mod.t          | 2017-02-01T12:01:00 |
      | LOAD2 | LLN0.Beh.stVal      |                   3 |
      | LOAD2 | LLN0.Beh.q          | VALIDITY_GOOD       |
      | LOAD2 | LLN0.Beh.t          | 2017-02-01T12:01:00 |
      | LOAD2 | LLN0.Health.stVal   |                   4 |
      | LOAD2 | LLN0.Health.q       | VALIDITY_GOOD       |
      | LOAD2 | LLN0.Health.t       | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD2 | GGIO1.Alm1.stVal    | false               |
      | LOAD2 | GGIO1.Alm1.q        | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.Alm1.t        | 2017-02-01T12:01:00 |
      | LOAD2 | GGIO1.Alm2.stVal    | true                |
      | LOAD2 | GGIO1.Alm2.q        | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.Alm2.t        | 2017-02-01T12:01:00 |
      | LOAD2 | GGIO1.Alm3.stVal    | false               |
      | LOAD2 | GGIO1.Alm3.q        | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.Alm3.t        | 2017-02-01T12:01:00 |
      | LOAD2 | GGIO1.Alm4.stVal    | true                |
      | LOAD2 | GGIO1.Alm4.q        | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.Alm4.t        | 2017-02-01T12:01:00 |
      | LOAD2 | GGIO1.IntIn1.stVal  |                   5 |
      | LOAD2 | GGIO1.IntIn1.q      | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.IntIn1.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD2 | GGIO1.Wrn1.stVal    | false               |
      | LOAD2 | GGIO1.Wrn1.q        | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.Wrn1.t        | 2017-02-01T12:01:00 |
      | LOAD2 | GGIO1.Wrn2.stVal    | true                |
      | LOAD2 | GGIO1.Wrn2.q        | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.Wrn2.t        | 2017-02-01T12:01:00 |
      | LOAD2 | GGIO1.Wrn3.stVal    | false               |
      | LOAD2 | GGIO1.Wrn3.q        | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.Wrn3.t        | 2017-02-01T12:01:00 |
      | LOAD2 | GGIO1.Wrn4.stVal    | true                |
      | LOAD2 | GGIO1.Wrn4.q        | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.Wrn4.t        | 2017-02-01T12:01:00 |
      | LOAD2 | GGIO1.IntIn2.stVal  |                   6 |
      | LOAD2 | GGIO1.IntIn2.q      | VALIDITY_GOOD       |
      | LOAD2 | GGIO1.IntIn2.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD2 | MMXU1.TotW.mag.f    |                  20 |
      | LOAD2 | MMXU1.TotW.q        | VALIDITY_GOOD       |
      | LOAD2 | MMXU1.TotW.t        | 2017-02-01T12:02:00 |
      | LOAD2 | MMXU1.MinWPhs.mag.f |                  21 |
      | LOAD2 | MMXU1.MinWPhs.q     | VALIDITY_GOOD       |
      | LOAD2 | MMXU1.MinWPhs.t     | 2017-02-01T12:02:00 |
      | LOAD2 | MMXU1.MaxWPhs.mag.f |                  22 |
      | LOAD2 | MMXU1.MaxWPhs.q     | VALIDITY_GOOD       |
      | LOAD2 | MMXU1.MaxWPhs.t     | 2017-02-01T12:02:00 |
      #...........................................................
      #...........................................................
      | LOAD3 | LLN0.Mod.stVal      |                   3 |
      | LOAD3 | LLN0.Mod.q          | VALIDITY_GOOD       |
      | LOAD3 | LLN0.Mod.t          | 2017-02-01T12:01:00 |
      | LOAD3 | LLN0.Beh.stVal      |                   4 |
      | LOAD3 | LLN0.Beh.q          | VALIDITY_GOOD       |
      | LOAD3 | LLN0.Beh.t          | 2017-02-01T12:01:00 |
      | LOAD3 | LLN0.Health.stVal   |                   5 |
      | LOAD3 | LLN0.Health.q       | VALIDITY_GOOD       |
      | LOAD3 | LLN0.Health.t       | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD3 | GGIO1.Alm1.stVal    | false               |
      | LOAD3 | GGIO1.Alm1.q        | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.Alm1.t        | 2017-02-01T12:01:00 |
      | LOAD3 | GGIO1.Alm2.stVal    | true                |
      | LOAD3 | GGIO1.Alm2.q        | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.Alm2.t        | 2017-02-01T12:01:00 |
      | LOAD3 | GGIO1.Alm3.stVal    | false               |
      | LOAD3 | GGIO1.Alm3.q        | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.Alm3.t        | 2017-02-01T12:01:00 |
      | LOAD3 | GGIO1.Alm4.stVal    | true                |
      | LOAD3 | GGIO1.Alm4.q        | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.Alm4.t        | 2017-02-01T12:01:00 |
      | LOAD3 | GGIO1.IntIn1.stVal  |                   6 |
      | LOAD3 | GGIO1.IntIn1.q      | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.IntIn1.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD3 | GGIO1.Wrn1.stVal    | false               |
      | LOAD3 | GGIO1.Wrn1.q        | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.Wrn1.t        | 2017-02-01T12:01:00 |
      | LOAD3 | GGIO1.Wrn2.stVal    | true                |
      | LOAD3 | GGIO1.Wrn2.q        | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.Wrn2.t        | 2017-02-01T12:01:00 |
      | LOAD3 | GGIO1.Wrn3.stVal    | false               |
      | LOAD3 | GGIO1.Wrn3.q        | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.Wrn3.t        | 2017-02-01T12:01:00 |
      | LOAD3 | GGIO1.Wrn4.stVal    | true                |
      | LOAD3 | GGIO1.Wrn4.q        | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.Wrn4.t        | 2017-02-01T12:01:00 |
      | LOAD3 | GGIO1.IntIn2.stVal  |                   7 |
      | LOAD3 | GGIO1.IntIn2.q      | VALIDITY_GOOD       |
      | LOAD3 | GGIO1.IntIn2.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD3 | MMXU1.TotW.mag.f    |                  30 |
      | LOAD3 | MMXU1.TotW.q        | VALIDITY_GOOD       |
      | LOAD3 | MMXU1.TotW.t        | 2017-02-01T12:02:00 |
      | LOAD3 | MMXU1.MinWPhs.mag.f |                  31 |
      | LOAD3 | MMXU1.MinWPhs.q     | VALIDITY_GOOD       |
      | LOAD3 | MMXU1.MinWPhs.t     | 2017-02-01T12:02:00 |
      | LOAD3 | MMXU1.MaxWPhs.mag.f |                  32 |
      | LOAD3 | MMXU1.MaxWPhs.q     | VALIDITY_GOOD       |
      | LOAD3 | MMXU1.MaxWPhs.t     | 2017-02-01T12:02:00 |
      #...........................................................
      #...........................................................
      | LOAD4 | LLN0.Mod.stVal      |                   4 |
      | LOAD4 | LLN0.Mod.q          | VALIDITY_GOOD       |
      | LOAD4 | LLN0.Mod.t          | 2017-02-01T12:01:00 |
      | LOAD4 | LLN0.Beh.stVal      |                   5 |
      | LOAD4 | LLN0.Beh.q          | VALIDITY_GOOD       |
      | LOAD4 | LLN0.Beh.t          | 2017-02-01T12:01:00 |
      | LOAD4 | LLN0.Health.stVal   |                   6 |
      | LOAD4 | LLN0.Health.q       | VALIDITY_GOOD       |
      | LOAD4 | LLN0.Health.t       | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD4 | GGIO1.Alm1.stVal    | false               |
      | LOAD4 | GGIO1.Alm1.q        | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.Alm1.t        | 2017-02-01T12:01:00 |
      | LOAD4 | GGIO1.Alm2.stVal    | true                |
      | LOAD4 | GGIO1.Alm2.q        | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.Alm2.t        | 2017-02-01T12:01:00 |
      | LOAD4 | GGIO1.Alm3.stVal    | false               |
      | LOAD4 | GGIO1.Alm3.q        | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.Alm3.t        | 2017-02-01T12:01:00 |
      | LOAD4 | GGIO1.Alm4.stVal    | true                |
      | LOAD4 | GGIO1.Alm4.q        | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.Alm4.t        | 2017-02-01T12:01:00 |
      | LOAD4 | GGIO1.IntIn1.stVal  |                   7 |
      | LOAD4 | GGIO1.IntIn1.q      | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.IntIn1.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD4 | GGIO1.Wrn1.stVal    | false               |
      | LOAD4 | GGIO1.Wrn1.q        | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.Wrn1.t        | 2017-02-01T12:01:00 |
      | LOAD4 | GGIO1.Wrn2.stVal    | true                |
      | LOAD4 | GGIO1.Wrn2.q        | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.Wrn2.t        | 2017-02-01T12:01:00 |
      | LOAD4 | GGIO1.Wrn3.stVal    | false               |
      | LOAD4 | GGIO1.Wrn3.q        | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.Wrn3.t        | 2017-02-01T12:01:00 |
      | LOAD4 | GGIO1.Wrn4.stVal    | true                |
      | LOAD4 | GGIO1.Wrn4.q        | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.Wrn4.t        | 2017-02-01T12:01:00 |
      | LOAD4 | GGIO1.IntIn2.stVal  |                   8 |
      | LOAD4 | GGIO1.IntIn2.q      | VALIDITY_GOOD       |
      | LOAD4 | GGIO1.IntIn2.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD4 | MMXU1.TotW.mag.f    |                  40 |
      | LOAD4 | MMXU1.TotW.q        | VALIDITY_GOOD       |
      | LOAD4 | MMXU1.TotW.t        | 2017-02-01T12:02:00 |
      | LOAD4 | MMXU1.MinWPhs.mag.f |                  41 |
      | LOAD4 | MMXU1.MinWPhs.q     | VALIDITY_GOOD       |
      | LOAD4 | MMXU1.MinWPhs.t     | 2017-02-01T12:02:00 |
      | LOAD4 | MMXU1.MaxWPhs.mag.f |                  42 |
      | LOAD4 | MMXU1.MaxWPhs.q     | VALIDITY_GOOD       |
      | LOAD4 | MMXU1.MaxWPhs.t     | 2017-02-01T12:02:00 |
      #...........................................................
      #...........................................................
      | LOAD5 | LLN0.Mod.stVal      |                   5 |
      | LOAD5 | LLN0.Mod.q          | VALIDITY_GOOD       |
      | LOAD5 | LLN0.Mod.t          | 2017-02-01T12:01:00 |
      | LOAD5 | LLN0.Beh.stVal      |                   6 |
      | LOAD5 | LLN0.Beh.q          | VALIDITY_GOOD       |
      | LOAD5 | LLN0.Beh.t          | 2017-02-01T12:01:00 |
      | LOAD5 | LLN0.Health.stVal   |                   7 |
      | LOAD5 | LLN0.Health.q       | VALIDITY_GOOD       |
      | LOAD5 | LLN0.Health.t       | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD5 | GGIO1.Alm1.stVal    | false               |
      | LOAD5 | GGIO1.Alm1.q        | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.Alm1.t        | 2017-02-01T12:01:00 |
      | LOAD5 | GGIO1.Alm2.stVal    | true                |
      | LOAD5 | GGIO1.Alm2.q        | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.Alm2.t        | 2017-02-01T12:01:00 |
      | LOAD5 | GGIO1.Alm3.stVal    | false               |
      | LOAD5 | GGIO1.Alm3.q        | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.Alm3.t        | 2017-02-01T12:01:00 |
      | LOAD5 | GGIO1.Alm4.stVal    | true                |
      | LOAD5 | GGIO1.Alm4.q        | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.Alm4.t        | 2017-02-01T12:01:00 |
      | LOAD5 | GGIO1.IntIn1.stVal  |                   8 |
      | LOAD5 | GGIO1.IntIn1.q      | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.IntIn1.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD5 | GGIO1.Wrn1.stVal    | false               |
      | LOAD5 | GGIO1.Wrn1.q        | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.Wrn1.t        | 2017-02-01T12:01:00 |
      | LOAD5 | GGIO1.Wrn2.stVal    | true                |
      | LOAD5 | GGIO1.Wrn2.q        | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.Wrn2.t        | 2017-02-01T12:01:00 |
      | LOAD5 | GGIO1.Wrn3.stVal    | false               |
      | LOAD5 | GGIO1.Wrn3.q        | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.Wrn3.t        | 2017-02-01T12:01:00 |
      | LOAD5 | GGIO1.Wrn4.stVal    | true                |
      | LOAD5 | GGIO1.Wrn4.q        | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.Wrn4.t        | 2017-02-01T12:01:00 |
      | LOAD5 | GGIO1.IntIn2.stVal  |                   9 |
      | LOAD5 | GGIO1.IntIn2.q      | VALIDITY_GOOD       |
      | LOAD5 | GGIO1.IntIn2.t      | 2017-02-01T12:01:00 |
      #...........................................................
      | LOAD5 | MMXU1.TotW.mag.f    |                  50 |
      | LOAD5 | MMXU1.TotW.q        | VALIDITY_GOOD       |
      | LOAD5 | MMXU1.TotW.t        | 2017-02-01T12:02:00 |
      | LOAD5 | MMXU1.MinWPhs.mag.f |                  51 |
      | LOAD5 | MMXU1.MinWPhs.q     | VALIDITY_GOOD       |
      | LOAD5 | MMXU1.MinWPhs.t     | 2017-02-01T12:02:00 |
      | LOAD5 | MMXU1.MaxWPhs.mag.f |                  52 |
      | LOAD5 | MMXU1.MaxWPhs.q     | VALIDITY_GOOD       |
      | LOAD5 | MMXU1.MaxWPhs.t     | 2017-02-01T12:02:00 |
    When a get data request is received
      #...........................................................
      | DeviceIdentification       | RTU-PAMPUS-LOAD |
      | NumberOfSystems            |               5 |
      #...........................................................
      | SystemId_1                 |               1 |
      | SystemType_1               | LOAD            |
      | NumberOfMeasurements_1     |              16 |
      #...........................................................
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
      #...........................................................
      | SystemId_2                 |               2 |
      | SystemType_2               | LOAD            |
      | NumberOfMeasurements_2     |              16 |
      #...........................................................
      | MeasurementFilterNode_2_1  | Mod             |
      | MeasurementFilterNode_2_2  | Beh             |
      | MeasurementFilterNode_2_3  | Health          |
      | MeasurementFilterNode_2_4  | Alm1            |
      | MeasurementFilterNode_2_5  | Alm2            |
      | MeasurementFilterNode_2_6  | Alm3            |
      | MeasurementFilterNode_2_7  | Alm4            |
      | MeasurementFilterNode_2_8  | IntIn1          |
      | MeasurementFilterNode_2_9  | Wrn1            |
      | MeasurementFilterNode_2_10 | Wrn2            |
      | MeasurementFilterNode_2_11 | Wrn3            |
      | MeasurementFilterNode_2_12 | Wrn4            |
      | MeasurementFilterNode_2_13 | IntIn2          |
      | MeasurementFilterNode_2_14 | TotW            |
      | MeasurementFilterNode_2_15 | MinWPhs         |
      | MeasurementFilterNode_2_16 | MaxWPhs         |
      #...........................................................
      | SystemId_3                 |               3 |
      | SystemType_3               | LOAD            |
      | NumberOfMeasurements_3     |              16 |
      #...........................................................
      | MeasurementFilterNode_3_1  | Mod             |
      | MeasurementFilterNode_3_2  | Beh             |
      | MeasurementFilterNode_3_3  | Health          |
      | MeasurementFilterNode_3_4  | Alm1            |
      | MeasurementFilterNode_3_5  | Alm2            |
      | MeasurementFilterNode_3_6  | Alm3            |
      | MeasurementFilterNode_3_7  | Alm4            |
      | MeasurementFilterNode_3_8  | IntIn1          |
      | MeasurementFilterNode_3_9  | Wrn1            |
      | MeasurementFilterNode_3_10 | Wrn2            |
      | MeasurementFilterNode_3_11 | Wrn3            |
      | MeasurementFilterNode_3_12 | Wrn4            |
      | MeasurementFilterNode_3_13 | IntIn2          |
      | MeasurementFilterNode_3_14 | TotW            |
      | MeasurementFilterNode_3_15 | MinWPhs         |
      | MeasurementFilterNode_3_16 | MaxWPhs         |
      #...........................................................
      | SystemId_4                 |               4 |
      | SystemType_4               | LOAD            |
      | NumberOfMeasurements_4     |              16 |
      #...........................................................
      | MeasurementFilterNode_4_1  | Mod             |
      | MeasurementFilterNode_4_2  | Beh             |
      | MeasurementFilterNode_4_3  | Health          |
      | MeasurementFilterNode_4_4  | Alm1            |
      | MeasurementFilterNode_4_5  | Alm2            |
      | MeasurementFilterNode_4_6  | Alm3            |
      | MeasurementFilterNode_4_7  | Alm4            |
      | MeasurementFilterNode_4_8  | IntIn1          |
      | MeasurementFilterNode_4_9  | Wrn1            |
      | MeasurementFilterNode_4_10 | Wrn2            |
      | MeasurementFilterNode_4_11 | Wrn3            |
      | MeasurementFilterNode_4_12 | Wrn4            |
      | MeasurementFilterNode_4_13 | IntIn2          |
      | MeasurementFilterNode_4_14 | TotW            |
      | MeasurementFilterNode_4_15 | MinWPhs         |
      | MeasurementFilterNode_4_16 | MaxWPhs         |
      #...........................................................
      | SystemId_5                 |               5 |
      | SystemType_5               | LOAD            |
      | NumberOfMeasurements_5     |              16 |
      #...........................................................
      | MeasurementFilterNode_5_1  | Mod             |
      | MeasurementFilterNode_5_2  | Beh             |
      | MeasurementFilterNode_5_3  | Health          |
      | MeasurementFilterNode_5_4  | Alm1            |
      | MeasurementFilterNode_5_5  | Alm2            |
      | MeasurementFilterNode_5_6  | Alm3            |
      | MeasurementFilterNode_5_7  | Alm4            |
      | MeasurementFilterNode_5_8  | IntIn1          |
      | MeasurementFilterNode_5_9  | Wrn1            |
      | MeasurementFilterNode_5_10 | Wrn2            |
      | MeasurementFilterNode_5_11 | Wrn3            |
      | MeasurementFilterNode_5_12 | Wrn4            |
      | MeasurementFilterNode_5_13 | IntIn2          |
      | MeasurementFilterNode_5_14 | TotW            |
      | MeasurementFilterNode_5_15 | MinWPhs         |
      | MeasurementFilterNode_5_16 | MaxWPhs         |
    Then the get data response should be returned
      #...........................................................
      | DeviceIdentification      | RTU-PAMPUS-LOAD          |
      | Result                    | OK                       |
      | NumberOfSystems           |                        5 |
      #...........................................................
      | SystemId_1                |                        1 |
      | SystemType_1              | LOAD                     |
      | NumberOfMeasurements_1    |                       16 |
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
      | SystemId_2                |                        2 |
      | SystemType_2              | LOAD                     |
      | NumberOfMeasurements_2    |                       16 |
      #...........................................................
      | MeasurementId_2_1         |                        1 |
      | MeasurementNode_2_1       | Mod                      |
      | MeasurementValue_2_1      |                        2 |
      | MeasurementQualifier_2_1  |                        0 |
      | MeasurementTime_2_1       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_2         |                        1 |
      | MeasurementNode_2_2       | Beh                      |
      | MeasurementValue_2_2      |                        3 |
      | MeasurementQualifier_2_2  |                        0 |
      | MeasurementTime_2_2       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_3         |                        1 |
      | MeasurementNode_2_3       | Health                   |
      | MeasurementValue_2_3      |                        4 |
      | MeasurementQualifier_2_3  |                        0 |
      | MeasurementTime_2_3       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_4         |                        1 |
      | MeasurementNode_2_4       | Alm1                     |
      | MeasurementValue_2_4      |                        0 |
      | MeasurementQualifier_2_4  |                        0 |
      | MeasurementTime_2_4       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_5         |                        1 |
      | MeasurementNode_2_5       | Alm2                     |
      | MeasurementValue_2_5      |                        1 |
      | MeasurementQualifier_2_5  |                        0 |
      | MeasurementTime_2_5       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_6         |                        1 |
      | MeasurementNode_2_6       | Alm3                     |
      | MeasurementValue_2_6      |                        0 |
      | MeasurementQualifier_2_6  |                        0 |
      | MeasurementTime_2_6       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_7         |                        1 |
      | MeasurementNode_2_7       | Alm4                     |
      | MeasurementValue_2_7      |                        1 |
      | MeasurementQualifier_2_7  |                        0 |
      | MeasurementTime_2_7       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_8         |                        1 |
      | MeasurementNode_2_8       | IntIn1                   |
      | MeasurementValue_2_8      |                        5 |
      | MeasurementQualifier_2_8  |                        0 |
      | MeasurementTime_2_8       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_9         |                        1 |
      | MeasurementNode_2_9       | Wrn1                     |
      | MeasurementValue_2_9      |                        0 |
      | MeasurementQualifier_2_9  |                        0 |
      | MeasurementTime_2_9       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_10        |                        1 |
      | MeasurementNode_2_10      | Wrn2                     |
      | MeasurementValue_2_10     |                        1 |
      | MeasurementQualifier_2_10 |                        0 |
      | MeasurementTime_2_10      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_11        |                        1 |
      | MeasurementNode_2_11      | Wrn3                     |
      | MeasurementValue_2_11     |                        0 |
      | MeasurementQualifier_2_11 |                        0 |
      | MeasurementTime_2_11      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_12        |                        1 |
      | MeasurementNode_2_12      | Wrn4                     |
      | MeasurementValue_2_12     |                        1 |
      | MeasurementQualifier_2_12 |                        0 |
      | MeasurementTime_2_12      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_13        |                        1 |
      | MeasurementNode_2_13      | IntIn2                   |
      | MeasurementValue_2_13     |                        6 |
      | MeasurementQualifier_2_13 |                        0 |
      | MeasurementTime_2_13      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_2_14        |                        1 |
      | MeasurementNode_2_14      | TotW                     |
      | MeasurementValue_2_14     |                       20 |
      | MeasurementQualifier_2_14 |                        0 |
      | MeasurementTime_2_14      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_2_15        |                        1 |
      | MeasurementNode_2_15      | MinWPhs                  |
      | MeasurementValue_2_15     |                       21 |
      | MeasurementQualifier_2_15 |                        0 |
      | MeasurementTime_2_15      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_2_16        |                        1 |
      | MeasurementNode_2_16      | MaxWPhs                  |
      | MeasurementValue_2_16     |                       22 |
      | MeasurementQualifier_2_16 |                        0 |
      | MeasurementTime_2_16      | 2017-02-01T12:02:00.000Z |
      #...........................................................
      | SystemId_3                |                        3 |
      | SystemType_3              | LOAD                     |
      | NumberOfMeasurements_3    |                       16 |
      #...........................................................
      | MeasurementId_3_1         |                        1 |
      | MeasurementNode_3_1       | Mod                      |
      | MeasurementValue_3_1      |                        3 |
      | MeasurementQualifier_3_1  |                        0 |
      | MeasurementTime_3_1       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_2         |                        1 |
      | MeasurementNode_3_2       | Beh                      |
      | MeasurementValue_3_2      |                        4 |
      | MeasurementQualifier_3_2  |                        0 |
      | MeasurementTime_3_2       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_3         |                        1 |
      | MeasurementNode_3_3       | Health                   |
      | MeasurementValue_3_3      |                        5 |
      | MeasurementQualifier_3_3  |                        0 |
      | MeasurementTime_3_3       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_4         |                        1 |
      | MeasurementNode_3_4       | Alm1                     |
      | MeasurementValue_3_4      |                        0 |
      | MeasurementQualifier_3_4  |                        0 |
      | MeasurementTime_3_4       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_5         |                        1 |
      | MeasurementNode_3_5       | Alm2                     |
      | MeasurementValue_3_5      |                        1 |
      | MeasurementQualifier_3_5  |                        0 |
      | MeasurementTime_3_5       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_6         |                        1 |
      | MeasurementNode_3_6       | Alm3                     |
      | MeasurementValue_3_6      |                        0 |
      | MeasurementQualifier_3_6  |                        0 |
      | MeasurementTime_3_6       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_7         |                        1 |
      | MeasurementNode_3_7       | Alm4                     |
      | MeasurementValue_3_7      |                        1 |
      | MeasurementQualifier_3_7  |                        0 |
      | MeasurementTime_3_7       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_8         |                        1 |
      | MeasurementNode_3_8       | IntIn1                   |
      | MeasurementValue_3_8      |                        6 |
      | MeasurementQualifier_3_8  |                        0 |
      | MeasurementTime_3_8       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_9         |                        1 |
      | MeasurementNode_3_9       | Wrn1                     |
      | MeasurementValue_3_9      |                        0 |
      | MeasurementQualifier_3_9  |                        0 |
      | MeasurementTime_3_9       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_10        |                        1 |
      | MeasurementNode_3_10      | Wrn2                     |
      | MeasurementValue_3_10     |                        1 |
      | MeasurementQualifier_3_10 |                        0 |
      | MeasurementTime_3_10      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_11        |                        1 |
      | MeasurementNode_3_11      | Wrn3                     |
      | MeasurementValue_3_11     |                        0 |
      | MeasurementQualifier_3_11 |                        0 |
      | MeasurementTime_3_11      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_12        |                        1 |
      | MeasurementNode_3_12      | Wrn4                     |
      | MeasurementValue_3_12     |                        1 |
      | MeasurementQualifier_3_12 |                        0 |
      | MeasurementTime_3_12      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_13        |                        1 |
      | MeasurementNode_3_13      | IntIn2                   |
      | MeasurementValue_3_13     |                        7 |
      | MeasurementQualifier_3_13 |                        0 |
      | MeasurementTime_3_13      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_3_14        |                        1 |
      | MeasurementNode_3_14      | TotW                     |
      | MeasurementValue_3_14     |                       30 |
      | MeasurementQualifier_3_14 |                        0 |
      | MeasurementTime_3_14      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_3_15        |                        1 |
      | MeasurementNode_3_15      | MinWPhs                  |
      | MeasurementValue_3_15     |                       31 |
      | MeasurementQualifier_3_15 |                        0 |
      | MeasurementTime_3_15      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_3_16        |                        1 |
      | MeasurementNode_3_16      | MaxWPhs                  |
      | MeasurementValue_3_16     |                       32 |
      | MeasurementQualifier_3_16 |                        0 |
      | MeasurementTime_3_16      | 2017-02-01T12:02:00.000Z |
      #...........................................................
      | SystemId_4                |                        4 |
      | SystemType_4              | LOAD                     |
      | NumberOfMeasurements_4    |                       16 |
      #...........................................................
      | MeasurementId_4_1         |                        1 |
      | MeasurementNode_4_1       | Mod                      |
      | MeasurementValue_4_1      |                        4 |
      | MeasurementQualifier_4_1  |                        0 |
      | MeasurementTime_4_1       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_2         |                        1 |
      | MeasurementNode_4_2       | Beh                      |
      | MeasurementValue_4_2      |                        5 |
      | MeasurementQualifier_4_2  |                        0 |
      | MeasurementTime_4_2       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_3         |                        1 |
      | MeasurementNode_4_3       | Health                   |
      | MeasurementValue_4_3      |                        6 |
      | MeasurementQualifier_4_3  |                        0 |
      | MeasurementTime_4_3       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_4         |                        1 |
      | MeasurementNode_4_4       | Alm1                     |
      | MeasurementValue_4_4      |                        0 |
      | MeasurementQualifier_4_4  |                        0 |
      | MeasurementTime_4_4       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_5         |                        1 |
      | MeasurementNode_4_5       | Alm2                     |
      | MeasurementValue_4_5      |                        1 |
      | MeasurementQualifier_4_5  |                        0 |
      | MeasurementTime_4_5       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_6         |                        1 |
      | MeasurementNode_4_6       | Alm3                     |
      | MeasurementValue_4_6      |                        0 |
      | MeasurementQualifier_4_6  |                        0 |
      | MeasurementTime_4_6       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_7         |                        1 |
      | MeasurementNode_4_7       | Alm4                     |
      | MeasurementValue_4_7      |                        1 |
      | MeasurementQualifier_4_7  |                        0 |
      | MeasurementTime_4_7       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_8         |                        1 |
      | MeasurementNode_4_8       | IntIn1                   |
      | MeasurementValue_4_8      |                        7 |
      | MeasurementQualifier_4_8  |                        0 |
      | MeasurementTime_4_8       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_9         |                        1 |
      | MeasurementNode_4_9       | Wrn1                     |
      | MeasurementValue_4_9      |                        0 |
      | MeasurementQualifier_4_9  |                        0 |
      | MeasurementTime_4_9       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_10        |                        1 |
      | MeasurementNode_4_10      | Wrn2                     |
      | MeasurementValue_4_10     |                        1 |
      | MeasurementQualifier_4_10 |                        0 |
      | MeasurementTime_4_10      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_11        |                        1 |
      | MeasurementNode_4_11      | Wrn3                     |
      | MeasurementValue_4_11     |                        0 |
      | MeasurementQualifier_4_11 |                        0 |
      | MeasurementTime_4_11      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_12        |                        1 |
      | MeasurementNode_4_12      | Wrn4                     |
      | MeasurementValue_4_12     |                        1 |
      | MeasurementQualifier_4_12 |                        0 |
      | MeasurementTime_4_12      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_13        |                        1 |
      | MeasurementNode_4_13      | IntIn2                   |
      | MeasurementValue_4_13     |                        8 |
      | MeasurementQualifier_4_13 |                        0 |
      | MeasurementTime_4_13      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_4_14        |                        1 |
      | MeasurementNode_4_14      | TotW                     |
      | MeasurementValue_4_14     |                       40 |
      | MeasurementQualifier_4_14 |                        0 |
      | MeasurementTime_4_14      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_4_15        |                        1 |
      | MeasurementNode_4_15      | MinWPhs                  |
      | MeasurementValue_4_15     |                       41 |
      | MeasurementQualifier_4_15 |                        0 |
      | MeasurementTime_4_15      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_4_16        |                        1 |
      | MeasurementNode_4_16      | MaxWPhs                  |
      | MeasurementValue_4_16     |                       42 |
      | MeasurementQualifier_4_16 |                        0 |
      | MeasurementTime_4_16      | 2017-02-01T12:02:00.000Z |
      #...........................................................
      | SystemId_5                |                        5 |
      | SystemType_5              | LOAD                     |
      | NumberOfMeasurements_5    |                       16 |
      #...........................................................
      | MeasurementId_5_1         |                        1 |
      | MeasurementNode_5_1       | Mod                      |
      | MeasurementValue_5_1      |                        5 |
      | MeasurementQualifier_5_1  |                        0 |
      | MeasurementTime_5_1       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_2         |                        1 |
      | MeasurementNode_5_2       | Beh                      |
      | MeasurementValue_5_2      |                        6 |
      | MeasurementQualifier_5_2  |                        0 |
      | MeasurementTime_5_2       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_3         |                        1 |
      | MeasurementNode_5_3       | Health                   |
      | MeasurementValue_5_3      |                        7 |
      | MeasurementQualifier_5_3  |                        0 |
      | MeasurementTime_5_3       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_4         |                        1 |
      | MeasurementNode_5_4       | Alm1                     |
      | MeasurementValue_5_4      |                        0 |
      | MeasurementQualifier_5_4  |                        0 |
      | MeasurementTime_5_4       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_5         |                        1 |
      | MeasurementNode_5_5       | Alm2                     |
      | MeasurementValue_5_5      |                        1 |
      | MeasurementQualifier_5_5  |                        0 |
      | MeasurementTime_5_5       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_6         |                        1 |
      | MeasurementNode_5_6       | Alm3                     |
      | MeasurementValue_5_6      |                        0 |
      | MeasurementQualifier_5_6  |                        0 |
      | MeasurementTime_5_6       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_7         |                        1 |
      | MeasurementNode_5_7       | Alm4                     |
      | MeasurementValue_5_7      |                        1 |
      | MeasurementQualifier_5_7  |                        0 |
      | MeasurementTime_5_7       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_8         |                        1 |
      | MeasurementNode_5_8       | IntIn1                   |
      | MeasurementValue_5_8      |                        8 |
      | MeasurementQualifier_5_8  |                        0 |
      | MeasurementTime_5_8       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_9         |                        1 |
      | MeasurementNode_5_9       | Wrn1                     |
      | MeasurementValue_5_9      |                        0 |
      | MeasurementQualifier_5_9  |                        0 |
      | MeasurementTime_5_9       | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_10        |                        1 |
      | MeasurementNode_5_10      | Wrn2                     |
      | MeasurementValue_5_10     |                        1 |
      | MeasurementQualifier_5_10 |                        0 |
      | MeasurementTime_5_10      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_11        |                        1 |
      | MeasurementNode_5_11      | Wrn3                     |
      | MeasurementValue_5_11     |                        0 |
      | MeasurementQualifier_5_11 |                        0 |
      | MeasurementTime_5_11      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_12        |                        1 |
      | MeasurementNode_5_12      | Wrn4                     |
      | MeasurementValue_5_12     |                        1 |
      | MeasurementQualifier_5_12 |                        0 |
      | MeasurementTime_5_12      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_13        |                        1 |
      | MeasurementNode_5_13      | IntIn2                   |
      | MeasurementValue_5_13     |                        9 |
      | MeasurementQualifier_5_13 |                        0 |
      | MeasurementTime_5_13      | 2017-02-01T12:01:00.000Z |
      | MeasurementId_5_14        |                        1 |
      | MeasurementNode_5_14      | TotW                     |
      | MeasurementValue_5_14     |                       50 |
      | MeasurementQualifier_5_14 |                        0 |
      | MeasurementTime_5_14      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_5_15        |                        1 |
      | MeasurementNode_5_15      | MinWPhs                  |
      | MeasurementValue_5_15     |                       51 |
      | MeasurementQualifier_5_15 |                        0 |
      | MeasurementTime_5_15      | 2017-02-01T12:02:00.000Z |
      | MeasurementId_5_16        |                        1 |
      | MeasurementNode_5_16      | MaxWPhs                  |
      | MeasurementValue_5_16     |                       52 |
      | MeasurementQualifier_5_16 |                        0 |
      | MeasurementTime_5_16      | 2017-02-01T12:02:00.000Z |
