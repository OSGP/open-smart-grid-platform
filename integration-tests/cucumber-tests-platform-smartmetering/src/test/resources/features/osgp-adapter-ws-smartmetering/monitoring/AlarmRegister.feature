@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Alarm Register
  As a grid operator
  I want to be able to read and clear the alarm register on a device
  So I can see which alarms have occurred without depending on the alarm filter
  and I can clear the register to be able to see new alarms

  Background:
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |

  Scenario: Read the alarm register from a device
    Given device "TEST1024000000001" has some alarms registered
    When the get read alarm register request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the alarm register should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Clear alarm register
    When the Clear Alarm Code request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the Clear Alarm Code response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
