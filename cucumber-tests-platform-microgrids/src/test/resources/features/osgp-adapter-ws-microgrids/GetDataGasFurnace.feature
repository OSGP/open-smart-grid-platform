@MicroGrids @Platform @Iec61850MockServerSchoteroog
Feature: MicroGrids Get Gas Furnace Data
  In order to be able to know data of a gas furnace with a remote terminal unit
  As an OSGP client
  I want to get Gas Furnace data from an RTU


  Scenario: GetData for Gas Furnace
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-SCHOTEROOG |
      | Port                 |      62104 |
    And the Schoteroog RTU returning
      | GAS_FURNACE1 | LLN0.Mod.stVal        |                   1 |
      | GAS_FURNACE1 | LLN0.Mod.q            | VALIDITY_GOOD       |
      | GAS_FURNACE1 | LLN0.Beh.stVal        |                   2 |
      | GAS_FURNACE1 | LLN0.Beh.q            | VALIDITY_GOOD       |
      | GAS_FURNACE1 | LLN0.Health.stVal     |                   3 |
      | GAS_FURNACE1 | LLN0.Health.q         | VALIDITY_GOOD       |
      | GAS_FURNACE1 | TTMP1.TmpSv.instMag.f |                   4 |
      | GAS_FURNACE1 | TTMP1.TmpSv.q         | VALIDITY_GOOD       |
      | GAS_FURNACE1 | TTMP1.TmpSv.t         | 2017-02-01T12:01:00 |
      | GAS_FURNACE1 | TTMP2.TmpSv.instMag.f |                   5 |
      | GAS_FURNACE1 | TTMP2.TmpSv.q         | VALIDITY_GOOD       |
      | GAS_FURNACE1 | TTMP1.TmpSv.t         | 2017-02-01T12:02:00 |
      | GAS_FURNACE1 | MFLW1.FlwRte.mag.f    |                   6 |
      | GAS_FURNACE1 | MFLW1.FlwRte.q        | VALIDITY_GOOD       |
      | GAS_FURNACE1 | MFLW2.FlwRte.mag.f    |                   7 |
      | GAS_FURNACE1 | MFLW2.FlwRte.q        | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.Alm1.stVal      | false               |
      | GAS_FURNACE1 | GGIO1.Alm1.q          | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.Alm2.stVal      | true                |
      | GAS_FURNACE1 | GGIO1.Alm2.q          | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.Alm3.stVal      | false               |
      | GAS_FURNACE1 | GGIO1.Alm3.q          | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.Alm4.stVal      | true                |
      | GAS_FURNACE1 | GGIO1.Alm4.q          | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.IntIn1.stVal    |                   8 |
      | GAS_FURNACE1 | GGIO1.IntIn1.q        | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.IntIn2.stVal    |                   9 |
      | GAS_FURNACE1 | GGIO1.IntIn2.q        | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.Wrn1.stVal      | false               |
      | GAS_FURNACE1 | GGIO1.Wrn1.q          | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.Wrn2.stVal      | true                |
      | GAS_FURNACE1 | GGIO1.Wrn2.q          | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.Wrn3.stVal      | false               |
      | GAS_FURNACE1 | GGIO1.Wrn3.q          | VALIDITY_GOOD       |
      | GAS_FURNACE1 | GGIO1.Wrn4.stVal      | true                |
      | GAS_FURNACE1 | GGIO1.Wrn4.q          | VALIDITY_GOOD       |
    When a get data request is received
      | DeviceIdentification       | RTU-SCHOTEROOG  |
      | NumberOfSystems            |           1 |
      | SystemId_1                 |           1 |
      | SystemType_1               | GAS_FURNACE |
      | NumberOfMeasurements_1     |          17 |
      | MeasurementFilterNode_1_1  | Mod         |
      | MeasurementFilterNode_1_2  | Beh         |
      | MeasurementFilterNode_1_3  | Health      |
      | MeasurementFilterNode_1_4  | TmpSv       |
      | MeasurementFilterId_1_4    |           1 |
      | MeasurementFilterNode_1_5  | TmpSv       |
      | MeasurementFilterId_1_5    |           2 |
      | MeasurementFilterNode_1_6  | FlwRte      |
      | MeasurementFilterId_1_6    |           1 |
      | MeasurementFilterNode_1_7  | FlwRte      |
      | MeasurementFilterId_1_7    |           2 |
      | MeasurementFilterNode_1_8  | Alm1        |
      | MeasurementFilterNode_1_9  | Alm2        |
      | MeasurementFilterNode_1_10 | Alm3        |
      | MeasurementFilterNode_1_11 | Alm4        |
      | MeasurementFilterNode_1_12 | IntIn1      |
      | MeasurementFilterNode_1_13 | IntIn2      |
      | MeasurementFilterNode_1_14 | Wrn1        |
      | MeasurementFilterNode_1_15 | Wrn2        |
      | MeasurementFilterNode_1_16 | Wrn3        |
      | MeasurementFilterNode_1_17 | Wrn4        |
    Then the get data response should be returned
      | DeviceIdentification      | RTU-SCHOTEROOG               |
      | Result                    | OK                       |
      | NumberOfSystems           |                        1 |
      | SystemId_1                |                        1 |
      | SystemType_1              | GAS_FURNACE              |
      | NumberOfMeasurements_1    |                       17 |
      | MeasurementId_1_1         |                        1 |
      | MeasurementNode_1_1       | Mod                      |
      | MeasurementQualifier_1_1  |                        0 |
      | MeasurementValue_1_1      |                      1.0 |
      | MeasurementId_1_2         |                        1 |
      | MeasurementNode_1_2       | Beh                      |
      | MeasurementQualifier_1_2  |                        0 |
      | MeasurementValue_1_2      |                      2.0 |
      | MeasurementId_1_3         |                        1 |
      | MeasurementNode_1_3       | Health                   |
      | MeasurementQualifier_1_3  |                        0 |
      | MeasurementValue_1_3      |                      3.0 |
      | MeasurementId_1_4         |                        1 |
      | MeasurementNode_1_4       | TmpSv                    |
      | MeasurementQualifier_1_4  |                        0 |
      | MeasurementTime_1_4       | 2017-02-01T12:01:00.000Z |
      | MeasurementValue_1_4      |                      4.0 |
      | MeasurementId_1_5         |                        2 |
      | MeasurementNode_1_5       | TmpSv                    |
      | MeasurementQualifier_1_5  |                        0 |
      | MeasurementTime_1_5       | 2017-02-01T12:02:00.000Z |
      | MeasurementValue_1_5      |                      5.0 |
      | MeasurementId_1_6         |                        1 |
      | MeasurementNode_1_6       | FlwRte                   |
      | MeasurementQualifier_1_6  |                        0 |
      | MeasurementValue_1_6      |                      6.0 |
      | MeasurementId_1_7         |                        2 |
      | MeasurementNode_1_7       | FlwRte                   |
      | MeasurementQualifier_1_7  |                        0 |
      | MeasurementValue_1_7      |                      7.0 |
      | MeasurementId_1_8         |                        1 |
      | MeasurementNode_1_8       | Alm1                     |
      | MeasurementQualifier_1_8  |                        0 |
      | MeasurementValue_1_8      |                      0.0 |
      | MeasurementId_1_9         |                        1 |
      | MeasurementNode_1_9       | Alm2                     |
      | MeasurementQualifier_1_9  |                        0 |
      | MeasurementValue_1_9      |                      1.0 |
      | MeasurementId_1_10        |                        1 |
      | MeasurementNode_1_10      | Alm3                     |
      | MeasurementQualifier_1_10 |                        0 |
      | MeasurementValue_1_10     |                      0.0 |
      | MeasurementId_1_11        |                        1 |
      | MeasurementNode_1_11      | Alm4                     |
      | MeasurementQualifier_1_11 |                        0 |
      | MeasurementValue_1_11     |                      1.0 |
      | MeasurementId_1_12        |                        1 |
      | MeasurementNode_1_12      | IntIn1                   |
      | MeasurementQualifier_1_12 |                        0 |
      | MeasurementValue_1_12     |                      8.0 |
      | MeasurementId_1_13        |                        1 |
      | MeasurementNode_1_13      | IntIn2                   |
      | MeasurementQualifier_1_13 |                        0 |
      | MeasurementValue_1_13     |                      9.0 |
      | MeasurementId_1_14        |                        1 |
      | MeasurementNode_1_14      | Wrn1                     |
      | MeasurementQualifier_1_14 |                        0 |
      | MeasurementValue_1_14     |                      0.0 |
      | MeasurementId_1_15        |                        1 |
      | MeasurementNode_1_15      | Wrn2                     |
      | MeasurementQualifier_1_15 |                        0 |
      | MeasurementValue_1_15     |                      1.0 |
      | MeasurementId_1_16        |                        1 |
      | MeasurementNode_1_16      | Wrn3                     |
      | MeasurementQualifier_1_16 |                        0 |
      | MeasurementValue_1_16     |                      0.0 |
      | MeasurementId_1_17        |                        1 |
      | MeasurementNode_1_17      | Wrn4                     |
      | MeasurementQualifier_1_17 |                        0 |
      | MeasurementValue_1_17     |                      1.0 |
