@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering - Configuration - M-Bus encryption key status
  As a product owner
  I want to be able to retrieve the encryption key status from an M-Bus device
  So that I have insight into the status encryption key replacement

  Scenario: Get M-Bus encryption key status from coupled M-Bus device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
    When a get M-Bus encryption key status request is received
      | DeviceIdentification | TESTG102400000001 |
    Then the M-Bus encryption key status should be returned
#      | Result              | OK                    |
#      | EncryptionKeyStatus | ENCRYPTION_KEY_IN_USE |
      
#Scenario: Get M-Bus encryption key status from decoupled M-Bus device
#Given a dlms device
#|DeviceIdentification | TESTG102400000001|
#|DeviceType|  SMART_METER_G|
#
#When the get M-Bus encryption key status request is received
#|DeviceIdentification  | TESTG102400000001|
#
#Then an error message "Meter for gas reads should have an energy meter as gateway device." should be returned
