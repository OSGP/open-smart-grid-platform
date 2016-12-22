Feature: Find Recent Devices
  As a ...
  I want to ...
  In order to ...

  # Recent means today, yesterday and the day before yesterday (full days).
  # TODO Check response corretly.
  #	Scenario Outline: Find recent devices
  #		Given a device
  #| DeviceIdentification | <DeviceIdentification> |
  #When receiving a find recent devices request
  #	| DeviceIdentification       | <DeviceIdentification>       |
  #	| OrganizationIdentification | <OrganizationIdentification> |
  #Then the find recent devices response contains
  #	| DeviceIdentification       | <DeviceIdentification>       |
  #	| OrganizationIdentification | <OrganizationIdentification> |
  #
  #Examples:
  #	| DeviceIdentification | OrganizationIdentification |
  #	| TEST1024000000001    | test-org                   |
  
  Scenario Outline: Find recent devices without owner
    When receiving a find recent devices request
      | DeviceIdentification | <DeviceIdentification> |
    Then the find recent devices response contains "0" devices

    Examples: 
      | DeviceIdentification |
      | TEST1024000000002    |
