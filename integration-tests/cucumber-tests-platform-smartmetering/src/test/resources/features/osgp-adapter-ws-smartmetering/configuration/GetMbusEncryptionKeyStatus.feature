# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
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
    Then the get M-Bus encryption key status request should return an encryption key status

  Scenario: Get M-Bus encryption key status from decoupled M-Bus device
    Given a dlms device
      | DeviceIdentification | TESTG102400000001 |
      | DeviceType           | SMART_METER_G     |
    When a get M-Bus encryption key status request is received
      | DeviceIdentification | TESTG102400000001 |
    Then the get M-Bus encryption key status request should return an exception
    And a SOAP fault should have been returned
      | Code         |                                                                220 |
      | Component    | DOMAIN_SMART_METERING                                              |
      | Message      | GATEWAY_DEVICE_NOT_SET_FOR_MBUS_DEVICE                             |
      | InnerMessage | Meter for gas reads should have an energy meter as gateway device. |
