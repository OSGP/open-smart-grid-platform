# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @PushSetup @SetPushSetupAlarm @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Push Setup Alarm
  As a grid operator
  I want to be able to set the Push setup alarm on a device
  So the device will push its related messages to the correct endpoint

  Scenario Outline: Set push setup alarm on a <protocol><version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the set PushSetupAlarm request is received
      | DeviceIdentification   | <deviceIdentification>                        |
      | Hostname               | localhost2                                    |
      | Port                   |                                          4321 |
      | PushObjectClassIds     | 1,40,1                                        |
      | PushObjectObisCodes    | 0-0:96.1.1.255,0-1:25.9.0.255,0-0:97.98.0.255 |
      | PushObjectAttributeIds | 2,1,2                                         |
      | PushObjectDataIndexes  | 0,0,0                                         |
    Then the PushSetupAlarm response should be returned
      | DeviceIdentification | TEST1030000000001 |
      | Result               | OK                |
    And the PushSetupAlarm should be set on the device
      | DeviceIdentification | <deviceIdentification> |

    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 4.2.2   |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1027000000001    | SMR      | 5.0.0   |
      | TEST1028000000001    | SMR      | 5.1     |
      | TEST1029000000001    | SMR      | 5.2     |
      | TEST1030000000001    | SMR      | 5.5     |