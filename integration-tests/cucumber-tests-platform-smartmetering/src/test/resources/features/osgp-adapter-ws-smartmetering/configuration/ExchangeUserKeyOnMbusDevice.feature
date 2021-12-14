@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Exchange User Key on M-Bus Device
  As a grid operator
  I want to be able to exchange the user key on an M-Bus device coupled on a device
  In order to setup secure communications between the M-Bus device and the host

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          24000000 |
      | MbusManufacturerIdentification | LGB               |
      | MbusUserKey                    | MBUS_USER_KEY     |

  # This test runs mostly OK in isolation. However, when run with other tests it fails.
  # Somehow the M-Bus User key is stored in the database, but is not seen in the device
  # as it is inspected in Then-step: "a valid m-bus user key is stored".
  @Skip
  Scenario: Exchange user key on a gas device with no existing user key
    Given a dlms device
      | DeviceIdentification | TEST2560000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102411111111 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST2560000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          24111111 |
      | MbusManufacturerIdentification | LGB               |
    When the exchange user key request is received
      | DeviceIdentification | TESTG102411111111 |
    Then a valid m-bus user key is stored
      | DeviceIdentification | TESTG102411111111 |

  Scenario: Exchange user key on a gas device with existing user key
    When the exchange user key request is received
      | DeviceIdentification | TESTG102400000001 |
    Then the exchange user key response should be returned
      | DeviceIdentification | TESTG102400000001 |
      | Result               | OK                |
    And a valid m-bus user key is stored
      | DeviceIdentification | TESTG102400000001 |

  Scenario: Exchange user key on an m-bus device identified by channel
    Given a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             | 1        |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    When the set m-bus user key by channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 2 |
    Then the set m-bus user key by channel response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And a valid m-bus user key is stored
      | DeviceIdentification | TESTG101205673117 |
