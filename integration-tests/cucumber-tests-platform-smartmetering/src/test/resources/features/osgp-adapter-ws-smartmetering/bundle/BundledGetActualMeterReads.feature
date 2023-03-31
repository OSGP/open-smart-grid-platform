@SmartMetering @Platform
Feature: SmartMetering Bundle - GetActualMeterReads
  As a grid operator 
  I want to be able to get actual meter reads from a meter via a bundle request

  @DSMR22
  Scenario Outline : Get actual meter reads of a device (<protocol> <protocolversion>) in a bundle request
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <protocolversion>      |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get actual meter reads action
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads response

    Examples:
      | deviceIdentification | protocol | protocolversion |
      | TEST1024000000001    | DSMR     | 4.2.2           |
      | KTEST10220000001     | DSMR     | 2.2             |
      | ZTEST10220000001     | DSMR     | 2.2             |

  @DSMR22
  Scenario Outline: Get actual meter reads gas of a device (<protocol> <protocolversion>) in a bundle request
    Given a dlms device
      | DeviceIdentification        | <deviceIdentification> |
      | DeviceType                  | SMART_METER_G          |
      | GatewayDeviceIdentification | TEST1024000000001      |
      | Protocol                    | <protocol>             |
      | ProtocolVersion             | <protocolversion>      |
      | Channel                     |                      1 |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get actual meter reads gas action
      | DeviceIdentification | TESTG102400000001 |
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads gas response

    Examples:
      | deviceIdentification | protocol | protocolversion |
      | TESTG102400000001    | DSMR     | 4.2.2           |
      | 2TEST102400000001    | DSMR     | 2.2             |

  Scenario: Get actual meter reads of E and G of a device in a bundle request
    Given a dlms device
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

