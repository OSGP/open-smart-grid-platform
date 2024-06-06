# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Installation - Add M-Bus device
  As a grid operator
  I want to be able to add a new M-Bus device

  Scenario: Add a new gas device
    When receiving a smartmetering add device request
      | DeviceIdentification                | TESTG101205673117                       |
      | DeviceType                          | SMART_METER_G                           |
      | CommunicationMethod                 | GPRS                                    |
      | MbusIdentificationNumber            | 12056731                                |
      | MbusManufacturerIdentification      | LGB                                     |
      | MbusVersion                         | 66                                      |
      | MbusDeviceTypeIdentification        | 3                                       |
      | MbusDefaultKey                      | MBUS_USER_KEY                           |
      | MbusUserKey                         | SECURITY_KEY_1                          |
      | MbusFirmwareUpdateAuthenticationKey | SECURITY_KEY_2                          |
      | MbusP0Key                           | SECURITY_KEY_3                          |
      | protocolName                        | DSMR                                    |
      | protocolVersion                     | 4.2.2                                   |
      | ManufacturerCode                    | Test                                    |
      | ModelCode                           | Test                                    |
    Then the add device response should be returned
      | DeviceIdentification | TESTG101205673117 |
      | Result               | OK                |
    And the dlms device with identification "TESTG101205673117" exists with device model
      | ManufacturerCode | Test |
      | ModelCode        | Test |
    And the dlms device with identification "TESTG101205673117" exists with properties
      | DlmsDeviceTimezone |   |
    And the smart meter is registered in the core database
      | DeviceIdentification           | TESTG101205673117 |
      | MbusIdentificationNumber       | 12056731          |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    | 66                |
      | MbusDeviceTypeIdentification   | 3                 |
    And the stored M-Bus Default key is not equal to the received key
    And the encrypted_secret table in the secret management database should contain "MbusUserKey" keys for device "TESTG101205673117"
      | SECURITY_KEY_1 | ACTIVE   |
    And the encrypted_secret table in the secret management database should contain "MbusFirmwareUpdateAuthenticationKey" keys for device "TESTG101205673117"
      | SECURITY_KEY_2 | ACTIVE   |
    And the encrypted_secret table in the secret management database should contain "MbusP0Key" keys for device "TESTG101205673117"
      | SECURITY_KEY_3 | ACTIVE   |
