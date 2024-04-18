# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @ThdConfiguration
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
      | Thd Min Duration Normal To Over | 1 |
      | Thd Min Duration Over To Normal | 2 |
      | Thd Time Threshold              | 3 |
      | Thd Value Hysteresis            | 4 |
      | Thd Value Threshold             | 5 |
    When the bundle request is received
    Then the bundle response should contain a set THD configuration response with values
      | Result | OK |

    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1029000000002     | SMR      | 5.2     |
      | TEST1030000000002     | SMR      | 5.5     |


  Scenario Outline: Set THD configuration is not supported on a <protocol> <version> device
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      |              <version> |
    And the bundle request contains a set THD configuration action with parameters
      | Thd Min Duration Normal To Over | 1 |
      | Thd Min Duration Over To Normal | 2 |
      | Thd Time Threshold              | 3 |
      | Thd Value Hysteresis            | 4 |
      | Thd Value Threshold             | 5 |
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | Error handling request with SetThdConfigurationCommandExecutor: No address found for THD_VALUE_THRESHOLD in protocol <protocol> <version> |

    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1024000000002     | DSMR     | 2.2     |
      | TEST1024000000002     | DSMR     | 4.2.2   |
      | TEST1031000000002     | SMR      | 4.3     |
      | TEST1027000000002     | SMR      | 5.0.0   |
      | TEST1028000000002     | SMR      | 5.1     |
