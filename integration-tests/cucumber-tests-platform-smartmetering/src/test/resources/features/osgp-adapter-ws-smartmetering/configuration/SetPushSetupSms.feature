# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @PushSetup @SetPushSetupSms @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Push Setup SMS
  As a grid operator
  I want to be able to set the Push setup SMS on a device
  So the device will push its related messages to the correct endpoint

  Scenario Outline: Set push setup sms on a <protocol><version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the set PushSetupSms request is received
      | DeviceIdentification | <deviceIdentification> |
      | Hostname             | localhost              |
      | Port                 |                   9598 |
    Then the PushSetupSms should be set on the device
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