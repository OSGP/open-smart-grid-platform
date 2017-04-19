Feature: Get Data Alternative Scenarios
  In order to make OSGP more robust
  As an OSGP client
  I want to properly handle alternative or wrong configurations and get data failures

  @Iec61850MockServerPampus
  Scenario: Request PV1 Health for a non-existing logical device
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS |
    When a get data request is received
      | DeviceIdentification      | RTU-PAMPUS |
      | NumberOfSystems           |          1 |
      | SystemId_1                |          4 |
      | SystemType_1              | PV         |
      | NumberOfMeasurements_1    |          1 |
      | MeasurementFilterNode_1_1 | Health     |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU-PAMPUS                   |
      | Component            | PROTOCOL_IEC61850            |
      | Message              | fcmodelNode must not be null |

  @Iec61850MockServerWago
  Scenario: Request PV1 Health not default servername
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-WAGO    |
      | ServerName           | WAGO123     |
      | IcdFilename          | WAGO123.icd |
      | Port                 |       60104 |
    And the WAGO RTU returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU-WAGO |
      | NumberOfSystems           |        1 |
      | SystemId_1                |        1 |
      | SystemType_1              | PV       |
      | NumberOfMeasurements_1    |        1 |
      | MeasurementFilterNode_1_1 | Health   |
    Then the get data response should be returned
      | DeviceIdentification     | RTU-WAGO |
      | Result                   | OK       |
      | NumberOfSystems          |        1 |
      | SystemId_1               |        1 |
      | SystemType_1             | PV       |
      | NumberOfMeasurements_1   |        1 |
      | MeasurementId_1_1        |        1 |
      | MeasurementNode_1_1      | Health   |
      | MeasurementQualifier_1_1 |     1024 |
      | MeasurementValue_1_1     |      3.0 |

  @Iec61850MockServerPampus
  Scenario: Request PV1 Health wrong servername
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS |
      | ServerName           | Wrong      |
    And an rtu simulator returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU-PAMPUS |
      | NumberOfSystems           |          1 |
      | SystemId_1                |          1 |
      | SystemType_1              | PV         |
      | NumberOfMeasurements_1    |          1 |
      | MeasurementFilterNode_1_1 | Health     |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU-PAMPUS                   |
      | Component            | PROTOCOL_IEC61850            |
      | Message              | fcmodelNode must not be null |

  @Iec61850MockServerPampus
  Scenario: Request PV1 Health wrong icd file
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS |
      | IcdFilename          | Wrong.icd  |
    And an rtu simulator returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU-PAMPUS |
      | NumberOfSystems           |          1 |
      | SystemId_1                |          1 |
      | SystemType_1              | PV         |
      | NumberOfMeasurements_1    |          1 |
      | MeasurementFilterNode_1_1 | Health     |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU-PAMPUS                   |
      | Component            | PROTOCOL_IEC61850            |
      | Message              | fcmodelNode must not be null |
