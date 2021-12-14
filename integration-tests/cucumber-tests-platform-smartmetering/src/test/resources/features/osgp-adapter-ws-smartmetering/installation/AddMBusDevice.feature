@SmartMetering @Platform
Feature: SmartMetering Installation - Add M-Bus device
  As a grid operator
  I want to be able to add a new M-Bus device

  Scenario: Add a new gas device
    When receiving a smartmetering add device request
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | CommunicationMethod            | GPRS              |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
      | MbusDefaultKey                 | MBUS_USER_KEY     |
      | protocolName                   | DSMR              |
      | protocolVersion                | 4.2.2             |
      | ManufacturerCode               | Test              |
      | ModelCode                      | Test              |
    Then the add device response should be returned
      | DeviceIdentification | TESTG101205673117 |
      | Result               | OK                |
    And the dlms device with identification "TESTG101205673117" exists with device model
      | ManufacturerCode | Test |
      | ModelCode        | Test |
    And the smart meter is registered in the core database
      | DeviceIdentification           | TESTG101205673117 |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And the stored M-Bus Default key is not equal to the received key
