@SmartMetering @Platform
Feature: SmartMetering Installation - Couple M-Bus Device
  As a grid operator
  I want to be able to couple an M-Bus device to a smart meter

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  Scenario: Couple G-meter "TESTG102400000001" to E-meter "TEST1024000000001" on first channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 9        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 12514    |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    When the Couple G-meter "TESTG102400000001" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG102400000001" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "9"

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: ITG corresponds with the device attributeID 7: 9863
  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG102400000001" with missing attributes to E-meter "TEST1024000000001" on first channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | ITG               |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 9        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 9863     |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    When the Couple G-meter "TESTG102400000001" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG102400000001" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "9"

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG102400000001" to E-meter "TEST1024000000001" on second channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-2:24.1.0" and attributes
      | 5 | unsigned             | 9        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 12514    |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    When the Couple G-meter "TESTG102400000001" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG102400000001" is coupled to device "TEST1024000000001" on M-Bus channel "2" with PrimaryAddress "9"

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG102400000001" to E-meter "TEST1024000000002" while G-meter is already coupled.
    Given a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        | 1                 |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
      | MbusPrimaryAddress             | 3                 |
    When the Couple G-meter "TESTG102400000001" request is received for E-meter "TEST1024000000002"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    | 216                               |
      | Message | GIVEN_MBUS_DEVICE_ALREADY_COUPLED |
    And the M-Bus device "TESTG102400000001" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"

  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG102400000002" to E-meter "TEST1024000000001" on second channel with already coupled channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        | 1                 |
      | MbusIdentificationNumber       | 12056700          |
      | MbusManufacturerIdentification | NVT               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And a dlms device
      | DeviceIdentification           | TESTG102400000002 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-2:24.1.0" and attributes
      | 5 | unsigned             | 9        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 12514    |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    When the Couple G-meter "TESTG102400000002" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG102400000002" is coupled to device "TEST1024000000001" on M-Bus channel "2" with PrimaryAddress "9"

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  @NightlyBuildOnly
  Scenario: Couple G-meter "TESTG102400000001" to E-meter "TEST1024000000001" which is already coupled on channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        | 1                 |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
      | MbusPrimaryAddress             | 3                 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 9        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 12514    |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    When the Couple G-meter "TESTG102400000001" request is received for E-meter "TEST1024000000001"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    | 216                               |
      | Message | GIVEN_MBUS_DEVICE_ALREADY_COUPLED |
    And the M-Bus device "TESTG102400000001" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  @NightlyBuildOnly
  Scenario: Couple another G-meter to an E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |
      | MbusPrimaryAddress          | 3                 |
    And a dlms device
      | DeviceIdentification           | TESTG102400000002 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-2:24.1.0" and attributes
      | 5 | unsigned             | 9        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 12514    |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    When the Couple G-meter "TESTG102400000002" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG102400000001" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
    And the M-Bus device "TESTG102400000002" is coupled to device "TEST1024000000001" on M-Bus channel "2" with PrimaryAddress "9"

  @NightlyBuildOnly
  Scenario: Couple unknown G-meter to an E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the Couple G-meter "TESTG10240unknown" request is received for E-meter "TEST1024000000001"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    | 201            |
      | Message | UNKNOWN_DEVICE |

  @NightlyBuildOnly
  Scenario: Couple G-meter to an unknown E-meter
    Given a dlms device
      | DeviceIdentification | TESTG102400000001 |
      | DeviceType           | SMART_METER_G     |
    When the Couple G-meter "TESTG102400000001" to E-meter "TEST102400unknown" request is received for an unknown gateway
    Then a SOAP fault should have been returned
      | Code    | 201            |
      | Message | UNKNOWN_DEVICE |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  @NightlyBuildOnly
  Scenario: Couple unbound G-meter "TESTG101205673101" to E-meter "TEST1024000000001" on a channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | MbusPrimaryAddress             | 3                 |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    When the Couple G-meter "TESTG101205673101" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG101205673101" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 3         |
      | 6 | double-long-unsigned | 302343985 |
      | 7 | long-unsigned        | 12514     |
      | 8 | unsigned             | 66        |
      | 9 | unsigned             | 3         |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: ITG corresponds with the device attributeID 7: 9863
  @NightlyBuildOnly
  Scenario: Couple unbound G-meter "TESTG101205673101" without a primary address to E-meter "TEST1024000000001" on a channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | ITG               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    When the Couple G-meter "TESTG101205673101" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG101205673101" is coupled to device "TEST1024000000001" on M-Bus channel "1"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 6 | double-long-unsigned | 302343985 |
      | 7 | long-unsigned        | 9863      |
      | 8 | unsigned             | 66        |
      | 9 | unsigned             | 3         |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  @NightlyBuildOnly
  Scenario: Couple unbound G-meter "TESTG101205673101" to E-meter "TEST1024000000001" on a channel 2
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 241       |
      | 6 | double-long-unsigned | 302343974 |
      | 7 | long-unsigned        | 12514     |
      | 8 | unsigned             | 66        |
      | 9 | unsigned             | 3         |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-2:24.1.255" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | MbusPrimaryAddress             | 3                 |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    When the Couple G-meter "TESTG101205673101" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG101205673101" is coupled to device "TEST1024000000001" on M-Bus channel "2" with PrimaryAddress "3"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 241       |
      | 6 | double-long-unsigned | 302343974 |
      | 7 | long-unsigned        | 12514     |
      | 8 | unsigned             | 66        |
      | 9 | unsigned             | 3         |
    And the values for classid 72 obiscode "0-2:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 3         |
      | 6 | double-long-unsigned | 302343985 |
      | 7 | long-unsigned        | 12514     |
      | 8 | unsigned             | 66        |
      | 9 | unsigned             | 3         |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  @NightlyBuildOnly
  Scenario: Couple G-meter to an E-meter when all MBus channels are occupied
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And all mbus channels are occupied for E-meter "TEST1024000000001"
    When the Couple G-meter "TESTG102400000001" request is received for E-meter "TEST1024000000001"
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    | 217                        |
      | Message | ALL_MBUS_CHANNELS_OCCUPIED |
    And the mbus device "TESTG102400000001" is not coupled to the device "TEST1024000000001"

  @NightlyBuildOnly
  Scenario: Couple G-meter to an E-meter that is already coupled with other G-meter on channel 2
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client values for channel 2
      | MbusPrimaryAddress             | 241      |
      | MbusIdentificationNumber       | 12056726 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 2                 |
      | MbusPrimaryAddress          | 3                 |
    And device simulation of "TEST1024000000001" with M-Bus client values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | MbusPrimaryAddress             | 3                 |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    When the Couple G-meter "TESTG101205673101" request is received for E-meter "TEST1024000000001"
    Then the Couple response is "OK"
    And the M-Bus device "TESTG101205673101" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
    And the values for classid 72 obiscode "0-2:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 241       |
      | 6 | double-long-unsigned | 302343974 |
      | 7 | long-unsigned        | 12514     |
      | 8 | unsigned             | 66        |
      | 9 | unsigned             | 3         |
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 3         |
      | 6 | double-long-unsigned | 302343985 |
      | 7 | long-unsigned        | 12514     |
      | 8 | unsigned             | 66        |
      | 9 | unsigned             | 3         |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  @NightlyBuildOnly
  Scenario: Couple a connected and bound G-meter "TESTG100261510717" to E-meter "TEST1024000000001" on channel 1
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client values for channel 1
      | MbusPrimaryAddress             | 3        |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       | 12056731          |
      | MbusPrimaryAddress             | 9                 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    When the Couple M-Bus Device By Channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              | 1                 |
    Then the Couple M-Bus Device By Channel response is "OK"
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"
