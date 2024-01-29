# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - GetActualMeterReads
  As a grid operator 
  I want to be able to get actual meter reads from a meter via a bundle request

  @DSMR22
  Scenario Outline: Get actual meter reads of a device (<protocol> <protocolversion>) in a bundle request
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <protocolversion>      |
      | Port                 | <port>                 |
      | Lls1active           | <lls1active>           |
      | Hls5active           | <hls5active>           |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get actual meter reads action
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads response

    Examples:
      | deviceIdentification | protocol | protocolversion | port |lls1active | hls5active |
      | TEST1024000000001    | DSMR     | 4.2.2           |      | false     | true       |
      | KTEST10260000001     | DSMR     | 2.2             | 1026 | true      | false      |
      | ZTEST10260000001     | DSMR     | 2.2             | 1026 | true      | false      |

  @DSMR22
  Scenario Outline: Get actual meter reads gas of a device (<protocol> <protocolversion>) in a bundle request
    Given a dlms device
      | DeviceIdentification | <deviceIdentificationGateway> |
      | DeviceType           | SMART_METER_E                 |
      | Protocol             | <protocol>                    |
      | ProtocolVersion      | <protocolversion>             |
      | Port                 | <port>                        |
      | Lls1active           | <lls1active>                  |
      | Hls5active           | <hls5active>                  |
    And a dlms device
      | DeviceIdentification        | <deviceIdentification>        |
      | DeviceType                  | SMART_METER_G                 |
      | GatewayDeviceIdentification | <deviceIdentificationGateway> |
      | Channel                     | 1                             |
    And a bundle request
      | DeviceIdentification | <deviceIdentificationGateway> |
    And the bundle request contains a get actual meter reads gas action
      | DeviceIdentification | <deviceIdentification> |
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads gas response

    Examples:
      | deviceIdentificationGateway | deviceIdentification | protocol | protocolversion | port | lls1active | hls5active |
      | TEST1024000000001           | TESTG102400000001    | DSMR     | 4.2.2           |      | false      | true       |
      # identification for device with protocol DSMR 2.2 has only 16 positions (E-meter) or start with a number (G-meter)
      | TEST102600000001            | 2TEST102600000001    | DSMR     | 2.2             | 1026 | true       | false      |

  Scenario: Get actual meter reads of E and G of a device in a bundle request
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get actual meter reads action
    And the bundle request contains a get actual meter reads gas action
      | DeviceIdentification | TESTG102400000001 |
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads response
    Then the bundle response should contain a get actual meter reads gas response

  Scenario: Invalid g meter configuration
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get actual meter reads action
    And the bundle request contains a get actual meter reads gas action
      | DeviceIdentification | TESTG102400000001 |
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads response
    Then the bundle response should contain a fault response
      | Message      | VALIDATION_ERROR                                      |
      | InnerMessage | Meter for gas reads should have a channel configured. |

  Scenario: Get actual meter reads of a G device without a gateway device
    Given a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | Channel                     |                 1 |
    And a bundle request
      | DeviceIdentification | TESTG102400000001 |
    And the bundle request contains a get actual meter reads gas action
      | DeviceIdentification | TESTG102400000001 |
    When the bundle request is received
    When the bundle request generating an error is received
    Then a SOAP fault should have been returned
      | Code         |                                         401 |
      | Message      | VALIDATION_ERROR                            |
      | Component    | DOMAIN_SMART_METERING                       |
      | InnerMessage | Bundle request is not allowed for gas meter (possible cause: gateway not defined for gas meter) |

  Scenario: Get actual meter reads of a G device without a gateway device and bundle device is E meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | Channel                     |                 1 |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get actual meter reads gas action
      | DeviceIdentification | TESTG102400000001 |
    When the bundle request is received
    Then the bundle response should contain a fault response
      | Message      | VALIDATION_ERROR                                                   |
      | InnerMessage | Meter for gas reads should have an energy meter as gateway device. |
