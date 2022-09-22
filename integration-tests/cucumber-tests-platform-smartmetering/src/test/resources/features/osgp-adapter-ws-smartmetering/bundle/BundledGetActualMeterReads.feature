@SmartMetering @Platform
Feature: SmartMetering Bundle - GetActualMeterReads
  As a grid operator 
  I want to be able to get actual meter reads from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Get actual meter reads of a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get actual meter reads action
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads response

  Scenario: Get actual meter reads gas of a device in a bundle request
    Given a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get actual meter reads gas action
      | DeviceIdentification | TESTG102400000001 |
    When the bundle request is received
    Then the bundle response should contain a get actual meter reads gas response

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

  @Validation
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

