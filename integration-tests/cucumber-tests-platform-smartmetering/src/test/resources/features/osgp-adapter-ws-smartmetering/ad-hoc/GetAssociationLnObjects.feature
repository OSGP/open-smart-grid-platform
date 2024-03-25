# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringAdHoc @NightlyBuildOnly
Feature: SmartMetering AdHoc - Get Association LN Objects
  As a grid operator
  I want to be able to get the Association LN object list from a device
  So I can see which COSEM objects are supported by the device

  Scenario Outline: Retrieve the association LN objectlist from a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification     | <deviceIdentification> |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | <protocol>        |
      | ProtocolVersion          | <version>         |
    When receiving a retrieve association LN objectlist request
      | DeviceIdentification | <deviceIdentification> |
    Then the objectlist should be returned
      | DeviceIdentification | <deviceIdentification> |
    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1024000000001     | DSMR     | 2.2     |
      | TEST1024000000001     | DSMR     | 4.2.2   |
      | TEST1031000000001     | SMR      | 4.3     |
      | TEST1027000000001     | SMR      | 5.0.0   |
      | TEST1028000000001     | SMR      | 5.1     |
      | TEST1029000000001     | SMR      | 5.2     |
      | TEST1030000000001     | SMR      | 5.5     |
