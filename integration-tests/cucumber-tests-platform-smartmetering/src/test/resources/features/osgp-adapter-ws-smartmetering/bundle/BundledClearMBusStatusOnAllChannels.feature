# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - Clear M-Bus alarm status on all channels of a E meter
  As a grid operator
  I want to be able to clear the M-Bus alarm status on all channels of a E meter

  Scenario: Clear M-Bus alarm status on all channels of a E-meter
    Given a bundle request
      | DeviceIdentification | TEST1028000000001 |
    And the bundle request contains a clear M-Bus status on all channels action
    And a dlms device
      | DeviceIdentification | TEST1028000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      |               5.1 |
    When the bundle request is received
    Then the bundle response should be OK

  Scenario: Clear M-Bus alarm status of a E-meter with a non supported protocol should fail
    Given a bundle request
      | DeviceIdentification | TEST1027000000001 |
    And the bundle request contains a clear M-Bus status on all channels action
    And a dlms device
      | DeviceIdentification | TEST1027000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      |             5.0.0 |
    When the bundle request is received
    Then the bundle response should be a FaultResponse with message containing
      | Message | Error handling request with ClearMBusStatusOnAllChannelsCommandExecutor: Did not find CLEAR_MBUS_STATUS object for device 27000000001 for channel 1 |
