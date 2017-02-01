Feature: Microgrids Get CHP Data
  As an OSGP client
  I want to get CHP data from an RTU
  So this data can be used by other processes

  @Iec61850MockServer
  Scenario: Request CHP
    Given an rtu device
      | DeviceIdentification | RTU10001 |
    And an rtu simulator returning
      | HEAT_BUFFER1 | TTMP1.TmpSv.instMag.f | 20 |
    When a get data request is received
      | DeviceIdentification      | RTU10001    |
      | NumberOfSystems           |           1 |
      | SystemId_1                |           1 |
      | SystemType_1              | HEAT_BUFFER |
      | NumberOfMeasurements_1    |           1 |
      | MeasurementFilterNode_1_1 | TmpSv       |
      | MeasurementFilterId_1_1   |           1 |
    Then the get data response should be returned 
      | DeviceIdentification     | RTU10001    |
      | Result                   | OK          |
      | NumberOfSystems          |           1 |
      | SystemId_1               |           1 |
      | SystemType_1             | HEAT_BUFFER |
      | NumberOfMeasurements_1   |           1 |
      | MeasurementId_1_1        |           1 |
      | MeasurementNode_1_1      | TmpSv       |
      | MeasurementValue_1_1     |          20 |
      | MeasurementQualifier_1_1 |           0 |
