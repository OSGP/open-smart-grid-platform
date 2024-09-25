# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringInstallation @MBusDevice @DecoupleMBus
Feature: SmartMetering Installation - Decouple M-Bus Device By Channel
  As a grid operator
  I want to be able to decouple an M-Bus device by channel to a smart meter

  Scenario Outline: Decouple M-Bus Device By Channel (<ch>) on a administratively decoupled E-meter (<protocol> <version>)
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel <ch>
      | MbusPrimaryAddress             |          9 |
      | MbusIdentificationNumber       | <mbusid>   |
      | MbusManufacturerIdentification | LGB        |
      | MbusVersion                    |         66 |
      | MbusDeviceTypeIdentification   | <mbustype> |
    When the Decouple M-Bus Device By Channel "<ch>" from E-meter "<deviceIdentification>" request is received
    Then the Decouple M-Bus Device By Channel response is "OK" with responsedata
      | ResultString                   | <result_string>      |
      | MbusIdentificationNumber       | <mbusid_in_response> |
      | MbusDeviceIdentification       |                      |
    And the values for the M-Bus client for channel <ch> on device simulator "<deviceIdentification>" are
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    Examples:
      | deviceIdentification | protocol | version | mbusversion | ch | mbusid   | mbustype | mbusid_in_response                     | result_string                                                                                        |
      | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 | 12056731 |        3 | 12056731                               | Decouple Mbus Device was successful                                                                  |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version | mbusversion | ch | mbusid   | mbustype | mbusid_in_response                     | result_string                                                                                        |
      | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  2 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  3 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  4 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1024000000001    | DSMR     | 2.2     |           0 |  1 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1031000000001    | SMR      | 4.3     |           0 |  1 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1027000000001    | SMR      | 5.0.0   |           1 |  1 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1028000000001    | SMR      | 5.1     |           1 |  1 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1029000000001    | SMR      | 5.2     |           1 |  1 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1030000000001    | SMR      | 5.5     |           1 |  1 |        3 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |
      | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |        3 | A2056731 | DOUBLE_LONG_UNSIGNED Value: 2718263089 | Channel information could not be correctly interpreted. Mbus Device was successful decoupled anyway. |
    @Hydrogen2
    Examples:
      | deviceIdentification | protocol | version | mbusversion | ch | mbusid   | mbustype | mbusid_in_response                     | result_string                                                                                        |
      | TEST1030000000001    | SMR      | 5.5     |           1 |  1 |        7 | 12056731 | 12056731                               | Decouple Mbus Device was successful                                                                  |

  Scenario: Decouple M-Bus Device By Channel on a administratively decoupled E-meter with empty channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    When the Decouple M-Bus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the Decouple M-Bus Device By Channel response is "OK" with responsedata
      | ResultString                   | Decouple Mbus Device was successful |
      | MbusIdentificationNumber       | 00000000                            |
      | MbusDeviceIdentification       |                                     |
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |

  Scenario: Decouple M-Bus Device By Channel from unknown E-meter by channel
    Given a dlms device
      | DeviceIdentification | TESTG102400000001 |
      | DeviceType           | SMART_METER_G     |
    When the Decouple M-Bus Device By Channel "1" from E-meter "TEST102400unknown" request is received for an unknown gateway
    Then a SOAP fault should have been returned
      | Code    |            201 |
      | Message | UNKNOWN_DEVICE |

  Scenario Outline: Decouple M-Bus Device By Channel on a administratively coupled E-meter, same M-Bus device as in channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 9        |
      | MbusIdentificationNumber       | <mbusid> |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusPrimaryAddress             |                 9 |
      | MbusIdentificationNumber       |          <mbusid> |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the Decouple M-Bus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the Decouple M-Bus Device By Channel response is "OK" with responsedata
      | ResultString                   | <result_string>      |
      | MbusIdentificationNumber       | <mbusid_in_response> |
      | MbusDeviceIdentification       | <mbus_device>        |
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And the smart meter is decoupled from gateway device in the core database
      | DeviceIdentification           | TESTG101205673117 |
    Examples:
      | mbusid   | mbusid_in_response                     | mbus_device       | result_string                                                                                        |
      | 12056731 | 12056731                               | TESTG101205673117 | Decouple Mbus Device was successful                                                                  |
      | A2056731 | DOUBLE_LONG_UNSIGNED Value: 2718263089 |                   | Channel information could not be correctly interpreted. Mbus Device was successful decoupled anyway. |

  Scenario Outline: Decouple M-Bus Device By Channel on a administratively coupled E-meter, different from one in channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 9        |
      | MbusIdentificationNumber       | <mbusid> |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    And a dlms device
      | DeviceIdentification | TEST1024000000002 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000002 |
      | Channel                        |                 1 |
      | MbusPrimaryAddress             |                 9 |
      | MbusIdentificationNumber       |          <mbusid> |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the Decouple M-Bus Device By Channel "1" from E-meter "TEST1024000000001" request is received
    Then the Decouple M-Bus Device By Channel response is "OK" with responsedata
      | ResultString                   | <result_string>      |
      | MbusIdentificationNumber       | <mbusid_in_response> |
      | MbusDeviceIdentification       | <mbus_device>        |
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And the smart meter is not decoupled from gateway device in the core database
      | DeviceIdentification           | TESTG101205673117 |
      | GatewayDeviceIdentification    | TEST1024000000002 |
      | Channel                        |                 1 |
      | MbusPrimaryAddress             |                 9 |
    Examples:
      | mbusid   | mbusid_in_response                     | mbus_device       | result_string                                                                                        |
      | 12056731 | 12056731                               | TESTG101205673117 | Decouple Mbus Device was successful                                                                  |
      | A2056731 | DOUBLE_LONG_UNSIGNED Value: 2718263089 |                   | Channel information could not be correctly interpreted. Mbus Device was successful decoupled anyway. |
