# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Get Actual Meter Reads
  As a grid operator
  I want to be able to get the actual meter reads from a device
  So I can see them when investigating some issue

  Scenario Outline: Get the actual meter reads from a device (<protocol> <protocolversion>)
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <protocolversion>      |
    When the get actual meter reads request is received
      | DeviceIdentification | <deviceIdentification> |
    Then the actual meter reads result should be returned
      | DeviceIdentification | <deviceIdentification> |

    Examples:
      | deviceIdentification | protocol | protocolversion |
      | KTEST10260000001     | DSMR     | 2.2             |
      | ZTEST10260000001     | DSMR     | 2.2             |
      | TEST1024000000001    | DSMR     | 4.2.2           |
      | TEST1031000000001    | SMR      | 4.3             |
      | TEST1027000000001    | SMR      | 5.0.0           |
      | TEST1028000000001    | SMR      | 5.1             |
      | TEST1029000000001    | SMR      | 5.2             |
      | TEST1030000000001    | SMR      | 5.5             |

  Scenario Outline: Get the actual meter reads from a gas device (<protocol> <protocolversion>)
    Given a dlms device
      | DeviceIdentification | <deviceIdentificationGateway> |
      | DeviceType           | SMART_METER_E                 |
      | Protocol             | <protocol>                    |
      | ProtocolVersion      | <protocolversion>             |
    And a dlms device
      | DeviceIdentification        | <deviceIdentification>        |
      | DeviceType                  | SMART_METER_G                 |
      | GatewayDeviceIdentification | <deviceIdentificationGateway> |
      | Channel                     | 1                             |
    When the get actual meter reads gas request is received
      | DeviceIdentification | <deviceIdentification>  |
    Then the actual meter reads gas result should be returned
      | DeviceIdentification | <deviceIdentification>  |

    Examples:
      | deviceIdentificationGateway | deviceIdentification | protocol | protocolversion |
      | TEST102600000001            | 2TEST102600000001    | DSMR     | 2.2             |
      | TEST1024000000001           | TESTG102400000001    | DSMR     | 4.2.2           |
      | TEST1031000000001           | TESTG103100000001    | SMR      | 4.3             |
      | TEST1027000000001           | TESTG102700000001    | SMR      | 5.0.0           |
      | TEST1028000000001           | TESTG102800000001    | SMR      | 5.1             |
      | TEST1029000000001           | TESTG102900000001    | SMR      | 5.2             |
      | TEST1030000000001           | TESTG103000000001    | SMR      | 5.5             |

  @NightlyBuildOnly
  Scenario: Do not refuse an operation with an inactive device
    Given a dlms device
      | DeviceIdentification  | E9998000014123414 |
      | DeviceType            | SMART_METER_E     |
      | DeviceLifecycleStatus | NEW_IN_INVENTORY  |
    When the get actual meter reads request is received
      | DeviceIdentification | E9998000014123414 |
    Then the actual meter reads result should be returned
      | DeviceIdentification | E9998000014123414 |
