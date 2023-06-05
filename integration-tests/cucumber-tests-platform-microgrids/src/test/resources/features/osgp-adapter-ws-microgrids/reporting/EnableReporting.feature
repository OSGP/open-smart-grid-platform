# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Microgrids @Platform @Iec61850MockServer @Iec61850MockServerPampus
Feature: Microgrids Enable Reporting
  In order to be able to receive data from a RTU
  As an OSGP client
  I want to enable all reporting on the RTU when a connection is established

# NOTE: Different device identifications are used for each scenario
#       in order to make sure a new connection is established
#       (as connections are cached in the protocol adapter)

  Scenario: Connect with enabling all reports
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10011 |
      | Port                 |    62102 |
      | EnableAllReports     | true     |
    And the Pampus RTU returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    And all reports are disabled on the rtu
    When a get data request is received
      | DeviceIdentification      | RTU10011 |
      | NumberOfSystems           |        1 |
      | SystemId_1                |        1 |
      | SystemType_1              | PV       |
      | NumberOfMeasurements_1    |        1 |
      | MeasurementFilterNode_1_1 | Health   |
    Then the get data response should be returned
      | DeviceIdentification     | RTU10011 |
      | Result                   | OK       |
      | NumberOfSystems          |        1 |
      | SystemId_1               |        1 |
      | SystemType_1             | PV       |
      | NumberOfMeasurements_1   |        1 |
      | MeasurementId_1_1        |        1 |
      | MeasurementNode_1_1      | Health   |
      | MeasurementQualifier_1_1 |     1024 |
      | MeasurementValue_1_1     |      3.0 |
    And all reports should be enabled

  Scenario: Connect without enabling all reports
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10010 |
      | Port                 |    62102 |
      | EnableAllReports     | false    |
    And the Pampus RTU returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    And all reports are disabled on the rtu
    When a get data request is received
      | DeviceIdentification      | RTU10010 |
      | NumberOfSystems           |        1 |
      | SystemId_1                |        1 |
      | SystemType_1              | PV       |
      | NumberOfMeasurements_1    |        1 |
      | MeasurementFilterNode_1_1 | Health   |
    Then the get data response should be returned
      | DeviceIdentification     | RTU10010 |
      | Result                   | OK       |
      | NumberOfSystems          |        1 |
      | SystemId_1               |        1 |
      | SystemType_1             | PV       |
      | NumberOfMeasurements_1   |        1 |
      | MeasurementId_1_1        |        1 |
      | MeasurementNode_1_1      | Health   |
      | MeasurementQualifier_1_1 |     1024 |
      | MeasurementValue_1_1     |      3.0 |
    And all reports should not be enabled
