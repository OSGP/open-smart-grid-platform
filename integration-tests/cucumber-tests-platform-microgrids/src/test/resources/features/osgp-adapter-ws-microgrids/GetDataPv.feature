# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Microgrids @Platform @Iec61850MockServer @Iec61850MockServerPampus
Feature: Microgrids Get PhotoVoltaic System Data
  In order to be able to know data of a photovoltaic system with a remote terminal unit
  As an OSGP client
  I want to get PV data from an RTU

  Scenario: Request PV1 Health
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS |
      | Port                 |      62102 |
    And the Pampus RTU returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU-PAMPUS |
      | NumberOfSystems           |          1 |
      | SystemId_1                |          1 |
      | SystemType_1              | PV         |
      | NumberOfMeasurements_1    |          1 |
      | MeasurementFilterNode_1_1 | Health     |
    Then the get data response should be returned
      | DeviceIdentification     | RTU-PAMPUS |
      | Result                   | OK         |
      | NumberOfSystems          |          1 |
      | SystemId_1               |          1 |
      | SystemType_1             | PV         |
      | NumberOfMeasurements_1   |          1 |
      | MeasurementId_1_1        |          1 |
      | MeasurementNode_1_1      | Health     |
      | MeasurementQualifier_1_1 |       1024 |
      | MeasurementValue_1_1     |        3.0 |
