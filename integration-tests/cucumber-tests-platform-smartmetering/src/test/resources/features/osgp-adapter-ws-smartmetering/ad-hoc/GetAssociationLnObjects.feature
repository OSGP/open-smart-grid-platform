# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringAdHoc @NightlyBuildOnly
Feature: SmartMetering AdHoc - Get Association LN Objects
  As a grid operator
  I want to be able to get the Association LN object list from a device
  So I can see which COSEM objects are supported by the device

  Scenario: Retrieve the association LN objectlist from a device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When receiving a retrieve association LN objectlist request
      | DeviceIdentification | TEST1024000000001 |
    Then the objectlist should be returned
      | DeviceIdentification | TEST1024000000001 |
