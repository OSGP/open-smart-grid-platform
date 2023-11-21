# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Activity Calendar
  As a grid operator
  I want to be able to set the activity calendar on a device
  In order to ensure proper tarriffication on the device

  Scenario Outline: Use wildcards for set activity calendar
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the set activity calendar request is received
      | DeviceIdentification | <deviceIdentification> |
    Then the activity calendar profiles are set on the device
      | DeviceIdentification | <deviceIdentification> |

    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1024000000002     | DSMR     | 2.2     |
      | TEST1024000000002     | DSMR     | 4.2.2   |
      | TEST1031000000002     | SMR      | 4.3     |
      | TEST1027000000002     | SMR      | 5.0.0   |
      | TEST1028000000002     | SMR      | 5.1     |
      | TEST1028000000002     | SMR      | 5.2     |
      | TEST1028000000002     | SMR      | 5.5     |