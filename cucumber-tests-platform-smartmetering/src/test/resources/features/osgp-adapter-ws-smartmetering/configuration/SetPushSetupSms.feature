@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering Configuration - Set Push Setup SMS
  As a grid operator
  I want to be able to set the Push setup SMS on a device
  So the device will push its related messages to the correct endpoint

  Scenario: Set push setup sms on a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the set PushSetupSms request is received
      | DeviceIdentification | TEST1024000000001 |
      | Hostname             | localhost         |
      | Port                 |              9598 |
    Then the PushSetupSms should be set on the device
      | DeviceIdentification | TEST1024000000001 |
