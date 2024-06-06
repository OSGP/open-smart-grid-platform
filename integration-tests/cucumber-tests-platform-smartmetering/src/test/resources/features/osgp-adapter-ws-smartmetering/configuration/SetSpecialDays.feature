# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Special Days
  As a grid operator
  I want to be able to set special days on a <protocol> <version> device
  So correct tarrifs are used for billing

  Scenario Outline: Set special days on a device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the set special days request is received
      | DeviceIdentification | <deviceIdentification> |
    Then the special days should be set on the device
      | DeviceIdentification | <deviceIdentification> |
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 2.2     |
      | TEST1024000000001    | DSMR     | 4.2.2   |
      | TEST1031000000001    | SMR      | 4.3     |
      | TEST1027000000001    | SMR      | 5.0.0   |
      | TEST1028000000001    | SMR      | 5.1     |
      | TEST1029000000001    | SMR      | 5.2     |
      | TEST1030000000001    | SMR      | 5.5     |