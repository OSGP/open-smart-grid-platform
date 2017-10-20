Feature: Microgrids Get Data Alternative Scenarios
  In order to make OSGP more robust
  As an OSGP client
  I want to properly handle alternative or wrong configurations and get data failures

  @Iec61850MockServerPampus
  Scenario: Request PV1 Health for a non-existing logical device
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS |
      | Port                 |      62102 |
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
      | Message              | FcModelNode with objectReference WAGO61850ServerPV4/LLN0.Health does not exist |

  @Iec61850MockServerWago
  Scenario: Request PV1 Health not default servername
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-WAGO    |
      | ServerName           | WAGO123     |
      | IcdFilename          | WAGO123.icd |
      | Port                 |       62105 |
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
      | DeviceIdentification | RTU-PAMPUS-WAGO |
      | ServerName           | WAGO            |
      | Port                 |           62102 |
    And the Pampus RTU returning
      | PV1 | LLN0.Health.stVal |        3 |
      | PV1 | LLN0.Health.q     | OLD_DATA |
    When a get data request is received
      | DeviceIdentification      | RTU-PAMPUS-WAGO |
      | NumberOfSystems           |               1 |
      | SystemId_1                |               1 |
      | SystemType_1              | PV              |
      | NumberOfMeasurements_1    |               1 |
      | MeasurementFilterNode_1_1 | Health          |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU-PAMPUS-WAGO              |
      | Component            | PROTOCOL_IEC61850            |
      | Message              | FcModelNode with objectReference WAGOPV1/LLN0.Health does not exist |

  # Scenario is skipped, as it is not working yet, device simulator is closing connection...
  # The actual behavior of a real device is yet to be tested...
  @Skip @Iec61850MockServerPampus
  Scenario: Request data from Boiler logical device present in ICD file, but not in RTU
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS             |
      | IcdFilename          | MarkerWadden_0_1_1.icd |
      | Port                 |                  62102 |
    When a get data request is received
      | DeviceIdentification      | RTU-PAMPUS |
      | NumberOfSystems           |          1 |
      | SystemId_1                |          1 |
      | SystemType_1              | BOILER     |
      | NumberOfMeasurements_1    |          1 |
      | MeasurementFilterNode_1_1 | Health     |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU-PAMPUS        |
      | Component            | PROTOCOL_IEC61850 |
      | Message              | ???               |

  @Iec61850MockServerPampus
  Scenario: Request data from CHP logical device present in RTU, but not in ICD file
    Given an rtu iec61850 device
      | DeviceIdentification | RTU-PAMPUS-CHP         |
      | IcdFilename          | MarkerWadden_0_1_1.icd |
      | Port                 |                  62102 |
    When a get data request is received
      | DeviceIdentification      | RTU-PAMPUS-CHP |
      | NumberOfSystems           |              1 |
      | SystemId_1                |              1 |
      | SystemType_1              | CHP            |
      | NumberOfMeasurements_1    |              1 |
      | MeasurementFilterNode_1_1 | Health         |
    Then a SOAP fault should be returned
      | DeviceIdentification | RTU-PAMPUS-CHP               |
      | Component            | PROTOCOL_IEC61850            |
      | Message              | FcModelNode with objectReference WAGO61850ServerCHP1/LLN0.Health does not exist |
