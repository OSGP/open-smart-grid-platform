@SmartMetering @Platform
Feature: SmartMetering Clock configuration
  As a grid operator
  I want to be able to change the clock configuration of a meter
  So the meter works with localized time settings

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set clock configuration in a single request
    When the SetClockConfiguration request is received
      | DeviceIdentification     | TEST1024000000001        |
      | TimeZoneOffset           |                      -60 |
      | DaylightSavingsBegin     | FFFF03FE0702000000FFC400 |
      | DaylightSavingsEnd       | FFFF0AFE0703000000FF8880 |
      | DaylightSavingsDeviation |                      -60 |
      | DaylightSavingsEnabled   | TRUE                     |
    Then the set clock configuration response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |

  Scenario: Set clock configuration and synchronize time with incorrect timezone
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And a set clock configuration action is part of a bundled request
      | TimeZoneOffset           |                     -480 |
      | DaylightSavingsBegin     | FFFF03FE0702000000FFC400 |
      | DaylightSavingsEnd       | FFFF0AFE0703000000FF8880 |
      | DaylightSavingsDeviation |                       60 |
      | DaylightSavingsEnabled   | true                     |
    And a valid synchronize time action for timezone "Europe/Amsterdam" is part of a bundled request
    When the bundle request is received
    Then the bundle response contains a set clock configuration response
      | Result | OK |
    And the bundle response contains a synchronize time response
      | Result | NOT OK |

  Scenario: Set clock configuration and synchronize time
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And a set clock configuration action is part of a bundled request
      | TimeZoneOffset           |                      -60 |
      | DaylightSavingsBegin     | FFFF03FE0702000000FFC400 |
      | DaylightSavingsEnd       | FFFF0AFE0703000000FF8880 |
      | DaylightSavingsDeviation |                       60 |
      | DaylightSavingsEnabled   | true                     |
    And a valid synchronize time action for timezone "Europe/Amsterdam" is part of a bundled request
    When the bundle request is received
    Then the bundle response contains a set clock configuration response
      | Result | OK |
    And the bundle response contains a synchronize time response
      | Result | OK |
