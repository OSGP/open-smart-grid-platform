@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Get Actual Meter Reads
  As a grid operator
  I want to be able to get the actual meter reads from a device
  So I can see them when investigating some issue

  Background:
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |

  Scenario: Get the actual meter reads from a device
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the actual meter reads result should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Get the actual meter reads from a gas device
    When the get actual meter reads gas request is received
      | DeviceIdentification | TESTG102400000001 |
    Then the actual meter reads gas result should be returned
      | DeviceIdentification | TESTG102400000001 |

  Scenario: Do not refuse an operation with an inactive device
    Given a dlms device
      | DeviceIdentification  | E9998000014123414 |
      | DeviceType            | SMART_METER_E     |
      | DeviceLifecycleStatus | NEW_IN_INVENTORY  |
    When the get actual meter reads request is received
      | DeviceIdentification | E9998000014123414 |
    Then the actual meter reads result should be returned
      | DeviceIdentification | E9998000014123414 |
