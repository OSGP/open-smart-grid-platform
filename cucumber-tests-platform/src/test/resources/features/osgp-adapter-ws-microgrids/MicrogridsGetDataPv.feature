Feature: Get PhotoVoltaic System Data
  In order to be able to know data of a photovoltaic system with a remote terminal unit
  As an OSGP client
  I want to get PV data from an RTU

  @Iec61850MockServer
  Scenario: Request PV1 Health
    Given an rtu device
      | DeviceIdentification | RTU10001 |
    And an rtu simulator returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU10001 |
      | NumberOfSystems           |        1 |
      | SystemId_1                |        1 |
      | SystemType_1              | PV       |
      | NumberOfMeasurements_1    |        1 |
      | MeasurementFilterNode_1_1 | Health   |
    Then the get data response should be returned
      | DeviceIdentification     | RTU10001 |
      | Result                   | OK       |
      | NumberOfSystems          |        1 |
      | SystemId_1               |        1 |
      | SystemType_1             | PV       |
      | NumberOfMeasurements_1   |        1 |
      | MeasurementId_1_1        |        1 |
      | MeasurementNode_1_1      | Health   |
      | MeasurementQualifier_1_1 |     1024 |
      | MeasurementValue_1_1     |      3.0 |
