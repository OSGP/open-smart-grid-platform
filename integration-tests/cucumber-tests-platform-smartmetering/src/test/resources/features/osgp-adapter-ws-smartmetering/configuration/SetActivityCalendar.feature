# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Activity Calendar
  As a grid operator
  I want to be able to set the activity calendar on a device
  In order to ensure proper tarriffication on the device

  Scenario: Use wildcards for set activity calendar
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the set activity calendar request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the activity calendar profiles are set on the device
      | DeviceIdentification | TEST1024000000001 |
