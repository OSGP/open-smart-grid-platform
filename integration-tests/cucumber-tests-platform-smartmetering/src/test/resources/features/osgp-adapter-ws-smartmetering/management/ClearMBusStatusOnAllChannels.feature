# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringManagement @NightlyBuildOnly
Feature: SmartMetering Management - Clear M-Bus alarm status on all channels of a E meter
  As a grid operator
  I want to be able to clear the M-Bus alarm status on all channels of a E meter

  Scenario Outline: Clear M-Bus alarm status on all channels of a E-meter for protocol <protocol> <version> and check result OK
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | <protocol>        |
      | ProtocolVersion      | <version>         |
    When the clear M-Bus status on all channels request is received
      | DeviceIdentification | <deviceIdentification> |
    Then the clear M-Bus status on all channels response is "OK"

    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1028000000001     | SMR      | 5.1     |
      | TEST1029000000001     | SMR      | 5.2     |
      | TEST1030000000001     | SMR      | 5.5     |

  @SMHE-1695
  Scenario Outline: Clear M-Bus alarm status on all channels of a E-meter for protocol <protocol> <version> and check result NOT_OK
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | <protocol>        |
      | ProtocolVersion      | <version>         |
    When the clear M-Bus status on all channels request is received
      | DeviceIdentification | <deviceIdentification> |
    Then check notification
      | result  | NOT_OK     |
      | message | <message>  |

    Examples:
      | deviceIdentification  | protocol | version | message |
      | TEST1024000000001     | DSMR     | 2.2     | Did not find READ_MBUS_STATUS object for device 24000000001 for channel 1 |
      | TEST1024000000001     | DSMR     | 4.2.2   | Did not find READ_MBUS_STATUS object for device 24000000001 for channel 1 |
      | TEST1031000000001     | SMR      | 4.3     | Did not find READ_MBUS_STATUS object for device 31000000001 for channel 1 |
      | TEST1027000000001     | SMR      | 5.0.0   | Did not find CLEAR_MBUS_STATUS object for device 27000000001 for channel 1 |
