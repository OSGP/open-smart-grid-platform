@MicroGrids @Platform @Iec61850MockServer @Iec61850MockServerPampus @TestThis
Feature: MicroGrids Enable Reporting
  In order to be able to receive data from a RTU
  As an OSGP client
  I want to enable all reporting on the RTU when a connection is established

  Scenario: Connect with enabling all reports
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10011 |
      | Port                 |    62102 |
      | EnableAllReports     | true     |
    And an rtu simulator returning
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

  #@RestartIec61850MockServerPampus
  Scenario: Connect without enabling all reports
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10010 |
      | Port                 |    62102 |
      | EnableAllReports     | false    |
    And an rtu simulator returning
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
    
    
