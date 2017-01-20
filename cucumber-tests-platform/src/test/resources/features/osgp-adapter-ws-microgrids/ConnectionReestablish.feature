Feature: MicroGrids Re-establish Connection
  As MSP 
  I want to know when a connection between OSGP and RTU is lost or re-established.

@Iec61850MockServer @Skip
  Scenario: Connection lost and reestablished
    Given an rtu device
      | DeviceIdentification | RTU10001 |
    When the OSGP connection is lost with the RTU device
    Then I should receive a notification
    And the get data response should be returned
      | DeviceIdentification     | RTU10001    |
      | Result                   | OK          |
      | NumberOfSystems          |           1 |
      | SystemId_1               |           1 |
      | SystemType_1             | RTU |
      | NumberOfMeasurements_1   |           1 |
      | MeasurementId_1_1        |           1 |
      | MeasurementNode_1_1      | Alm1        |
      | MeasurementValue_1_1     |           1 |
      | MeasurementQualifier_1_1 |           0 |
    And I should receive a notification
    And the get data response should be returned
      | DeviceIdentification     | RTU10001    |
      | Result                   | OK          |
      | NumberOfSystems          |           1 |
      | SystemId_1               |           1 |
      | SystemType_1             | RTU |
      | NumberOfMeasurements_1   |           1 |
      | MeasurementId_1_1        |           1 |
      | MeasurementNode_1_1      | Alm1        |
      | MeasurementValue_1_1     |           0 |
      | MeasurementQualifier_1_1 |           0 |
