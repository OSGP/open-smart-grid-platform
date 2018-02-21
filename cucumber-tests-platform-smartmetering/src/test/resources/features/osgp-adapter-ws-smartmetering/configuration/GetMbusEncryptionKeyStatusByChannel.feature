@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering - Configuration - M-Bus encryption key status by channel
  As a product owner
  I want to be able to retrieve the encryption key status from an M-Bus device using the gateway device identification and a channel
  So that I have insight into the status encryption key replacement

  Scenario: Get M-Bus encryption key status from coupled M-Bus device by using Channel id and Gateway device id
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |         9 |
      | 6 | double-long-unsigned | 302343985 |
      | 7 | long-unsigned        |     12514 |
      | 8 | unsigned             |        66 |
      | 9 | unsigned             |         3 |
    When a get M-Bus encryption key status by channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 1 |
    Then the get M-Bus encryption key status by channel response is returned
      | DeviceIdentification | TEST1024000000001     |
      | Channel              |                     1 |
      | EncryptionKeyStatus  | ENCRYPTION_KEY_IN_USE |

  Scenario: Get M-Bus encryption key status using Channel and Gateway device id, no device on that channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TESTG102400000001 |
      | DeviceType           | SMART_METER_G     |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             | 0 |
      | 6 | double-long-unsigned | 0 |
      | 7 | long-unsigned        | 0 |
      | 8 | unsigned             | 0 |
      | 9 | unsigned             | 0 |
    When a get M-Bus encryption key status by channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 1 |
    Then the get M-Bus encryption key status request should return an exception
    And a SOAP fault should have been returned
      | Code    |                        219 |
      | Message | NO_DEVICE_FOUND_ON_CHANNEL |