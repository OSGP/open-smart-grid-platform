Feature: Find Recent Devices
  As a ...
  I want to ...
  In order to ...

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
