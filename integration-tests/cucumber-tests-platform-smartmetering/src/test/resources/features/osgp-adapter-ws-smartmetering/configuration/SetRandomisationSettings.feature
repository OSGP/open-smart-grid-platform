# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - SetRandomisationSettings
  As a grid operator
  I want to be able to set randomisation settings on a device

  Scenario Outline: Set set randomisation settings on a <protocol> <version> device with direct attach <da>
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    When the set randomisation settings request is received
      | DeviceIdentification     | <deviceIdentification> |
      | directAttach             | <da>                   |
      | randomisationStartWindow | <rsw>                  |
      | multiplicationFactor     | <mf>                   |
      | numberOfRetries          | <nor>                  |
    Then the randomisation settings <shouldBeSetOrNot> on the device
    Examples:
      | deviceIdentification | protocol | version | da | rsw | mf | nor | shouldBeSetOrNot  |
      | TEST1027000000001    | SMR      | 5.1     |  1 |   1 |  1 |   1 | should be set     |
      | TEST1027000000001    | SMR      | 5.1     |  0 |   5 |  3 |   1 | should be set     |
      | TEST1024000000001    | DSMR     | 4.2.2   |  1 |   1 |  1 |   1 | should not be set |
      | TEST1024000000001    | DSMR     | 4.2.2   |  0 |  10 |  4 |   1 | should be set     |
