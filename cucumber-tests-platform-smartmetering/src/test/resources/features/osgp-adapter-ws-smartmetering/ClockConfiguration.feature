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
      | DaylightSavingsEnabled   | TRUE                     |
    Then the set clock configuration response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |

  Scenario: Set clock configuration and synchronize time with incorrect timezone
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
