# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @MBusDevice
Feature: SmartMetering Bundle - Clear M-Bus alarm status on all channels of a E meter
  As a grid operator
  I want to be able to clear the M-Bus alarm status on all channels of a E meter

  Scenario Outline: Clear M-Bus alarm status on all channels of a E-meter for protocol <protocol> <version> and check result OK
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a clear M-Bus status on all channels action
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | <protocol>        |
      | ProtocolVersion      | <version> |
    When the bundle request is received
    Then the bundle response should be OK

    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1028000000001     | SMR      | 5.1     |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1029000000001     | SMR      | 5.2     |
      | TEST1030000000001     | SMR      | 5.5     |

  Scenario Outline: Clear M-Bus alarm status of a E-meter with a non supported protocol should fail
    Given a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a clear M-Bus status on all channels action
    And a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | <message> |

    Examples:
      | deviceIdentification  | protocol | version | message |
      | TEST1024000000001     | DSMR     | 4.2.2   | Did not find READ_MBUS_STATUS object for device 24000000001 for channel 1 |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification  | protocol | version | message |
      | TEST1024000000001     | DSMR     | 2.2     | Did not find READ_MBUS_STATUS object for device 24000000001 for channel 1 |
      | TEST1031000000001     | SMR      | 4.3     | Did not find READ_MBUS_STATUS object for device 31000000001 for channel 1 |
      | TEST1027000000001     | SMR      | 5.0.0   | Did not find CLEAR_MBUS_STATUS object for device 27000000001 for channel 1 |