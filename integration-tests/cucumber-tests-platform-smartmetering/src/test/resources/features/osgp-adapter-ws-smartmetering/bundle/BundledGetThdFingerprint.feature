# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @Fingerprint @NightlyBuildOnly
Feature: SmartMetering Bundle - GetThdFingerprint
  As a grid operator
  I want to be able to get the THD fingerprint and counters from a device via a bundle request

  Scenario Outline: Get THD fingerprint on a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get THD fingerprint action
    When the bundle request is received
    Then the bundle response should contain a get THD fingerprint response with values
      | THD_CURRENT_L1     |  317 |
      | THD_CURRENT_L2     |  517 |
      | THD_CURRENT_L3     |  717 |
      | THD_FINGERPRINT_L1 |   15 |
      | THD_FINGERPRINT_L2 |   15 |
      | THD_FINGERPRINT_L3 |   15 |
      | THD_COUNTER_L1     | 3136 |
      | THD_COUNTER_L2     | 5136 |
      | THD_COUNTER_L3     | 7136 |

    Examples:
      | deviceIdentification | protocol | version |
      | TEST1029000000001    | SMR      | 5.2     |
      | TEST1030000000001    | SMR      | 5.5     |
