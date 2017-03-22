Feature: Clock configuration
	As a grid operator
	I want to be able to change the clock configuration of a meter
	So the meter works with localized time settings

  Scenario: Set clock configuration in a single request 
  	Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When the SetClockConfiguration request is received
      | DeviceIdentification     | TEST1024000000001        |
      | TimeZoneOffset           |                      -60 |
      | DaylightSavingsBegin     | FFFF03FE0702000000003CFF |
      | DaylightSavingsEnd       | FFFF0AFE07020000000078FF |
      | DaylightSavingsDeviation |                      -60 |
      | DaylightSavingsEnabled   | TRUE                     |
    Then the set clock configuration response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |

  @Skip
  Scenario: Set clock configuration and synchronize time with incorrect timezone
    Given a dlms device
      | DeviceIdentification      | EKAIFA10000000001                                                                                |
      | DeviceType                | SMART_METER_E                                                                                    |
      | IccId                     |                                                                              8931086214024582787 |
      | SecurityKeyAuthentication | bc082efed278e1bbebddc0431877d4fa9330410a0f962b9a9fed6e3def1a3144e8886b0aa5e6ec74319df619714b8605 |
      | SecurityKeyMaster         | bc082efed278e1bbebddc0431877d4fa3c5c7ceef62f2e86f68c6d15bb2a08b65dee1734eb30b7de15c081f9ce69223b |
      | SecurityKeyEncryption     | bc082efed278e1bbebddc0431877d4fa406faa0664b058bbf0d5415f34360c5447e8d47634c664cbdc4037c89599f959 |
      | IpAddressIsStatic         | false                                                                                            |
      | Port                      |                                                                                             4059 |
      | ChallengeLength           |                                                                                               16 |
    And a bundle request
      | DeviceIdentification | EKAIFA10000000001 |
    And a set clock configuration action is part of a bundled request
      | TimeZoneOffset           |                     -480 |
      | DaylightSavingsBegin     | FFFF03FE0702000000003CFF |
      | DaylightSavingsEnd       | FFFF0AFE07020000000078FF |
      | DaylightSavingsDeviation |                       60 |
      | DaylightSavingsEnabled   | true                     |
    And a synchronize time action is part of a bundled request
      | Deviation | -60 |
      | DST       | true |
    When the bundle request is received
    Then the bundle response contains a set clock configuration response
      | Result | OK |
    And the bundle response contains a synchronize time response
      | Result | NOT OK |

  @Skip
  Scenario: Set clock configuration and synchronize time
    Given a dlms device
      | DeviceIdentification      | EKAIFA10000000001                                                                                |
      | DeviceType                | SMART_METER_E                                                                                    |
      | IccId                     |                                                                              8931086214024582787 |
      | SecurityKeyAuthentication | bc082efed278e1bbebddc0431877d4fa9330410a0f962b9a9fed6e3def1a3144e8886b0aa5e6ec74319df619714b8605 |
      | SecurityKeyMaster         | bc082efed278e1bbebddc0431877d4fa3c5c7ceef62f2e86f68c6d15bb2a08b65dee1734eb30b7de15c081f9ce69223b |
      | SecurityKeyEncryption     | bc082efed278e1bbebddc0431877d4fa406faa0664b058bbf0d5415f34360c5447e8d47634c664cbdc4037c89599f959 |
      | IpAddressIsStatic         | false                                                                                            |
      | Port                      |                                                                                             4059 |
      | ChallengeLength           |                                                                                               16 |
    And a bundle request
      | DeviceIdentification | EKAIFA10000000001 |
    And a set clock configuration action is part of a bundled request
      | TimeZoneOffset           |                      -60 |
      | DaylightSavingsBegin     | FFFF03FE0702000000003CFF |
      | DaylightSavingsEnd       | FFFF0AFE07020000000078FF |
      | DaylightSavingsDeviation |                       60 |
      | DaylightSavingsEnabled   | true                     |
    And a synchronize time action is part of a bundled request
      | Deviation | -60 |
      | DST       | true |
    When the bundle request is received
    Then the bundle response contains a set clock configuration response
      | Result | OK |
    And the bundle response contains a synchronize time response
      | Result | OK |
      
