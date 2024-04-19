# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly @ThdConfiguration
Feature: SmartMetering Configuration - Set THD Configuration
  As a grid operator
  I want to be able to set the THD configuration on a device
  So the device will able to detect THD with different configurations

  Scenario Outline: Set THD configuration on a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      |              <version> |
    When the set ThdConfiguration request is received
      | DeviceIdentification            | <deviceIdentification> |
      | Thd Min Duration Normal To Over |                   1200 |
      | Thd Min Duration Over To Normal |                   1080 |
      | Thd Time Threshold              |                  96000 |
      | Thd Value Hysteresis            |                      5 |
      | Thd Value Threshold             |                     50 |
    Then the ThdConfiguration <shouldBeSetOrNot> on the device
      | DeviceIdentification | <deviceIdentification> |

    Examples:
      | deviceIdentification  | protocol | version | shouldBeSetOrNot  |
      | TEST1029000000002     | SMR      | 5.2     | should be set     |
      | TEST1030000000002     | SMR      | 5.5     | should be set     |
      | TEST1024000000002     | DSMR     | 2.2     | should not be set |
      | TEST1024000000002     | DSMR     | 4.2.2   | should not be set |
      | TEST1031000000002     | SMR      | 4.3     | should not be set |
      | TEST1027000000002     | SMR      | 5.0.0   | should not be set |
      | TEST1028000000002     | SMR      | 5.1     | should not be set |