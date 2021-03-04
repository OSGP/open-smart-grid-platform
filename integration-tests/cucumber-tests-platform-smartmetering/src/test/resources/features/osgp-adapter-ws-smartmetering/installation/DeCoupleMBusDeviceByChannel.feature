@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering Installation - De Couple M-Bus Device By Channel
  As a grid operator
  I want to be able to de couple an M-Bus device by channel to a smart meter

  Scenario: DeCouple device from E-meter by channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 9        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 12514    |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the DeCouple MBus Device By Channel response is "OK" for device "TESTG102400000001"
    And the G-meter "TESTG102400000001" is DeCoupled from device "TEST1024000000001"
    And the channel of device "TESTG102400000001" is cleared

  Scenario: DeCouple device from E-meter, but no G-meter on channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TESTG102400000001 |
      | DeviceType           | SMART_METER_G     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 9        |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        | 12514    |
      | 8 | unsigned             | 66       |
      | 9 | unsigned             | 3        |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the DeCouple MBus Device By Channel response is "OK" for device "NULL"
    And the G-meter "TESTG102400000001" is DeCoupled from device "TEST1024000000001"
    And the channel of device "TESTG102400000001" is cleared

  Scenario: DeCouple G-meter from unknown E-meter by channel
    Given a dlms device
      | DeviceIdentification | TESTG102400000001 |
      | DeviceType           | SMART_METER_G     |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST102400unknown" request is received for an unknown gateway
    Then a SOAP fault should have been returned
      | Code    |            201 |
      | Message | UNKNOWN_DEVICE |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  Scenario: Decouple coupled G-meter "TESTG101205673117" from E-meter "TEST1024000000001" by channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusPrimaryAddress             |                 9 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |         3 |
      | 6 | double-long-unsigned | 302343985 |
      | 7 | long-unsigned        |     12514 |
      | 8 | unsigned             |        66 |
      | 9 | unsigned             |         3 |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the DeCouple MBus Device By Channel response is "OK" for device "TESTG101205673117"
    And the mbus device "TESTG101205673117" is not coupled to the device "TEST1024000000001"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |

  # NOTE: The database MbusIdentificationNumber: 12056731 corresponds with the device attributeID 6: 302343985
  # and likewise the database MbusManufacturerIdentification: LGB corresponds with the device attributeID 7: 12514
  Scenario: Decouple decoupled G-meter "TESTG101205673117" from E-meter "TEST1024000000001" by channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusPrimaryAddress             |                 9 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the DeCouple MBus Device By Channel response is "OK" for device "NULL"
    And the mbus device "TESTG101205673117" is not coupled to the device "TEST1024000000001"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
