Feature: Find Recent Devices
  As a ...
  I want to ...
  In order to ...

  Scenario Outline: Find recent devices parameterized
    Given a device
      | DeviceIdentification       | <DeviceIdentification>       |
      | OrganizationIdentification | <OrganizationIdentification> |
      | HasSchedule                | <HasSchedule>                |
    When receiving a find recent devices request
    Then the find recent devices response contains "1" device
    And the find recent devices response contains at index "1"
      | DeviceIdentification       | <DeviceIdentification>       |
      | OrganizationIdentification | <OrganizationIdentification> |
      | HasSchedule                | <HasSchedule>                |

    Examples: 
      | DeviceIdentification | OrganizationIdentification | HasSchedule |
      | TEST1024000000001    | test-org                   |             |
      | TEST1024000000001    | test-org                   | true        |
      | TEST1024000000001    | test-org                   | false       |

  # Recent means today, yesterday and the day before yesterday (full days).
  # TODO Check response correctly.
  Scenario Outline: Find recent devices
    Given a device
      | DeviceIdentification       | <DeviceIdentification>       |
      | OrganizationIdentification | <OrganizationIdentification> |
    When receiving a find recent devices request
    Then the find recent devices response contains "1" device
    And the find recent devices response contains at index "1"
      | DeviceIdentification       | <DeviceIdentification>       |
      | OrganizationIdentification | <OrganizationIdentification> |

    Examples: 
      | DeviceIdentification | OrganizationIdentification |
      | TEST1024000000001    | test-org                   |

  Scenario Outline: Find recent devices without owner
    When receiving a find recent devices request
    Then the find recent devices response contains "0" devices

    Examples: 
      | DeviceIdentification |
      | TEST1024000000002    |
