# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringAdHoc
Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to set a specific attribute value in a device
  So I can change the configuration of a device

  Scenario Outline: Set watchdog timer for a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the Set Specific Attribute Value request is received
      | DeviceIdentification | <deviceIdentification> |
      | ObjectType           | WATCHDOG_TIMER         |
      | Attribute            | 2                      |
      | IntValue             | 42                     |
    Then the Set Specific Attribute Value response should be returned
      | DeviceIdentification | <deviceIdentification> |
      | Result               | <response>             |

    Examples:
      | deviceIdentification | protocol | version | response |
      | TEST1024000000001    | DSMR     | 4.2.2   | OK       |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version | response |
      | TEST1024000000001    | DSMR     | 2.2     | NOT_OK   |
      | TEST1031000000001    | SMR      | 4.3     | OK       |
      | TEST1027000000001    | SMR      | 5.0.0   | OK       |
      | TEST1028000000001    | SMR      | 5.1     | OK       |
      | TEST1029000000001    | SMR      | 5.2     | OK       |
      | TEST1030000000001    | SMR      | 5.5     | OK       |
