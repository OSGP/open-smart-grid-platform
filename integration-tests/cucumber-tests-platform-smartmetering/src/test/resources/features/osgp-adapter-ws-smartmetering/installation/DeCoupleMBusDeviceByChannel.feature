@SmartMetering @Platform
Feature: SmartMetering Installation - De Couple M-Bus Device By Channel
  As a grid operator
  I want to be able to de couple an M-Bus device by channel to a smart meter

  Scenario: DeCouple Mbus Device By Channel on a administratively decoupled E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the DeCouple MBus Device By Channel response is "OK" for device "NULL"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |

  Scenario: DeCouple Mbus Device By Channel on a administratively decoupled E-meter with empty channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the DeCouple MBus Device By Channel response is "OK" for device "NULL"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |

  Scenario: DeCouple Mbus Device By Channel from unknown E-meter by channel
    Given a dlms device
      | DeviceIdentification | TESTG102400000001 |
      | DeviceType           | SMART_METER_G     |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST102400unknown" request is received for an unknown gateway
    Then a SOAP fault should have been returned
      | Code    |            201 |
      | Message | UNKNOWN_DEVICE |

  Scenario: DeCouple Mbus Device By Channel on a administratively coupled E-meter, same G-meter as in channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusPrimaryAddress             |                 9 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the DeCouple MBus Device By Channel response is "OK" for device "TESTG102400000001"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And the smart meter is decoupled from gateway device in the core database
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |

  Scenario: DeCouple Mbus Device By Channel on a administratively coupled E-meter, different from one in channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000002 |
      | Channel                        |                 1 |
      | MbusPrimaryAddress             |                 9 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the DeCouple MBus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the DeCouple MBus Device By Channel response is "OK" for device "TESTG102400000001"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And the smart meter is registered in the core database
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000002 |
      | Channel                        |                 1 |
      | MbusPrimaryAddress             |                 9 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
