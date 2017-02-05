Feature: PublicLightingAdhocManagement Set Transition
  As OSGP 
  I want clients to be able to send night-day and day-night transition notifications to a device
  In order to ...
  NOTE: Authorisation is tested in Basic OSGP Functions - PBI119

  @OslpMockServer
  Scenario Outline: Set Transition
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set transition response "OK" over OSLP
    When receiving a set transition request
      | DeviceIdentification | TEST1024000000001 |
      | TransitionType       | <TransitionType>  |
      | Time                 | <Time>            |
    Then the set transition async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set transition OSLP message is sent to device "TEST1024000000001"
      | TransitionType | <TransitionType> |
      | Time           | <Time>           |
    And the platform buffers a set transition response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | TransitionType | Time   |
      | DAY_NIGHT      |        |
      | DAY_NIGHT      | 200000 |
      | NIGHT_DAY      |        |
      | NIGHT_DAY      | 080000 |

  Scenario: Set transition as an unknown organization
    When receiving a set transition request by an unknown organization
      | DeviceIdentification | TEST1024000000001 |
      | TransitionType       | DAY_NIGHT         |
    Then the set transition async response contains a soap fault
      | Message | UNKNOWN_ORGANISATION |

  Scenario: Set transition for an unknown device
    When receiving a set transition request
      | DeviceIdentification | TEST1024000000001 |
      | TransitionType       | DAY_NIGHT         |
    Then the set transition async response contains a soap fault
      | Message | UNKNOWN_DEVICE |

  Scenario Outline: Set Transition with invalid data
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set transition request
      | DeviceIdentification | TEST1024000000001 |
      | TransitionType       |                   |
      | Time                 | <Time>            |
    Then the set transition async response contains a soap fault
      | Result  | NOT_OK           |
      | Message | Validation error |

    Examples: 
      | Time   |
      |        |
      | 200000 |
