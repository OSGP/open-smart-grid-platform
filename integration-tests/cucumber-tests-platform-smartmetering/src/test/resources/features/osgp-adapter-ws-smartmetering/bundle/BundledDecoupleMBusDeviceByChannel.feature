@SmartMetering @Platform
Feature: SmartMetering Bundle - Decouple M-Bus Device By Channel
  As a grid operator
  I want to be able to decouple an M-Bus device by channel to a smart meter

  Scenario: Decouple M-Bus Device By Channel on a administratively decoupled E-meter
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a Decouple M-Bus Device By Channel action
      | Channel | 1 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    When the bundle request is received
    Then the Decouple M-Bus Device By Channel bundle response is "OK" without Mbus Device
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |

  Scenario: Decouple M-Bus Device By Channel on a administratively decoupled E-meter with empty channel
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a Decouple M-Bus Device By Channel action
      | Channel | 1 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    When the bundle request is received
    Then the Decouple M-Bus Device By Channel bundle response is "OK" without Mbus Device
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |

  Scenario: Decouple M-Bus Device By Channel from unknown E-meter by channel
    Given a dlms device
      | DeviceIdentification | TESTG102400000001 |
      | DeviceType           | SMART_METER_G     |
    When the Decouple M-Bus Device By Channel "1" from E-meter "TEST102400unknown" request is received for an unknown gateway
    Then a SOAP fault should have been returned
      | Code    |            201 |
      | Message | UNKNOWN_DEVICE |

  Scenario: Decouple M-Bus Device By Channel on a administratively coupled E-meter, same G-meter as in channel
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a Decouple M-Bus Device By Channel action
      | Channel | 1 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    And a dlms device
      | DeviceIdentification           | TESTG102400000002 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusPrimaryAddress             |                 9 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the bundle request is received
    Then the Decouple M-Bus Device By Channel bundle response is "OK" with Mbus Device "TESTG102400000002"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And the smart meter is decoupled from gateway device in the core database
      | DeviceIdentification           | TESTG102400000002 |

  Scenario: Decouple M-Bus Device By Channel on a administratively coupled E-meter, different from one in channel
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a Decouple M-Bus Device By Channel action
      | Channel | 1 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |        9 |
      | 6 | double-long-unsigned | 12056731 |
      | 7 | long-unsigned        |    12514 |
      | 8 | unsigned             |       66 |
      | 9 | unsigned             |        3 |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
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
    When the bundle request is received
    Then the Decouple M-Bus Device By Channel bundle response is "OK" with Mbus Device "TESTG102400000001"
    And the values for classid 72 obiscode "0-1:24.1.0" on device simulator "TEST1024000000001" are
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    And the smart meter is not decoupled from gateway device in the core database
      | DeviceIdentification           | TESTG102400000001 |
      | GatewayDeviceIdentification    | TEST1024000000002 |
      | Channel                        |                 1 |
      | MbusPrimaryAddress             |                 9 |
