# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringManagement @NightlyBuildOnly
Feature: SmartMetering Management - Clear M-Bus alarm status on all channels of a E meter
  As a grid operator
  I want to be able to clear the M-Bus alarm status on all channels of a E meter

  Scenario Outline: Clear M-Bus alarm status on all channels of a E-meter for protocol <protocol> <version> and check result OK
    Given a dlms device
      | DeviceIdentification | TEST1028000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | <protocol>        |
      | ProtocolVersion      | <version>         |
      | Port                 |              1028 |
    When the clear M-Bus status on all channels request is received
      | DeviceIdentification | TEST1028000000001 |
    Then the clear M-Bus status on all channels response is "OK"

    Examples:
    | protocol | version |
    | SMR      | 5.1     |
    | SMR      | 5.2     |
    | SMR      | 5.5     |


  Scenario Outline: Clear M-Bus alarm status on all channels of a E-meter for protocol <protocol> <version> and check result NOT_OK
    Given a dlms device
      | DeviceIdentification | TEST1028000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | <protocol>        |
      | ProtocolVersion      | <version>         |
      | Port                 |              1028 |
    When the clear M-Bus status on all channels request is received
      | DeviceIdentification | TEST1028000000001 |
    Then check notification
      | result  | NOT_OK     |
      | message | <message>  |

    Examples:
      | protocol | version | message |
      | DSMR     | 2.2     | Did not find READ_MBUS_STATUS object for device 28000000001 for channel 1 |
      | DSMR     | 4.2.2   | Did not find READ_MBUS_STATUS object for device 28000000001 for channel 1 |
      | SMR      | 4.3     | Did not find READ_MBUS_STATUS object for device 28000000001 for channel 1 |
      | SMR      | 5.0.0   | Did not find CLEAR_MBUS_STATUS object for device 28000000001 for channel 1 |
