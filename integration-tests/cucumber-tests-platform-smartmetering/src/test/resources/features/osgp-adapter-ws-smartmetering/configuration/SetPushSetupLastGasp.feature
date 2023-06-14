# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Push Setup LastGasp
  As a grid operator
  I want to be able to set the Push setup last gasp on a device
  So the device will push its related messages to the correct endpoint

  Scenario: Set push setup last gasp on a device
    Given a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |      
      | Port                 | 1030              |
    When the set PushSetupLastGasp request is received
      | DeviceIdentification | TEST1030000000001 |
      | Hostname             | localhost         |
      | Port                 |              9598 |
    Then the PushSetupLastGasp should be set on the device
      | DeviceIdentification | TEST1030000000001 |
