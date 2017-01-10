Feature: Set transition
  As OSGP 
  I want clients to be able to send night-day and day-night transition notifications to a device
  In order to ...
  NOTE: Authorisation is tested in Basic OSGP Functions - PBI119

  @OslpMockServer
  Scenario Outline: Set Transition
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
    And the device returns a set transition response "<Result>" over OSLP
    When receiving a set transition request
      | DeviceIdentification | <DeviceIdentification> |
      | TransitionType       | <TransitionType>       |
      | Time                 | <Time>                 |
    Then the set transition async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set transition OSLP message is sent to device "<DeviceIdentification>"
      | TransitionType | <TransitionType> |
      | Time           | <Time>           |
    And the platform buffers a set transition response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | TransitionType | Time   | Result |
      | TEST1024000000001    | DAY_NIGHT      |        | OK     |
      | TEST1024000000001    | DAY_NIGHT      | 200000 | OK     |
      | TEST1024000000001    | NIGHT_DAY      |        | OK     |
      | TEST1024000000001    | NIGHT_DAY      | 080000 | OK     |

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
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set transition request
      | DeviceIdentification | TEST1024000000001 |
      | TransitionType       | <TransitionType>  |
      | Time                 | <Time>            |
    Then the set transition async response contains a soap fault
      | Result  | <Result>  |
      | Message | <Message> |

    Examples: 
      | TransitionType | Time   | Result | Message          |
      |                |        | NOT_OK | Validation error |
      |                | 200000 | NOT_OK | Validation error |
