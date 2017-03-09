@Iec61850MockServer
Feature: MicroGrids Get PhotoVoltaic System Data
  In order to be able to know data of a photovoltaic system with a remote terminal unit
  As an OSGP client
  I want to get PV data from an RTU

  Scenario: Request PV1 Health
    Given an rtu iec61850 device
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

  Scenario: Request PV1 Health for a non-existing logical device
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10001 |
    When a get data request is received
      | DeviceIdentification      | RTU10001 |
      | NumberOfSystems           |        1 |
      | SystemId_1                |        4 |
      | SystemType_1              | PV       |
      | NumberOfMeasurements_1    |        1 |
      | MeasurementFilterNode_1_1 | Health   |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU10001                     |
      | Component            | PROTOCOL_IEC61850            |
      | Message              | fcmodelNode must not be null |

  #This test cannot run on the build server together with the scenario's in 
  #the GetDataHeatPump and GetDataBoiler features, due to an issue with ports.
  #Therefore, this scenario is skipped for now. 
  @Skip
  Scenario: Request PV1 Health not default servername
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10002    |
      | ServerName           | WAGO123     |
      | IcdFilename          | WAGO123.icd |
      | Port                 |       62102 |
    And an rtu simulator started with
      | ServerName  | WAGO123     |
      | IcdFilename | WAGO123.icd |
      | Port        |       62102 |
    And an rtu simulator returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU10002 |
      | NumberOfSystems           |        1 |
      | SystemId_1                |        1 |
      | SystemType_1              | PV       |
      | NumberOfMeasurements_1    |        1 |
      | MeasurementFilterNode_1_1 | Health   |
    Then the get data response should be returned
      | DeviceIdentification     | RTU10002 |
      | Result                   | OK       |
      | NumberOfSystems          |        1 |
      | SystemId_1               |        1 |
      | SystemType_1             | PV       |
      | NumberOfMeasurements_1   |        1 |
      | MeasurementId_1_1        |        1 |
      | MeasurementNode_1_1      | Health   |
      | MeasurementQualifier_1_1 |     1024 |
      | MeasurementValue_1_1     |      3.0 |

  #This test cannot run on the build server together with the scenario's in 
  #the GetDataHeatPump and GetDataBoiler features, due to an issue with ports.
  #Therefore, this scenario is skipped for now. 
  @Skip
  Scenario: Request PV1 Health wrong servername
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10003  |
      | ServerName           | Wrong     |
      | IcdFilename          | Wrong.icd |
      | Port                 |     63102 |
    And an rtu simulator started with
      | ServerName  | WAGO123     |
      | IcdFilename | WAGO123.icd |
      | Port        |       63102 |
    And an rtu simulator returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU10003 |
      | NumberOfSystems           |        1 |
      | SystemId_1                |        1 |
      | SystemType_1              | PV       |
      | NumberOfMeasurements_1    |        1 |
      | MeasurementFilterNode_1_1 | Health   |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU10003                     |
      | Component            | PROTOCOL_IEC61850            |
      | Message              | fcmodelNode must not be null |

  @Skip
  Scenario: Request PV1 Health wrong icd file
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10004  |
      | ServerName           | WAGO123   |
      | IcdFilename          | Wrong.icd |
      | Port                 |     64102 |
    And an rtu simulator started with
      | ServerName  | Wrong     |
      | IcdFilename | Wrong.icd |
      | Port        |     64102 |
    And an rtu simulator returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU10004 |
      | NumberOfSystems           |        1 |
      | SystemId_1                |        1 |
      | SystemType_1              | PV       |
      | NumberOfMeasurements_1    |        1 |
      | MeasurementFilterNode_1_1 | Health   |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU10004                     |
      | Component            | PROTOCOL_IEC61850            |
      | Message              | fcmodelNode must not be null |
