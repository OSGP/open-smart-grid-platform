# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@NightlyBuildOnly @Microgrids @Platform @Iec61850MockServerPampus
Feature: Microgrids Re-establish Connection
  As MSP
  I want to know when a connection between OSGP and RTU is lost or re-established.

  Scenario: Connection lost and reestablished
    Given an rtu iec61850 device
      | DeviceIdentification  | RTU-PAMPUS |
      | Port                  |      62102 |
      | LastCommunicationTime | yesterday  |
    When the OSGP connection is lost with the RTU device
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification     | RTU-PAMPUS |
      | Result                   | OK         |
      | NumberOfSystems          |          1 |
      | SystemId_1               |          1 |
      | SystemType_1             | RTU        |
      | NumberOfMeasurements_1   |          1 |
      | MeasurementId_1_1        |          1 |
      | MeasurementNode_1_1      | Alm1       |
      | MeasurementValue_1_1     |          1 |
      | MeasurementQualifier_1_1 |          0 |
    And I should receive a notification
    And the get data response should be returned
      | DeviceIdentification     | RTU-PAMPUS |
      | Result                   | OK         |
      | NumberOfSystems          |          1 |
      | SystemId_1               |          1 |
      | SystemType_1             | RTU        |
      | NumberOfMeasurements_1   |          1 |
      | MeasurementId_1_1        |          1 |
      | MeasurementNode_1_1      | Alm1       |
      | MeasurementValue_1_1     |          0 |
      | MeasurementQualifier_1_1 |          0 |
