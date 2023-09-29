# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0
@blahgxf
@SmartMetering @Platform @SmartMeteringAdHoc @NightlyBuildOnly
Feature: SmartMetering schedule test alarms
  As a grid operator
  I want to be able to set a test alarm on a device
  So scheduled alarms will be sent

  Scenario Outline: Set test alarm PARTIAL_POWER_OUTAGE on a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification | TEST1027000000001   |
      | DeviceType           | SMART_METER_E       |
      | Protocol             | <protocol>          |
      | ProtocolVersion      | <version>           |
      | Port                 | <port>              |
    When receiving a test alarm scheduler request
      | DeviceIdentification | TEST1027000000001    |
      | TestAlarmType        | PARTIAL_POWER_OUTAGE |
      | Time                 | 2088-01-01T00:00:00Z |
    Then a response is received
      | DeviceIdentification | TEST1027000000001    |

    Examples:
      | protocol | version | port |
      | SMR      | 5.1     |      |
      | SMR      | 5.2     |      |
      | SMR      | 5.5     | 1030 |


  Scenario Outline: Set test alarm LAST_GASP on a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification | TEST1030000000001    |
      | DeviceType           | SMART_METER_E        |
      | Protocol             | <protocol>          |
      | ProtocolVersion      | <version>           |
      | Port                 | <port>              |
    When receiving a test alarm scheduler request
      | DeviceIdentification | TEST1030000000001    |
      | TestAlarmType        | LAST_GASP            |
      | Time                 | 2088-01-01T00:00:00Z |
    Then a response is received
      | DeviceIdentification | TEST1030000000001    |

    Examples:
      | protocol | version | port |
      | SMR      | 5.5     | 1030 |
