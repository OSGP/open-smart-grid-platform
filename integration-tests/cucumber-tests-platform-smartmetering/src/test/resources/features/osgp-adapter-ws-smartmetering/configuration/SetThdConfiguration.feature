# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly @ThdConfiguration
Feature: SmartMetering Configuration - Set THD Configuration
  As a grid operator
  I want to be able to set the THD configuration on a device
  So the device will able to detect THD with different configurations

  Scenario Outline: Set THD configuration on a device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      |              <version> |
    When the set ThdConfiguration request is received
      | DeviceIdentification            | <deviceIdentification> |
      | Thd Value Threshold             |                      1 |
      | Thd Value Hysteresis            |                      2 |
      | Thd Min Duration Normal To Over |                      3 |
      | Thd Min Duration Over To Normal |                      4 |
      | Thd Time Threshold              |                      5 |
    Then the ThdConfiguration should be set on the device
      | DeviceIdentification | <deviceIdentification> |

    Examples:
      | deviceIdentification  | protocol | version |
#      | TEST1024000000002     | DSMR     | 2.2     |
#      | TEST1024000000002     | DSMR     | 4.2.2   |
#      | TEST1031000000002     | SMR      | 4.3     |
#      | TEST1027000000002     | SMR      | 5.0.0   |
#      | TEST1028000000002     | SMR      | 5.1     |
      | TEST1028000000002     | SMR      | 5.2     |
      | TEST1028000000002     | SMR      | 5.5     |