@SmartMetering @Platform
Feature: SmartMetering Installation - Add M-Bus device
  As a grid operator
  I want to be able to add a new M-Bus device

  Scenario: Add a new gas device
    When receiving a smartmetering add device request
      | DeviceIdentification           | TEST1024G00000001 |
      | DeviceType                     | SMART_METER_G     |
      | CommunicationMethod            | GPRS              |
      | MbusIdentificationNumber       |          02615107 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |               066 |
      | MbusDeviceTypeIdentification   |                03 |
      | DSMR_version                   | 4.2.2             |
    Then the add device response should be returned
      | DeviceIdentification | TEST1024G00000001 |
      | Result               | OK                |
    And the dlms device with identification "TEST1024G00000001" exists
    And the smart meter is registered in the core database
      | DeviceIdentification           | TEST1024G00000001 |
      | MbusIdentificationNumber       |          02615107 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |               066 |
      | MbusDeviceTypeIdentification   |                03 |

