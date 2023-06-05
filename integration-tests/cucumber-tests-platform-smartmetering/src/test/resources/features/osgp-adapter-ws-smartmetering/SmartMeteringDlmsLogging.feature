# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @NightlyBuildOnly
Feature: SmartMetering DLMS logging
  As a grid operator
  I want the platform to collect detailed DLMS communication logs for a device in debug mode
  In order to ...

  Scenario: DLMS device log information is collected for a device in debug mode.
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | InDebugMode          | true              |
    When the get administrative status request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the administrative status should be returned
      | DeviceIdentification | TEST1024000000001 |
    And the get administrative status communication for device "TEST1024000000001" should be in the device_log_item table

  Scenario: DLMS device log information is not collected for a device not in debug mode.
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | InDebugMode          | false             |
    When the get administrative status request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the administrative status should be returned
      | DeviceIdentification | TEST1024000000001 |
    And the get administrative status communication for device "TEST1024000000001" should not be in the device_log_item table
