# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering Configuration - Clock configuration
  As a grid operator
  I want to be able to change the clock configuration of a meter
  So the meter works with localized time settings

  @NightlyBuildOnly
  Scenario Outline: Set clock configuration in a single request for protocol: <protocol> <version> on device <deviceIdentification>
    Given a dlms device
      | DeviceIdentification     | <deviceIdentification> |
      | DeviceType               | SMART_METER_E          |
      | Protocol                 | <protocol>             |
      | ProtocolVersion          | <version>              |
    When the SetClockConfiguration request is received
      | DeviceIdentification     | <deviceIdentification>   |
      | TimeZoneOffset           |                      -60 |
      | DaylightSavingsBegin     | FFFF03FE0702000000FFC400 |
      | DaylightSavingsEnd       | FFFF0AFE0703000000FF8880 |
      | DaylightSavingsEnabled   | TRUE                     |
    Then the set clock configuration response should be returned
      | DeviceIdentification | <deviceIdentification> |
      | Result               | OK                |

    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1024000000002     | DSMR     | 2.2     |
      | TEST1024000000002     | DSMR     | 4.2.2   |
      | TEST1031000000002     | SMR      | 4.3     |
      | TEST1027000000002     | SMR      | 5.0.0   |
      | TEST1028000000002     | SMR      | 5.1     |
      | TEST1029000000002     | SMR      | 5.2     |
      | TEST1030000000002     | SMR      | 5.5     |

  @NightlyBuildOnly
  Scenario: Set clock configuration and synchronize time with incorrect timezone
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set clock configuration action with parameters
      | TimeZoneOffset           |                     -480 |
      | DaylightSavingsBegin     | FFFF03FE0702000000FFC400 |
      | DaylightSavingsEnd       | FFFF0AFE0703000000FF8880 |
      | DaylightSavingsEnabled   | true                     |
    And the bundle request contains a valid synchronize time action for timezone "Europe/Amsterdam"
    When the bundle request is received
    Then the bundle response should contain a set clock configuration response with values
      | Result | OK |
    And the bundle response should contain a synchronize time response with values
      | Result | NOT OK |

  Scenario: Set clock configuration and synchronize time
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set clock configuration action with parameters
      | TimeZoneOffset           |                      -60 |
      | DaylightSavingsBegin     | FFFF03FE0702000000FFC400 |
      | DaylightSavingsEnd       | FFFF0AFE0703000000FF8880 |
      | DaylightSavingsEnabled   | true                     |
    And the bundle request contains a valid synchronize time action for timezone "Europe/Amsterdam"
    When the bundle request is received
    Then the bundle response should contain a set clock configuration response with values
      | Result | OK |
    And the bundle response should contain a synchronize time response with values
      | Result | OK |
