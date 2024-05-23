# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @PushSetup @SetPushSetupLastGasp @NightlyBuildOnly
Feature: SmartMetering Configuration - Set Push Setup LastGasp
  As a grid operator
  I want to be able to set the Push setup last gasp on a device
  So the device will push its related messages to the correct endpoint

  Scenario: Set push setup last gasp on a SMR5.5 device
    Given a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
    When the set PushSetupLastGasp request is received
      | DeviceIdentification | TEST1030000000001 |
      | Hostname             | localhost         |
      | Port                 |              9598 |
    Then the PushSetupLastGasp response should be returned
      | DeviceIdentification | TEST1030000000001 |
      | Result               | OK                |
    And the PushSetupLastGasp should be set on the device
      | DeviceIdentification | TEST1030000000001 |


  Scenario Outline: Set push setup last gasp on a <protocol><version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | CommunicationMethod  | GPRS                   |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the set PushSetupLastGasp request is received
      | DeviceIdentification | <deviceIdentification> |
      | Hostname             | localhost              |
      | Port                 | 9598                   |
    Then the PushSetupLastGasp response should be returned
      | DeviceIdentification | <deviceIdentification> |
      | Result               | NOT_OK                 |

    Examples:
      | deviceIdentification | protocol | version |
      | TEST1029000000001    | SMR      | 5.2     |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version |
      | TEST1024000000001    | DSMR     | 4.2.2   |
      | TEST1027000000001    | SMR      | 5.0.0   |
      | TEST1028000000001    | SMR      | 5.1     |