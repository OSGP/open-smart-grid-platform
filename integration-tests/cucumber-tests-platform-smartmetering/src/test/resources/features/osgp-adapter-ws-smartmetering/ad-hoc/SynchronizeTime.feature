# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringAdHoc @NightlyBuildOnly
Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to synchronize time on a device
  So time related data on the device will have reliable timestamps

  Scenario Outline: Retrieve SynchronizeTime result from a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When receiving a get synchronize time request
      | DeviceIdentification | <deviceIdentification> |
    Then the date and time is synchronized on the device
      | DeviceIdentification | <deviceIdentification> |
    And the dlms device with identification "<deviceIdentification>" exists with properties
      | DlmsDeviceTimezone | Europe/Amsterdam |
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 2.2     |
      | TEST1024000000001    | DSMR     | 4.2.2   |
      | TEST1031000000001    | SMR      | 4.3     |
      | TEST1027000000001    | SMR      | 5.0.0   |
      | TEST1028000000001    | SMR      | 5.1     |
      | TEST1029000000001    | SMR      | 5.2     |
      | TEST1030000000001    | SMR      | 5.5     |
