# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform #@ThdConfiguration
Feature: SmartMetering Bundle - THD Configuation
  As a grid operator 
  I want to be able to set configuration for THD on a meter via a bundle request

  Scenario Outline: Set THD configuration on a <protocol> <version> device in a bundle request
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      |              <version> |
    And the bundle request contains a set THD configuration action with parameters
        | Thd Value Threshold             | 1 |
        | Thd Value Hysteresis            | 2 |
        | Thd Min Duration Normal To Over | 3 |
        | Thd Min Duration Over To Normal | 4 |
        | Thd Time Threshold              | 5 |
    When the bundle request is received
    Then the bundle response should contain a set THD configuration response with values
      | Result | OK |

    Examples:
      | deviceIdentification  | protocol | version |
#      | TEST1024000000002     | DSMR     | 2.2     |
#      | TEST1024000000002     | DSMR     | 4.2.2   |
#      | TEST1031000000002     | SMR      | 4.3     |
#      | TEST1027000000002     | SMR      | 5.0.0   |
#      | TEST1028000000002     | SMR      | 5.1     |
      | TEST1028000000002     | SMR      | 5.2     |
      | TEST1028000000002     | SMR      | 5.5     |

