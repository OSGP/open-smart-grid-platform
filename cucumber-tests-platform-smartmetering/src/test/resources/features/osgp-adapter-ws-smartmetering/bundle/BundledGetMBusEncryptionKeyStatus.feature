@SmartMetering @Platform
Feature: SmartMetering Bundle - GetMBusEncryptionKeyStatus
  As a grid operator 
  I want to retrieve the encryption key status for an M-Bus device from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
      
  Scenario: Get encryption key status for an M-Bus device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get M-Bus encryption key status action with parameters
      | MBusDeviceIdentification | TESTG102400000001      |
    When the bundle request is received
    Then the bundle response should contain a get M-Bus encryption key status response with values
      | Result | OK |
